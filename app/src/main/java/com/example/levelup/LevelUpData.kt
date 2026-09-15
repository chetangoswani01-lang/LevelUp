package com.example.levelup

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object LevelUpData {

    private const val PREFS_NAME = "levelup_data"

    private const val KEY_XP = "current_xp"
    private const val KEY_STREAK = "streak"
    private const val KEY_LAST_ACTIVITY_DAY = "last_activity_day"
    private const val KEY_LAST_DAY = "last_day"
    private const val KEY_DECAY_APPLIED = "decay_applied"
    private const val KEY_SYSTEM_VERSION = "system_version"

    private const val KEY_STUDY_COMPLETED = "study_completed"
    private const val KEY_EXERCISE_COMPLETED = "exercise_completed"
    private const val KEY_READING_COMPLETED = "reading_completed"

    private const val KEY_STUDY_START_TIME = "study_start_time"
    private const val KEY_EXERCISE_START_TIME = "exercise_start_time"
    private const val KEY_READING_START_TIME = "reading_start_time"

    private const val KEY_CUSTOM_QUESTS = "custom_quests"

    private const val SYSTEM_VERSION = 2
    private const val DEFAULT_XP = 0
    private const val DEFAULT_STREAK = 0
    private const val XP_PER_LEVEL = 100

    data class Quest(
        val id: Long,
        val name: String,
        val targetMinutes: Int,
        val baseXp: Int,
        val difficulty: String,
        val completed: Boolean = false,
        val startTime: Long = 0L
    )

    private fun prefs(context: Context) =
        context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

    private fun today(): String {
        return SimpleDateFormat(
            "yyyyMMdd",
            Locale.US
        ).format(Date())
    }

    private fun daysBetween(
        from: String,
        to: String
    ): Int {

        if (from.isEmpty()) {
            return 0
        }

        val format =
            SimpleDateFormat(
                "yyyyMMdd",
                Locale.US
            )

        return try {

            val start =
                format.parse(from)
                    ?: return 0

            val end =
                format.parse(to)
                    ?: return 0

            (
                    (end.time - start.time) /
                            (24L * 60L * 60L * 1000L)
                    ).toInt()

        } catch (e: Exception) {
            0
        }
    }

    private fun readCustomQuests(
        context: Context
    ): MutableList<Quest> {

        val quests =
            mutableListOf<Quest>()

        val json =
            prefs(context).getString(
                KEY_CUSTOM_QUESTS,
                "[]"
            ) ?: "[]"

        try {

            val array =
                JSONArray(json)

            for (i in 0 until array.length()) {

                val item =
                    array.getJSONObject(i)

                quests.add(
                    Quest(
                        id =
                            item.optLong(
                                "id"
                            ),

                        name =
                            item.optString(
                                "name"
                            ),

                        targetMinutes =
                            item.optInt(
                                "targetMinutes"
                            ),

                        baseXp =
                            item.optInt(
                                "baseXp"
                            ),

                        difficulty =
                            item.optString(
                                "difficulty",
                                "Medium"
                            ),

                        completed =
                            item.optBoolean(
                                "completed",
                                false
                            ),

                        startTime =
                            item.optLong(
                                "startTime",
                                0L
                            )
                    )
                )
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return quests
    }

    private fun saveCustomQuests(
        context: Context,
        quests: List<Quest>
    ) {

        val array =
            JSONArray()

        for (quest in quests) {

            val item =
                JSONObject()

            item.put(
                "id",
                quest.id
            )

            item.put(
                "name",
                quest.name
            )

            item.put(
                "targetMinutes",
                quest.targetMinutes
            )

            item.put(
                "baseXp",
                quest.baseXp
            )

            item.put(
                "difficulty",
                quest.difficulty
            )

            item.put(
                "completed",
                quest.completed
            )

            item.put(
                "startTime",
                quest.startTime
            )

            array.put(
                item
            )
        }

        prefs(context)
            .edit()
            .putString(
                KEY_CUSTOM_QUESTS,
                array.toString()
            )
            .apply()
    }

    private fun resetDailyQuests(
        context: Context
    ) {

        prefs(context)
            .edit()
            .putBoolean(
                KEY_STUDY_COMPLETED,
                false
            )
            .putBoolean(
                KEY_EXERCISE_COMPLETED,
                false
            )
            .putBoolean(
                KEY_READING_COMPLETED,
                false
            )
            .remove(
                KEY_STUDY_START_TIME
            )
            .remove(
                KEY_EXERCISE_START_TIME
            )
            .remove(
                KEY_READING_START_TIME
            )
            .apply()

        val quests =
            readCustomQuests(context)

        val resetQuests =
            quests.map {

                it.copy(
                    completed = false,
                    startTime = 0L
                )
            }

        saveCustomQuests(
            context,
            resetQuests
        )
    }

    private fun ensureSystemState(
        context: Context
    ) {

        val preferences =
            prefs(context)

        val currentVersion =
            preferences.getInt(
                KEY_SYSTEM_VERSION,
                0
            )

        if (
            currentVersion !=
            SYSTEM_VERSION
        ) {

            preferences
                .edit()
                .putInt(
                    KEY_SYSTEM_VERSION,
                    SYSTEM_VERSION
                )
                .putInt(
                    KEY_XP,
                    0
                )
                .putInt(
                    KEY_STREAK,
                    0
                )
                .putString(
                    KEY_LAST_ACTIVITY_DAY,
                    ""
                )
                .putString(
                    KEY_LAST_DAY,
                    today()
                )
                .putInt(
                    KEY_DECAY_APPLIED,
                    0
                )
                .putBoolean(
                    KEY_STUDY_COMPLETED,
                    false
                )
                .putBoolean(
                    KEY_EXERCISE_COMPLETED,
                    false
                )
                .putBoolean(
                    KEY_READING_COMPLETED,
                    false
                )
                .remove(
                    KEY_STUDY_START_TIME
                )
                .remove(
                    KEY_EXERCISE_START_TIME
                )
                .remove(
                    KEY_READING_START_TIME
                )
                .putString(
                    KEY_CUSTOM_QUESTS,
                    "[]"
                )
                .apply()

            return
        }

        val currentDay =
            today()

        val lastDay =
            preferences.getString(
                KEY_LAST_DAY,
                currentDay
            ) ?: currentDay

        if (
            currentDay ==
            lastDay
        ) {
            return
        }

        val lastActivity =
            preferences.getString(
                KEY_LAST_ACTIVITY_DAY,
                ""
            ) ?: ""

        val missedDays =
            if (
                lastActivity.isEmpty()
            ) {

                0

            } else {

                (
                        daysBetween(
                            lastActivity,
                            currentDay
                        ) - 1
                        ).coerceAtLeast(0)
            }

        val alreadyApplied =
            preferences.getInt(
                KEY_DECAY_APPLIED,
                0
            )

        val newDecayDays =
            (
                    missedDays -
                            alreadyApplied
                    ).coerceAtLeast(0)

        var xp =
            preferences.getInt(
                KEY_XP,
                0
            )

        repeat(
            newDecayDays
        ) {

            xp =
                (
                        xp * 0.95
                        )
                    .toInt()
                    .coerceAtLeast(0)
        }

        val gap =
            if (
                lastActivity.isEmpty()
            ) {

                0

            } else {

                daysBetween(
                    lastActivity,
                    currentDay
                )
            }

        val newStreak =
            when {

                gap <= 1 ->
                    preferences.getInt(
                        KEY_STREAK,
                        0
                    )

                else ->
                    0
            }

        preferences
            .edit()
            .putInt(
                KEY_XP,
                xp
            )
            .putInt(
                KEY_STREAK,
                newStreak
            )
            .putInt(
                KEY_DECAY_APPLIED,
                missedDays
            )
            .putString(
                KEY_LAST_DAY,
                currentDay
            )
            .apply()

        resetDailyQuests(
            context
        )
    }

    private fun markActivity(
        context: Context
    ) {

        ensureSystemState(
            context
        )

        val currentDay =
            today()

        val preferences =
            prefs(context)

        val lastActivity =
            preferences.getString(
                KEY_LAST_ACTIVITY_DAY,
                ""
            ) ?: ""

        val currentStreak =
            preferences.getInt(
                KEY_STREAK,
                0
            )

        val newStreak =
            when {

                lastActivity ==
                        currentDay ->
                    currentStreak

                lastActivity.isEmpty() ->
                    1

                daysBetween(
                    lastActivity,
                    currentDay
                ) == 1 ->
                    currentStreak + 1

                else ->
                    1
            }

        preferences
            .edit()
            .putString(
                KEY_LAST_ACTIVITY_DAY,
                currentDay
            )
            .putInt(
                KEY_STREAK,
                newStreak
            )
            .putInt(
                KEY_DECAY_APPLIED,
                0
            )
            .putString(
                KEY_LAST_DAY,
                currentDay
            )
            .apply()
    }

    fun getXp(
        context: Context
    ): Int {

        ensureSystemState(
            context
        )

        return prefs(context)
            .getInt(
                KEY_XP,
                DEFAULT_XP
            )
            .coerceAtLeast(0)
    }

    fun addXp(
        context: Context,
        amount: Int
    ) {

        if (
            amount <= 0
        ) {
            return
        }

        val newXp =
            getXp(context) +
                    amount

        prefs(context)
            .edit()
            .putInt(
                KEY_XP,
                newXp
            )
            .apply()
    }

    fun getLevel(
        context: Context
    ): Int {

        return getXp(context) /
                XP_PER_LEVEL
    }

    fun getCurrentLevelXp(
        context: Context
    ): Int {

        return getXp(context) %
                XP_PER_LEVEL
    }

    fun getXpToNextLevel(
        context: Context
    ): Int {

        return XP_PER_LEVEL -
                getCurrentLevelXp(
                    context
                )
    }

    fun getXpPerLevel(): Int {
        return XP_PER_LEVEL
    }

    fun calculateBaseXp(
        targetMinutes: Int,
        difficulty: String
    ): Int {

        val minutes =
            targetMinutes.coerceAtLeast(1)

        val timeXp =
            when {

                minutes <= 10 ->
                    10

                minutes <= 30 ->
                    20

                minutes <= 60 ->
                    30

                minutes <= 90 ->
                    40

                else ->
                    50
            }

        val multiplier =
            when (
                difficulty.lowercase()
            ) {

                "easy" ->
                    1.0

                "medium" ->
                    1.5

                "hard" ->
                    2.0

                else ->
                    1.5
            }

        return (
                timeXp *
                        multiplier
                ).toInt()
    }

    fun calculateFinalXp(
        baseXp: Int,
        targetMinutes: Int,
        actualSeconds: Long
    ): Int {

        if (
            baseXp <= 0
        ) {
            return 0
        }

        if (
            targetMinutes <= 0
        ) {
            return baseXp
        }

        if (
            actualSeconds <= 0
        ) {
            return baseXp
        }

        val targetSeconds =
            targetMinutes.toLong() *
                    60L

        if (
            actualSeconds >=
            targetSeconds
        ) {
            return baseXp
        }

        val saved =
            targetSeconds -
                    actualSeconds

        val savedPercentage =
            saved.toDouble() /
                    targetSeconds.toDouble()

        val bonus =
            1.0 +
                    (
                            savedPercentage *
                                    0.5
                            )

        val result =
            (
                    baseXp *
                            bonus
                    ).toInt()

        return result.coerceAtMost(
            (
                    baseXp * 1.5
                    ).toInt()
        )
    }

    fun calculateFinalXp(
        baseXp: Int,
        targetMinutes: Int,
        actualMinutes: Int
    ): Int {

        val safeMinutes =
            actualMinutes
                .coerceAtLeast(1)

        return calculateFinalXp(
            baseXp,
            targetMinutes,
            safeMinutes.toLong() * 60L
        )
    }

    fun getDefaultQuests(
        context: Context
    ): List<Quest> {

        ensureSystemState(
            context
        )

        return listOf(

            Quest(
                id = 1L,
                name = "Study for 30 minutes",
                targetMinutes = 30,
                baseXp =
                    calculateBaseXp(
                        30,
                        "Medium"
                    ),
                difficulty = "Medium",
                completed =
                    isStudyCompleted(
                        context
                    ),
                startTime =
                    getStudyStartTime(
                        context
                    )
            ),

            Quest(
                id = 2L,
                name = "Exercise",
                targetMinutes = 30,
                baseXp =
                    calculateBaseXp(
                        30,
                        "Hard"
                    ),
                difficulty = "Hard",
                completed =
                    isExerciseCompleted(
                        context
                    ),
                startTime =
                    getExerciseStartTime(
                        context
                    )
            ),

            Quest(
                id = 3L,
                name = "Read for 30 minutes",
                targetMinutes = 30,
                baseXp =
                    calculateBaseXp(
                        30,
                        "Medium"
                    ),
                difficulty = "Medium",
                completed =
                    isReadingCompleted(
                        context
                    ),
                startTime =
                    getReadingStartTime(
                        context
                    )
            )
        )
    }

    fun startDefaultQuest(
        context: Context,
        questId: Long
    ) {

        val time =
            System.currentTimeMillis()

        when (questId) {

            1L ->
                prefs(context)
                    .edit()
                    .putLong(
                        KEY_STUDY_START_TIME,
                        time
                    )
                    .apply()

            2L ->
                prefs(context)
                    .edit()
                    .putLong(
                        KEY_EXERCISE_START_TIME,
                        time
                    )
                    .apply()

            3L ->
                prefs(context)
                    .edit()
                    .putLong(
                        KEY_READING_START_TIME,
                        time
                    )
                    .apply()
        }
    }

    fun completeDefaultQuest(
        context: Context,
        questId: Long,
        actualSeconds: Long
    ): Int {

        ensureSystemState(
            context
        )

        val quest =
            getDefaultQuests(
                context
            ).firstOrNull {

                it.id ==
                        questId
            }
                ?: return 0

        if (
            quest.completed
        ) {
            return 0
        }

        val earnedXp =
            calculateFinalXp(
                quest.baseXp,
                quest.targetMinutes,
                actualSeconds
            )

        when (questId) {

            1L -> {

                setStudyCompleted(
                    context,
                    true
                )

                clearStudyStartTime(
                    context
                )
            }

            2L -> {

                setExerciseCompleted(
                    context,
                    true
                )

                clearExerciseStartTime(
                    context
                )
            }

            3L -> {

                setReadingCompleted(
                    context,
                    true
                )

                clearReadingStartTime(
                    context
                )
            }
        }

        addXp(
            context,
            earnedXp
        )

        markActivity(
            context
        )

        return earnedXp
    }

    private fun getStudyStartTime(
        context: Context
    ): Long {

        return prefs(context)
            .getLong(
                KEY_STUDY_START_TIME,
                0L
            )
    }

    private fun getExerciseStartTime(
        context: Context
    ): Long {

        return prefs(context)
            .getLong(
                KEY_EXERCISE_START_TIME,
                0L
            )
    }

    private fun getReadingStartTime(
        context: Context
    ): Long {

        return prefs(context)
            .getLong(
                KEY_READING_START_TIME,
                0L
            )
    }

    private fun clearStudyStartTime(
        context: Context
    ) {

        prefs(context)
            .edit()
            .remove(
                KEY_STUDY_START_TIME
            )
            .apply()
    }

    private fun clearExerciseStartTime(
        context: Context
    ) {

        prefs(context)
            .edit()
            .remove(
                KEY_EXERCISE_START_TIME
            )
            .apply()
    }

    private fun clearReadingStartTime(
        context: Context
    ) {

        prefs(context)
            .edit()
            .remove(
                KEY_READING_START_TIME
            )
            .apply()
    }

    fun getCustomQuests(
        context: Context
    ): MutableList<Quest> {

        ensureSystemState(
            context
        )

        return readCustomQuests(
            context
        )
    }

    fun addCustomQuest(
        context: Context,
        name: String,
        targetMinutes: Int,
        difficulty: String
    ): Quest {

        val quest =
            Quest(
                id =
                    System.currentTimeMillis(),

                name =
                    name,

                targetMinutes =
                    targetMinutes,

                baseXp =
                    calculateBaseXp(
                        targetMinutes,
                        difficulty
                    ),

                difficulty =
                    difficulty,

                completed =
                    false,

                startTime =
                    0L
            )

        val quests =
            getCustomQuests(
                context
            )

        quests.add(
            quest
        )

        saveCustomQuests(
            context,
            quests
        )

        return quest
    }

    fun updateQuest(
        context: Context,
        updatedQuest: Quest
    ) {

        val quests =
            getCustomQuests(
                context
            )

        val index =
            quests.indexOfFirst {

                it.id ==
                        updatedQuest.id
            }

        if (
            index >= 0
        ) {

            quests[index] =
                updatedQuest

            saveCustomQuests(
                context,
                quests
            )
        }
    }

    fun startQuest(
        context: Context,
        questId: Long
    ) {

        val quests =
            getCustomQuests(
                context
            )

        val index =
            quests.indexOfFirst {

                it.id ==
                        questId
            }

        if (
            index >= 0
        ) {

            quests[index] =
                quests[index].copy(
                    startTime =
                        System.currentTimeMillis()
                )

            saveCustomQuests(
                context,
                quests
            )
        }
    }

    fun completeQuest(
        context: Context,
        questId: Long,
        actualSeconds: Long
    ): Int {

        ensureSystemState(
            context
        )

        val quests =
            getCustomQuests(
                context
            )

        val index =
            quests.indexOfFirst {

                it.id ==
                        questId
            }

        if (
            index < 0
        ) {
            return 0
        }

        val quest =
            quests[index]

        if (
            quest.completed
        ) {
            return 0
        }

        val earnedXp =
            calculateFinalXp(
                quest.baseXp,
                quest.targetMinutes,
                actualSeconds
            )

        quests[index] =
            quest.copy(
                completed = true,
                startTime = 0L
            )

        saveCustomQuests(
            context,
            quests
        )

        addXp(
            context,
            earnedXp
        )

        markActivity(
            context
        )

        return earnedXp
    }

    fun isStudyCompleted(
        context: Context
    ): Boolean {

        return prefs(context)
            .getBoolean(
                KEY_STUDY_COMPLETED,
                false
            )
    }

    fun setStudyCompleted(
        context: Context,
        completed: Boolean
    ) {

        prefs(context)
            .edit()
            .putBoolean(
                KEY_STUDY_COMPLETED,
                completed
            )
            .apply()
    }

    fun isExerciseCompleted(
        context: Context
    ): Boolean {

        return prefs(context)
            .getBoolean(
                KEY_EXERCISE_COMPLETED,
                false
            )
    }

    fun setExerciseCompleted(
        context: Context,
        completed: Boolean
    ) {

        prefs(context)
            .edit()
            .putBoolean(
                KEY_EXERCISE_COMPLETED,
                completed
            )
            .apply()
    }

    fun isReadingCompleted(
        context: Context
    ): Boolean {

        return prefs(context)
            .getBoolean(
                KEY_READING_COMPLETED,
                false
            )
    }

    fun setReadingCompleted(
        context: Context,
        completed: Boolean
    ) {

        prefs(context)
            .edit()
            .putBoolean(
                KEY_READING_COMPLETED,
                completed
            )
            .apply()
    }

    fun getCompletedQuestCount(
        context: Context
    ): Int {

        ensureSystemState(
            context
        )

        var count = 0

        if (
            isStudyCompleted(
                context
            )
        ) {
            count++
        }

        if (
            isExerciseCompleted(
                context
            )
        ) {
            count++
        }

        if (
            isReadingCompleted(
                context
            )
        ) {
            count++
        }

        count +=
            getCustomQuests(
                context
            ).count {

                it.completed
            }

        return count
    }

    fun getStreak(
        context: Context
    ): Int {

        ensureSystemState(
            context
        )

        return prefs(context)
            .getInt(
                KEY_STREAK,
                DEFAULT_STREAK
            )
    }
}