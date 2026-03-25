package com.redhawk.wallet.feature_auth

data class RegisterUiState(
    val name: String = "",
    val username: String = "",
    val studentId: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",

    val nameError: String? = null,
    val usernameError: String? = null,
    val studentIdError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,

    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
) {
    fun isValid(): Boolean {
        return nameError == null &&
                usernameError == null &&
                studentIdError == null &&
                emailError == null &&
                passwordError == null &&
                confirmPasswordError == null &&
                name.isNotBlank() &&
                username.isNotBlank() &&
                studentId.isNotBlank() &&
                email.isNotBlank() &&
                password.isNotBlank() &&
                confirmPassword.isNotBlank()
    }
}