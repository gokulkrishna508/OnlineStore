package com.example.onlinestore.view.ui

import android.annotation.SuppressLint
import android.graphics.Matrix
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.onlinestore.databinding.FragmentImageZoomBinding
import com.example.onlinestore.view_model.CarImageZoomViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

@Suppress("UNREACHABLE_CODE")
class ZoomImageFragment : Fragment() {
    private lateinit var binding: FragmentImageZoomBinding
    private val imageViewModel by viewModels<CarImageZoomViewModel>()
    private var matrix:Matrix = Matrix()
    private lateinit var scaleGestureDetector: ScaleGestureDetector
    private var scaleFactor = 1.0f

    companion object {
        var zoomImageCompanionObject: String? = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentImageZoomBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            imageViewModel.imageStateFlow.collect{ updatedMatrix ->
                matrix.set(updatedMatrix)
                binding.zoomImageViewer.imageMatrix = matrix
            }

        }
        zoomImage()

    }
    @SuppressLint("ClickableViewAccessibility")
    private fun zoomImage() = binding.apply{
        scaleGestureDetector = context?.let { ScaleGestureDetector(it, ScaleListener()) }!!
        zoomImageViewer.setOnTouchListener {_, motionEvent ->
            scaleGestureDetector.onTouchEvent(motionEvent)
            true
        }

    }

    private fun initViews() = binding.apply {
        viewLifecycleOwner.lifecycleScope.launch {
            imageViewModel.loadImageStateFlow.collect {
                zoomImageCompanionObject?.let { companionImage ->
                    imageViewModel.imageLoader(companionImage, zoomImageViewer)
                }
            }
        }

    }


    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleFactor *= detector.scaleFactor
            scaleFactor = max(1.0f, min(scaleFactor, 4.0f))
            val focusX: Float = detector.focusX
            val focusY: Float = detector.focusY
            imageViewModel.zoomImageView(scaleFactor, focusX, focusY)
            return true
        }
    }

}
