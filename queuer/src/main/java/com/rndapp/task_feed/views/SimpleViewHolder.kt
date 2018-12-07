package com.rndapp.task_feed.views

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.rndapp.task_feed.R

open class SimpleViewHolder(itemView: View, private val listener: (RecyclerView.ViewHolder) -> Unit):
        RecyclerView.ViewHolder(itemView) {
    private val tv: TextView = itemView.findViewById(R.id.textView)
    private val rightTextView: TextView? = itemView.findViewById(R.id.rightTextView)

    init {
        itemView.setOnClickListener { listener(this) }
    }

    fun setText(text: String?) {
        tv.text = text
    }

    fun setRightText(text: String?) {
        if (text != null) {
            rightTextView?.text = text
        }
    }
}

class HighlightableViewHolder(itemView: View, listener: (RecyclerView.ViewHolder) -> Unit): SimpleViewHolder(itemView, listener) {
    var isHighlighted = false

    fun toggleHighlight() {
        if (isHighlighted) {
            itemView.setBackgroundResource(R.color.transparent)
        } else {
            itemView.setBackgroundResource(R.color.separator_color)
        }

        isHighlighted = !isHighlighted
    }
}
