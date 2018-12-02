package com.rndapp.task_feed.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by ell on 8/5/17.
 */
data class DayTask(val id: Int, @SerializedName("day_id") val dayId: Int, val task: Task?,
                   @SerializedName("next_id") val nextId: Int,
                   @SerializedName("previous_id") val prevId: Int): Serializable