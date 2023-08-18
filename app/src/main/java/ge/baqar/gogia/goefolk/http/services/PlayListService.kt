package ge.baqar.gogia.goefolk.http.services

import ge.baqar.gogia.goefolk.http.request.AddOrRemoveSongToPlayListRequest
import ge.baqar.gogia.goefolk.http.request.CreatePlayListRequest
import ge.baqar.gogia.goefolk.http.response.PlayList
import ge.baqar.gogia.goefolk.http.response.ResponseBase
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface PlayListService {

    @GET("playlist")
    suspend fun list(): ResponseBase<MutableList<PlayList>>

    @POST("playlist")
    suspend fun createNew(
        @Body request: CreatePlayListRequest
    ): ResponseBase<Boolean>

    @PUT("playlist/{id}")
    suspend fun addOrRemoveSong(
        @Path("id") id: String,
        @Body request: AddOrRemoveSongToPlayListRequest
    ): ResponseBase<Boolean>

    @DELETE("playlist/{id}")
    suspend fun delete(
        @Path("id") id: String
    ): ResponseBase<Boolean>
}