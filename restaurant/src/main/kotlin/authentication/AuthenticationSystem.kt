package authentication

open class AuthenticationSystem(private val userRepository: UserRepository) {
    fun registerUser(username: String, password: String, userType: UserType) {
        val existingUser = userRepository.getUser(username)
        if (existingUser != null) {
            println("Пользователь с таким логином уже существует.")
            return
        }

        if (passwordsMatch(password, repeatPassword())) {
            userRepository.createUser(username, password, userType)
            println("Пользователь успешно зарегистрирован.")
        } else {
            println("Пароли не совпадают. Повторите ввод.")
            registerUser(username, password, userType)
        }
    }

    open fun authenticateUser(username: String, password: String, userType: UserType): Boolean {
        val user = userRepository.getUser(username)

        if (user != null && user.password == password && user.userType == userType) {
            println("Аутентификация успешна для пользователя $username.")
            return true
        } else {
            println("Ошибка аутентификации. Проверьте введенные данные.")
            return false
        }
    }

    private fun passwordsMatch(password1: String, password2: String): Boolean {
        return password1 == password2
    }

    private fun repeatPassword(): String {
        println("Повторите пароль:")
        return readLine().orEmpty()
    }
}