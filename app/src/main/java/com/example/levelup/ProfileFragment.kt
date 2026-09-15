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

        super.onViewCreated(
            view,
            savedInstanceState
        )

        updateProfile(view)
    }

    private fun updateProfile(
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

        view.findViewById<TextView>(
            R.id.tvProfileLevel
        ).text =
            "Level: $level"

        view.findViewById<TextView>(
            R.id.tvProfileXp
        ).text =
            "Total XP: $xp"

        view.findViewById<TextView>(
            R.id.tvProfileStreak
        ).text =
            "Streak: $streak days"
    }

    override fun onResume() {

        super.onResume()

        view?.let {
            updateProfile(it)
        }
    }
}