package ge.baqar.gogia.gefolk.ui.media.songs

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import ge.baqar.gogia.gefolk.databinding.FragmentArtistBinding
import ge.baqar.gogia.gefolk.model.Song
import ge.baqar.gogia.gefolk.model.SongType
import ge.baqar.gogia.gefolk.ui.media.AuthorizedFragment
import ge.baqar.gogia.gefolk.ui.media.playlist.AddSongToPlayListDialog
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.time.ExperimentalTime

@OptIn(InternalCoroutinesApi::class, ExperimentalTime::class)
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
@ExperimentalTime
class SongsFragment : AuthorizedFragment() {

    private val viewModel: SongsViewModel by viewModel()
    private val addSongDialog: AddSongToPlayListDialog by inject()
    private var binding: FragmentArtistBinding? = null

    @SuppressLint("UseRequireInsteadOfGet", "UnsafeOptInUsageError", "NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentArtistBinding.inflate(inflater, container, false)

        val artist = folkPlayerController.artist
        binding?.toolbarInclude?.tabTitleView?.text = artist?.name

        val loadSongsAndChantsAction = flowOf(
            SongsRequested(artist!!),
        )
        initializeIntents(loadSongsAndChantsAction)

        initializeClickListeners()

        authorizedActivity.songMarkedAsFav = { songId, isFav ->
            binding?.songsListView?.post {
                (binding?.songsListView?.adapter as? SongsAdapter)?.apply {
                    dataSource
                        .filter { songId == it.id }
                        .forEach {
                            it.isFav = isFav
                        }
                    notifyDataSetChanged()
                }
                (binding?.chantsListView?.adapter as? SongsAdapter)?.apply {
                    dataSource
                        .filter { songId == it.id }
                        .forEach {
                            it.isFav = isFav
                        }
                    notifyDataSetChanged()
                }
            }
        }
        folkPlayerController.trackChanged = {
            currentPlayingSong(it)
        }
        isLoading()
        return binding?.root!!
    }

    private fun initializeClickListeners() {
        binding?.tabViewInclude?.artistSongsTab?.setOnClickListener {
            binding?.songsListView?.visibility = View.VISIBLE
            binding?.chantsListView?.visibility = View.GONE
        }

        binding?.tabViewInclude?.artistChantsTab?.setOnClickListener {
            binding?.chantsListView?.visibility = View.VISIBLE
            binding?.songsListView?.visibility = View.GONE
        }
        binding?.toolbarInclude?.tabBackImageView?.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun currentPlayingSong(song: Song) {
        if (song.songType == SongType.Song.index) {
            (binding?.songsListView?.adapter as? SongsAdapter)?.apply {
                applySongPlayingState(song)
            }
        } else {
            (binding?.chantsListView?.adapter as? SongsAdapter)?.apply {
                applySongPlayingState(song)
            }
        }
    }

    private fun SongsAdapter.applySongPlayingState(song: Song){
        applyNotPlayingState()
        val adapterSong = dataSource.firstOrNull { it.id == song.id }
        adapterSong?.let{
            it.isPlaying = true
            val position = dataSource.indexOf(it)
            notifyItemChanged(position)
        }
    }

    private fun initializeIntents(inputs: Flow<SongsAction>) {
        viewModel.intents(inputs)
            .onEach { output ->
                when (output) {
                    is ArtistState -> render(output)
                }
            }
            .launchIn(lifecycleScope)
    }

    private fun isLoading() {
        binding?.songsListView?.visibility = View.GONE
        binding?.chantsListView?.visibility = View.GONE
        binding?.progressbar?.visibility = View.VISIBLE
    }

    @SuppressLint("NotifyDataSetChanged", "DiscouragedApi", "UnsafeOptInUsageError")
    private fun render(state: ArtistState) {
        if (state.isInProgress) {
            isLoading()
            return
        }
        if (state.error != null) {
            Toast.makeText(context, state.error, Toast.LENGTH_LONG).show()
            return
        }

        binding?.progressbar?.visibility = View.GONE

        if (state.songs.size > 0) {
            binding?.songsListView?.adapter = SongsAdapter(state.songs, { song, index ->
                authorizedActivity.playMediaPlayback(index, state.songs)
                currentPlayingSong(song)
            }, {
                addSongDialog.showPlayListDialog(requireActivity(), mutableListOf(it))
            })

            binding?.songsListView?.visibility = View.VISIBLE
            binding?.chantsListView?.visibility = View.GONE
            binding?.tabViewInclude?.artistSongsTab?.visibility = View.VISIBLE
            binding?.tabViewInclude?.tabSeparator?.visibility = View.VISIBLE

            folkPlayerController.currentSong?.let {
                currentPlayingSong(it)
            }
        } else {
            binding?.chantsListView?.visibility = View.VISIBLE
            binding?.songsListView?.visibility = View.GONE
            binding?.songsListView?.visibility = View.GONE
            binding?.tabViewInclude?.artistSongsTab?.visibility = View.GONE
            binding?.tabViewInclude?.tabSeparator?.visibility = View.GONE
        }
        if (state.chants.size > 0) {
            binding?.chantsListView?.adapter = SongsAdapter(state.chants, { song, index ->
                authorizedActivity.playMediaPlayback(index, state.chants)
                currentPlayingSong(song)
            }, {
                addSongDialog.showPlayListDialog(requireActivity(), mutableListOf(it))
            })
            binding?.tabViewInclude?.artistChantsTab?.visibility = View.VISIBLE
            binding?.tabViewInclude?.tabSeparator?.visibility = View.VISIBLE

            folkPlayerController.currentSong?.let {
                currentPlayingSong(it)
            }
        } else {
            binding?.chantsListView?.visibility = View.GONE
            binding?.tabViewInclude?.artistChantsTab?.visibility = View.GONE
            binding?.tabViewInclude?.tabSeparator?.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}