package cz.bradacd.plankchallenge.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cz.bradacd.plankchallenge.LocalRepository.LogRecord
import cz.bradacd.plankchallenge.formatTimeFromSeconds
import cz.bradacd.plankchallenge.viewmodel.LogViewModel
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun LogScreen(viewModel: LogViewModel = viewModel()) {
    val context = LocalContext.current
    val log by viewModel.log.collectAsState()
    val loading by viewModel.loading.collectAsState()
    var dialogState by remember { mutableStateOf<DialogAction?>(null) }

    LaunchedEffect(Unit) {
        viewModel.onEntry(context)
    }

    LaunchedEffect(Unit) {
        viewModel.toastEvent.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // Scrollable log list
        LogList(
            log = log,
            onDeleteRequest = { dialogState = DialogAction.Delete(it) },
            onShareRequest = { dialogState = DialogAction.Share(it) },
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 72.dp)
        )

        // Sync button
        Button(
            onClick = { dialogState = DialogAction.Sync },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text("Sync data from challenge table")
        }

        // 🔄 Loading overlay
        if (loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f))
                    .align(Alignment.Center)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }

    // Dialog handling
    dialogState?.let { action ->
        val (title, confirmText, message, onConfirm) = when (action) {
            is DialogAction.Delete -> DialogInput(
                "Confirm Delete",
                "Delete",
                "Are you sure you want to delete plank:\n" +
                        "\nDate: ${formattedDate(action.record)}" +
                        "\nDuration: ${action.record.elapsedSeconds.formatTimeFromSeconds()}"
            ) { viewModel.delete(context, action.record) }

            is DialogAction.Share -> DialogInput(
                "Confirm Upload",
                "Upload",
                "Are you sure you want to upload plank:\n" +
                        "\nDate: ${formattedDate(action.record)}" +
                        "\nDuration: ${action.record.elapsedSeconds.formatTimeFromSeconds()}"
            ) { viewModel.share(context, action.record) }

            is DialogAction.Sync -> DialogInput(
                "Confirm Sync",
                "Synchronize",
                "Are you sure you want to pull data from the online sheet?\nExisting data will be overridden."
            ) { viewModel.pullDataFromSheet(context) }
        }

        ConfirmDialog(
            title = title,
            message = message,
            confirmText = confirmText,
            onConfirm = {
                onConfirm()
                dialogState = null
            },
            onDismiss = { dialogState = null }
        )
    }
}

@Composable
fun LogList(
    log: List<LogRecord>,
    onDeleteRequest: (LogRecord) -> Unit,
    onShareRequest: (LogRecord) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        log.forEach { record ->
            LogRecordItem(
                record = record,
                onDelete = { onDeleteRequest(record) },
                onShare = { onShareRequest(record) }
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
        }
    }
}

@Composable
fun LogRecordItem(
    record: LogRecord, onDelete: () -> Unit, onShare: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = formattedDate(record), style = MaterialTheme.typography.titleMedium)
            Text(
                text = "Duration: ${record.elapsedSeconds.formatTimeFromSeconds()}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Row {
            IconButton(onClick = onShare) {
                Icon(Icons.Default.Upload, contentDescription = "Share")
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}

@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(confirmText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        })
}

fun formattedDate(record: LogRecord): String =
    DateTimeFormatter.ofPattern("dd.MM.yyyy").withZone(ZoneId.systemDefault()).format(record.date)

sealed class DialogAction(open val record: LogRecord? = null) {
    data class Delete(override val record: LogRecord) : DialogAction(record)
    data class Share(override val record: LogRecord) : DialogAction(record)
    object Sync : DialogAction(null)
}

data class DialogInput<A, B, C, D>(
    val title: A,
    val confirmText: B,
    val message: C,
    val onConfirm: D
)
