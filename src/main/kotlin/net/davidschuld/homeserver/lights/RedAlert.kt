package net.davidschuld.homeserver.lights

import io.ktor.client.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class RedAlert : CoroutineScope by CoroutineScope(Dispatchers.IO) {
    private val httpClient = HttpClient { }
    private val serviceUrl = System.getenv("BRIDGE_URL")

    fun start() {
        println("Red Alert not implemented yet")
        // launch an external tool via command line

        // send API call to hue bridge to start red lights
    }

    fun stop() {
        // stop external music client

        //send API call to hue bridge to stop red lights
        // how go back to normal?
    }
}