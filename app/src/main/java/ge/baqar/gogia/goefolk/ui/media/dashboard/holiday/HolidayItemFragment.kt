package ge.baqar.gogia.goefolk.ui.media.dashboard.holiday

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import ge.baqar.gogia.goefolk.databinding.FragmentHolidayItemBinding
import ge.baqar.gogia.goefolk.http.response.HolidaySongData
import ge.baqar.gogia.goefolk.model.Artist
import ge.baqar.gogia.goefolk.model.ArtistType
import ge.baqar.gogia.goefolk.model.Song
import ge.baqar.gogia.goefolk.ui.media.MenuActivity
import ge.baqar.gogia.goefolk.ui.media.songs.SongsAdapter
import kotlinx.coroutines.InternalCoroutinesApi
import kotlin.time.ExperimentalTime

class HolidayItemFragment : Fragment() {
    lateinit var data: HolidaySongData
    private lateinit var binding: FragmentHolidayItemBinding

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
            SongsAdapter(data.holidaySongs) { song, index ->
                play(index, song)
            }

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
}