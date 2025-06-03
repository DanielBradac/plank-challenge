package cz.bradacd.plankchallenge.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.bradacd.plankchallenge.LogRepository.Log
import cz.bradacd.plankchallenge.LogRepository.LogRecord
import cz.bradacd.plankchallenge.LogRepository.delete
import cz.bradacd.plankchallenge.LogRepository.loadLogEntries
import cz.bradacd.plankchallenge.SheetsClient.getPersonData
import cz.bradacd.plankchallenge.SheetsClient.writePersonEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.net.URL
import java.time.Instant

class LogViewModel : ViewModel() {
    private val _log: MutableStateFlow<List<LogRecord>> = MutableStateFlow(emptyList())
    val log: StateFlow<List<LogRecord>> = _log

    fun onEntry(context: Context) {
        _log.value = loadLogEntries(context)

        viewModelScope.launch(Dispatchers.IO) {
            writePersonEntry(context, "1DkCBvTKXI61LB5D9O-dIym56QnwP8wbA5ZgsrqM2RlQ", "plank", "Stano", LogRecord(
                "as",
                Instant.now().plusSeconds(10000).toEpochMilli(),
                1500
            ))
        }
    }

    fun delete(context: Context, record: LogRecord) {
        record.delete(context)
        _log.value = loadLogEntries(context)


    }
}