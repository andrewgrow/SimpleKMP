package org.example.kmp.feature.catalog.presentation.list

import kotlinx.coroutines.flow.StateFlow
import org.example.kmp.feature.catalog.presentation.state.CatalogListState
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.kmp.feature.catalog.domain.usecase.ObserveCatalogUseCase
import org.example.kmp.feature.catalog.domain.usecase.SyncCatalogPageUseCase
import org.example.kmp.feature.catalog.domain.usecase.ToggleFavoriteUseCase
import org.example.kmp.feature.catalog.presentation.model.UiError

interface CatalogListComponent {

    val state: StateFlow<CatalogListState>

    fun onOpenDetails(productId: Int)
    fun onOpenFavorites()

    fun onQueryChange(query: String)
    fun onRefresh()
    fun onLoadNextPage()
    fun onToggleFavorite(productId: Int)
}

class CatalogListComponentImpl(
    componentContext: ComponentContext,
    private val openDetails: (Int) -> Unit,
    private val openFavorites: () -> Unit,
    private val observeCatalogUseCase: ObserveCatalogUseCase,
    private val syncCatalogPageUseCase: SyncCatalogPageUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
) : CatalogListComponent, ComponentContext by componentContext {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val _state = MutableStateFlow(CatalogListState())
    override val state: StateFlow<CatalogListState> = _state

    init {
        lifecycle.doOnDestroy {
            scope.cancel()
        }

        // Observe DB data for "first N items" (pageIndex controls N)
        scope.launch {
            _state
                .map { it.pageIndex to it.pageSize }
                .distinctUntilChanged()
                .flatMapLatest { (pageIndex, pageSize) ->
                    val totalLimit = pageSize * (pageIndex + 1)
                    observeCatalogUseCase(pageIndex = 0, pageSize = totalLimit)
                }
                .collect { items ->
                    _state.update { it.copy(items = items, error = null) }
                }
        }

        // Initial sync
        scope.launch {
            syncCurrentPage(isInitial = true)
        }
    }

    override fun onOpenDetails(productId: Int) = openDetails(productId)

    override fun onOpenFavorites() = openFavorites()

    override fun onQueryChange(query: String) {
        _state.update { it.copy(query = query) }
        // Search use case can be wired later.
        // For now we keep query in state only.
    }

    override fun onRefresh() {
        _state.update { it.copy(pageIndex = 0, isRefreshing = true, error = null) }
        scope.launch {
            syncCurrentPage(isRefresh = true)
        }
    }

    override fun onLoadNextPage() {
        val current = _state.value
        if (current.isPageLoading) return

        _state.update { it.copy(pageIndex = it.pageIndex + 1, isPageLoading = true, error = null) }
        scope.launch {
            syncCurrentPage(isPaging = true)
        }
    }

    override fun onToggleFavorite(productId: Int) {
        scope.launch {
            runCatching {
                toggleFavoriteUseCase(productId)
            }.onFailure { error ->
                _state.update { it.copy(error = UiError.from(error)) }
            }
        }
    }

    private suspend fun syncCurrentPage(
        isInitial: Boolean = false,
        isRefresh: Boolean = false,
        isPaging: Boolean = false,
    ) {
        val pageIndex = _state.value.pageIndex
        val pageSize = _state.value.pageSize

        if (isInitial || isPaging) {
            _state.update { it.copy(isPageLoading = true, error = null) }
        }

        runCatching {
            syncCatalogPageUseCase(pageIndex = pageIndex, pageSize = pageSize)
        }.onFailure { error ->
            _state.update {
                it.copy(
                    error = UiError.from(error),
                    isRefreshing = false,
                    isPageLoading = false,
                )
            }
            return
        }

        _state.update { it.copy(isRefreshing = false, isPageLoading = false, error = null) }
    }
}