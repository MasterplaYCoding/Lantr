package app.lantr.lantr.Views.mainMenu.navbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.unit.dp
import app.lantr.lantr.Util.PlatformUtil
import app.lantr.lantr.Views.mainMenu.navbar.components.AddButton
import app.lantr.lantr.Views.mainMenu.navbar.components.AddButtonLight
import java.util.UUID

@Composable
fun AdaptiveNavigationLayout(
    windowSizeClass: WindowSizeClass,
    selectedMenuItem: String,
    onItemClick: (String) -> Unit,
    onCreateProject: () -> Unit = {},
    modifier: Modifier = Modifier.background(
        if (PlatformUtil.isDesktop) Color.Transparent else MaterialTheme.colorScheme.background
    ),
    content: @Composable () -> Unit
) {

    when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            // Phone portrait - Bottom Navigation
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(if (PlatformUtil.isDesktop) Color.Transparent else MaterialTheme.colorScheme.background)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(if (PlatformUtil.isDesktop) Color.Transparent else MaterialTheme.colorScheme.background)
                        .padding(16.dp)
                )
                {
                    content()
                }
                BottomNavigationMenu(
                    menuItems = sampleMenuItems,
                    selectedItem = selectedMenuItem,
                    onItemClick = onItemClick
                )
            }
        }

        WindowWidthSizeClass.Medium , WindowWidthSizeClass.Expanded -> {
            // Tablet portrait or phone landscape - Navigation Rail
            Row(
                modifier = modifier
                    .fillMaxSize()
                    .background(if (PlatformUtil.isDesktop) Color.Transparent else MaterialTheme.colorScheme.background)
            ) {
                LeftSidebarMenu(
                    menuItems = sampleMenuItems,
                    selectedItem = selectedMenuItem,
                    onItemClick = onItemClick,
                    onCreateProject = onCreateProject
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(if (PlatformUtil.isDesktop) Color.Transparent else MaterialTheme.colorScheme.background)
                        .padding(16.dp)
                ) {
                    content()
                }
            }
        }
    }
}
