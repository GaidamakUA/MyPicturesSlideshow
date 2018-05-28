package com.blogspot.androidgaidamak.mypicturesslideshow

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.blogspot.androidgaidamak.mypicturesslideshow.ui.slides.SlidesFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, SlidesFragment.newInstance())
                    .commitNow()
        }
    }

}
