package org.example.kmp

import android.app.Application
import org.example.kmp.di.androidNetworkModule
import org.example.kmp.di.catalogModule
import org.example.kmp.di.databaseModule
import org.example.kmp.di.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class AppApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@AppApplication)
            modules(
                androidNetworkModule,
                databaseModule,
                networkModule,
                catalogModule,
            )
        }
    }
}