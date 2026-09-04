package com.example.levelup

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    // Current XP
    private var currentXp = 420

    // XP needed for this level
    private val maxXp = 500

    // Prevent the same quest from giving XP twice
    private var studyCompleted = false
    private var exerciseCompleted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Handle system bars
        val mainView = findViewById<View>(R.id.main)

        ViewCompat.setOnApplyWindowInsetsListener(mainView) { view, insets ->

            val systemBars =
                insets.getInsets(WindowInsetsCompat.Type.systemBars())

            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        // -----------------------------
        // Find views
        // -----------------------------

        val studyCard = findViewById<View>(R.id.cardQuest1)
        val exerciseCard = findViewById<View>(R.id.cardQuest2)

        val studyQuest = findViewById<TextView>(R.id.tvQuest1)
        val exerciseQuest = findViewById<TextView>(R.id.tvQuest2)

        val xpText = findViewById<TextView>(R.id.tvXpProgress)
        val xpProgressBar = findViewById<ProgressBar>(R.id.xpProgressBar)

        // Show initial XP
        updateXpDisplay(xpText, xpProgressBar)

        // -----------------------------
        // Study Quest
        // -----------------------------

        studyCard.setOnClickListener {

            if (!studyCompleted) {

                studyCompleted = true

                // Give 20 XP
                currentXp += 20

                // Mark quest as completed
                studyQuest.text = "✓  Study for 30 minutes"

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
                exerciseQuest.text = "✓  Exercise"

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

        // Update XP text
        xpText.text = "$currentXp / $maxXp XP"

        // Update progress bar
        xpProgressBar.max = maxXp
        xpProgressBar.progress = currentXp
    }
}