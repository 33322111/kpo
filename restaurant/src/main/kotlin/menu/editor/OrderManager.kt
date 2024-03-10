package menu.editor

import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.max

class OrderManager(private val orderFile: String, private val menuManager: MenuManager) {
    private var orders: MutableList<Order> = loadOrders()


    fun makeOrder(username: String) {
        val order = Order(username, mutableListOf(), 0.0, 0, DishStatus.NOT_STARTED)

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
                    order.maxCookingTime = maxOf(order.maxCookingTime, orderedDish.dish.cookingTime)
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
        } while (readLine()?.lowercase() == "да")

        if (order.totalPrice > 0.0) {
            orders.add(order)

            processOrderAsync(order)

            println("Спасибо, Ваш заказ №${orders.indexOf(order) + 1} принят в работу! " +
                    "Суммарная стоимость заказа: ${order.totalPrice} руб. " +
                    "Время приготовления: ${order.maxCookingTime} мин.")
        } else {
            println("Ошибка: Итоговая сумма заказа равна 0. Пожалуйста, добавьте блюда в заказ.")
        }
    }

    fun deleteOrder(username: String) {
        val userOrders = orders.filter { it.user == username && it.status != DishStatus.READY && it.status != DishStatus.CANCELLED && it.status != DishStatus.PAID }
        if (userOrders.isEmpty()) {
            println("У вас нет активных заказов.")
            return
        }

        println("Выберите номер заказа для отмены:")
        userOrders.forEachIndexed { index, order ->
            println("${index + 1}. Заказ №${orders.indexOf(order) + 1} - ${order.status}")
        }

        val selectedOrderIndex = readLine()?.toIntOrNull()?.minus(1)
        if (selectedOrderIndex != null && selectedOrderIndex in 0 until userOrders.size) {
            val selectedOrder = userOrders[selectedOrderIndex]
            selectedOrder.status = DishStatus.CANCELLED
            for (orderedDish in selectedOrder.dishes) {
                val dishFromMenu = menuManager.getMenu().find { it.name == orderedDish.dish.name }
                var quantityInMenu = dishFromMenu?.quantity ?: 0
                quantityInMenu += orderedDish.quantity
                dishFromMenu?.quantity = quantityInMenu
            }
            println("Ваш заказ успешно отменён!")
        } else {
            println("Ошибка: Некорректный номер заказа.")
        }
    }

    fun payOrder(username: String) {
        val userOrders = orders.filter { it.user == username && it.status == DishStatus.READY }
        if (userOrders.isEmpty()) {
            println("У вас нет неоплаченных заказов.")
            return
        }

        println("Выберите номер заказа для оплаты:")
        userOrders.forEachIndexed { index, order ->
            println("${index + 1}. Заказ №${orders.indexOf(order) + 1} - ${order.status} - ${order.totalPrice}р.")
        }

        val selectedOrderIndex = readLine()?.toIntOrNull()?.minus(1)
        if (selectedOrderIndex != null && selectedOrderIndex in 0 until userOrders.size) {
            val selectedOrder = userOrders[selectedOrderIndex]
            selectedOrder.status = DishStatus.PAID
            println("Ваш заказ успешно оплачен!")
        } else {
            println("Ошибка: Некорректный номер заказа.")
        }
    }

    fun getProfit() {
        print("Прибыль ресторана от заказов: ")
        println(orders.filter { it.status == DishStatus.READY }
            .sumOf { it.totalPrice })
    }

    fun addDishToOrder(username: String) {
        val userOrders = orders.filter { it.user == username && it.status != DishStatus.READY && it.status != DishStatus.CANCELLED && it.status != DishStatus.PAID}
        if (userOrders.isEmpty()) {
            println("У вас нет активных заказов.")
            return
        }

        println("Выберите номер заказа для добавления блюда:")
        userOrders.forEachIndexed { index, order ->
            println("${index + 1}. Заказ №${orders.indexOf(order) + 1} - ${order.status}")
        }


        val selectedOrderIndex = readLine()?.toIntOrNull()?.minus(1)
        if (selectedOrderIndex != null && selectedOrderIndex in 0 until userOrders.size) {
            val selectedOrder = userOrders[selectedOrderIndex]
            val existingMenu = menuManager.getMenu()

            println("Выберите блюдо для добавления:")
            existingMenu.forEachIndexed { index, dish ->
                println("${index + 1}. ${dish.name} - ${dish.price} руб., ${dish.quantity} порций, готовится за ${dish.cookingTime} мин.")
            }

            val selectedDishIndex = readLine()?.toIntOrNull()?.minus(1)
            if (selectedDishIndex != null && selectedDishIndex in 0 until existingMenu.size) {
                val selectedDish = existingMenu[selectedDishIndex]

                println("Введите количество порций (максимум ${selectedDish.quantity}):")
                val quantity = readLine()?.toIntOrNull()

                if (quantity != null && quantity > 0 && quantity <= selectedDish.quantity) {
                    val orderedDish = OrderedDish(selectedDish, quantity)
                    selectedOrder.dishes.add(orderedDish)
                    selectedDish.quantity -= quantity

                    selectedOrder.maxCookingTime = max(selectedOrder.maxCookingTime, selectedDish.cookingTime)
                    selectedOrder.totalPrice += selectedDish.price * quantity

                    println("Блюдо успешно добавлено в заказ. Обновленное содержание заказа:")
                    selectedOrder.displayOrder()
                } else {
                    println("Ошибка: Некорректное количество порций.")
                }
            } else {
                println("Ошибка: Некорректный номер блюда.")
            }
        } else {
            println("Ошибка: Некорректный номер заказа.")
        }
    }


    private fun processOrderAsync(order: Order) {
        val executorService = Executors.newSingleThreadExecutor()
        executorService.submit {
            println("Приготовление заказа №${orders.indexOf(order) + 1} началось.")
            order.status = DishStatus.COOKING
            TimeUnit.SECONDS.sleep(order.maxCookingTime.toLong())
            if (order.status != DishStatus.CANCELLED) {
                println("Заказ №${orders.indexOf(order) + 1} готов!")
                order.status = DishStatus.READY
                saveOrders()
            }
        }
        executorService.shutdown()
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
            val dishes = parts.subList(1, parts.size - 2).mapNotNull { parseOrderedDish(it) }
            val totalPrice = parts[parts.size - 2].toDoubleOrNull()
            val status = DishStatus.valueOf(parts[parts.size - 1])
            if (userId != null && dishes.isNotEmpty() && totalPrice != null) {
                return Order(userId, dishes.toMutableList(), totalPrice, 0, status)
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
        var status = order.status
        return "$username;$dishes;$totalPrice;$status"
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
