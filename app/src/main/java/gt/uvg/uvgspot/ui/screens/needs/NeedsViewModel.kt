package gt.uvg.uvgspot.ui.screens.needs

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class NeedsViewModel : ViewModel() {

    var quietSelected by mutableStateOf(true)
        private set

    var moderateNoiseSelected by mutableStateOf(false)
        private set

    var individualStudySelected by mutableStateOf(true)
        private set

    var groupWorkSelected by mutableStateOf(false)
        private set

    var powerOutletsSelected by mutableStateOf(false)
        private set

    var wifiSelected by mutableStateOf(false)
        private set

    fun selectQuiet() {
        quietSelected = true
        moderateNoiseSelected = false
    }

    fun selectModerateNoise() {
        moderateNoiseSelected = true
        quietSelected = false
    }

    fun selectIndividualStudy() {
        individualStudySelected = true
        groupWorkSelected = false
    }

    fun selectGroupWork() {
        groupWorkSelected = true
        individualStudySelected = false
    }

    fun togglePowerOutlets() {
        powerOutletsSelected = !powerOutletsSelected
    }

    fun toggleWifi() {
        wifiSelected = !wifiSelected
    }
}