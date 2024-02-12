package ge.baqar.gogia.gefolk.ui.media

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import ge.baqar.gogia.gefolk.media.FolkPlayerController
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.android.ext.android.inject
import kotlin.time.ExperimentalTime

@SuppressLint("UnsafeOptInUsageError")
@OptIn(InternalCoroutinesApi::class, ExperimentalTime::class)
open class AuthorizedFragment: Fragment() {

    val folkPlayerController: FolkPlayerController by inject()
    val authorizedActivity: AuthorizedActivity by lazy {
        activity as AuthorizedActivity
    }
}