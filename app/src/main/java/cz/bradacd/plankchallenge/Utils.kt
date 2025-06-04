package cz.bradacd.plankchallenge

import java.time.LocalDate
import java.time.ZoneId
import java.util.Locale

fun Int.formatTimeFromSeconds(): String {
    val totalSeconds = this
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.US, "%02d:%02d", minutes, seconds)
}

fun dateStringToMillis(dateString: String): Long {
    // Parse input like "6.6." or "15.6." and assume year 2025
    val parts = dateString.trim().removeSuffix(".").split(".")
    if (parts.size != 2) throw IllegalArgumentException("Invalid format. Use 'd.M.'")

    val day = parts[0].toInt()
    val month = parts[1].toInt()

    val date = LocalDate.of(2025, month, day)
    return date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
}