package app.lantr.lantr.Views.mainMenu.mainScreen.screens.settings.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import app.lantr.lantr.Views.mainMenu.mainScreen.screens.settings.components.theme.SearchContainer
import app.lantr.lantr.Views.mainMenu.mainScreen.screens.settings.components.theme.SearchHint
import app.lantr.lantr.Views.mainMenu.mainScreen.screens.settings.components.theme.SearchIcon
import app.lantr.lantr.Views.mainMenu.mainScreen.screens.settings.components.theme.SearchText

@Composable
fun SearchSettingsField(
    query: String,
    onQuery: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQuery,
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null, tint = SearchIcon) },
        placeholder = { Text("Search settings", color = SearchHint) },
        textStyle = LocalTextStyle.current.copy(color = SearchText),
        shape = RoundedCornerShape(28.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = SearchContainer,
            unfocusedContainerColor = SearchContainer,
            disabledContainerColor = SearchContainer,
            focusedTextColor = SearchText,
            unfocusedTextColor = SearchText,
            cursorColor = SearchText,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )
    )
}
