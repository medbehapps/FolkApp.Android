package ge.baqar.gogia.goefolk.ui.search

import ge.baqar.gogia.goefolk.model.SearchResult

//Actions
open class SearchAction

open class DoSearch(val term: String) : SearchAction()
object ClearSearchResult: SearchAction()
