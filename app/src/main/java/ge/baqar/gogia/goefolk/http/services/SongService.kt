package ge.baqar.gogia.goefolk.http.services

import ge.baqar.gogia.goefolk.http.response.ResponseBase
import ge.baqar.gogia.goefolk.model.Song
import ge.baqar.gogia.goefolk.model.SongsResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface SongService {

    @GET("songs/{artistId}/all")
    suspend fun songs(@Path("artistId") artistId: String): ResponseBase<SongsResponse>

    @GET("songs/{id}/file")
    suspend fun downloadSongFile(@Path("id") id: String): Response<ResponseBody>

    @PUT("songs/{id}/mark-as-favourite")
    suspend fun markSongAsFavourite(@Path("id") id: String): ResponseBase<Boolean>

    @GET("songs/favourites")
    suspend fun favourites(): ResponseBase<MutableList<Song>>
}