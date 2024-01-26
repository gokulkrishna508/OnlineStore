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


/*
import android.graphics.Matrix
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ZoomViewModel : ViewModel() {

    private val _matrix = MutableLiveData<Matrix>()
    val matrix: LiveData<Matrix> = _matrix

    private val matrix = Matrix()

    fun applyZoom(scaleFactor: Float, focusX: Float, focusY: Float) {
        matrix.postScale(scaleFactor, scaleFactor, focusX, focusY)
        _matrix.value = matrix
    }
}
/*/


/*import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.yourpackage.viewmodel.ZoomViewModel
import kotlinx.android.synthetic.main.your_activity_layout.*

class YourActivity : AppCompatActivity() {

    private val zoomViewModel: ZoomViewModel by viewModels()
    private lateinit var matrix: Matrix
    private lateinit var scaleGestureDetector: ScaleGestureDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.your_activity_layout)

        matrix = Matrix()

        // Initialize ScaleGestureDetector
        scaleGestureDetector = ScaleGestureDetector(this, ScaleListener())

        imageView.setOnTouchListener { _, event ->
            scaleGestureDetector.onTouchEvent(event)
            true
        }

        // Observe changes in the matrix from the ViewModel
        zoomViewModel.matrix.observe(this, Observer { updatedMatrix ->
            matrix.set(updatedMatrix)
            imageView.imageMatrix = matrix
        })
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val scaleFactor: Float = detector.scaleFactor
            val focusX: Float = detector.focusX
            val focusY: Float = detector.focusY

            // Update ViewModel with zoom data
            zoomViewModel.applyZoom(scaleFactor, focusX, focusY)
            return true
        }
    }
}
*/