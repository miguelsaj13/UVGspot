package gt.uvg.uvgspot.data.remote

import gt.uvg.uvgspot.domain.model.Availability
import gt.uvg.uvgspot.domain.model.AvailabilityReport
import gt.uvg.uvgspot.domain.model.NoiseLevel
import gt.uvg.uvgspot.domain.model.StudySpace
import gt.uvg.uvgspot.domain.model.StudyType

fun mapStudySpace(id: String, fields: Map<String, Any?>): StudySpace {
    require(id.isNotBlank() && '/' !in id)
    val document = StudySpaceDocument(
        name = fields.requiredText("name"),
        location = fields.requiredText("location"),
        description = fields.requiredText("description"),
        noiseLevel = fields.requiredText("noiseLevel"),
        studyTypes = (fields["studyTypes"] as? List<*>)?.map { it as? String
            ?: throw IllegalArgumentException("Invalid study type") }
            ?: throw IllegalArgumentException("Missing study types"),
        hasWifi = fields["hasWifi"] as? Boolean
            ?: throw IllegalArgumentException("Missing WiFi"),
        hasPowerOutlets = fields["hasPowerOutlets"] as? Boolean
            ?: throw IllegalArgumentException("Missing power outlets"),
        footTraffic = fields["footTraffic"] as? String,
        imageUrl = fields["imageUrl"] as? String
    )
    val studyTypes = document.studyTypes.map(StudyType::valueOf).toSet()
    require(studyTypes.isNotEmpty())
    return StudySpace(
        id, document.name, document.location, document.description,
        NoiseLevel.valueOf(document.noiseLevel), studyTypes,
        document.hasWifi, document.hasPowerOutlets,
        document.footTraffic?.takeIf(String::isNotBlank) ?: "Sin información",
        document.imageUrl?.takeIf(String::isNotBlank)
    )
}

fun mapAvailabilityReport(
    id: String,
    fields: Map<String, Any?>,
    createdAtMillis: Long?,
    isPending: Boolean
): AvailabilityReport {
    require(id.isNotBlank())
    val document = AvailabilityReportDocument(
        fields.requiredText("spaceId"), fields.requiredText("userId"),
        fields.requiredText("availability"), createdAtMillis
    )
    val availability = Availability.valueOf(document.availability)
    require(availability != Availability.UNKNOWN)
    require(isPending || document.createdAtMillis != null)
    return AvailabilityReport(
        id, document.spaceId, document.userId, availability,
        document.createdAtMillis, isPending
    )
}

private fun Map<String, Any?>.requiredText(key: String): String =
    (get(key) as? String)?.takeIf(String::isNotBlank)
        ?: throw IllegalArgumentException("Missing $key")
