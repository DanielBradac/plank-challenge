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
import cz.bradacd.plankchallenge.LocalRepository.LogRecord
import cz.bradacd.plankchallenge.LocalRepository.save
import java.time.Instant
import java.util.UUID

enum class StopWatchState {
    Ready,
    CountDown,
    Running,
    Stopped
}


class StopWatchViewModel : ViewModel() {

    private val _stopWatchState: MutableStateFlow<StopWatchState> =
        MutableStateFlow(StopWatchState.Ready)
    val stopWatchState: StateFlow<StopWatchState> = _stopWatchState

    // Elapsed time in tenths of a second
    private val _elapsedSeconds = MutableStateFlow(0)
    val elapsedSeconds: StateFlow<Int> = _elapsedSeconds

    // Elapsed time in tenths of a second
    private val _countDownSeconds = MutableStateFlow(5)
    val countDownSeconds: StateFlow<Int> = _countDownSeconds

    private var timerJob: Job? = null
    private var countDownJob: Job? = null
    private var startTime: Long = 0L

    private fun resetStopWatch() {
        _elapsedSeconds.value = 0
    }

    fun startStopWatch() {
        countDownJob?.cancel()
        startTime = SystemClock.elapsedRealtime()
        _stopWatchState.value = StopWatchState.Running

        timerJob = viewModelScope.launch {
            while (true) {
                val now = SystemClock.elapsedRealtime()
                val secondsElapsed = ((now - startTime) / 1000).toInt()
                _elapsedSeconds.value = secondsElapsed
                delay(1000L)
            }
        }
    }

    fun startCountDown() {
        countDownJob = viewModelScope.launch {
            _countDownSeconds.value = 5
            while (countDownSeconds.value > 0) {
                delay(1000L)
                _countDownSeconds.value -= 1
            }
            startStopWatch()
        }
    }

    fun stopStopWatch(context: Context) {
        timerJob?.cancel()
        println("Current time: ${Instant.now()}")
        LogRecord(
            id = UUID.randomUUID().toString(),
            dateMillis = Instant.now().toEpochMilli(),
            elapsedSeconds = elapsedSeconds.value
        ).save(context)
    }

    fun onTap(context: Context) {
        when (stopWatchState.value) {
            StopWatchState.Stopped -> {
                resetStopWatch()
                _stopWatchState.value = StopWatchState.Ready
            }
            StopWatchState.Running -> {
                stopStopWatch(context)
                _stopWatchState.value = StopWatchState.Stopped
            }
            StopWatchState.Ready -> {
                startCountDown()
                _stopWatchState.value = StopWatchState.CountDown
            }
            StopWatchState.CountDown -> {
                _stopWatchState.value = StopWatchState.Ready
                countDownJob?.cancel()
            }
        }
    }
}