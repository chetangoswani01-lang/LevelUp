package com.example.levelup

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

object LevelUpData {

    private const val PREFS_NAME = "levelup_data"

    // =========================================================
    // GENERAL DATA
    // =========================================================

    private const val KEY_XP = "current_xp"
    private const val KEY_STREAK = "streak"

    private const val DEFAULT_XP = 420
    private const val DEFAULT_STREAK = 7
    private const val XP_PER_LEVEL = 100


    // =========================================================
    // DEFAULT QUEST DATA
    // =========================================================

    private const val KEY_STUDY_COMPLETED =
        "study_completed"

    private const val KEY_EXERCISE_COMPLETED =
        "exercise_completed"

    private const val KEY_READING_COMPLETED =
        "reading_completed"


    private const val KEY_STUDY_START_TIME =
        "study_start_time"

    private const val KEY_EXERCISE_START_TIME =
        "exercise_start_time"

    private const val KEY_READING_START_TIME =
        "reading_start_time"


    // =========================================================
    // CUSTOM QUEST DATA
    // =========================================================

    private const val KEY_CUSTOM_QUESTS =
        "custom_quests"


    // =========================================================
    // QUEST DATA CLASS
    // =========================================================

    data class Quest(

        val id: Long,

        val name: String,

        val targetMinutes: Int,

        val baseXp: Int,

        val difficulty: String,

        val completed: Boolean = false,

        val startTime: Long = 0L
    )


    // =========================================================
    // SHARED PREFERENCES
    // =========================================================

