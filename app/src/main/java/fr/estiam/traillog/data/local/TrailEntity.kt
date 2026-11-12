
package fr.estiam.traillog.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import fr.estiam.traillog.model.Difficulty
import fr.estiam.traillog.model.Trail

@Entity(tableName = "trails")
data class TrailEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val region: String,
    val distanceKm: Double,
    val elevationGainM: Int,
    val difficulty: String,
    val notes: String,
    val isFavorite: Boolean,
    val updatedAt: Long
) {
    fun toTrail(): Trail = Trail(
        id = id,
        name = name,
        region = region,
        distanceKm = distanceKm,
        elevationGainM = elevationGainM,
        difficulty = Difficulty.valueOf(difficulty),
        notes = notes,
        isFavorite = isFavorite,
        updatedAt = updatedAt
    )

    companion object {
        fun fromTrail(trail: Trail): TrailEntity = TrailEntity(
            id = trail.id,
            name = trail.name,
            region = trail.region,
            distanceKm = trail.distanceKm,
            elevationGainM = trail.elevationGainM,
            difficulty = trail.difficulty.name,
            notes = trail.notes,
            isFavorite = trail.isFavorite,
            updatedAt = trail.updatedAt
        )
    }
}
