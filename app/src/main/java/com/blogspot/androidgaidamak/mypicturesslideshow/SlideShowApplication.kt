package com.blogspot.androidgaidamak.mypicturesslideshow

import android.app.Application
import okhttp3.OkHttpClient


class SlideShowApplication : Application() {
    var client = OkHttpClient()
}