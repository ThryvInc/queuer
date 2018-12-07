package com.thryvinc.thux.models

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.thryvinc.thux.into
import org.json.JSONObject
import java.lang.reflect.Type

inline fun <reified T> Gson.fromJsonString(json: String?): T? = this.fromJson<T>(json, object: TypeToken<T>() {}.type)

inline fun <reified T> jsonStringToModel(string: String, gson: Gson, key: String = snakeCaseKeyForType<T>()): T? {
    return JSONObject(string)[key]?.toString() into gson::fromJsonString
}

inline fun <reified T> jsonStringToModels(string: String, gson: Gson, key: String = pluralKeyForType<T>(), type: Type = object: TypeToken<ArrayList<T>>() {}.type): ArrayList<T>? {
    return gson.fromJson<ArrayList<T>>(JSONObject(string)[key]?.toString(), type)
}
