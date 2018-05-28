package com.blogspot.androidgaidamak.mypicturesslideshow

import android.app.Application
import com.blogspot.androidgaidamak.mypicturesslideshow.data.ImagesRepository
import com.blogspot.androidgaidamak.mypicturesslideshow.data.LocalImagesLiveData
import com.blogspot.androidgaidamak.mypicturesslideshow.data.RemoteImagesLiveData
import okhttp3.OkHttpClient


class SlideShowApplication : Application() {
    lateinit var imageRepository: ImagesRepository
    override fun onCreate() {
        super.onCreate()
        
        val client = OkHttpClient()
        val localDataSource = LocalImagesLiveData(this.contentResolver)
        val remoteDataSource = RemoteImagesLiveData(client)
        imageRepository = ImagesRepository(localDataSource, remoteDataSource)
    }
}