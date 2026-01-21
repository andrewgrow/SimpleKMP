package org.example.kmp

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import org.example.kmp.app.navigation.RootComponent
import org.example.kmp.app.navigation.RootContent

@Composable
fun App(rootComponent: RootComponent) {
    MaterialTheme {
        RootContent(component = rootComponent)
    }
}