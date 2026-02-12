package dev.heinkel.service

import org.flywaydb.core.Flyway
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.generated.tabby.tables.Category.CATEGORY
import org.jooq.generated.tabby.tables.Group.GROUP
import org.jooq.generated.tabby.tables.GroupUser.GROUP_USER
import org.jooq.generated.tabby.tables.Split.SPLIT
import org.jooq.generated.tabby.tables.Transaction.TRANSACTION
import org.jooq.generated.tabby.tables.User.USER
import org.jooq.impl.DSL
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.postgresql.ds.PGSimpleDataSource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class DatabaseIntegrationTest {
    private val postgres = PostgreSQLContainer("postgres:18.1")
        .withDatabaseName("testDb")
        .withUsername("test")
        .withPassword("test")

    protected lateinit var dslContext: DSLContext

    @BeforeAll
    fun setupDatabase() {
        postgres.start()

        val dataSource = PGSimpleDataSource().apply {
            setUrl(postgres.jdbcUrl)
            user = postgres.username
            password = postgres.password
        }

        Flyway.configure()
            .dataSource(dataSource)
            .load()
            .migrate()

        dslContext = DSL.using(dataSource, SQLDialect.POSTGRES)
    }

    @BeforeEach
    fun clearTables() {
        dslContext.delete(CATEGORY).execute()
        dslContext.delete(GROUP).execute()
        dslContext.delete(GROUP_USER).execute()
        dslContext.delete(SPLIT).execute()
        dslContext.delete(TRANSACTION).execute()
        dslContext.delete(USER).execute()
    }
}