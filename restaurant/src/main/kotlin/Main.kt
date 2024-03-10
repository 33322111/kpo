import authentication.FileUserRepository
import authentication.UserType

import menu.editor.InMemoryMenuManager
import menu.editor.ExtendedAuthenticationSystem
fun main() {
    val userService = FileUserRepository("user_data.txt")
    val menuManager = InMemoryMenuManager("menu.txt")
    val authenticationSystem = ExtendedAuthenticationSystem(userService, menuManager)
    var authenticated = false

    while (true) {
        println("Выберите действие: 1 - Регистрация, 2 - Аутентификация, 3 - Закончить работу ресторана")
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
                var isAuthenticated = false
                var username = ""
                while (!isAuthenticated) {
                    println("Введите логин:")
                    username = readLine().orEmpty()

                    println("Введите пароль:")
                    val password = readLine().orEmpty()

                    isAuthenticated = authenticationSystem.registerUser(username, password, userType)
                }

                println("Пользователь $username успешно зарегистрирован!")
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
            3 -> {
                break
            }
            else -> {
                println("Некорректный выбор действия.")
                continue
            }
        }
    }

    println("Программа завершена.")
}
