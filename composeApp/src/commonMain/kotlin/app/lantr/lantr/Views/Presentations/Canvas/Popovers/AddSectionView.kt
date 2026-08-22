package app.lantr.lantr.Views.Presentations.Canvas.Popovers

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.lantr.lantr.Models.StorySectionType
import app.lantr.lantr.Util.PlatformUtil


@Composable
fun AddSectionView(
    onAddSection: (StorySectionType) -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(
                min = 200.dp,
                max = if (PlatformUtil.isPhone) Dp.Infinity else 240.dp
            )
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // Title
        Text(
            text = "Add Section",
            fontSize = if (PlatformUtil.isDesktop) 18.sp else 26.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(top = 16.dp)
                .padding(horizontal = 4.dp)
        )

        // Grid of section types
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 120.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                SectionTypeBox(
                    sectionType = StorySectionType.hook,
                    tintColor = CurtainRed,
                    onAddSection = onAddSection
                )
            }
            item {
                SectionTypeBox(
                    sectionType = StorySectionType.close,
                    tintColor = CurtainRed,
                    onAddSection = onAddSection
                )
            }
            item {
                SectionTypeBox(
                    sectionType = StorySectionType.stakes,
                    tintColor = WallPurple,
                    onAddSection = onAddSection
                )
            }
            item {
                SectionTypeBox(
                    sectionType = StorySectionType.core,
                    tintColor = WallPurple,
                    onAddSection = onAddSection
                )
            }
            item {
                SectionTypeBox(
                    sectionType = StorySectionType.audience,
                    tintColor = AccentColor,
                    onAddSection = onAddSection
                )
            }
            item {
                SectionTypeBox(
                    sectionType = StorySectionType.turn,
                    tintColor = AccentColor,
                    onAddSection = onAddSection
                )
            }
        }

        Spacer(modifier = Modifier.height(0.dp))
    }
}

@Composable
private fun SectionTypeBox(
    sectionType: StorySectionType,
    tintColor: Color,
    onAddSection: (StorySectionType) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(tintColor.copy(alpha = 0.15f))
            .dashedBorder(
                color = tintColor,
                width = 2.8.dp,
                radius = 16.dp,
                dashLength = 8.dp,
                gapLength = 8.dp
            )
            .clickable { onAddSection(sectionType) },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = sectionType.rawValue,
            fontSize = if (PlatformUtil.isDesktop) 16.sp else 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = tintColor
        )
    }
}

// Dashed border extension (reuse from previous code)
private fun Modifier.dashedBorder(
    color: Color,
    width: androidx.compose.ui.unit.Dp = 1.dp,
    radius: androidx.compose.ui.unit.Dp = 0.dp,
    dashLength: androidx.compose.ui.unit.Dp = 4.dp,
    gapLength: androidx.compose.ui.unit.Dp = 4.dp
): Modifier = this.then(
    Modifier.drawWithContent {
        drawContent()

        val pathEffect = PathEffect.dashPathEffect(
            intervals = floatArrayOf(dashLength.toPx(), gapLength.toPx()),
            phase = 0f
        )

        drawRoundRect(
            color = color,
            style = Stroke(
                width = width.toPx(),
                pathEffect = pathEffect
            ),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(radius.toPx())
        )
    }
)

// Colors
val CurtainRed = Color(0xFFDC143C)
val WallPurple = Color(0xFF9370DB)
val AccentColor = Color(0xFF2196F3)
