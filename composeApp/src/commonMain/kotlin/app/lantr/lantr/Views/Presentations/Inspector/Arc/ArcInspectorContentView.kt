package app.lantr.lantr.Views.Presentations.Inspector.Arc

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.lantr.lantr.Models.Slide
import app.lantr.lantr.Models.StoryBlockCategory
import app.lantr.lantr.Theme.LantrTheme
import app.lantr.lantr.Util.PlatformUtil
import app.lantr.lantr.ViewModel.PresentationViewModel
import app.lantr.lantr.Views.Presentations.Canvas.Toolbar.ToolbarButton
import app.lantr.lantr.Views.Presentations.Canvas.Toolbar.ToolbarButtonGroup
import app.lantr.lantr.Views.Presentations.Canvas.Toolbar.ToolbarDivider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArcInspectorContentView(
    viewModel: PresentationViewModel,
    modifier: Modifier = Modifier,
    onSlideUpdate: (Slide) -> Unit
) {
    var selectedStoryBlocksCategory by remember { mutableStateOf<StoryBlockCategory?>(null) }
    var selectedInspectorView by remember { mutableIntStateOf(0) }

    val titles = listOf("Blocks", "Director", "Context")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        if (selectedStoryBlocksCategory == null) {
            Column(
                verticalArrangement = Arrangement.spacedBy(0.dp),
                horizontalAlignment = Alignment.Start
            ) {
                // Title (mobile only)
                if (!PlatformUtil.isDesktop) {
                    Text(
                        text = titles[selectedInspectorView],
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(top = 20.dp)
                            .padding(horizontal = 4.dp)
                    )
                }

                ToolbarButtonGroup {
                    ToolbarButton(
                        title = "Blocks",
                        icon = null,
                        isActive = selectedInspectorView == 0,
                        isDisabled = false,
                        helpText = "Switch to Arc Mode",
                        backgroundColor = if (selectedInspectorView == 0) LantrTheme.extendedColors.arcInspectorToggleMenuSelected else LantrTheme.extendedColors.arcInspectorToggleMenuUnselected,
                        tintColor = MaterialTheme.colorScheme.onBackground,
                        onClick = {
                            selectedInspectorView = 0
                        },
                        modifier = Modifier.weight(1f)
                    )
                    ToolbarDivider()
                    ToolbarButton(
                        title = "Director",
                        icon = null,
                        isActive = selectedInspectorView == 1,
                        isDisabled = false,
                        backgroundColor = if (selectedInspectorView == 1) LantrTheme.extendedColors.arcInspectorToggleMenuSelected else LantrTheme.extendedColors.arcInspectorToggleMenuUnselected,
                        tintColor = MaterialTheme.colorScheme.onBackground,
                        helpText = "Switch to Slides Mode",
                        onClick = {
                            selectedInspectorView = 1
                        },
                        modifier = Modifier.weight(1f)
                    )
                    ToolbarDivider()
                    ToolbarButton(
                        title = "Context",
                        icon = null,
                        isActive = selectedInspectorView == 2,
                        isDisabled = false,
                        backgroundColor = if (selectedInspectorView == 2) LantrTheme.extendedColors.arcInspectorToggleMenuSelected else LantrTheme.extendedColors.arcInspectorToggleMenuUnselected,
                        tintColor = MaterialTheme.colorScheme.onBackground,
                        helpText = "Switch to Slides Mode",
                        onClick = {
                            selectedInspectorView = 2
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Content based on selected inspector view
            when (selectedInspectorView) {
                0 -> StoryBlocksInspectorListView(
                    selectedCategory = selectedStoryBlocksCategory,
                    onCategorySelected = { selectedStoryBlocksCategory = it }
                )
                1 -> {}// AssistantInspectorContentView()
                2 -> {}// ContextInspectorContentView()
            }
        } else {
            // Category detail view
            StoryBlocksCategoryView(
                selectedCategory = selectedStoryBlocksCategory,
                onBack = { selectedStoryBlocksCategory = null },
            )
        }
    }
}

@Composable
private fun SegmentedControl(
    selectedIndex: Int,
    items: List<String>,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    TabRow(
        selectedTabIndex = selectedIndex,
        modifier = modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        indicator = { tabPositions ->
            if (selectedIndex < tabPositions.size) {
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    ) {
        items.forEachIndexed { index, title ->
            Tab(
                selected = selectedIndex == index,
                onClick = { onItemSelected(index) },
                text = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            )
        }
    }
}
