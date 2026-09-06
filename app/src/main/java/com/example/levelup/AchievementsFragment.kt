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

        super.onViewCreated(view, savedInstanceState)

        val context = requireContext()

        val achievement1 =
            view.findViewById<TextView>(R.id.tvAchievement1)

        val achievement2 =
            view.findViewById<TextView>(R.id.tvAchievement2)

        val achievement3 =
            view.findViewById<TextView>(R.id.tvAchievement3)

        val achievement4 =
            view.findViewById<TextView>(R.id.tvAchievement4)

        val xp =
            LevelUpData.getXp(context)

        val completedQuests =
            LevelUpData.getCompletedQuestCount(context)

        val streak =
            LevelUpData.getStreak(context)

        if (completedQuests >= 1) {
            achievement1.text =
                "🏆 First Steps\n✓ Unlocked - Complete your first quest"
        } else {
            achievement1.text =
                "🏆 First Steps\n🔒 Complete your first quest"
        }

        if (streak >= 7) {
            achievement2.text =
                "🔥 7 Day Streak\n✓ Unlocked - Maintain a 7-day streak"
        } else {
            achievement2.text =
                "🔥 7 Day Streak\n🔒 Reach a 7-day streak"
        }

        if (xp >= 500) {
            achievement3.text =
                "⭐ Level Up\n✓ Unlocked - Reach Level 6"
        } else {
            achievement3.text =
                "⭐ Level Up\n🔒 ${500 - xp} XP remaining to reach Level 6"
        }

        if (completedQuests >= 3) {
            achievement4.text =
                "🎯 Quest Master\n✓ Unlocked - Complete 3 quests"
        } else {
            achievement4.text =
                "🎯 Quest Master\n🔒 Complete 3 quests"
        }
    }
}