package org.example.kmp.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import org.example.kmp.core.database.converter.StringListConverter
import org.example.kmp.core.database.dao.FavoriteDao
import org.example.kmp.core.database.dao.ProductDao
import org.example.kmp.core.database.entity.FavoriteEntity
import org.example.kmp.core.database.entity.ProductEntity

@Database(
    entities = [
        ProductEntity::class,
        FavoriteEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(StringListConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao

    abstract fun favoriteDao(): FavoriteDao
}