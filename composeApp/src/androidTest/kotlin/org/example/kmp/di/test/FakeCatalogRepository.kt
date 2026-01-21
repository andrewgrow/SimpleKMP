package org.example.kmp.di.test

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import org.example.kmp.feature.catalog.domain.model.Product
import org.example.kmp.feature.catalog.domain.repository.CatalogRepository

class FakeCatalogRepository : CatalogRepository {

    private val products = MutableStateFlow<List<Product>>(emptyList())

    override fun observeCatalogPage(pageIndex: Int, pageSize: Int): Flow<List<Product>> {
        // Your component uses "totalLimit" approach,
        // so we just return first N items.
        return products.map { it.take(pageSize * (pageIndex + 1)) }
    }

    override fun observeSearchPage(query: String, pageIndex: Int, pageSize: Int): Flow<List<Product>> {
        // Not used in UI right now, but keep it safe.
        return products.map { list ->
            val q = query.trim().lowercase()
            list.filter {
                it.title.lowercase().contains(q) || it.description.lowercase().contains(q)
            }
        }
    }

    override fun observeProductDetails(productId: Int): Flow<Product?> {
        return products.map { list -> list.firstOrNull { it.id == productId } }
    }

    override fun observeFavorites(): Flow<List<Product>> {
        return products.map { list -> list.filter { it.isFavorite } }
    }

    override suspend fun syncCatalogPage(pageIndex: Int, pageSize: Int) {
        // Simulate "network sync": append next page
        val startId = products.value.size + 1
        val newItems = (0 until pageSize).map { index ->
            val id = startId + index
            Product(
                id = id,
                title = "Product $id",
                description = "Description for product $id",
                price = id.toDouble(),
                thumbnailUrl = "",
                images = emptyList(),
                isFavorite = false,
            )
        }

        products.update { it + newItems }
    }

    override suspend fun syncProductDetails(productId: Int) {
        // Not needed for UI tests
    }

    override suspend fun toggleFavorite(productId: Int) {
        products.update { list ->
            list.map { product ->
                if (product.id == productId) {
                    product.copy(isFavorite = !product.isFavorite)
                } else {
                    product
                }
            }
        }
    }
}