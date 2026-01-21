package org.example.kmp.feature.catalog.presentation.state

import org.example.kmp.feature.catalog.domain.model.Product
import org.example.kmp.feature.catalog.presentation.model.UiError

data class CatalogListState(
    val items: List<Product> = emptyList(),

    val pageIndex: Int = 0,
    val pageSize: Int = DEFAULT_PAGE_SIZE,
    val canLoadMore: Boolean = true,

    val isRefreshing: Boolean = false,
    val isPageLoading: Boolean = false,

    val error: UiError? = null,
) {
    companion object {
        const val DEFAULT_PAGE_SIZE = 20
    }
}