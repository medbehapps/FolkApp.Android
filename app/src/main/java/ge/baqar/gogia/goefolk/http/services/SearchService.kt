package ge.baqar.gogia.goefolk.http.services

import ge.baqar.gogia.goefolk.model.SearchResult
import retrofit2.http.GET
import retrofit2.http.Path

interface SearchService {
    @GET("search/{term}")
    suspend fun search(@Path("term") term: String): SearchResult
}