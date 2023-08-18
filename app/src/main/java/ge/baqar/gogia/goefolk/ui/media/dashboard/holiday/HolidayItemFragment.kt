package ge.baqar.gogia.goefolk.ui.media.dashboard.holiday

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import ge.baqar.gogia.goefolk.databinding.FragmentHolidayItemBinding
import ge.baqar.gogia.goefolk.http.response.HolidaySongData
import ge.baqar.gogia.goefolk.model.Artist
import ge.baqar.gogia.goefolk.model.ArtistType
import ge.baqar.gogia.goefolk.model.Song
import ge.baqar.gogia.goefolk.model.events.CurrentPlayingSong
import ge.baqar.gogia.goefolk.model.events.SongsMarkedAsFavourite
import ge.baqar.gogia.goefolk.model.events.SongsUnmarkedAsFavourite
import ge.baqar.gogia.goefolk.storage.db.FolkApiDao
import ge.baqar.gogia.goefolk.ui.media.MenuActivity
import ge.baqar.gogia.goefolk.ui.media.playlist.AddSongToPlayListDialog
import ge.baqar.gogia.goefolk.ui.media.songs.SongsAdapter
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.android.ext.android.inject
import kotlin.time.ExperimentalTime

class HolidayItemFragment : Fragment() {
    lateinit var data: HolidaySongData
    private lateinit var binding: FragmentHolidayItemBinding

    private val addSongDialog: AddSongToPlayListDialog by inject()
    private val folkApiDao: FolkApiDao by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

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
                    song.isFav = folkApiDao.song(song.id) != null
                    play(index, song)
                    currentPlayingSong(CurrentPlayingSong(song))
                }
            }, {
                addSongDialog.showPlayListDialog(requireActivity(), mutableListOf(it))
            })

        return binding.root
    }

    @SuppressLint("NewApi")
    @OptIn(InternalCoroutinesApi::class, ExperimentalTime::class)
    private fun play(position: Int, song: Song) {
        (activity as MenuActivity).playMediaPlayback(
            position,
            data.holidaySongs,
            Artist(song.artistId, song.artistName, ArtistType.ENSEMBLE, true)
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

    @SuppressLint("NotifyDataSetChanged")
    @Subscribe
    fun songsMarkedAsFavourite(event: SongsMarkedAsFavourite) {
        (binding.holidaySongsListView.adapter as SongsAdapter).apply {
            dataSource
                .filter { event.songs.map { it.id }.contains(it.id) }
                .forEach {
                    it.isFav = true
                }
            notifyDataSetChanged()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @Subscribe
    fun songsMarkedAsFavourite(event: SongsUnmarkedAsFavourite) {
        (binding.holidaySongsListView.adapter as? SongsAdapter)?.apply {
            dataSource
                .filter { event.songs.map { it.id }.contains(it.id) }
                .forEach {
                    it.isFav = false
                }
            notifyDataSetChanged()
        }
    }
}