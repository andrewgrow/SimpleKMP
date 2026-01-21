package org.example.kmp.feature.catalog.domain.usecase

import org.example.kmp.feature.catalog.domain.repository.CatalogRepository

class SyncCatalogPageUseCase(
    private val repository: CatalogRepository,
) {

    suspend operator fun invoke(
        pageIndex: Int,
        pageSize: Int,
    ) {
        require(pageIndex >= 0) { "pageIndex must be >= 0" }
        require(pageSize > 0) { "pageSize must be > 0" }

        repository.syncCatalogPage(
            pageIndex = pageIndex,
            pageSize = pageSize,
        )
    }
}