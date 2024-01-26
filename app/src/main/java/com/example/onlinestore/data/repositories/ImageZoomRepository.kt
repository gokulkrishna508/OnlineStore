package com.example.onlinestore.data.repositories

import android.graphics.Matrix
import android.widget.ImageView
import com.bumptech.glide.Glide

class ImageZoomRepository {
    private val matrix = Matrix()

    fun imageLoader(imageId: String, imageView: ImageView){
        Glide.with(imageView).load(imageId).into(imageView)
    }
    fun zoomImage(scaleFactor: Float?, focusX: Float?, focusY: Float?): Matrix {
        val resultMatrix = Matrix(matrix)
        if (scaleFactor != null && focusX != null && focusY != null) {
            resultMatrix.postScale(scaleFactor, scaleFactor, focusX, focusY)
        }
        return resultMatrix
    }

}
