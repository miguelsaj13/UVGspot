package gt.uvg.uvgspot.ui.screens.recommendations

import androidx.lifecycle.ViewModel
import gt.uvg.uvgspot.data.SampleData
import gt.uvg.uvgspot.data.model.StudySpace

class RecommendationsViewModel : ViewModel() {

    val studySpaces: List<StudySpace> = SampleData.studySpaces
}