package ge.baqar.gogia.gefolk.ui.media.artists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import ge.baqar.gogia.gefolk.R
import ge.baqar.gogia.gefolk.databinding.FragmentArtistsBinding
import ge.baqar.gogia.gefolk.ui.media.AuthorizedFragment
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.time.ExperimentalTime

@InternalCoroutinesApi
@OptIn(ExperimentalTime::class)
class ArtistsFragment : AuthorizedFragment() {

    private val viewModel: ArtistsViewModel by viewModel()
    private lateinit var binding: FragmentArtistsBinding
    private var _view: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (_view == null) {
            binding = FragmentArtistsBinding.inflate(inflater, container, false)
            binding.include.tabTitleView.text = getString(R.string.artists)

            if (binding.artistsListView.adapter == null) {
                val action = if (arguments?.get("artistType")?.toString()?.equals("1") == true) {
                    ArtistsRequested()
                } else {
                    OldRecordingsRequested()
                }
                val loadFlow = flowOf(action)
                initializeIntents(loadFlow)
            }

            binding.include.tabBackImageView.setOnClickListener {
                findNavController().navigateUp()
            }

            _view = binding.root
            return _view!!
        }
        return _view!!
    }

    private fun openEnsembleFragment() {
        val navController = findNavController()
        navController.navigate(
            R.id.navigation_artists_details)

//        navController.addOnDestinationChangedListener(object :
//            NavController.OnDestinationChangedListener {
//            override fun onDestinationChanged(
//                controller: NavController,
//                destination: NavDestination,
//                arguments: Bundle?
//            ) {
//                EventBus.getDefault()
//                    .post(CurrentPlayingSong(event.playingSong))
//                navController.removeOnDestinationChangedListener(this)
//            }
//        })
    }

    private fun initializeIntents(inputs: Flow<ArtistsAction>) {
        viewModel.intents(inputs)
            .onEach { output ->
                when (output) {
                    is ArtistsState -> render(output)
                }
            }
            .launchIn(lifecycleScope)
    }

    private fun render(state: ArtistsState) {
        if (state.error != null) {
            Toast.makeText(context, state.error, Toast.LENGTH_LONG).show()
            return
        }
        if (state.isInProgress) {
            binding.noRecordsView.visibility = View.GONE
            binding.artistsProgressbar.visibility = View.VISIBLE
            return
        }

        binding.artistsProgressbar.visibility = View.GONE
        if (state.artists.isNotEmpty()) {
            binding.noRecordsView.visibility = View.GONE
            binding.artistsListView.visibility = View.VISIBLE
            binding.artistsListView.adapter =
                ArtistsAdapter(state.artists) {
                    folkPlayerController.artist = it
                    openEnsembleFragment()
                }
        } else {
            binding.artistsListView.visibility = View.GONE
            binding.noRecordsView.visibility = View.VISIBLE
        }
    }
}