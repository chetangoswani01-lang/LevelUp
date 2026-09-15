package com.example.levelup

import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import java.util.Locale
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class HomeFragment : Fragment() {

    private lateinit var questContainer: LinearLayout
    private lateinit var levelText: TextView
    private lateinit var xpText: TextView
    private lateinit var xpUntilLevelText: TextView
    private lateinit var xpProgressBar: ProgressBar

    private val timerViews =
        mutableMapOf<Long, TextView>()

    private val actionButtons =
        mutableMapOf<Long, Button>()

    private val infoViews =
        mutableMapOf<Long, TextView>()

    private val handler =
        android.os.Handler(
            android.os.Looper.getMainLooper()
        )

    private val timerRunnable =
        object : Runnable {

            override fun run() {

                if (!isAdded) {
                    return
                }

                updateRunningTimers()

                handler.postDelayed(
                    this,
                    1000
                )
            }
        }

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

        questContainer =
            view.findViewById(
                R.id.questContainer
            )

        levelText =
            view.findViewById(
                R.id.tvLevel
            )

        xpText =
            view.findViewById(
                R.id.tvXpProgress
            )

        xpUntilLevelText =
            view.findViewById(
                R.id.tvXpUntilLevel
            )

        xpProgressBar =
            view.findViewById(
                R.id.xpProgressBar
            )

        val addQuestButton =
            view.findViewById<Button>(
                R.id.btnAddQuest
            )

        updateLevelDisplay()

        renderAllQuests()

        addQuestButton.setOnClickListener {

            showCreateQuestDialog()
        }

        handler.post(
            timerRunnable
        )
    }

    private fun showCreateQuestDialog() {

        val context =
            requireContext()

        val layout =
            LinearLayout(context)

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

        questNameInput.setSingleLine(
            true
        )

        layout.addView(
            questNameInput,
            fieldParams()
        )

        val targetTimeInput =
            EditText(context)

        targetTimeInput.hint =
            "Target time in minutes"

        targetTimeInput.inputType =
            InputType.TYPE_CLASS_NUMBER

        targetTimeInput.setSingleLine(
            true
        )

        layout.addView(
            targetTimeInput,
            fieldParams()
        )

        val difficultyLabel =
            TextView(context)

        difficultyLabel.text =
            "Difficulty"

        difficultyLabel.textSize =
            14f

        difficultyLabel.setTextColor(
            ContextCompat.getColor(
                context,
                R.color.levelup_text_primary
            )
        )

        difficultyLabel.setPadding(
            0,
            16,
            0,
            5
        )

        layout.addView(
            difficultyLabel
        )

        val difficultySpinner =
            Spinner(context)

        val difficulties =
            arrayOf(
                "Easy",
                "Medium",
                "Hard"
            )

        val adapter =
            ArrayAdapter(
                context,
                android.R.layout.simple_spinner_dropdown_item,
                difficulties
            )

        difficultySpinner.adapter =
            adapter

        difficultySpinner.setSelection(
            1
        )

        layout.addView(
            difficultySpinner,
            fieldParams()
        )

        val information =
            TextView(context)

        information.text =
            "XP is calculated automatically from target time and difficulty."

        information.textSize =
            12f

        information.setTextColor(
            ContextCompat.getColor(
                context,
                R.color.levelup_text_secondary
            )
        )

        information.setPadding(
            0,
            15,
            0,
            5
        )

        layout.addView(
            information
        )

        val dialog =
            AlertDialog.Builder(context)
                .setTitle(
                    "⚔ Create New Quest"
                )
                .setMessage(
                    "Create your challenge"
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
                    questNameInput.text
                        .toString()
                        .trim()

                val targetText =
                    targetTimeInput.text
                        .toString()
                        .trim()

                if (
                    questName.isEmpty()
                ) {

                    questNameInput.error =
                        "Enter a quest name"

                    return@setOnClickListener
                }

                if (
                    targetText.isEmpty()
                ) {

                    targetTimeInput.error =
                        "Enter target time"

                    return@setOnClickListener
                }

                val targetMinutes =
                    targetText.toIntOrNull()

                if (
                    targetMinutes == null ||
                    targetMinutes <= 0
                ) {

                    targetTimeInput.error =
                        "Enter a valid number"

                    return@setOnClickListener
                }

                if (
                    targetMinutes > 1440
                ) {

                    targetTimeInput.error =
                        "Maximum is 1440 minutes"

                    return@setOnClickListener
                }

                val difficulty =
                    difficultySpinner
                        .selectedItem
                        .toString()

                val newQuest =
                    LevelUpData.addCustomQuest(
                        context,
                        questName,
                        targetMinutes,
                        difficulty
                    )

                dialog.dismiss()

                renderAllQuests()

                AlertDialog.Builder(context)
                    .setTitle(
                        "⚔ Quest Created"
                    )
                    .setMessage(
                        "$questName\n\n" +
                                "Target: $targetMinutes minutes\n" +
                                "Difficulty: $difficulty\n" +
                                "Base XP: +${newQuest.baseXp}"
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

    private fun renderAllQuests() {

        if (
            !::questContainer.isInitialized
        ) {
            return
        }

        questContainer.removeAllViews()

        timerViews.clear()
        actionButtons.clear()
        infoViews.clear()

        val context =
            requireContext()

        val defaultQuests =
            LevelUpData.getDefaultQuests(
                context
            )

        for (
        quest in defaultQuests
        ) {

            addQuestCard(
                quest,
                true
            )
        }

        val customQuests =
            LevelUpData.getCustomQuests(
                context
            )

        for (
        quest in customQuests
        ) {

            addQuestCard(
                quest,
                false
            )
        }
    }

    private fun addQuestCard(
        quest: LevelUpData.Quest,
        isDefault: Boolean
    ) {

        val context =
            requireContext()

        val card =
            androidx.cardview.widget.CardView(
                context
            )

        val cardParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

        cardParams.bottomMargin =
            8.dp()

        card.layoutParams =
            cardParams

        card.radius =
            18.dp().toFloat()

        card.cardElevation =
            0f

        card.setCardBackgroundColor(
            ContextCompat.getColor(
                context,
                R.color.levelup_surface
            )
        )

        val mainLayout =
            LinearLayout(context)

        mainLayout.orientation =
            LinearLayout.VERTICAL

        mainLayout.setPadding(
            18.dp(),
            14.dp(),
            18.dp(),
            14.dp()
        )

        val topRow =
            LinearLayout(context)

        topRow.orientation =
            LinearLayout.HORIZONTAL

        topRow.gravity =
            Gravity.CENTER_VERTICAL

        val nameText =
            TextView(context)

        nameText.text =
            if (
                quest.completed
            ) {
                "✓  ${quest.name}"
            } else {
                quest.name
            }

        nameText.textSize =
            15f

        nameText.setTypeface(
            null,
            android.graphics.Typeface.BOLD
        )

        nameText.setTextColor(
            ContextCompat.getColor(
                context,
                R.color.levelup_text_primary
            )
        )

        val nameParams =
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )

        topRow.addView(
            nameText,
            nameParams
        )

        val xpRewardText =
            TextView(context)

        xpRewardText.text =
            "+${quest.baseXp} XP"

        xpRewardText.textSize =
            14f

        xpRewardText.setTypeface(
            null,
            android.graphics.Typeface.BOLD
        )

        xpRewardText.setTextColor(
            ContextCompat.getColor(
                context,
                R.color.levelup_primary
            )
        )

        topRow.addView(
            xpRewardText
        )

        mainLayout.addView(
            topRow
        )

        val infoText =
            TextView(context)

        infoText.text =
            "${quest.targetMinutes} min  •  ${quest.difficulty}"

        infoText.textSize =
            12f

        infoText.setTextColor(
            ContextCompat.getColor(
                context,
                R.color.levelup_text_secondary
            )
        )

        val infoParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

        infoParams.topMargin =
            5.dp()

        mainLayout.addView(
            infoText,
            infoParams
        )

        infoViews[
            quest.id
        ] =
            infoText

        if (
            quest.completed
        ) {

            card.addView(
                mainLayout
            )

            questContainer.addView(
                card
            )

            return
        }

        val actionButton =
            Button(context)

        val running =
            quest.startTime > 0L

        if (
            running
        ) {

            val elapsed =
                formatElapsedTime(
                    quest.startTime
                )

            actionButton.text =
                "COMPLETE  •  $elapsed"

        } else {

            actionButton.text =
                "▶  START QUEST"
        }

        actionButton.textSize =
            12f

        actionButton.setTypeface(
            null,
            android.graphics.Typeface.BOLD
        )

        actionButton.setTextColor(
            ContextCompat.getColor(
                context,
                R.color.levelup_text_primary
            )
        )

        actionButton.backgroundTintList =
            ContextCompat.getColorStateList(
                context,
                R.color.levelup_primary
            )

        val buttonParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                48.dp()
            )

        buttonParams.topMargin =
            10.dp()

        mainLayout.addView(
            actionButton,
            buttonParams
        )

        actionButtons[
            quest.id
        ] =
            actionButton

        val timerText =
            TextView(context)

        timerText.gravity =
            Gravity.CENTER

        timerText.textSize =
            12f

        timerText.setTextColor(
            ContextCompat.getColor(
                context,
                R.color.levelup_cyan
            )
        )

        timerText.text =
            if (
                running
            ) {

                "Elapsed: ${
                    formatElapsedTime(
                        quest.startTime
                    )
                }"

            } else {

                "Ready to start"
            }

        val timerParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

        timerParams.topMargin =
            5.dp()

        mainLayout.addView(
            timerText,
            timerParams
        )

        timerViews[
            quest.id
        ] =
            timerText

        actionButton.setOnClickListener {

            if (
                quest.startTime > 0L
            ) {

                completeQuest(
                    quest
                )

            } else {

                startQuest(
                    quest,
                    isDefault
                )
            }
        }

        card.addView(
            mainLayout
        )

        questContainer.addView(
            card
        )
    }

    private fun startQuest(
        quest: LevelUpData.Quest,
        isDefault: Boolean
    ) {

        val context =
            requireContext()

        if (
            isDefault
        ) {

            LevelUpData.startDefaultQuest(
                context,
                quest.id
            )

        } else {

            LevelUpData.startQuest(
                context,
                quest.id
            )
        }

        renderAllQuests()
    }

    private fun completeQuest(
        quest: LevelUpData.Quest
    ) {

        val context =
            requireContext()

        val actualSeconds =
            getElapsedSeconds(
                quest.startTime
            )

        val actualMinutes =
            (
                    actualSeconds / 60L
                    )
                .toInt()
                .coerceAtLeast(1)

        val finalXp =
            LevelUpData.calculateFinalXp(
                quest.baseXp,
                quest.targetMinutes,
                actualSeconds
            )

        AlertDialog.Builder(context)
            .setTitle(
                "⚔ Complete Quest?"
            )
            .setMessage(
                "${quest.name}\n\n" +
                        "Target: ${quest.targetMinutes} minutes\n" +
                        "Actual: $actualMinutes minutes\n" +
                        "Reward: +$finalXp XP"
            )
            .setNegativeButton(
                "CANCEL",
                null
            )
            .setPositiveButton(
                "COMPLETE"
            ) { _, _ ->

                val earnedXp =
                    if (
                        quest.id <= 3L
                    ) {

                        LevelUpData.completeDefaultQuest(
                            context,
                            quest.id,
                            actualSeconds
                        )

                    } else {

                        LevelUpData.completeQuest(
                            context,
                            quest.id,
                            actualSeconds
                        )
                    }

                updateLevelDisplay()

                renderAllQuests()

                AlertDialog.Builder(context)
                    .setTitle(
                        "🏆 Quest Complete!"
                    )
                    .setMessage(
                        "${quest.name}\n\n" +
                                "Completed in: " +
                                "$actualMinutes minutes\n" +
                                "XP Earned: " +
                                "+$earnedXp XP"
                    )
                    .setPositiveButton(
                        "CONTINUE",
                        null
                    )
                    .show()
            }
            .show()
    }

    private fun updateLevelDisplay() {

        if (
            !::levelText.isInitialized
        ) {
            return
        }

        val context =
            requireContext()

        val level =
            LevelUpData.getLevel(
                context
            )

        val currentXp =
            LevelUpData.getCurrentLevelXp(
                context
            )

        val xpToNext =
            LevelUpData.getXpToNextLevel(
                context
            )

        val xpPerLevel =
            LevelUpData.getXpPerLevel()

        levelText.text =
            "LEVEL $level"

        xpText.text =
            "$currentXp / $xpPerLevel XP"

        xpUntilLevelText.text =
            "$xpToNext XP until Level ${level + 1}"

        xpProgressBar.max =
            xpPerLevel

        xpProgressBar.progress =
            currentXp
    }

    private fun updateRunningTimers() {

        if (
            !::questContainer.isInitialized
        ) {
            return
        }

        val context =
            requireContext()

        val defaultQuests =
            LevelUpData.getDefaultQuests(
                context
            )

        for (
        quest in defaultQuests
        ) {

            if (
                !quest.completed &&
                quest.startTime > 0L
            ) {

                updateTimerUI(
                    quest
                )
            }
        }

        val customQuests =
            LevelUpData.getCustomQuests(
                context
            )

        for (
        quest in customQuests
        ) {

            if (
                !quest.completed &&
                quest.startTime > 0L
            ) {

                updateTimerUI(
                    quest
                )
            }
        }
    }

    private fun updateTimerUI(
        quest: LevelUpData.Quest
    ) {

        val elapsed =
            formatElapsedTime(
                quest.startTime
            )

        timerViews[
            quest.id
        ]?.text =
            "Elapsed: $elapsed"

        actionButtons[
            quest.id
        ]?.text =
            "COMPLETE  •  $elapsed"

        infoViews[
            quest.id
        ]?.text =
            "${quest.targetMinutes} min  •  ${quest.difficulty}"
    }

    private fun getElapsedSeconds(
        startTime: Long
    ): Long {

        if (
            startTime <= 0L
        ) {
            return 0L
        }

        return (
                System.currentTimeMillis() -
                        startTime
                )
            .coerceAtLeast(0L) /
                1000L
    }

    private fun formatElapsedTime(
        startTime: Long
    ): String {

        val totalSeconds =
            getElapsedSeconds(
                startTime
            )

        val hours =
            totalSeconds / 3600L

        val minutes =
            (
                    totalSeconds % 3600L
                    ) / 60L

        val seconds =
            totalSeconds % 60L

        return if (
            hours > 0L
        ) {

            String.format(
                Locale.getDefault(),
                "%02d:%02d:%02d",
                hours,
                minutes,
                seconds
            )

        } else {

            String.format(
                Locale.getDefault(),
                "%02d:%02d",
                minutes,
                seconds
            )
        }
    }

    private fun fieldParams():
            LinearLayout.LayoutParams {

        val params =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

        params.topMargin =
            8.dp()

        return params
    }

    private fun Int.dp(): Int {

        return (
                this *
                        resources.displayMetrics.density
                ).toInt()
    }

    override fun onDestroyView() {

        handler.removeCallbacks(
            timerRunnable
        )

        timerViews.clear()
        actionButtons.clear()
        infoViews.clear()

        super.onDestroyView()
    }
}