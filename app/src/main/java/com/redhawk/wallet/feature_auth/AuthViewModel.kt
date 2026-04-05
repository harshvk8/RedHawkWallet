package com.redhawk.wallet.feature_auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.redhawk.wallet.data.datasource.FirestoreDataSource
import com.redhawk.wallet.data.models.UserProfile
import com.redhawk.wallet.data.repository.UserRepository
import com.redhawk.wallet.data.repository.WalletRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val authManager = AuthManager()
    private val sessionManager = SessionManager(application)
    private val repository = AuthRepository(authManager, sessionManager)
    private val userRepository = UserRepository(FirestoreDataSource())
    private val walletRepository = WalletRepository(FirestoreDataSource())

    private val _authState = MutableStateFlow<AuthResult?>(null)
    val authState: StateFlow<AuthResult?> = _authState

    private val _isEmailVerified = MutableStateFlow(false)
    val isEmailVerified: StateFlow<Boolean> = _isEmailVerified

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    fun register(name: String, studentId: String, email: String, password: String) {
        _authState.value = AuthResult.Loading

        viewModelScope.launch {
            try {
                val result = repository.register(email, password)

                result.fold(
                    onSuccess = {
                        val user = FirebaseAuth.getInstance().currentUser
                        val uid = user?.uid.orEmpty()

                        if (uid.isBlank()) {
                            _authState.value = AuthResult.Error("Registration succeeded but UID is missing")
                            return@launch
                        }

                        val profile = UserProfile(
                            uid = uid,
                            name = name,
                            studentId = studentId,
                            email = email,
                            photoUrl = ""
                        )

                        try {
                            userRepository.createUserProfile(profile)
                            walletRepository.initWallet(uid)

                            try {
                                user?.sendEmailVerification()
                                _message.value = "Verification email sent to $email"
                            } catch (e: Exception) {
                                _message.value = e.message ?: "Failed to send verification email"
                            }

                            _authState.value = AuthResult.Success(uid)
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
                _authState.value = AuthResult.Error(e.message ?: "Registration failed")
            }
        }
    }

    fun login(email: String, password: String) {
        _authState.value = AuthResult.Loading

        viewModelScope.launch {
            try {
                val result = repository.login(email, password)

                result.fold(
                    onSuccess = { uid ->
                        val user = FirebaseAuth.getInstance().currentUser
                        val currentUid = user?.uid ?: uid

                        if (currentUid.isNotBlank()) {
                            val wallet = walletRepository.getWallet(currentUid)
                            if (wallet == null) {
                                walletRepository.initWallet(currentUid)
                            }
                        }

                        _isEmailVerified.value = user?.isEmailVerified == true

                        if (user?.isEmailVerified == false) {
                            _message.value = "Please verify your email before continuing."
                        }

                        _authState.value = AuthResult.Success(currentUid)
                    },
                    onFailure = { e ->
                        _authState.value = AuthResult.Error(
                            e.message ?: "Login failed"
                        )
                    }
                )
            } catch (e: Exception) {
                _authState.value = AuthResult.Error(e.message ?: "Login failed")
            }
        }
    }

    fun resendVerificationEmail() {
        viewModelScope.launch {
            try {
                val user = FirebaseAuth.getInstance().currentUser
                if (user != null) {
                    user.sendEmailVerification()
                    _message.value = "Verification email sent again."
                } else {
                    _message.value = "No logged-in user found."
                }
            } catch (e: Exception) {
                _message.value = e.message ?: "Failed to resend verification email."
            }
        }
    }

    fun reloadCurrentUser() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val user = FirebaseAuth.getInstance().currentUser

                if (user != null) {
                    user.reload()
                    val refreshedUser = FirebaseAuth.getInstance().currentUser
                    val verified = refreshedUser?.isEmailVerified == true
                    _isEmailVerified.value = verified

                    _message.value = if (verified) {
                        "Email verified successfully."
                    } else {
                        "Email is still not verified yet."
                    }
                } else {
                    _message.value = "No logged-in user found."
                }
            } catch (e: Exception) {
                _message.value = e.message ?: "Failed to refresh user status."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        authManager.signOut()
        _authState.value = null
        _isEmailVerified.value = false
        _isLoading.value = false
        _message.value = null
    }

    fun signOut() {
        logout()
    }

    fun getCurrentUserEmail(): String? {
        return FirebaseAuth.getInstance().currentUser?.email
    }

    fun checkCurrentUser(): Boolean {
        return authManager.getCurrentUser() != null
    }

    fun clearState() {
        _authState.value = null
    }

    fun clearMessage() {
        _message.value = null
    }
}