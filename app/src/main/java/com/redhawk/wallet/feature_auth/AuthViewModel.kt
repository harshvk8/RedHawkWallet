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
    private val repository: AuthRepository = AuthRepository(),
    private val manager: AuthManager = AuthManager(),
    private val userRepository: UserRepository = UserRepository(FirestoreDataSource()),
    private val walletRepository: WalletRepository = WalletRepository(FirestoreDataSource())
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthResult?>(null)
    val authState: StateFlow<AuthResult?> = _authState

    fun register(name: String, studentId: String, email: String, password: String) {
        _authState.value = AuthResult.Loading

        viewModelScope.launch {
            val result = repository.registerIfAllowed(
                name = name,
                studentId = studentId,
                email = email,
                password = password
            )

            // ✅ If register success → create profile + init wallet(200)
            if (result is AuthResult.Success) {
                val user = FirebaseAuth.getInstance().currentUser
                val uid = user?.uid.orEmpty()

                if (uid.isNotBlank()) {
                    val profile = UserProfile(
                        uid = uid,
                        name = name,
                        studentId = studentId,
                        email = email,
                        photoUrl = ""
                    )

                    try {
                        userRepository.createUserProfile(profile)
                        walletRepository.initWallet(uid) // ✅ $200 wallet
                    } catch (e: Exception) {
                        // If Firestore fails, show error
                        _authState.value = AuthResult.Error(
                            e.message ?: "Failed to save user profile / wallet"
                        )
                        return@launch
                    }
                } else {
                    _authState.value = AuthResult.Error("Registration succeeded but UID is missing")
                    return@launch
                }
            }

            _authState.value = result
        }
    }

    fun login(email: String, password: String) {
        _authState.value = AuthResult.Loading

        viewModelScope.launch {
            val result = repository.login(email, password)

            // ✅ if login success, ensure wallet exists
            if (result is AuthResult.Success) {
                val uid = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
                if (uid.isNotBlank()) {
                    val w = walletRepository.getWallet(uid)
                    if (w == null) walletRepository.initWallet(uid)
                }
            }

            _authState.value = result
        }
    }

    fun signOut() {
        manager.signOut()
        _authState.value = null
    }

    fun checkCurrentUser(): Boolean {
        return manager.getCurrentUser() != null
    }

    fun clearState() {
        _authState.value = null
    }
}