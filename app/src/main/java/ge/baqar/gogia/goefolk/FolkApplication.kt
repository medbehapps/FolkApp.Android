package ge.baqar.gogia.goefolk

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import androidx.media3.common.util.UnstableApi
import ge.baqar.gogia.goefolk.http.networkModule
import ge.baqar.gogia.goefolk.media.mediaModule
import ge.baqar.gogia.goefolk.storage.storageModule
import ge.baqar.gogia.goefolk.ui.account.login.LoginActivity
import ge.baqar.gogia.goefolk.ui.account.login.loginModule
import ge.baqar.gogia.goefolk.ui.account.register.registerModule
import ge.baqar.gogia.goefolk.ui.media.AuthorizedActivity
import ge.baqar.gogia.goefolk.ui.media.artists.artistsModule
import ge.baqar.gogia.goefolk.ui.media.authorizedModule
import ge.baqar.gogia.goefolk.ui.media.dashboard.dashboardModule
import ge.baqar.gogia.goefolk.ui.media.favourites.favouritesModule
import ge.baqar.gogia.goefolk.ui.media.playlist.playlistModule
import ge.baqar.gogia.goefolk.ui.media.search.searchModule
import ge.baqar.gogia.goefolk.ui.media.songs.songsModule
import ge.baqar.gogia.goefolk.utility.utilityModule
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.logger.Level
import kotlin.time.ExperimentalTime

class FolkApplication : Application() {

    @OptIn(InternalCoroutinesApi::class, ExperimentalTime::class)
    var activeActivity: AuthorizedActivity? = null

    @SuppressLint("NewApi")
    @OptIn(ExperimentalTime::class, InternalCoroutinesApi::class)
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@FolkApplication)
            modules(
                listOf(
                    authorizedModule,
                    utilityModule,
                    mediaModule,
                    networkModule,
                    storageModule,
                    loginModule,
                    dashboardModule,
                    registerModule,
                    artistsModule,
                    songsModule,
                    searchModule,
                    favouritesModule,
                    playlistModule
                )
            )
        }

        registerActivityLifecycleCallbacks(object :
            ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                assignMenuActivity(activity)
            }

            override fun onActivityStarted(activity: Activity) {
                assignMenuActivity(activity)
            }

            @OptIn(InternalCoroutinesApi::class, ExperimentalTime::class)
            override fun onActivityResumed(activity: Activity) {
                assignMenuActivity(activity)
            }

            @OptIn(InternalCoroutinesApi::class, ExperimentalTime::class)
            override fun onActivityPaused(activity: Activity) {
                activeActivity = null
            }

            override fun onActivityStopped(activity: Activity) {
                activeActivity = null
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

            }

            override fun onActivityDestroyed(activity: Activity) {
                //activeActivity = null
            }
        })
    }

    @androidx.annotation.OptIn(UnstableApi::class)
    @OptIn(InternalCoroutinesApi::class, ExperimentalTime::class)
    private fun assignMenuActivity(activity: Activity) {
        if (activity is AuthorizedActivity) {
            activeActivity = activity
        }
    }

    fun logOut() {
        startActivity(Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        })
    }
}