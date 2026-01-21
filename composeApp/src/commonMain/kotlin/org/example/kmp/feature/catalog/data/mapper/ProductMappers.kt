package org.example.kmp.feature.catalog.data.mapper

import org.example.kmp.core.database.entity.ProductEntity
import org.example.kmp.feature.catalog.api.dto.ProductDto
import org.example.kmp.feature.catalog.domain.model.Product

class ProductMappers {

    fun dtoToEntity(
        dto: ProductDto,
        nowMillis: Long,
    ): ProductEntity {
        return ProductEntity(
            id = dto.id,
            title = dto.title,
            description = dto.description,
            price = dto.price,
            thumbnailUrl = dto.thumbnailUrl,
            images = dto.images,
            updatedAt = nowMillis,
            lastSeenAt = nowMillis,
        )
    }

    fun entityToDomain(
        entity: ProductEntity,
        isFavorite: Boolean,
    ): Product {
        return Product(
            id = entity.id,
            title = entity.title,
            description = entity.description,
            price = entity.price,
            thumbnailUrl = entity.thumbnailUrl,
            images = entity.images,
            isFavorite = isFavorite,
        )
    }

    fun entitiesToDomain(
        entities: List<ProductEntity>,
        favoriteIds: Set<Int>,
    ): List<Product> {
        return entities.map { entity ->
            entityToDomain(
                entity = entity,
                isFavorite = favoriteIds.contains(entity.id),
            )
        }
    }
}