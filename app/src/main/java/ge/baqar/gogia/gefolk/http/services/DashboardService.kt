package ge.baqar.gogia.gefolk.http.services

import ge.baqar.gogia.gefolk.http.response.DashboardDataResponse
import ge.baqar.gogia.gefolk.http.response.ResponseBase
import retrofit2.http.GET
import retrofit2.http.Query

interface DashboardService {

    @GET("dashboard")
    suspend fun dashboardData(@Query("date") date: String): ResponseBase<DashboardDataResponse>
}