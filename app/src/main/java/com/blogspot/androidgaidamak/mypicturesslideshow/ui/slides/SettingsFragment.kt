package com.blogspot.androidgaidamak.mypicturesslideshow.ui.slides

import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import com.blogspot.androidgaidamak.mypicturesslideshow.R

val KEY_PREF_SLIDE_SWITHC_DELAY = "delay_between_slides"

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.application_preferences)
    }
}
