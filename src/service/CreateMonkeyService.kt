package com.monkeys.service

import com.monkeys.repository.MonkeyGateway
import com.monkeys.repository.NewMonkey

interface CreateMonkeyService {
    fun execute(newMonkey: NewMonkey): Boolean
}

class CreateMonkeyServiceImp(val monkeyGateway: MonkeyGateway) : CreateMonkeyService {
    override fun execute(newMonkey: NewMonkey): Boolean {
        return this.monkeyGateway.createMonkey(newMonkey)
    }
}