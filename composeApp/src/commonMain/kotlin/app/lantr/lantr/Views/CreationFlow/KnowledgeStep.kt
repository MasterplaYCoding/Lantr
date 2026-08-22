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
import app.lantr.lantr.Models.Audience
import app.lantr.lantr.Views.CreationFlow.components.BackButton
import app.lantr.lantr.Views.CreationFlow.components.DescripitonCreationFlow
import app.lantr.lantr.Views.CreationFlow.components.DropdownMenuEnumClass
import app.lantr.lantr.Views.CreationFlow.components.NextButton
import app.lantr.lantr.Views.CreationFlow.components.PreviousButton
import app.lantr.lantr.Views.CreationFlow.components.TextBoxForCreationFlow
import app.lantr.lantr.Views.CreationFlow.components.TitleCreationFlow

@Composable
fun KnowledgeStep(
    onNext: () -> Unit,
    onBack: () -> Unit,
    isPhone: Boolean,
    projectTitle: String,
    updateData: (Audience.KnowledgeLevel, String) -> Unit

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

        var knowledgeLevel by remember { mutableStateOf(Audience.KnowledgeLevel.ADVANCED) }
        var description by remember { mutableStateOf("") }

        Column {
            if (isPhone) {
                Spacer(modifier = Modifier.height(20.dp))
                BackButton(onClick = onBack)
                Spacer(modifier = Modifier.height(20.dp))

            }
            TitleCreationFlow(title = projectTitle)

            Spacer(modifier = Modifier.height(4.dp))

            DescripitonCreationFlow(text = "What's on their mind? How do they see your topic?")

            Spacer(modifier = Modifier.height(10.dp))

            DropdownMenuEnumClass(Audience.KnowledgeLevel::class, text = "Knowledge Level", getValue = {it.value}, onChange = {knowledgeLevel = it})

            Spacer(modifier = Modifier.height(20.dp))

            TextBoxForCreationFlow(text1 = "Current Opinion",
                text2 = "How do they feel about your topic? E.g., 'Skeptical of AI' or 'Excited but confused'.",
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
                updateData(knowledgeLevel, description)
            })
        }
    }
}