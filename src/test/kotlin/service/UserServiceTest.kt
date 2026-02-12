package dev.heinkel.service

import kotlin.test.assertFailsWith
import kotlinx.coroutines.test.runTest
import org.jooq.exception.IntegrityConstraintViolationException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class UserServiceTest : DatabaseIntegrationTest() {
    private lateinit var userService: UserService

    @Test
    fun `create and get user by username`() = runTest {
        userService = UserService(dslContext)

        val expectedUser = userService.createUser("username", "email", "firstName", "lastName")
        val actualUser = userService.getUserByUsername("username")

        assertEquals(expectedUser.id, actualUser.id)
        assertEquals(expectedUser.username, actualUser.username)
        assertEquals(expectedUser.email, actualUser.email)
        assertEquals(expectedUser.firstName, actualUser.firstName)
        assertEquals(expectedUser.lastName, actualUser.lastName)
    }

    @Test
    fun `create and get user by email`() = runTest {
        userService = UserService(dslContext)

        val expectedUser = userService.createUser("username", "email", "firstName", "lastName")
        val actualUser = userService.getUserByEmail("email")

        assertEquals(expectedUser.id, actualUser.id)
        assertEquals(expectedUser.username, actualUser.username)
        assertEquals(expectedUser.email, actualUser.email)
        assertEquals(expectedUser.firstName, actualUser.firstName)
        assertEquals(expectedUser.lastName, actualUser.lastName)
    }

    @Test
    fun `check create user email case sensitivity`() = runTest {
        userService = UserService(dslContext)

        userService.createUser("username1", "email", "firstName", "lastName")
        assertFailsWith<IntegrityConstraintViolationException> {
            userService.createUser("username2", "EMAIL", "firstName", "lastName")
        }
    }
}