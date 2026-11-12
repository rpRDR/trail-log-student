
package fr.estiam.traillog

import android.app.Application
import androidx.room.Room
import fr.estiam.traillog.data.*
import fr.estiam.traillog.data.local.RoomTrailRepository
import fr.estiam.traillog.data.local.TrailDatabase
import fr.estiam.traillog.worker.TrailReminderWorker

class TrailLogApplication : Application() {

    private val database: TrailDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            TrailDatabase::class.java,
            "traillog.db"
        ).build()
    }

    val trails: TrailRepository by lazy {
        RoomTrailRepository(
            database.trailDao(),
            applicationContext
        )
    }


    val settings: PreferencesRepository by lazy {
        DataStorePreferencesRepository(applicationContext)
    }


    override fun onCreate() {
        super.onCreate()
        TrailReminderWorker.ensureChannel(this)
    }
}
