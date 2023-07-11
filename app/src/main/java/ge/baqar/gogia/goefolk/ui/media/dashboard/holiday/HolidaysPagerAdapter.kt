package ge.baqar.gogia.goefolk.ui.media.dashboard.holiday

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import ge.baqar.gogia.goefolk.http.response.HolidaySongData


class HolidaysPagerAdapter(
    private val holidayItems: MutableList<HolidaySongData>,
    fragmentManager: FragmentManager
) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getCount(): Int {
        return holidayItems.size
    }

    override fun getItem(position: Int): Fragment {
        val item = holidayItems[position]
        return newInstance(item)
    }

    private fun newInstance(item: HolidaySongData): Fragment {
        val fragment = HolidayItemFragment()
        fragment.data = item
        return fragment
    }
}