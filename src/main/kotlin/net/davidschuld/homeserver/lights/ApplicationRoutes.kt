package net.davidschuld.homeserver.lights


import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*

fun Application.countdownRouting() {
    val countdown = Countdown()
    val redAlert = RedAlert()

    routing {
        route("/lights/countdown") {
            post("/start") {
                countdown.start()
                call.respond(HttpStatusCode.OK)
            }
            post("/stop") {
                countdown.stop()
                call.respond(HttpStatusCode.OK)
            }
        }
        route("red_alert") {
            post("/start") {
                redAlert.start()
                call.respond(HttpStatusCode.OK)
            }
            post("/stop") {
                redAlert.stop()
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}
