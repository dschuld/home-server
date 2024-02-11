package net.davidschuld.homeserver.lights


import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit

class ShoppingListUpdate : CoroutineScope by CoroutineScope(Dispatchers.IO) {

    private val httpClient = HttpClient { }
    fun start() {
        val timer = Timer()
        val task = object : TimerTask() {
            override fun run() {
                launch {
                    val bodyContent = body("milk", "eggs", "schoki", "hummus")
                    val headers = mapOf(
                        "Content-Type" to "application/json",
                        "Notion-Version" to "2022-06-28",
                        "Authorization" to "Bearer $NOTION_API_KEY"
                    )
                    httpClient.patch<Unit>(NOTION_BLOCK_ENDPOINT) {
                        headers.forEach { (key, value) ->
                            header(key, value)
                        }
                        contentType(ContentType.Application.Json)
                        body = bodyContent
                    }

                    println("Shopping list updated")
                }
            }
        }

        // Set the schedule function
        val calendar = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
            set(Calendar.HOUR_OF_DAY, 20)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        // If the scheduled time has passed today, schedule task for next week
        if (calendar.time < Date()) {
            calendar.add(Calendar.DAY_OF_YEAR, 7)
        }

        // Schedule the task
        timer.schedule(task, calendar.time, TimeUnit.DAYS.toMillis(7))
    }

}


val NOTION_API_KEY = System.getenv("NOTION_API_KEY")
val SHOPPING_LIST_ID = System.getenv("SHOPPING_LIST_ID")


val NOTION_BLOCK_ENDPOINT = "https://api.notion.com/v1/blocks/$SHOPPING_LIST_ID/children"


fun body(vararg items: String) =
    """
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
        ]
    }
    """