package com.blogspot.androidgaidamak.mypicturesslideshow.data

import android.graphics.Bitmap
import android.net.Uri

class MediaData(val mediaType: MediaType, val bitmap: Bitmap?, val videoUri: Uri?) {

    enum class MediaType {
        VIDEO,
        IMAGE
    }
}