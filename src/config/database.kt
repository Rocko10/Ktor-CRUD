package com.monkeys.config

import com.monkeys.repository.Monkeys
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun dbConnect() {
    Database.connect(getConnection())
    transaction { SchemaUtils.create(Monkeys) }
}

suspend fun <T> dbQuery(query: () -> T): T = withContext(Dispatchers.IO) {
    transaction { query() }
}

private fun getConnection(): HikariDataSource {
    val config = HikariConfig()
    config.driverClassName = "org.h2.Driver"
    config.jdbcUrl = "jdbc:h2:mem:test"
    config.maximumPoolSize = 3
    config.isAutoCommit = false
    config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
    config.validate()

    return HikariDataSource(config)
}