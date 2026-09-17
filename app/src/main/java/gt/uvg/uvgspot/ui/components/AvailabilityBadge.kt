package gt.uvg.uvgspot.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import gt.uvg.uvgspot.data.model.Availability

@Composable
fun AvailabilityBadge(
    availability: Availability,
    modifier: Modifier = Modifier
) {
    val text: String
    val backgroundColor: Color
    val textColor: Color

    when (availability) {
        Availability.HIGH -> {
            text = "Disponibilidad: Alta"
            backgroundColor = Color(0xFFDDF5DF)
            textColor = Color(0xFF2E7D32)
        }

        Availability.MEDIUM -> {
            text = "Disponibilidad: Media"
            backgroundColor = Color(0xFFFFF1C2)
            textColor = Color(0xFF9A6700)
        }

        Availability.LOW -> {
            text = "Disponibilidad: Baja"
            backgroundColor = Color(0xFFFADDDD)
            textColor = Color(0xFFBA1A1A)
        }
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = backgroundColor
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = textColor,
            modifier = Modifier.padding(
                horizontal = 8.dp,
                vertical = 4.dp
            )
        )
    }
}