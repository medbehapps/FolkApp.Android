package ge.baqar.gogia.malazani.model

data class ConnectionError(
    override val message: String,
    override val exception: Exception
) : DomainError