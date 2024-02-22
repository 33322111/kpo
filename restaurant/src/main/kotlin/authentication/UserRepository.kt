package authentication

interface UserRepository {
    fun createUser(username: String, password: String, userType: UserType)
    fun getUser(username: String): User?
}