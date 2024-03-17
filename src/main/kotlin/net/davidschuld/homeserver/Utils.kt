package net.davidschuld.homeserver

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

suspend fun playMp3(fileName: String) {
    println("Playing $fileName")
    val processBuilder = ProcessBuilder("mpg123", fileName)
    processBuilder.directory(File(System.getProperty("user.dir")))

    try {
        val process = processBuilder.start()
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        val errorReader = BufferedReader(InputStreamReader(process.errorStream))
        coroutineScope {
            launch {
                reader.lines().forEach {
                    println(it)
                }
            }
            launch {
                errorReader.lines().forEach {
                    println(it)
                }
            }
        }
        val exitCode = process.waitFor()
        println("Exited with code $exitCode")
    } catch (e: Exception) {
        println("An error occurred while trying to play the file $fileName")
        e.printStackTrace()
    }
}