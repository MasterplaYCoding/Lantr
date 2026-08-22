package app.lantr.lantr.Views.CreationFlow

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.sp
import app.lantr.lantr.Models.Audience
import app.lantr.lantr.Views.CreationFlow.components.BackButton
import app.lantr.lantr.Views.CreationFlow.components.CancelButton
import app.lantr.lantr.Views.CreationFlow.components.DescripitonCreationFlow
import app.lantr.lantr.Views.CreationFlow.components.NextButton
import app.lantr.lantr.Views.CreationFlow.components.TitleCreationFlow
import app.lantr.lantr.Views.mainMenu.mainScreen.screens.resources.ResourcesScreen
import app.lantr.lantr.generated.resources.Res
import app.lantr.lantr.generated.resources.cancel
import org.jetbrains.compose.resources.painterResource

@Composable
fun NameStep(
    onNext: () -> Unit,
    onBack: () -> Unit,
    isPhone: Boolean,
    updateData: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }

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
        Column {
            if (isPhone) {
                Spacer(modifier = Modifier.height(20.dp))
                BackButton(onClick = onBack)
                Spacer(modifier = Modifier.height(20.dp))

            }
            TitleCreationFlow(title = "New Presentation")

            Spacer(modifier = Modifier.height(4.dp))

            DescripitonCreationFlow(text = "Let's start off with a title")

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Title") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = {
                        text = ""
                    }) {
                        Icon(painterResource(Res.drawable.cancel), contentDescription = "")
                    }
                },

                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFCB9D08),
                    unfocusedBorderColor = Color(0xFFCB9D08),
                    focusedLabelColor = Color(0xFFCB9D08),
                    cursorColor = Color(0xFFCB9D08),
                )
            )
        }

        if(!isPhone)
            Spacer(modifier = Modifier.height(40.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {

            if(!isPhone) {
                CancelButton(onClick = onBack)

                Spacer(modifier = Modifier.width(20.dp))
            }
            NextButton(onClick = {
                onNext();
                updateData(text)
            })
        }
    }
}