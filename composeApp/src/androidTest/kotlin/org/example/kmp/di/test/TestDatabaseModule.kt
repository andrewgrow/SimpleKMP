package org.example.kmp.di.test

import androidx.room.Room
import org.example.kmp.core.database.AppDatabase
import org.example.kmp.core.database.dao.FavoriteDao
import org.example.kmp.core.database.dao.ProductDao
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val testDatabaseModule = module {
    single<AppDatabase> {
        Room.inMemoryDatabaseBuilder(
            context = androidContext(),
            klass = AppDatabase::class.java,
        )
            .allowMainThreadQueries() // ok for instrumentation tests
            .build()
    }

    single<ProductDao> { get<AppDatabase>().productDao() }
    single<FavoriteDao> { get<AppDatabase>().favoriteDao() }
}