package gt.uvg.uvgspot.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import gt.uvg.uvgspot.data.model.StudySpace
import gt.uvg.uvgspot.ui.theme.StudyTextSecondary
import coil3.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.layout.fillMaxSize

@Composable
fun StudySpaceCard(
    studySpace: StudySpace,
    onDetailsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {

                AsyncImage(
                    model = studySpace.imageUrl,
                    contentDescription = studySpace.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                AvailabilityBadge(
                    availability = studySpace.availability,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                )
            }

            Column(
                modifier = Modifier.padding(
                    horizontal = 12.dp,
                    vertical = 10.dp
                )
            ) {

                // Nombre del lugar
                Text(
                    text = studySpace.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                // Descripción
                Text(
                    text = studySpace.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = StudyTextSecondary
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                // Características + botón
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    SpaceCharacteristic(
                        text = studySpace.noiseLevel,
                        icon = if (studySpace.noiseLevel == "Silencio") {
                            Icons.Default.VolumeOff
                        } else {
                            Icons.Default.VolumeUp
                        }
                    )

                    Spacer(
                        modifier = Modifier.weight(1f)
                    )

                    SpaceCharacteristic(
                        text = studySpace.studyType,
                        icon = if (studySpace.studyType == "Individual") {
                            Icons.Default.Person
                        } else {
                            Icons.Default.Group
                        }
                    )

                    Spacer(
                        modifier = Modifier.weight(1f)
                    )

                    Button(
                        onClick = onDetailsClick,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(
                            text = "Ver detalles",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }
    }
}