package net.davidschuld.homeserver

class Pausengong : TaskRunner() {
    fun start() {


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