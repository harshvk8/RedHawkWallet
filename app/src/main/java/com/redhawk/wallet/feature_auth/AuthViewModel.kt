package com.redhawk.wallet.feature_auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.redhawk.wallet.data.datasource.FirestoreDataSource
import com.redhawk.wallet.data.models.UserProfile
import com.redhawk.wallet.data.repository.UserRepository
import com.redhawk.wallet.data.repository.WalletRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authManager: AuthManager = AuthManager(),
    private val userRepository: UserRepository = UserRepository(FirestoreDataSource()),
    private val walletRepository: WalletRepository = WalletRepository(FirestoreDataSource())
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthResult?>(null)
    val authState: StateFlow<AuthResult?> = _authState

    private fun validateUsername(username: String): String? {
        return when {
            username.isBlank() -> "Username cannot be empty"
            username.length < 3 -> "Username must be at least 3 characters"
            username.contains(" ") -> "Username cannot contain spaces"
            else -> null
        }
    }

    fun register(
        name: String,
        username: String,
        studentId: String,
        email: String,
        password: String
    ) {
        val usernameError = validateUsername(username)
        if (usernameError != null) {
            _authState.value = AuthResult.Error(usernameError)
            return
        }

        _authState.value = AuthResult.Loading

        viewModelScope.launch {
            try {
                val result = authManager.register(email, password)

                result.fold(
                    onSuccess = { uid ->
                        val safeUid = uid.ifBlank {
                            FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
                        }

                        if (safeUid.isBlank()) {
                            _authState.value = AuthResult.Error("UID missing")
                            return@launch
                        }

                        val profile = UserProfile(
                            uid = safeUid,
                            name = name,
                            username = username,
                            studentId = studentId,
                            email = email,
                            photoUrl = ""
                        )

                        try {
                            userRepository.createUserProfile(profile)
                            walletRepository.initWallet(safeUid)
                            _authState.value = AuthResult.Success(safeUid)
                        } catch (e: Exception) {
                            _authState.value = AuthResult.Error(
                                e.message ?: "Failed to save user profile / wallet"
                            )
                        }
                    },
                    onFailure = { e ->
                        _authState.value = AuthResult.Error(
                            e.message ?: "Registration failed"
                        )
                    }
                )
            } catch (e: Exception) {
                _authState.value = AuthResult.Error(
                    e.message ?: "Registration failed"
                )
            }
        }
    }

    fun login(email: String, password: String) {
        _authState.value = AuthResult.Loading

        viewModelScope.launch {
            try {
                val result = authManager.login(email, password)

                result.fold(
                    onSuccess = { uid ->
                        if (uid.isNotBlank()) {
                            val wallet = walletRepository.getWallet(uid)
                            if (wallet == null) {
                                walletRepository.initWallet(uid)
                            }
                        }

                        _authState.value = AuthResult.Success(uid)
                    },
                    onFailure = { e ->
                        _authState.value = AuthResult.Error(
                            e.message ?: "Login failed"
                        )
                    }
                )
            } catch (e: Exception) {
                _authState.value = AuthResult.Error(
                    e.message ?: "Login failed"
                )
            }
        }
    }

    fun signOut() {
        authManager.signOut()
        _authState.value = null
    }

    fun clearState() {
        _authState.value = null
    }

    fun checkCurrentUser(): Boolean {
        return authManager.getCurrentUser() != null
    }
}