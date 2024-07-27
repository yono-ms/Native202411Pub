package com.example.native202411pub.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.native202411pub.server.GitHubRepos
import com.example.native202411pub.server.GitHubUsers
import kotlinx.coroutines.flow.Flow

@Dao
interface GitHubDao {
    @Insert
    suspend fun insertRepos(repos: GitHubRepos)

    @Delete
    suspend fun deleteRepos(repos: GitHubRepos)

    @Query("SELECT * from git_hub_repos ORDER BY name")
    suspend fun getAllRepos(): List<GitHubRepos>

    @Query("SELECT * from git_hub_repos ORDER BY name")
    fun getAllReposFlow(): Flow<List<GitHubRepos>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: GitHubUsers)

    @Delete
    suspend fun deleteUsers(users: GitHubUsers)

    @Query("SELECT * from git_hub_users ORDER BY login")
    fun getAllUsersFlow(): Flow<List<GitHubUsers>>

    @Query("SELECT * FROM git_hub_repos INNER JOIN git_hub_users ON git_hub_users.userId = git_hub_repos.userId WHERE git_hub_users.login LIKE :login")
    suspend fun getRepos(login: String): List<GitHubRepos>

    @Query("SELECT * FROM git_hub_repos INNER JOIN git_hub_users ON git_hub_users.userId = git_hub_repos.userId WHERE git_hub_users.login LIKE :login")
    fun getReposFlow(login: String): Flow<List<GitHubRepos>>
}
