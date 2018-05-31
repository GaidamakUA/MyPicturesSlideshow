package com.blogspot.androidgaidamak.mypicturesslideshow.data

import android.arch.lifecycle.LiveData
import android.content.ContentResolver
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.provider.MediaStore
import java.io.File

class LocalMediaLiveData(private val contentResolver: ContentResolver) : LiveData<MediaData?>() {
    private lateinit var mediaPaths: List<Pair<MediaData.MediaType, String>>
    private var currentImagePathIndex = 0

    fun nextMedia() {
        LoadNextImageTask().execute(currentImagePathIndex)
        currentImagePathIndex++
    }

    // TODO Make this class static and remove vararg parameters
    private inner class LoadNextImageTask : AsyncTask<Int, Void, MediaData?>() {
        override fun doInBackground(vararg params: Int?): MediaData? {
            if (!::mediaPaths.isInitialized) {
                initImagePaths()
            }
            val imagePathIndex: Int = params[0]!! % mediaPaths.size
            val mediaPath = mediaPaths[imagePathIndex]

            return if (mediaPath.first == MediaData.MediaType.IMAGE) {
                val uri = Uri.fromFile(File(mediaPath.second))
                val bitmapStream = contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(bitmapStream)
                MediaData(mediaPath.first, bitmap, null)
            } else {
                MediaData(mediaPath.first, null, Uri.parse(mediaPath.second))
            }
        }

        override fun onPostExecute(result: MediaData?) {
            value = result
        }
    }

    private fun initImagePaths() {
        val projection = arrayOf(MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.MEDIA_TYPE)
        val selection = (MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                + " OR "
                + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)
        val cursor = contentResolver.query(MediaStore.Files.getContentUri("external"),
                projection,
                selection,
                null,
                "${MediaStore.Files.FileColumns.DATE_ADDED} DESC")

        val tempList = mutableListOf<Pair<MediaData.MediaType, String>>()
        val mediaPathColumnNumber = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
        val mediaTypeColumnNumber = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE)

        while (cursor.moveToNext()) {
            val path = cursor.getString(mediaPathColumnNumber)
            val type = getMediaType(cursor.getInt(mediaTypeColumnNumber))
            tempList.add(Pair(type, path))
        }

        cursor.close()
        mediaPaths = tempList.toList()
    }

    private fun getMediaType(mediaTypeCode: Int) =
            when (mediaTypeCode) {
                MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE -> MediaData.MediaType.IMAGE
                MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO -> MediaData.MediaType.VIDEO
                else -> throw IllegalArgumentException("Unexpected media type: $mediaTypeCode")
            }
}