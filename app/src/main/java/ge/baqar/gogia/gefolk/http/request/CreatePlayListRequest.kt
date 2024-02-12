package ge.baqar.gogia.gefolk.http.request

data class CreatePlayListRequest(val name: String, val songs: List<String>)