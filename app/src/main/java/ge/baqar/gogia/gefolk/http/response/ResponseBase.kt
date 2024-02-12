package ge.baqar.gogia.gefolk.http.response

data class ResponseBase<TBody>(val body: TBody?, val error: BaseError?)