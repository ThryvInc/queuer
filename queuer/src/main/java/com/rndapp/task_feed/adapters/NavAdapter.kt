package com.rndapp.task_feed.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

import com.rndapp.task_feed.R
import com.rndapp.task_feed.models.Project

//import org.jetbrains.annotations.Nullable;

import java.util.ArrayList

/**
 * Created by eschrock on 2/4/14.
 */
class NavAdapter(private val context: Context, private val projects: ArrayList<Project>) : BaseAdapter() {

    override fun getCount(): Int {
        return projects.size
    }

    override fun getItem(position: Int): Project {
        return projects[position]
    }

    override fun getItemId(position: Int): Long {
        return projects[position].id.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.item, null)
        }

        val project = getItem(position)
        val tv = convertView!!.findViewById<TextView>(R.id.textView)
        tv.text = project.name
        tv.setTextColor(project.color)
        return convertView
    }
}
