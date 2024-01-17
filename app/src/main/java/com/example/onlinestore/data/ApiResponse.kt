package com.example.onlinestore.data

import org.json.JSONObject

data class ApiResponse(val code:Int?, val status:Boolean?, val response: JSONObject?, val message:String?)
