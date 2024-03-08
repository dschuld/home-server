package net.davidschuld.homeserver.lights


import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit


open class TaskRunner : CoroutineScope by CoroutineScope(Dispatchers.IO) {
    fun schedule(calendar: Calendar, block: suspend CoroutineScope.() -> Unit) {
        val timer = Timer()
        val task = object : TimerTask() {
            override fun run() {
                launch(block = block)
            }
        }

        // Schedule the task
        timer.schedule(task, calendar.time, TimeUnit.DAYS.toMillis(7))
    }

    fun scheduleAtFixedRate(calendar: Calendar, block: suspend CoroutineScope.() -> Unit) {
        val timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)

                // Check if the current day is a weekday and the current hour is between 9 am and 5 pm
                if (currentDay in Calendar.MONDAY..Calendar.FRIDAY && currentHour in 9..17) {
                    println("Running pausengong task")
                    launch(block = block)
                }
            }
        }, calendar.time, TimeUnit.HOURS.toMillis(1)) // Run the task every hour
    }
}

class ShoppingListUpdate : TaskRunner() {

    private val httpClient = HttpClient { }
    fun start() {

        val calendar = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY)
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 37)
            set(Calendar.SECOND, 0)
        }

        // If the scheduled time has passed today, schedule task for next week
        if (calendar.time < Date()) {
            calendar.add(Calendar.DAY_OF_YEAR, 7)
        }
        
        schedule(calendar) {

            println("Running shopping list update task")
            val headers = mapOf(
                "Content-Type" to "application/json",
                "Notion-Version" to "2022-06-28",
                "Authorization" to "Bearer $NOTION_API_KEY"
            )

            val response = httpClient.get<String>(NOTION_BLOCK_ENDPOINT) {
                headers.forEach { (key, value) ->
                    if (key != "Content-Type") {
                        header(key, value)
                    }
                }
            }
            val json = Json.parseToJsonElement(response) as JsonObject
            val results = json["results"]?.jsonArray

            results?.let {
                for (result in results) {
                    val block = result as JsonObject
                    val text = block.toString()
                    if ("GROCERIES" in text) {
                        val id = block["id"]?.jsonPrimitive?.content ?: continue
                        val bodyContent = body(id)

                        httpClient.patch<Unit>(NOTION_BLOCK_ENDPOINT) {
                            headers.forEach { (key, value) ->
                                header(key, value)
                            }
                            contentType(ContentType.Application.Json)
                            body = bodyContent
                        }

                        println("Shopping list updated")
                        break
                    }
                }
            }
        }
    }


    fun body(blockId: String): String {
        val filePath = "/data/shopping_list"
        val defaultFilePath = "/shopping_list_default"
        val fileContent = if (File(filePath).exists()) {
            File(filePath).readText()
        } else {
            this::class.java.getResourceAsStream(defaultFilePath).bufferedReader()
                .use { it.readText() }
        }
        val items = fileContent.split(",")
        return """
{
    "children": [
        {
            "object": "block",
            "type": "paragraph",
            "paragraph": {
                "rich_text": [
                    {
                        "type": "text",
                        "text": {
                            "content": "${items.joinToString("\\n")}",
                            "link": null
                        }
                    }
                ]
            }
        }
    ],
    "after": "$blockId"
}
"""
    }

}


val NOTION_API_KEY = System.getenv("NOTION_API_KEY")
val SHOPPING_LIST_ID = System.getenv("SHOPPING_LIST_ID")


val NOTION_BLOCK_ENDPOINT = "https://api.notion.com/v1/blocks/$SHOPPING_LIST_ID/children"
