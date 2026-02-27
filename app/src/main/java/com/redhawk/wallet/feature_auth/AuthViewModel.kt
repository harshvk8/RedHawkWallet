package com.redhawk.wallet.feature_auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository(),
    private val manager: AuthManager = AuthManager()
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
            _authState.value = result
        }
    }

    fun login(email: String, password: String) {
        _authState.value = AuthResult.Loading

        viewModelScope.launch {
            val result = repository.login(email, password)
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