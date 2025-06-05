package cz.bradacd.plankchallenge.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.semantics.Role.Companion.Button
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cz.bradacd.plankchallenge.SheetsClient.PersonStanding
import cz.bradacd.plankchallenge.viewmodel.StandingsViewModel

@Composable
fun StandingsScreen(
    viewModel: StandingsViewModel = viewModel()
) {
    val context = LocalContext.current
    val standings by viewModel.standings.collectAsState()
    val loading by viewModel.loading.collectAsState()

    // Remember if we already loaded data
    var initialLoadDone by remember { mutableStateOf(false) }

    // Initial data load on screen entry
    LaunchedEffect(Unit) {
        if (!initialLoadDone) {
            viewModel.onEntry(context)
            initialLoadDone = true
        }
    }

    // Show toast messages
    LaunchedEffect(true) {
        viewModel.toastEvent.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        if (loading) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                itemsIndexed(
                    standings.sortedByDescending { it.points }
                ) { index, standing ->
                    PersonStandingRow(
                        rank = index + 1,
                        standing = standing
                    )
                    Divider()
                }
            }
        }

        // Reload button at bottom
        Button(
            onClick = { viewModel.reloadStandings(context) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text("Reload Standings")
        }
    }
}


@Composable
private fun PersonStandingRow(
    rank: Int,
    standing: PersonStanding
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$rank.",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.width(40.dp)
        )

        Text(
            text = standing.name,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = standing.points.toString(),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

