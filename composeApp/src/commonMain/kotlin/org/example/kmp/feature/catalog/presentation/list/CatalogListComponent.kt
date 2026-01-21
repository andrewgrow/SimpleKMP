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
        lifecycle.doOnDestroy { scope.cancel() }

        observeCatalog()
        scope.launch { syncPage(pageIndex = 0) }
    }

    override fun onOpenDetails(productId: Int) = openDetails(productId)

    override fun onOpenFavorites() = openFavorites()

    override fun onRefresh() {
        _state.update { it.copy(pageIndex = 0, isRefreshing = true, error = null) }
        scope.launch { syncPage(pageIndex = 0) }
    }

    override fun onLoadNextPage() {
        val current = _state.value
        if (current.isPageLoading) return

        val nextPage = current.pageIndex + 1

        _state.update { it.copy(pageIndex = nextPage, isPageLoading = true, error = null) }
        scope.launch { syncPage(pageIndex = nextPage) }
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

    private fun observeCatalog() {
        scope.launch {
            _state
                .map { it.pageIndex }
                .distinctUntilChanged()
                .flatMapLatest { pageIndex ->
                    val pageSize = _state.value.pageSize
                    val totalLimit = pageSize * (pageIndex + 1)
                    observeCatalogUseCase(pageIndex = 0, pageSize = totalLimit)
                }
                .collect { items ->
                    _state.update {
                        it.copy(
                            items = items,
                            isRefreshing = false,
                            isPageLoading = false,
                            error = null,
                        )
                    }
                }
        }
    }

    private suspend fun syncPage(pageIndex: Int) {
        runCatching {
            syncCatalogPageUseCase(pageIndex = pageIndex, pageSize = _state.value.pageSize)
        }.onFailure { error ->
            _state.update {
                it.copy(
                    isRefreshing = false,
                    isPageLoading = false,
                    error = UiError.from(error),
                )
            }
        }
    }
}