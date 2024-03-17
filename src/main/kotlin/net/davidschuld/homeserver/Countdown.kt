package net.davidschuld.homeserver


import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Countdown : CoroutineScope by CoroutineScope(Dispatchers.IO) {
    private val httpClient = HttpClient { }
    private var job: Job? = null

    fun start() {
        println("Starting countdown...")
        job?.cancel()
        job = launch {
            delay(10 * 60 * 1000)
            println("Sending LIGHTS OFF")
            httpClient.put<Unit>("$BRIDGE_URL/groups/0/action") {
                contentType(ContentType.Application.Json)
                body = """{ "on": false }"""
            }
        }
    }


    fun stop() {
        println("Stopping countdown")
        job?.cancel()
    }
}