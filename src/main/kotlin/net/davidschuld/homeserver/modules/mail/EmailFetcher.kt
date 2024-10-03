package net.davidschuld.homeserver.modules.mail

import com.google.api.services.gmail.model.Message
import com.google.api.services.gmail.model.MessagePart
import com.google.api.services.gmail.model.MessagePartBody
import kotlinx.coroutines.runBlocking
import net.davidschuld.homeserver.TaskRunner
import java.io.File
import java.io.FileOutputStream
import java.util.*

const val INVOICE_DIR = "INVOICE_DIR"

class EmailFetcher(private val gmailService: GmailService) : TaskRunner() {

    private var lastCheckedTimestamp: Date = Date(0)
    private val MAX_ATTACHMENT_SIZE = 200 * 1024 // 200 KB in bytes

    override fun start() {
        val timer = Timer()
        val task = object : TimerTask() {
            override fun run() {
                fetchRecentEmailsWithAttachments()
            }
        }
        timer.scheduleAtFixedRate(task, 0, 3600000)
    }

    fun fetchRecentEmailsWithAttachments() {
        val service = gmailService.getService()
        val user = "me"
        val query = "has:attachment newer_than:1h"

        val result = service.users().messages().list(user).setQ(query).execute()
        val messages = result.messages

        if (messages.isNullOrEmpty()) {
            println("No new messages found.")
            return
        }

        for (message in messages) {
            val fullMessage = service.users().messages().get(user, message.id).execute()
            processMessage(fullMessage)
        }
        lastCheckedTimestamp = Date()
    }

    private fun processMessage(message: Message) {
        println("Message ID: ${message.id}")
        println("Subject: ${getSubject(message)}")

        val attachments = getAttachments(message.payload)
        for (attachment in attachments.filter { it.filename.lowercase().endsWith("pdf") }) {
            val attachmentSize = (attachment.body["size"] ?: 0) as Integer
            println("Attachment: ${attachment.filename}, Size: $attachmentSize")

            if (attachmentSize <= MAX_ATTACHMENT_SIZE) {
                downloadAttachment(message.id, attachment)
            } else {
                println("Skipping download: Attachment size exceeds limit ($attachmentSize)")
            }
        }
        println("--------------------")
    }

    private fun getSubject(message: Message): String {
        return message.payload.headers.find { it.name == "Subject" }?.value ?: "No Subject"
    }

    private fun getAttachments(messagePart: MessagePart): List<MessagePart> {
        val attachments = mutableListOf<MessagePart>()
        if (messagePart.filename != null && messagePart.filename.isNotEmpty()) {
            attachments.add(messagePart)
        }
        if (messagePart.parts != null) {
            for (part in messagePart.parts) {
                attachments.addAll(getAttachments(part))
            }
        }
        return attachments
    }

    private fun downloadAttachment(messageId: String, attachment: MessagePart) {
        val service = gmailService.getService()
        val attachmentId = attachment.body.attachmentId
        val filename = attachment.filename

        try {
            val attachmentContent: MessagePartBody = service.users().messages().attachments()
                .get("me", messageId, attachmentId).execute()

            val fileData = Base64.getUrlDecoder().decode(attachmentContent.data)

            val directoryPath = System.getenv(INVOICE_DIR) ?: throw
            IllegalStateException("\$INVOICE_DIR is not set!")
            val file = File("$directoryPath/$filename")
            file.parentFile.mkdirs()
            FileOutputStream(file).use { it.write(fileData) }

            println("Downloaded attachment: $filename")
        } catch (e: Exception) {
            println("Error downloading attachment $filename: ${e.message}")
        }
    }
}



fun main() = runBlocking {
    val gmailService = GmailService()
    val emailFetcher = EmailFetcher(gmailService)

    emailFetcher.fetchRecentEmailsWithAttachments()

}