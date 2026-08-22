package app.lantr.lantr.Views.mainMenu.mainScreen.screens.presentations.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import app.lantr.lantr.Theme.LantrTheme
import app.lantr.lantr.generated.resources.Res
import app.lantr.lantr.generated.resources.search
import org.jetbrains.compose.resources.painterResource

@Composable
fun SearchTextField(
    searchText: String,
    onSearchTextChange: (String) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(LantrTheme.extendedColors.searchBackground)
            .padding(15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Spacer(Modifier.width(1.dp))

        Icon(
            painterResource(Res.drawable.search),
            contentDescription = "search",
            tint = LantrTheme.extendedColors.searchForeground
        )

        Spacer(Modifier.width(8.dp))

        BasicTextField(
            value = searchText,
            onValueChange = onSearchTextChange,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    keyboardController?.hide()
                }
            ),
            singleLine = true,
            decorationBox = { innerTextField ->
                if (searchText.isEmpty()) {
                    Text(
                        "Search your presentations",
                        color = LantrTheme.extendedColors.searchForeground
                    )
                }
                innerTextField()
            }
        )
    }
}