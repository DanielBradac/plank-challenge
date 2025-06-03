package cz.bradacd.plankchallenge.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cz.bradacd.plankchallenge.LocalRepository.Settings
import cz.bradacd.plankchallenge.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = viewModel()) {
    val context = LocalContext.current
    val settings by viewModel.settings.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onEntry(context)
    }

    // If settings not loaded yet, show nothing or a loading indicator
    settings?.let { currentSettings ->
        var sheetId by remember { mutableStateOf(currentSettings.sheetId) }
        var sheetName by remember { mutableStateOf(currentSettings.sheetName) }
        var personName by remember { mutableStateOf(currentSettings.personName) }

        // Save on every change
        fun updateAndSave() {
            val newSettings = Settings(sheetId, sheetName, personName)
            viewModel.updateSettings(newSettings)
            viewModel.saveSettings(context)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = sheetId,
                onValueChange = {
                    sheetId = it
                    updateAndSave()
                },
                label = { Text("Sheet ID") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = sheetName,
                onValueChange = {
                    sheetName = it
                    updateAndSave()
                },
                label = { Text("Sheet Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = personName,
                onValueChange = {
                    personName = it
                    updateAndSave()
                },
                label = { Text("Your Name") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
