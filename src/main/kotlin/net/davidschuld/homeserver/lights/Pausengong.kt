package net.davidschuld.homeserver.lights

import java.util.*

class Pausengong: TaskRunner() {
    fun start() {

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        // If the current time has passed the scheduled time for today, schedule task for the next hour
        if (calendar.time < Date()) {
            calendar.add(Calendar.HOUR_OF_DAY, 1)
        }

        scheduleAtFixedRate(calendar) {
            println("Running pausengong task")
            playMp3("/app/pausengong.mp3")
        }
    }
}