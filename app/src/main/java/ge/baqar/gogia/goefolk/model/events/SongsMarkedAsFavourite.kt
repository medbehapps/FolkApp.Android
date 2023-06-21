package ge.baqar.gogia.goefolk.model.events

import ge.baqar.gogia.goefolk.model.DownloadableSong

data class SongsMarkedAsFavourite(val songs: MutableList<DownloadableSong>)

data class SongsUnmarkedAsFavourite(val songs: MutableList<DownloadableSong>)

