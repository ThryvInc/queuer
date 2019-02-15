package com.rndapp.task_feed.views

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.rndapp.task_feed.R

class SprintProjectViewHolder(itemView: View, private val listener: (RecyclerView.ViewHolder) -> Unit):
        RecyclerView.ViewHolder(itemView) {
    private val tv: TextView = itemView.findViewById(R.id.textView)
    private val rightTextView: TextView? = itemView.findViewById(R.id.rightTextView)
    private val firstTextView: TextView? = itemView.findViewById(R.id.firstTextView)
    private val secondTextView: TextView? = itemView.findViewById(R.id.secondTextView)
    private val thirdTextView: TextView? = itemView.findViewById(R.id.thirdTextView)
    private val fourthTextView: TextView? = itemView.findViewById(R.id.fourthTextView)
    private val fifthTextView: TextView? = itemView.findViewById(R.id.fifthTextView)
    private val sixthTextView: TextView? = itemView.findViewById(R.id.sixthTextView)
    private val seventhTextView: TextView? = itemView.findViewById(R.id.seventhTextView)
    private val textViews: List<TextView?> = listOf(firstTextView, secondTextView, thirdTextView, fourthTextView, fifthTextView, sixthTextView, seventhTextView)

    init {
        itemView.setOnClickListener { listener(this) }
    }

    fun reset() {
        textViews.forEach { it?.visibility = View.GONE }
    }

    fun setText(text: String?) {
        tv.text = text
    }

    fun setRightText(text: String?) {
        if (text != null) {
            rightTextView?.text = text
            rightTextView?.visibility = View.VISIBLE
        } else {
            rightTextView?.visibility = View.GONE
        }
    }

    fun setNthText(n: Int, text: String?) {
        if (text != null) {
            textViews[n]?.text = text
            textViews[n]?.visibility = View.VISIBLE
        } else {
            textViews[n]?.visibility = View.GONE
        }
    }
}
