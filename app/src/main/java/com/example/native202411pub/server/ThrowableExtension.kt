package com.example.native202411pub.server

import com.example.native202411pub.logger
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.http.contentType
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

fun Throwable.alertMessage(): String {
    return when (this) {
        is ClientRequestException -> {
            val response = this.response
            logger.debug("ContentType = {}", response.contentType())
            val model: ErrorResponse = runBlocking {
                response.body()
            }
            model.message
        }

        else -> this.localizedMessage ?: "error"
    }
}

@Serializable
data class ErrorResponse(
    val message: String,
    @SerialName("documentation_url")
    val documentationUrl: String,
    val status: String
)
