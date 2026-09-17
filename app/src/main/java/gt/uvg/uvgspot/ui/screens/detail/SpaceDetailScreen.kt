package gt.uvg.uvgspot.ui.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import gt.uvg.uvgspot.data.model.StudySpace
import gt.uvg.uvgspot.ui.components.AvailabilityBadge
import gt.uvg.uvgspot.ui.theme.StudyOptionBackground
import gt.uvg.uvgspot.ui.theme.StudyTextSecondary

@Composable
fun SpaceDetailRoute(
    spaceId: Int,
    onBackClick: () -> Unit,
    viewModel: SpaceDetailViewModel = viewModel()
) {
    val studySpace = viewModel.getStudySpace(spaceId)

    if (studySpace != null) {
        SpaceDetailScreen(
            studySpace = studySpace,
            onBackClick = onBackClick
        )
    }
}

@Composable
fun SpaceDetailScreen(
    studySpace: StudySpace,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {

        // Imagen principal
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        ) {

            AsyncImage(
                model = studySpace.imageUrl,
                contentDescription = studySpace.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Botón regresar
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

            // Favorito
            IconButton(
                onClick = {
                    // Se implementará posteriormente
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.BookmarkBorder,
                    contentDescription = "Guardar espacio"
                )
            }

            // Disponibilidad
            AvailabilityBadge(
                availability = studySpace.availability,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(14.dp)
            )
        }

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            // Nombre
            Text(
                text = studySpace.name,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            // Ubicación
            Text(
                text = "⌖ ${studySpace.location}",
                style = MaterialTheme.typography.labelMedium,
                color = StudyTextSecondary
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            // Primera fila
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                InformationCard(
                    title = "NIVEL DE RUIDO",
                    value = studySpace.noiseLevel,
                    icon = Icons.Default.VolumeOff,
                    modifier = Modifier.weight(1f)
                )

                InformationCard(
                    title = "TIPO",
                    value = studySpace.studyType,
                    icon = Icons.Default.Person,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            // Segunda fila
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
                    value = studySpace.wifi,
                    icon = Icons.Default.Wifi,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            // Información adicional
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
