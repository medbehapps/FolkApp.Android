package ge.baqar.gogia.goefolk.ui

import android.Manifest
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import ge.baqar.gogia.goefolk.FolkApplication
import ge.baqar.gogia.goefolk.R
import ge.baqar.gogia.goefolk.databinding.ActivityMenuBinding
import ge.baqar.gogia.goefolk.job.SyncFilesAndDatabaseJob
import ge.baqar.gogia.goefolk.media.MediaPlaybackService
import ge.baqar.gogia.goefolk.media.MediaPlaybackServiceManager
import ge.baqar.gogia.goefolk.media.MediaPlayerController
import ge.baqar.gogia.goefolk.model.Artist
import ge.baqar.gogia.goefolk.model.Song
import ge.baqar.gogia.goefolk.model.events.RequestMediaControllerInstance
import ge.baqar.gogia.goefolk.model.events.ServiceCreatedEvent
import ge.baqar.gogia.goefolk.utility.permission.BgPermission
import ge.baqar.gogia.goefolk.widget.MediaPlayerView.Companion.OPENED
import kotlinx.coroutines.InternalCoroutinesApi
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.android.ext.koin.androidApplication
import org.koin.core.component.KoinComponent
import org.koin.dsl.module
import kotlin.time.ExperimentalTime


@InternalCoroutinesApi
@ExperimentalTime
class MenuActivity : AppCompatActivity(), KoinComponent,
    NavController.OnDestinationChangedListener {

    var destinationChanged: ((String) -> Unit)? = null
    private var tempLastPlayedSong: Song? = null
    private var tempArtist: Artist? = null
    private var tempDataSource: MutableList<Song>? = null
    private var tempPosition: Int? = null

    private var _playbackRequest: Boolean = false
    private var _playMediaPlaybackAction: ((MutableList<Song>, Int, Artist) -> Unit)? =
        { songs, position, ensemble ->
            mediaPlayerController?.playList = songs
            mediaPlayerController?.artist = ensemble
            mediaPlayerController?.setInitialPosition(position)
            val intent = Intent(this, MediaPlaybackService::class.java).apply {
                action = MediaPlaybackService.PLAY_MEDIA
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
        }

    private val notificationManager by lazy {
        getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }
    private lateinit var binding: ActivityMenuBinding
    private lateinit var navController: NavController
    private var mediaPlayerController: MediaPlayerController? = null
    private var bgPermission: BgPermission? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        navController = findNavController(R.id.nav_host_fragment_activity_menu)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_ensembles,
                R.id.navigation_oldRecordings,
                R.id.navigation_search,
                R.id.navigation_favs
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener(this)
        binding.mediaPlayerView.setupWithBottomNavigation(binding.navView)
        binding.mediaPlayerView.setOnClickListener {
            val state = binding.mediaPlayerView.state
            if (state != OPENED) {
                binding.mediaPlayerView.maximize()
            }
        }

        if (MediaPlaybackServiceManager.isRunning) {
            doBindService()
            binding.mediaPlayerView.show()
        }

        if (!notificationManager.areNotificationsEnabled()) {
            binding.root.findViewById<AppCompatImageButton>(R.id.notificationOffImage)
                ?.visibility = View.VISIBLE

            binding.root.findViewById<AppCompatImageButton>(R.id.notificationOffImage)
                ?.setOnClickListener {
                    checkNotificationPermission()
                }
        }
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
        SyncFilesAndDatabaseJob.triggerNow(this)
    }

    override fun onResume() {
        super.onResume()
        if (!notificationManager.areNotificationsEnabled()) {
            binding.root.findViewById<AppCompatImageButton>(R.id.notificationOffImage)
                ?.visibility = View.VISIBLE
        } else {
            binding.root.findViewById<AppCompatImageButton>(R.id.notificationOffImage)
                ?.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!MediaPlaybackServiceManager.isRunning) doUnbindService()
        navController.removeOnDestinationChangedListener(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val state = binding.mediaPlayerView.state
        if (state == OPENED) {
            binding.mediaPlayerView.minimize()
        } else super.onBackPressed()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        bgPermission?.onPermissionsResult(requestCode, permissions, grantResults)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onMessageEvent(event: MediaPlayerController) {
        mediaPlayerController = event
        mediaPlayerController?.binding = binding

        if (tempLastPlayedSong != null) {
            mediaPlayerController?.setCurrentSong(tempLastPlayedSong!!)
            tempLastPlayedSong = null
        }
        if (_playbackRequest) {
            _playMediaPlaybackAction?.invoke(
                tempDataSource!!, tempPosition!!, tempArtist!!
            )
            tempDataSource = null
            tempPosition = null
            _playbackRequest = false
            return
        }
        if (mediaPlayerController?.isPlaying() == true) {
            mediaPlayerController?.updatePlayer()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun serviceCreated(event: ServiceCreatedEvent) {
        EventBus.getDefault().post(RequestMediaControllerInstance())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun playMediaPlayback(position: Int, songs: MutableList<Song>, artist: Artist) {
        (binding.navHostFragmentActivityMenuContainer.layoutParams as ConstraintLayout.LayoutParams).apply {
            bottomMargin = resources.getDimension(R.dimen.minimized_media_player_height).toInt()
        }
        if (mediaPlayerController != null) {
            _playMediaPlaybackAction?.invoke(songs, position, artist)
        } else {
            tempDataSource = songs
            tempPosition = position
            tempArtist = artist
            _playbackRequest = true
            doBindService()
        }
    }

    private fun checkNotificationPermission() {
        if (!notificationManager.areNotificationsEnabled()) {
//            val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
//                putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
//                putExtra(Settings.EXTRA_CHANNEL_ID,
//                    getString(R.string.app_name_notification_channel))
//            }
//
//            startActivity(intent)

            bgPermission = BgPermission.builder()
                ?.requestCode(203)
                ?.permission(Manifest.permission.POST_NOTIFICATIONS)
                ?.callBack({ granted ->

                }, { denied -> }, { failure -> })

            bgPermission?.request(this)
        }
    }

    private fun doBindService() {
        val intent = Intent(this, MediaPlaybackService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent);
        }

        if (mediaPlayerController == null) EventBus.getDefault()
            .postSticky(RequestMediaControllerInstance())
    }

    private fun doUnbindService() {
        val intent = Intent(this, MediaPlaybackService::class.java)
        stopService(intent)
    }

    override fun onDestinationChanged(
        controller: NavController, destination: NavDestination, arguments: Bundle?
    ) {
        destinationChanged?.invoke(destination.javaClass.name)
    }
}

@OptIn(InternalCoroutinesApi::class, ExperimentalTime::class)
val activityModule = module {
    single { (androidApplication() as FolkApplication).activeActivity }
}