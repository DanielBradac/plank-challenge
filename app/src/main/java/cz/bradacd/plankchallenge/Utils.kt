package cz.bradacd.plankchallenge

import java.util.Locale

fun Int.formatTimeFromTenths(): String {
    val totalSeconds = this / 10
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val tenths = this % 10
    return String.format(Locale.US, "%02d:%02d.%d", minutes, seconds, tenths)
}