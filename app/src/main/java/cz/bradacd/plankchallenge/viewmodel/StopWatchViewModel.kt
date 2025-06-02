package cz.bradacd.plankchallenge.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.os.SystemClock
import cz.bradacd.plankchallenge.LogRepository.LogRecord
import cz.bradacd.plankchallenge.LogRepository.save
import java.time.Instant
import java.util.UUID

enum class StopWatchState {
    Ready,
    Set,
    Running,
    Stopped
}


class StopWatchViewModel : ViewModel() {

    private val _stopWatchState: MutableStateFlow<StopWatchState> =
        MutableStateFlow(StopWatchState.Ready)
    val stopWatchState: StateFlow<StopWatchState> = _stopWatchState

    // Elapsed time in tenths of a second
    private val _elapsedTenths = MutableStateFlow(0)
    val elapsedTenths: StateFlow<Int> = _elapsedTenths

    private var timerJob: Job? = null
    private var startTime: Long = 0L

    fun resetStopWatch() {
        _elapsedTenths.value = 0
    }

    fun startStopWatch() {
        startTime = SystemClock.elapsedRealtime()
        _stopWatchState.value = StopWatchState.Running

        timerJob = viewModelScope.launch {
            while (true) {
                val now = SystemClock.elapsedRealtime()
                val tenthsElapsed = ((now - startTime) / 100).toInt()
                _elapsedTenths.value = tenthsElapsed
                delay(100L)
            }
        }
    }

    fun stopStopWatch(context: Context) {
        timerJob?.cancel()
        println("Current time: ${Instant.now()}")
        LogRecord(
            id = UUID.randomUUID().toString(),
            dateMillis = Instant.now().toEpochMilli(),
            elapsedTenths = elapsedTenths.value
        ).save(context)
    }

    fun onTap(context: Context) {
        // New run
        if (stopWatchState.value == StopWatchState.Stopped) {
            resetStopWatch()
            _stopWatchState.value = StopWatchState.Ready
        }

        // Run stopped
        if (stopWatchState.value == StopWatchState.Running) {
            stopStopWatch(context)
            _stopWatchState.value = StopWatchState.Stopped
        }
    }

    fun onPressDown() {
        // Run set
        if (stopWatchState.value == StopWatchState.Ready) {
            _stopWatchState.value = StopWatchState.Set
        }
    }

    fun onRelease() {
        // Run started
        if (stopWatchState.value == StopWatchState.Set) {
            startStopWatch()
            _stopWatchState.value = StopWatchState.Running
        }
    }


}