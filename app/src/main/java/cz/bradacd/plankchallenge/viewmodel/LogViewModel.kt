package cz.bradacd.plankchallenge.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.bradacd.plankchallenge.LocalRepository.Log
import cz.bradacd.plankchallenge.LocalRepository.LogRecord
import cz.bradacd.plankchallenge.LocalRepository.delete
import cz.bradacd.plankchallenge.LocalRepository.loadLogEntries
import cz.bradacd.plankchallenge.LocalRepository.loadSettingsValidated
import cz.bradacd.plankchallenge.LocalRepository.saveLog
import cz.bradacd.plankchallenge.SheetsClient.getPersonData
import cz.bradacd.plankchallenge.SheetsClient.writePersonEntry
import cz.bradacd.plankchallenge.dateStringToMillis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID

class LogViewModel : ViewModel() {
    private val _log: MutableStateFlow<List<LogRecord>> = MutableStateFlow(emptyList())
    val log: StateFlow<List<LogRecord>> = _log

    private val _loading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _toastEvent = Channel<String>()
    val toastEvent = _toastEvent.receiveAsFlow()

    fun onEntry(context: Context) {
        _log.value = loadLogEntries(context)
    }

    fun delete(context: Context, record: LogRecord) {
        record.delete(context)
        _log.value = loadLogEntries(context)
    }

    fun share(context: Context, record: LogRecord) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                writePersonEntry(context, record, loadSettingsValidated(context))
                _toastEvent.send("Plank uploaded successfully!")
            } catch (e: Exception) {
                _toastEvent.send("Upload failed: ${e.localizedMessage ?: "Unknown error"}")
            }
        }
    }

    fun pullDataFromSheet(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            _loading.value = true
            try {
                val data = getPersonData(context, loadSettingsValidated(context))

                val newLog = Log(
                    logEntries = data.entries.map {
                        LogRecord(
                            dateMillis = dateStringToMillis(it.key),
                            elapsedSeconds = it.value,
                            id = UUID.randomUUID().toString()
                        )
                    }.sortedBy { it.dateMillis }
                )

                saveLog(context, newLog)
                _log.value = loadLogEntries(context)

                _toastEvent.send("Data synchronized successfully!")
            } catch (e: Exception) {
                _toastEvent.send("Sync failed: ${e.localizedMessage ?: "Unknown error"}")
            } finally {
                _loading.value = false
            }
        }
    }
}