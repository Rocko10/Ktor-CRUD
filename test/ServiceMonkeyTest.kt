package com.monkeys

import com.monkeys.repository.Monkey
import com.monkeys.repository.MonkeyGateway
import com.monkeys.repository.NewMonkey
import com.monkeys.service.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

var monkeys = mutableListOf<Monkey>()

class MonkeyTestRepository : MonkeyGateway {
    override fun getMonkeys(): List<Monkey?> {
        return monkeys
    }

    override fun createMonkey(newMonkey: NewMonkey): Boolean {
        val size = monkeys.size
        val lastId = monkeys[size - 1]?.id

        monkeys.add(Monkey(lastId, newMonkey.name))

        return monkeys[size - 1]?.id == lastId
    }

    override fun updateMonkey(id: Int, name: String) {
        for (monkey in monkeys) {
            if (monkey.id == id) {
                monkey.name = name
            }
        }
    }

    override fun showMonkey(id: Int): Monkey? {
        for (m in monkeys) {
            if (m.id == id) {
                return m
            }
        }

        return null
    }

    override fun deleteMonkey(id: Int) {
        for (monkey in monkeys) {
            if (monkey.id == id) {
                monkeys.remove(monkey)
            }
        }
    }
}

class ServiceMonkeyTest : KoinTest {

    private val getMonkeysService by inject<GetMonkeysService>()
    private val createMonkeyService by inject<CreateMonkeyService>()
    private val updateMonkeyService by inject<UpdateMonkeyService>()
    private val showMonkeyService by inject<ShowMonkeyService>()
    private val deleteMonkeyService by inject<DeleteMonkeyService>()

    private val getMonkeysTestModule = module {
        single<MonkeyGateway> { MonkeyTestRepository() }
        single<GetMonkeysService> { GetMonkeysServiceImp(MonkeyTestRepository()) }
        single<CreateMonkeyService> { CreateMonkeyServiceImp(MonkeyTestRepository()) }
        single<UpdateMonkeyService> { UpdateMonkeyServiceImp(MonkeyTestRepository()) }
        single<ShowMonkeyService> { ShowMonkeyServiceImp(MonkeyTestRepository()) }
        single<DeleteMonkeyService> { DeleteMonkeyServiceImp(MonkeyTestRepository()) }
    }

    @Before
    fun before() {
        startKoin { modules(listOf(getMonkeysTestModule)) }
        monkeys = mutableListOf(
            Monkey(1, "Mojo"),
            Monkey(2, "Jeffrey"),
            Monkey(3, "Patrick")
        )
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

    @Test
    fun testCreateMonkey() {
        var monkeys: List<Monkey?> = getMonkeysService.execute()

        assertEquals(monkeys.size, 3)

        val newMonkey = NewMonkey(null, "Maurice")
        val result = createMonkeyService.execute(newMonkey)

        assertTrue(result)

        monkeys = getMonkeysService.execute()

        assertEquals(monkeys.size, 4)
        assertEquals(monkeys[3]?.name, "Maurice")
    }

    @Test
    fun testUpdateMonkey() {
        var monkeys: List<Monkey?> = getMonkeysService.execute()

        assertEquals(monkeys[0]?.name, "Mojo")

        updateMonkeyService.execute(mapOf("id" to "1", "name" to "Mojo II"))

        monkeys = getMonkeysService.execute()

        assertEquals(monkeys[0]?.name, "Mojo II")
    }

    @Test
    fun testShowMonkey() {
        val m1: Monkey? = showMonkeyService.execute(1)

        assertEquals(m1!!.name, "Mojo")

        val m2: Monkey? = showMonkeyService.execute(5)

        assertEquals(m2, null)
    }

    @Test
    fun testDeleteMonkey() {
        var monkeys: List<Monkey?> = getMonkeysService.execute()

        assertEquals(monkeys.size, 3)
        assertEquals(showMonkeyService.execute(2)?.name, "Jeffrey")

        deleteMonkeyService.execute(2)

        monkeys = getMonkeysService.execute()

        assertEquals(monkeys.size, 2)
        assertNull(showMonkeyService.execute(2))
    }
}
