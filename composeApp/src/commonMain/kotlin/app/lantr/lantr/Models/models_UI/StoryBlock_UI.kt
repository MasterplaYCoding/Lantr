package app.lantr.lantr.Models.models_UI

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import app.lantr.lantr.Models.PredefinedBlockType
import app.lantr.lantr.Models.StoryBlock
import app.lantr.lantr.Models.StoryBlockCategory

val StoryBlock.tintColor: Color
    @Composable
    get() = category.tintColor

val StoryBlock.isPrimaryBlock: Boolean
    get() = predefinedType?.isPrimaryBlock ?: false

val StoryBlock.icon: ImageVector
    @Composable
    get() = when (category) {
        StoryBlockCategory.HOOK -> Icons.Default.PlayArrow
        StoryBlockCategory.STAKES -> Icons.Default.Warning
        StoryBlockCategory.CORE -> Icons.Default.Star
        StoryBlockCategory.TURN -> Icons.Default.Refresh
        StoryBlockCategory.AUDIENCE -> Icons.Default.Person
        StoryBlockCategory.CLOSE -> Icons.Default.Check
        StoryBlockCategory.OTHER -> Icons.Default.Info
    }

val StoryBlock.systemIconName: String
    get() = when {
        isCustomType -> "circle.dashed"
        predefinedType != null -> predefinedType!!.systemIconName
        else -> "circle.dashed"
    }

val StoryBlockCategory.tintColor: Color
    @Composable
    get() = when (this) {
        StoryBlockCategory.HOOK,
        StoryBlockCategory.CLOSE -> MaterialTheme.colorScheme.secondary

        StoryBlockCategory.STAKES,
        StoryBlockCategory.CORE,
        StoryBlockCategory.OTHER -> MaterialTheme.colorScheme.tertiary

        StoryBlockCategory.TURN,
        StoryBlockCategory.AUDIENCE -> MaterialTheme.colorScheme.primary
    }

val PredefinedBlockType.isPrimaryBlock: Boolean
    get() = when (this) {
        PredefinedBlockType.PATTERN_BREAK,
        PredefinedBlockType.HUMAN_MOMENT,
        PredefinedBlockType.BIG_PROMISE,
        PredefinedBlockType.HARD_QUESTION -> true

        PredefinedBlockType.SHOCK_STAT,
        PredefinedBlockType.QUIET_BEAT -> false

        PredefinedBlockType.FALSE_CHOICE,
        PredefinedBlockType.HIDDEN_ENEMY,
        PredefinedBlockType.SHARED_PAIN,
        PredefinedBlockType.REAL_COST -> true

        PredefinedBlockType.TIME_PRESSURE,
        PredefinedBlockType.MORAL_COST -> false

        PredefinedBlockType.FIRST_TRUTH,
        PredefinedBlockType.PROOF_POINT,
        PredefinedBlockType.CLEAR_PATH,
        PredefinedBlockType.DEEP_DIVE,
        PredefinedBlockType.FAIR_OBJECTION,
        PredefinedBlockType.REVEAL_MOMENT -> true

        PredefinedBlockType.CASE_STUDY,
        PredefinedBlockType.FAILURE_LESSON,
        PredefinedBlockType.FRAMING_METAPHOR -> false

        PredefinedBlockType.NEW_LENS,
        PredefinedBlockType.KEY_INSIGHT,
        PredefinedBlockType.BOLD_LEAP -> true

        PredefinedBlockType.UNEXPECTED_ALLY,
        PredefinedBlockType.COMIC_BEAT -> false

        PredefinedBlockType.DIRECT_MIRROR,
        PredefinedBlockType.INNER_TRUTH,
        PredefinedBlockType.DIRECT_CHALLENGE -> true

        PredefinedBlockType.JOIN_IN,
        PredefinedBlockType.PULSE_CHECK -> false

        PredefinedBlockType.FULL_CIRCLE,
        PredefinedBlockType.FUTURE_VIEW,
        PredefinedBlockType.NEXT_STEP -> true

        PredefinedBlockType.FINAL_LINE,
        PredefinedBlockType.OPEN_LOOP,
        PredefinedBlockType.CALL_BACK -> false
    }

val PredefinedBlockType.systemIconName: String
    get() = when (this) {
        PredefinedBlockType.PATTERN_BREAK -> "hammer.fill"
        PredefinedBlockType.HUMAN_MOMENT -> "person.fill"
        PredefinedBlockType.BIG_PROMISE -> "scroll.fill"
        PredefinedBlockType.HARD_QUESTION -> "questionmark"
        PredefinedBlockType.SHOCK_STAT -> "chart.bar.xaxis"
        PredefinedBlockType.QUIET_BEAT -> "pause"

        PredefinedBlockType.FALSE_CHOICE -> "arrow.left.and.right"
        PredefinedBlockType.HIDDEN_ENEMY -> "sunglasses.fill"
        PredefinedBlockType.SHARED_PAIN -> "person.2.fill"
        PredefinedBlockType.REAL_COST -> "banknote"
        PredefinedBlockType.TIME_PRESSURE -> "hourglass"
        PredefinedBlockType.MORAL_COST -> "scalemass"

        PredefinedBlockType.FIRST_TRUTH -> "atom"
        PredefinedBlockType.PROOF_POINT -> "checkmark.seal"
        PredefinedBlockType.CLEAR_PATH -> "list.number"
        PredefinedBlockType.DEEP_DIVE -> "square.3.layers.3d.down.right"
        PredefinedBlockType.FAIR_OBJECTION -> "figure.fencing"
        PredefinedBlockType.REVEAL_MOMENT -> "curtains.open"
        PredefinedBlockType.CASE_STUDY -> "doc.text.magnifyingglass"
        PredefinedBlockType.FAILURE_LESSON -> "arrow.uturn.backward.circle"
        PredefinedBlockType.FRAMING_METAPHOR -> "photo.artframe"

        PredefinedBlockType.NEW_LENS -> "loupe"
        PredefinedBlockType.KEY_INSIGHT -> "key.horizontal.fill"
        PredefinedBlockType.BOLD_LEAP -> "figure.run"
        PredefinedBlockType.UNEXPECTED_ALLY -> "figure"
        PredefinedBlockType.COMIC_BEAT -> "face.smiling"

        PredefinedBlockType.DIRECT_MIRROR -> "person.crop.artframe"
        PredefinedBlockType.INNER_TRUTH -> "lock.open"
        PredefinedBlockType.DIRECT_CHALLENGE -> "hand.raised"
        PredefinedBlockType.JOIN_IN -> "person.3.fill"
        PredefinedBlockType.PULSE_CHECK -> "waveform.path.ecg"

        PredefinedBlockType.FULL_CIRCLE -> "arrow.trianglehead.counterclockwise"
        PredefinedBlockType.FUTURE_VIEW -> "binoculars"
        PredefinedBlockType.NEXT_STEP -> "arrow.right.circle"
        PredefinedBlockType.FINAL_LINE -> "music.microphone"
        PredefinedBlockType.OPEN_LOOP -> "mountain.2.fill"
        PredefinedBlockType.CALL_BACK -> "arrow.uturn.down"
    }