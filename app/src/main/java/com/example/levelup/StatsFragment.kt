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

        super.onViewCreated(
            view,
            savedInstanceState
        )

        updateStats(view)
    }

    private fun updateStats(
        view: View
    ) {

        val context =
            requireContext()

        val level =
            LevelUpData.getLevel(
                context
            )

        val totalXp =
            LevelUpData.getXp(
                context
            )

        val currentXp =
            LevelUpData.getCurrentLevelXp(
                context
            )

        val streak =
            LevelUpData.getStreak(
                context
            )

        val completed =
            LevelUpData.getCompletedQuestCount(
                context
            )

        val customQuests =
            LevelUpData.getCustomQuests(
                context
            ).size

        view.findViewById<TextView>(
            R.id.tvStatsLevel
        ).text =
            "Current Level: $level"

        view.findViewById<TextView>(
            R.id.tvStatsTotalXp
        ).text =
            "Total XP: $totalXp"

        view.findViewById<TextView>(
            R.id.tvStatsCurrentXp
        ).text =
            "XP This Level: $currentXp / 100"

        view.findViewById<TextView>(
            R.id.tvStatsStreak
        ).text =
            "Current Streak: $streak days"

        view.findViewById<TextView>(
            R.id.tvStatsCustom
        ).text =
            "Custom Quests: $customQuests"

        view.findViewById<TextView>(
            R.id.tvStatsCompleted
        ).text =
            "Completed Quests: $completed"
    }

    override fun onResume() {

        super.onResume()

        view?.let {
            updateStats(it)
        }
    }
}