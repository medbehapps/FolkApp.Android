package ge.baqar.gogia.goefolk.http.response

import ge.baqar.gogia.goefolk.model.HolidaySong
import ge.baqar.gogia.goefolk.model.Song

data class DashboardDataResponse(val daySong: Song, val dayChant: Song, val holidaySongData: HolidaySongData)

data class HolidaySongData(val title: String, val holidaySongs: HolidaySong);