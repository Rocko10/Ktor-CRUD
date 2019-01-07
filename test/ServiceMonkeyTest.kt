package com.monkeys

import com.monkeys.repository.Monkey
import com.monkeys.repository.MonkeyGateway
import com.monkeys.service.GetMonkeysService
import com.monkeys.service.GetMonkeysServiceImp
import org.junit.After
import org.junit.Before
import kotlin.test.assertEquals
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.StandAloneContext.stopKoin
import org.koin.standalone.inject
import org.koin.test.KoinTest
import kotlin.test.assertTrue

var monkeys = listOf(
    Monkey(1, "Mojo"),
    Monkey(2, "Jeffrey"),
    Monkey(3, "Patrick")
)

class MonkeyTestRepository : MonkeyGateway {
    override fun getMonkeys(): List<Monkey?> {
        return monkeys
    }
}

class ServiceMonkeyTest : KoinTest {

    val getMonkeysService by inject<GetMonkeysService>()

    val getMonkeysTestModule = module {
        single { MonkeyTestRepository() as MonkeyGateway}
        single { GetMonkeysServiceImp(MonkeyTestRepository()) as GetMonkeysService }
    }

    @Before
    fun before() {
        startKoin(listOf(getMonkeysTestModule))
    }

    @After
    fun after() {
        stopKoin()
    }

    @Test
    fun testGetMonkeys() {
        val monkeys: List<Monkey?> = getMonkeysService.execute()

        assertTrue(monkeys.isNotEmpty())
        assertEquals(monkeys[0]?.id, 1)
        assertEquals(monkeys[0]?.name, "Mojo")
    }

}