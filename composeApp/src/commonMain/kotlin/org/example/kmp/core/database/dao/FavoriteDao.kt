package org.example.kmp.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.example.kmp.core.database.entity.FavoriteEntity

@Dao
interface FavoriteDao {

    @Query("SELECT productId FROM favorites")
    fun observeFavoriteIds(): Flow<List<Int>>

    @Query("SELECT * FROM favorites ORDER BY createdAt DESC")
    fun observeFavorites(): Flow<List<FavoriteEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE productId = :id)")
    fun observeIsFavorite(id: Int): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE productId = :productId")
    suspend fun deleteByProductId(productId: Int)
}