package gt.uvg.uvgspot.ui.screens.needs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import gt.uvg.uvgspot.domain.model.NoiseLevel
import gt.uvg.uvgspot.domain.model.StudyPreferences
import gt.uvg.uvgspot.domain.model.StudyType
import gt.uvg.uvgspot.ui.components.BottomDestination
import gt.uvg.uvgspot.ui.components.BottomNavigationBar
import gt.uvg.uvgspot.ui.theme.StudyOptionBackground
import gt.uvg.uvgspot.ui.theme.StudyProfileBackground
import gt.uvg.uvgspot.ui.theme.StudyTextSecondary

@Composable
fun NeedsRoute(
    onBackClick: () -> Unit,
    onRecommendationsClick: (StudyPreferences) -> Unit,
    onSpacesClick: (StudyPreferences) -> Unit,
    onFavoritesClick: (StudyPreferences) -> Unit,
    viewModel: NeedsViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    NeedsScreen(
        uiState = uiState,
        onQuietClick = viewModel::selectQuiet,
        onModerateNoiseClick = viewModel::selectModerateNoise,
        onIndividualStudyClick = viewModel::selectIndividualStudy,
        onGroupWorkClick = viewModel::selectGroupWork,
        onPowerOutletsClick = viewModel::togglePowerOutlets,
        onWifiClick = viewModel::toggleWifi,
        onBackClick = onBackClick,
        onRecommendationsClick = { onRecommendationsClick(uiState.preferences) },
        onSpacesClick = { onSpacesClick(uiState.preferences) },
        onFavoritesClick = { onFavoritesClick(uiState.preferences) }
    )
}

@Composable
fun NeedsScreen(
    uiState: NeedsUiState,
    onQuietClick: () -> Unit,
    onModerateNoiseClick: () -> Unit,
    onIndividualStudyClick: () -> Unit,
    onGroupWorkClick: () -> Unit,
    onPowerOutletsClick: () -> Unit,
    onWifiClick: () -> Unit,
    onBackClick: () -> Unit,
    onRecommendationsClick: () -> Unit,
    onSpacesClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        BoxWithConstraints(modifier = Modifier.weight(1f)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .heightIn(min = maxHeight)
                    .padding(horizontal = 20.dp)
            ) {

                Spacer(modifier = Modifier.height(12.dp))

                NeedsHeader(
                    onBackClick = onBackClick
                )

                Spacer(modifier = Modifier.height(30.dp))

                Text(
                    text = "Escoge lo que tu lugar en UVG necesita para poder encontrarlo.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = StudyTextSecondary
                )

                Spacer(modifier = Modifier.height(24.dp))

                NeedSectionTitle("AMBIENTE")

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    NeedOption(
                        text = "Silencio",
                        icon = Icons.Default.VolumeOff,
                        selected = uiState.preferences.noiseLevel == NoiseLevel.QUIET,
                        onClick = onQuietClick,
                        modifier = Modifier.weight(1f)
                    )

                    NeedOption(
                        text = "Ruido moderado",
                        icon = Icons.Default.VolumeUp,
                        selected = uiState.preferences.noiseLevel == NoiseLevel.MODERATE,
                        onClick = onModerateNoiseClick,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                NeedSectionTitle("ACTIVIDAD")

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    NeedOption(
                        text = "Individual",
                        icon = Icons.Default.Person,
                        selected = uiState.preferences.studyType == StudyType.INDIVIDUAL,
                        onClick = onIndividualStudyClick,
                        modifier = Modifier.weight(1f)
                    )

                    NeedOption(
                        text = "En grupo",
                        icon = Icons.Default.Group,
                        selected = uiState.preferences.studyType == StudyType.GROUP,
                        onClick = onGroupWorkClick,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                NeedSectionTitle("ESENCIALES")

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    NeedOption(
                        text = "Enchufes",
                        icon = Icons.Default.Power,
                        selected = uiState.preferences.requiresPowerOutlets,
                        onClick = onPowerOutletsClick,
                        modifier = Modifier.weight(1f)
                    )

                    NeedOption(
                        text = "WiFi",
                        icon = Icons.Default.Wifi,
                        selected = uiState.preferences.requiresWifi,
                        onClick = onWifiClick,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = onRecommendationsClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = "Ver Recomendaciones",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
            }
        }

        BottomNavigationBar(
            selectedDestination = BottomDestination.HOME,
            onHomeClick = onRecommendationsClick,
            onSpacesClick = onSpacesClick,
            onFavoritesClick = onFavoritesClick
        )
    }
}
@Composable
private fun NeedsHeader(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClick
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Regresar"
            )
        }

        Text(
            text = "¿Qué necesitas?",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f)
        )

        Surface(
            shape = CircleShape,
            color = StudyProfileBackground
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Perfil",
                modifier = Modifier
                    .padding(8.dp)
                    .size(20.dp)
            )
        }
    }
}

@Composable
private fun NeedSectionTitle(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = StudyTextSecondary
    )
}

@Composable
private fun NeedOption(
    text: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        StudyOptionBackground
    }

    val contentColor = if (selected) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Surface(
        modifier = modifier
            .height(66.dp)
            .clickable(onClick = onClick)
            .semantics { this.selected = selected },
        shape = RoundedCornerShape(12.dp),
        color = containerColor
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = contentColor
            )
        }
    }
}
