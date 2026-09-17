package gt.uvg.uvgspot.ui.screens.needs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import gt.uvg.uvgspot.ui.theme.StudyTextSecondary
import gt.uvg.uvgspot.ui.theme.StudyOptionBackground
import gt.uvg.uvgspot.ui.theme.StudyProfileBackground
import gt.uvg.uvgspot.ui.components.BottomDestination
import gt.uvg.uvgspot.ui.components.BottomNavigationBar

@Composable
fun NeedsRoute(
    onBackClick: () -> Unit,
    onRecommendationsClick: () -> Unit,
    viewModel: NeedsViewModel = viewModel()
) {
    NeedsScreen(
        quietSelected = viewModel.quietSelected,
        moderateNoiseSelected = viewModel.moderateNoiseSelected,
        individualStudySelected = viewModel.individualStudySelected,
        groupWorkSelected = viewModel.groupWorkSelected,
        powerOutletsSelected = viewModel.powerOutletsSelected,
        wifiSelected = viewModel.wifiSelected,
        onQuietClick = viewModel::selectQuiet,
        onModerateNoiseClick = viewModel::selectModerateNoise,
        onIndividualStudyClick = viewModel::selectIndividualStudy,
        onGroupWorkClick = viewModel::selectGroupWork,
        onPowerOutletsClick = viewModel::togglePowerOutlets,
        onWifiClick = viewModel::toggleWifi,
        onBackClick = onBackClick,
        onRecommendationsClick = onRecommendationsClick
    )
}

@Composable
fun NeedsScreen(
    quietSelected: Boolean,
    moderateNoiseSelected: Boolean,
    individualStudySelected: Boolean,
    groupWorkSelected: Boolean,
    powerOutletsSelected: Boolean,
    wifiSelected: Boolean,
    onQuietClick: () -> Unit,
    onModerateNoiseClick: () -> Unit,
    onIndividualStudyClick: () -> Unit,
    onGroupWorkClick: () -> Unit,
    onPowerOutletsClick: () -> Unit,
    onWifiClick: () -> Unit,
    onBackClick: () -> Unit,
    onRecommendationsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        Column(
            modifier = Modifier
                .weight(1f)
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
                    selected = quietSelected,
                    onClick = onQuietClick,
                    modifier = Modifier.weight(1f)
                )

                NeedOption(
                    text = "Ruido moderado",
                    icon = Icons.Default.VolumeUp,
                    selected = moderateNoiseSelected,
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
                    selected = individualStudySelected,
                    onClick = onIndividualStudyClick,
                    modifier = Modifier.weight(1f)
                )

                NeedOption(
                    text = "En grupo",
                    icon = Icons.Default.Group,
                    selected = groupWorkSelected,
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
                    selected = powerOutletsSelected,
                    onClick = onPowerOutletsClick,
                    modifier = Modifier.weight(1f)
                )

                NeedOption(
                    text = "WiFi",
                    icon = Icons.Default.Wifi,
                    selected = wifiSelected,
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

        BottomNavigationBar(
            selectedDestination = BottomDestination.HOME
        )
    }
}
@Composable
private fun NeedsHeader(
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
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
    text: String
) {
    Text(
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
            .clickable(onClick = onClick),
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