package gt.uvg.uvgspot.ui.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.LifecycleResumeEffect
import coil3.compose.AsyncImage
import gt.uvg.uvgspot.domain.model.Availability
import gt.uvg.uvgspot.domain.model.ReportSubmission
import gt.uvg.uvgspot.domain.model.SubmissionStatus
import gt.uvg.uvgspot.R
import gt.uvg.uvgspot.ui.components.AvailabilityBadge
import gt.uvg.uvgspot.ui.components.AvailabilityReportDialog
import gt.uvg.uvgspot.ui.components.ContentState
import gt.uvg.uvgspot.ui.components.SpaceCharacteristic
import gt.uvg.uvgspot.ui.components.toDisplayText
import gt.uvg.uvgspot.ui.components.toUserMessage
import gt.uvg.uvgspot.ui.theme.StudyOptionBackground
import gt.uvg.uvgspot.ui.theme.StudyTextSecondary

@Composable
fun SpaceDetailRoute(
    onBackClick: () -> Unit,
    viewModel: SpaceDetailViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LifecycleResumeEffect(viewModel) {
        viewModel.refreshEstimates()
        onPauseOrDispose { }
    }
    SpaceDetailScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onFavoriteClick = viewModel::onFavoriteClick,
        onReport = viewModel::onReport,
        onRetry = viewModel::retry
    )
}

@Composable
fun SpaceDetailScreen(
    uiState: SpaceDetailUiState,
    onBackClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onReport: (Availability) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val studySpace = uiState.space
    var showReportDialog by rememberSaveable { mutableStateOf(false) }
    if (studySpace == null) {
        Column(modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            TextButton(onClick = onBackClick) { Text("Regresar") }
            ContentState(
                message = if (uiState.isLoading) "Cargando espacio…" else {
                    uiState.errorMessage ?: "No se encontró este espacio."
                },
                isLoading = uiState.isLoading,
                onRetry = if (uiState.isLoading) null else onRetry
            )
            if (uiState.isFavorite) {
                Text("Este espacio sigue guardado en tus favoritos.", modifier = Modifier.padding(16.dp))
                TextButton(onClick = onFavoriteClick) { Text("Quitar de favoritos") }
            }
            uiState.favoriteError?.let { Text(it, modifier = Modifier.padding(16.dp)) }
        }
        return
    }
    if (showReportDialog) {
        AvailabilityReportDialog(
            onDismiss = { showReportDialog = false },
            onConfirm = { availability ->
                showReportDialog = false
                onReport(availability)
            }
        )
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
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
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Regresar"
                )
            }
            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = if (uiState.isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = if (uiState.isFavorite) "Quitar de favoritos" else "Guardar espacio"
                )
            }
            AvailabilityBadge(
                availability = uiState.availability,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(14.dp)
            )
        }

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            if (uiState.isFromCache) {
                Text("Datos guardados; pueden estar desactualizados.", style = MaterialTheme.typography.labelMedium)
            }
            uiState.errorMessage?.let { ContentState(message = it, onRetry = onRetry) }
            uiState.availabilityMessage?.let { ContentState(message = it, onRetry = onRetry) }
            uiState.favoriteError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            Text(
                text = studySpace.name,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )
            Text(
                text = "⌖ ${studySpace.location}",
                style = MaterialTheme.typography.labelMedium,
                color = StudyTextSecondary
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                InformationCard(
                    title = "NIVEL DE RUIDO",
                    value = studySpace.noiseLevel.toDisplayText(),
                    icon = Icons.Default.VolumeOff,
                    modifier = Modifier.weight(1f)
                )

                InformationCard(
                    title = "TIPO",
                    value = studySpace.studyTypes.toDisplayText(),
                    icon = Icons.Default.Person,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                InformationCard(
                    title = "FRECUENCIA",
                    value = studySpace.footTraffic,
                    icon = Icons.Default.DirectionsWalk,
                    modifier = Modifier.weight(1f)
                )

                InformationCard(
                    title = "WIFI",
                    value = if (studySpace.hasWifi) "Disponible" else "No disponible",
                    icon = Icons.Default.Wifi,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            SpaceCharacteristic(
                text = if (studySpace.hasPowerOutlets) "Enchufes disponibles" else "Sin enchufes registrados",
                icon = Icons.Default.Power
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "Estimación de estudiantes; no representa ocupación en tiempo real.",
                style = MaterialTheme.typography.labelMedium,
                color = StudyTextSecondary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { showReportDialog = true },
                enabled = uiState.submission.status != SubmissionStatus.SENDING &&
                    uiState.submission.status != SubmissionStatus.PENDING,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) { Text("Reportar disponibilidad") }
            ReportStatus(submission = uiState.submission)
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Sobre este espacio",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = studySpace.description,
                style = MaterialTheme.typography.bodyMedium,
                color = StudyTextSecondary
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )
        }
    }
}

@Composable
private fun InformationCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(14.dp),
        color = StudyOptionBackground
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {

            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(6.dp)
                        .size(18.dp)
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = StudyTextSecondary
            )

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun ReportStatus(submission: ReportSubmission, modifier: Modifier = Modifier) {
    val status = submission.status
    val message = when (status) {
        SubmissionStatus.IDLE -> null
        SubmissionStatus.SENDING -> "Enviando reporte…"
        SubmissionStatus.PENDING -> "Reporte pendiente de sincronización. Aún no está confirmado."
        SubmissionStatus.CONFIRMED -> "Reporte confirmado por el servidor."
        SubmissionStatus.ERROR -> submission.error?.toUserMessage() ?: "No se pudo enviar el reporte. Vuelve a intentarlo."
    }
    if (message != null) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = if (status == SubmissionStatus.ERROR) MaterialTheme.colorScheme.error else StudyTextSecondary,
            modifier = modifier.padding(top = 8.dp)
        )
    }
}
