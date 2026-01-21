package org.example.kmp.feature.catalog.data.mapper

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.example.kmp.core.database.entity.ProductEntity
import org.example.kmp.feature.catalog.api.dto.ProductDto

class ProductMappersTest {

    private val mappers = ProductMappers()

    @Test
    fun dtoToEntity_mapsAllFields_and_setsTimestamps() {
        val nowMillis = 1_700_000_000_000L

        val dto = ProductDto(
            id = 123,
            title = "iPhone",
            description = "Best phone ever",
            price = 999.99,
            thumbnailUrl = "https://example.com/thumb.png",
            images = listOf(
                "https://example.com/1.png",
                "https://example.com/2.png",
            ),
        )

        val entity = mappers.dtoToEntity(
            dto = dto,
            nowMillis = nowMillis,
        )

        assertEquals(123, entity.id)
        assertEquals("iPhone", entity.title)
        assertEquals("Best phone ever", entity.description)
        assertEquals(999.99, entity.price)
        assertEquals("https://example.com/thumb.png", entity.thumbnailUrl)
        assertEquals(listOf("https://example.com/1.png", "https://example.com/2.png"), entity.images)

        assertEquals(nowMillis, entity.updatedAt)
        assertEquals(nowMillis, entity.lastSeenAt)
    }

    @Test
    fun entityToDomain_mapsFields_and_setsFavoriteTrue() {
        val entity = ProductEntity(
            id = 5,
            title = "MacBook",
            description = "Laptop",
            price = 1999.0,
            thumbnailUrl = "https://example.com/thumb.png",
            images = listOf("https://example.com/a.png"),
            updatedAt = 111L,
            lastSeenAt = 222L,
        )

        val domain = mappers.entityToDomain(
            entity = entity,
            isFavorite = true,
        )

        assertEquals(5, domain.id)
        assertEquals("MacBook", domain.title)
        assertEquals("Laptop", domain.description)
        assertEquals(1999.0, domain.price)
        assertEquals("https://example.com/thumb.png", domain.thumbnailUrl)
        assertEquals(listOf("https://example.com/a.png"), domain.images)
        assertTrue(domain.isFavorite)
    }

    @Test
    fun entityToDomain_mapsFields_and_setsFavoriteFalse() {
        val entity = ProductEntity(
            id = 7,
            title = "AirPods",
            description = "Headphones",
            price = 249.5,
            thumbnailUrl = "",
            images = emptyList(),
            updatedAt = 0L,
            lastSeenAt = 0L,
        )

        val domain = mappers.entityToDomain(
            entity = entity,
            isFavorite = false,
        )

        assertEquals(7, domain.id)
        assertEquals("AirPods", domain.title)
        assertEquals("Headphones", domain.description)
        assertEquals(249.5, domain.price)
        assertEquals("", domain.thumbnailUrl)
        assertEquals(emptyList(), domain.images)
        assertFalse(domain.isFavorite)
    }

    @Test
    fun entitiesToDomain_mapsFavoriteIdsCorrectly() {
        val entities = listOf(
            ProductEntity(
                id = 1,
                title = "A",
                description = "Desc A",
                price = 1.0,
                thumbnailUrl = "t1",
                images = emptyList(),
                updatedAt = 0L,
                lastSeenAt = 0L,
            ),
            ProductEntity(
                id = 2,
                title = "B",
                description = "Desc B",
                price = 2.0,
                thumbnailUrl = "t2",
                images = emptyList(),
                updatedAt = 0L,
                lastSeenAt = 0L,
            ),
        )

        val favoriteIds = setOf(2)

        val result = mappers.entitiesToDomain(
            entities = entities,
            favoriteIds = favoriteIds,
        )

        assertEquals(2, result.size)
        assertFalse(result.first { it.id == 1 }.isFavorite)
        assertTrue(result.first { it.id == 2 }.isFavorite)
    }

    @Test
    fun dtoToEntity_handlesEmptyImages() {
        val nowMillis = 1_700_000_000_000L

        val dto = ProductDto(
            id = 1,
            title = "Product",
            description = "Desc",
            price = 1.0,
            thumbnailUrl = "https://example.com/thumb.png",
            images = emptyList(),
        )

        val entity = mappers.dtoToEntity(
            dto = dto,
            nowMillis = nowMillis,
        )

        assertEquals(emptyList(), entity.images)
    }
}