package cz.bradacd.plankchallenge.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.bradacd.plankchallenge.LocalRepository.loadSettingValidatedBasic
import cz.bradacd.plankchallenge.SheetsClient.PersonStanding
import cz.bradacd.plankchallenge.SheetsClient.getCurrentStandings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class StandingsViewModel : ViewModel() {
    private val _loading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _toastEvent = Channel<String>()
    val toastEvent = _toastEvent.receiveAsFlow()

    private val _standings: MutableStateFlow<List<PersonStanding>> = MutableStateFlow(emptyList())
    val standings: StateFlow<List<PersonStanding>> = _standings

    private val _standingsInitialised: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val standingsInitialised: StateFlow<Boolean> = _standingsInitialised

    fun onEntry(context: Context) {
        if (!standingsInitialised.value) {
            reloadStandings(context)
            _standingsInitialised.value = true
        }
    }

    fun reloadStandings(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            _loading.value = true
            try {
                _standings.value = getCurrentStandings(context, loadSettingValidatedBasic(context))
            } catch (e: Exception) {
                _toastEvent.send("Sync failed: ${e.localizedMessage ?: "Unknown error"}")
            } finally {
                _loading.value = false
            }
        }
    }
}