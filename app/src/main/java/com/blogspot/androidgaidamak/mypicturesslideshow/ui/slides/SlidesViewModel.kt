package com.blogspot.androidgaidamak.mypicturesslideshow.ui.slides

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.graphics.Bitmap
import android.os.Handler
import com.blogspot.androidgaidamak.mypicturesslideshow.data.ImageLiveData


class SlidesViewModel(private val context: Application) : AndroidViewModel(context) {
    private val FRAME_SWITCH_DELAY = 3000L
    private val imageLiveData = ImageLiveData(context)
    private val handler = Handler()


    init {
        imageLiveData.nextImage()
    }

    public fun getImageLiveData(): LiveData<Bitmap?> = imageLiveData

    fun startUpdatingImage() {
        postUpdateImage()
    }

    private fun postUpdateImage() {
        handler.postDelayed({
            postUpdateImage()
            imageLiveData.nextImage()
        }, FRAME_SWITCH_DELAY)
    }

    fun stopUpdatingImage() {
        handler.removeCallbacksAndMessages(null)
    }
}
