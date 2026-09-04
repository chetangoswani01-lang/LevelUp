package com.example.levelup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment

class HomeFragment : Fragment() {

    // Current XP
    private var currentXp = 420

    // XP needed for this level
    private val maxXp = 500

    // Prevent the same quest from giving XP twice
    private var studyCompleted = false
    private var exerciseCompleted = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(
            R.layout.fragment_home,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        super.onViewCreated(view, savedInstanceState)

        // -----------------------------
        // Find views
        // -----------------------------

        val studyCard =
            view.findViewById<View>(R.id.cardQuest1)

        val exerciseCard =
            view.findViewById<View>(R.id.cardQuest2)

        val studyQuest =
            view.findViewById<TextView>(R.id.tvQuest1)

        val exerciseQuest =
            view.findViewById<TextView>(R.id.tvQuest2)

        val xpText =
            view.findViewById<TextView>(R.id.tvXpProgress)

        val xpProgressBar =
            view.findViewById<ProgressBar>(R.id.xpProgressBar)

        // Show initial XP
        updateXpDisplay(
            xpText,
            xpProgressBar
        )

        // -----------------------------
        // Study Quest
        // -----------------------------

        studyCard.setOnClickListener {

            if (!studyCompleted) {

                studyCompleted = true

                // Give 20 XP
                currentXp += 20

                // Mark quest as completed
                studyQuest.text =
                    "✓  Study for 30 minutes"

                // Update XP
                updateXpDisplay(
                    xpText,
                    xpProgressBar
                )
            }
        }

        // -----------------------------
        // Exercise Quest
        // -----------------------------

        exerciseCard.setOnClickListener {

            if (!exerciseCompleted) {

                exerciseCompleted = true

                // Give 30 XP
                currentXp += 30

                // Mark quest as completed
                exerciseQuest.text =
                    "✓  Exercise"

                // Update XP
                updateXpDisplay(
                    xpText,
                    xpProgressBar
                )
            }
        }
    }

    // -----------------------------
    // Update XP display
    // -----------------------------

    private fun updateXpDisplay(
        xpText: TextView,
        xpProgressBar: ProgressBar
    ) {

        xpText.text =
            "$currentXp / $maxXp XP"

        xpProgressBar.max =
            maxXp

        xpProgressBar.progress =
            currentXp
    }
}