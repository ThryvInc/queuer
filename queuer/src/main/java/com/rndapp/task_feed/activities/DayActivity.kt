package com.rndapp.task_feed.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.rndapp.task_feed.R
import com.rndapp.task_feed.fragments.DayFragment
import com.rndapp.task_feed.fragments.DayFragment.Companion.DAY_KEY
import com.rndapp.task_feed.models.Day

/**
 * Created by ell on 8/5/17.
 */
class DayActivity: AppCompatActivity() {
    var day: Day? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_day)

        val extras = intent?.extras
        if (extras != null) {
            val dayExtra = extras.getSerializable(DAY_KEY)
            if (dayExtra != null && dayExtra is Day) {
                day = dayExtra
            }
        }

        supportActionBar?.title = day?.nameFromDate()

        if (savedInstanceState == null) {
            val fragment = DayFragment()
            supportFragmentManager.beginTransaction()
                    .add(R.id.frameLayout, fragment)
                    .commit()
        }
    }
}