package net.davidschuld.homeserver.lights

import io.ktor.application.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    routing {
        countdownRouting()
        ShoppingListUpdate().start()
        Pausengong().start()
    }
}