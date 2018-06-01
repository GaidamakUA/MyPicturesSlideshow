package com.blogspot.androidgaidamak.mypicturesslideshow.utils

import android.graphics.Bitmap
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import com.google.android.exoplayer2.ui.PlayerView

class ViewController(val imageBack: ImageView,
                     val playerViewBack: PlayerView,
                     val imageFront: ImageView,
                     val playerViewFront: PlayerView) {
    private val interpolator = LinearInterpolator()
    private var fadeDelay: Long = 1000

    private var frontShowing: Boolean = true

    fun isFrontShowing() = frontShowing

    fun showImage(bitmap: Bitmap) {
        if (frontShowing) {
            hideFrontView()

            playerViewBack.visibility = View.GONE
            imageBack.visibility = View.VISIBLE
            imageBack.setImageBitmap(bitmap)
        } else {
            playerViewFront.visibility = View.GONE
            imageFront.visibility = View.VISIBLE
            imageFront.setImageBitmap(bitmap)

            animateFadeIn(imageFront)
        }
        frontShowing = !frontShowing
    }

    fun showVideo() {
        if (frontShowing) {
            hideFrontView()

            imageBack.visibility = View.GONE
            playerViewBack.visibility = View.VISIBLE
        } else {
            imageFront.visibility = View.GONE
            playerViewFront.visibility = View.VISIBLE
            playerViewFront.alpha = 1f
        }
        frontShowing = !frontShowing
    }

    private fun hideFrontView() {
        if (playerViewFront.visibility == View.VISIBLE) {
            animateFadeOut(playerViewFront)
            imageFront.visibility = View.GONE
        } else if (imageFront.visibility == View.VISIBLE) {
            animateFadeOut(imageFront)
            playerViewFront.visibility = View.GONE
        }
    }

    private fun animateFadeIn(view: View) {
        view.scaleX = 1.5f
        view.scaleY = 1.5f
        view.alpha = 0f
        view.animate().setInterpolator(interpolator)
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(fadeDelay)
    }

    private fun animateFadeOut(view: View) {
        view.scaleX = 1f
        view.scaleY = 1f
        view.animate().setInterpolator(interpolator)
                .alpha(0f)
                .scaleX(1.5f)
                .scaleY(1.5f)
                .setDuration(fadeDelay)
    }
}