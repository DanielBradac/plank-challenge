package cz.bradacd.plankchallenge.LocalRepository

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import cz.bradacd.plankchallenge.InvalidSettingsException
import java.time.Instant

data class Settings(
    @SerializedName("sheetId") val sheetId: String,
    @SerializedName("sheetName") val sheetName: String,
    @SerializedName("personName") val personName: String
)

private val gson = Gson()
private const val storageName = "PlankAppSettings"

fun Settings.save(context: Context) {
    val sharedPreferences = getPreferences(context)
    val editor = sharedPreferences.edit()
    editor.putString(storageName, Gson().toJson(this))
    editor.apply()
}

fun loadSettings(context: Context): Settings {
    val sharedPreferences = getPreferences(context)
    val settingsJson = sharedPreferences.getString(storageName, null)

    return if (settingsJson != null) {
        gson.fromJson(settingsJson, Settings::class.java)
    } else {
        Settings("", "", "")
    }
}

fun loadSettingValidatedBasic(context: Context): Settings {
    val settings = loadSettings(context)

    if (settings.sheetId.isBlank()) {
        throw InvalidSettingsException("Invalid settings - sheetId is not filled in.")
    }

    if (settings.sheetName.isBlank()) {
        throw InvalidSettingsException("Invalid settings - sheetName is not filled in.")
    }

    return settings
}

fun loadSettingsValidatedFull(context: Context): Settings {
    loadSettingValidatedBasic(context)

    val settings = loadSettings(context)
    if (settings.personName.isBlank()) {
        throw InvalidSettingsException("Invalid settings - name is not filled in.")
    }

    return settings
}

private fun getPreferences(context: Context): SharedPreferences {
    return context.getSharedPreferences(storageName, Context.MODE_PRIVATE)
}