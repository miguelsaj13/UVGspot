package gt.uvg.uvgspot.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier

enum class BottomDestination {
    HOME,
    SPACES,
    FAVORITES
}

@Composable
fun BottomNavigationBar(
    selectedDestination: BottomDestination,
    onHomeClick: () -> Unit = {},
    onSpacesClick: () -> Unit = {},
    onFavoritesClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        windowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MaterialTheme.colorScheme.surface
    ) {

        NavigationBarItem(
            selected = selectedDestination == BottomDestination.HOME,
            onClick = onHomeClick,
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Inicio"
                )
            },
            label = {
                Text(
                    text = "Inicio",
                    style = MaterialTheme.typography.labelMedium
                )
            },
            colors = bottomNavigationColors()
        )

        NavigationBarItem(
            selected = selectedDestination == BottomDestination.SPACES,
            onClick = onSpacesClick,
            icon = {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Espacios"
                )
            },
            label = {
                Text(
                    text = "Espacios",
                    style = MaterialTheme.typography.labelMedium
                )
            },
            colors = bottomNavigationColors()
        )

        NavigationBarItem(
            selected = selectedDestination == BottomDestination.FAVORITES,
            onClick = onFavoritesClick,
            icon = {
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = "Favoritos"
                )
            },
            label = {
                Text(
                    text = "Favoritos",
                    style = MaterialTheme.typography.labelMedium
                )
            },
            colors = bottomNavigationColors()
        )
    }
}

@Composable
private fun bottomNavigationColors() =
    NavigationBarItemDefaults.colors(
        selectedIconColor = MaterialTheme.colorScheme.primary,
        selectedTextColor = MaterialTheme.colorScheme.primary,
        indicatorColor = Color.Transparent
    )
