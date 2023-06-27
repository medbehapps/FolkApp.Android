package ge.baqar.gogia.goefolk.http.services

import ge.baqar.gogia.goefolk.model.SongsResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface SongService {

    @GET("songs/{artistId}/all")
    suspend fun songs(@Path("artistId") artistId: String): SongsResponse

    @GET("songs/{id}/file")
    suspend fun downloadSongFile(@Path("id") id: String): Response<ResponseBody>
}