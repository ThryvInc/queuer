package com.rndapp.task_feed.views

import android.support.v7.widget.CardView
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import com.rndapp.task_feed.R

class PointsViewHolder(val view: View, val type: PointsType) {
    val pointsTextView = view.findViewById<TextView>(R.id.pointsTextView)
    val pointTypeTextView = view.findViewById<TextView>(R.id.ptTypeTextView)
    val unitsTextView = view.findViewById<TextView>(R.id.unitsTextView)

    var isSelected = false
    set(value) {
        field = value
        val cardView = view as CardView
        if (value) {
            val value = TypedValue()
            view.context.theme.resolveAttribute(R.attr.colorPrimary, value, true)

            cardView.setCardBackgroundColor(value.data)
            pointsTextView.setTextColor(view.context.resources.getColor(R.color.white))
            pointTypeTextView.setTextColor(view.context.resources.getColor(R.color.white))
            unitsTextView.setTextColor(view.context.resources.getColor(R.color.white))
        } else {
            cardView.setCardBackgroundColor(view.context.resources.getColor(R.color.white))
            pointsTextView.setTextColor(view.context.resources.getColor(android.support.design.R.color.abc_secondary_text_material_light))
            pointTypeTextView.setTextColor(view.context.resources.getColor(android.support.design.R.color.abc_secondary_text_material_light))
            unitsTextView.setTextColor(view.context.resources.getColor(android.support.design.R.color.abc_secondary_text_material_light))
        }
    }

    init {
        pointTypeTextView.text = type.toString()
    }

    fun toggleIsSelected() {
        isSelected = !isSelected
    }

    fun setupPoints(points: Int) {
        view.visibility = View.VISIBLE
        pointsTextView.text = "$points"
    }
}

enum class PointsType {
    remaining, finished;
}
