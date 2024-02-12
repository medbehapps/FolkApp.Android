package ge.baqar.gogia.goefolk.media.player

import android.content.Context
import android.os.CountDownTimer
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Player.DISCONTINUITY_REASON_AUTO_TRANSITION
import androidx.media3.exoplayer.ExoPlayer


class AudioPlayer(private val context: Context, private val player: ExoPlayer) {
    val position: Int
        get() = player.currentMediaItemIndex

    var onTrackChange: ((Int) -> Unit)? = null
    var onReady: ((Int, String?) -> Unit)? = null
    var updateCallback: ((Long, String?) -> Unit)? = null
    var onPlayPause: ((Boolean) -> Unit)? = null

    private var timer: CountDownTimer? = null
    private var durationSet: Boolean = false
    var initialized = false

    fun isPlaying(): Boolean {
        return player.isPlaying
    }

    fun addMediaData(data: List<MediaItem>) {
        player.clearMediaItems()
        player.addMediaItems(data)
    }

    fun play(position: Int) {
        player.prepare()
        player.seekToDefaultPosition(position)
        player.play()
    }

    private fun startTimer() {
        timer = object : CountDownTimer(player.duration, 1000.toLong()) {
            override fun onTick(currentDuration: Long) {
                val durationString = getTimeString(player.currentPosition)
                updateCallback?.invoke(player.currentPosition, durationString)
            }

            override fun onFinish() {
            }
        }
        timer?.start()
    }

    fun getDurationString(duration: Long): String {
        val totalDuration = duration
        return getTimeString(totalDuration)
    }

    private fun getTimeString(millis: Long): String {
        val buf = StringBuffer()
        val minutes = (millis % (1000 * 60 * 60) / (1000 * 60)).toInt()
        val seconds = (millis % (1000 * 60 * 60) % (1000 * 60) / 1000).toInt()
        buf.append(String.format("%02d", minutes))
            .append(":")
            .append(String.format("%02d", seconds))
        return buf.toString()
    }

    fun initialize() {
        initialized = true
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                onPlayPause?.invoke(isPlaying)
            }


            override fun onPositionDiscontinuity(
                oldPosition: Player.PositionInfo,
                newPosition: Player.PositionInfo,
                reason: Int
            ) {
                super.onPositionDiscontinuity(oldPosition, newPosition, reason)
                if (reason == DISCONTINUITY_REASON_AUTO_TRANSITION) {
                    onTrackChange?.invoke(newPosition.mediaItemIndex)
                    timer?.cancel()
                }
            }

            override fun onPlaybackStateChanged(state: Int) {
                when (state) {
                    Player.STATE_ENDED -> {
                    }

                    Player.STATE_BUFFERING -> {
                    }

                    Player.STATE_IDLE -> {
                    }

                    Player.STATE_READY -> {
                        durationSet = true
                        player.playWhenReady = true
                        onReady?.invoke(player.duration.toInt(), getDurationString(player.duration))
                        startTimer()
                    }
                }
            }
        })
    }

    fun pause() {
        player.playWhenReady = false
    }

    fun resume() {
        player.playWhenReady = true
    }

    fun stop() {
        initialized = false
        pause()
        player.seekTo(0)
    }

    fun rewind(position: Int) {
        player.seekTo(position.toLong())
    }

    fun forceUpdateCallback() {
        onReady?.invoke(player.duration.toInt(), getDurationString(player.duration))
        startTimer()
    }
}