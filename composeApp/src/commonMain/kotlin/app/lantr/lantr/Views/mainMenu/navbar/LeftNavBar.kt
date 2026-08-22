package app.lantr.lantr.Views.mainMenu.navbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.key.Key.Companion.R
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import app.lantr.lantr.Theme.LantrTheme
import app.lantr.lantr.Views.mainMenu.navbar.components.AddButton
import app.lantr.lantr.generated.resources.Res
import app.lantr.lantr.generated.resources.add
import app.lantr.lantr.generated.resources.menu
import app.lantr.lantr.generated.resources.settings
import org.jetbrains.compose.resources.painterResource
import java.util.UUID


@Composable
fun LeftSidebarMenu(
    menuItems: List<MenuItem>,
    selectedItem: String,
    onItemClick: (String) -> Unit,
    onCreateProject: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    NavigationRail(
        modifier = modifier
            .fillMaxHeight()
            .width(85.dp), // Compact rail

        containerColor = LantrTheme.extendedColors.navBackground,
    ) {

        Spacer(modifier = Modifier.height(16.dp))

        Icon(painterResource(Res.drawable.menu), contentDescription = "Menu")

        Spacer(modifier = Modifier.height(10.dp))

        AddButton(onClick = onCreateProject)

        Spacer(modifier = Modifier.height(16.dp))

        menuItems.forEach { item ->
            NavigationRailItem(
                icon = {
                    Icon(
                        painter = painterResource(item.icon),
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                selected = selectedItem == item.route,
                onClick = { onItemClick(item.route) },
                colors = NavigationRailItemDefaults.colors(
                    indicatorColor = LantrTheme.extendedColors.navIndicator
                )
            )
        }
    }
}
/*
// Alternative: Expanded sidebar for larger screens
@Composable
fun ExpandedLeftSidebar(
    menuItems: List<MenuItem>,
    selectedItem: String,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationDrawer(
        modifier = modifier
            .fillMaxHeight()
            .width(240.dp),
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(16.dp))

                menuItems.forEach { item ->
                    NavigationDrawerItem(
                        icon = { Icon(item.icon, contentDescription = null) },
                        label = { Text(item.label) },
                        selected = selectedItem == item.id,
                        onClick = { onItemClick(item.id) },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        },
        content = { /* Your main content */ }
    )
}
*/