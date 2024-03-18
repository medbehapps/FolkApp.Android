package ge.baqar.gogia.gefolk.ui.media.favourites

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.util.UnstableApi
import ge.baqar.gogia.gefolk.databinding.FragmentFavouritesBinding
import ge.baqar.gogia.gefolk.model.Artist
import ge.baqar.gogia.gefolk.model.ArtistType
import ge.baqar.gogia.gefolk.model.Song
import ge.baqar.gogia.gefolk.ui.media.AuthorizedFragment
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.time.ExperimentalTime

@OptIn(UnstableApi::class)
@ExperimentalTime
@InternalCoroutinesApi
class FavouritesFragment : AuthorizedFragment() {
    private val viewModel: FavouritesViewModel by viewModel()
    private var binding: FragmentFavouritesBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavouritesBinding.inflate(inflater, container, false)
        initializeIntents(flowOf(FavouritesList()))

        authorizedActivity.songMarkedAsFav = { _, _ ->
            initializeIntents(flowOf(FavouritesList()))
        }
        folkPlayerController.trackChanged = {
            currentPlayingSong(it)
        }
        return binding?.root!!
    }

    private fun initializeIntents(inputs: Flow<FavouriteAction>) {
        viewModel.intents(inputs)
            .onEach { output ->
                when (output) {
                    is FavouriteState -> render(output)
                }
            }
            .launchIn(lifecycleScope)
    }

    private fun render(state: FavouriteState) {
        if (state.isInProgress) {
            binding?.favsProgressBar?.visibility = View.VISIBLE
            return
        }

        if (state.error != null) {
            Toast.makeText(context, state.error, Toast.LENGTH_LONG).show()
            return
        }

        binding?.favsProgressBar?.visibility = View.GONE
        if (state.favSongs.isNotEmpty()) {
            binding?.noRecordsView?.visibility = View.GONE
            binding?.favSongsListView?.adapter =
                FavouritesAdapter(state.favSongs) { position, song ->
                    play(
                        position,
                        Artist(song.artistId, song.artistName, ArtistType.ENSEMBLE, true),
                        state.favSongs
                    )
                }
            binding?.favSongsListView?.visibility = View.VISIBLE
        } else {
            binding?.noRecordsView?.visibility = View.VISIBLE
        }
    }

    @SuppressLint("NewApi")
    private fun play(position: Int, artist: Artist, songs: MutableList<Song>) {
        folkPlayerController.artist = artist
        authorizedActivity.playMediaPlayback(position, songs)
    }

    private fun currentPlayingSong(song: Song) {
        binding?.favSongsListView
        (binding?.favSongsListView?.adapter as? FavouritesAdapter)?.apply {
            applyNotPlayingState()
            val adapterSong = dataSource.firstOrNull { it.id == song.id }
            adapterSong?.let{
                it.isPlaying = true
                val position = dataSource.indexOf(it)
                notifyItemChanged(position)
            }
        }
    }
}