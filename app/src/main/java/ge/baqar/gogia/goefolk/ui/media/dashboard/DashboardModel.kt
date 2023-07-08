package ge.baqar.gogia.goefolk.ui.media.dashboard

import ge.baqar.gogia.goefolk.model.HolidaySong
import ge.baqar.gogia.goefolk.model.Song

data class DashboardModel(val daySong: Song?,
                          val dayChant: Song?,
                          val upcomingHoliday: String?,
                          val holidaySongs: Array<HolidaySong>?)