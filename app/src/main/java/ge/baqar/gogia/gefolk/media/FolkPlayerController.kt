package ge.baqar.gogia.gefolk.media

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import androidx.core.app.ShareCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import ge.baqar.gogia.gefolk.media.player.AudioPlayer
import ge.baqar.gogia.gefolk.model.Artist
import ge.baqar.gogia.gefolk.model.AutoPlayState
import ge.baqar.gogia.gefolk.model.Song
import ge.baqar.gogia.gefolk.storage.FolkAppPreferences
import ge.baqar.gogia.gefolk.ui.media.AuthorizedActivity
import ge.baqar.gogia.gefolk.view.MediaPlayerView
import kotlinx.coroutines.InternalCoroutinesApi
import kotlin.time.ExperimentalTime

@ExperimentalTime
@OptIn(InternalCoroutinesApi::class)
class FolkPlayerController(
    private val audioPlayer: AudioPlayer,
    private val folkAppPreferences: FolkAppPreferences
) {
    var authorizedActivity: AuthorizedActivity? = null
    var artist: Artist? = null
    var position: Int = -1
    var onPlayListOpen: (() -> Unit)? = null

    private var autoPlayState = AutoPlayState.OFF
    private val mediaPlayerView: MediaPlayerView?
        get() = authorizedActivity?.binding?.mediaPlayerView

    private var playList: MutableList<Song> = mutableListOf()

    val currentSong: Song?
        get() {
            if (position == -1) {
                return null
            }
            return playList[position]
        }

    fun initialize(checkIsPlaying: Boolean = false) {
        audioPlayer.initialize()
        audioPlayer.onReady = { duration, durationString ->
            mediaPlayerView?.setDuration(durationString, duration)
        }

        audioPlayer.updateCallback = { progress, timeString ->
            mediaPlayerView?.setProgress(timeString, progress.toInt())
        }
        audioPlayer.onTrackChange = { audioPlayerPosition ->
            if (autoPlayState == AutoPlayState.REPEAT_ONE) {
                play()
            } else if (position == playList.size - 1 && autoPlayState == AutoPlayState.REPEAT_ALBUM) {
                position = 0
                play()
            } else if (audioPlayerPosition != position) {
                position = audioPlayerPosition
                mediaPlayerView?.setTrackTitle(currentSong?.name, artist?.name)
            }
        }
        audioPlayer.onPlayPause = {
            checkOnPlayPause(it)
        }
        checkAutoPlayEnabled()
        initializeViewListeners()

        if (checkIsPlaying) {
            audioPlayer.forceUpdateCallback()
            checkOnPlayPause(audioPlayer.isPlaying())
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun initializeViewListeners() {
        mediaPlayerView?.onNext = {
            if (position == playList.size - 1) {
                audioPlayer.stop()
            } else {
                position += 1
                audioPlayer.play(position)

                showControls()
            }
        }
        mediaPlayerView?.onPrev = {
            if (position == 0) {
                audioPlayer.stop()
            } else {
                position -= 1
                audioPlayer.play(position)

                showControls()
            }
        }
        mediaPlayerView?.onPlayPause = {
            if (!audioPlayer.initialized) {
                audioPlayer.initialize()
                audioPlayer.play(position)
            }
            checkOnPlayPause(audioPlayer.isPlaying(), true)
        }
        mediaPlayerView?.onShare = {
            val currentSong = currentSong
            currentSong?.let {
                ShareCompat.IntentBuilder(authorizedActivity!!)
                    .setType("text/plain")
                    .setChooserTitle(it.name)
                    .setText(it.path)
                    .startChooser();
            }
        }
        mediaPlayerView?.onStop = {
            audioPlayer.stop()
            authorizedActivity?.stopFolkService()
        }
        mediaPlayerView?.onAutoPlayChanged = {
            when (autoPlayState) {
                AutoPlayState.OFF -> {
                    autoPlayState = AutoPlayState.REPEAT_ALBUM
                }

                AutoPlayState.REPEAT_ALBUM -> {
                    autoPlayState = AutoPlayState.REPEAT_ONE
                }

                AutoPlayState.REPEAT_ONE -> {
                    autoPlayState = AutoPlayState.OFF
                }
            }
            folkAppPreferences.updateAutoPlay(autoPlayState)
            checkAutoPlayEnabled()
        }

        mediaPlayerView?.onRewind = { position ->
            audioPlayer.rewind(position)
        }

        mediaPlayerView?.openPlayListListener = {
            onPlayListOpen?.invoke()
            mediaPlayerView?.minimize()
        }
    }

    private fun checkAutoPlayEnabled() {
        autoPlayState = folkAppPreferences.getAutoPlay()
        authorizedActivity?.binding?.mediaPlayerView?.setAutoPlayState(autoPlayState)
    }

    private fun checkOnPlayPause(isPlaying: Boolean, dispatchEvent: Boolean = false) {
        if (isPlaying) {
            if (dispatchEvent) {
                audioPlayer.pause()
                mediaPlayerView?.isPlaying(false)
                return
            }
            mediaPlayerView?.isPlaying(true)
        } else {
            if (dispatchEvent) {
                audioPlayer.resume()
                mediaPlayerView?.isPlaying(true)
                return
            }
            mediaPlayerView?.isPlaying(false)
        }
        mediaPlayerView?.setIsFav(playList[position].isFav)
    }

    fun setPlaylist(songs: MutableList<Song>) {
        playList = songs
        val data = songs.map {
            val item = MediaItem.Builder().setUri(it.path).setMediaId(it.id).setMediaMetadata(
                MediaMetadata.Builder().setArtist(it.artistName).setTitle(it.name)
                    .setDisplayTitle(it.name).setAlbumTitle(it.artistName)
                    .setDescription(it.detailedName()).build()
            ).build()
            item
        }
        audioPlayer.addMediaData(data)
    }

    fun play() {
        audioPlayer.play(position)
    }

    fun showControls(invalidate: Boolean = false) {
        val song = currentSong
        mediaPlayerView?.show()
        mediaPlayerView?.setTrackTitle(song?.name, artist?.name)

        if (invalidate) {
            Handler(Looper.getMainLooper()).postDelayed({
                mediaPlayerView?.forceMinimize()
                mediaPlayerView?.invalidate()
            }, 100)
        }
    }
}