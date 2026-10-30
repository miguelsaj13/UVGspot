package gt.uvg.uvgspot.ui.screens.recommendations

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.LifecycleResumeEffect
import gt.uvg.uvgspot.domain.model.NoiseLevel
import gt.uvg.uvgspot.domain.model.StudyPreferences
import gt.uvg.uvgspot.domain.model.StudyType
import gt.uvg.uvgspot.ui.components.BottomDestination
import gt.uvg.uvgspot.ui.components.BottomNavigationBar
import gt.uvg.uvgspot.ui.components.ContentState
import gt.uvg.uvgspot.ui.components.StudySpaceCard
import gt.uvg.uvgspot.ui.components.toDisplayText
import gt.uvg.uvgspot.ui.theme.StudyOptionBackground
import gt.uvg.uvgspot.ui.theme.StudyTextSecondary

@Composable
fun RecommendationsRoute(
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onDetailsClick: (String) -> Unit,
    onHomeClick: () -> Unit,
    viewModel: RecommendationsViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LifecycleResumeEffect(viewModel) {
        viewModel.refreshEstimates()
        onPauseOrDispose { }
    }
    RecommendationsScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onEditClick = onEditClick,
        onDetailsClick = onDetailsClick,
        onHomeClick = onHomeClick,
        onModeChange = viewModel::onModeChange,
        onRetry = viewModel::retry
    )
}

@Composable
fun RecommendationsScreen(
    uiState: RecommendationsUiState,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onDetailsClick: (String) -> Unit,
    onHomeClick: () -> Unit,
    onModeChange: (ListMode) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val title = when (uiState.mode) {
        ListMode.RECOMMENDATIONS -> "Recomendaciones para ti"
        ListMode.SPACES -> "Espacios"
        ListMode.FAVORITES -> "Favoritos"
    }
    Column(
        modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.weight(1f).padding(horizontal = 12.dp)) {
            RecommendationsHeader(onBackClick = onBackClick, title = title)
            Text(
                text = if (uiState.mode == ListMode.RECOMMENDATIONS) {
                    "Basado en tus necesidades"
                } else {
                    "Disponibilidad estimada por reportes de estudiantes"
                },
                style = MaterialTheme.typography.labelMedium,
                color = StudyTextSecondary,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(18.dp))
            if (uiState.mode == ListMode.RECOMMENDATIONS) {
                SelectedNeeds(preferences = uiState.preferences, onEditClick = onEditClick)
            }
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 12.dp)
            ) {
                if (uiState.isFromCache) {
                    item(key = "cache") { Text("Datos guardados; pueden estar desactualizados.", style = MaterialTheme.typography.labelMedium) }
                }
                uiState.availabilityMessage?.let { message ->
                    item(key = "availability") { ContentState(message = message, onRetry = onRetry) }
                }
                uiState.favoritesError?.let { message ->
                    item(key = "favoritesError") { ContentState(message = message, onRetry = onRetry) }
                }
                if (uiState.isLoading) {
                    item(key = "loading") { ContentState(message = "Cargando espacios…", isLoading = true) }
                }
                uiState.errorMessage?.let { message ->
                    item(key = "error") { ContentState(message = message, onRetry = onRetry) }
                }
                if (!uiState.isLoading && uiState.errorMessage == null &&
                    (uiState.mode != ListMode.FAVORITES || uiState.favoritesError == null) &&
                    uiState.recommendations.isEmpty() && uiState.missingFavoriteIds.isEmpty()
                ) {
                    item(key = "empty") {
                        ContentState(message = when (uiState.mode) {
                            ListMode.RECOMMENDATIONS -> "Todavía no hay espacios disponibles para recomendar."
                            ListMode.SPACES -> "Todavía no hay espacios disponibles."
                            ListMode.FAVORITES -> "Aún no has guardado espacios favoritos."
                        })
                    }
                }
                items(uiState.recommendations, key = { "space:${it.space.id}" }) { recommendation ->
                    StudySpaceCard(
                        recommendation = recommendation,
                        onDetailsClick = { onDetailsClick(recommendation.space.id) }
                    )
                }
                items(uiState.missingFavoriteIds.sorted(), key = { "favorite:$it" }) { spaceId ->
                    Surface(
                        onClick = { onDetailsClick(spaceId) },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Favorito guardado: $spaceId\nFicha no disponible; conecta a internet para consultarla.",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
        BottomNavigationBar(
            selectedDestination = when (uiState.mode) {
                ListMode.RECOMMENDATIONS -> BottomDestination.HOME
                ListMode.SPACES -> BottomDestination.SPACES
                ListMode.FAVORITES -> BottomDestination.FAVORITES
            },
            onHomeClick = onHomeClick,
            onSpacesClick = { onModeChange(ListMode.SPACES) },
            onFavoritesClick = { onModeChange(ListMode.FAVORITES) }
        )
    }
}
@Composable
private fun RecommendationsHeader(
    onBackClick: () -> Unit,
    title: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
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
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f)
        )

        // Mantiene el título visualmente centrado
        Spacer(
            modifier = Modifier.size(48.dp)
        )
    }
}

@Composable
private fun SelectedNeeds(
    preferences: StudyPreferences,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SelectedNeedChip(
            text = preferences.noiseLevel.toDisplayText(),
            icon = if (preferences.noiseLevel == NoiseLevel.QUIET) Icons.Default.VolumeOff else Icons.Default.VolumeUp
        )

        SelectedNeedChip(
            text = preferences.studyType.toDisplayText(),
            icon = if (preferences.studyType == StudyType.INDIVIDUAL) Icons.Default.Person else Icons.Default.Group
        )

        if (preferences.requiresWifi) {
            SelectedNeedChip(text = "WiFi", icon = Icons.Default.Wifi)
        }
        if (preferences.requiresPowerOutlets) {
            SelectedNeedChip(text = "Enchufes", icon = Icons.Default.Power)
        }
        Surface(
            modifier = Modifier.clickable(
                onClick = onEditClick
            ),
            shape = RoundedCornerShape(16.dp),
            color = StudyOptionBackground
        ) {
            Row(
                modifier = Modifier.padding(
                    horizontal = 10.dp,
                    vertical = 6.dp
                ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {

                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)
                )

                Text(
                    text = "Editar",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

@Composable
private fun SelectedNeedChip(
    text: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFDDF5DF)
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 6.dp
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(14.dp)
            )

            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
