package com.monkeys.repository

import org.jetbrains.exposed.sql.*

interface MonkeyGateway {
    fun getMonkeys(): List<Monkey?>
    fun createMonkey(newMonkey: NewMonkey): Boolean
    fun updateMonkey(id: Int, name: String)
    fun showMonkey(id: Int): Monkey?
    fun deleteMonkey(id: Int)
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
        val id = Monkeys.insert {
            it[name] = newMonkey.name
        } get Monkeys.id

        return id ?: -1 >= 0
    }

    override fun updateMonkey(id: Int, name: String) {
        Monkeys.update({ Monkeys.id eq id }) {
            it[Monkeys.name] = name
        }
    }

    override fun showMonkey(id: Int): Monkey? {
        val query = Monkeys.select { Monkeys.id.eq(id) }.firstOrNull() ?: return null

        return Monkey(query[Monkeys.id], query[Monkeys.name])
    }

    override fun deleteMonkey(id: Int) {
        Monkeys.deleteWhere {
            Monkeys.id eq id
        }
    }
}