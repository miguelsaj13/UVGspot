package gt.uvg.uvgspot.data

import gt.uvg.uvgspot.data.remote.mapAvailabilityReport
import gt.uvg.uvgspot.data.remote.mapStudySpace
import gt.uvg.uvgspot.domain.model.Availability
import gt.uvg.uvgspot.domain.model.NoiseLevel
import gt.uvg.uvgspot.domain.model.StudyType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FirestoreMappersTest {
    @Test
    fun mapsDocumentIdAndTypedCharacteristics() {
        val space = mapStudySpace("cit-4", validSpace())
        assertEquals("cit-4", space.id)
        assertEquals(NoiseLevel.QUIET, space.noiseLevel)
        assertEquals(setOf(StudyType.INDIVIDUAL, StudyType.GROUP), space.studyTypes)
        assertTrue(space.hasWifi)
        assertFalse(space.hasPowerOutlets)
        assertNull(space.imageUrl)
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsMissingRequiredCharacteristic() {
        mapStudySpace("cit-4", validSpace() - "hasWifi")
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsUnknownEnumValue() {
        mapStudySpace("cit-4", validSpace() + ("noiseLevel" to "silent"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsEmptyStudyTypes() {
        mapStudySpace("cit-4", validSpace() + ("studyTypes" to emptyList<String>()))
    }

    @Test
    fun pendingTimestampIsNotInvented() {
        val report = mapAvailabilityReport("report-1", validReport(), null, true)
        assertNull(report.createdAtMillis)
        assertTrue(report.isPending)
    }

    @Test
    fun mapsConfirmedTimestampAndUid() {
        val report = mapAvailabilityReport("report-1", validReport(), 1000L, false)
        assertEquals(1000L, report.createdAtMillis)
        assertEquals("anonymous-uid", report.userId)
        assertEquals(Availability.HIGH, report.availability)
        assertFalse(report.isPending)
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsConfirmedReportWithoutTimestamp() {
        mapAvailabilityReport("report-1", validReport(), null, false)
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsUnknownReportedAvailability() {
        mapAvailabilityReport("report-1", validReport() + ("availability" to "UNKNOWN"), 1000L, false)
    }

    private fun validSpace(): Map<String, Any?> = mapOf(
        "name" to "CIT – Nivel 4",
        "location" to "Edificio CIT · Nivel 4",
        "description" to "Espacio registrado para estudiar",
        "noiseLevel" to "QUIET",
        "studyTypes" to listOf("INDIVIDUAL", "GROUP"),
        "hasWifi" to true,
        "hasPowerOutlets" to false
    )

    private fun validReport(): Map<String, Any?> = mapOf(
        "spaceId" to "cit-4",
        "userId" to "anonymous-uid",
        "availability" to "HIGH"
    )
}
