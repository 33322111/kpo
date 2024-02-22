import authentication.AuthenticationSystem
import authentication.FileUserRepository
import authentication.UserType

fun main() {
    val userRepository = FileUserRepository("user_data.txt")
    val authenticationSystem = AuthenticationSystem(userRepository)

    var authenticated = false

    while (!authenticated) {
        println("Выберите действие: 1 - Регистрация, 2 - Аутентификация")
        when (readLine()?.toIntOrNull()) {
            1 -> {
                println("Выберите тип пользователя: 1 - Посетитель, 2 - Администратор")
                val userType = when (readLine()?.toIntOrNull()) {
                    1 -> UserType.VISITOR
                    2 -> UserType.ADMIN
                    else -> {
                        println("Некорректный выбор типа пользователя.")
                        continue
                    }
                }
                println("Введите логин:")
                val username = readLine().orEmpty()
                println("Введите пароль:")
                val password = readLine().orEmpty()

                authenticationSystem.registerUser(username, password, userType)
            }
            2 -> {
                println("Выберите тип пользователя: 1 - Посетитель, 2 - Администратор")
                val userType = when (readLine()?.toIntOrNull()) {
                    1 -> UserType.VISITOR
                    2 -> UserType.ADMIN
                    else -> {
                        println("Некорректный выбор типа пользователя.")
                        continue
                    }
                }
                println("Введите логин:")
                val username = readLine().orEmpty()
                println("Введите пароль:")
                val password = readLine().orEmpty()

                authenticated = authenticationSystem.authenticateUser(username, password, userType)
                if (!authenticated) {
                    println("Повторите попытку.")
                }
            }
            else -> {
                println("Некорректный выбор действия.")
                continue
            }
        }
    }

    println("Программа завершена.")
}
