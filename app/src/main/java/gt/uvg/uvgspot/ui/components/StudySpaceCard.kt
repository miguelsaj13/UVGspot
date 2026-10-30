package gt.uvg.uvgspot.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import gt.uvg.uvgspot.domain.model.NoiseLevel
import gt.uvg.uvgspot.domain.model.Recommendation
import gt.uvg.uvgspot.domain.model.StudyType
import gt.uvg.uvgspot.R
import gt.uvg.uvgspot.ui.theme.StudyTextSecondary

@Composable
fun StudySpaceCard(
    recommendation: Recommendation,
    onDetailsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val studySpace = recommendation.space
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
                    placeholder = painterResource(R.drawable.study_space_placeholder),
                    error = painterResource(R.drawable.study_space_placeholder),
                    fallback = painterResource(R.drawable.study_space_placeholder),
                    contentDescription = studySpace.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                AvailabilityBadge(
                    availability = recommendation.availability,
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
                Text(
                    text = studySpace.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )
                Text(
                    text = studySpace.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = StudyTextSecondary
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                if (recommendation.reasons.isNotEmpty()) {
                    Text(
                        text = recommendation.reasons.joinToString(" · "),
                        style = MaterialTheme.typography.labelMedium,
                        color = StudyTextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    SpaceCharacteristic(
                        text = studySpace.noiseLevel.toDisplayText(),
                        modifier = Modifier.weight(1f),
                        icon = if (studySpace.noiseLevel == NoiseLevel.QUIET) {
                            Icons.Default.VolumeOff
                        } else {
                            Icons.Default.VolumeUp
                        }
                    )

                    SpaceCharacteristic(
                        text = studySpace.studyTypes.toDisplayText(),
                        modifier = Modifier.weight(1f),
                        icon = if (studySpace.studyTypes == setOf(StudyType.INDIVIDUAL)) {
                            Icons.Default.Person
                        } else {
                            Icons.Default.Group
                        }
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
