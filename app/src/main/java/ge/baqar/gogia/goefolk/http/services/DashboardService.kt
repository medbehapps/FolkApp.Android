package ge.baqar.gogia.goefolk.http.services

import ge.baqar.gogia.goefolk.http.response.DashboardDataResponse
import ge.baqar.gogia.goefolk.http.response.ResponseBase
import ge.baqar.gogia.goefolk.model.SongsResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface DashboardService {

    @GET("dashboard")
    suspend fun dashboardData(): ResponseBase<DashboardDataResponse>
}