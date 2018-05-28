package com.blogspot.androidgaidamak.mypicturesslideshow.data

import android.app.Application
import android.arch.lifecycle.LiveData
import android.graphics.BitmapFactory
import com.blogspot.androidgaidamak.mypicturesslideshow.SlideShowApplication
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import java.io.IOException


class RemoteImagesLiveData(context: Application) : LiveData<BitmapErrorWrapper>() {
    private val okhttpClient = (context as SlideShowApplication).client
    private val nextImageRequest = Request.Builder().url("https://lorempixel.com/1000/1000/").build()

    fun nextImage() {
        okhttpClient.newCall(nextImageRequest)
                .enqueue(object : Callback {
                    override fun onFailure(call: Call?, e: IOException?) {
                        postValue(BitmapErrorWrapper(e, null))
                    }

                    override fun onResponse(call: Call?, response: Response?) {
                        val bm = BitmapFactory.decodeStream(response?.body()?.byteStream())
                        postValue(BitmapErrorWrapper(null, bm))
                    }
                })
    }
}