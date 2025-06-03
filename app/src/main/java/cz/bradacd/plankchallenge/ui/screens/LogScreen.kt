package cz.bradacd.plankchallenge.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cz.bradacd.plankchallenge.LogRepository.LogRecord
import cz.bradacd.plankchallenge.formatTimeFromSeconds
import cz.bradacd.plankchallenge.viewmodel.LogViewModel
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun LogScreen(viewModel: LogViewModel = viewModel()) {
    val context = LocalContext.current
    val log by viewModel.log.collectAsState()
    var itemToDelete by remember { mutableStateOf<LogRecord?>(null) }

    LaunchedEffect(Unit) {
        viewModel.onEntry(context)
    }

    LogList(
        log = log,
        onDeleteRequest = { itemToDelete = it }
    )

    itemToDelete?.let { record ->
        DeleteConfirmationDialog(
            record = record,
            onConfirm = {
                viewModel.delete(context, record)
                itemToDelete = null
            },
            onDismiss = {
                itemToDelete = null
            }
        )
    }
}

@Composable
fun LogRecordItem(
    record: LogRecord,
    onDelete: () -> Unit
) {
    val formattedTime = record.elapsedSeconds.formatTimeFromSeconds()
    val formattedDate = remember(record.date) {
        DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
            .withZone(ZoneId.systemDefault())
            .format(record.date)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = formattedDate, style = MaterialTheme.typography.titleMedium)
            Text(text = "Duration: $formattedTime", style = MaterialTheme.typography.bodyMedium)
        }

        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete"
            )
        }
    }
}

@Composable
fun LogList(
    log: List<LogRecord>,
    onDeleteRequest: (LogRecord) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        log.forEach { record ->
            LogRecordItem(
                record = record,
                onDelete = { onDeleteRequest(record) }
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
        }
    }
}

@Composable
fun DeleteConfirmationDialog(
    record: LogRecord,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val formattedDate = remember(record.date) {
        DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
            .withZone(ZoneId.systemDefault())
            .format(record.date)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirm Delete") },
        text = {
            Text("Are you sure you want to delete your plank from:\n\n$formattedDate?")
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}