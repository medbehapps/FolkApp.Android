package ge.baqar.gogia.gefolk.http.services

import ge.baqar.gogia.gefolk.http.response.ResponseBase
import ge.baqar.gogia.gefolk.model.Artist
import retrofit2.http.GET
import retrofit2.http.Path

interface ArtistsService {
    @GET("artists/2/all")
    suspend fun ensembles(): ResponseBase<MutableList<Artist>>

    @GET("artists/{id}")
    suspend fun ensemble(@Path("id") id: String): ResponseBase<Artist>

    @GET("artists/1/all")
    suspend fun oldRecordings(): ResponseBase<MutableList<Artist>>
}