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
import kotlinx.coroutines.tasks.await

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val context = getApplication<Application>().applicationContext

    private val authManager = AuthManager()
    private val sessionManager = SessionManager(context)
    private val repository = AuthRepository(authManager, sessionManager)
    private val walletRepository = WalletRepository(FirestoreDataSource())

    private val _authState = MutableStateFlow<AuthResult?>(null)
    val authState: StateFlow<AuthResult?> = _authState

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    private val _isEmailVerified = MutableStateFlow(false)
    val isEmailVerified: StateFlow<Boolean> = _isEmailVerified

    init {
        reloadCurrentUser()
    }

    fun register(
        name: String,
        studentId: String,
        email: String,
        password: String
    ) {
        _isLoading.value = true
        _message.value = null
        _authState.value = AuthResult.Loading

        viewModelScope.launch {
            try {
                val result = repository.register(email, password)

                if (result.isSuccess) {
                    val uid = result.getOrNull().orEmpty()

                    if (uid.isBlank()) {
                        _authState.value = AuthResult.Error("UID missing")
                        _message.value = "UID missing"
                        _isLoading.value = false
                        return@launch
                    }

                    val wallet = walletRepository.getWallet(uid)
                    if (wallet == null) {
                        walletRepository.initWallet(uid)
                    }

                    _isEmailVerified.value =
                        FirebaseAuth.getInstance().currentUser?.isEmailVerified == true

                    _message.value = if (_isEmailVerified.value) {
                        "Registration successful."
                    } else {
                        "Registration successful. Please verify your email."
                    }

                    _authState.value = AuthResult.Success(uid)
                } else {
                    val msg = result.exceptionOrNull()?.message ?: "Registration failed"
                    _authState.value = AuthResult.Error(msg)
                    _message.value = msg
                }
            } catch (e: Exception) {
                val msg = e.message ?: "Registration error"
                _authState.value = AuthResult.Error(msg)
                _message.value = msg
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun login(email: String, password: String) {
        _isLoading.value = true
        _message.value = null
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

                    reloadCurrentUser()

                    _message.value = if (_isEmailVerified.value) {
                        "Login successful."
                    } else {
                        "Please verify your email."
                    }

                    _authState.value = AuthResult.Success(uid)
                } else {
                    val msg = result.exceptionOrNull()?.message ?: "Login failed"
                    _authState.value = AuthResult.Error(msg)
                    _message.value = msg
                }
            } catch (e: Exception) {
                val msg = e.message ?: "Login error"
                _authState.value = AuthResult.Error(msg)
                _message.value = msg
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun reloadCurrentUser() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val user = FirebaseAuth.getInstance().currentUser
                user?.reload()?.await()
                _isEmailVerified.value = user?.isEmailVerified == true
            } catch (e: Exception) {
                _message.value = e.message ?: "Failed to refresh user."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resendVerificationEmail() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _message.value = null

                val user = FirebaseAuth.getInstance().currentUser
                if (user == null) {
                    _message.value = "No logged in user found."
                    return@launch
                }

                user.sendEmailVerification().await()
                _message.value = "Verification email sent."
            } catch (e: Exception) {
                _message.value = e.message ?: "Failed to send verification email."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        repository.logout()
        _authState.value = null
        _isEmailVerified.value = false
        _message.value = null
    }

    fun signOut() {
        logout()
    }

    fun checkCurrentUser(): Boolean {
        val user = authManager.getCurrentUser()
        return user != null && user.uid.isNotBlank()
    }

    fun clearState() {
        _authState.value = null
    }

    fun clearMessage() {
        _message.value = null
    }
}