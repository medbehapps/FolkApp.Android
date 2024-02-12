package ge.baqar.gogia.gefolk.media

import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import ge.baqar.gogia.gefolk.media.notification.FolkMediaNotificationManager
import org.koin.java.KoinJavaComponent.inject


@RequiresApi(Build.VERSION_CODES.O)
class FolkMediaService: MediaSessionService() {
    private val mediaSession: MediaSession by inject(MediaSession::class.java)
    private val notificationManager: FolkMediaNotificationManager by inject(
        FolkMediaNotificationManager::class.java)

    @UnstableApi
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        notificationManager.startNotificationService(
            mediaSessionService = this,
            mediaSession = mediaSession
        )

        return super.onStartCommand(intent, flags, startId)
    }



    override fun onDestroy() {
        super.onDestroy()
        mediaSession.run {
            release()
            if (player.playbackState != Player.STATE_IDLE) {
                player.seekTo(0)
                player.playWhenReady = false
                player.stop()
            }
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession =
        mediaSession


    companion object {
        const val INITIALIZE = "INITIALIZE"
        const val PLAY_MEDIA = "PLAY_MEDIA"
        var isRunning: Boolean = false
    }
}