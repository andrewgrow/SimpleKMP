package org.example.kmp.feature.catalog.domain.repository

import kotlinx.coroutines.flow.Flow
import org.example.kmp.feature.catalog.domain.model.Product

interface CatalogRepository {

    fun observeCatalogPage(pageIndex: Int, pageSize: Int): Flow<List<Product>>

    // skip = pageIndex * pageSize
    fun observeSearchPage(
        query: String,
        pageIndex: Int,
        pageSize: Int
    ): Flow<List<Product>>

    fun observeProductDetails(productId: Int): Flow<Product?>

    fun observeFavorites(): Flow<List<Product>>

    suspend fun syncCatalogPage(pageIndex: Int, pageSize: Int)

    suspend fun syncProductDetails(productId: Int)

    suspend fun toggleFavorite(productId: Int)
}