package menu.editor


data class Order(
    val user: String,
    val dishes: MutableList<OrderedDish>,
    var totalPrice: Double = 0.0,
    var maxCookingTime: Int = 0,
    var status: DishStatus
) {
    fun displayOrder() {
        println("Заказ пользователя $user:")
        dishes.forEachIndexed { index, orderedDish ->
            println("${index + 1}. ${orderedDish.dish.name} - ${orderedDish.quantity} порций")
        }
        println("Суммарная стоимость: $totalPrice руб.")
        println("Максимальное время приготовления: $maxCookingTime мин.")
        println("Статус заказа: $status")
    }
}
