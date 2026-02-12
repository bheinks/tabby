package dev.heinkel.service

import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jooq.DSLContext
import org.jooq.generated.tabby.tables.daos.PaymentDao
import org.jooq.generated.tabby.tables.pojos.Payment
import org.jooq.generated.tabby.tables.pojos.Split

class PaymentService(dslContext: DSLContext) {
    private val paymentDao = PaymentDao(dslContext.configuration())

    suspend fun createPayment(split: Split, amount: BigDecimal, timestamp: OffsetDateTime): Payment =
        withContext(Dispatchers.IO) {
            val payment = Payment(UUID.randomUUID(), split.id, amount, timestamp)
            paymentDao.insert(payment)
            payment
        }
}