class AuthViewModel(
    private val repository: AuthRepository = AuthRepository(),
    private val manager: AuthManager = AuthManager(),
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

    fun register(name: String, username: String, studentId: String, email: String, password: String) {
        val usernameError = validateUsername(username)
        if (usernameError != null) {
            _authState.value = AuthResult.Error(usernameError)
            return
        }

        _authState.value = AuthResult.Loading

        viewModelScope.launch {
            val result = repository.registerIfAllowed(
                name = name,
                studentId = studentId,
                email = email,
                password = password
            )

            if (result is AuthResult.Success) {
                val user = FirebaseAuth.getInstance().currentUser
                val uid = user?.uid.orEmpty()

                if (uid.isNotBlank()) {
                    val profile = UserProfile(
                        uid = uid,
                        name = name,
                        username = username,
                        studentId = studentId,
                        email = email,
                        photoUrl = ""
                    )

                    try {
                        userRepository.createUserProfile(profile)
                        walletRepository.initWallet(uid)
                    } catch (e: Exception) {
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