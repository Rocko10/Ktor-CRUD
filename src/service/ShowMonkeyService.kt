package com.monkeys.service

import com.monkeys.repository.Monkey
import com.monkeys.repository.MonkeyGateway

interface ShowMonkeyService {
    fun execute(id: Int): Monkey?
}

class ShowMonkeyServiceImp(val monkeyGateway: MonkeyGateway) : ShowMonkeyService {
    override fun execute(id: Int): Monkey? {
        return this.monkeyGateway.showMonkey(id)
    }
}