package ge.baqar.gogia.goefolk.ui.media.dashboard

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import ge.baqar.gogia.goefolk.databinding.FragmentArtistsBinding
import ge.baqar.gogia.goefolk.databinding.FragmentDashboardBinding
import ge.baqar.gogia.goefolk.http.response.HolidaySongData
import ge.baqar.gogia.goefolk.model.Artist
import ge.baqar.gogia.goefolk.model.ArtistType
import ge.baqar.gogia.goefolk.model.Song
import ge.baqar.gogia.goefolk.ui.media.MenuActivity
import ge.baqar.gogia.goefolk.ui.media.ensembles.EnsemblesRequested
import ge.baqar.gogia.goefolk.ui.media.ensembles.OldRecordingsRequested
import ge.baqar.gogia.goefolk.ui.media.songs.SongsAdapter
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import kotlin.time.ExperimentalTime

class DashboardFragment : Fragment() {

    private val viewModel: DashboardViewModel by viewModel()
    private lateinit var binding: FragmentDashboardBinding
    private var _view: View? = null

    @OptIn(InternalCoroutinesApi::class, ExperimentalTime::class)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (_view == null) {
            binding = FragmentDashboardBinding.inflate(inflater, container, false)
            binding.daySongLayout.setOnClickListener {
                (activity as MenuActivity).playMediaPlayback(
                    0,
                    mutableListOf(viewModel.state.daySong!!),
                    Artist(
                        viewModel.state.daySong?.artistId!!,
                        viewModel.state.daySong?.artistName!!,
                        ArtistType.ENSEMBLE,
                        true
                    )
                )
            }
            binding.dayChantLayout.setOnClickListener {
                (activity as MenuActivity).playMediaPlayback(
                    0,
                    mutableListOf(viewModel.state.dayChant!!),
                    Artist(
                        viewModel.state.dayChant?.artistId!!,
                        viewModel.state.dayChant?.artistName!!,
                        ArtistType.ENSEMBLE,
                        true
                    )
                )
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
            Timber.i(state.error)
            return
        }

        binding.daySong = state.daySong?.detailedName()
        binding.dayChant = state.dayChant?.detailedName()

        binding.holidaysViewPager.adapter = HolidaysPagerAdapter(state.holidayItems!!, childFragmentManager)
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