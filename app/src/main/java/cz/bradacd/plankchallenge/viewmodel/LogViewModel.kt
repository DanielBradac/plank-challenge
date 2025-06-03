package cz.bradacd.plankchallenge.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.bradacd.plankchallenge.LocalRepository.LogRecord
import cz.bradacd.plankchallenge.LocalRepository.delete
import cz.bradacd.plankchallenge.LocalRepository.loadLogEntries
import cz.bradacd.plankchallenge.LocalRepository.loadSettingsValidated
import cz.bradacd.plankchallenge.SheetsClient.writePersonEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.Instant

class LogViewModel : ViewModel() {
    private val _log: MutableStateFlow<List<LogRecord>> = MutableStateFlow(emptyList())
    val log: StateFlow<List<LogRecord>> = _log

    fun onEntry(context: Context) {
        _log.value = loadLogEntries(context)
    }

    fun delete(context: Context, record: LogRecord) {
        record.delete(context)
        _log.value = loadLogEntries(context)
    }

    fun share(context: Context, record: LogRecord) {
        viewModelScope.launch(Dispatchers.IO) {
            writePersonEntry(context, record, loadSettingsValidated(context))
        }
    }
}