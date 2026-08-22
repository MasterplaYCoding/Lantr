package app.lantr.lantr.Views.Presentations.Canvas.Popovers

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.lantr.lantr.Models.Presentation
import app.lantr.lantr.Util.PlatformUtil
import app.lantr.lantr.ViewModel.PresentationViewModel
import java.text.SimpleDateFormat
import java.util.*


@Composable
fun PresentationInfoView(
    viewModel: PresentationViewModel,
    modifier: Modifier = Modifier
) {
    val presentation by viewModel.presentation.collectAsState()
    var selectedCategory by remember { mutableIntStateOf(0) }

    val isPhone = PlatformUtil.isPhone

    val minimumHeights = listOf(260.dp, 300.dp, 220.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(
                min = 200.dp,
                max = (if (isPhone) true  else minimumHeights[selectedCategory]) as Dp
            )
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Information",
                fontSize = if (PlatformUtil.isDesktop) 18.sp else 26.sp,
                fontWeight = FontWeight.Bold
            )

            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(28.dp)
                    .clickable {
                        println("Edit info tapped")
                    }
            )
        }

        // Segmented control
        TabRow(
            selectedTabIndex = selectedCategory,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            Tab(
                selected = selectedCategory == 0,
                onClick = { selectedCategory = 0 },
                text = { Text("General") }
            )
            Tab(
                selected = selectedCategory == 1,
                onClick = { selectedCategory = 1 },
                text = { Text("Audience") }
            )
            Tab(
                selected = selectedCategory == 2,
                onClick = { selectedCategory = 2 },
                text = { Text("Purpose") }
            )
        }

        // Content
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .padding(bottom = 12.dp)
        ) {
            when (selectedCategory) {
                0 -> GeneralView(presentation)
                1 -> AudienceView(presentation)
                2 -> PurposeView(presentation)
            }
        }

        Spacer(modifier = Modifier.height(0.dp))
    }
}

@Composable
private fun GeneralView(
    presentation: Presentation,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PresentationInfoRow(
            label = "Kind",
            value = "Lantr Presentation"
        )
        HorizontalDivider()

        PresentationInfoRow(
            label = "Author",
            value = "David Retegan"
        )
        HorizontalDivider()

        PresentationInfoRow(
            label = "Duration",
            value = presentation.duration.value
        )
        HorizontalDivider()

        PresentationInfoRow(
            label = "Created",
            value = presentation.createdAt.formattedDateString()
        )
    }
}

@Composable
private fun AudienceView(
    presentation: Presentation,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PresentationInfoRow(
            label = "Group Size",
            value = presentation.audience.groupSize.value
        )
        HorizontalDivider()

        PresentationInfoRow(
            label = "Main Role",
            value = presentation.audience.mainRole.value
        )
        HorizontalDivider()

        PresentationInfoRow(
            label = "Age Group",
            value = presentation.audience.ageGroup.value
        )
        HorizontalDivider()

        PresentationInfoRow(
            label = "Knowledge Level",
            value = presentation.audience.knowledgeLevel.value
        )
        HorizontalDivider()

        PresentationInfoRow(
            label = "Main Motivation",
            value = presentation.audience.mainMotivation.value
        )
    }
}

@Composable
private fun PurposeView(
    presentation: Presentation,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Reasons",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        presentation.purpose.forEach { purpose ->
            Text(
                text = "→ ${purpose.value}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun PresentationInfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray
        )

        Text(
            text = value,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

// Extension function for date formatting
fun Date.formattedDateString(): String {
    val format = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return format.format(this)
}
