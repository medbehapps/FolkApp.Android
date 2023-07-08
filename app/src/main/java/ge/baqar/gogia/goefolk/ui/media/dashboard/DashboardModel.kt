package ge.baqar.gogia.goefolk.ui.media.dashboard

import ge.baqar.gogia.goefolk.model.Song

data class DashboardModel(var daySong: Song?,
                          var dayChant: Song?,
                          var upcomingHoliday: String?,
                          var holidaySongs: Array<HolidaySong>?)