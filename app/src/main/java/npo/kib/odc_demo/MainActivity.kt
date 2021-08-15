package npo.kib.odc_demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val transaction = supportFragmentManager.beginTransaction()
        if (savedInstanceState == null) {
            transaction.add(R.id.fragment_container, ExchangeFragment.newInstance())
                .commitAllowingStateLoss()
        }


    }
}