package app.lantr.lantr.Models

import kotlinx.serialization.Serializable

@Serializable
data class Audience(
    var groupSize: GroupSize = GroupSize.EXTRA_LARGE,
    var mainRole: MainRole = MainRole.DECISION_MAKERS,
    var ageGroup: AgeGroup = AgeGroup.UNDER_18,
    var shortDescription: String = "",
    var knowledgeLevel: KnowledgeLevel = KnowledgeLevel.ADVANCED,
    var opinionOnTopic: String = "",
    var mainMotivation: MainMotivation = MainMotivation.BE_ENTERTAINED,
    var goalOrPain: String = ""
) {
    @Serializable
    enum class GroupSize(val value: String) {
        SMALL("Small (1-10)"),
        MEDIUM("Medium (11-50)"),
        LARGE("Large (50-100)"),
        EXTRA_LARGE("Extra Large (100+)"),
        UNKNOWN("Unknown");

        companion object {
            fun values() = enumValues<GroupSize>()
        }
    }

    @Serializable
    enum class MainRole(val value: String, val description: String) {
        DECISION_MAKERS("Decision Makers", "Investors, execs, managers"),
        LEARNERS("Learners", "Students, trainees, curious public"),
        PRACTITIONERS("Practicioners", "Developers, creatives, professionals"),
        SUPPORTERS_ADVOCATES("Supporters/Advocates", "Team members, fans, employees"),
        MIXED_OTHER("Mixed/Other", "");

        companion object {
            fun values() = enumValues<MainRole>()
        }
    }

    @Serializable
    enum class AgeGroup(val value: String) {
        UNDER_18("<18"),
        _18_TO_24("18-24"),
        _25_TO_34("25-34"),
        _35_TO_44("35-44"),
        _45_TO_54("45-54"),
        _55_TO_64("55-64"),
        _65_OVER("65+");

        companion object {
            fun values() = enumValues<AgeGroup>()
        }
    }

    @Serializable
    enum class KnowledgeLevel(val value: String) {
        NONE("None"),
        BASIC("Basic"),
        ADVANCED("Advanced"),
        MIXED("Mixed");

        companion object {
            fun values() = enumValues<KnowledgeLevel>()
        }
    }

    @Serializable
    enum class MainMotivation(val value: String) {
        LEARN_SKILLS("Learn New Skills"),
        SOLVE_PROBLEM("Solve a Problem"),
        GET_INSPIRED("Get Inspired"),
        SAVE_TIME_MONEY("Save Time/Money"),
        STAY_UPDATED("Stay Updated"),
        BE_ENTERTAINED("Be Entertained"),
        OTHER("Other");

        companion object {
            fun values() = enumValues<MainMotivation>()
        }
    }
}

@Serializable
enum class PresentationPurpose(val value: String, val description: String) {
    MOTIVATE("Motivate", "Inspire action, Change behavior, Push mindset shift"),
    EDUCATE("Educate", "Explain concept, Teach skill, Transfer knowledge"),
    TRAIN("Train", "Guide through process, Step-by-step, Check understanding"),
    SELL("Sell", "Convince to buy, Show value, Overcome objections"),
    RAISE_MONEY("Raise Money", "Show traction, Create belief, Demonstrate potential"),
    PERSUADE("Persuade", "Win hearts, Defend opinion, Change mindset"),
    ENTERTAIN("Entertain", "Evoke emotion, Tell story, Be memorable"),
    REPORT("Report", "Present data, Show results, Make progress clear"),
    PITCH_IDEA("Pitch an Idea", "Validate opportunity, Show market, Present differentiator"),
    RECRUIT("Recruit", "Attract talent, Share vision, Show growth path"),
    UPDATE_STATUS("Update/Status", "Keep stakeholders informed, Highlight risks/successes"),
    OTHER("Other", "Nothing on this list fits");

    companion object {
        fun values() = enumValues<PresentationPurpose>()
    }
}

@Serializable
enum class PresentationDuration(val value: String, val description: String) {
    THIRTY_SECONDS("30s", "Elevator pitch"),
    FIVE_MINUTES("5min", "Demo or teaser"),
    FIFTEEN_MINUTES("15min", "TED-style talk"),
    THIRTY_MINUTES("30min", "Workshop or investor deck"),
    OVER_ONE_HOUR(">1hr", "Keynote or training");

    companion object {
        fun values() = enumValues<PresentationDuration>()
    }
}