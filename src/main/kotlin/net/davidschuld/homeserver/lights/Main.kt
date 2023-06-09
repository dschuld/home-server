package net.davidschuld.homeserver.lights

import io.ktor.application.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 7077, module = Application::module).start(wait = true)
}

fun Application.module() {
    routing {
        countdownRouting()
    }
}