package com.monkeys.repository

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll

interface MonkeyGateway {
    fun getMonkeys(): List<Monkey?>
    fun createMonkey(newMonkey: NewMonkey): Boolean
    fun updateMonkey(id: Int, name: String)
}

data class Monkey(val id: Int, var name: String)
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

    override fun createMonkey(newMonkey: NewMonkey): Boolean {
        val id =Monkeys.insert {
            it[name] = newMonkey.name
        } get Monkeys.id

        return id ?: -1 >= 0
    }

    override fun updateMonkey(id: Int, name: String) {

    }
}