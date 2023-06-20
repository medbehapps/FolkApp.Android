package ge.baqar.gogia.malazani.model.events

import ge.baqar.gogia.malazani.model.DownloadableSong

data class SongsMarkedAsFavourite(val songs: MutableList<DownloadableSong>)

data class SongsUnmarkedAsFavourite(val songs: MutableList<DownloadableSong>)

