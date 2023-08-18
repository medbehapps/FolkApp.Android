package ge.baqar.gogia.goefolk.media

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ShareCompat
import androidx.lifecycle.viewModelScope
import ge.baqar.gogia.goefolk.R
import ge.baqar.gogia.goefolk.databinding.ActivityMenuBinding
import ge.baqar.gogia.goefolk.http.request.downloadLogType
import ge.baqar.gogia.goefolk.http.request.playedLogType
import ge.baqar.gogia.goefolk.http.service_implementations.SongServiceImpl
import ge.baqar.gogia.goefolk.media.MediaPlaybackService.Companion.NEXT_MEDIA
import ge.baqar.gogia.goefolk.media.MediaPlaybackService.Companion.PAUSE_OR_MEDIA
import ge.baqar.gogia.goefolk.media.MediaPlaybackService.Companion.PLAY_MEDIA
import ge.baqar.gogia.goefolk.media.MediaPlaybackService.Companion.PREV_MEDIA
import ge.baqar.gogia.goefolk.media.MediaPlaybackService.Companion.STOP_MEDIA
import ge.baqar.gogia.goefolk.media.player.AudioPlayer
import ge.baqar.gogia.goefolk.model.Artist
import ge.baqar.gogia.goefolk.model.AutoPlayState
import ge.baqar.gogia.goefolk.model.Song
import ge.baqar.gogia.goefolk.model.events.ArtistChanged
import ge.baqar.gogia.goefolk.model.events.OpenEnsembleFragment
import ge.baqar.gogia.goefolk.model.events.SetTimerEvent
import ge.baqar.gogia.goefolk.model.events.SongsMarkedAsFavourite
import ge.baqar.gogia.goefolk.model.events.SongsUnmarkedAsFavourite
import ge.baqar.gogia.goefolk.model.events.UnSetTimerEvent
import ge.baqar.gogia.goefolk.storage.DownloadService
import ge.baqar.gogia.goefolk.storage.FolkAppPreferences
import ge.baqar.gogia.goefolk.ui.media.MenuActivity
import ge.baqar.gogia.goefolk.ui.media.songs.SongsViewModel
import ge.baqar.gogia.goefolk.utility.asDownloadable
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import kotlin.time.ExperimentalTime


