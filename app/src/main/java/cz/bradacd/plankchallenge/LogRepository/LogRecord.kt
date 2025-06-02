package cz.bradacd.plankchallenge.LogRepository

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
    @SerializedName("date") val date: Instant,
    @SerializedName("timeMilis") val timeMilis: Long
)

private val gson = Gson()

fun LogRecord.save(context: Context) {
    val currentLogEntries = loadLogEntries(context)
    saveLog(
        context = context,
        newLog = Log(currentLogEntries + this)
    )
}

fun LogRecord.delete(context: Context) {
    val currentLogEntries = loadLogEntries(context)
    saveLog(
        context = context,
        newLog = Log(currentLogEntries.filter { id != this.id })
    )
}

fun loadLogEntries(context: Context): List<LogRecord> {
    val sharedPreferences = getPreferences(context)

    val logJson = sharedPreferences.getString("PlankLog", null)

    return if (logJson != null) {
        gson.fromJson(logJson, Log::class.java).logEntries
    } else {
        emptyList()
    }
}

private fun saveLog(context: Context, newLog: Log) {
    val sharedPreferences = getPreferences(context)
    val editor = sharedPreferences.edit()
    gson.toJson(newLog)
    editor.putString("PlankLog", Gson().toJson(newLog))
    editor.apply()
}

private fun getPreferences(context: Context): SharedPreferences {
    return context.getSharedPreferences("PlankLog", Context.MODE_PRIVATE)
}
