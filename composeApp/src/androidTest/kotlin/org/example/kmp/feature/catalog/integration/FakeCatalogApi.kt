package org.example.kmp.feature.catalog.integration

import org.example.kmp.feature.catalog.api.CatalogApi
import org.example.kmp.feature.catalog.api.dto.ProductDto
import org.example.kmp.feature.catalog.api.dto.ProductsPageDto

class FakeCatalogApi(
    private val allProducts: List<ProductDto> = defaultProducts(),
) : CatalogApi {

    override suspend fun getProductsPage(limit: Int, skip: Int): ProductsPageDto {
        val safeLimit = limit.coerceAtLeast(0)
        val safeSkip = skip.coerceAtLeast(0)

        val pageItems = allProducts
            .drop(safeSkip)
            .take(safeLimit)

        return ProductsPageDto(
            products = pageItems,
            total = allProducts.size,
            skip = safeSkip,
            limit = safeLimit,
        )
    }

    override suspend fun getProductDetails(productId: Int): ProductDto {
        return allProducts.firstOrNull { it.id == productId }
            ?: error("FakeCatalogApi: Product not found: id=$productId")
    }

    companion object {
        private fun defaultProducts(): List<ProductDto> {
            return (1..50).map { id ->
                ProductDto(
                    id = id,
                    title = "Product $id",
                    description = "Description $id",
                    price = id.toDouble(),
                    thumbnailUrl = "https://example.com/thumb/$id.jpg",
                    images = listOf(
                        "https://example.com/img/$id/1.jpg",
                        "https://example.com/img/$id/2.jpg",
                    ),
                )
            }
        }
    }
}