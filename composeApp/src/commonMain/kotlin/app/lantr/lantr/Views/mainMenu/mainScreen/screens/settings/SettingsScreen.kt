package app.lantr.lantr.Views.mainMenu.mainScreen.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.overscroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.lantr.lantr.Models.models_UI.SettingsSectionUI
import app.lantr.lantr.Util.PlatformUtil
import app.lantr.lantr.Views.mainMenu.mainScreen.screens.settings.components.AccountCell
import app.lantr.lantr.Views.mainMenu.mainScreen.screens.settings.components.SearchSettingsField
import app.lantr.lantr.Views.mainMenu.mainScreen.screens.settings.components.SectionHeader
import app.lantr.lantr.Views.mainMenu.mainScreen.screens.settings.components.SettingsSectionCard
import app.lantr.lantr.Views.mainMenu.mainScreen.screens.settings.components.theme.BgScreen
import app.lantr.lantr.Views.mainMenu.mainScreen.screens.settings.components.theme.BubbleGold
import app.lantr.lantr.Views.mainMenu.mainScreen.screens.settings.components.theme.BubbleLilac
import app.lantr.lantr.Views.mainMenu.mainScreen.screens.settings.components.theme.BubblePink
import app.lantr.lantr.Views.mainMenu.mainScreen.screens.settings.components.theme.IconBurgundy
import app.lantr.lantr.Views.mainMenu.mainScreen.screens.settings.components.theme.IconGold
import app.lantr.lantr.Views.mainMenu.mainScreen.screens.settings.components.theme.IconPink
import app.lantr.lantr.Views.mainMenu.mainScreen.screens.settings.components.theme.TitleOnBg
import app.lantr.lantr.ui.icons.AppIcons

private val EdgePadding = 12.dp
private val TopPadding = 20.dp
private val PillGray = Color(0xFFF0F0F0)
private val BugText = IconBurgundy
private val SubscriptionText = Color(0xFFD61F2C)
private val SuggestionText = Color(0xFFCC9E00)

@Composable
fun SettingsScreen(
    onRoute: (String) -> Unit = {}
) {
    var query by remember { mutableStateOf("") }
    var haptic by remember { mutableStateOf(true) }
    var sounds by remember { mutableStateOf(false) }
    var showContactSheet by rememberSaveable { mutableStateOf(false) }

    val account = settingsAccountCard(onAccount = { onRoute("account") })
    val sections = remember(query, haptic, sounds) {
        settingsSections(
            haptic = haptic,
            sounds = sounds,
            onHapticToggle = { haptic = it },
            onSoundsToggle = { sounds = it },
            onEnterBehaviour = { onRoute("enter_behaviour") },
            onInterventionLevel = { onRoute("intervention_level") },
            onPersonalization = { onRoute("personalization") },
            onSmartFormatting = { onRoute("smart_formatting") },
            onContactUs = { showContactSheet = true },
            smartFormattingIcon = AppIcons.Pilcrow
        ).filterByQuery(query)
    }

    Column(
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.statusBars.only(WindowInsetsSides.Top))
            .fillMaxSize()
            .background(if (PlatformUtil.isDesktop) Color.Transparent else BgScreen)
            .padding(top = TopPadding)
            .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Bottom))
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Medium),
            color = TitleOnBg,
            modifier = Modifier.padding(start = EdgePadding, end = EdgePadding, bottom = 16.dp)
        )

        SearchSettingsField(
            query = query,
            onQuery = { query = it },
            modifier = Modifier.padding(horizontal = EdgePadding)
        )

        Spacer(Modifier.height(12.dp))

        val listTopShape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)

        androidx.compose.material3.Surface(
            color = if (PlatformUtil.isDesktop) Color.Transparent else BgScreen,
            shape = listTopShape,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(if (PlatformUtil.isDesktop) Color.Transparent else BgScreen)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .overscroll(ScrollableDefaults.overscrollEffect()),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    item {
                        AccountCell(
                            name = account.name,
                            subtitle = account.subtitle,
                            onClick = account.onClick
                        )
                    }

                    sections.forEach { section ->
                        item {
                            val (bubbleBackground, bubbleTint) = when (section.title) {
                                "General" -> BubblePink to IconPink
                                "Other" -> BubbleLilac to IconBurgundy
                                else -> BubbleGold to IconGold
                            }

                            Column(Modifier.fillMaxWidth()) {
                                SectionHeader(section.title)
                                Spacer(Modifier.height(4.dp))
                                SettingsSectionCard(
                                    items = section.items,
                                    showChevron = false,
                                    iconBubbleBackground = bubbleBackground,
                                    iconBubbleTint = bubbleTint
                                )
                            }
                        }
                        item { Spacer(Modifier.height(2.dp)) }
                    }
                }
            }
        }

        if (showContactSheet) {
            ContactUsSheet(
                onDismiss = { showContactSheet = false },
                onBug = {
                    showContactSheet = false
                    onRoute("contact_bug")
                },
                onSubscription = {
                    showContactSheet = false
                    onRoute("contact_subscription")
                },
                onSuggestion = {
                    showContactSheet = false
                    onRoute("contact_suggestion")
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContactUsSheet(
    onDismiss: () -> Unit,
    onBug: () -> Unit,
    onSubscription: () -> Unit,
    onSuggestion: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = {},
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = EdgePadding, vertical = 12.dp)
                .navigationBarsPadding()
        ) {
            Column(horizontalAlignment = Alignment.Start) {
                Icon(
                    imageVector = Icons.Filled.Campaign,
                    contentDescription = null,
                    tint = IconBurgundy,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Contact Us",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = TitleOnBg
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "Choose how we can help",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF7B7B7B)
                )
            }

            Spacer(Modifier.height(16.dp))

            ContactOptionButton(
                text = "I have a bug",
                icon = Icons.Filled.BugReport,
                textColor = BugText,
                background = PillGray,
                onClick = onBug
            )
            ContactOptionButton(
                text = "I have a subscription problem",
                icon = Icons.Filled.ReceiptLong,
                textColor = SubscriptionText,
                background = PillGray,
                onClick = onSubscription
            )
            ContactOptionButton(
                text = "I have a suggestion",
                icon = Icons.Filled.Favorite,
                textColor = SuggestionText,
                background = PillGray,
                onClick = onSuggestion
            )

            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun ContactOptionButton(
    text: String,
    icon: ImageVector,
    textColor: Color,
    background: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = background,
            contentColor = textColor
        ),
        shape = RoundedCornerShape(24.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = textColor)
            Spacer(Modifier.width(12.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                color = textColor
            )
        }
    }
}

private fun List<SettingsSectionUI>.filterByQuery(query: String): List<SettingsSectionUI> =
    if (query.isBlank()) {
        this
    } else {
        map { section ->
            section.copy(items = section.items.filter { row -> row.title.contains(query, ignoreCase = true) })
        }.filter { it.items.isNotEmpty() }
    }
