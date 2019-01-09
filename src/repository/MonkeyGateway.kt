package com.monkeys.repository

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.selectAll

interface MonkeyGateway {
    fun getMonkeys(): List<Monkey?>
}

data class Monkey(val id: Int, val name: String)
data class NewMonkey(val id: Int?, val name: String)

object Monkeys : Table() {
    val id = integer("id").autoIncrement().primaryKey()
    val name = varchar("name", 100)
}

private fun toMonkey(row: ResultRow): Monkey {
    return Monkey(
        id = row[Monkeys.id],
        name = row[Monkeys.name]
    )
}

class MonkeyRepository : MonkeyGateway {
    override fun getMonkeys(): List<Monkey?> {
        return Monkeys.selectAll().map { toMonkey(it) }
    }
}