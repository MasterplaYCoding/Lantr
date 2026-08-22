package app.lantr.lantr.Views.Presentations.Canvas.Toolbar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import app.lantr.lantr.Models.Arc
import app.lantr.lantr.Models.StorySectionType
import app.lantr.lantr.Util.PlatformUtil
import app.lantr.lantr.ViewModel.ArcViewModel
import app.lantr.lantr.ViewModel.PresentationViewModel
import app.lantr.lantr.Views.DragAndDrop.DragData
import app.lantr.lantr.Views.DragAndDrop.DragType
import app.lantr.lantr.Views.DragAndDrop.rememberDragAndDropManager
import app.lantr.lantr.Views.Presentations.Canvas.StorySectionTemplateCard
import app.lantr.lantr.Views.Presentations.Canvas.sectionTemplatePayload
import app.lantr.lantr.generated.resources.Res
import app.lantr.lantr.generated.resources.add_circle
import app.lantr.lantr.generated.resources.electric_bolt
import app.lantr.lantr.generated.resources.timeline
import app.lantr.lantr.generated.resources.transition_chop
import kotlinx.coroutines.delay

@Composable
fun ArcCanvasToolbar(
    arc: Arc,
    onArcChange: (Arc) -> Unit = {},
    arcViewModel: ArcViewModel,
    onEditorModeChange: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showBrainDump by remember { mutableStateOf(false) }
    var showAddSection by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(if (PlatformUtil.isDesktop) 46.dp else 52.dp)
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        EditorGroup(
            onEditorModeChange = onEditorModeChange
        )

        BrainDumpGroup(
            onShowBrainDumpChange = { showBrainDump = it }
        )

        AddSectionGroup(
            showAddSection = showAddSection,
            onShowAddSectionChange = { showAddSection = it },
            onAddSection = { sectionType ->
                arcViewModel.addStorySection(sectionType)
                showAddSection = false
            }
        )

        AnalysisGroup()
    }

    if (showBrainDump) {
        BrainDumpDialog(
            onDismiss = { showBrainDump = false }
        )
    }
}

@Composable
private fun EditorGroup(
    onEditorModeChange: () -> Unit,
    modifier: Modifier = Modifier
) {
    ToolbarGroup {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Editor",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(start = 6.dp)
            )
            ToolbarButtonGroup {
                ToolbarButton(
                    title = "Arc",
                    icon = Res.drawable.timeline,
                    isActive = true,
                    isDisabled = false,
                    helpText = "Switch to Arc Mode",
                    backgroundColor = MaterialTheme.colorScheme.primary,
                    tintColor = MaterialTheme.colorScheme.background,
                    onClick = {}
                )
                ToolbarDivider()
                ToolbarButton(
                    title = "Slides",
                    icon = Res.drawable.transition_chop,
                    isActive = false,
                    isDisabled = false,
                    helpText = "Switch to Slides Mode",
                    backgroundColor = MaterialTheme.colorScheme.surface,
                    tintColor = MaterialTheme.colorScheme.onSurface,
                    onClick = onEditorModeChange
                )
            }
        }
    }
}

@Composable
private fun BrainDumpGroup(
    onShowBrainDumpChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    ToolbarGroup {
        ToolbarButton(
            title = "Brain Dump",
            icon = Res.drawable.electric_bolt,
            isActive = true,
            helpText = "Turn ideas into blocks",
            backgroundColor = MaterialTheme.colorScheme.secondary,
            tintColor = MaterialTheme.colorScheme.background,
            onClick = { onShowBrainDumpChange(true) }
        )
    }
}

@Composable
private fun AddSectionGroup(
    showAddSection: Boolean,
    onShowAddSectionChange: (Boolean) -> Unit,
    onAddSection: (StorySectionType) -> Unit,
    modifier: Modifier = Modifier
) {
    val dragDropManager = rememberDragAndDropManager()
    var isDraggingTemplate by remember { mutableStateOf(false) }
    val density = LocalDensity.current
    val popupOffsetY = with(density) { 44.dp.roundToPx() }

    LaunchedEffect(isDraggingTemplate) {
        if (isDraggingTemplate) {
            onShowAddSectionChange(false)
        }
    }

    ToolbarGroup {
        Box {
            ToolbarButton(
                title = "Add Section",
                icon = Res.drawable.add_circle,
                isActive = true,
                helpText = "Add section",
                backgroundColor = MaterialTheme.colorScheme.primary,
                tintColor = MaterialTheme.colorScheme.background,
                onClick = { onShowAddSectionChange(!showAddSection) }
            )

            if (showAddSection || isDraggingTemplate) {
                Popup(
                    alignment = Alignment.TopStart,
                    offset = IntOffset(0, popupOffsetY),
                    onDismissRequest = {
                        if (!isDraggingTemplate) {
                            onShowAddSectionChange(false)
                        }
                    }
                ) {
                    Surface(
                        modifier = Modifier
                            .width(320.dp)
                            .graphicsLayer {
                                alpha = if (isDraggingTemplate) 0f else 1f
                            },
                        color = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        tonalElevation = if (isDraggingTemplate) 0.dp else 4.dp,
                        shadowElevation = if (isDraggingTemplate) 0.dp else 8.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "Add Section",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            StorySectionType.values().forEach { sectionType ->
                                DraggableSectionTemplateCard(
                                    sectionType = sectionType,
                                    dragDropManager = dragDropManager,
                                    onDragStart = {
                                        isDraggingTemplate = true
                                    },
                                    onDragEnd = { isDraggingTemplate = false },
                                    onClick = { onAddSection(sectionType) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DraggableSectionTemplateCard(
    sectionType: StorySectionType,
    dragDropManager: app.lantr.lantr.Views.DragAndDrop.DragAndDropManager,
    onDragStart: () -> Unit,
    onDragEnd: () -> Unit,
    onClick: () -> Unit
) {
    var isDragging by remember { mutableStateOf(false) }

    Box(
        modifier = dragDropManager.createDragModifier(
            data = DragData(
                type = DragType.STORY_SECTION,
                id = sectionType.name,
                sourceIndex = -1,
                payload = sectionTemplatePayload(sectionType)
            ),
            onDragStart = {
                isDragging = true
                onDragStart()
            },
            onDragEnd = {
                isDragging = false
                onDragEnd()
            },
            dragPreview = {
                StorySectionTemplateCard(
                    sectionType = sectionType,
                    modifier = Modifier.width(280.dp)
                )
            }
        )
            .graphicsLayer {
                alpha = if (isDragging) 0.5f else 1f
                scaleX = if (isDragging) 0.98f else 1f
                scaleY = if (isDragging) 0.98f else 1f
            }
            .clickable(onClick = onClick)
    ) {
        StorySectionTemplateCard(sectionType = sectionType)
    }
}

@Composable
private fun AnalysisGroup(
    modifier: Modifier = Modifier
) {
    ToolbarGroup {
        ToolbarButton(
            title = "Analysis",
            icon = Res.drawable.timeline,
            isActive = true,
            helpText = "Director analysis",
            backgroundColor = MaterialTheme.colorScheme.tertiary,
            tintColor = MaterialTheme.colorScheme.background,
            onClick = {
                // TODO: Show analysis
            }
        )
    }
}

@Composable
private fun BrainDumpDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Brain Dump") },
        text = { BrainDumpView() },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done")
            }
        }
    )
}

@Composable
private fun BrainDumpView() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("Brain Dump View - Coming Soon")
    }
}

val LocalPresentationViewModel = compositionLocalOf<PresentationViewModel> {
    error("No PresentationViewModel provided")
}
