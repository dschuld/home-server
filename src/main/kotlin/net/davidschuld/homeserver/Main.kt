package net.davidschuld.homeserver

import io.ktor.application.*
import io.ktor.routing.*
import io.ktor.server.netty.*
import net.davidschuld.homeserver.modules.mail.EmailFetcher
import net.davidschuld.homeserver.modules.mail.GmailService
import net.davidschuld.homeserver.modules.misc.Pausengong
import net.davidschuld.homeserver.modules.misc.ShoppingListUpdate


fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    println("Starting homeserver...")
    routing {
        configureRoutes()
    }
    ShoppingListUpdate().start()
    Pausengong().start()
    EmailFetcher(GmailService()).start()
}



