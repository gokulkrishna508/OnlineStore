package com.example.onlinestore.view_model

import android.app.Application
import android.graphics.Matrix
import android.widget.ImageView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlinestore.data.repositories.ImageZoomRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class CarImageZoomViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ImageZoomRepository = ImageZoomRepository()
    val imageStateFlow: MutableStateFlow<Matrix?> = MutableStateFlow(null)
    val loadImageStateFlow: MutableStateFlow<Unit?> = MutableStateFlow(null)

    fun imageLoader(imageId: String,imageView: ImageView){
        viewModelScope.launch(Dispatchers.Main) {
            val imageLoad = repository.imageLoader(imageId,imageView)
            loadImageStateFlow.emit(imageLoad)
        }
    }
    fun zoomImageView(scaleFactor: Float, focusX: Float, focusY: Float){
        viewModelScope.launch (Dispatchers.Main){
            val zoomImage = repository.zoomImage(scaleFactor,focusX,focusY)
            imageStateFlow.emit(zoomImage)
        }
    }

}