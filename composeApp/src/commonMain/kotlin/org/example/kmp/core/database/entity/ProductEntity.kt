package org.example.kmp.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: Int,

    val title: String,
    val description: String,
    val price: Double,

    val thumbnailUrl: String,
    val images: List<String>,

    val updatedAt: Long,
    val lastSeenAt: Long,
)