package cz.bradacd.plankchallenge.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import cz.bradacd.plankchallenge.LocalRepository.Settings
import cz.bradacd.plankchallenge.LocalRepository.loadSettings
import cz.bradacd.plankchallenge.LocalRepository.save
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel : ViewModel() {
    private val _settings: MutableStateFlow<Settings?> = MutableStateFlow(null)
    val settings: StateFlow<Settings?> = _settings

    fun onEntry(context: Context) {
        _settings.value = loadSettings(context)
    }

    fun saveSettings(context: Context) {
        settings.value?.save(context)
    }

    fun updateSettings(newSettings: Settings) {
        _settings.value = newSettings
    }
}