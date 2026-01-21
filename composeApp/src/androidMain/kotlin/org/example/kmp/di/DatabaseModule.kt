package org.example.kmp.di

import androidx.room.Room
import org.example.kmp.core.database.AppDatabase
import org.example.kmp.core.database.dao.FavoriteDao
import org.example.kmp.core.database.dao.ProductDao
import org.koin.dsl.module
import org.koin.android.ext.koin.androidContext

val databaseModule = module {
    single<AppDatabase> {
        Room.databaseBuilder(
            context = androidContext(),
            klass = AppDatabase::class.java,
            name = "app_database.db",
        ).build()
    }

    single<ProductDao> {
        get<AppDatabase>().productDao()
    }

    single<FavoriteDao> {
        get<AppDatabase>().favoriteDao()
    }
}