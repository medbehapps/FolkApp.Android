package ge.baqar.gogia.gefolk.ui.account.register

import ge.baqar.gogia.gefolk.http.service_implementations.AccountServiceImpl
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


@InternalCoroutinesApi
val registerModule = module {
    single { AccountServiceImpl(get()) }
    viewModel { RegisterViewModel(get()) }
}