package org.example.kmp.feature.catalog.presentation.favorites

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.kmp.feature.catalog.domain.usecase.ObserveFavoritesUseCase
import org.example.kmp.feature.catalog.domain.usecase.ToggleFavoriteUseCase
import org.example.kmp.feature.catalog.presentation.model.UiError
import org.example.kmp.feature.catalog.presentation.state.FavoritesState

interface FavoritesComponent {

    val state: StateFlow<FavoritesState>

    fun onOpenDetails(productId: Int)
    fun onBack()

    fun onToggleFavorite(productId: Int)
}

class FavoritesComponentImpl(
    componentContext: ComponentContext,
    private val openDetails: (Int) -> Unit,
    private val back: () -> Unit,
    private val observeFavoritesUseCase: ObserveFavoritesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
) : FavoritesComponent, ComponentContext by componentContext {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val _state = MutableStateFlow(FavoritesState())
    override val state: StateFlow<FavoritesState> = _state

    init {
        lifecycle.doOnDestroy { scope.cancel() }

        scope.launch {
            observeFavoritesUseCase()
                .collect { products ->
                    _state.update { it.copy(items = products, error = null) }
                }
        }
    }

    override fun onOpenDetails(productId: Int) = openDetails(productId)

    override fun onBack() = back()

    override fun onToggleFavorite(productId: Int) {
        scope.launch {
            runCatching {
                toggleFavoriteUseCase(productId)
            }.onFailure { error ->
                _state.update { it.copy(error = UiError.from(error)) }
            }
        }
    }
}