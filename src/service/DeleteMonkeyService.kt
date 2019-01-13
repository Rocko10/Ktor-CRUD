package com.monkeys.service

import com.monkeys.repository.MonkeyGateway

interface DeleteMonkeyService {
    fun execute(id: Int)
}

class DeleteMonkeyServiceImp(val monkeyGateway: MonkeyGateway) : DeleteMonkeyService {
    override fun execute(id: Int) {
        this.monkeyGateway.deleteMonkey(id)
    }
}