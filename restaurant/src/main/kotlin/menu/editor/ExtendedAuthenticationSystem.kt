package menu.editor
import authentication.AuthenticationSystem
import authentication.UserType
import authentication.FileUserRepository
class ExtendedAuthenticationSystem(
    private val userService: FileUserRepository,
    private val menuManager: MenuManager
) : AuthenticationSystem(userService) {
    override fun authenticateUser(username: String, password: String, userType: UserType): Boolean {
        val isAuthenticated = super.authenticateUser(username, password, userType)
        if (isAuthenticated && userType == UserType.ADMIN) {
            adminMenu()
        }
        return isAuthenticated
    }

    private fun adminMenu() {
        val menuManager = InMemoryMenuManager("menu.txt")

        while (true) {
            println("Меню администратора:")
            println("1. Добавить блюдо")
            println("2. Удалить блюдо")
            println("3. Редактировать блюдо")
            println("4. Посмотреть всё меню")
            println("5. Закончить смену")

            when (readLine()?.toIntOrNull()) {
                1 -> addDish()
                2 -> removeDish()
                3 -> editDish()
                4 -> viewFullMenu()
                5 -> return
                else -> println("Некорректный выбор.")
            }
        }
    }

    private fun addDish() {
        println("Введите название блюда:")
        val name = readLine().orEmpty()

        println("Введите количество порций:")
        val quantity = readLine()?.toIntOrNull() ?: 0
        if (quantity <= 0) {
            println("Ошибка: Некорректное количество порций.")
            return
        }

        println("Введите цену блюда:")
        val price = readLine()?.toDoubleOrNull() ?: 0.0
        if (price <= 0) {
            println("Ошибка: Некорректная цена блюда.")
            return
        }

        println("Введите время приготовления в минутах:")
        val cookingTime = readLine()?.toIntOrNull() ?: 0
        if (cookingTime <= 0) {
            println("Ошибка: Некорректное время приготовления блюда.")
            return
        }

        menuManager.addDish(name, quantity, price, cookingTime)
        println("Блюдо успешно добавлено в меню.")
    }

    private fun removeDish() {
        menuManager.displayMenu()
        println("Введите номер блюда для удаления:")
        val dishNumber = readLine()?.toIntOrNull() ?: 0
        menuManager.removeDish(dishNumber)
    }

    private fun viewFullMenu() {
        menuManager.viewFullMenu()
    }

    private fun editDish() {
        menuManager.displayMenu()
        println("Введите номер блюда для редактирования:")
        val dishNumber = readLine()?.toIntOrNull() ?: 0

        println("Выберите параметр для редактирования:")
        println("1. Количество порций")
        println("2. Цена блюда")
        println("3. Время приготовления")

        when (readLine()?.toIntOrNull()) {
            1 -> editDishProperty(dishNumber, "quantity")
            2 -> editDishProperty(dishNumber, "price")
            3 -> editDishProperty(dishNumber, "cookingtime")
            else -> println("Некорректный выбор.")
        }
    }

    private fun editDishProperty(dishNumber: Int, property: String) {
        println("Введите новое значение:")
        val value = when (property) {
            "quantity", "cookingtime" -> readLine()?.toIntOrNull() ?: 0
            "price" -> readLine()?.toDoubleOrNull() ?: 0.0
            else -> 0
        }
        menuManager.editDish(dishNumber, property, value)
    }
}