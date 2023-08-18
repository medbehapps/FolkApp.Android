package ge.baqar.gogia.goefolk.http.request

data class CreatePlayListRequest(val name: String, val songs: List<String>)