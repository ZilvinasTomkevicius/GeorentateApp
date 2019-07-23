package app.georentate.georentate_app.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.georentate.georentate_app.R

class SettingsFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_settings, container, false)

    companion object {
        fun newInstance(): SettingsFragment = SettingsFragment()
    }
}