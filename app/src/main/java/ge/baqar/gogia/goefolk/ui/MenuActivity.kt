package ge.baqar.gogia.goefolk.ui

import android.Manifest
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import ge.baqar.gogia.goefolk.FolkApplication
import ge.baqar.gogia.goefolk.R
import ge.baqar.gogia.goefolk.databinding.ActivityMenuBinding
import ge.baqar.gogia.goefolk.databinding.FragmentAccountBinding
import ge.baqar.gogia.goefolk.job.SyncFilesAndDatabaseJob
import ge.baqar.gogia.goefolk.media.MediaPlaybackService
import ge.baqar.gogia.goefolk.media.MediaPlaybackServiceManager
import ge.baqar.gogia.goefolk.media.MediaPlayerController
import ge.baqar.gogia.goefolk.model.Artist
import ge.baqar.gogia.goefolk.model.Song
import ge.baqar.gogia.goefolk.storage.FolkAppPreferences
import ge.baqar.gogia.goefolk.ui.account.AccountActivity
import ge.baqar.gogia.goefolk.utility.TokenValidator
import ge.baqar.gogia.goefolk.utility.permission.BgPermission
import ge.baqar.gogia.goefolk.widget.MediaPlayerView.Companion.OPENED
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidApplication
import org.koin.core.component.KoinComponent
import org.koin.dsl.module
import kotlin.time.ExperimentalTime


@InternalCoroutinesApi
@ExperimentalTime
class MenuActivity : AppCompatActivity(), KoinComponent,
    NavController.OnDestinationChangedListener {

    var destinationChanged: ((String) -> Unit)? = null
    private var tempArtist: Artist? = null
    private var tempDataSource: MutableList<Song>? = null
    private var tempPosition: Int? = null

    private var mediaPlaybackService: MediaPlaybackService? = null
    private val serviceConnection: ServiceConnection by lazy {
        object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                (service as? MediaPlaybackService.MediaPlaybackServiceBinder)?.let {
                    mediaPlaybackService = it.service
                    mediaPlayerController = mediaPlaybackService?.mediaPlayerController
                }
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                mediaPlayerController?.stop()
                mediaPlayerController = null
                mediaPlaybackService = null
            }
        }
    }
    private var _playMediaPlaybackAction: ((MutableList<Song>, Int, Artist) -> Unit)? =
        { songs, position, ensemble ->
            mediaPlayerController?.binding = binding
            mediaPlayerController?.playList = songs
            mediaPlayerController?.artist = ensemble
            mediaPlayerController?.setInitialPosition(position)

            if (mediaPlaybackService != null)
                mediaPlaybackService?.handleMediaAction(MediaPlaybackService.PLAY_MEDIA)
            else {
                val intent = Intent(this, MediaPlaybackService::class.java).apply {
                    action = MediaPlaybackService.PLAY_MEDIA
                }
                bindService(intent, serviceConnection, BIND_AUTO_CREATE)
            }
        }

    private val notificationManager by lazy {
        getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }
    private lateinit var binding: ActivityMenuBinding
    private lateinit var navController: NavController
    private var mediaPlayerController: MediaPlayerController? = null
    private var bgPermission: BgPermission? = null
    private val folkAppPreferences: FolkAppPreferences by inject()

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

        doBindService()
        if (MediaPlaybackServiceManager.isRunning) {
            mediaPlayerController?.showPlayer()
        }

        if (!notificationManager.areNotificationsEnabled()) {
            binding.root.findViewById<AppCompatImageButton>(R.id.notificationOffImage)
                ?.visibility = View.VISIBLE

            binding.root.findViewById<AppCompatImageButton>(R.id.notificationOffImage)
                ?.setOnClickListener {
                    checkNotificationPermission()
                }
        }

        binding.showAccountBtn.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(this)

            val token = folkAppPreferences.getToken()
            token?.let {
                val account = TokenValidator.parseAccountFromJwt(token)

                val binding: FragmentAccountBinding = FragmentAccountBinding.inflate(layoutInflater)
                binding.account = account
                bottomSheetDialog.setContentView(binding.root)

                bottomSheetDialog.show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
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
            doBindService()
        }
    }

    private fun checkNotificationPermission() {
        if (!notificationManager.areNotificationsEnabled()) {
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
        bindService(intent, serviceConnection, BIND_AUTO_CREATE)
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

    fun logOut() {
        unbindService(serviceConnection)
        startActivity(Intent(this, AccountActivity::class.java))
        finish()
    }
}

@OptIn(InternalCoroutinesApi::class, ExperimentalTime::class)
val activityModule = module {
    single { (androidApplication() as FolkApplication).activeActivity }
}