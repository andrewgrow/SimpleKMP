package org.example.kmp.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import org.example.kmp.core.database.entity.ProductEntity

@Dao
interface ProductDao {

    @Query("""
        SELECT * FROM products
        ORDER BY id ASC
        LIMIT :limit OFFSET :offset
    """)
    fun observeCatalogPaged(limit: Int, offset: Int): Flow<List<ProductEntity>>

    @Query("""
        SELECT * FROM products
        WHERE id = :id
        LIMIT 1
    """)
    fun observeProduct(id: Int): Flow<ProductEntity?>

    @Query("""
        SELECT * FROM products
        WHERE LOWER(title) LIKE '%' || LOWER(:query) || '%'
           OR LOWER(description) LIKE '%' || LOWER(:query) || '%'
        ORDER BY id ASC
        LIMIT :limit OFFSET :offset
    """)
    fun searchCatalogPaged(query: String, limit: Int, offset: Int): Flow<List<ProductEntity>>

    @Query("""
        SELECT p.* FROM products p
        INNER JOIN favorites f ON p.id = f.productId
        ORDER BY f.createdAt DESC
    """)
    fun observeFavoriteProducts(): Flow<List<ProductEntity>>

    @Upsert
    suspend fun upsertAll(products: List<ProductEntity>)

    @Upsert
    suspend fun upsert(product: ProductEntity)

    @Query("SELECT COUNT(*) FROM products")
    suspend fun getCount(): Int
}