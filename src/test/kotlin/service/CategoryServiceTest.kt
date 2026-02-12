package dev.heinkel.service

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CategoryServiceTest : DatabaseIntegrationTest() {
    private lateinit var categoryService: CategoryService

    @Test
    fun `create and get category by name`() = runTest {
        categoryService = CategoryService(dslContext)

        val expectedCategory = categoryService.createCategory("name", "subcategory")
        val actualCategory = categoryService.getCategoryByName("name", "subcategory")

        actualCategory!!
        assertEquals(expectedCategory.id, actualCategory.id)
        assertEquals(expectedCategory.name, actualCategory.name)
        assertEquals(expectedCategory.subcategory, actualCategory.subcategory)
    }
}