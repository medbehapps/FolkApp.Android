package ge.baqar.gogia.malazani.utility

import ge.baqar.gogia.malazani.storage.model.DbEnsemble
import ge.baqar.gogia.malazani.storage.model.DbSong
import ge.baqar.gogia.malazani.model.DownloadableSong
import ge.baqar.gogia.malazani.model.Artist
import ge.baqar.gogia.malazani.model.Song
import java.util.*


fun DownloadableSong.toDb(): DbSong {
    return DbSong(
        UUID.randomUUID().toString(),
        this.id,
        this.name,
        this.nameEng,
        this.link,
        this.ensembleId,
        this.songType,
        "",
        false
    )
}


fun Song.toDb(isCurrentPlaying: Boolean = false): DbSong {
    return DbSong(
        UUID.randomUUID().toString(),
        this.id,
        this.name,
        this.nameEng,
        this.path,
        this.ensembleId,
        this.songType,
        "",
        isCurrentPlaying
    )
}

fun Song.asDownloadable(): DownloadableSong {
    return DownloadableSong(
        this.id,
        this.name,
        this.nameEng,
        this.path,
        this.songType,
        this.ensembleId
    )
}

fun DbSong.toModel(ensembleName: String, data: ByteArray?): Song {
    return Song(
        this.referenceId,
        this.name,
        this.nameEng,
        this.filePath,
        this.songType,
        this.ensembleId,
        ensembleName,
        false,
        data = data,
        isFav = true
    )
}



fun Artist.toDb(isCurrentPlaying: Boolean = false): DbEnsemble {
    return DbEnsemble(
        UUID.randomUUID().toString(),
        this.id,
        this.name,
        this.nameEng,
        this.artistType,
        isCurrentPlaying
    )
}


fun DbEnsemble.toModel(): Artist {
    return Artist(
        this.id,
        this.name,
        this.nameEng,
        this.artistType,
        this.isCurrent
    )
}