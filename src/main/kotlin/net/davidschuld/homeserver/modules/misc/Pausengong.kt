package net.davidschuld.homeserver.modules.misc

import mu.KotlinLogging
import net.davidschuld.homeserver.TaskRunner
import net.davidschuld.homeserver.playMp3
import net.davidschuld.homeserver.taskConfig

class Pausengong : TaskRunner() {

    override fun start() {

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