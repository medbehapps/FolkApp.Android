package ge.baqar.gogia.gefolk.ui.media.dashboard.holiday

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.util.UnstableApi
import com.bumptech.glide.Glide
import ge.baqar.gogia.gefolk.databinding.FragmentHolidayItemBinding
import ge.baqar.gogia.gefolk.http.response.HolidaySongData
import ge.baqar.gogia.gefolk.model.Artist
import ge.baqar.gogia.gefolk.model.ArtistType
import ge.baqar.gogia.gefolk.model.Song
import ge.baqar.gogia.gefolk.model.events.CurrentPlayingSong
import ge.baqar.gogia.gefolk.ui.media.AuthorizedFragment
import ge.baqar.gogia.gefolk.ui.media.playlist.AddSongToPlayListDialog
import ge.baqar.gogia.gefolk.ui.media.songs.SongsAdapter
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.android.ext.android.inject
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class HolidayItemFragment : AuthorizedFragment() {
    lateinit var data: HolidaySongData

    private lateinit var binding: FragmentHolidayItemBinding

    private val addSongDialog: AddSongToPlayListDialog by inject()

    @SuppressLint("UnsafeOptInUsageError", "NotifyDataSetChanged")
    @OptIn(InternalCoroutinesApi::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHolidayItemBinding.inflate(layoutInflater, container, false)

        binding.holidayTitle = data.title
        Glide.with(requireActivity())
            .load(data.imagePath)
            .into(binding.holidayImageView)

        binding.holidaySongsListView.adapter =
            SongsAdapter(data.holidaySongs, { song, index ->
                lifecycleScope.launch {
                    play(index, song)
                    currentPlayingSong(CurrentPlayingSong(song))
                }
            }, {
                addSongDialog.showPlayListDialog(requireActivity(), mutableListOf(it))
            })

        authorizedActivity.songMarkedAsFav = { songId, isFav ->
            binding.holidaySongsListView.post {
                (binding.holidaySongsListView.adapter as? SongsAdapter)?.apply {
                    dataSource
                        .filter { songId == it.id }
                        .forEach {
                            it.isFav = isFav
                        }
                    notifyDataSetChanged()
                }
            }
        }
        return binding.root
    }

    @androidx.annotation.OptIn(UnstableApi::class)
    @SuppressLint("NewApi")
    @OptIn(InternalCoroutinesApi::class, ExperimentalTime::class)
    private fun play(position: Int, song: Song) {
        folkPlayerController.artist =
            Artist(song.artistId, song.artistName, ArtistType.ENSEMBLE, true)
        authorizedActivity.playMediaPlayback(
            position,
            data.holidaySongs
        )
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun currentPlayingSong(event: CurrentPlayingSong?) {
        val song = event?.song
        if (data.holidaySongs.isNotEmpty()) {
            song?.let {
                (binding.holidaySongsListView.adapter as? SongsAdapter)?.apply {
                    applyNotPlayingState()
                    dataSource.firstOrNull { it.id == song.id }?.isPlaying = true
                }
            }
        }
    }
}