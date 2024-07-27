package com.example.native202411pub.server

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object GitHubAPI {

    private fun getClient(): HttpClient {
        return HttpClient {
            install(Logging)
            install(ContentNegotiation) {
                json(
                    Json {
                        prettyPrint = true
                        isLenient = true
                        ignoreUnknownKeys = true
                    }
                )
            }
            install(DefaultRequest) {
                url {
                    protocol = URLProtocol.HTTPS
                    host = "api.github.com"
                }
            }
            expectSuccess = true
        }
    }

    suspend fun getUsers(login: String): GitHubUsers {
        getClient().use { client ->
            val response = client.get("users/$login")
            return response.body()
        }
    }

    suspend fun getRepos(url: String): List<GitHubRepos> {
        getClient().use { client ->
            val response = client.get(url)
            return response.body()
        }
    }
}
