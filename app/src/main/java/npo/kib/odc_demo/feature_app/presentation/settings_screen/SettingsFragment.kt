package npo.kib.odc_demo.feature_app.presentation.settings_screen

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import npo.kib.odc_demo.R

class SettingsFragment : PreferenceFragmentCompat() {

    companion object {
        fun newInstance() = SettingsFragment()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}