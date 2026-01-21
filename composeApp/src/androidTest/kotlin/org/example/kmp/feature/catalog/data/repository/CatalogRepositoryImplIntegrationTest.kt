package org.example.kmp.feature.catalog.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.example.kmp.core.database.AppDatabase
import org.example.kmp.feature.catalog.data.mapper.ProductMappers
import org.example.kmp.feature.catalog.integration.FakeCatalogApi
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class CatalogRepositoryImplIntegrationTest {

    private lateinit var db: AppDatabase
    private lateinit var repository: CatalogRepositoryImpl

    private val fixedNow = 1_700_000_000_000L

    @Before
    fun setup() {
        val context: Context = ApplicationProvider.getApplicationContext()

        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        repository = CatalogRepositoryImpl(
            api = FakeCatalogApi(),
            productDao = db.productDao(),
            favoriteDao = db.favoriteDao(),
            mappers = ProductMappers(),
            nowMillis = { fixedNow },
        )
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun syncCatalogPage_writesToDb_and_observeCatalogPageEmitsItems() = runTest {
        val pageSize = 20

        repository.observeCatalogPage(pageIndex = 0, pageSize = pageSize).test {
            // Usually first emission = empty
            val first = awaitItem()
            assertTrue(first.isEmpty())

            repository.syncCatalogPage(pageIndex = 0, pageSize = pageSize)

            val second = awaitItem()
            assertEquals(pageSize, second.size)
            assertEquals("Product 1", second.first().title)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun toggleFavorite_updatesDetailsFlow_isFavoriteFlag() = runTest {
        repository.syncCatalogPage(pageIndex = 0, pageSize = 20)

        repository.observeProductDetails(productId = 1).test {
            val initial = awaitItem()
            assertNotNull(initial)
            assertFalse(initial.isFavorite)

            repository.toggleFavorite(productId = 1)

            val afterFav = awaitItem()
            assertNotNull(afterFav)
            assertTrue(afterFav.isFavorite)

            repository.toggleFavorite(productId = 1)

            val afterRemove = awaitItem()
            assertNotNull(afterRemove)
            assertFalse(afterRemove.isFavorite)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun observeFavorites_returnsOnlyFavoriteItems() = runTest {
        repository.syncCatalogPage(pageIndex = 0, pageSize = 20)

        repository.observeFavorites().test {
            // initial
            assertTrue(awaitItem().isEmpty())

            repository.toggleFavorite(1)

            val afterFirst = awaitItemUntil { items -> items.any { it.id == 1 } }
            assertEquals(listOf(1), afterFirst.map { it.id })

            repository.toggleFavorite(2)

            val afterSecond = awaitItemUntil { items -> items.map { it.id }.toSet() == setOf(1, 2) }
            assertEquals(setOf(1, 2), afterSecond.map { it.id }.toSet())

            repository.toggleFavorite(1) // remove

            val afterRemove = awaitItemUntil { items -> items.map { it.id } == listOf(2) }
            assertEquals(listOf(2), afterRemove.map { it.id })

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun observeSearchPage_filtersFromLocalDb() = runTest {
        repository.syncCatalogPage(pageIndex = 0, pageSize = 20)

        repository.observeSearchPage(
            query = "Product 1",
            pageIndex = 0,
            pageSize = 100,
        ).test {
            val result = awaitItem()
            assertTrue(result.isNotEmpty())

            assertTrue(
                result.all { product ->
                    product.title.contains("Product 1") || product.description.contains("Product 1")
                }
            )

            cancelAndIgnoreRemainingEvents()
        }
    }
}

private suspend fun <T> app.cash.turbine.ReceiveTurbine<T>.awaitItemUntil(
    maxEvents: Int = 10,
    predicate: (T) -> Boolean,
): T {
    repeat(maxEvents) {
        val item = awaitItem()
        if (predicate(item)) return item
    }
    throw AssertionError("Condition was not met after $maxEvents emissions")
}