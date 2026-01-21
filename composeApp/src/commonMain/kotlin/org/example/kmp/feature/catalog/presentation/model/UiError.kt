package org.example.kmp.feature.catalog.presentation.model

data class UiError(
    val message: String,
) {
    companion object {
        fun from(error: Throwable): UiError {
            val msg = error.message?.takeIf { it.isNotBlank() } ?: "Unexpected error"
            return UiError(message = msg)
        }
    }
}