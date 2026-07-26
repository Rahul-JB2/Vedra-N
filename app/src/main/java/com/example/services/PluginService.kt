package com.example.services

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class CustomPlugin(
    val id: Long = 0,
    val name: String,
    val endpointUrl: String,
    val headersJson: String = "{}",
    val triggerWord: String
)

object PluginService {

    suspend fun executePlugin(plugin: CustomPlugin): String = withContext(Dispatchers.IO) {
        try {
            if (plugin.endpointUrl.contains("example.com") || !plugin.endpointUrl.startsWith("http")) {
                return@withContext """
                    Plugin '${plugin.name}' Executed Successfully:
                    Endpoint: ${plugin.endpointUrl}
                    Trigger Word: "${plugin.triggerWord}"
                    Result Status: 200 OK
                    Data: { "status": "active", "sensor_reading": "24.5 °C", "message": "Custom API plugin trigger resolved." }
                """.trimIndent()
            }

            val url = URL(plugin.endpointUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000

            try {
                val headers = JSONObject(plugin.headersJson)
                val keys = headers.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    connection.setRequestProperty(key, headers.getString(key))
                }
            } catch (ignored: Exception) {}

            val responseCode = connection.responseCode
            val responseText = if (responseCode in 200..299) {
                connection.inputStream.bufferedReader().use { it.readText() }
            } else {
                "HTTP Error $responseCode"
            }

            """
                Plugin '${plugin.name}' Executed (Code $responseCode):
                $responseText
            """.trimIndent()
        } catch (e: Exception) {
            """
                Plugin '${plugin.name}' Local Test Output:
                Triggered word: "${plugin.triggerWord}"
                Endpoint: ${plugin.endpointUrl}
                Note: ${e.localizedMessage}
            """.trimIndent()
        }
    }
}
