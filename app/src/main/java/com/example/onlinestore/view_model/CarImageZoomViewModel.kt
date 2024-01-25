package com.example.onlinestore.view_model

import android.app.Application
import android.widget.ImageView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlinestore.data.repositories.ImageZoomRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class CarImageZoomViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ImageZoomRepository = ImageZoomRepository()
    val imageStateFlow: MutableStateFlow<Unit?> = MutableStateFlow(null)

    fun zoomImageView(imageId: String,imageView: ImageView){
        viewModelScope.launch (Dispatchers.Main){
            val zoomImage = repository.zoomImage(imageId,imageView)
            imageStateFlow.emit(zoomImage)
        }
    }

}