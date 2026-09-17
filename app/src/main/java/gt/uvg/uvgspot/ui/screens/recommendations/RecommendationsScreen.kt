package gt.uvg.uvgspot.ui.screens.recommendations

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import gt.uvg.uvgspot.data.model.StudySpace
import gt.uvg.uvgspot.ui.components.StudySpaceCard
import gt.uvg.uvgspot.ui.theme.StudyOptionBackground
import gt.uvg.uvgspot.ui.theme.StudyTextSecondary
import gt.uvg.uvgspot.ui.components.BottomDestination
import gt.uvg.uvgspot.ui.components.BottomNavigationBar


// ---------------------------------------------------------
// ROUTE
// Conecta el ViewModel con la pantalla.
// ---------------------------------------------------------

@Composable
fun RecommendationsRoute(
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onDetailsClick: (Int) -> Unit,
    viewModel: RecommendationsViewModel = viewModel()
) {
    RecommendationsScreen(
        studySpaces = viewModel.studySpaces,
        onBackClick = onBackClick,
        onEditClick = onEditClick,
        onDetailsClick = onDetailsClick
    )
}


// ---------------------------------------------------------
// SCREEN
// Solo se encarga de mostrar la interfaz.
// ---------------------------------------------------------

@Composable
fun RecommendationsScreen(
    studySpaces: List<StudySpace>,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onDetailsClick: (Int) -> Unit,
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
                .padding(horizontal = 12.dp)
        ) {

            RecommendationsHeader(
                onBackClick = onBackClick
            )

            Text(
                text = "Basado en tus necesidades",
                style = MaterialTheme.typography.labelMedium,
                color = StudyTextSecondary,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            SelectedNeeds(
                onEditClick = onEditClick
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(
                    bottom = 12.dp
                )
            ) {
                items(
                    items = studySpaces,
                    key = { studySpace ->
                        studySpace.id
                    }
                ) { studySpace ->

                    StudySpaceCard(
                        studySpace = studySpace,
                        onDetailsClick = {
                            onDetailsClick(studySpace.id)
                        }
                    )
                }
            }
        }

        BottomNavigationBar(
            selectedDestination = BottomDestination.HOME
        )
    }
}

@Composable
private fun RecommendationsHeader(
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
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
            text = "Recomendaciones para ti",
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


// ---------------------------------------------------------
// NECESIDADES SELECCIONADAS
// ---------------------------------------------------------

@Composable
private fun SelectedNeeds(
    onEditClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        SelectedNeedChip(
            text = "Silencio",
            icon = Icons.Default.VolumeOff
        )

        SelectedNeedChip(
            text = "Individual",
            icon = Icons.Default.Person
        )

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
    icon: ImageVector
) {
    Surface(
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
