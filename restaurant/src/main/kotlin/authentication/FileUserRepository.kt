package authentication

import java.io.File

class FileUserRepository(private val filePath: String) : UserRepository {
    private val users: MutableList<User> = mutableListOf()

    init {
        loadUsersFromFile()
    }

    override fun createUser(username: String, password: String, userType: UserType) {
        users.add(User(username, password, userType))
        saveUsersToFile()
    }

    override fun getUser(username: String): User? {
        return users.find { it.username == username }
    }

    private fun loadUsersFromFile() {
        if (File(filePath).exists()) {
            File(filePath).forEachLine {
                val (username, password, userTypeStr) = it.split(",")
                val userType = UserType.valueOf(userTypeStr)
                users.add(User(username, password, userType))
            }
        }
    }

    private fun saveUsersToFile() {
        File(filePath).bufferedWriter().use { writer ->
            users.forEach {
                writer.write("${it.username},${it.password},${it.userType}\n")
            }
        }
    }
}