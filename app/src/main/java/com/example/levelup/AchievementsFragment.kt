package com.example.levelup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class AchievementsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(
            R.layout.fragment_achievements,
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

        updateAchievements(view)
    }

    private fun updateAchievements(
        view: View
    ) {

        val context =
            requireContext()

        val level =
            LevelUpData.getLevel(
                context
            )

        val xp =
            LevelUpData.getXp(
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

        val firstStep =
            view.findViewById<TextView>(
                R.id.tvAchievementFirst
            )

        val levelFive =
            view.findViewById<TextView>(
                R.id.tvAchievementFive
            )

        val levelTen =
            view.findViewById<TextView>(
                R.id.tvAchievementTen
            )

        val weekWarrior =
            view.findViewById<TextView>(
                R.id.tvAchievementWeek
            )

        val xpMaster =
            view.findViewById<TextView>(
                R.id.tvAchievementXp
            )

        firstStep.text =
            if (xp > 0) {
                "🔓  FIRST STEP\nEarned your first XP"
            } else {
                "🔒  FIRST STEP\nEarn your first XP"
            }

        levelFive.text =
            if (level >= 5) {
                "🔓  LEVEL 5\nReached Level 5"
            } else {
                "🔒  LEVEL 5\nReach Level 5"
            }

        levelTen.text =
            if (level >= 10) {
                "🔓  LEVEL 10\nReached Level 10"
            } else {
                "🔒  LEVEL 10\nReach Level 10"
            }

        weekWarrior.text =
            if (streak >= 7) {
                "🔓  WEEK WARRIOR\nReached a 7 day streak"
            } else {
                "🔒  WEEK WARRIOR\nReach a 7 day streak"
            }

        xpMaster.text =
            if (xp >= 1000) {
                "🔓  XP MASTER\nEarned 1000 XP"
            } else {
                "🔒  XP MASTER\nEarn 1000 XP"
            }
    }

    override fun onResume() {

        super.onResume()

        view?.let {
            updateAchievements(it)
        }
    }
}