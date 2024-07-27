package com.example.native202411pub.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.native202411pub.server.GitHubRepos
import com.example.native202411pub.server.GitHubUsers

@Database(
    entities = [GitHubUsers::class, GitHubRepos::class],
    version = 1,
    exportSchema = false
)
abstract class MyDatabase : RoomDatabase() {
    abstract fun gitHubDao(): GitHubDao

    companion object {
        @Volatile
        private var Instance: MyDatabase? = null

        fun getDatabase(context: Context): MyDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, MyDatabase::class.java, "my_database")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
