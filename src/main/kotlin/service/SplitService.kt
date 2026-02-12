package dev.heinkel.service

import java.math.BigDecimal
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jooq.DSLContext
import org.jooq.generated.tabby.tables.daos.SplitDao
import org.jooq.generated.tabby.tables.pojos.Split
import org.jooq.generated.tabby.tables.pojos.Transaction
import org.jooq.generated.tabby.tables.pojos.User

class SplitService(dslContext: DSLContext) {
    private val splitDao = SplitDao(dslContext.configuration())

    suspend fun createSplit(
        owedBy: User,
        transaction: Transaction,
        amount: BigDecimal,
        settled: Boolean = false
    ): Split =
        withContext(Dispatchers.IO) {
            val split = Split(
                UUID.randomUUID(),
                owedBy.id,
                transaction.id,
                amount,
                settled
            )
            splitDao.insert(split)
            split
        }

    suspend fun getSplitsByTransaction(transaction: Transaction): List<Split> =
        withContext(Dispatchers.IO) {
            splitDao.fetchByTransactionId(transaction.id)
        }

    suspend fun deleteSplit(split: Split) {
        withContext(Dispatchers.IO) {
            splitDao.delete(split)
        }
    }

    suspend fun Split.settle() {
        val split = this
        withContext(Dispatchers.IO) {
            split.settled = true
            splitDao.update(split)
        }
    }
}