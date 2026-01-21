package org.example.kmp.feature.catalog.domain.usecase

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertSame
import org.example.kmp.feature.catalog.domain.model.Product
import org.example.kmp.feature.catalog.domain.repository.CatalogRepository

class CatalogUseCasesTest {

    private val repository: CatalogRepository = mockk()

    @Test
    fun observeCatalog_delegatesToRepository() {
        val pageIndex = 2
        val pageSize = 10

        val expectedFlow: Flow<List<Product>> = emptyFlow()
        every { repository.observeCatalogPage(pageIndex, pageSize) } returns expectedFlow

        val useCase = ObserveCatalogUseCase(repository)

        val result = useCase(pageIndex = pageIndex, pageSize = pageSize)

        assertSame(expectedFlow, result)
    }

    @Test
    fun observeFavorites_delegatesToRepository() {
        val expectedFlow: Flow<List<Product>> = emptyFlow()
        every { repository.observeFavorites() } returns expectedFlow

        val useCase = ObserveFavoritesUseCase(repository)

        val result = useCase()

        assertSame(expectedFlow, result)
    }

    @Test
    fun observeProductDetails_delegatesToRepository() {
        val productId = 42

        val expectedFlow: Flow<Product?> = emptyFlow()
        every { repository.observeProductDetails(productId) } returns expectedFlow

        val useCase = ObserveProductDetailsUseCase(repository)

        val result = useCase(productId)

        assertSame(expectedFlow, result)
    }

    @Test
    fun syncCatalogPage_delegatesToRepository() = runTest {
        val pageIndex = 1
        val pageSize = 20

        coEvery { repository.syncCatalogPage(pageIndex, pageSize) } returns Unit

        val useCase = SyncCatalogPageUseCase(repository)

        useCase(pageIndex = pageIndex, pageSize = pageSize)

        coVerify(exactly = 1) { repository.syncCatalogPage(pageIndex, pageSize) }
    }

    @Test
    fun toggleFavorite_delegatesToRepository() = runTest {
        val productId = 100

        coEvery { repository.toggleFavorite(productId) } returns Unit

        val useCase = ToggleFavoriteUseCase(repository)

        useCase(productId)

        coVerify(exactly = 1) { repository.toggleFavorite(productId) }
    }
}