package net.davidschuld.homeserver.lights

import java.util.*

class Pausengong: TaskRunner() {
    fun start() {

        scheduleHourly(Calendar.MONDAY, Calendar.SUNDAY, 8, 17) {
            println("Running pausengong task")
            playMp3("/app/pausengong.mp3")
        }
    }
}