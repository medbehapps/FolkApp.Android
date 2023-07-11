package ge.baqar.gogia.goefolk.http.services

import ge.baqar.gogia.goefolk.http.response.DashboardDataResponse
import ge.baqar.gogia.goefolk.http.response.ResponseBase
import ge.baqar.gogia.goefolk.model.SongsResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface DashboardService {

    @GET("dashboard")
    suspend fun dashboardData(@Query("date") date: String): ResponseBase<DashboardDataResponse>
}