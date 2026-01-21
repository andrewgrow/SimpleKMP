package org.example.kmp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.arkivanov.decompose.defaultComponentContext
import org.example.kmp.app.navigation.RootChildFactory
import org.example.kmp.app.navigation.RootComponent
import org.example.kmp.app.navigation.RootNavigation
import org.koin.android.ext.android.get

class MainActivity : ComponentActivity() {

    private lateinit var rootComponent: RootComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val childFactory: RootChildFactory = get()

        rootComponent = RootComponent(
            RootNavigation(
                componentContext = defaultComponentContext(),
                childFactory = childFactory,
            )
        )

        setContent {
            App(rootComponent)
        }
    }
}