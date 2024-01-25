package com.example.onlinestore.view.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.onlinestore.databinding.FragmentImageZoomBinding
import com.example.onlinestore.view_model.CarImageZoomViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ZoomImageFragment : Fragment() {
    private lateinit var binding: FragmentImageZoomBinding
    private val imageViewModel by viewModels<CarImageZoomViewModel>()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentImageZoomBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() =binding.apply{
        viewLifecycleOwner.lifecycleScope.launch{
            imageViewModel.imageStateFlow.collect() {
                imageViewModel.zoomImageView("https://app.salik.qa/storage/18423/62b32e36e5bf3_1655909941349Patrol-Grey-Side.png",zoomImageViewer)
            }
        }

    }
}