package com.example.native202411pub.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "locations")
data class LocationEntity(
    @PrimaryKey(autoGenerate = true)
    val locationId: Int,
    val latitude: Double,
    val longitude: Double,
    val updatedAt: Long,
)
