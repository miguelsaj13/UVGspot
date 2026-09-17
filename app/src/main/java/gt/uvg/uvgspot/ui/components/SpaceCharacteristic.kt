package gt.uvg.uvgspot.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import gt.uvg.uvgspot.ui.theme.StudyTextSecondary

@Composable
fun SpaceCharacteristic(
    text: String,
    icon: ImageVector
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = StudyTextSecondary
        )

        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = StudyTextSecondary
        )
    }
}