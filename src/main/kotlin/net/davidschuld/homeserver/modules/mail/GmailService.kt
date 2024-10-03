package net.davidschuld.homeserver.modules.mail
import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.gmail.Gmail
import com.google.api.services.gmail.GmailScopes
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader

const val AUTH_DIR = "AUTH_DIR"

class GmailService {
    private val APPLICATION_NAME = "Gmail Kotlin API"
    private val JSON_FACTORY: JsonFactory = GsonFactory.getDefaultInstance()
    private val SCOPES = listOf(GmailScopes.GMAIL_READONLY)

    private val authDirectory = System.getenv(AUTH_DIR) ?: throw
    IllegalStateException("\$AUTH_DIR is not set!")

    private fun getCredentials(httpTransport: NetHttpTransport): Credential {
        val file = FileInputStream("$authDirectory/credentials.json")
        val clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, InputStreamReader(file))

        val flow = GoogleAuthorizationCodeFlow.Builder(
            httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
            .setDataStoreFactory(FileDataStoreFactory(File(authDirectory)))
            .setAccessType("offline")
            .build()

        return flow.loadCredential("user")
    }

    fun getService(): Gmail {
        val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
        val credential = getCredentials(httpTransport)
        return Gmail.Builder(httpTransport, JSON_FACTORY, credential)
            .setApplicationName(APPLICATION_NAME)
            .build()
    }

    // This method should be run once, interactively, to obtain the initial refresh token
    fun authorizeUser() {
        val HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport()
        val inputStream = FileInputStream("$authDirectory/credentials.json")
        val clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, InputStreamReader(inputStream))

        val flow = GoogleAuthorizationCodeFlow.Builder(
            HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
            .setDataStoreFactory(FileDataStoreFactory(File(AUTH_DIR)))
            .setAccessType("offline")
            .build()

        val receiver = LocalServerReceiver.Builder().setPort(8888).build()
        val credential = AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
        println("Authorization successful. Refresh token obtained and stored.")
    }
}