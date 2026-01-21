package org.example.kmp.feature.catalog.presentation.list

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import org.example.kmp.feature.catalog.domain.model.Product
import org.example.kmp.feature.catalog.domain.usecase.ObserveCatalogUseCase
import org.example.kmp.feature.catalog.domain.usecase.SyncCatalogPageUseCase
import org.example.kmp.feature.catalog.domain.usecase.ToggleFavoriteUseCase

class CatalogListComponentImplTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher = UnconfinedTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun init_observesItemsFromObserveCatalogUseCase_and_triggersInitialSync() = runTest {
        val observeCatalogUseCase = mockk<ObserveCatalogUseCase>()
        val syncCatalogPageUseCase = mockk<SyncCatalogPageUseCase>()
        val toggleFavoriteUseCase = mockk<ToggleFavoriteUseCase>()

        val emittedProducts = listOf(
            Product(
                id = 1,
                title = "A",
                description = "Desc",
                price = 10.0,
                thumbnailUrl = "",
                images = emptyList(),
                isFavorite = false,
            )
        )

        every { observeCatalogUseCase.invoke(any(), any()) } returns flowOf(emittedProducts)
        coEvery { syncCatalogPageUseCase.invoke(any(), any()) } returns Unit

        val lifecycle = LifecycleRegistry()
        val componentContext = DefaultComponentContext(lifecycle = lifecycle)

        val component = CatalogListComponentImpl(
            componentContext = componentContext,
            openDetails = {},
            openFavorites = {},
            observeCatalogUseCase = observeCatalogUseCase,
            syncCatalogPageUseCase = syncCatalogPageUseCase,
            toggleFavoriteUseCase = toggleFavoriteUseCase,
        )

        component.state.test {
            val first = awaitItem()
            val withItems = if (first.items.isNotEmpty()) first else awaitItem()

            assertEquals(1, withItems.items.size)
            assertEquals(1, withItems.items.first().id)

            cancelAndIgnoreRemainingEvents()
        }

        // Initial sync should happen once
        coVerify(exactly = 1) { syncCatalogPageUseCase.invoke(any(), any()) }
        verify(atLeast = 1) { observeCatalogUseCase.invoke(any(), any()) }
    }

    @Test
    fun onLoadNextPage_triggersSyncForNextPage_and_updatesObserveLimit() = runTest {
        // Arrange
        val observeCatalogUseCase = mockk<ObserveCatalogUseCase>()
        val syncCatalogPageUseCase = mockk<SyncCatalogPageUseCase>()
        val toggleFavoriteUseCase = mockk<ToggleFavoriteUseCase>()

        // We'll simulate DB stream that can change
        val dbFlow = MutableStateFlow<List<Product>>(emptyList())

        // Observe always returns the same DB flow instance for simplicity
        every { observeCatalogUseCase.invoke(any(), any()) } returns dbFlow

        coEvery { syncCatalogPageUseCase.invoke(any(), any()) } returns Unit

        val lifecycle = LifecycleRegistry()
        val componentContext = DefaultComponentContext(lifecycle = lifecycle)

        val component = CatalogListComponentImpl(
            componentContext = componentContext,
            openDetails = {},
            openFavorites = {},
            observeCatalogUseCase = observeCatalogUseCase,
            syncCatalogPageUseCase = syncCatalogPageUseCase,
            toggleFavoriteUseCase = toggleFavoriteUseCase,
        )

        // Act: emulate initial DB emission
        dbFlow.value = listOf(Product(id = 1, title = "A", description = "", price = 1.0))

        // Trigger paging
        component.onLoadNextPage()

        // Emulate more products after sync
        dbFlow.value = listOf(
            Product(id = 1, title = "A", description = "", price = 1.0),
            Product(id = 2, title = "B", description = "", price = 2.0),
        )

        // Assert: sync should be called at least twice:
        // - initial sync (pageIndex = 0)
        // - paging sync (pageIndex = 1)
        coVerify(atLeast = 1) { syncCatalogPageUseCase.invoke(0, any()) }
        coVerify(atLeast = 1) { syncCatalogPageUseCase.invoke(1, any()) }

        // Observe should be invoked again when pageIndex changes (limit increases)
        verify(atLeast = 2) { observeCatalogUseCase.invoke(0, any()) }
    }

    @Test
    fun onToggleFavorite_delegatesToUseCase() = runTest {
        // Arrange
        val observeCatalogUseCase = mockk<ObserveCatalogUseCase>()
        val syncCatalogPageUseCase = mockk<SyncCatalogPageUseCase>()
        val toggleFavoriteUseCase = mockk<ToggleFavoriteUseCase>()

        every { observeCatalogUseCase.invoke(any(), any()) } returns flowOf(emptyList())
        coEvery { syncCatalogPageUseCase.invoke(any(), any()) } returns Unit
        coEvery { toggleFavoriteUseCase.invoke(any()) } returns Unit

        val lifecycle = LifecycleRegistry()
        val componentContext = DefaultComponentContext(lifecycle = lifecycle)

        val component = CatalogListComponentImpl(
            componentContext = componentContext,
            openDetails = {},
            openFavorites = {},
            observeCatalogUseCase = observeCatalogUseCase,
            syncCatalogPageUseCase = syncCatalogPageUseCase,
            toggleFavoriteUseCase = toggleFavoriteUseCase,
        )

        // Act
        component.onToggleFavorite(productId = 99)

        // Assert
        coVerify(exactly = 1) { toggleFavoriteUseCase.invoke(99) }
    }
}