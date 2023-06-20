package ge.baqar.gogia.malazani.ui

import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import ge.baqar.gogia.malazani.R
import ge.baqar.gogia.malazani.databinding.ActivityMenuBinding
import ge.baqar.gogia.malazani.job.SyncFilesAndDatabaseJob
import ge.baqar.gogia.malazani.media.MediaPlaybackService
import ge.baqar.gogia.malazani.media.MediaPlaybackServiceManager
import ge.baqar.gogia.malazani.media.MediaPlayerController
import ge.baqar.gogia.malazani.model.Artist
import ge.baqar.gogia.malazani.model.Song
import ge.baqar.gogia.malazani.model.events.RequestMediaControllerInstance
import ge.baqar.gogia.malazani.model.events.ServiceCreatedEvent
import ge.baqar.gogia.malazani.utility.permission.BgPermission
import ge.baqar.gogia.malazani.widget.MediaPlayerView.Companion.OPENED
import kotlinx.coroutines.InternalCoroutinesApi
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.core.component.KoinComponent
import org.koin.dsl.module
import java.io.File
import kotlin.time.ExperimentalTime


@InternalCoroutinesApi
@ExperimentalTime
class MenuActivity : AppCompatActivity(), KoinComponent,
    NavController.OnDestinationChangedListener {

    private val PERMISSION_REQUEST_CODE = 200
    var destinationChanged: ((String) -> Unit)? = null
    private var tempLastPlayedSong: Song? = null
    private var tempArtist: Artist? = null
    private var tempDataSource: MutableList<Song>? = null
    private var tempPosition: Int? = null

    private var runtimePermission: BgPermission? = null
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

    private lateinit var binding: ActivityMenuBinding
    private lateinit var navController: NavController
    private var mediaPlayerController: MediaPlayerController? = null
    var permissionResult: ActivityResultLauncher<String>? = null
    private val storageActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                //Android is 11(R) or above
                if (Environment.isExternalStorageManager()) {
                } else {
                }
            } else {
                //Android is below 11(R)
            }
        }
    var permissionAccuired: (() -> Unit)? = null

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

        instance = this
    }


    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
        SyncFilesAndDatabaseJob.triggerNow(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!MediaPlaybackServiceManager.isRunning) doUnbindService()
        navController.removeOnDestinationChangedListener(this)
        instance = null
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

//    fun requestPermission(callback: (() -> Unit)) {
//        runtimePermission = BgPermission.Builder()
//            .requestCode(125)
//            .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//            .callBack(object : OnGrantPermissions {
//                override fun get(grantedPermissions: List<String>) {
//                    if (grantedPermissions.any()) {
//                        callback.invoke()
//                    }
//                }
//            }, object : OnDenyPermissions {
//                override fun get(deniedPermissions: List<String>) {
//
//                }
//            }, object : OnFailure {
//                override fun fail(e: Exception) {
//                    e.printStackTrace()
//                }
//            })
//        runtimePermission?.request(this)
//    }

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
        runtimePermission?.onPermissionsResult(requestCode, permissions, grantResults)
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

    companion object {
        var instance: MenuActivity? = null
    }
}

@OptIn(InternalCoroutinesApi::class, ExperimentalTime::class)
val activityModule = module {
    single { MenuActivity.instance }
}
