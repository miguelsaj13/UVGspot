package gt.uvg.uvgspot.data

import gt.uvg.uvgspot.data.model.Availability
import gt.uvg.uvgspot.data.model.StudySpace

object SampleData {

    val studySpaces = listOf(

        StudySpace(
            id = 1,
            name = "CIT – Nivel 4",
            location = "Edificio CIT · Nivel 4",
            description = "Un lugar silencioso perfecto para el estudio individual.",
            availability = Availability.HIGH,
            noiseLevel = "Silencio",
            studyType = "Individual",
            footTraffic = "Baja",
            wifi = "WiFi",
            matchReason = "Este espacio coincide con tu necesidad de estudiar individualmente en un ambiente silencioso y con poca afluencia.",
            imageUrl = "https://picsum.photos/id/20/800/400"
        ),

        StudySpace(
            id = 2,
            name = "Pasillo Sarita",
            location = "Edificio principal · Pasillo Sarita",
            description = "Lugar perfecto para trabajar en equipo.",
            availability = Availability.MEDIUM,
            noiseLevel = "Ruido moderado",
            studyType = "En grupo",
            footTraffic = "Media",
            wifi = "WiFi",
            matchReason = "Este espacio es adecuado para actividades grupales y permite trabajar en un ambiente moderado.",
            imageUrl = "https://picsum.photos/id/42/800/400"
        ),

        StudySpace(
            id = 3,
            name = "Golden Hall",
            location = "Golden Hall",
            description = "Un lugar tranquilo perfecto para trabajar en equipo.",
            availability = Availability.LOW,
            noiseLevel = "Ruidoso",
            studyType = "Limitado",
            footTraffic = "Alta",
            wifi = "WiFi",
            matchReason = "Este espacio puede utilizarse para trabajo grupal, aunque actualmente presenta menor disponibilidad.",
            imageUrl = "https://picsum.photos/id/48/800/400"
        )
    )
}