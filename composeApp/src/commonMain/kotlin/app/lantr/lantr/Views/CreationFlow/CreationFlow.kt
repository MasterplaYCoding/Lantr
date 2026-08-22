package app.lantr.lantr.Views.CreationFlow

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import app.lantr.lantr.Models.Audience
import app.lantr.lantr.Models.PresentationDuration
import app.lantr.lantr.Models.PresentationPurpose
import app.lantr.lantr.Models.WindowWidthSizeClass
import app.lantr.lantr.Util.PlatformBackHandler
import app.lantr.lantr.Views.mainMenu.mainScreen.calculateWindowSizeClass

@Composable
fun CreateProjectFlow(
    onCancelingCreationFlow: () -> Unit = {}
) {
    var currentStep by remember { mutableStateOf(1) }
    var audience by remember { mutableStateOf(Audience()) }
    var projectTitle by remember { mutableStateOf("") }
    var presentationDuration by remember { mutableStateOf(PresentationDuration.THIRTY_SECONDS) }
    var presentationPurpose by remember { mutableStateOf(PresentationPurpose.UPDATE_STATUS) }

    val customWindowSize = calculateWindowSizeClass()
    val isPhone = customWindowSize.widthSizeClass == WindowWidthSizeClass.Compact

    PlatformBackHandler {
        if (currentStep == 1) {
            onCancelingCreationFlow()
        } else {
            currentStep -= 1
        }
    }

    when (currentStep) {
        1 -> NameStep(
            onNext = { currentStep = 2 },
            onBack = onCancelingCreationFlow,
            isPhone = isPhone,
            updateData = { projectTitle = it }
        )

        2 -> DurationStep(
            onNext = { currentStep = 3 },
            onBack = { currentStep = 1 },
            isPhone = isPhone,
            projectTitle = projectTitle,
            updateData = { presentationDuration = it }
        )

        3 -> AudienceStep(
            onNext = { currentStep = 4 },
            onBack = { currentStep = 2 },
            isPhone = isPhone,
            projectTitle = projectTitle,
            updateData = { groupSize, mainRole, ageGroup, shortDescription ->
                audience.groupSize = groupSize
                audience.mainRole = mainRole
                audience.ageGroup = ageGroup
                audience.shortDescription = shortDescription
            }
        )

        4 -> KnowledgeStep(
            onNext = { currentStep = 5 },
            onBack = { currentStep = 3 },
            isPhone = isPhone,
            projectTitle = projectTitle,
            updateData = { knowledgeLevel, opinionOnTopic ->
                audience.knowledgeLevel = knowledgeLevel
                audience.opinionOnTopic = opinionOnTopic
            }
        )

        5 -> AudienceGoalStep(
            onNext = { currentStep = 6 },
            onBack = { currentStep = 4 },
            isPhone = isPhone,
            projectTitle = projectTitle,
            updateData = { mainMotivation, goalOrPain ->
                audience.mainMotivation = mainMotivation
                audience.goalOrPain = goalOrPain
            }
        )

        6 -> MotivationStep(
            onNext = {},
            onBack = { currentStep = 5 },
            isPhone = isPhone,
            projectTitle = projectTitle,
            updateData = {}
        )
    }
}
