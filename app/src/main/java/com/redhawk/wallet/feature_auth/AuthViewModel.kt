package com.redhawk.wallet.feature_auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.redhawk.wallet.data.datasource.FirestoreDataSource
import com.redhawk.wallet.data.repository.WalletRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val context = getApplication<Application>().applicationContext


    private val authManager = AuthManager()
    private val sessionManager = SessionManager(context)
    private val repository = AuthRepository(authManager, sessionManager)

    private val walletRepository = WalletRepository(FirestoreDataSource())

    private val _authState = MutableStateFlow<AuthResult?>(null)
    val authState: StateFlow<AuthResult?> = _authState

    /**
     * ✅ REGISTER (NO UserProfile)
     */
    fun register(
        name: String,        // still passed from UI (optional use later)
        studentId: String,   // still passed from UI (optional use later)
        email: String,
        password: String
    ) {
        _authState.value = AuthResult.Loading

        viewModelScope.launch {
            try {
                val result = repository.register(email, password)

                if (result.isSuccess) {
                    val uid = result.getOrNull().orEmpty()

                    if (uid.isBlank()) {
                        _authState.value = AuthResult.Error("UID missing")
                        return@launch
                    }


                    val wallet = walletRepository.getWallet(uid)
                    if (wallet == null) {
                        walletRepository.initWallet(uid)
                    }

                    _authState.value = AuthResult.Success(uid)

                } else {
                    _authState.value = AuthResult.Error(
                        result.exceptionOrNull()?.message ?: "Registration failed"
                    )
                }

            } catch (e: Exception) {
                _authState.value = AuthResult.Error(
                    e.message ?: "Registration error"
                )
            }
        }
    }


    fun login(email: String, password: String) {
        _authState.value = AuthResult.Loading

        viewModelScope.launch {
            try {
                val result = repository.login(email, password)

                if (result.isSuccess) {
                    val uid = result.getOrNull().orEmpty()

                    if (uid.isNotBlank()) {
                        val wallet = walletRepository.getWallet(uid)
                        if (wallet == null) {
                            walletRepository.initWallet(uid)
                        }
                    }

                    _authState.value = AuthResult.Success(uid)

                } else {
                    _authState.value = AuthResult.Error(
                        result.exceptionOrNull()?.message ?: "Login failed"
                    )
                }

            } catch (e: Exception) {
                _authState.value = AuthResult.Error(
                    e.message ?: "Login error"
                )
            }
        }
    }


    fun signOut() {
        repository.logout()
        _authState.value = null
    }


    fun checkCurrentUser(): Boolean {
        val user = authManager.getCurrentUser()
        return user != null && user.uid.isNotBlank()
    }


    fun clearState() {
        _authState.value = null
    }
}