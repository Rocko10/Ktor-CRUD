package com.monkeys.service

import com.monkeys.repository.MonkeyGateway

interface UpdateMonkeyService {
    fun execute(params: Map<String, String>)
}

class UpdateMonkeyServiceImp(private val monkeyGateway: MonkeyGateway) : UpdateMonkeyService {
    override fun execute(params: Map<String, String>) {
       this.monkeyGateway.updateMonkey(params["id"]!!.toInt(), params["name"]!!)
    }
}