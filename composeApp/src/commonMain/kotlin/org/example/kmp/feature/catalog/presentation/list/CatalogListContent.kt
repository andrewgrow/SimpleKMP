package org.example.kmp.feature.catalog.presentation.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogListContent(
    component: CatalogListComponent,
    modifier: Modifier = Modifier,
) {
    val state by component.state.collectAsState()

    var query by remember { mutableStateOf("") }

    val filteredItems = remember(state.items, query) {
        val q = query.trim()
        if (q.isBlank()) {
            state.items
        } else {
            state.items.filter { product ->
                product.title.contains(q, ignoreCase = true) ||
                        product.description.contains(q, ignoreCase = true)
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Catalog") },
                actions = {
                    IconButton(onClick = component::onOpenFavorites) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = "Favorites",
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Search (local)") },
                singleLine = true,
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(
                    items = filteredItems,
                    key = { it.id },
                ) { product ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { component.onOpenDetails(product.id) }
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = product.title,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                text = product.description,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text("Price: ${product.price}")
                        }

                        IconButton(onClick = { component.onToggleFavorite(product.id) }) {
                            Text(if (product.isFavorite) "♥" else "♡")
                        }
                    }
                }
            }

            if (query.isBlank()) {
                Button(
                    onClick = component::onLoadNextPage,
                    enabled = !state.isPageLoading && state.canLoadMore,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Load next page")
                }
            }
        }
    }
}