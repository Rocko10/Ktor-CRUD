package com.monkeys.service

import com.monkeys.repository.Monkey
import com.monkeys.repository.MonkeyGateway

interface GetMonkeysService {
    fun execute(): List<Monkey?>
}

class GetMonkeysServiceImp(private val monkeyGateway: MonkeyGateway) : GetMonkeysService {
    override fun execute(): List<Monkey?> {
        return this.monkeyGateway.getMonkeys()
    }
}