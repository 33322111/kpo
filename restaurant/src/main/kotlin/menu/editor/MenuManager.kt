package menu.editor

interface MenuManager {
    fun displayMenu()
    fun addDish(name: String, quantity: Int, price: Double, cookingTime: Int)
    fun removeDish(dishNumber: Int)
    fun editDish(dishNumber: Int, property: String, value: Any)
    fun viewFullMenu()
}