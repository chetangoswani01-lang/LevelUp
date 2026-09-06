package com.example.levelup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class StatsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(
            R.layout.fragment_stats,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        super.onViewCreated(view, savedInstanceState)

        val context = requireContext()

        val currentLevel =
            view.findViewById<TextView>(R.id.tvCurrentLevel)

        val totalXp =
            view.findViewById<TextView>(R.id.tvTotalXp)

        val levelProgress =
            view.findViewById<TextView>(R.id.tvLevelProgress)

        val streak =
            view.findViewById<TextView>(R.id.tvStreak)

        val quests =
            view.findViewById<TextView>(R.id.tvQuests)

        val xp = LevelUpData.getXp(context)

        val level =
            LevelUpData.getLevel(context)

        val currentLevelXp =
            LevelUpData.getCurrentLevelXp(context)

        val xpPerLevel =
            LevelUpData.getXpPerLevel()

        val xpToNext =
            LevelUpData.getXpToNextLevel(context)

        val currentStreak =
            LevelUpData.getStreak(context)

        val completedQuests =
            LevelUpData.getCompletedQuestCount(context)

        currentLevel.text =
            "⭐ Current Level\nLevel $level"

        totalXp.text =
            "💎 Total XP\n$xp XP"

        levelProgress.text =
            "▣ Level Progress\n$currentLevelXp / $xpPerLevel XP\n\n$xpToNext XP until Level ${level + 1}"

        streak.text =
            "🔥 Current Streak\n$currentStreak Days"

        quests.text =
            "🎯 Quests Completed\n$completedQuests"
    }
}