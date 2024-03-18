package ge.baqar.gogia.gefolk.ui.media.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.BindingAdapter
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.util.UnstableApi
import androidx.navigation.fragment.findNavController
import ge.baqar.gogia.gefolk.R
import ge.baqar.gogia.gefolk.databinding.FragmentDashboardBinding
import ge.baqar.gogia.gefolk.model.Artist
import ge.baqar.gogia.gefolk.model.ArtistType
import ge.baqar.gogia.gefolk.ui.media.AuthorizedFragment
import ge.baqar.gogia.gefolk.ui.media.dashboard.holiday.HolidaysPagerAdapter
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.time.ExperimentalTime

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(InternalCoroutinesApi::class, ExperimentalTime::class)
class DashboardFragment : AuthorizedFragment() {

    private val viewModel: DashboardViewModel by viewModel()
    private lateinit var binding: FragmentDashboardBinding
    private var _view: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (_view == null) {
            binding = FragmentDashboardBinding.inflate(inflater, container, false)
            binding.daySongLayout.setOnClickListener {
                folkPlayerController.artist = Artist(
                    viewModel.state.daySong?.artistId!!,
                    viewModel.state.daySong?.artistName!!,
                    ArtistType.ENSEMBLE,
                    true
                )
                authorizedActivity.playMediaPlayback(
                    0,
                    mutableListOf(viewModel.state.daySong!!)
                )
            }
            binding.dayChantLayout.setOnClickListener {
                folkPlayerController.artist = Artist(
                    viewModel.state.dayChant?.artistId!!,
                    viewModel.state.dayChant?.artistName!!,
                    ArtistType.ENSEMBLE,
                    true
                )
                authorizedActivity.playMediaPlayback(
                    0,
                    mutableListOf(viewModel.state.dayChant!!)
                )
            }
            binding.searchBtn.setOnClickListener {
                findNavController().navigate(R.id.navigation_search)
            }

            initializeIntents(flowOf(DashboardDataRequested()))
            _view = binding.root
            return _view!!
        }
        return _view!!
    }

    private fun initializeIntents(inputs: Flow<DashboardAction>) {
        viewModel.intents(inputs)
            .onEach { output ->
                when (output) {
                    is DashboardState -> render(output)
                }
            }
            .launchIn(lifecycleScope)
    }

    private fun render(state: DashboardState) {
        if (state.isInProgress) {
            binding.loading = true
            return
        }

        binding.loading = false
        if (state.error != null) {
            Toast.makeText(context, state.error, Toast.LENGTH_LONG).show()
            return
        }

        binding.daySong = state.daySong?.detailedName()
        binding.dayChant = state.dayChant?.detailedName()

        binding.holidaysViewPager.adapter =
            HolidaysPagerAdapter(state.holidayItems!!, childFragmentManager)
        binding.hasHolidays = state.holidayItems.any()
    }

    companion object {
        @JvmStatic
        @BindingAdapter("app:loading")
        fun loading(view: View, loading: Boolean) {
            if (loading) {
                view.visibility = View.VISIBLE
            } else {
                view.visibility = View.GONE
            }
        }

        @JvmStatic
        @BindingAdapter("app:areHolidaysAvailable")
        fun areHolidaysAvailable(view: View, holidays: Boolean) {
            if (holidays) {
                view.visibility = View.VISIBLE
            } else {
                view.visibility = View.GONE
            }
        }
    }
}