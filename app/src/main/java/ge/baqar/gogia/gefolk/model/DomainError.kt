package ge.baqar.gogia.gefolk.model

interface DomainError {
    val message: String?
    val exception: Exception?
}