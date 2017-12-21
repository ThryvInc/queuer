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
import com.rndapp.task_feed.api.SprintRequest
import com.rndapp.task_feed.api.VolleyManager
import com.rndapp.task_feed.listeners.OnDayClickedListener
import com.rndapp.task_feed.models.Day
import com.rndapp.task_feed.models.Sprint
import java.util.*

/**
 * Created by ell on 11/26/17.
 */
class DaysFragment: Fragment() {
    var sprint: Sprint? = null
        set(value) {
            field = value
            days.removeAll(days)
            if (value != null) {
                days.addAll(value.days.sorted())
            }
        }
    private var adapter: DayAdapter? = null
    private var days: ArrayList<Day> = ArrayList()
    private var dayClickedListener: OnDayClickedListener? = null

    companion object {
        fun newInstance(listener: OnDayClickedListener): DaysFragment {
            val fragment = DaysFragment()
            fragment.dayClickedListener = listener
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_days, container, false)

        val manager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val rv = rootView.findViewById<RecyclerView>(R.id.rv_days)
        rv.layoutManager = manager

        adapter = days.let { DayAdapter(it, dayClickedListener) }
        rv.adapter = adapter

        rootView.findViewById<View>(R.id.fab).setOnClickListener(View.OnClickListener {
            addDay()
        })

        refresh()

        return rootView
    }

    fun refresh() {
        if (sprint != null) {
            val request = SprintRequest(sprint as Sprint, Response.Listener { sprint ->
                this@DaysFragment.sprint = sprint
                this@DaysFragment.adapter?.notifyDataSetChanged()
            }, Response.ErrorListener { error -> error.printStackTrace() })
            VolleyManager.queue?.add(request)
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
        })
        VolleyManager.queue?.add(request)
    }
}