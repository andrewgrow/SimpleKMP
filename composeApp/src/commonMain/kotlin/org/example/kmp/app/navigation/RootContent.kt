package org.example.kmp.app.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.stack.Children
import org.example.kmp.feature.catalog.presentation.details.ProductDetailsComponent
import org.example.kmp.feature.catalog.presentation.favorites.FavoritesComponent
import org.example.kmp.feature.catalog.presentation.list.CatalogListComponent

@Composable
fun RootContent(
    component: RootComponent,
    modifier: Modifier = Modifier,
) {
    BackHandler {
        component.onBackClicked()
    }

    Children(
        stack = component.childStack,
        modifier = modifier,
    ) { child ->
        when (val instance = child.instance) {
            is RootComponent.Child.Catalog -> CatalogPlaceholder(instance.component)
            is RootComponent.Child.Details -> DetailsPlaceholder(instance.component)
            is RootComponent.Child.Favorites -> FavoritesPlaceholder(instance.component)
        }
    }
}

@Composable
private fun CatalogPlaceholder(component: CatalogListComponent) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Catalog screen (placeholder)")

        Button(onClick = { component.onOpenDetails(productId = 1) }) {
            Text("Open product #1")
        }

        Button(onClick = component::onOpenFavorites) {
            Text("Open favorites")
        }
    }
}

@Composable
private fun DetailsPlaceholder(component: ProductDetailsComponent) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Details screen (placeholder)")
        Text("Product id: ${component.productId}")

        Button(onClick = component::onBack) {
            Text("Back")
        }
    }
}

@Composable
private fun FavoritesPlaceholder(component: FavoritesComponent) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Favorites screen (placeholder)")

        Button(onClick = { component.onOpenDetails(productId = 1) }) {
            Text("Open product #1")
        }

        Button(onClick = component::onBack) {
            Text("Back")
        }
    }
}