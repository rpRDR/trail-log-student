
package fr.estiam.traillog.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TrailDao {

    @Query("SELECT * FROM trails ORDER BY name COLLATE NOCASE ASC")
    fun pagingByName(): PagingSource<Int, TrailEntity>

    @Query("SELECT * FROM trails WHERE isFavorite = 1 ORDER BY name COLLATE NOCASE ASC")
    fun pagingFavoritesByName(): PagingSource<Int, TrailEntity>

    @Query("SELECT * FROM trails ORDER BY distanceKm DESC, name ASC")
    fun pagingByDistance(): PagingSource<Int, TrailEntity>

    @Query("SELECT * FROM trails WHERE isFavorite = 1 ORDER BY distanceKm DESC, name ASC")
    fun pagingFavoritesByDistance(): PagingSource<Int, TrailEntity>

    @Query("SELECT * FROM trails")
    fun observeTrails(): Flow<List<TrailEntity>>

    @Query("SELECT * FROM trails WHERE id = :id LIMIT 1")
    suspend fun getTrail(id: String): TrailEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(trail: TrailEntity)

    @Query("DELETE FROM trails WHERE id = :id")
    suspend fun delete(id: String)

    @Query("SELECT COUNT(*) FROM trails")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIfAbsent(trail: TrailEntity): Long

}
