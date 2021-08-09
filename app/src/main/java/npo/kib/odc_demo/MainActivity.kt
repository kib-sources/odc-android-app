package npo.kib.odc_demo

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import npo.kib.odc_demo.data.BankRepository

import com.google.android.gms.nearby.connection.Payload
import npo.kib.odc_demo.data.P2PConnection


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