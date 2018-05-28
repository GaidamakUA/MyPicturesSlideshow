package com.blogspot.androidgaidamak.mypicturesslideshow.data

import android.arch.lifecycle.LiveData
import android.graphics.BitmapFactory
import okhttp3.*
import java.io.IOException


class RemoteImagesLiveData(private val okhttpClient: OkHttpClient) : LiveData<BitmapErrorWrapper>(), ImageDataSource {
    private val nextImageRequest = Request.Builder().url("https://lorempixel.com/1000/1000/").build()

    override fun nextImage() {
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