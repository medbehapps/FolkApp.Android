package ge.baqar.gogia.gefolk.ui.media.playlist

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatEditText
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.util.UnstableApi
import androidx.navigation.fragment.findNavController
import ge.baqar.gogia.gefolk.R
import ge.baqar.gogia.gefolk.databinding.FragmentPlaylistBinding
import ge.baqar.gogia.gefolk.http.request.removeSongAction
import ge.baqar.gogia.gefolk.http.response.PlayList
import ge.baqar.gogia.gefolk.model.Artist
import ge.baqar.gogia.gefolk.model.ArtistType
import ge.baqar.gogia.gefolk.model.Song
import ge.baqar.gogia.gefolk.ui.media.AuthorizedFragment
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.time.ExperimentalTime


@androidx.annotation.OptIn(UnstableApi::class)
class PlayListFragment : AuthorizedFragment() {

    private var selectedPlayList: PlayList? = null
    private lateinit var binding: FragmentPlaylistBinding
    private val viewModel: PlayListViewModel by viewModel()
    private val playListType = 1
    private val songType = 2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        initializeClickEvents()
        initializeIntents(flowOf(LoadPlayLists()))

        return binding.root
    }

    private fun initializeClickEvents() {
        binding.toolbarInclude.tabTitleView.text = getString(R.string.play_list)
        binding.toolbarInclude.tabBackImageView.setOnClickListener {
            if (canNavigateUp()) {
                findNavController().navigateUp()
                return@setOnClickListener
            }

            selectedPlayList = null
            lifecycleScope.launch {
                initializeIntents(channelFlow {
                    send(ReloadAction())
                })
            }
            binding.toolbarInclude.tabTitleView.text = getString(R.string.play_list)
        }
        binding.createNewPlaylistBtn.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
            builder.setTitle(getString(R.string.new_list))

            val viewInflated: View = LayoutInflater.from(context)
                .inflate(R.layout.view_new_playlist_dialog, view as ViewGroup?, false)

            val input = viewInflated.findViewById<AppCompatEditText>(R.id.playlistNameInput)
            builder.setView(viewInflated)

            builder.setPositiveButton(android.R.string.ok) { dialog, which ->
                dialog.dismiss()
                lifecycleScope.launch {
                    initializeIntents(channelFlow {
                        send(CreateNewPlaylist(input.text.toString(), mutableListOf()))
                    })
                }
            }
            builder.setNegativeButton(android.R.string.cancel, { dialog, which -> dialog.cancel() })
            builder.setCancelable(false)
            builder.show()
        }
        binding.deleteSongsBtn.setOnClickListener {
            when (itemDeleteType()) {
                playListType -> {
                    (binding.playlistListView.adapter as? PlayListAdapter)?.let {
                        val playlists =
                            it.dataSource.filter { a -> a.isSelected }.map { it.playListId }
                                .toMutableList()

                        if (playlists.any()) {
                            lifecycleScope.launch {
                                initializeIntents(channelFlow {
                                    send(
                                        DeletePlayListsAction(
                                            playlists
                                        )
                                    )
                                })
                            }
                        }
                    }
                }

                songType -> {
                    (binding.playlistSongs.adapter as? PlayListSongsAdapter)?.let {
                        val selectedSongs =
                            it.dataSource.filter { a -> a.isSelected }.toMutableList()

                        if (selectedSongs.any()) {
                            lifecycleScope.launch {
                                initializeIntents(channelFlow {
                                    send(
                                        RemoveSongsFromPlayList(
                                            selectedPlayList?.playListId!!,
                                            selectedSongs,
                                            removeSongAction
                                        )
                                    )
                                })
                            }
                        }
                    }
                }
            }
        }
    }

    private fun itemDeleteType(): Int {
        return if (canNavigateUp()) playListType else songType
    }

    private fun canNavigateUp(): Boolean {
        return binding.playlistSongs.visibility == View.GONE
    }

    private fun initializeIntents(inputs: Flow<PlayListAction>) {
        viewModel.intents(inputs).onEach { output ->
            when (output) {
                is PlayListResultState -> render(output)
                is ReloadState -> initializeIntents(flowOf(LoadPlayLists()))
            }
        }.launchIn(lifecycleScope)
    }

    private fun render(output: PlayListResultState) {
        if (output.isInProgress) {
            binding.playlistProgressBar.visibility = View.VISIBLE
            binding.playlistSongs.visibility = View.GONE
            binding.playlistListView.visibility = View.GONE
            return
        }
        binding.playlistProgressBar.visibility = View.GONE

        if (output.error != null) {
            Toast.makeText(activity, output.error, Toast.LENGTH_SHORT).show()
            return
        }

        if (output.result.isEmpty()) {
            binding.emptyText.visibility = View.VISIBLE
            return
        }

        binding.deleteSongsBtn.visibility = View.GONE
        binding.emptyText.visibility = View.GONE
        binding.playlistListView.visibility = View.VISIBLE
        val listAdapter = PlayListAdapter(
            output.result,
            { playList ->

                selectedPlayList = playList
                binding.playlistSongs.visibility = View.VISIBLE
                binding.playlistListView.visibility = View.GONE

                binding.playlistSongs.adapter = PlayListSongsAdapter(
                    playList.songs,
                    { position, song ->
                        play(
                            position,
                            Artist(song.artistId, song.artistName, ArtistType.ENSEMBLE, true),
                            playList.songs
                        )
                    },
                    { songs ->
                        binding.deleteSongsBtn.visibility =
                            if (songs.any()) View.VISIBLE else View.GONE
                    })
                binding.toolbarInclude.tabTitleView.text = playList.name
            },
            {
                binding.deleteSongsBtn.visibility =
                    if (it.any()) View.VISIBLE else View.GONE
            }
        )
        binding.playlistListView.adapter = listAdapter

        selectedPlayList?.let {
            val playList =
                viewModel.state.result.find { it.playListId == selectedPlayList?.playListId!! }
            if (playList != null) {
                binding.playlistSongs.adapter = PlayListSongsAdapter(
                    playList.songs,
                    { position, song ->
                        play(
                            position,
                            Artist(song.artistId, song.artistName, ArtistType.ENSEMBLE, true),
                            playList.songs
                        )
                    },
                    { songs ->
                        binding.deleteSongsBtn.visibility =
                            if (songs.any()) View.VISIBLE else View.GONE
                    })
            }
        }
    }

    @OptIn(InternalCoroutinesApi::class, ExperimentalTime::class)
    @SuppressLint("NewApi")
    private fun play(position: Int, artist: Artist, songs: MutableList<Song>) {
        folkPlayerController.artist = artist
        authorizedActivity.playMediaPlayback(position, songs)
    }
}