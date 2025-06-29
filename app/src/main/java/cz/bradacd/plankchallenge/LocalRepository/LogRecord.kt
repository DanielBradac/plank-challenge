package cz.bradacd.plankchallenge.LocalRepository

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.time.Instant

data class Log(
    @SerializedName("logEntries") val logEntries: List<LogRecord>
)

data class LogRecord(
    @SerializedName("id") val id: String,
    @SerializedName("date") val dateMillis: Long,
    @SerializedName("elapsedSeconds") val elapsedSeconds: Int
) {
    val date: Instant get() = Instant.ofEpochMilli(dateMillis)
}

private val gson = Gson()
private const val storageName = "PlankLog"

fun LogRecord.save(context: Context) {
    val currentLogEntries = loadLogEntries(context)
    saveLog(
        context = context,
        newLog = Log(currentLogEntries + this)
    )
}

fun LogRecord.delete(context: Context) {
    val currentLogEntries = loadLogEntries(context)
    val newLogDataEntries = currentLogEntries.filter { it.id != this.id }.toMutableList()
    saveLog(
        context = context,
        newLog = Log(newLogDataEntries)
    )
}

fun loadLogEntries(context: Context): List<LogRecord> {
    val sharedPreferences = getPreferences(context)

    val logJson = sharedPreferences.getString(storageName, null)

    return if (logJson != null) {
        gson.fromJson(logJson, Log::class.java).logEntries.sortedBy { it.dateMillis }
    } else {
        emptyList()
    }
}

fun saveLog(context: Context, newLog: Log) {
    val sharedPreferences = getPreferences(context)
    val editor = sharedPreferences.edit()
    gson.toJson(newLog)
    editor.putString(storageName, Gson().toJson(newLog))
    editor.apply()
}

private fun getPreferences(context: Context): SharedPreferences {
    return context.getSharedPreferences(storageName, Context.MODE_PRIVATE)
}
