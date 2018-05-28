package com.blogspot.androidgaidamak.mypicturesslideshow.ui.slides

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.os.Handler
import android.support.v7.preference.PreferenceManager
import com.blogspot.androidgaidamak.mypicturesslideshow.data.RemoteImagesLiveData


class SlidesViewModel(context: Application) : AndroidViewModel(context), SharedPreferences.OnSharedPreferenceChangeListener {
    private val imageLiveData = RemoteImagesLiveData(context)//LocalImagesLiveData(context)
    private val remoteImagesLiveData = RemoteImagesLiveData(context)
    private val handler = Handler()
    private var frameSwitchDelay: Long

    init {
        imageLiveData.nextImage()
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        frameSwitchDelay = sharedPref.getString(KEY_PREF_SLIDE_SWITHC_DELAY, "5").toLong() * 1000
        sharedPref.registerOnSharedPreferenceChangeListener(this)
    }

    fun getImageLiveData(): LiveData<Bitmap?> = Transformations.map(imageLiveData, { wrapper -> wrapper.bitmap })

    fun startUpdatingImage() {
        postUpdateImage()
    }

    private fun postUpdateImage() {
        handler.postDelayed({
            postUpdateImage()
            imageLiveData.nextImage()
        }, frameSwitchDelay)
    }

    fun stopUpdatingImage() {
        handler.removeCallbacksAndMessages(null)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == KEY_PREF_SLIDE_SWITHC_DELAY) {
            val stringDelay = sharedPreferences?.getString(KEY_PREF_SLIDE_SWITHC_DELAY, "5")?.toLong()
            frameSwitchDelay = (stringDelay ?: 5) * 1000
        }
    }
}
