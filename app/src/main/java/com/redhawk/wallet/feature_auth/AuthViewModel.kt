package com.redhawk.wallet.feature_auth

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthResult?>(null)
    val authState: StateFlow<AuthResult?> = _authState

    fun signUp(email: String, password: String) {
        _authState.value = AuthResult.Loading

        repository.signUp(email, password) { result ->
            _authState.value = result
        }
    }

    fun signIn(email: String, password: String) {
        _authState.value = AuthResult.Loading

        repository.signIn(email, password) { result ->
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
}
