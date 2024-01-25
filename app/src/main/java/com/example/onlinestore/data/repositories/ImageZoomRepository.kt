package com.example.onlinestore.data.repositories

import android.widget.ImageView
import com.bumptech.glide.Glide

class ImageZoomRepository {

    fun zoomImage(imageId: String, imageView: ImageView) {
        Glide.with(imageView).load(imageId).into(imageView)
    }
}