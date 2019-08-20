package com.monkeys

import com.monkeys.config.dbConnect
import com.monkeys.repository.Monkey
import com.monkeys.repository.MonkeyRepository
import com.monkeys.repository.NewMonkey
import com.monkeys.service.*
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
    single { ShowMonkeyServiceImp(MonkeyRepository()) as ShowMonkeyService }
    single { UpdateMonkeyServiceImp(MonkeyRepository()) as UpdateMonkeyService }
    single { DeleteMonkeyServiceImp(MonkeyRepository()) as DeleteMonkeyService }
}

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    startKoin(listOf(monkeysModule))

    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }

    dbConnect()

    val getMonkeysService by inject<GetMonkeysService>()
    val createMonkeyService by inject<CreateMonkeyService>()
    val showMonkeyService by inject<ShowMonkeyService>()
    val updateMonkeyService by inject<UpdateMonkeyService>()
    val deleteMonkeyService by inject<DeleteMonkeyService>()

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
            call.respond(FreeMarkerContent("monkeys/new.ftl", mapOf("name" to "", "id" to -1, "url" to "/new")))
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

        get("/edit") {
            val id: Int = call.request.queryParameters["id"]!!.toInt()
            var monkey: Monkey? = null

            withContext(Dispatchers.IO) {
                transaction {
                    monkey = showMonkeyService.execute(id)
                }
                call.respond(FreeMarkerContent("monkeys/new.ftl",
                    mapOf("name" to monkey!!.name, "id" to monkey!!.id,"url" to "/update")))
            }
        }

        post("/update") {
            val params = call.receiveParameters()
            println(params)
            val id = params["id"]!!
            val name = params["name"]!!

            withContext(Dispatchers.IO) {
                transaction {
                    updateMonkeyService.execute(mapOf("id" to id, "name" to name))
                }

                call.respondRedirect("/")
            }
        }

        post("/delete") {
            val id = call.receiveParameters()["id"]!!.toInt()

            withContext(Dispatchers.IO) {
                transaction {
                    deleteMonkeyService.execute(id)
                }

                call.respondRedirect("/")
            }
        }
    }
}

data class IndexData(val items: List<Int>)

