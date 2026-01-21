package org.example.kmp.feature.catalog.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.example.kmp.core.database.dao.FavoriteDao
import org.example.kmp.core.database.dao.ProductDao
import org.example.kmp.core.database.entity.FavoriteEntity
import org.example.kmp.feature.catalog.api.CatalogApi
import org.example.kmp.feature.catalog.data.mapper.ProductMappers
import org.example.kmp.feature.catalog.domain.model.Product
import org.example.kmp.feature.catalog.domain.repository.CatalogRepository

class CatalogRepositoryImpl(
    private val api: CatalogApi,
    private val productDao: ProductDao,
    private val favoriteDao: FavoriteDao,
    private val mappers: ProductMappers,
    private val nowMillis: () -> Long,
) : CatalogRepository {

    override fun observeCatalogPage(
        pageIndex: Int,
        pageSize: Int,
    ): Flow<List<Product>> {
        val offset = pageIndex * pageSize

        val productsFlow = productDao.observeCatalogPaged(limit = pageSize, offset = offset)
        val favoriteIdsFlow = favoriteDao.observeFavoriteIds()
            .map { it.toSet() }

        return combine(productsFlow, favoriteIdsFlow) { entities, favoriteIds ->
            mappers.entitiesToDomain(entities, favoriteIds)
        }
    }

    override fun observeSearchPage(
        query: String,
        pageIndex: Int,
        pageSize: Int,
    ): Flow<List<Product>> {
        val offset = pageIndex * pageSize
        val trimmedQuery = query.trim()

        val productsFlow = productDao.searchCatalogPaged(
            query = trimmedQuery,
            limit = pageSize,
            offset = offset,
        )

        val favoriteIdsFlow = favoriteDao.observeFavoriteIds()
            .map { it.toSet() }

        return combine(productsFlow, favoriteIdsFlow) { entities, favoriteIds ->
            mappers.entitiesToDomain(entities, favoriteIds)
        }
    }

    override fun observeProductDetails(productId: Int): Flow<Product?> {
        val productFlow = productDao.observeProduct(id = productId)
        val favoriteIdsFlow = favoriteDao.observeFavoriteIds()
            .map { it.toSet() }

        return combine(productFlow, favoriteIdsFlow) { entity, favoriteIds ->
            entity?.let {
                mappers.entityToDomain(
                    entity = it,
                    isFavorite = favoriteIds.contains(it.id),
                )
            }
        }
    }

    override fun observeFavorites(): Flow<List<Product>> {
        val favoritesFlow = productDao.observeFavoriteProducts()
        val favoriteIdsFlow = favoriteDao.observeFavoriteIds()
            .map { it.toSet() }

        return combine(favoritesFlow, favoriteIdsFlow) { entities, favoriteIds ->
            mappers.entitiesToDomain(entities, favoriteIds)
        }
    }

    override suspend fun syncCatalogPage(pageIndex: Int, pageSize: Int) {
        val skip = pageIndex * pageSize
        val now = nowMillis()

        val page = api.getProductsPage(
            limit = pageSize,
            skip = skip,
        )

        val entities = page.products.map { dto ->
            mappers.dtoToEntity(dto = dto, nowMillis = now)
        }

        productDao.upsertAll(entities)
    }

    override suspend fun syncProductDetails(productId: Int) {
        val now = nowMillis()

        val dto = api.getProductDetails(productId)
        val entity = mappers.dtoToEntity(dto = dto, nowMillis = now)

        productDao.upsert(entity)
    }

    override suspend fun toggleFavorite(productId: Int) {
        val isFavorite = favoriteDao.observeIsFavorite(productId).first()
        if (isFavorite) {
            favoriteDao.deleteByProductId(productId)
        } else {
            favoriteDao.insert(
                FavoriteEntity(
                    productId = productId,
                    createdAt = nowMillis(),
                )
            )
        }
    }
}