package com.example.native202411pub.server

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity(tableName = "git_hub_users")
@Serializable
data class GitHubUsers(
    @PrimaryKey
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
