package org.example.kmp.app.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import org.example.kmp.feature.catalog.presentation.details.ProductDetailsComponent
import org.example.kmp.feature.catalog.presentation.details.ProductDetailsContent
import org.example.kmp.feature.catalog.presentation.favorites.FavoritesComponent
import org.example.kmp.feature.catalog.presentation.favorites.FavoritesContent
import org.example.kmp.feature.catalog.presentation.list.CatalogListComponent
import org.example.kmp.feature.catalog.presentation.list.CatalogListContent

@Composable
fun RootContent(
    component: RootComponent,
    modifier: Modifier = Modifier,
) {
    val stack by component.childStack.subscribeAsState()
    val canGoBack = stack.items.size > 1

    BackHandler(enabled = canGoBack) {
        component.onBackClicked()
    }

    Children(
        stack = component.childStack,
        modifier = modifier,
    ) { child ->
        when (val instance = child.instance) {
            is RootComponent.Child.Catalog -> CatalogListContent(instance.component)
            is RootComponent.Child.Details -> ProductDetailsContent(instance.component)
            is RootComponent.Child.Favorites -> FavoritesContent(instance.component)
        }
    }
}