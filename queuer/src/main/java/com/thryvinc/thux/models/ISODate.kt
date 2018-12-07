package com.thryvinc.thux.models

import com.google.gson.GsonBuilder
import java.text.SimpleDateFormat
import java.util.*

fun isoMockPattern() = "yyyy-MM-dd'T'HH:mm:ss.ssssss"
fun isoPattern() = "yyyy-MM-dd'T'HH:mm:ss.sss'Z'"
fun isoDatePattern() = "yyyy-MM-dd"
fun isoFormatter(): SimpleDateFormat = SimpleDateFormat(isoPattern(), Locale.US)
fun isoDateFormatter(): SimpleDateFormat = SimpleDateFormat(isoDatePattern(), Locale.US)
fun Date.isoString(): String = isoFormatter().format(this)
fun String.isoDate(): Date = isoFormatter().parse(this)

fun isoGson() = GsonBuilder().setDateFormat(isoPattern()).create()
fun isoMockGson() = GsonBuilder().setDateFormat(isoMockPattern()).create()
