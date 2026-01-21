package org.example.kmp.feature.catalog.presentation.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import simplekmp.composeapp.generated.resources.Res
import simplekmp.composeapp.generated.resources.compose_multiplatform

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogListContent(
    component: CatalogListComponent,
    modifier: Modifier = Modifier,
) {
    val searchQueryState = remember { mutableStateOf("") }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Catalog") },
                actions = {
                    IconButton(onClick = component::onOpenFavorites) {
                        // placeholder icon (replace later)
                        Icon(
                            painter = painterResource(Res.drawable.compose_multiplatform),
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
                value = searchQueryState.value,
                onValueChange = { searchQueryState.value = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Search (local)") },
                singleLine = true,
            )

            Text("Catalog list will be here")

            Button(
                onClick = { component.onOpenDetails(productId = 1) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Open product #1 (temporary)")
            }

            Button(
                onClick = { /* later: load next page */ },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Load next page (temporary)")
            }
        }
    }
}