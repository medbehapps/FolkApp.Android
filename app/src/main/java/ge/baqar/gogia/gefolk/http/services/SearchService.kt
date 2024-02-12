package ge.baqar.gogia.gefolk.http.services

import ge.baqar.gogia.gefolk.http.response.ResponseBase
import ge.baqar.gogia.gefolk.model.SearchResult
import retrofit2.http.GET
import retrofit2.http.Path

interface SearchService {
    @GET("search/{term}")
    suspend fun search(@Path("term") term: String): ResponseBase<SearchResult>
}