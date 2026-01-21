package org.example.kmp.core.database.converter

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json

class StringListConverter {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromList(value: List<String>): String = json.encodeToString(value)

    @TypeConverter
    fun toList(value: String): List<String> = runCatching {
        json.decodeFromString<List<String>>(value)
    }.getOrDefault(emptyList())
}