package com.example.levelup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(
            R.layout.fragment_profile,
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

        val userName =
            view.findViewById<TextView>(R.id.tvUserName)

        val xpText =
            view.findViewById<TextView>(R.id.tvProfileXp)

        val streakText =
            view.findViewById<TextView>(R.id.tvProfileStreak)

        val xp =
            LevelUpData.getXp(context)

        val streak =
            LevelUpData.getStreak(context)

        val level =
            LevelUpData.getLevel(context)

        userName.text =
            "PLAYER\nLEVEL $level"

        xpText.text =
            "💎 Total XP\n$xp XP"

        streakText.text =
            "🔥 Current Streak\n$streak Days"
    }
}