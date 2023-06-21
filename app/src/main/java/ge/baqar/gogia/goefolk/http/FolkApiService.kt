package ge.baqar.gogia.goefolk.http

import ge.baqar.gogia.goefolk.model.Artist
import ge.baqar.gogia.goefolk.model.SongsResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface FolkApiService {
    @GET("artists/2/all")
    suspend fun ensembles(): MutableList<Artist>

    @GET("artists/{id}")
    suspend fun ensemble(@Path("id") id: String): Artist

    @GET("artists/1/all")
    suspend fun oldRecordings(): MutableList<Artist>

    @GET("songs/{artistId}/all")
    suspend fun songs(@Path("artistId") artistId: String): SongsResponse

    @GET("songs/{id}/file")
    suspend fun downloadSongFile(@Path("id") id: String): Response<ResponseBody>
}