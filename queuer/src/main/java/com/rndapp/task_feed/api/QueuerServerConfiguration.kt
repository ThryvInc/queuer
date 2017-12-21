package com.rndapp.task_feed.api

class QueuerServerConfiguration {

    companion object {

        val BASE_URL = "https://queuer-production.herokuapp.com/api/v1/" //192.168.11.152:3000
        val API_KEY_PREFERENCE = "com.rndapp.queuer.api_key_pref"
        val API_KEY_HEADER = "X-Qer-Authorization"

    }
}