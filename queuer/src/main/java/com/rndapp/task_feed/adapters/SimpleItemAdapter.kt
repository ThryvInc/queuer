package com.rndapp.task_feed.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.rndapp.task_feed.R
import com.rndapp.task_feed.listeners.OnSimpleItemClickedListener
import com.rndapp.task_feed.views.SimpleViewHolder

/**
 * Created by ell on 12/7/17.
 */
abstract class SimpleItemAdapter<T>(protected var array: List<T>):
        RecyclerView.Adapter<SimpleViewHolder>(), OnSimpleItemClickedListener {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item, parent, false)
        return SimpleViewHolder(view, this)
    }

    override fun getItemCount(): Int {
        return array.size
    }
}