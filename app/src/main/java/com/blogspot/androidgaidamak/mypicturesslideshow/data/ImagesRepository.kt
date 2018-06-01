package com.blogspot.androidgaidamak.mypicturesslideshow.data

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData

class ImagesRepository(private val localDataSource: LocalMediaLiveData, private val remoteDataSource: RemoteImagesLiveData) {
    private val mergerLiveData = MediatorLiveData<MediaData>()

    init {
        mergerLiveData.addSource(localDataSource, { mediaData -> mergerLiveData.value = mediaData })
        mergerLiveData.addSource(remoteDataSource, { wrapper ->
            if (wrapper?.e == null && wrapper?.bitmap != null) {
                mergerLiveData.value = MediaData(MediaData.MediaType.IMAGE, wrapper.bitmap, null)
            } else {
                localDataSource.nextMedia()
            }
        })
    }

    fun getImageLiveData(): LiveData<MediaData> = mergerLiveData

    fun nextImage() {
        remoteDataSource.nextImage()
    }
}