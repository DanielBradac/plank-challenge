package cz.bradacd.plankchallenge

import java.util.Locale

fun Int.formatTimeFromSeconds(): String {
    val totalSeconds = this
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.US, "%02d:%02d", minutes, seconds)
}