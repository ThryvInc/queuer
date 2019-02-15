package com.rndapp.task_feed.view_models

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.android.volley.Response
import com.rndapp.task_feed.api.DaysRequest
import com.rndapp.task_feed.api.SprintProjectsRequest
import com.rndapp.task_feed.api.SprintRequest
import com.rndapp.task_feed.api.VolleyManager
import com.rndapp.task_feed.models.Day
import com.rndapp.task_feed.models.Sprint
import com.rndapp.task_feed.models.SprintProject

class SprintActivityViewModel(val sprintId: Int): ViewModel() {
    var sprintLiveData = MutableLiveData<Sprint>()
    var sprintProjectsLiveData = MutableLiveData<List<SprintProject>>()
    var daysLiveData = MutableLiveData<List<Day>>()
    val isLoadingLiveData = MutableLiveData<Boolean>()
    val isLoadingDaysLiveData = MutableLiveData<Boolean>()
    val isLoadingSprintProjectsLiveData = MutableLiveData<Boolean>()

    init {
        isLoadingLiveData.observeForever {
            if (it != null) {
                isLoadingDaysLiveData.value = it
                isLoadingSprintProjectsLiveData.value = it
            }
        }
    }

    fun refreshSprint() {
        isLoadingLiveData.value = true
        val request = SprintRequest(sprintId, Response.Listener { sprint ->
            sprintLiveData.value = sprint
            isLoadingLiveData.value = false
        }, Response.ErrorListener { error ->
            error.printStackTrace()
            isLoadingLiveData.value = false
        })
        VolleyManager.queue?.add(request)
    }

    fun refreshSprintProjects() {
        isLoadingSprintProjectsLiveData.value = true
        val request = SprintProjectsRequest(sprintId, Response.Listener { sprintProjects ->
            sprintProjectsLiveData.value = sprintProjects
            isLoadingSprintProjectsLiveData.value = false
        }, Response.ErrorListener { error ->
            error.printStackTrace()
            isLoadingSprintProjectsLiveData.value = false
        })
        VolleyManager.queue?.add(request)
    }

    fun refreshDays() {
        isLoadingDaysLiveData.value = true
        val request = DaysRequest(sprintId, Response.Listener { days ->
            daysLiveData.value = days
            isLoadingDaysLiveData.value = false
        }, Response.ErrorListener { error ->
            error.printStackTrace()
            isLoadingDaysLiveData.value = false
        })
        VolleyManager.queue?.add(request)

    }
}