package com.monkeys.repository

interface MonkeyGateway {
    fun getMonkeys(): List<Monkey?>
}

data class Monkey(val id: Int, val name: String)
data class NewMonkey(val id: Int?, val name: String)

class MonkeyRepository : MonkeyGateway {
    override fun getMonkeys(): List<Monkey?> {
        // TODO: Connect with DB to retrieve the data
        return listOf()
    }
}