package ge.baqar.gogia.gefolk.ui.media

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import ge.baqar.gogia.gefolk.FolkApplication
import ge.baqar.gogia.gefolk.R
import ge.baqar.gogia.gefolk.databinding.ActivityMenuBinding
import ge.baqar.gogia.gefolk.databinding.FragmentAccountBinding
import ge.baqar.gogia.gefolk.media.FolkMediaService
import ge.baqar.gogia.gefolk.media.FolkPlayerController
import ge.baqar.gogia.gefolk.model.Song
import ge.baqar.gogia.gefolk.model.SucceedResult
import ge.baqar.gogia.gefolk.storage.FolkAppPreferences
import ge.baqar.gogia.gefolk.ui.media.songs.SongsViewModel
import ge.baqar.gogia.gefolk.utility.TokenValidator
import ge.baqar.gogia.gefolk.utility.permission.BgPermission
import ge.baqar.gogia.gefolk.view.MediaPlayerView.Companion.OPENED
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent
import kotlin.time.ExperimentalTime

@UnstableApi
@InternalCoroutinesApi
@ExperimentalTime
class AuthorizedActivity : AppCompatActivity(), KoinComponent,
    NavController.OnDestinationChangedListener {

    var destinationChanged: ((String) -> Unit)? = null
    var songMarkedAsFav: ((String, Boolean) -> Unit)? = null

    private val notificationManager by lazy {
        getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }
    lateinit var binding: ActivityMenuBinding
    private lateinit var navController: NavController
    private val folkPlayerController: FolkPlayerController by inject()
    private var bgPermission: BgPermission? = null
    private val folkAppPreferences: FolkAppPreferences by inject()
    private val songsViewModel: SongsViewModel by viewModel()
    private var notificationPermissionRequestCode = 203

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = ActivityMenuBinding.inflate(layoutInflater)
        folkPlayerController.authorizedActivity = this
        setContentView(binding.root)
        supportActionBar?.hide()
        navController = findNavController(R.id.nav_host_fragment_activity_menu)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_dashboard,
                R.id.navigation_ensembles,
                R.id.navigation_oldRecordings,
                R.id.navigation_playlist,
                R.id.navigation_favs
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.bottomNavigationView.setupWithNavController(navController)
        binding.bottomNavigationView.isSelected = false
        navController.addOnDestinationChangedListener(this)

        if (!notificationManager.areNotificationsEnabled()) {
            binding.root.findViewById<AppCompatImageButton>(R.id.notificationOffImage)
                ?.visibility = View.VISIBLE

            binding.root.findViewById<AppCompatImageButton>(R.id.notificationOffImage)
                ?.setOnClickListener {
                    checkNotificationPermission()
                }
        }

        initUi()
    }

    @SuppressLint("NewApi")
    private fun initUi() {
        binding.showAccountBtn.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(this)

            val token = folkAppPreferences.getToken()
            token?.let {
                val account = TokenValidator.parseAccountFromJwt(token)

                val binding: FragmentAccountBinding = FragmentAccountBinding.inflate(layoutInflater)
                binding.logoutButton.setOnClickListener {
                    stopFolkService()
                    folkAppPreferences.setToken(null)
                    logOut()
                }
                binding.account = account
                bottomSheetDialog.setContentView(binding.root)

                bottomSheetDialog.show()
            }
        }
        binding.mediaPlayerView.onSizeChange = {
            if (it) {
                binding.showAccountBtn.animate()
                    .alpha(0f)
                    .setDuration(500)
                    .start()
                binding.showAccountBtn.visibility = View.GONE
            } else {
                binding.showAccountBtn.animate()
                    .alpha(1f)
                    .setDuration(500)
                    .start()
                binding.showAccountBtn.visibility = View.VISIBLE
            }
        }
        binding.mediaPlayerView.setupWithBottomNavigation(binding.bottomNavigationView)
        binding.mediaPlayerView.setOnClickListener {
            val state = binding.mediaPlayerView.state
            if (state != OPENED) {
                binding.mediaPlayerView.maximize()
            }
        }
        folkPlayerController.songMarkedAsFav = { currentSong ->
            lifecycleScope.launch {
                songsViewModel.markAsFavourite(currentSong.id).collect {
                    val isFav: Boolean
                    if (it is SucceedResult) {
                        isFav = it.value
                        lifecycleScope.launch(Dispatchers.Main) {
                            binding.mediaPlayerView.setIsFav(isFav)
                            songMarkedAsFav?.invoke(currentSong.id, isFav)
                        }
                    }
                }
            }
        }
        folkPlayerController.onPlayListOpen = {
            navController.navigate(
                R.id.navigation_artists_details
            )
        }

        if (FolkMediaService.isRunning) {
            folkPlayerController.initialize(true)
            folkPlayerController.showControls(true)
        }
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
        folkPlayerController.authorizedActivity = null
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

    @SuppressLint("NewApi")
    fun playMediaPlayback(
        position: Int,
        songs: MutableList<Song>,
        playInitially: Boolean = true
    ) {
        (binding.navHostFragmentActivityMenuContainer.layoutParams as ConstraintLayout.LayoutParams).apply {
            bottomMargin = resources.getDimension(R.dimen.minimized_media_player_height).toInt()
        }

        folkPlayerController.initialize()
        folkPlayerController.position = position
        folkPlayerController.setPlaylist(songs)
        folkPlayerController.play()
        folkPlayerController.showControls()

        val serviceAction =
            if (!playInitially) FolkMediaService.INITIALIZE else FolkMediaService.PLAY_MEDIA
        startService(serviceAction)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkNotificationPermission() {
        if (!notificationManager.areNotificationsEnabled()) {
            bgPermission = BgPermission.builder()
                ?.requestCode(notificationPermissionRequestCode)
                ?.permission(Manifest.permission.POST_NOTIFICATIONS)
                ?.callBack({

                }, { }, { })

            bgPermission?.request(this)
        }
    }

    @SuppressLint("NewApi")
    private fun startService(action: String) {
        if (!FolkMediaService.isRunning) {
            Intent(this, FolkMediaService::class.java).let {
                it.action = action
                startForegroundService(it)
                FolkMediaService.isRunning = true
            }
        }
    }

    @SuppressLint("NewApi")
    fun stopFolkService() {
        Intent(this, FolkMediaService::class.java).let {
            stopService(it)
            FolkMediaService.isRunning = false
        }
    }

    override fun onDestinationChanged(
        controller: NavController, destination: NavDestination, arguments: Bundle?
    ) {
        destinationChanged?.invoke(destination.javaClass.name)
    }

    private fun logOut() {
        (application as FolkApplication).logOut()
        finish()
    }
}
