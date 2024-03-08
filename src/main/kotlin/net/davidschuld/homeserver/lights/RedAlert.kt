package net.davidschuld.homeserver.lights

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

class RedAlert : CoroutineScope by CoroutineScope(Dispatchers.IO) {
    private val httpClient = HttpClient { }
    private val serviceUrl = System.getenv("BRIDGE_URL")

    fun start() {
        // launch an external tool via command line

        // send API call to hue bridge to start red lights
        println("Sending LIGHTS OFF to living room")
        launch {
            httpClient.put<Unit>("$serviceUrl/groups/1/action") {
                contentType(ContentType.Application.Json)
                body = """{ "on": false }"""
            }
            val audio = async { playMp3("/app/tng_red_alert1.mp3") }
            val lights = async { flashOnce() }
            audio.await() to lights.await()
            httpClient.put<Unit>("$serviceUrl/groups/1/action") {
                contentType(ContentType.Application.Json)
                body = """{ "on": false }"""
            }
            println("Done")
//            flashOnce()
//            flashOnce()
        }
    }

    private suspend fun flashOnce() {
        println("Flashing lights")
        //TODO why does this not work when it works in Postman with Alert Lightstrip
        httpClient.put<Unit>("$serviceUrl/lights/8/state") {
            contentType(ContentType.Application.Json)
            body = requestBody(true)
        }
//                httpClient.put<Unit>("$serviceUrl/lights/8/state") {
//                    contentType(ContentType.Application.Json)
//                    body = requestBody(false)
//                }
//            }
//            delay(1000)
//        }
    }

    fun stop() {
        // stop external music client

        //send API call to hue bridge to stop red lights
        // how go back to normal?
    }
}

fun requestBody(status: Boolean) =
    """
        {
        "on": $status,
        "hue": 0,
        "sat": 254,
        "bri": 254,
        "alert": "lselect"
    }
    """.trimIndent()