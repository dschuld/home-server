package net.davidschuld.homeserver

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit

abstract class TaskRunner : CoroutineScope by CoroutineScope(Dispatchers.IO) {

    abstract fun start()
    fun schedule(calendar: Calendar, block: suspend CoroutineScope.() -> Unit) {
        val timer = Timer()
        val task = object : TimerTask() {
            override fun run() {
                launch(block = block)
            }
        }

        timer.schedule(task, calendar.time, TimeUnit.DAYS.toMillis(7))
    }

    fun scheduleHourly(
        startDay: Int, endDay: Int, startHour: Int,
        endHour: Int, block: suspend CoroutineScope.() -> Unit
    ) {
        val timer = Timer()
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, startHour)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        // If the current time has passed the scheduled time for today, schedule task for the next hour
        if (calendar.time < Date()) {
            calendar.add(Calendar.HOUR_OF_DAY, 1)
        }
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)

                if (currentDay in startDay..endDay && currentHour in startHour..endHour
                ) {
                    launch(block = block)
                }
            }
        }, calendar.time, TimeUnit.HOURS.toMillis(1)) // Run the task every hour
    }
}



fun readConfFile(filePath: String): Config {
    val inputStream = TaskRunner::class.java.getResourceAsStream(filePath)
    return ConfigFactory.parseReader(inputStream.bufferedReader())
}

const val TASK_CONFIG_PATH = "/tasks.conf"
val config = readConfFile(TASK_CONFIG_PATH)

object taskConfig: Config by config{

    fun getDay(key: String): Int {
        return when (config.getString(key)) {
            "MONDAY" -> Calendar.MONDAY
            "TUESDAY" -> Calendar.TUESDAY
            "WEDNESDAY" -> Calendar.WEDNESDAY
            "THURSDAY" -> Calendar.THURSDAY
            "FRIDAY" -> Calendar.FRIDAY
            "SATURDAY" -> Calendar.SATURDAY
            "SUNDAY" -> Calendar.SUNDAY
            else -> throw IllegalArgumentException("Invalid day")
        }
    }

}