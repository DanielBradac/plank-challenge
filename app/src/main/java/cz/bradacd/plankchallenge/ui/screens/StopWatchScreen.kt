package cz.bradacd.plankchallenge.ui.screens

import android.content.Context
import android.os.PowerManager
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cz.bradacd.plankchallenge.formatTimeFromSeconds
import cz.bradacd.plankchallenge.viewmodel.StopWatchState
import cz.bradacd.plankchallenge.viewmodel.StopWatchViewModel

@Composable
fun StopWatchScreen(viewModel: StopWatchViewModel = viewModel()) {
    val context = LocalContext.current

    val stopWatchState by viewModel.stopWatchState.collectAsState()
    val elapsedSeconds by viewModel.elapsedSeconds.collectAsState()
    val countDownSeconds by viewModel.countDownSeconds.collectAsState()

    // Keep the screen alive on stopwatch
    val lifecycleOwner = LocalLifecycleOwner.current
    val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    val wakeLock = remember { powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "Plank::WakeLock") }
    DisposableEffect(lifecycleOwner) {
        wakeLock.acquire(30 * 60 * 1000L) // 30 minutes
        onDispose {
            wakeLock.release()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        viewModel.onTap(context)
                    }
                )
            }
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val displayedTime = if (stopWatchState == StopWatchState.CountDown) {
                countDownSeconds
            } else {
                elapsedSeconds
            }

            Text(
                text = displayedTime.formatTimeFromSeconds(),
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = getTitleByState(stopWatchState),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

fun getTitleByState(state: StopWatchState): String {
    return when(state) {
        StopWatchState.Ready -> "Tap to start"
        StopWatchState.CountDown -> "Get ready"
        StopWatchState.Running -> "Tap to stop"
        StopWatchState.Stopped -> "Tap to reset run"
    }
}

