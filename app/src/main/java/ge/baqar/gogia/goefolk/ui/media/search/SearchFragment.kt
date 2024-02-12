package ge.baqar.gogia.goefolk.ui.media.search

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.util.UnstableApi
import androidx.navigation.fragment.findNavController
import ge.baqar.gogia.goefolk.R
import ge.baqar.gogia.goefolk.ui.media.AuthorizedActivity
import ge.baqar.gogia.goefolk.model.Artist
import ge.baqar.gogia.goefolk.model.Song
import ge.baqar.gogia.goefolk.databinding.FragmentSearchBinding
import ge.baqar.gogia.goefolk.ui.media.AuthorizedFragment
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import reactivecircus.flowbinding.android.widget.textChanges
import timber.log.Timber
import kotlin.time.ExperimentalTime


@ExperimentalTime
@InternalCoroutinesApi
@FlowPreview
@androidx.annotation.OptIn(UnstableApi::class)
class SearchFragment : AuthorizedFragment() {
    private val viewModel: SearchViewModel by viewModel()
    private lateinit var binding: FragmentSearchBinding
    private val ime: InputMethodManager by lazy {
        context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        initializeIntents(binding.searchTermInput.textChanges()
            .debounce(500)
            .map { it.toString() }
            .map {
                if (it.length > 2)
                    DoSearch(it)
                else
                    ClearSearchResult
            }
        )

        binding.searchInclude.ensemblesSearchTab.setOnClickListener {
            binding.ensemblesSearchResultListView.visibility = View.VISIBLE
            binding.songsSearchResultListView.visibility = View.GONE
        }
        binding.searchInclude.songsSearchTab.setOnClickListener {
            binding.ensemblesSearchResultListView.visibility = View.GONE
            binding.songsSearchResultListView.visibility = View.VISIBLE
        }
        showKeyBoard()
        (activity as AuthorizedActivity).destinationChanged = {
            if (it == javaClass.name) {
                showKeyBoard()
            } else {
                hideKeyBoard()
            }
        }
        return binding.root
    }

    private fun hideKeyBoard() {
        ime.hideSoftInputFromWindow(view?.windowToken, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    private fun showKeyBoard() {
        binding.searchTermInput.post {
            binding.searchTermInput.requestFocus()
            ime.showSoftInput(binding.searchTermInput, InputMethodManager.SHOW_IMPLICIT)
            binding.searchTermInput.requestFocus()
        }
    }

    private fun initializeIntents(inputs: Flow<SearchAction>) {
        viewModel.intents(inputs)
            .onEach { output ->
                when (output) {
                    is SearchState -> render(output)
                }
            }
            .launchIn(lifecycleScope)
    }

    private fun render(state: SearchState) {
        if (state.error != null) {
            Toast.makeText(context, state.error, Toast.LENGTH_LONG).show()
            binding.searchProgressBar.visibility = View.GONE
            Timber.i(state.error)
            return
        }

        if (state.isInProgress) {
            binding.searchProgressBar.visibility = View.VISIBLE
            return
        }
        binding.ensemblesSearchResultListView.visibility = View.GONE
        binding.songsSearchResultListView.visibility = View.GONE

        state.result?.let {
            binding.searchProgressBar.visibility = View.GONE
            if (state.result.artists.isNotEmpty()) {
                binding.ensemblesSearchResultListView.adapter =
                    SearchedDataAdapter(state.result.artists) { _, ensemble ->
                        findNavController().navigate(
                            R.id.navigation_artists_details,
                            Bundle().apply {
                                putParcelable("ensemble", ensemble)
                            })
                    }
                binding.ensemblesSearchResultListView.visibility = View.VISIBLE
            } else {
                binding.searchInclude.tabSeparator.visibility = View.GONE
                binding.searchInclude.ensemblesSearchTab.visibility = View.GONE
            }

            if (state.result.songs.isNotEmpty()) {
                binding.songsSearchResultListView.adapter =
                    SearchedDataAdapter(state.result.songs) { position, song ->
                        viewModel.ensembleById(song.artistId) { ensemble ->
                            ensemble?.let {
                                play(position, ensemble, state.result.songs)
                            }
                        }
                    }
                if (state.result.artists.isEmpty()) {
                    binding.songsSearchResultListView.visibility = View.VISIBLE
                } else {
                    binding.songsSearchResultListView.visibility = View.GONE
                }
            } else {
                binding.searchInclude.tabSeparator.visibility = View.GONE
                binding.searchInclude.songsSearchTab.visibility = View.GONE
            }
        }
    }

    @SuppressLint("NewApi")
    private fun play(position: Int, artist: Artist, songs: MutableList<Song>) {
        folkPlayerController.artist = artist
        authorizedActivity.playMediaPlayback(position, songs)
    }
}