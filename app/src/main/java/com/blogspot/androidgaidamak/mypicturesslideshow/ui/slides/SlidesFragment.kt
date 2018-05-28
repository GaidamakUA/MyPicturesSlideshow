package com.blogspot.androidgaidamak.mypicturesslideshow.ui.slides

import android.Manifest
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blogspot.androidgaidamak.mypicturesslideshow.R
import kotlinx.android.synthetic.main.slides_fragment.*

class SlidesFragment : Fragment() {
    private val READ_EXTERNAL_STORAGE_CODE_REQUEST_CODE = 0;

    companion object {
        fun newInstance() = SlidesFragment()
    }

    private var viewModel: SlidesViewModel? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.slides_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_CALENDAR)
                == PackageManager.PERMISSION_GRANTED) {
            initViewModelAndBind()
        } else {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), READ_EXTERNAL_STORAGE_CODE_REQUEST_CODE)
        }

        analogClock.setAutoUpdate(true)
    }

    override fun onResume() {
        super.onResume()
        viewModel?.startUpdatingImage()
    }

    override fun onPause() {
        super.onPause()
        viewModel?.stopUpdatingImage()
    }

    private fun initViewModelAndBind() {
        viewModel = ViewModelProviders.of(this).get(SlidesViewModel::class.java)
        viewModel?.getImageLiveData()?.observe(this, Observer { bitmap -> image.showImage(bitmap) })
        image.showImage(viewModel?.getImageLiveData()?.value)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == READ_EXTERNAL_STORAGE_CODE_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                initViewModelAndBind()
                viewModel?.startUpdatingImage()
            } else {
                AlertDialog.Builder(activity!!)
                        .setTitle("Permission is missing")
                        .setMessage("For app to work it needs permission to read your storage.")
                        .setCancelable(false)
                        .setPositiveButton("OK", { _, _ -> activity!!.finish() })
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
}
