package com.rndapp.task_feed.views

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.rndapp.task_feed.R
import com.rndapp.task_feed.listeners.OnSprintClickedListener
import com.rndapp.task_feed.models.Sprint

/**
 * Created by ell on 11/26/17.
 */
class SprintViewHolder(itemView: View, private val listener: OnSprintClickedListener) : RecyclerView.ViewHolder(itemView) {
    private val tv: TextView
    private var sprint: Sprint? = null

    init {
        tv = itemView.findViewById<TextView>(R.id.textView) as TextView

        itemView.setOnClickListener { this@SprintViewHolder.listener.onSprintClicked(this@SprintViewHolder.sprint!!) }
    }

    fun setSprint(sprint: Sprint?) {
        this.sprint = sprint
        tv.text = sprint?.name ?: sprint?.nameFromStartDate()
    }
}