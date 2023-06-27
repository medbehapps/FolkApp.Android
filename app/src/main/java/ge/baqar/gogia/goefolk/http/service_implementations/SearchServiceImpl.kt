package ge.baqar.gogia.goefolk.http.service_implementations

import ge.baqar.gogia.goefolk.http.services.SearchService
import ge.baqar.gogia.goefolk.model.ReactiveResult
import ge.baqar.gogia.goefolk.model.SearchResult
import ge.baqar.gogia.goefolk.model.asError
import ge.baqar.gogia.goefolk.model.asSuccess
import ge.baqar.gogia.goefolk.utility.NetworkStatus
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf

class SearchServiceImpl(
    private var networkStatus: NetworkStatus,
    private var searchService: SearchService
) {


    suspend fun search(term: String): Flow<ReactiveResult<String, SearchResult>> {
        return coroutineScope {
            if (networkStatus.isOnline()) {
                val searchResult = searchService.search(term)
                val flow = callbackFlow<ReactiveResult<String, SearchResult>> {
                    trySend(searchResult.asSuccess)
                    awaitClose { channel.close() }
                }
                return@coroutineScope flow
            } else {
                return@coroutineScope flowOf("network_is_off".asError)
            }
        }
    }
}