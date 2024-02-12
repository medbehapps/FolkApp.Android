package ge.baqar.gogia.gefolk.ui.account.register

import ge.baqar.gogia.gefolk.ui.ReactiveViewModel
import ge.baqar.gogia.gefolk.http.request.RegisterAccountRequest
import ge.baqar.gogia.gefolk.http.request.VerifyAccountRequest
import ge.baqar.gogia.gefolk.http.service_implementations.AccountServiceImpl
import ge.baqar.gogia.gefolk.model.FailedResult
import ge.baqar.gogia.gefolk.model.SucceedResult
import kotlinx.coroutines.flow.Flow

class RegisterViewModel(private val accountService: AccountServiceImpl) :
    ReactiveViewModel<RegisterActions, RegisterResult, RegisterState>(
        RegisterState.DEFAULT
    ) {
    override fun RegisterActions.process(): Flow<() -> RegisterResult> {
        return when (this) {
            is RegisterRequested -> update {
                emit {
                    RegisterState.DEFAULT
                }
                accountService.register(
                    RegisterAccountRequest(
                        email!!,
                        firstName!!,
                        lastName!!,
                        password!!
                    )
                ).collect { result ->
                    if (result is SucceedResult) {
                        emit {
                            state.copy(isInProgress = false, accountId = result.value, error = null)
                        }
                        return@collect
                    }

                    if (result is FailedResult) {
                        emit {
                            state.copy(
                                isInProgress = false,
                                accountId = null,
                                error = result.value.message
                            )
                        }
                        return@collect
                    }
                }
            }

            is VerificationRequested -> update {
                emit {
                    RegisterState.DEFAULT
                }

                accountService.verify(VerifyAccountRequest(code), accountId)
                    .collect { result ->
                        if (result is SucceedResult) {
                            emit {
                                state.copy(
                                    isInProgress = false,
                                    verified = result.value,
                                    accountId = null,
                                    error = null
                                )
                            }
                            return@collect
                        }

                        if (result is FailedResult) {
                            emit {
                                state.copy(
                                    isInProgress = false,
                                    accountId = null,
                                    error = result.value.message
                                )
                            }
                            return@collect
                        }
                    }
            }

            else -> update {

            }
        }
    }

}