package com.example.native202411pub.server

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity(tableName = "git_hub_repos")
@Serializable
data class GitHubRepos(
    @PrimaryKey
    @SerialName("id")
    val repoId: Int,
    @SerialName("name")
    val name: String,
    @SerialName("updated_at")
    val updatedAt: String,

    var userId: Int = 0
)
