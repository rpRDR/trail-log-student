
package fr.estiam.traillog.data.local

import androidx.paging.PagingSource
import androidx.paging.map
import fr.estiam.traillog.data.TrailRepository
import fr.estiam.traillog.model.Trail
import fr.estiam.traillog.model.TrailSortOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

import android.content.Context
import fr.estiam.traillog.model.Difficulty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray

class RoomTrailRepository(
    private val dao: TrailDao,
    private val context: Context
) : TrailRepository {


    override fun pagingSource(
        sort: TrailSortOrder,
        favoritesOnly: Boolean
    ): PagingSource<Int, Trail> {

        val source: PagingSource<Int, TrailEntity> =
            when (sort) {
                TrailSortOrder.NAME ->
                    if (favoritesOnly) {
                        dao.pagingFavoritesByName()
                    } else {
                        dao.pagingByName()
                    }

                TrailSortOrder.DISTANCE ->
                    if (favoritesOnly) {
                        dao.pagingFavoritesByDistance()
                    } else {
                        dao.pagingByDistance()
                    }
            }

        return TrailPagingSource(source)
    }


    override fun observeTrails(): Flow<List<Trail>> =
        dao.observeTrails().map { entities ->
            entities.map { entity -> entity.toTrail() }
        }

    override suspend fun getTrail(id: String): Trail? =
        dao.getTrail(id)?.toTrail()

    override suspend fun save(trail: Trail) {
        require(trail.isValid()) {
            "Les données de la randonnée sont invalides"
        }

        dao.insert(TrailEntity.fromTrail(trail))
    }

    override suspend fun delete(id: String) {
        dao.delete(id)
    }


    override suspend fun seedIfNeeded() = withContext(Dispatchers.IO) {
        val preferences = context.getSharedPreferences(
            "traillog_seed",
            Context.MODE_PRIVATE
        )

        if (preferences.getBoolean("seed_completed", false)) {
            return@withContext
        }

        val json = context.assets.open("trails.json")
            .bufferedReader()
            .use { it.readText() }

        val trails = JSONArray(json)

        for (i in 0 until trails.length()) {
            val item = trails.getJSONObject(i)

            val difficulty = runCatching {
                Difficulty.valueOf(item.getString("difficulty"))
            }.getOrNull() ?: continue

            val trail = Trail(
                id = item.getString("id"),
                name = item.getString("name"),
                region = item.getString("region"),
                distanceKm = item.getDouble("distanceKm"),
                elevationGainM = item.getInt("elevationGainM"),
                difficulty = difficulty,
                notes = item.optString("notes", ""),
                isFavorite = item.optBoolean("isFavorite", false)
            )

            if (trail.isValid()) {
                dao.insertIfAbsent(TrailEntity.fromTrail(trail))
            }
        }


        check(
            preferences.edit()
                .putBoolean("seed_completed", true)
                .commit()
        ) {
            "Impossible d'enregistrer le marqueur d'import"
        }

    }

}
