package ge.baqar.gogia.gefolk.model.events

import ge.baqar.gogia.gefolk.model.DownloadableSong

data class SongsMarkedAsFavourite(val songs: MutableList<DownloadableSong>)

data class SongsUnmarkedAsFavourite(val songs: MutableList<DownloadableSong>)

