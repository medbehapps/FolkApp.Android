package ge.baqar.gogia.goefolk

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import ge.baqar.gogia.goefolk.http.networkModule
import ge.baqar.gogia.goefolk.media.mediaModule
import ge.baqar.gogia.goefolk.storage.storageModule
import ge.baqar.gogia.goefolk.ui.activityModule
import ge.baqar.gogia.goefolk.ui.ensembles.ensemblesModule
import ge.baqar.gogia.goefolk.ui.favourites.favouritesModule
import ge.baqar.gogia.goefolk.ui.search.searchModule
import ge.baqar.gogia.goefolk.ui.songs.songsModule
import ge.baqar.gogia.goefolk.utility.utilityModule
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