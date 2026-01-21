package org.example.kmp

import android.app.Application
import org.example.kmp.di.test.testCatalogModule
import org.example.kmp.di.test.testDatabaseModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class TestAppApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@TestAppApplication)
            modules(
                testDatabaseModule,
                testCatalogModule,
            )
        }
    }
}