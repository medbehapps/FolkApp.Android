package ge.baqar.gogia.goefolk.utility

import ge.baqar.gogia.goefolk.model.DownloadableSong
import ge.baqar.gogia.goefolk.model.Song
import ge.baqar.gogia.goefolk.storage.model.DbSong
import java.util.UUID


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

fun Song. asDownloadable(): DownloadableSong {
    return DownloadableSong(
        this.id,
        this.name,
        this.nameEng,
        this.path,
        this.songType,
        this.artistId
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