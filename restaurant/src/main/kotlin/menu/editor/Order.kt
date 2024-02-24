package menu.editor


data class Order(
    val user: String,
    val dishes: MutableList<OrderedDish>,
    var totalPrice: Double = 0.0,
    var maxCookingTime: Int = 0
)
