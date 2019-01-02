package com.rndapp.task_feed.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rndapp.task_feed.R
import kotlinx.android.synthetic.main.standard_recycler.*

abstract class RecyclerViewFragment: Fragment() {
    var layoutId: Int = R.layout.standard_recycler

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layoutId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        refreshLayout.setOnRefreshListener(this::refresh)

        recyclerView.layoutManager = getLayoutManager()
    }

    open fun getLayoutManager(): RecyclerView.LayoutManager {
        return LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    abstract fun refresh()
}
