package app.lantr.lantr.Views.CreationFlow.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.lantr.lantr.Models.Audience
import app.lantr.lantr.Theme.LantrTheme
import kotlin.reflect.KClass

@Composable
fun <T : Enum<T>> DropdownMenuEnumClass(
    enumClass: KClass<T>,
    text: String,
    getValue: (T) -> String = { it.name },
    onChange: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf("Select") }


    Box(modifier = Modifier.fillMaxWidth()) {
        // Trigger button

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(LantrTheme.extendedColors.chipBackground)
                .clickable { expanded = !expanded }
                .padding(start = 10.dp, top = 4.dp, bottom = 4.dp, end =  4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(text = text)

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(LantrTheme.extendedColors.goldSubtle)
                    .padding(4.dp)
            ) {
                Text(text = selectedText, color = LantrTheme.extendedColors.goldPrimary)
            }
        }

        // Dropdown menu
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            // Get all enum values from the class
            enumClass.java.enumConstants?.forEach { enumValue ->
                val displayValue = getValue(enumValue)
                DropdownMenuItem(
                    text = { Text(displayValue) },
                    onClick = {
                        selectedText = displayValue
                        expanded = false
                        onChange(enumValue)
                    }
                )
            }
        }
    }
}