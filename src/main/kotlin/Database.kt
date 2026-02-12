package dev.heinkel

import io.ktor.server.plugins.di.annotations.Property
import java.sql.DriverManager
import kotlinx.serialization.Serializable
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.conf.MappedSchema
import org.jooq.conf.RenderMapping
import org.jooq.conf.Settings
import org.jooq.impl.DSL

@Serializable
data class DataSource(
    val host: String,
    val port: Int,
    val db: String,
    val user: String,
    val password: String,
    val schema: String
)

fun provideDslContext(@Property("datasource") ds: DataSource): DSLContext {
    val jdbcUrl = "jdbc:postgresql://${ds.host}:${ds.port}/${ds.db}"
    val connection = DriverManager.getConnection(jdbcUrl, ds.user, ds.password)
    val settings = Settings().withRenderMapping(
        RenderMapping().withSchemata(
            MappedSchema().withInput("public").withOutput(ds.schema)
        )
    )
    return DSL.using(connection, SQLDialect.POSTGRES, settings)
}

