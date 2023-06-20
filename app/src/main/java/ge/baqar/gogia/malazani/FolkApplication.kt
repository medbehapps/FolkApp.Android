package ge.baqar.gogia.malazani

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import ge.baqar.gogia.malazani.http.networkModule
import ge.baqar.gogia.malazani.media.mediaModule
import ge.baqar.gogia.malazani.storage.storageModule
import ge.baqar.gogia.malazani.ui.activityModule
import ge.baqar.gogia.malazani.ui.ensembles.ensemblesModule
import ge.baqar.gogia.malazani.ui.favourites.favouritesModule
import ge.baqar.gogia.malazani.ui.search.searchModule
import ge.baqar.gogia.malazani.ui.songs.songsModule
import ge.baqar.gogia.malazani.utility.utilityModule
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.logger.Level
import kotlin.time.ExperimentalTime

class FolkApplication : Application() {

    @OptIn(ExperimentalTime::class, InternalCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@FolkApplication)
            modules(
                listOf(
                    activityModule,
                    utilityModule,
                    mediaModule,
                    networkModule,
                    storageModule,
                    ensemblesModule,
                    songsModule,
                    searchModule,
                    favouritesModule
                )
            )
        }
    }
}