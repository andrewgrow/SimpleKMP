package org.example.kmp.feature.catalog.presentation.details

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
import org.example.kmp.feature.catalog.domain.usecase.ObserveProductDetailsUseCase
import org.example.kmp.feature.catalog.domain.usecase.ToggleFavoriteUseCase
import org.example.kmp.feature.catalog.presentation.model.UiError
import org.example.kmp.feature.catalog.presentation.state.ProductDetailsState

interface ProductDetailsComponent {

    val productId: Int
    val state: StateFlow<ProductDetailsState>

    fun onBack()
    fun onToggleFavorite()
}

class ProductDetailsComponentImpl(
    componentContext: ComponentContext,
    override val productId: Int,
    private val back: () -> Unit,
    private val observeProductDetailsUseCase: ObserveProductDetailsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
) : ProductDetailsComponent, ComponentContext by componentContext {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val _state = MutableStateFlow(ProductDetailsState())
    override val state: StateFlow<ProductDetailsState> = _state

    init {
        lifecycle.doOnDestroy { scope.cancel() }

        scope.launch {
            observeProductDetailsUseCase(productId)
                .collect { product ->
                    _state.update { it.copy(product = product, error = null) }
                }
        }
    }

    override fun onBack() = back()

    override fun onToggleFavorite() {
        scope.launch {
            runCatching {
                val currentProductId = _state.value.product?.id ?: productId
                toggleFavoriteUseCase(currentProductId)
            }.onFailure { error ->
                _state.update { it.copy(error = UiError.from(error)) }
            }
        }
    }
}