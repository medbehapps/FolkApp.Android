package ge.baqar.gogia.goefolk.http.response

data class ResponseBase<TBody>(val body: TBody?, val error: BaseError?)