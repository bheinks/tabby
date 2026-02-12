package dev.heinkel.service

import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jooq.DSLContext
import org.jooq.generated.tabby.tables.daos.TransactionDao
import org.jooq.generated.tabby.tables.pojos.Category
import org.jooq.generated.tabby.tables.pojos.Group
import org.jooq.generated.tabby.tables.pojos.Split
import org.jooq.generated.tabby.tables.pojos.Transaction
import org.jooq.generated.tabby.tables.pojos.User

class TransactionService(dslContext: DSLContext) {
    private val transactionDao = TransactionDao(dslContext.configuration())
    private val splitService = SplitService(dslContext)

    suspend fun getGroupTransactions(group: Group): List<Transaction> =
        withContext(Dispatchers.IO) {
            transactionDao.fetchByGroupId(group.id)
        }

    suspend fun createTransaction(
        group: Group, paidBy: User, cost: BigDecimal, description: String, category: Category,
        timestamp: OffsetDateTime = OffsetDateTime.now(), notes: String? = null
    ): Transaction =
        withContext(Dispatchers.IO) {
            val transaction = Transaction(
                UUID.randomUUID(),
                paidBy.id,
                cost,
                description,
                group.id,
                category.id,
                timestamp,
                notes
            )
            transactionDao.insert(transaction)
            transaction
        }

    private fun splitEqually(numSplits: Int, amount: BigDecimal): List<BigDecimal> {
        val totalCents = amount.movePointRight(2)
        val baseSplit = totalCents.divide(numSplits.toBigDecimal())
        var centsShort = (totalCents - baseSplit).toInt()

        // Apply any remaining amount randomly when creating splits
        return MutableList(numSplits) {
            (baseSplit + (if (centsShort-- > 0) 1 else 0).toBigDecimal()).movePointLeft(2)
        }.shuffled()
    }

    suspend fun Transaction.getSplits(): List<Split> =
        splitService.getSplitsByTransaction(this)

    suspend fun Transaction.deleteSplits() {
        val splits = splitService.getSplitsByTransaction(this)
        for (split in splits) {
            splitService.deleteSplit(split)
        }
    }

    suspend fun Transaction.split(users: List<User>): List<Split> {
        this.deleteSplits()
        val splitAmounts = splitEqually(users.size, this.cost)
        return users.zip(splitAmounts) { user, amount -> splitService.createSplit(user, this, amount) }
    }
}