buildscript {
    dependencies {
        classpath(libs.postgresql)
        classpath(libs.flyway.gradle.plugin)
        classpath(libs.flyway.database.postgresql)
    }
}

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
    alias(libs.plugins.jooq.codegen.gradle)
    alias(libs.plugins.flyway)
    alias(libs.plugins.dotenv.gradle)
}

group = "dev.heinkel"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

dependencies {
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.jackson)
    implementation(libs.ktor.server.host.common)
    implementation(libs.ktor.server.status.pages)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.config.yaml)
    implementation(libs.ktor.server.di)
    implementation(libs.jooq)
    implementation(libs.jooq.postgres.extensions)
    implementation(libs.postgresql)
    implementation(libs.flyway.database.postgresql)
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.testcontainers.junit.jupiter)
    testImplementation(libs.testcontainers.postgresql)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.platform.launcher)
}

flyway {
    val host = env.POSTGRES_HOST.value
    val db = env.POSTGRES_DB.value
    val user = env.POSTGRES_USER.value
    val password = env.POSTGRES_PASSWORD.value
    url = "jdbc:postgresql://$host:5432/$db?user=$user&password=$password"
}

jooq {
    configuration {
        jdbc {
            url = flyway.url
        }
        generator {
            generate {
                isPojos = true
                isDaos = true
                pojosIncludes = "tabby.*"
                daosIncludes = "tabby.*"
                recordsIncludes = "tabby.*"
            }
            database {
                forcedTypes {
                    forcedType {
                        userType = "java.lang.String"
                        binding = "org.jooq.postgres.extensions.bindings.CitextBinding"
                        includeTypes = "citext"
                    }
                }
            }
        }
    }
}

tasks.compileKotlin { dependsOn(tasks.jooqCodegen) }
tasks.jooqCodegen { dependsOn(tasks.flywayMigrate) }
tasks.test { useJUnitPlatform()}