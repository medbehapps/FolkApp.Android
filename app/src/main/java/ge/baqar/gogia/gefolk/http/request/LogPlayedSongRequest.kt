package ge.baqar.gogia.gefolk.http.request

data class LogPlayedSongRequest(val logType: Int)

public val playedLogType = 0
public val downloadLogType = 1