package com.blogspot.androidgaidamak.mypicturesslideshow.ui.slides

import android.Manifest
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.blogspot.androidgaidamak.mypicturesslideshow.R
import com.blogspot.androidgaidamak.mypicturesslideshow.data.MediaData
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.slides_fragment.*


class SlidesFragment : Fragment() {
    private val READ_EXTERNAL_STORAGE_CODE_REQUEST_CODE = 0;

    private val observer = Observer<MediaData> { mediaData -> showMedia(mediaData) }
    private var viewModel: SlidesViewModel? = null

    private var playerBack: SimpleExoPlayer? = null
    private var playerFront: SimpleExoPlayer? = null
    private lateinit var factory: ExtractorMediaSource.Factory
    private lateinit var viewController: ViewController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.slides_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            initViewModelAndBind()
        } else {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), READ_EXTERNAL_STORAGE_CODE_REQUEST_CODE)
        }

        analogClock.setAutoUpdate(true)

        val dataSourceFactory = DefaultDataSourceFactory(
                activity,
                Util.getUserAgent(activity, getString(R.string.app_name)))
        factory = ExtractorMediaSource.Factory(dataSourceFactory)
        viewController = ViewController(imageBack, playerViewBack, imageFront, playerViewFront)
    }

    override fun onStart() {
        super.onStart()
        playerBack = ExoPlayerFactory.newSimpleInstance(activity, DefaultTrackSelector())
        playerFront = ExoPlayerFactory.newSimpleInstance(activity, DefaultTrackSelector())
        playerViewBack.player = playerBack
        playerViewFront.player = playerFront
        playerBack?.playWhenReady = true
        playerFront?.playWhenReady = true
    }

    override fun onResume() {
        super.onResume()
        viewModel?.startUpdatingImage()
    }

    override fun onPause() {
        super.onPause()
        viewModel?.stopUpdatingImage()
    }

    override fun onStop() {
        playerViewBack.player = null
        playerViewFront.player = null
        playerBack?.release()
        playerBack = null
        playerFront?.release()
        playerFront = null

        super.onStop()
    }

    private fun initViewModelAndBind() {
        viewModel = ViewModelProviders.of(this).get(SlidesViewModel::class.java)
        viewModel?.getMediaLiveData()?.observe(this, observer)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == READ_EXTERNAL_STORAGE_CODE_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                initViewModelAndBind()
                viewModel?.startUpdatingImage()
            } else {
                AlertDialog.Builder(activity!!)
                        .setTitle(R.string.dialog_permission_is_missilng_title)
                        .setMessage(R.string.dialog_permission_is_missilng_message)
                        .setCancelable(false)
                        .setPositiveButton(android.R.string.ok, { _, _ -> activity!!.finish() })
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun showMedia(mediaData: MediaData?) {
        if (mediaData == null) return
        playerBack?.stop()
        playerFront?.stop()
        when (mediaData.mediaType) {
            MediaData.MediaType.IMAGE -> viewController.showImage(mediaData.bitmap!!)
            MediaData.MediaType.VIDEO -> {
                val mediaSource = factory.createMediaSource(mediaData.videoUri)
                if (viewController.isFrontShowing()) {
                    playerBack?.prepare(mediaSource)
                } else {
                    playerFront?.prepare(mediaSource)
                }
                viewController.showVideo()
            }
        }
    }

    class ViewController(val imageBack: ImageView,
                         val playerViewBack: PlayerView,
                         val imageFront: ImageView,
                         val playerViewFront: PlayerView) {
        private var frontShowing: Boolean = true

        fun isFrontShowing() = frontShowing

        fun showImage(bitmap: Bitmap) {
            if (frontShowing) {
                playerViewBack.visibility = View.GONE
                playerViewFront.visibility = View.GONE
                imageFront.visibility = View.GONE

                imageBack.visibility = View.VISIBLE
                imageBack.setImageBitmap(bitmap)
            } else {
                playerViewFront.visibility = View.GONE
                playerViewBack.visibility = View.GONE
                imageBack.visibility = View.GONE

                imageFront.visibility = View.VISIBLE
                imageFront.setImageBitmap(bitmap)
            }
            frontShowing = !frontShowing
        }

        fun showVideo() {
            if (frontShowing) {
                imageFront.visibility = View.GONE
                imageBack.visibility = View.GONE

                playerViewFront.visibility = View.GONE
                playerViewBack.visibility = View.VISIBLE
            } else {
                imageFront.visibility = View.GONE
                imageBack.visibility = View.GONE

                playerViewBack.visibility = View.GONE
                playerViewFront.visibility = View.VISIBLE
            }
            frontShowing = !frontShowing
        }
    }
}
