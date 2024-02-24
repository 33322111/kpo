package menu.editor

import java.io.File
import kotlin.math.max


class OrderManager(private val orderFile: String, private val menuManager: MenuManager) {
    private var orders: MutableList<Order> = loadOrders()


    fun makeOrder(username: String) {
        val order = Order(username, mutableListOf(), 0.0, 0)

        do {
            menuManager.displayMenu()

            println("Выберите номер блюда для заказа:")
            val dishNumber = readLine()?.toIntOrNull()

            if (dishNumber != null && dishNumber in 1..menuManager.getMenu().size) {
                val dish = menuManager.getMenu()[dishNumber - 1]
                println("Введите количество порций (максимум ${dish.quantity}):")
                val quantity = readLine()?.toIntOrNull()

                if (quantity != null && quantity > 0 && quantity <= dish.quantity) {
                    val orderedDish = OrderedDish(dish, quantity)
                    order.dishes.add(orderedDish)
                    dish.quantity -= quantity
                    order.totalPrice += orderedDish.calculateTotalPrice()
                    order.maxCookingTime = max(order.maxCookingTime, orderedDish.dish.cookingTime)
                    println("Блюдо добавлено в заказ.")
                } else {
                    println("Ошибка: Некорректное количество порций.")
                }
            } else {
                println("Ошибка: Некорректный номер блюда.")
            }

            println("Ваш текущий заказ:")
            order.dishes.forEachIndexed { index, orderedDish ->
                println("${index + 1}. ${orderedDish.dish.name} - ${orderedDish.quantity} порций")
            }

            println("Хотите добавить еще блюдо в заказ? (да/любая другая строка):")
        } while (readLine()?.toLowerCase() == "да")

        if (order.totalPrice > 0.0) {
            orders.add(order)
            saveOrders()
            println("Спасибо, Ваш заказ №${orders.indexOf(order) + 1} принят в работу! " +
                    "Суммарная стоимость заказа: ${order.totalPrice} руб. " +
                    "Время приготовления: ${order.maxCookingTime} мин.")
        } else {
            println("Ошибка: Итоговая сумма заказа равна 0. Пожалуйста, добавьте блюда в заказ.")
        }
    }

    private fun loadOrders(): MutableList<Order> {
        if (File(orderFile).exists()) {
            val lines = File(orderFile).readLines()
            return lines.mapNotNull { parseOrder(it) }.toMutableList()
        }
        return mutableListOf()
    }

    private fun parseOrder(line: String): Order? {
        val parts = line.split(";")
        if (parts.size >= 4) {
            val userId = parts[0]
            val dishes = parts.subList(1, parts.size - 1).mapNotNull { parseOrderedDish(it) }
            val totalPrice = parts[parts.size - 1].toDoubleOrNull()
            if (userId != null && dishes.isNotEmpty() && totalPrice != null) {
                return Order(userId, dishes.toMutableList(), totalPrice)
            }
        }
        return null
    }
    private fun saveOrders() {
        val content = orders.joinToString("\n") { formatOrder(it) }
        File(orderFile).writeText(content)
    }

    private fun formatOrder(order: Order): String {
        val username = order.user
        val dishes = order.dishes.joinToString(";") { formatOrderedDish(it) }
        val totalPrice = order.totalPrice
        return "$username;$dishes;$totalPrice"
    }

    private fun parseOrderedDish(line: String): OrderedDish? {
        val parts = line.split(",")
        if (parts.size == 2) {
            val dishId = parts[0].toIntOrNull()
            val quantity = parts[1].toIntOrNull()
            if (dishId != null && quantity != null) {
                return OrderedDish(menuManager.getMenu()[dishId - 1], quantity)
            }
        }
        return null
    }

    private fun formatOrderedDish(orderedDish: OrderedDish): String {
        val dishId = menuManager.getMenu().indexOf(orderedDish.dish) + 1
        val quantity = orderedDish.quantity
        return "$dishId,$quantity"
    }
}
