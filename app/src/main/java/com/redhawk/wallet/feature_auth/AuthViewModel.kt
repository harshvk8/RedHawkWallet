package com.redhawk.wallet.feature_auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthResult?>(null)
    val authState: StateFlow<AuthResult?> = _authState

    fun signUp(universityId: String, password: String) {
        _authState.value = AuthResult.Loading

        repository.signUp(universityId, password) { result ->
            _authState.value = result
        }
    }

    fun signIn(universityId: String, password: String) {
        _authState.value = AuthResult.Loading

        repository.signIn(universityId, password) { result ->
            _authState.value = result
        }
    }

    fun signOut() {
        repository.signOut()
        _authState.value = null
    }

    fun checkCurrentUser(): Boolean {
        return repository.getCurrentUser() != null
    }

    fun getCurrentUniversityId(): String? {
        return repository.getCurrentUniversityId()
    }

    fun clearAuthState() {
        _authState.value = null
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
                val sessionManager = SessionManager(context.applicationContext)
                val authManager = AuthManager()
                val repository = AuthRepository(authManager, sessionManager)
                @Suppress("UNCHECKED_CAST")
                return AuthViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
