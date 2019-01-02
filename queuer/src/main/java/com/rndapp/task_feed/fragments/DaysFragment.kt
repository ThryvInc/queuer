package com.rndapp.task_feed.fragments

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import com.android.volley.Response
import com.rndapp.task_feed.R
import com.rndapp.task_feed.activities.DayActivity
import com.rndapp.task_feed.adapters.DayAdapter
import com.rndapp.task_feed.api.CreateDayRequest
import com.rndapp.task_feed.api.DaysRequest
import com.rndapp.task_feed.api.SprintRequest
import com.rndapp.task_feed.api.VolleyManager
import com.rndapp.task_feed.listeners.OnDayClickedListener
import com.rndapp.task_feed.models.Day
import com.rndapp.task_feed.models.Sprint
import kotlinx.android.synthetic.main.standard_recycler.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by ell on 11/26/17.
 */
class DaysFragment: RecyclerViewFragment() {
    var sprint: Sprint? = null
        set(value) {
            field = value
            days.removeAll(days)
            if (value != null) {
                days.addAll(value.days.sorted())
            }
        }
    private var days: ArrayList<Day> = ArrayList()
        set(value) {
            field.removeAll(field)
            field.addAll(value)
            if (adapter == null) {
                if (view != null) {
                    setupDaysIn(view!!)
                }
            } else {
                adapter?.notifyDataSetChanged()
            }
        }
    private var adapter: DayAdapter? = null
    private lateinit var dayClickedListener: OnDayClickedListener

    companion object {
        fun newInstance(listener: OnDayClickedListener): DaysFragment {
            val fragment = DaysFragment()
            fragment.dayClickedListener = listener
            return fragment
        }
    }

    override fun onViewCreated(rootView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(rootView, savedInstanceState)

        setupDaysIn(rootView)

        rootView.findViewById<View>(R.id.fab).setOnClickListener {
            addDay()
        }
    }

    override fun onResume() {
        super.onResume()
        refresh()

        setupDaysIn(view)
    }

    override fun refresh() {
        if (sprint != null) {
            refreshLayout.isRefreshing = true
            val request = DaysRequest(sprint!!.id, Response.Listener { days ->
                this@DaysFragment.days = ArrayList(days.sorted())
                this@DaysFragment.adapter?.updateArray(this@DaysFragment.days)
                refreshLayout.isRefreshing = false
            }, Response.ErrorListener { error ->
                error.printStackTrace()
                refreshLayout.isRefreshing = false
            })
            VolleyManager.queue?.add(request)
        }
    }

    fun setupDaysIn(view: View?) {
        if (view != null) {
            days.sorted()

            adapter = days.let { DayAdapter(it, dayClickedListener) }
            recyclerView.adapter = adapter
        }
    }

    fun addDay() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(context, this::dayDateChosen, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    fun dayDateChosen(datePicker: DatePicker, year: Int, month: Int, day: Int) {
        val date = GregorianCalendar(year, month, day).time
        val request = CreateDayRequest(sprint!!.id, date, Response.Listener { day ->
            refresh()
        }, Response.ErrorListener { error ->
            error.printStackTrace()
            refresh()
        })
        VolleyManager.queue?.add(request)
    }
}