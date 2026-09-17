package gt.uvg.uvgspot.ui.screens.detail

import androidx.lifecycle.ViewModel
import gt.uvg.uvgspot.data.SampleData
import gt.uvg.uvgspot.data.model.StudySpace

class SpaceDetailViewModel : ViewModel() {

    fun getStudySpace(spaceId: Int): StudySpace? {
        return SampleData.studySpaces.find { studySpace ->
            studySpace.id == spaceId
        }
    }
}