package com.monkeys

import com.monkeys.config.dbConnect
import com.monkeys.repository.Monkey
import com.monkeys.repository.MonkeyRepository
import com.monkeys.repository.NewMonkey
import com.monkeys.service.CreateMonkeyService
import com.monkeys.service.CreateMonkeyServiceImp
import com.monkeys.service.GetMonkeysService
import com.monkeys.service.GetMonkeysServiceImp
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import freemarker.cache.*
import io.ktor.freemarker.*
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.dsl.module.module
import org.koin.ktor.ext.inject
import org.koin.standalone.StandAloneContext.startKoin

val monkeysModule = module {
    single { GetMonkeysServiceImp(MonkeyRepository()) as GetMonkeysService }
    single { CreateMonkeyServiceImp(MonkeyRepository()) as CreateMonkeyService }
}

fun main(args: Array<String>): Unit {
    startKoin(listOf(monkeysModule))
    io.ktor.server.netty.EngineMain.main(args)
}

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }

    dbConnect()

    val getMonkeysService by inject<GetMonkeysService>()
    val createMonkeyService by inject<CreateMonkeyService>()

    val client = HttpClient(Apache) {
    }

    routing {
        get("/") {
            var monkeys: List<Monkey?> = listOf()

            withContext(Dispatchers.IO) {
                transaction {
                    monkeys = getMonkeysService.execute()
                }
            }

            call.respond(FreeMarkerContent(
                "monkeys/index.ftl",
                mapOf("monkeys" to monkeys, "size" to monkeys.size),
                ""
                )
            )
        }

        get("/new") {
            call.respond(FreeMarkerContent("monkeys/new.ftl", null))
        }

        post("/new") {
            val name: String? = call.receiveParameters()["name"]

            withContext(Dispatchers.IO) {
                transaction {
                    createMonkeyService.execute(NewMonkey(id = null, name = name!!))
                }
                call.respondRedirect("/")
            }
        }

        get("/html-freemarker") {
            call.respond(FreeMarkerContent("index.ftl", mapOf("name" to "Jerry"), ""))
        }
    }
}

data class IndexData(val items: List<Int>)

