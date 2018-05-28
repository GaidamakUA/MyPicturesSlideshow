package com.blogspot.androidgaidamak.mypicturesslideshow.data

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.graphics.Bitmap

class ImagesRepository(private val localDataSource: LocalImagesLiveData, private val remoteDataSource: RemoteImagesLiveData) : ImageDataSource {
    private val mergerLiveData = MediatorLiveData<Bitmap>()

    init {
        mergerLiveData.addSource(localDataSource, { bitmap -> mergerLiveData.value = bitmap })
        mergerLiveData.addSource(remoteDataSource, { wrapper ->
            if (wrapper?.e == null && wrapper?.bitmap != null) {
                mergerLiveData.value = wrapper.bitmap
            } else {
                localDataSource.nextImage()
            }
        })
    }

    fun getImageLiveData(): LiveData<Bitmap?> = mergerLiveData

    override fun nextImage() {
        remoteDataSource.nextImage()
    }
}