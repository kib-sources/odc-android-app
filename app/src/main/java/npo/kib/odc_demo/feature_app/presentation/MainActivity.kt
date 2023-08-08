package npo.kib.odc_demo.feature_app.presentation

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import npo.kib.odc_demo.R
import npo.kib.odc_demo.feature_app.presentation.request_screen.RequestFragment
import npo.kib.odc_demo.feature_app.presentation.send_screen.SendFragment
import npo.kib.odc_demo.feature_app.presentation.settings_screen.SettingsFragment
import npo.kib.odc_demo.feature_app.presentation.home_screen.HomeFragment

class MainActivity : AppCompatActivity(), SwitcherInterface {
    private val settingsFragmentTag = "SETTINGS_FRAGMENT"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, HomeFragment.newInstance())
            .commit()
    }

    override fun openHomeFragment() {
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, HomeFragment.newInstance())
            .commit()
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val settingsFragment = supportFragmentManager.findFragmentByTag(settingsFragmentTag)
        if (item.itemId == R.id.settings && settingsFragment == null) {
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.fragment_container,
                    SettingsFragment.newInstance(),
                    settingsFragmentTag
                )
                .addToBackStack(null)
                .commit()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun openRequireFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, RequestFragment.newInstance())
            .addToBackStack(null)
            .commit()
    }

    override fun openSendFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, SendFragment.newInstance())
            .addToBackStack(null)
            .commit()
    }


}