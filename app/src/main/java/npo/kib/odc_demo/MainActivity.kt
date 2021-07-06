package npo.kib.odc_demo

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import npo.kib.odc_demo.core.Crypto
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
        val prefs = getSharedPreferences("npo.kib.odc_demo", MODE_PRIVATE)
        // Проверка на первый запуск
        if (prefs.getBoolean("first", true)) {

            val repo = BankRepository()

            GlobalScope.launch {
                val bok = repo.getBok()
                Log.d("Api", bok.toString())
            }

            //Создаем sok and spk
            Crypto.initPair()

            //Берем созданные ключи из keystore и выводим
            val alias = "SOK and SPK"
            val keyStore: KeyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)
            val entry: KeyStore.Entry = keyStore.getEntry(alias, null)
            val privateKey: PrivateKey = (entry as KeyStore.PrivateKeyEntry).privateKey
            val publicKey: PublicKey = keyStore.getCertificate(alias).publicKey

            Log.d("key", publicKey.toString())
            Log.d("key", privateKey.toString())


            prefs.edit().putBoolean("first", false).apply()
        }
    }
}