package ge.baqar.gogia.gefolk.http.service_implementations

import ge.baqar.gogia.gefolk.http.response.BaseError
import ge.baqar.gogia.gefolk.http.services.SearchService
import ge.baqar.gogia.gefolk.model.ReactiveResult
import ge.baqar.gogia.gefolk.model.SearchResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class SearchServiceImpl(
    private var searchService: SearchService
) : ServiceBase() {

    suspend fun search(term: String): Flow<ReactiveResult<BaseError, SearchResult>> {
        return coroutineScope {
            val searchResult = searchService.search(term)
            val flow = callbackFlow {
                trySend(mapToReactiveResult(searchResult))
                awaitClose { channel.close() }
            }
            return@coroutineScope flow
        }
    }
}