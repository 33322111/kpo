package menu.editor

data class OrderedDish(val dish: Dish, val quantity: Int) {
    fun calculateTotalPrice(): Double {
        return dish.price * quantity
    }
}