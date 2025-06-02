package cz.bradacd.plankchallenge.ui.screens

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cz.bradacd.plankchallenge.formatTimeFromTenths
import cz.bradacd.plankchallenge.viewmodel.StopWatchState
import cz.bradacd.plankchallenge.viewmodel.StopWatchViewModel

@Composable
fun StopWatchScreen(viewModel: StopWatchViewModel = viewModel()) {
    val context = LocalContext.current

    val stopWatchState by viewModel.stopWatchState.collectAsState()
    val elapsedTenths by viewModel.elapsedTenths.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        viewModel.onPressDown()
                        tryAwaitRelease()
                        viewModel.onRelease()
                    },
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
            Text(
                text = elapsedTenths.formatTimeFromTenths(),
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
        StopWatchState.Ready -> "Press and hold the screen when you're ready"
        StopWatchState.Set -> "Release to start"
        StopWatchState.Running -> "Tap to stop"
        StopWatchState.Stopped -> "Tap to reset run"
    }
}

