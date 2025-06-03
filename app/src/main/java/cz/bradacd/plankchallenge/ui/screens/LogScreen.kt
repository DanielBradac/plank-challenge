package cz.bradacd.plankchallenge.ui.screens

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
    var dialogState by remember { mutableStateOf<DialogAction?>(null) }

    LaunchedEffect(Unit) {
        viewModel.onEntry(context)
    }

    LogList(log = log,
        onDeleteRequest = { dialogState = DialogAction.Delete(it) },
        onShareRequest = { dialogState = DialogAction.Share(it) })

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
        }

        ConfirmDialog(title = title, message = message, confirmText = confirmText, onConfirm = {
            onConfirm()
            dialogState = null
        }, onDismiss = { dialogState = null })
    }
}

@Composable
fun LogList(
    log: List<LogRecord>, onDeleteRequest: (LogRecord) -> Unit, onShareRequest: (LogRecord) -> Unit
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        log.forEach { record ->
            LogRecordItem(record = record,
                onDelete = { onDeleteRequest(record) },
                onShare = { onShareRequest(record) })
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

sealed class DialogAction(val record: LogRecord) {
    class Delete(record: LogRecord) : DialogAction(record)
    class Share(record: LogRecord) : DialogAction(record)
}

data class DialogInput<A, B, C, D>(val title: A, val confirmText: B, val message: C, val onConfirm: D)
