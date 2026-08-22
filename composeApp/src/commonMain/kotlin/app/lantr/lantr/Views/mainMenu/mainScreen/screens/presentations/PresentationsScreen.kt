package app.lantr.lantr.Views.mainMenu.mainScreen.screens.presentations

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.lantr.lantr.Models.Presentation
import app.lantr.lantr.Models.WindowWidthSizeClass
import app.lantr.lantr.Util.PlatformUtil
import app.lantr.lantr.Util.requestOpenLtrnPresentationWindow
import app.lantr.lantr.Views.mainMenu.mainScreen.calculateWindowSizeClass
import app.lantr.lantr.Views.mainMenu.mainScreen.screens.presentations.components.ProjectsList
import app.lantr.lantr.Views.mainMenu.mainScreen.screens.presentations.components.SearchTextField
import app.lantr.lantr.Views.mainMenu.navbar.components.AddButtonLight

@Composable
fun PresentationsScreen(
    onCreateProject: () -> Unit = {},
    onOpenPresentation: (Presentation) -> Unit = {}
) {
    var searchText by remember { mutableStateOf("") }

    val customWindowSize = calculateWindowSizeClass()
    val showFloatingAddButton = customWindowSize.widthSizeClass == WindowWidthSizeClass.Compact

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        PresentationsContent(
            searchText = searchText,
            showFloatingAddButton = showFloatingAddButton,
            onSearchTextChange = { searchText = it },
            onCreateProject = onCreateProject,
            onOpenPresentation = onOpenPresentation,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
private fun PresentationsContent(
    searchText: String,
    showFloatingAddButton: Boolean,
    onSearchTextChange: (String) -> Unit,
    onCreateProject: () -> Unit,
    onOpenPresentation: (Presentation) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(if (PlatformUtil.isDesktop) Color.Transparent else MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(if (PlatformUtil.isDesktop) Color.Transparent else MaterialTheme.colorScheme.background)
        ) {
            PresentationsHeading()

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Button(onClick = { requestOpenLtrnPresentationWindow() }) {
                    Text("Open .ltrn")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            SearchTextField(
                searchText = searchText,
                onSearchTextChange = onSearchTextChange
            )

            Spacer(modifier = Modifier.height(20.dp))

            key(searchText) {
                ProjectsList(
                    projectName = searchText,
                    isPhone = showFloatingAddButton,
                    onOpenPresentation = onOpenPresentation
                )
            }
        }

        if (showFloatingAddButton) {
            AddButtonLight(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(1.dp),
                onClick = onCreateProject
            )
        }
    }
}

@Composable
private fun PresentationsHeading() {
    Text(
        text = "Presentations",
        fontSize = 30.sp,
        fontWeight = FontWeight.SemiBold
    )
}
