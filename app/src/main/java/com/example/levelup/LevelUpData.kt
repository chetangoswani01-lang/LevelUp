package com.example.levelup

import android.content.Context

object LevelUpData {

    private const val PREFS_NAME = "levelup_data"

    private const val KEY_XP = "current_xp"

    private const val KEY_STUDY_COMPLETED =
        "study_completed"

    private const val KEY_EXERCISE_COMPLETED =
        "exercise_completed"

    private const val KEY_READING_COMPLETED =
        "reading_completed"

    private const val KEY_STREAK = "streak"

    private const val DEFAULT_XP = 420

    private const val DEFAULT_STREAK = 7

    private const val XP_PER_LEVEL = 100

    private fun prefs(context: Context) =
        context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

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
        val newXp = getXp(context) + amount

        prefs(context)
            .edit()
            .putInt(KEY_XP, newXp)
            .apply()
    }

    fun getLevel(context: Context): Int {
        val totalXp = getXp(context)

        return (totalXp / XP_PER_LEVEL) + 1
    }

    fun getCurrentLevelXp(context: Context): Int {
        val totalXp = getXp(context)

        return totalXp % XP_PER_LEVEL
    }

    fun getXpToNextLevel(context: Context): Int {
        val currentLevelXp =
            getCurrentLevelXp(context)

        return XP_PER_LEVEL - currentLevelXp
    }

    fun getXpPerLevel(): Int {
        return XP_PER_LEVEL
    }

    fun isStudyCompleted(context: Context): Boolean {
        return prefs(context).getBoolean(
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

    fun isExerciseCompleted(context: Context): Boolean {
        return prefs(context).getBoolean(
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

    fun isReadingCompleted(context: Context): Boolean {
        return prefs(context).getBoolean(
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

    fun getCompletedQuestCount(context: Context): Int {

        var count = 0

        if (isStudyCompleted(context)) {
            count++
        }

        if (isExerciseCompleted(context)) {
            count++
        }

        if (isReadingCompleted(context)) {
            count++
        }

        return count
    }

    fun getStreak(context: Context): Int {
        return prefs(context).getInt(
            KEY_STREAK,
            DEFAULT_STREAK
        )
    }
}