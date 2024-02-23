package menu.editor

import java.io.File

class InMemoryMenuManager(private val menuFile: String) : MenuManager {
    private val menu: MutableList<Dish> = mutableListOf()

    init {
        loadMenuFromFile()
    }

    override fun displayMenu() {
        println("Меню:")
        menu.forEachIndexed { index, dish ->
            println("${index + 1}. ${dish.name} - ${dish.price} руб., ${dish.quantity} порций, готовится за ${dish.cookingTime} мин.")
        }
    }

    override fun addDish(name: String, quantity: Int, price: Double, cookingTime: Int) {
        menu.add(Dish(name, quantity, price, cookingTime))
        saveMenuToFile()
    }

    override fun removeDish(dishNumber: Int) {
        if (dishNumber in 1..menu.size) {
            menu.removeAt(dishNumber - 1)
            println("Блюдо удалено.")
            saveMenuToFile()
        } else {
            println("Ошибка: Некорректный номер блюда.")
        }
    }

    override fun editDish(dishNumber: Int, property: String, value: Any) {
        if (dishNumber in 1..menu.size) {
            val dish = menu[dishNumber - 1]
            when (property.toLowerCase()) {
                "quantity" -> {
                    if (value is Int && value > 0) {
                        dish.quantity = value
                        println("Количество блюд изменено.")
                    } else {
                        println("Ошибка: Некорректное количество блюд.")
                    }
                }
                "price" -> {
                    if (value is Double && value > 0) {
                        dish.price = value
                        println("Цена блюда изменена.")
                    } else {
                        println("Ошибка: Некорректная цена блюда.")
                    }
                }
                "cookingtime" -> {
                    if (value is Int && value > 0) {
                        dish.cookingTime = value
                        println("Время приготовления блюда изменено.")
                    } else {
                        println("Ошибка: Некорректное время приготовления блюда.")
                    }
                }
                else -> {
                    println("Ошибка: Некорректный параметр для редактирования.")
                }
            }
            saveMenuToFile()
        } else {
            println("Ошибка: Некорректный номер блюда.")
        }
    }

    override fun viewFullMenu() {
        println("Полное меню:")
        menu.forEach { dish ->
            println("${dish.name} - ${dish.price} руб., ${dish.quantity} порций, готовится за ${dish.cookingTime} мин.")
        }
    }

    private fun loadMenuFromFile() {
        if (File(menuFile).exists()) {
            val lines = File(menuFile).readLines()
            menu.addAll(lines.mapNotNull { parseDish(it) })
        }
    }

    private fun saveMenuToFile() {
        val content = menu.joinToString("\n") { formatDish(it) }
        File(menuFile).writeText(content)
    }

    private fun parseDish(line: String): Dish? {
        val parts = line.split(";")
        return if (parts.size == 4) {
            Dish(parts[0], parts[1].toInt(), parts[2].toDouble(), parts[3].toInt())
        } else {
            null
        }
    }

    private fun formatDish(dish: Dish): String {
        return "${dish.name};${dish.quantity};${dish.price};${dish.cookingTime}"
    }
}
