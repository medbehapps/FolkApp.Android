package ge.baqar.gogia.goefolk.storage.utils

fun String?.nullIfEmpty(): String? {
    return this?.takeUnless { it.isEmpty() }
}

fun String.suffixOrEmpty(): String {
    return substringAfterLast(DOT, EMPTY_STRING)
}

fun String.endsWithMp3(): Boolean {
    return this.endsWith(".mp3")
}