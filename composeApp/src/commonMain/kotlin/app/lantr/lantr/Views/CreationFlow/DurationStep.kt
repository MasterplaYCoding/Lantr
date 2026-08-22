package app.lantr.lantr.Views.CreationFlow

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.material3.MaterialTheme
import app.lantr.lantr.Theme.LantrTheme
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.lantr.lantr.Models.Audience
import app.lantr.lantr.Models.PresentationDuration
import app.lantr.lantr.Views.CreationFlow.components.BackButton
import app.lantr.lantr.Views.CreationFlow.components.CancelButton
import app.lantr.lantr.Views.CreationFlow.components.DescripitonCreationFlow
import app.lantr.lantr.Views.CreationFlow.components.NextButton
import app.lantr.lantr.Views.CreationFlow.components.PreviousButton
import app.lantr.lantr.Views.CreationFlow.components.TitleCreationFlow
import app.lantr.lantr.Views.mainMenu.mainScreen.screens.resources.ResourcesScreen
import app.lantr.lantr.generated.resources.Res
import app.lantr.lantr.generated.resources.bolt
import app.lantr.lantr.generated.resources.cancel
import app.lantr.lantr.generated.resources.groups
import app.lantr.lantr.generated.resources.insert_chart
import app.lantr.lantr.generated.resources.mic
import app.lantr.lantr.generated.resources.play_circle
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import javax.swing.Icon

@Composable
fun DurationStep(
    onNext: () -> Unit,
    onBack: () -> Unit,
    isPhone: Boolean,
    projectTitle: String,
    updateData: (PresentationDuration) -> Unit

) {

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .then(
                if(isPhone)
                    Modifier.padding(vertical = 40.dp)
                        .padding(horizontal = 16.dp)
                        .fillMaxSize()

                else
                    Modifier.padding(vertical = 0.dp)
                        .padding(horizontal = 1.dp)
            )
            .imePadding(),

        verticalArrangement = if (isPhone)
            Arrangement.SpaceBetween
        else
            Arrangement.Center

    ) {
        val items = listOf(
            Pair(PresentationDuration.THIRTY_SECONDS, Res.drawable.bolt),
            Pair(PresentationDuration.FIVE_MINUTES, Res.drawable.play_circle),
            Pair(PresentationDuration.FIFTEEN_MINUTES, Res.drawable.mic),
            Pair(PresentationDuration.THIRTY_MINUTES, Res.drawable.insert_chart),
            Pair(PresentationDuration.OVER_ONE_HOUR, Res.drawable.groups)
        )

        var selectedDuration by remember { mutableStateOf<PresentationDuration>(PresentationDuration.THIRTY_SECONDS) }

        Column {
            if (isPhone) {
                Spacer(modifier = Modifier.height(20.dp))
                BackButton(onClick = onBack)
                Spacer(modifier = Modifier.height(20.dp))

            }

            TitleCreationFlow(title = projectTitle)

            Spacer(modifier = Modifier.height(4.dp))

            DescripitonCreationFlow(text = "Now, let's settle how much stage time you'll have. This helps us plan the depth level of the content.")

            Spacer(modifier = Modifier.height(15.dp))

            FlowRow(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = if(isPhone) Arrangement.spacedBy(24.dp)
                else Arrangement.SpaceBetween,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items.forEach { duration ->
                    Box(
                        modifier = Modifier.clickable {
                            selectedDuration = duration.first
                        }
                    ) {
                        DurationBlock(
                            duration = duration.first,
                            icon = duration.second,
                            isPressed = selectedDuration == duration.first
                        )
                    }
                }
            }
        }

        if(!isPhone)
            Spacer(modifier = Modifier.height(40.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {

            if(!isPhone) {
                PreviousButton(onClick = onBack)

                Spacer(modifier = Modifier.width(20.dp))
            }
            NextButton(onClick = {
                onNext();
                updateData(selectedDuration)
            })
        }
    }
}

@Composable
fun DurationBlock(
    duration: PresentationDuration,
    icon: DrawableResource,
    isPressed: Boolean
){
    // Animate all style changes
    val unselectedBg = LantrTheme.extendedColors.chipBackground
    val selectedBg = LantrTheme.extendedColors.selectionIcon
    val onSelectedBg = LantrTheme.extendedColors.onGold
    val onUnselectedBg = LantrTheme.extendedColors.selectionIcon
    val normalText = MaterialTheme.colorScheme.onBackground

    val backgroundColor by animateColorAsState(
        targetValue = if (!isPressed) unselectedBg else selectedBg,
        animationSpec = tween(durationMillis = 300)
    )

    val iconTint by animateColorAsState(
        targetValue = if (isPressed) onSelectedBg else onUnselectedBg,
        animationSpec = tween(durationMillis = 300)
    )

    val textColor by animateColorAsState(
        targetValue = if (isPressed) onSelectedBg else normalText,
        animationSpec = tween(durationMillis = 300)
    )

    val cornerRadius by animateDpAsState(
        targetValue = if (isPressed) 30.dp else 6.dp,
        animationSpec = tween(durationMillis = 300)
    )

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )
    )

    Column(
        modifier = Modifier.scale(scale)
            .width(110.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(cornerRadius))
                .background(backgroundColor)
                .padding(vertical = 12.dp, horizontal = 17.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painterResource(icon),
                contentDescription = "",
                tint = iconTint
            )

            Spacer(modifier = Modifier.width(3.dp))

            Text(
                text = duration.value,
                color = textColor
            )
        }

        Spacer(modifier = Modifier.height(1.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.TopCenter
        ) {
            Text(
                text = duration.description,
                fontSize = 12.sp,
                lineHeight = 17.sp,
                textAlign = TextAlign.Center,
                style = TextStyle(
                    lineBreak = LineBreak.Paragraph,
                    hyphens = Hyphens.None
                )
            )
        }
    }
}