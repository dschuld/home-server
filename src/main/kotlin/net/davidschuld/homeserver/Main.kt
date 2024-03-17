package net.davidschuld.homeserver

import com.typesafe.config.Config
import io.ktor.application.*
import io.ktor.routing.*
import io.ktor.server.netty.*
import java.io.File
import java.util.*
import com.typesafe.config.ConfigFactory


fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    routing {
        countdownRouting()
    }
    ShoppingListUpdate().start()
    Pausengong().start()
}



