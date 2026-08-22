package app.lantr.lantr.Views.CreationFlow.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.lantr.lantr.Theme.LantrTheme

@Composable
fun TitleCreationFlow(
    title: String = "Ceva titlu"
) {
    Text(
        text = title,
        modifier = Modifier.fillMaxWidth(),
        fontSize = 30.sp
    )
}

@Composable
fun DescripitonCreationFlow(
    text: String = ""
) {
    Text(
        text = text,
        modifier = Modifier.fillMaxWidth(),
        fontSize = 18.sp
    )
}

@Composable
fun TextBoxForCreationFlow(
    text1: String,
    text2: String,
    onChange: (String) -> Unit
) {

    var text by remember { mutableStateOf("") }

    Column(

    ) {
        Text(text = text1, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = text,
            onValueChange = {
                text = it
                onChange(it)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = LantrTheme.extendedColors.inputFieldBackground,
                unfocusedContainerColor = LantrTheme.extendedColors.inputFieldBackground,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            placeholder = { Text(text = text2, color = LantrTheme.extendedColors.placeholderText)},
            maxLines = 10,
            textStyle = TextStyle(fontSize = 15.sp),
        )
    }
}


@Composable
fun TextBoxForStoryBlock(
    text2: String,
    onChange: (String) -> Unit
) {

    var text by remember { mutableStateOf("") }
    TextField(
        value = text,
        onValueChange = {
            text = it
            onChange(it) },
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(RoundedCornerShape(16.dp)),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.background,
            unfocusedContainerColor = MaterialTheme.colorScheme.background,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        placeholder = { Text(text = text2, color = LantrTheme.extendedColors.placeholderText)},
        maxLines = 10,
        textStyle = TextStyle(fontSize = 15.sp),
        )
}