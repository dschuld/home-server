package net.davidschuld.homeserver

import mu.KLogger
import mu.KotlinLogging

class Pausengong : TaskRunner() {

    fun start() {

        KotlinLogging.logger("Pausengong").info { "Starting pausengong task" }


        val startDay = taskConfig.getDay("tasks.pausengong.start_day")
        val endDay = taskConfig.getDay("tasks.pausengong.end_day")
        val startHour = taskConfig.getInt("tasks.pausengong.start_time")
        val endHour = taskConfig.getInt("tasks.pausengong.end_time")

        scheduleHourly(startDay, endDay, startHour, endHour) {
            println("Running pausengong task")
            playMp3("/app/pausengong.mp3")
        }
    }
}