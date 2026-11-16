package gt.uvg.uvgspot.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

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
                    imageVector = if (selectedDestination == BottomDestination.HOME) {
                        Icons.Filled.Home
                    } else {
                        Icons.Outlined.Home
                    },
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
                    imageVector = if (selectedDestination == BottomDestination.SPACES) {
                        Icons.Filled.LocationOn
                    } else {
                        Icons.Outlined.LocationOn
                    },
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
                    imageVector = if (selectedDestination == BottomDestination.FAVORITES) {
                        Icons.Filled.Favorite
                    } else {
                        Icons.Outlined.FavoriteBorder
                    },
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
        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
        indicatorColor = Color.Transparent
    )