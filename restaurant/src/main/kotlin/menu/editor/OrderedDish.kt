package menu.editor

data class OrderedDish(val dish: Dish, val quantity: Int, var status: DishStatus = DishStatus.NOT_STARTED) {
    fun calculateTotalPrice(): Double {
        return dish.price * quantity
    }
}