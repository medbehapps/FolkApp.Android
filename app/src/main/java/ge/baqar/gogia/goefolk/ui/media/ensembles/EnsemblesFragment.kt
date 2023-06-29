package ge.baqar.gogia.goefolk.ui.media.ensembles

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.findNavController
import ge.baqar.gogia.goefolk.R
import ge.baqar.gogia.goefolk.databinding.FragmentArtistsBinding
import ge.baqar.gogia.goefolk.model.events.CurrentPlayingSong
import ge.baqar.gogia.goefolk.model.events.OpenArtistFragment
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

@InternalCoroutinesApi
class EnsemblesFragment : Fragment() {

    private val viewModel: EnsemblesViewModel by viewModel()
    private var binding: FragmentArtistsBinding? = null
    private var _view: View? = null

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
        if (_view == null) {
            binding = FragmentArtistsBinding.inflate(inflater, container, false)
            if (binding?.artistsListView?.adapter == null) {
                val action = if (arguments?.get("artistType")?.toString()?.equals("1") == true) {
                    EnsemblesRequested()
                } else {
                    OldRecordingsRequested()
                }
                val loadFlow = flowOf(action)
                initializeIntents(loadFlow)
            }
            _view = binding?.root
            return _view!!
        }
        return _view!!
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun openArtistFragment(event: OpenArtistFragment) {
        val navController = findNavController()
        navController.navigate(
            R.id.navigation_artists_details,
            Bundle().apply {
                putParcelable("ensemble", event.artist)
            })
        navController.addOnDestinationChangedListener(object: NavController.OnDestinationChangedListener{
            override fun onDestinationChanged(
                controller: NavController,
                destination: NavDestination,
                arguments: Bundle?
            ) {
                EventBus.getDefault()
                    .post(CurrentPlayingSong(event.playingSong))
                navController.removeOnDestinationChangedListener(this)
            }
        })
    }

    private fun initializeIntents(inputs: Flow<EnsemblesAction>) {
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
            Timber.i(state.error)
            return
        }
        if (state.isInProgress) {
            binding?.noRecordsView?.visibility = View.GONE
            binding?.artistsProgressbar?.visibility = View.VISIBLE
            return
        }

        binding?.artistsProgressbar?.visibility = View.GONE
        if (state.artists.isNotEmpty()) {
            binding?.noRecordsView?.visibility = View.GONE
            binding?.artistsListView?.visibility = View.VISIBLE
            binding?.artistsListView?.adapter =
                EnsemblesAdapter(state.artists) {
                    openArtistFragment(OpenArtistFragment(it))
                }
        } else {
            binding?.artistsListView?.visibility = View.GONE
            binding?.noRecordsView?.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}