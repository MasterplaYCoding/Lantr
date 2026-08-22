package app.lantr.lantr.Views.CreationFlow

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.MaterialTheme
import app.lantr.lantr.Theme.LantrTheme
import app.lantr.lantr.Models.PresentationPurpose
import app.lantr.lantr.Views.CreationFlow.components.BackButton
import app.lantr.lantr.Views.CreationFlow.components.DescripitonCreationFlow
import app.lantr.lantr.Views.CreationFlow.components.DoneButton
import app.lantr.lantr.Views.CreationFlow.components.PreviousButton
import app.lantr.lantr.Views.CreationFlow.components.TitleCreationFlow
import app.lantr.lantr.generated.resources.Res
import app.lantr.lantr.generated.resources.check_circle
import app.lantr.lantr.generated.resources.circle
import org.jetbrains.compose.resources.painterResource

@Composable
fun MotivationStep(
    onNext: () -> Unit,
    onBack: () -> Unit,
    isPhone: Boolean,
    projectTitle: String,
    updateData: () -> Unit

) {

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
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

        val list = mutableSetOf<PresentationPurpose>()
        var optionsSelected by remember { mutableStateOf(0) }

        Column {
            if (isPhone) {
                Spacer(modifier = Modifier.height(20.dp))
                BackButton(onClick = onBack)
                Spacer(modifier = Modifier.height(20.dp))

            }
            TitleCreationFlow(title = projectTitle)

            Spacer(modifier = Modifier.height(4.dp))

            DescripitonCreationFlow(text = "Lastly, why are you holding this talk? Select up to 3 reasons from below.")

            Spacer(modifier = Modifier.height(10.dp))

            Text(text =  "${optionsSelected}/3 selected", color = Color(0xff858585), fontSize = 18.sp)

            PresentationPurpose.entries.forEach { enumValue ->
                SelectableOption(enumValue, optionsSelected,
                    onAdd = {
                        list.add(it)
                        optionsSelected++
                    },
                    onRemove = {
                        list.remove(it)
                        optionsSelected--
                    })

                Spacer(modifier = Modifier.height(4.dp))
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
            DoneButton(onClick = {
                onNext();
                //updateData(mainMotivation, description)
            })
        }
    }
}

@Composable
fun SelectableOption(
    purpose: PresentationPurpose,
    optionsSelected: Int,
    onAdd: (PresentationPurpose) -> Unit,
    onRemove: (PresentationPurpose) -> Unit
)
{
    var isSelected by remember { mutableStateOf(false) }

    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) Color(0x40852727) else Color(0xffEFEFEF),
        animationSpec = tween(durationMillis = 300)
    )

    val borderColor by animateColorAsState(
        targetValue = if (isSelected) Color(0x80852727) else Color(0xffEFEFEF),
        animationSpec = tween(durationMillis = 300)
    )

    val borderWidth by animateDpAsState(
        targetValue = if (isSelected) 2.dp else 2.dp,
        animationSpec = tween(durationMillis = 300)
    )

    val iconTint by animateColorAsState(
        targetValue = if (isSelected) Color(0xff852727) else Color(0xff9C9C9C),
        animationSpec = tween(durationMillis = 300)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .border(width = borderWidth, color = borderColor, shape = RoundedCornerShape(16.dp))
            .padding(4.dp)
            .clickable {
                if(isSelected) {
                    onRemove(purpose)
                    isSelected = false
                }
                else {
                    if(optionsSelected < 3) {
                        onAdd(purpose)
                        isSelected = true
                    }
                }
            },
        verticalAlignment = Alignment.CenterVertically
    ) {

        Spacer(modifier = Modifier.width(11.dp))

        Icon(
            painterResource(
                if (isSelected) Res.drawable.check_circle
                else Res.drawable.circle
            ),
            contentDescription = "",
            tint = iconTint
        )

        Spacer(modifier = Modifier.width(11.dp))

        Column(
            modifier = Modifier
        ) {
            Text(text = purpose.value, color = Color(0xff000000), fontSize = 18.sp, fontWeight = FontWeight.SemiBold)

            Spacer(modifier = Modifier.height(2.dp))

            Text(text =  purpose.description, color = Color(0xff777777), fontSize = 16.sp)
        }
    }

}
