package dev.heinkel.service

import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jooq.DSLContext
import org.jooq.generated.tabby.tables.Category.CATEGORY
import org.jooq.generated.tabby.tables.daos.CategoryDao
import org.jooq.generated.tabby.tables.pojos.Category

const val DEFAULT_CATEGORY_NAME = "Uncategorized"
const val DEFAULT_SUBCATEGORY = "General"

class CategoryService(private val dslContext: DSLContext) {
    private val categoryDao = CategoryDao(dslContext.configuration())

    suspend fun createCategory(name: String, subcategory: String): Category =
        withContext(Dispatchers.IO) {
            val category = Category(UUID.randomUUID(), name, subcategory)
            categoryDao.insert(category)
            category
        }

    suspend fun getCategories(): List<Category> =
        withContext(Dispatchers.IO) {
            categoryDao.findAll()
        }

    suspend fun getCategoryByName(name: String, subcategory: String): Category? =
        withContext(Dispatchers.IO) {
            dslContext.selectFrom(CATEGORY)
                .where(CATEGORY.NAME.eq(name))
                .and(CATEGORY.SUBCATEGORY.eq(subcategory))
                .fetchOneInto(Category::class.java)
        }

    suspend fun getDefaultCategory(): Category =
        getCategoryByName(DEFAULT_CATEGORY_NAME, DEFAULT_SUBCATEGORY) ?: createCategory(
            DEFAULT_CATEGORY_NAME,
            DEFAULT_SUBCATEGORY
        )
}