package ge.baqar.gogia.goefolk.model

interface DomainError {
    val message: String?
    val exception: Exception?
}