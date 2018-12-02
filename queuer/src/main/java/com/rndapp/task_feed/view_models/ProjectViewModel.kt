package com.rndapp.task_feed.view_models

import com.rndapp.task_feed.R
import com.rndapp.task_feed.models.Project
import com.rndapp.task_feed.models.ProjectColor

fun styleForProject(project: Project): Int {
    return when(project.color) {
        ProjectColor.BLUE.rgb -> R.style.BlueTheme
        ProjectColor.RED.rgb -> R.style.RedTheme
        ProjectColor.YELLOW.rgb -> R.style.YellowTheme
        ProjectColor.GOLDENROD.rgb -> R.style.GoldenrodTheme
        ProjectColor.TURQUOISE.rgb -> R.style.TurquoiseTheme
        ProjectColor.ORANGE.rgb -> R.style.OrangeTheme
        ProjectColor.PLUM.rgb -> R.style.PlumTheme
        else -> R.style.AppTheme
    }
}