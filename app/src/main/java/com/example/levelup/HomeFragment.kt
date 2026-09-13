package com.example.levelup

import android.os.Bundle
import android.text.InputType
import android.view.Gravity
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

    // Running quest timer TextViews
    private val timerViews =
        mutableMapOf<Long, TextView>()

    // Running quest buttons
    private val actionButtons =
        mutableMapOf<Long, Button>()

    // Quest information TextViews
    private val infoViews =
        mutableMapOf<Long, TextView>()

    // Main-thread handler
    private val handler =
        android.os.Handler(
            android.os.Looper.getMainLooper()
        )

    // Updates timers every second
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


    // =========================================================
    // CREATE VIEW
    // =========================================================

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


    // =========================================================
    // VIEW CREATED
    // =========================================================

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


        // Show current XP and level
        updateLevelDisplay()

        // Display all quests
        renderAllQuests()


        // Add quest button
        addQuestButton.setOnClickListener {

            showCreateQuestDialog()
        }


        // Start timer updates
        handler.post(
            timerRunnable
        )
    }


    // =========================================================
    // CREATE QUEST DIALOG
    // =========================================================

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


        // =====================================================
        // QUEST NAME
        // =====================================================

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


        // =====================================================
        // TARGET TIME
        // =====================================================

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


        // =====================================================
        // DIFFICULTY
        // =====================================================

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

        // Medium selected by default
        difficultySpinner.setSelection(
            1
        )

        layout.addView(
            difficultySpinner,
            fieldParams()
        )


        // =====================================================
        // INFORMATION
        // =====================================================

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


        // =====================================================
        // DIALOG
        // =====================================================

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


                // =================================================
                // VALIDATE NAME
                // =================================================

                if (questName.isEmpty()) {

                    questNameInput.error =
                        "Enter a quest name"

                    return@setOnClickListener
                }


                // =================================================
                // VALIDATE TARGET TIME
                // =================================================

                if (targetText.isEmpty()) {

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


                if (targetMinutes > 1440) {

                    targetTimeInput.error =
                        "Maximum is 1440 minutes"

                    return@setOnClickListener
                }


                // =================================================
                // GET DIFFICULTY
                // =================================================

                val difficulty =
                    difficultySpinner
                        .selectedItem
                        .toString()


                // =================================================
                // SAVE QUEST
                // =================================================

                val newQuest =
                    LevelUpData.addCustomQuest(
                        context,
                        questName,
                        targetMinutes,
                        difficulty
                    )


                dialog.dismiss()


                // Refresh Home screen
                renderAllQuests()


                // =================================================
                // CONFIRMATION
                // =================================================

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


    // =========================================================
    // RENDER ALL QUESTS
    // =========================================================

    private fun renderAllQuests() {

        if (!::questContainer.isInitialized) {
            return
        }


        questContainer.removeAllViews()


        timerViews.clear()
        actionButtons.clear()
        infoViews.clear()


        val context =
            requireContext()


        // =====================================================
        // DEFAULT QUESTS
        // =====================================================

        val defaultQuests =
            LevelUpData.getDefaultQuests(
                context
            )


        for (quest in defaultQuests) {

            addQuestCard(
                quest = quest,
                isDefault = true
            )
        }


        // =====================================================
        // CUSTOM QUESTS
        // =====================================================

        val customQuests =
            LevelUpData.getCustomQuests(
                context
            )


        for (quest in customQuests) {

            addQuestCard(
                quest = quest,
                isDefault = false
            )
        }
    }


    // =========================================================
    // ADD QUEST CARD
    // =========================================================

    private fun addQuestCard(
        quest: LevelUpData.Quest,
        isDefault: Boolean
    ) {

        val context =
            requireContext()


        // =====================================================
        // CARD
        // =====================================================

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


        // =====================================================
        // MAIN LAYOUT
        // =====================================================

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


        // =====================================================
        // TOP ROW
        // =====================================================

        val topRow =
            LinearLayout(context)

        topRow.orientation =
            LinearLayout.HORIZONTAL

        topRow.gravity =
            Gravity.CENTER_VERTICAL


        // QUEST NAME

        val nameText =
            TextView(context)

        nameText.text =
            if (quest.completed) {
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


        // XP

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


        // =====================================================
        // INFORMATION
        // =====================================================

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


        infoViews[quest.id] =
            infoText


        // =====================================================
        // COMPLETED QUEST
        // =====================================================

        if (quest.completed) {

            card.addView(
                mainLayout
            )

            questContainer.addView(
                card
            )

            return
        }


        // =====================================================
        // ACTION BUTTON
        // =====================================================

        val actionButton =
            Button(context)


        val isRunning =
            quest.startTime > 0L


        if (isRunning) {

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


        actionButtons[quest.id] =
            actionButton


        // =====================================================
        // TIMER
        // =====================================================

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


        if (isRunning) {

            timerText.text =
                "Elapsed: ${formatElapsedTime(quest.startTime)}"

        } else {

            timerText.text =
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


        timerViews[quest.id] =
            timerText


        // =====================================================
        // BUTTON CLICK
        // =====================================================

        actionButton.setOnClickListener {

            if (quest.startTime > 0L) {

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


    // =========================================================
    // START QUEST
    // =========================================================

    private fun startQuest(
        quest: LevelUpData.Quest,
        isDefault: Boolean
    ) {

        val context =
            requireContext()


        if (isDefault) {

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


    // =========================================================
    // COMPLETE QUEST
    // =========================================================

    private fun completeQuest(
        quest: LevelUpData.Quest
    ) {

        val context =
            requireContext()


        // Calculate elapsed minutes
        val actualMinutes =
            getElapsedMinutes(
                quest.startTime
            )


        // Calculate preview XP
        val finalXp =
            LevelUpData.calculateFinalXp(
                baseXp =
                    quest.baseXp,

                targetMinutes =
                    quest.targetMinutes,

                actualMinutes =
                    actualMinutes
            )


        // =====================================================
        // CONFIRMATION DIALOG
        // =====================================================

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


                val earnedXp: Int


                // =================================================
                // DEFAULT QUEST
                // =================================================

                if (quest.id <= 3L) {

                    earnedXp =
                        LevelUpData.completeDefaultQuest(
                            context = context,
                            questId = quest.id,
                            actualSeconds =
                                actualMinutes * 60L
                        )

                }


                // =================================================
                // CUSTOM QUEST
                // =================================================

                else {

                    earnedXp =
                        LevelUpData.completeQuest(
                            context = context,
                            questId = quest.id,
                            actualSeconds =
                                actualMinutes * 60L
                        )
                }


                // Update XP and level
                updateLevelDisplay()

                // Refresh quest list
                renderAllQuests()


                // =================================================
                // SUCCESS MESSAGE
                // =================================================

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


    // =========================================================
    // UPDATE LEVEL DISPLAY
    // =========================================================

    private fun updateLevelDisplay() {

        if (!::levelText.isInitialized) {
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


    // =========================================================
    // UPDATE RUNNING TIMERS
    // =========================================================

    private fun updateRunningTimers() {

        if (!::questContainer.isInitialized) {
            return
        }


        val context =
            requireContext()


        // =====================================================
        // DEFAULT QUESTS
        // =====================================================

        val defaultQuests =
            LevelUpData.getDefaultQuests(
                context
            )


        for (quest in defaultQuests) {

            if (
                !quest.completed &&
                quest.startTime > 0L
            ) {

                updateTimerUI(
                    quest
                )
            }
        }


        // =====================================================
        // CUSTOM QUESTS
        // =====================================================

        val customQuests =
            LevelUpData.getCustomQuests(
                context
            )


        for (quest in customQuests) {

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


    // =========================================================
    // UPDATE ONE TIMER
    // =========================================================

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
            "${quest.targetMinutes} min  •  " +
                    "${quest.difficulty}"
    }


    // =========================================================
    // GET ELAPSED MINUTES
    // =========================================================

    private fun getElapsedMinutes(
        startTime: Long
    ): Int {

        if (startTime <= 0L) {
            return 0
        }


        val elapsedMillis =
            System.currentTimeMillis() -
                    startTime


        val minutes =
            elapsedMillis /
                    (60 * 1000)


        /*
         * At least 1 minute is counted.
         * This prevents a quest completed
         * immediately from getting an
         * unrealistic 0-minute value.
         */

        return minutes
            .toInt()
            .coerceAtLeast(1)
    }


    // =========================================================
    // FORMAT TIMER
    // =========================================================

    private fun formatElapsedTime(
        startTime: Long
    ): String {

        if (startTime <= 0L) {
            return "00:00"
        }


        val elapsedMillis =
            System.currentTimeMillis() -
                    startTime


        val totalSeconds =
            (elapsedMillis / 1000)
                .coerceAtLeast(0)


        val hours =
            totalSeconds / 3600


        val minutes =
            (totalSeconds % 3600) / 60


        val seconds =
            totalSeconds % 60


        return if (hours > 0) {

            String.format(
                "%02d:%02d:%02d",
                hours,
                minutes,
                seconds
            )

        } else {

            String.format(
                "%02d:%02d",
                minutes,
                seconds
            )
        }
    }


    // =========================================================
    // DIALOG FIELD PARAMETERS
    // =========================================================

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


    // =========================================================
    // DP HELPER
    // =========================================================

    private fun Int.dp(): Int {

        return (
                this *
                        resources.displayMetrics.density
                ).toInt()
    }


    // =========================================================
    // DESTROY VIEW
    // =========================================================

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