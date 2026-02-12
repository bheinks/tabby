package dev.heinkel.service

import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jooq.DSLContext
import org.jooq.generated.tabby.tables.daos.GroupDao
import org.jooq.generated.tabby.tables.pojos.Category
import org.jooq.generated.tabby.tables.pojos.Group
import org.jooq.generated.tabby.tables.pojos.Transaction
import org.jooq.generated.tabby.tables.pojos.User

class GroupService(dslContext: DSLContext) {
    private val groupDao = GroupDao(dslContext.configuration())
    private val groupUserService = GroupUserService(dslContext)
    private val transactionService = TransactionService(dslContext)

    suspend fun createGroup(name: String, notes: String? = null): Group =
        withContext(Dispatchers.IO) {
            val group = Group(UUID.randomUUID(), name, notes)
            groupDao.insert(group)
            group
        }

    suspend fun Group.addUser(user: User, isAdmin: Boolean = false) =
        groupUserService.createGroupUser(this, user, isAdmin)

    suspend fun Group.setAdmin(user: User, isAdmin: Boolean) =
        groupUserService.updateAdmin(this, user, isAdmin)

    suspend fun Group.getTransactions(): List<Transaction> =
        transactionService.getGroupTransactions(this)

    suspend fun Group.addTransaction(
        paidBy: User, cost: BigDecimal, description: String, category: Category,
        timestamp: OffsetDateTime = OffsetDateTime.now(), notes: String? = null
    ): Transaction =
        transactionService.createTransaction(this, paidBy, cost, description, category, timestamp, notes)
}