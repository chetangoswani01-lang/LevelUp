package com.example.levelup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment

class HomeFragment : Fragment() {

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

        super.onViewCreated(
            view,
            savedInstanceState
        )

        val studyCard =
            view.findViewById<View>(
                R.id.cardQuest1
            )

        val exerciseCard =
            view.findViewById<View>(
                R.id.cardQuest2
            )

        val readingCard =
            view.findViewById<View>(
                R.id.cardQuest3
            )

        val addQuestButton =
            view.findViewById<View>(
                R.id.btnAddQuest
            )

        val studyQuest =
            view.findViewById<TextView>(
                R.id.tvQuest1
            )

        val exerciseQuest =
            view.findViewById<TextView>(
                R.id.tvQuest2
            )

        val readingQuest =
            view.findViewById<TextView>(
                R.id.tvQuest3
            )

        val levelText =
            view.findViewById<TextView>(
                R.id.tvLevel
            )

        val xpText =
            view.findViewById<TextView>(
                R.id.tvXpProgress
            )

        val xpUntilLevelText =
            view.findViewById<TextView>(
                R.id.tvXpUntilLevel
            )

        val xpProgressBar =
            view.findViewById<ProgressBar>(
                R.id.xpProgressBar
            )

        updateScreen(
            studyQuest,
            exerciseQuest,
            readingQuest,
            levelText,
            xpText,
            xpUntilLevelText,
            xpProgressBar
        )

        studyCard.setOnClickListener {

            val context = requireContext()

            if (!LevelUpData.isStudyCompleted(context)) {

                LevelUpData.setStudyCompleted(
                    context,
                    true
                )

                LevelUpData.addXp(
                    context,
                    20
                )

                updateScreen(
                    studyQuest,
                    exerciseQuest,
                    readingQuest,
                    levelText,
                    xpText,
                    xpUntilLevelText,
                    xpProgressBar
                )
            }
        }

        exerciseCard.setOnClickListener {

            val context = requireContext()

            if (!LevelUpData.isExerciseCompleted(context)) {

                LevelUpData.setExerciseCompleted(
                    context,
                    true
                )

                LevelUpData.addXp(
                    context,
                    30
                )

                updateScreen(
                    studyQuest,
                    exerciseQuest,
                    readingQuest,
                    levelText,
                    xpText,
                    xpUntilLevelText,
                    xpProgressBar
                )
            }
        }

        readingCard.setOnClickListener {

            val context = requireContext()

            if (!LevelUpData.isReadingCompleted(context)) {

                LevelUpData.setReadingCompleted(
                    context,
                    true
                )

                LevelUpData.addXp(
                    context,
                    30
                )

                updateScreen(
                    studyQuest,
                    exerciseQuest,
                    readingQuest,
                    levelText,
                    xpText,
                    xpUntilLevelText,
                    xpProgressBar
                )
            }
        }

        addQuestButton.setOnClickListener {
            showCreateQuestDialog()
        }
    }

    private fun showCreateQuestDialog() {

        val context = requireContext()

        val layout = LinearLayout(context)

        layout.orientation =
            LinearLayout.VERTICAL

        layout.setPadding(
            50,
            10,
            50,
            10
        )

        val questNameInput =
            EditText(context)

        questNameInput.hint =
            "Quest name"

        questNameInput.setSingleLine(true)

        layout.addView(
            questNameInput
        )

        val xpInput =
            EditText(context)

        xpInput.hint =
            "XP reward"

        xpInput.inputType =
            android.text.InputType.TYPE_CLASS_NUMBER

        xpInput.setSingleLine(true)

        layout.addView(
            xpInput
        )

        val dialog =
            AlertDialog.Builder(context)
                .setTitle("⚔ Create New Quest")
                .setMessage(
                    "Add a new quest to your system"
                )
                .setView(layout)
                .setNegativeButton(
                    "CANCEL",
                    null
                )
                .setPositiveButton(
                    "CREATE QUEST",
                    null
                )
                .create()

        dialog.setOnShowListener {

            val createButton =
                dialog.getButton(
                    AlertDialog.BUTTON_POSITIVE
                )

            createButton.setOnClickListener {

                val questName =
                    questNameInput
                        .text
                        .toString()
                        .trim()

                val xpText =
                    xpInput
                        .text
                        .toString()
                        .trim()

                if (questName.isEmpty()) {

                    questNameInput.error =
                        "Enter a quest name"

                    return@setOnClickListener
                }

                if (xpText.isEmpty()) {

                    xpInput.error =
                        "Enter XP reward"

                    return@setOnClickListener
                }

                val xpReward =
                    xpText.toIntOrNull()

                if (xpReward == null ||
                    xpReward <= 0
                ) {

                    xpInput.error =
                        "Enter valid XP"

                    return@setOnClickListener
                }

                dialog.dismiss()

                AlertDialog.Builder(context)
                    .setTitle("⚔ Quest Created")
                    .setMessage(
                        "$questName\n\nReward: +$xpReward XP"
                    )
                    .setPositiveButton(
                        "OK",
                        null
                    )
                    .show()
            }
        }

        dialog.show()
    }

    private fun updateScreen(
        studyQuest: TextView,
        exerciseQuest: TextView,
        readingQuest: TextView,
        levelText: TextView,
        xpText: TextView,
        xpUntilLevelText: TextView,
        xpProgressBar: ProgressBar
    ) {

        val context =
            requireContext()

        val level =
            LevelUpData.getLevel(context)

        val currentLevelXp =
            LevelUpData.getCurrentLevelXp(
                context
            )

        val xpToNextLevel =
            LevelUpData.getXpToNextLevel(
                context
            )

        val xpPerLevel =
            LevelUpData.getXpPerLevel()

        levelText.text =
            "LEVEL $level"

        xpText.text =
            "$currentLevelXp / $xpPerLevel XP"

        xpUntilLevelText.text =
            "$xpToNextLevel XP until Level ${level + 1}"

        xpProgressBar.max =
            xpPerLevel

        xpProgressBar.progress =
            currentLevelXp

        if (LevelUpData.isStudyCompleted(context)) {

            studyQuest.text =
                "✓  Study for 30 minutes"

        } else {

            studyQuest.text =
                "Study for 30 minutes"
        }

        if (LevelUpData.isExerciseCompleted(context)) {

            exerciseQuest.text =
                "✓  Exercise"

        } else {

            exerciseQuest.text =
                "Exercise"
        }

        if (LevelUpData.isReadingCompleted(context)) {

            readingQuest.text =
                "✓  Read for 30 minutes"

        } else {

            readingQuest.text =
                "Read for 30 minutes"
        }
    }
}