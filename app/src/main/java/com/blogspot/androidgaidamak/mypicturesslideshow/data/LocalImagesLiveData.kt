package com.blogspot.androidgaidamak.mypicturesslideshow.data

import android.arch.lifecycle.LiveData
import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.provider.MediaStore
import java.io.File

class LocalImagesLiveData(private val contentResolver: ContentResolver) : LiveData<Bitmap?>(), ImageDataSource {
    private lateinit var imagePaths: List<String>
    private var currentImagePathIndex = 0

    override fun nextImage() {
        LoadNextImageTask().execute(currentImagePathIndex)
        currentImagePathIndex++
    }

    // TODO Make this class static and remove vararg parameters
    private inner class LoadNextImageTask : AsyncTask<Int, Void, Bitmap?>() {
        override fun doInBackground(vararg params: Int?): Bitmap? {
            if (!::imagePaths.isInitialized) {
                initImagePaths()
            }
            val imagePathIndex: Int = params[0]!! % imagePaths.size

            return if (imagePaths.size > imagePathIndex) {
                val uri = Uri.fromFile(File(imagePaths[imagePathIndex]))
                val bitmapStream = contentResolver.openInputStream(uri)
                BitmapFactory.decodeStream(bitmapStream)
            } else {
                null
            }
        }

        override fun onPostExecute(result: Bitmap?) {
            value = result
        }
    }

    private fun initImagePaths() {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                MediaStore.Images.Media.DATE_ADDED + " DESC")

        val tempList = mutableListOf<String>()
        val imagePathColumnNumber = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)

        while (cursor.moveToNext()) {
            val path = cursor.getString(imagePathColumnNumber)
            tempList.add(path)
        }

        cursor.close()
        imagePaths = tempList.toList()
    }
}