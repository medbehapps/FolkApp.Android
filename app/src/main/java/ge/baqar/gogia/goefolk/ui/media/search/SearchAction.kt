package ge.baqar.gogia.goefolk.ui.media.search

//Actions
open class SearchAction
open class DoSearch(val term: String) : SearchAction()
object ClearSearchResult: SearchAction()
