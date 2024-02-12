package ge.baqar.gogia.goefolk.media

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.session.MediaSession
import ge.baqar.gogia.goefolk.media.notification.FolkMediaNotificationManager
import ge.baqar.gogia.goefolk.media.player.AudioPlayer
import ge.baqar.gogia.goefolk.ui.media.AuthorizedActivity
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.dsl.module
import kotlin.time.ExperimentalTime


@RequiresApi(Build.VERSION_CODES.O)
@ExperimentalTime
@UnstableApi
@InternalCoroutinesApi
val mediaModule = module {
    single { AudioPlayer(get(), get()) }
    single { FolkPlayerController(get(), get()) }
    single { provideService() }
    single { providePlayer(get()) }
    single { provideNotificationManager(get(), get()) }
    single { provideMediaSession(get(), get()) }
}


@RequiresApi(Build.VERSION_CODES.O)
fun provideService(): FolkMediaService =
    FolkMediaService()

@UnstableApi
fun providePlayer(
    context: Context
): ExoPlayer =
    ExoPlayer.Builder(context)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
                .setUsage(C.USAGE_MEDIA)
                .build(), true
        )
        .setHandleAudioBecomingNoisy(true)
        .setTrackSelector(DefaultTrackSelector(context))
        .build()

@RequiresApi(Build.VERSION_CODES.O)
fun provideNotificationManager(
    context: Context,
    player: ExoPlayer
): FolkMediaNotificationManager =
    FolkMediaNotificationManager(
        context = context,
        player = player
    )

@OptIn(InternalCoroutinesApi::class, ExperimentalTime::class)
@SuppressLint("UnsafeOptInUsageError")
fun provideMediaSession(
    context: Context,
    player: ExoPlayer
): MediaSession {
    val intent = Intent(context, AuthorizedActivity::class.java)
    val pi = PendingIntent.getActivity(
        context,
        0,
        intent,
        PendingIntent.FLAG_MUTABLE
    )
    return MediaSession.Builder(context, player)
        .setSessionActivity(pi)
        .build()
}