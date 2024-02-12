package ge.baqar.gogia.gefolk.ui.account.login

import ge.baqar.gogia.gefolk.model.Artist

open class LoginActions
data class LoginResul(val artists: MutableList<Artist>) : LoginActions()
class LoginRequested(val email: String, val password: String, val deviceId: String) : LoginActions()
class LoginByTokenRequested(val token: String, val deviceId: String) : LoginActions()