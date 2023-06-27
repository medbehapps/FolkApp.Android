package ge.baqar.gogia.goefolk.model.events

import ge.baqar.gogia.goefolk.media.MediaPlayerController
import kotlinx.coroutines.InternalCoroutinesApi
import kotlin.time.ExperimentalTime

class RequestMediaControllerInstance @OptIn(ExperimentalTime::class,
    InternalCoroutinesApi::class
) constructor(var mediaPlayerController: MediaPlayerController)