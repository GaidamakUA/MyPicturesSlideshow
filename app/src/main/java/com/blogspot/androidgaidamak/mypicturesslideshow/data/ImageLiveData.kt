package com.blogspot.androidgaidamak.mypicturesslideshow.data

import android.app.Application
import android.arch.lifecycle.LiveData
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.provider.MediaStore
import java.io.File

public class ImageLiveData(private val context: Application) : LiveData<Bitmap?>() {
    private lateinit var imagePaths: List<String>
    private var currentImagePathIndex = 0

    fun nextImage() {
        LoadNextImageTask().execute(currentImagePathIndex)
        currentImagePathIndex++
    }

    private inner class LoadNextImageTask : AsyncTask<Int, Void, Bitmap?>() {
        override fun doInBackground(vararg params: Int?): Bitmap? {
            if (!::imagePaths.isInitialized) {
                initImagePaths()
            }
            val imagePathIndex = params[0]

            return if (imagePaths.size > imagePathIndex!!) {
                val uri = Uri.fromFile(File(imagePaths[imagePathIndex]))
                val bitmapStream = context.contentResolver.openInputStream(uri)
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
        val cursor = context.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
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