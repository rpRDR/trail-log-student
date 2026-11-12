
package fr.estiam.traillog.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import fr.estiam.traillog.model.DistanceUnit
import fr.estiam.traillog.model.TrailSortOrder
import fr.estiam.traillog.model.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.trailSettingsDataStore by preferencesDataStore(
    name = "trail_settings"
)

class DataStorePreferencesRepository(
    private val context: Context
) : PreferencesRepository {

    private val unitKey =
        stringPreferencesKey("pref_distance_units")

    private val sortKey =
        stringPreferencesKey("pref_default_sort")

    override val settings: Flow<UserSettings> =
        context.trailSettingsDataStore.data.map { preferences ->

            val unit = when (preferences[unitKey]) {
                DistanceUnit.MI.name -> DistanceUnit.MI
                else -> DistanceUnit.KM
            }

            val sort = when (preferences[sortKey]) {
                TrailSortOrder.DISTANCE.name ->
                    TrailSortOrder.DISTANCE

                else -> TrailSortOrder.NAME
            }

            UserSettings(
                unit = unit,
                sortOrder = sort
            )
        }

    override suspend fun setUnit(unit: DistanceUnit) {
        context.trailSettingsDataStore.edit { preferences ->
            preferences[unitKey] = unit.name
        }
    }

    override suspend fun setSort(sort: TrailSortOrder) {
        context.trailSettingsDataStore.edit { preferences ->
            preferences[sortKey] = sort.name
        }
    }
}
