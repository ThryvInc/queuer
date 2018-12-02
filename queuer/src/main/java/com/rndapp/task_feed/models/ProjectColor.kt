package com.rndapp.task_feed.models

import com.rndapp.task_feed.R

enum class ProjectColor(val rgb: Int) {
    SMOG(-12169892),
    GREEN(-13508189),
    GOLDENROD(-12169892),
    RED(-47803),
    BLUE(-16737587),
    YELLOW(-526544),
    ORANGE(-28593),
    PLUM(-6606593),
    TURQUOISE(-16661061);

    companion object {

        fun idFromProjectColor(projectColorInt: Int): Int = when (projectColorInt) {
            ProjectColor.BLUE.rgb -> R.color.blue
            ProjectColor.GOLDENROD.rgb -> R.color.goldenrod
            ProjectColor.GREEN.rgb -> R.color.green
            ProjectColor.RED.rgb -> R.color.red
            ProjectColor.PLUM.rgb -> R.color.plum
            ProjectColor.TURQUOISE.rgb -> R.color.turquoise
            ProjectColor.SMOG.rgb -> R.color.smog
            ProjectColor.ORANGE.rgb -> R.color.orange
            ProjectColor.YELLOW.rgb -> R.color.yellow
            else -> R.color.smog
        }

        fun projectColorFromId(id: Int): ProjectColor = when (id) {
            R.color.blue -> ProjectColor.BLUE
            R.color.goldenrod -> ProjectColor.GOLDENROD
            R.color.green -> ProjectColor.GREEN
            R.color.red -> ProjectColor.RED
            R.color.plum -> ProjectColor.PLUM
            R.color.turquoise -> ProjectColor.TURQUOISE
            R.color.smog -> ProjectColor.SMOG
            R.color.orange -> ProjectColor.ORANGE
            R.color.yellow -> ProjectColor.YELLOW
            else -> ProjectColor.SMOG
        }

    }
}