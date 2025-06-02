package cz.bradacd.plankchallenge.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import cz.bradacd.plankchallenge.LogRepository.Log
import cz.bradacd.plankchallenge.LogRepository.LogRecord
import cz.bradacd.plankchallenge.LogRepository.delete
import cz.bradacd.plankchallenge.LogRepository.loadLogEntries
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

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
}