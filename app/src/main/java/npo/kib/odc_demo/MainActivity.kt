package npo.kib.odc_demo

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import npo.kib.odc_demo.core.Crypto
import npo.kib.odc_demo.core.getString
import npo.kib.odc_demo.data.BankRepository
import java.security.KeyStore
import java.security.PrivateKey
import java.security.PublicKey

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    override fun onResume() {
        super.onResume()

        val repo = BankRepository(applicationContext)
        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            GlobalScope.launch {

                val wallet = repo.registerWallet()
                //Получаем список <Banknotes, Block, ProtectedBlock>
                val editText = findViewById<EditText>(R.id.editTextNumber)
                val iB = repo.issueBanknotes(editText.text.toString().toInt(), wallet)
                for (b in iB) {
                    Log.d("OpenDigitalCash", b.first.toString())
                    Log.d("OpenDigitalCash", b.second.toString())
                    Log.d("OpenDigitalCash", b.third.toString())

                    //TODO запись банкнот в БД
                }
            }
        }

    }
    //  }

}