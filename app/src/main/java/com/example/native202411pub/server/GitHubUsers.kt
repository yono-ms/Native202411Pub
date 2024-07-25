package com.example.native202411pub.server

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitHubUsers(
    @SerialName("id")
    val userId: Int,
    @SerialName("login")
    val login: String,
    @SerialName("public_repos")
    val publicRepos: Int,
    @SerialName("repos_url")
    val reposUrl: String,
    @SerialName("updated_at")
    val updatedAt: String
)