@ExperimentalTime
@InternalCoroutinesApi
class MediaPlayerController(
    private val songsViewModel: SongsViewModel,
    private val songServiceImpl: SongServiceImpl,
    private val folkAppPreferences: FolkAppPreferences,
    private val audioPlayer: AudioPlayer,
    private val activity: MenuActivity?
) {

    val isInitialized: Boolean by lazy {
        audioPlayer.isInitialized
    }
    private val binding: ActivityMenuBinding? by lazy {
        activity?.binding
    }
    var artist: Artist? = null
    var playList: MutableList<Song>? = null

    private var position = 0
    private var autoPlayState = AutoPlayState.OFF
    private var timerSet = false

    private fun checkAutoPlayEnabled() {
        autoPlayState = folkAppPreferences.getAutoPlay()
        binding?.mediaPlayerView?.setAutoPlayState(autoPlayState)
    }

    private fun onPrepareListener() {
        val durationString = audioPlayer.getDurationString()
        val duration = audioPlayer.getDuration().toInt()
        binding?.mediaPlayerView?.setAutoPlayState(autoPlayState)
        binding?.mediaPlayerView?.setDuration(durationString, duration)
    }

    private fun initializeAudioPlayerChanges() {
        audioPlayer.completed {
            when (autoPlayState) {
                AutoPlayState.OFF -> {
                    stop()
                    binding?.mediaPlayerView?.setDuration(null, 0)
                    return@completed
                }

                AutoPlayState.REPEAT_ONE -> {
                    binding?.mediaPlayerView?.setDuration(null, 0)
                    val repeatedSong = getCurrentSong()!!
                    songsViewModel.viewModelScope.launch {
                        audioPlayer.play(
                            repeatedSong.path,
                            repeatedSong.data
                        ) { onPrepareListener() }
                        EventBus.getDefault().post(ArtistChanged(PLAY_MEDIA))
                    }
                    updateUI(repeatedSong)
                    logPlayedSong(repeatedSong)
                }

                AutoPlayState.REPEAT_ALBUM -> {
                    if (isThereAnythingNextToPlay()) {
                        next()
                        return@completed
                    }

                    stop()
                }
            }
        }
        audioPlayer.listenPlayer {
            binding?.mediaPlayerView?.isPlaying(it)
        }
        audioPlayer.updateTimeHandler { progress, time ->
            binding?.mediaPlayerView?.setProgress(time, progress.toInt())
        }
    }

    private fun initializeViewListeners() {
        binding?.mediaPlayerView?.setSeekListener = { progress ->
            songsViewModel.viewModelScope.launch {
                audioPlayer.playOn(progress)
            }
        }

        binding?.mediaPlayerView?.onNext = {
            if (isThereAnythingNextToPlay())
                next()
        }
        binding?.mediaPlayerView?.onPrev = {
            if (isThereAnythingPriorToPlay())
                previous()
        }
        binding?.mediaPlayerView?.onPlayPause = {
            if (audioPlayer.isPlaying()) {
                pause()
            } else {
                if (audioPlayer.isInitialized)
                    resume()
                else
                    play()
            }
        }
        binding?.mediaPlayerView?.onShare = {
            val currentSong = getCurrentSong()
            currentSong?.let {
                ShareCompat.IntentBuilder(activity!!)
                    .setType("text/plain")
                    .setChooserTitle(it.name)
                    .setText(it.path)
                    .startChooser();
            }
        }
        binding?.mediaPlayerView?.onStop = {
            stop()
        }
        binding?.mediaPlayerView?.onAutoPlayChanged = {
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

        binding?.mediaPlayerView?.setOnCloseListener = {
            folkAppPreferences.setPlayerState(binding?.mediaPlayerView?.minimized!!)
        }
        binding?.mediaPlayerView?.openPlayListListener = {
            EventBus.getDefault().post(OpenEnsembleFragment(artist!!, getCurrentSong()))
            binding?.mediaPlayerView?.minimize()
        }

        timerSet = folkAppPreferences.getTimerSet()
        binding?.mediaPlayerView?.setTimer(timerSet)
        binding?.mediaPlayerView?.onTimerSetRequested = {
            timerSet = !timerSet
            folkAppPreferences.setTimerSet(timerSet)
            binding?.mediaPlayerView?.setTimer(timerSet)

            val array =
                arrayOf(
                    activity?.resources?.getString(R.string.unset),
                    "5 წთ",
                    "10 წთ",
                    "30 წთ",
                    "60 წთ"
                )
            var selectedPosition = 0
            val dialog = AlertDialog.Builder(activity!!)
                .setTitle(R.string.timer_title)
                .setSingleChoiceItems(
                    array, 0
                ) { _, position -> selectedPosition = position }
                .setPositiveButton(
                    activity.resources?.getString(R.string.set)
                ) { _, _ ->
                    val item = array[selectedPosition]
                    if (item == activity.resources?.getString(R.string.unset)) {
                        EventBus.getDefault().post(UnSetTimerEvent)
                    } else {
                        val time = item?.split(' ')!![0].toLong()
                        EventBus.getDefault().post(SetTimerEvent(time))
                    }
                }
                .create()
            dialog.show()
        }

        binding?.mediaPlayerView?.setFavButtonClickListener = {
            val currentSong = getCurrentSong()!!
            songsViewModel.viewModelScope.launch {
                var isFav = currentSong.isFav || songsViewModel.isSongFav(currentSong.id)
                songServiceImpl.markAsFavourite(currentSong.id)
                val downloadableSongs = currentSong.asDownloadable()
                if (!isFav) {
                    updateFavouriteMarkFor(currentSong.also {
                        isFav = true
                    })
                    EventBus.getDefault()
                        .post(SongsMarkedAsFavourite(mutableListOf(downloadableSongs)))

                    songsViewModel.log(currentSong.id, downloadLogType)
                    val intent = Intent(activity, DownloadService::class.java).apply {
                        action = DownloadService.DOWNLOAD_SONGS
                        putExtra("ensemble", artist)
                        putParcelableArrayListExtra(
                            "songs",
                            arrayListOf(downloadableSongs)
                        )
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        activity?.startForegroundService(intent)
                    } else {
                        activity?.startService(intent)
                    }
                } else {
                    updateFavouriteMarkFor(currentSong.also {
                        isFav = false
                    })
                    EventBus.getDefault()
                        .post(SongsUnmarkedAsFavourite(mutableListOf(downloadableSongs)))

                    val intent = Intent(activity, DownloadService::class.java).apply {
                        action = DownloadService.STOP_DOWNLOADING
                        putExtra("ensemble", artist)
                        putParcelableArrayListExtra(
                            "songs",
                            arrayListOf(downloadableSongs)
                        )
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        activity?.startForegroundService(intent)
                    } else {
                        activity?.startService(intent)
                    }
                }
            }
        }
    }

    fun songsMarkedAsFavourite(event: SongsMarkedAsFavourite) {
        val ids = event.songs
        playList?.filter { event.songs.map { song -> song.id }.contains(it.id) }?.forEach { song ->
            song.isFav = ids.map { it.id }.contains(song.id)
        }
        updateFavouriteMarkFor(getCurrentSong())
    }

    fun songsUnMarkedAsFavourite(event: SongsUnmarkedAsFavourite) {
        val ids = event.songs
        playList?.filter { event.songs.map { song -> song.id }.contains(it.id) }?.forEach { song ->
            song.isFav = !ids.map { it.id }.contains(song.id)
        }
        updateFavouriteMarkFor(getCurrentSong())
    }

    fun play() {
        playList?.let {
            val song = playList!![this.position]
            initializeAudioPlayerChanges()
            initializeViewListeners()
            songsViewModel.viewModelScope.launch {
                audioPlayer.play(song.path, song.data) { onPrepareListener() }
            }
            binding?.mediaPlayerView?.setTrackTitle(song.name, artist?.name)
            binding?.mediaPlayerView?.show()
            checkAutoPlayEnabled()
            updateFavouriteMarkFor(song)
            logPlayedSong(song)
        }
    }

    fun initialize() {
        playList?.let {
            val song = playList!![this.position]
            initializeAudioPlayerChanges()
            initializeViewListeners()
            binding?.mediaPlayerView?.setTrackTitle(song.name, artist?.name)
            binding?.mediaPlayerView?.show()
            checkAutoPlayEnabled()
            updateFavouriteMarkFor(song)
        }
    }

    fun pause() {
        EventBus.getDefault().post(ArtistChanged(PAUSE_OR_MEDIA))

        audioPlayer.pause()
    }

    fun stop() {
        audioPlayer.stop()
        EventBus.getDefault().post(ArtistChanged(STOP_MEDIA))
        binding?.mediaPlayerView?.setProgress(null, 0)
        binding?.mediaPlayerView?.minimize()
    }

    fun resume() {
        EventBus.getDefault().post(ArtistChanged(PAUSE_OR_MEDIA))
        audioPlayer.resume()
    }

    private fun isThereAnythingNextToPlay(): Boolean {
        return (position + 1) < playList?.size!!
    }

    private fun isThereAnythingPriorToPlay(): Boolean {
        return position > 0
    }

    fun next() {
        ++position
        val song = playList!![position]
        updateUI(song)
        logPlayedSong(song)
        songsViewModel.viewModelScope.launch {
            audioPlayer.play(song.path, song.data) { onPrepareListener() }
            EventBus.getDefault().post(ArtistChanged(NEXT_MEDIA))
        }
    }

    fun previous() {
        --position
        val song = playList!![position]
        updateUI(song)
        logPlayedSong(song)
        songsViewModel.viewModelScope.launch {
            audioPlayer.play(song.path, song.data) { onPrepareListener() }
            EventBus.getDefault().post(ArtistChanged(PREV_MEDIA))
        }
    }

    private fun updateFavouriteMarkFor(song: Song?) {
        song?.let {
            binding?.mediaPlayerView?.setIsFav(song.isFav)
            playList?.firstOrNull { it.id == song.id }?.isFav = song.isFav
        }
    }

    fun getCurrentSong(): Song? {
        return if (playList == null) null else playList!![position]
    }

    fun isPlaying(): Boolean {
        return audioPlayer.isPlaying()
    }

    private fun updateUI(song: Song) {
        checkAutoPlayEnabled()
        binding?.mediaPlayerView?.setTrackTitle(song.name, artist?.name)
        binding?.mediaPlayerView?.isPlaying(true)
        updateFavouriteMarkFor(song)
        onPrepareListener()
        binding?.mediaPlayerView?.show()
    }

    private fun logPlayedSong(song: Song) {
        songsViewModel.viewModelScope.launch {
            songsViewModel.log(song.id, playedLogType)
        }
    }

    fun setInitialPosition(position: Int) {
        this.position = position
    }

    fun showPlayer() {
        binding?.mediaPlayerView?.show()
    }

    fun storeCurrentSong() {
        getCurrentSong()?.let {
            folkAppPreferences.setCurrentSong(it.id)
            folkAppPreferences.setCurrentArtist(it.artistId)
        }
    }
}