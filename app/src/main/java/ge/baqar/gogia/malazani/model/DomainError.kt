package ge.baqar.gogia.malazani.model

interface DomainError {
    val message: String?
    val exception: Exception?
}