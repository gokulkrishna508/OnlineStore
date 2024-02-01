package com.example.onlinestore.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlinestore.data.CarData
import com.example.onlinestore.data.repositories.CarRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

class CarViewModel(application: Application): AndroidViewModel(application) {
    private val repository: CarRepository = CarRepository()
    val apiResponseStateFlow: MutableStateFlow<JSONObject?> = MutableStateFlow(null)
    var currentPage = 1
    var lastPage = 1
    var carList: MutableList<CarData?> = mutableListOf()

    fun fetchData(currentPage: Int){
        viewModelScope.launch(Dispatchers.IO) {
            val data = repository.apiCall(currentPage).response
            apiResponseStateFlow.emit(data)
        }
    }
}