package app.lantr.lantr.Views.CreationFlow

import androidx.compose.foundation.background
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import app.lantr.lantr.Theme.LantrTheme
import app.lantr.lantr.Models.Audience
import app.lantr.lantr.Views.CreationFlow.components.BackButton
import app.lantr.lantr.Views.CreationFlow.components.DescripitonCreationFlow
import app.lantr.lantr.Views.CreationFlow.components.DropdownMenuEnumClass
import app.lantr.lantr.Views.CreationFlow.components.NextButton
import app.lantr.lantr.Views.CreationFlow.components.PreviousButton
import app.lantr.lantr.Views.CreationFlow.components.TextBoxForCreationFlow
import app.lantr.lantr.Views.CreationFlow.components.TitleCreationFlow

@Composable
fun AudienceGoalStep(
    onNext: () -> Unit,
    onBack: () -> Unit,
    isPhone: Boolean,
    projectTitle: String,
    updateData: (Audience.MainMotivation, String) -> Unit
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

        var mainMotivation by remember { mutableStateOf(Audience.MainMotivation.BE_ENTERTAINED) }
        var description by remember { mutableStateOf("") }

        Column {
            if (isPhone) {
                Spacer(modifier = Modifier.height(20.dp))
                BackButton(onClick = onBack)
                Spacer(modifier = Modifier.height(20.dp))

            }
            TitleCreationFlow(title = projectTitle)

            Spacer(modifier = Modifier.height(4.dp))

            DescripitonCreationFlow(text = "Why will they listen? What do they want?")

            Spacer(modifier = Modifier.height(10.dp))

            DropdownMenuEnumClass(Audience.MainMotivation::class, text = "Main Motivation", getValue = {it.value}, onChange = {mainMotivation = it})

            Spacer(modifier = Modifier.height(20.dp))

            TextBoxForCreationFlow(text1 = "Their Goal/Pain",
                text2 = "What's their big want or struggle? E.g., 'Less stress' or 'Career boost'.",
                onChange = {description = it})

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
                updateData(mainMotivation, description)
            })
        }
    }
}