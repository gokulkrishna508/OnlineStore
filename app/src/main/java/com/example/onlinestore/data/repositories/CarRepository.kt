package com.example.onlinestore.data.repositories

import android.util.Log
import com.example.onlinestore.data.ApiResponse
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class CarRepository {

    fun apiCall(currentPage: Int): ApiResponse {
        val url = "https://app.salik.qa/api/v1/cars?category_id=2&pickup_date=2023-12-28&pickup_time=6:00%20PM&return_date=2023-12-30&return_time=6:00%20PM"
        var lastPage = 3
        val client = OkHttpClient()
        val urlPage = "$url&page=$currentPage"
        val request = Request.Builder().url(urlPage).get().build()

        var code: Int
        var apiResponse: JSONObject? = null
        var message: String
        val status = false

        client.newCall(request).execute().use { response ->
            code = response.code
            message = response.message
            try {
                val responseString = response.body?.string().let { JSONObject(it) }
                apiResponse = responseString
                Log.e("@@url", "<<< ${request}")
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("@@exception", "<<< ${e.message}")
            }
        }

        return ApiResponse(code, status, apiResponse, message)
    }
}

