package ge.baqar.gogia.gefolk.http.response

import ge.baqar.gogia.gefolk.model.Song

data class DashboardDataResponse(val daySong: Song, val dayChant: Song, val holidayItems: MutableList<HolidaySongData>)

data class HolidaySongData(val title: String, val imagePath: String, val holidaySongs: MutableList<Song>);