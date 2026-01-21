package org.example.kmp.feature.catalog.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import org.example.kmp.feature.catalog.api.dto.ProductDto
import org.example.kmp.feature.catalog.api.dto.ProductsPageDto

interface CatalogApi {
    suspend fun getProductsPage(limit: Int, skip: Int): ProductsPageDto
    suspend fun getProductDetails(productId: Int): ProductDto
}

internal object CatalogApiRoutes {
    const val BASE_URL = "https://dummyjson.com"
    const val PRODUCTS = "$BASE_URL/products"
}

class CatalogApiImpl(
    private val httpClient: HttpClient,
) : CatalogApi {

    override suspend fun getProductsPage(limit: Int, skip: Int): ProductsPageDto =
        httpClient.get(CatalogApiRoutes.PRODUCTS) {
            parameter("limit", limit)
            parameter("skip", skip)
        }.body()

    override suspend fun getProductDetails(productId: Int): ProductDto =
        httpClient.get("${CatalogApiRoutes.PRODUCTS}/$productId").body()
}