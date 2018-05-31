package com.blogspot.androidgaidamak.mypicturesslideshow.data

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.graphics.Bitmap

class ImagesRepository(private val localDataSource: LocalMediaLiveData, private val remoteDataSource: RemoteImagesLiveData) {
    private val mergerLiveData = MediatorLiveData<Bitmap>()

    init {
        mergerLiveData.addSource(localDataSource, { mediaData -> mergerLiveData.value = mediaData?.bitmap })
        mergerLiveData.addSource(remoteDataSource, { wrapper ->
            if (wrapper?.e == null && wrapper?.bitmap != null) {
                mergerLiveData.value = wrapper.bitmap
            } else {
                localDataSource.nextMedia()
            }
        })
    }

    fun getImageLiveData(): LiveData<Bitmap?> = mergerLiveData

    fun nextImage() {
        remoteDataSource.nextImage()
    }
}