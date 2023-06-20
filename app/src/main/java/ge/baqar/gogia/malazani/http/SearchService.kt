package ge.baqar.gogia.malazani.http

import ge.baqar.gogia.malazani.model.SearchResult
import retrofit2.http.GET
import retrofit2.http.Path

interface SearchService {
    @GET("search/{term}")
    suspend fun search(@Path("term") term: String): SearchResult
}