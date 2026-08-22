package app.lantr.lantr.Util

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale

expect fun requestPickPresentationImage(onImageSelected: (String?) -> Unit)

@Composable
expect fun PlatformImageFromPath(
    imagePath: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit
)
