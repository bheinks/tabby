package dev.heinkel.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jooq.DSLContext
import org.jooq.generated.tabby.tables.daos.UserDao
import org.jooq.generated.tabby.tables.pojos.User
import java.util.UUID

class UserService(dslContext: DSLContext) {
    private val userDao = UserDao(dslContext.configuration())

    suspend fun createUser(username: String, email: String, firstName: String, lastName: String): User =
        withContext(Dispatchers.IO) {
            val user = User(UUID.randomUUID(), username, email, firstName, lastName)
            userDao.insert(user)
            user
    }

    suspend fun getUserByUsername(username: String): User =
        withContext(Dispatchers.IO) {
            userDao.fetchOneByUsername(username)
        }

    suspend fun getUserByEmail(email: String): User =
        withContext(Dispatchers.IO) {
            userDao.fetchOneByEmail(email)
        }
}