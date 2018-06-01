package com.blogspot.androidgaidamak.mypicturesslideshow.ui.slides

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.content.SharedPreferences
import android.os.Handler
import android.support.v7.preference.PreferenceManager
import com.blogspot.androidgaidamak.mypicturesslideshow.SlideShowApplication
import com.blogspot.androidgaidamak.mypicturesslideshow.data.MediaData


class SlidesViewModel(context: Application) : AndroidViewModel(context), SharedPreferences.OnSharedPreferenceChangeListener {
    private val repository = (context as SlideShowApplication).imageRepository
    private val handler = Handler()
    private var frameSwitchDelay: Long

    init {
        repository.nextImage()
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        frameSwitchDelay = sharedPref.getString(KEY_PREF_SLIDE_SWITHC_DELAY, "5").toLong() * 1000
        sharedPref.registerOnSharedPreferenceChangeListener(this)
    }

    fun getMediaLiveData(): LiveData<MediaData> = repository.getImageLiveData()

    fun startUpdatingImage() {
        postUpdateImage()
    }

    private fun postUpdateImage() {
        handler.postDelayed({
            postUpdateImage()
            repository.nextImage()
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