    private fun prefs(context: Context) =
        context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )


    // =========================================================
    // XP SYSTEM
    // =========================================================

    fun getXp(context: Context): Int {

        return prefs(context).getInt(
            KEY_XP,
            DEFAULT_XP
        )
    }


    fun addXp(
        context: Context,
        amount: Int
    ) {

        if (amount <= 0) {
            return
        }

        val newXp =
            getXp(context) + amount

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

        val totalXp =
            getXp(context)

        return (
                totalXp /
                        XP_PER_LEVEL
                ) + 1
    }


    fun getCurrentLevelXp(
        context: Context
    ): Int {

        val totalXp =
            getXp(context)

        return totalXp %
                XP_PER_LEVEL
    }


    fun getXpToNextLevel(
        context: Context
    ): Int {

        val currentLevelXp =
            getCurrentLevelXp(context)

        return XP_PER_LEVEL -
                currentLevelXp
    }


    fun getXpPerLevel(): Int {

        return XP_PER_LEVEL
    }


    // =========================================================
    // AUTOMATIC BASE XP
    // =========================================================

    fun calculateBaseXp(
        targetMinutes: Int,
        difficulty: String
    ): Int {

        val safeMinutes =
            targetMinutes.coerceAtLeast(1)


        val difficultyMultiplier =
            when (difficulty.lowercase()) {

                "easy" -> 1.0

                "medium" -> 1.5

                "hard" -> 2.0

                else -> 1.5
            }


        val timeXp =
            when {

                safeMinutes <= 10 -> 10

                safeMinutes <= 30 -> 20

                safeMinutes <= 60 -> 30

                safeMinutes <= 90 -> 40

                else -> 50
            }


        return (
                timeXp *
                        difficultyMultiplier
                ).toInt()
    }


    // =========================================================
    // FINAL XP - SECONDS BASED
    // =========================================================

    fun calculateFinalXp(
        baseXp: Int,
        targetMinutes: Int,
        actualSeconds: Long
    ): Int {

        if (baseXp <= 0) {
            return 0
        }


        if (targetMinutes <= 0) {
            return baseXp
        }


        if (actualSeconds <= 0L) {
            return baseXp
        }


        val targetSeconds =
            targetMinutes.toLong() *
                    60L


        // -----------------------------------------------------
        // If user takes target time or longer:
        // No speed bonus.
        // -----------------------------------------------------

        if (actualSeconds >= targetSeconds) {
            return baseXp
        }


        // -----------------------------------------------------
        // Calculate how much time was saved.
        // -----------------------------------------------------

        val timeSaved =
            targetSeconds -
                    actualSeconds


        val percentageSaved =
            timeSaved.toDouble() /
                    targetSeconds.toDouble()


        // -----------------------------------------------------
        // Maximum speed bonus = 50%
        // -----------------------------------------------------

        val bonusMultiplier =
            1.0 +
                    (
                            percentageSaved *
                                    0.5
                            )


        val calculatedXp =
            (
                    baseXp *
                            bonusMultiplier
                    ).toInt()


        return calculatedXp.coerceAtMost(
            (
                    baseXp *
                            1.5
                    ).toInt()
        )
    }


    // =========================================================
    // BACKWARD-COMPATIBLE XP FUNCTION
    // =========================================================

    fun calculateFinalXp(
        baseXp: Int,
        targetMinutes: Int,
        actualMinutes: Int
    ): Int {

        val safeMinutes =
            actualMinutes.coerceAtLeast(1)


        val actualSeconds =
            safeMinutes.toLong() *
                    60L


        return calculateFinalXp(
            baseXp =
                baseXp,

            targetMinutes =
                targetMinutes,

            actualSeconds =
                actualSeconds
        )
    }


    // =========================================================
    // DEFAULT QUESTS
    // =========================================================

    fun getDefaultQuests(
        context: Context
    ): List<Quest> {

        return listOf(

            Quest(

                id = 1L,

                name =
                    "Study for 30 minutes",

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

                name =
                    "Read for 30 minutes",

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


    // =========================================================
    // START DEFAULT QUEST
    // =========================================================

    fun startDefaultQuest(
        context: Context,
        questId: Long
    ) {

        val currentTime =
            System.currentTimeMillis()


        when (questId) {

            1L -> {

                prefs(context)
                    .edit()
                    .putLong(
                        KEY_STUDY_START_TIME,
                        currentTime
                    )
                    .apply()
            }


            2L -> {

                prefs(context)
                    .edit()
                    .putLong(
                        KEY_EXERCISE_START_TIME,
                        currentTime
                    )
                    .apply()
            }


            3L -> {

                prefs(context)
                    .edit()
                    .putLong(
                        KEY_READING_START_TIME,
                        currentTime
                    )
                    .apply()
            }
        }
    }


    // =========================================================
    // COMPLETE DEFAULT QUEST
    // =========================================================

    fun completeDefaultQuest(
        context: Context,
        questId: Long,
        actualSeconds: Long
    ): Int {

        val quest =
            getDefaultQuests(context)
                .firstOrNull {
                    it.id == questId
                }
                ?: return 0


        if (quest.completed) {
            return 0
        }


        val finalXp =
            calculateFinalXp(
                baseXp =
                    quest.baseXp,

                targetMinutes =
                    quest.targetMinutes,

                actualSeconds =
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
            finalXp
        )


        return finalXp
    }


    // =========================================================
    // DEFAULT QUEST START TIMES
    // =========================================================

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


    // =========================================================
    // GET CUSTOM QUESTS
    // =========================================================

    fun getCustomQuests(
        context: Context
    ): MutableList<Quest> {

        val questList =
            mutableListOf<Quest>()


        val jsonString =
            prefs(context)
                .getString(
                    KEY_CUSTOM_QUESTS,
                    "[]"
                )


        try {

            val jsonArray =
                JSONArray(jsonString)


            for (
            i in 0 until
                    jsonArray.length()
            ) {

                val jsonObject =
                    jsonArray.getJSONObject(i)


                val quest =
                    Quest(

                        id =
                            jsonObject.optLong(
                                "id"
                            ),

                        name =
                            jsonObject.optString(
                                "name"
                            ),

                        targetMinutes =
                            jsonObject.optInt(
                                "targetMinutes"
                            ),

                        baseXp =
                            jsonObject.optInt(
                                "baseXp"
                            ),

                        difficulty =
                            jsonObject.optString(
                                "difficulty",
                                "Medium"
                            ),

                        completed =
                            jsonObject.optBoolean(
                                "completed",
                                false
                            ),

                        startTime =
                            jsonObject.optLong(
                                "startTime",
                                0L
                            )
                    )


                questList.add(
                    quest
                )
            }

        } catch (e: Exception) {

            e.printStackTrace()
        }


        return questList
    }


    // =========================================================
    // SAVE CUSTOM QUESTS
    // =========================================================

    private fun saveCustomQuests(
        context: Context,
        quests: List<Quest>
    ) {

        val jsonArray =
            JSONArray()


        for (quest in quests) {

            val jsonObject =
                JSONObject()


            jsonObject.put(
                "id",
                quest.id
            )


            jsonObject.put(
                "name",
                quest.name
            )


            jsonObject.put(
                "targetMinutes",
                quest.targetMinutes
            )


            jsonObject.put(
                "baseXp",
                quest.baseXp
            )


            jsonObject.put(
                "difficulty",
                quest.difficulty
            )


            jsonObject.put(
                "completed",
                quest.completed
            )


            jsonObject.put(
                "startTime",
                quest.startTime
            )


            jsonArray.put(
                jsonObject
            )
        }


        prefs(context)
            .edit()
            .putString(
                KEY_CUSTOM_QUESTS,
                jsonArray.toString()
            )
            .apply()
    }


    // =========================================================
    // ADD CUSTOM QUEST
    // =========================================================

    fun addCustomQuest(
        context: Context,
        name: String,
        targetMinutes: Int,
        difficulty: String
    ): Quest {

        val baseXp =
            calculateBaseXp(
                targetMinutes,
                difficulty
            )


        val newQuest =
            Quest(

                id =
                    System.currentTimeMillis(),

                name =
                    name,

                targetMinutes =
                    targetMinutes,

                baseXp =
                    baseXp,

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
            newQuest
        )


        saveCustomQuests(
            context,
            quests
        )


        return newQuest
    }


    // =========================================================
    // UPDATE CUSTOM QUEST
    // =========================================================

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


        if (index != -1) {

            quests[index] =
                updatedQuest


            saveCustomQuests(
                context,
                quests
            )
        }
    }


    // =========================================================
    // START CUSTOM QUEST
    // =========================================================

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
                it.id == questId
            }


        if (index != -1) {

            val quest =
                quests[index]


            quests[index] =
                quest.copy(
                    startTime =
                        System.currentTimeMillis()
                )


            saveCustomQuests(
                context,
                quests
            )
        }
    }


    // =========================================================
    // COMPLETE CUSTOM QUEST
    // =========================================================

    fun completeQuest(
        context: Context,
        questId: Long,
        actualSeconds: Long
    ): Int {

        val quests =
            getCustomQuests(
                context
            )


        val index =
            quests.indexOfFirst {
                it.id == questId
            }


        if (index == -1) {
            return 0
        }


        val quest =
            quests[index]


        if (quest.completed) {
            return 0
        }


        val finalXp =
            calculateFinalXp(
                baseXp =
                    quest.baseXp,

                targetMinutes =
                    quest.targetMinutes,

                actualSeconds =
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
            finalXp
        )


        return finalXp
    }


    // =========================================================
    // STUDY QUEST
    // =========================================================

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


    // =========================================================
    // EXERCISE QUEST
    // =========================================================

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


    // =========================================================
    // READING QUEST
    // =========================================================

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


    // =========================================================
    // COMPLETED QUEST COUNT
    // =========================================================

    fun getCompletedQuestCount(
        context: Context
    ): Int {

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


        val customQuests =
            getCustomQuests(
                context
            )


        count +=
            customQuests.count {
                it.completed
            }


        return count
    }


    // =========================================================
    // STREAK
    // =========================================================

    fun getStreak(
        context: Context
    ): Int {

        return prefs(context)
            .getInt(
                KEY_STREAK,
                DEFAULT_STREAK
            )
    }
}