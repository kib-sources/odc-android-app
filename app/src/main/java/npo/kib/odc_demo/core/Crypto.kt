package npo.kib.odc_demo.core

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import org.bouncycastle.asn1.ASN1Object
import org.bouncycastle.asn1.DLSequence
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
import org.bouncycastle.openssl.PEMParser
import java.io.StringReader
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.*

import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.RSAPublicKeySpec
import java.security.spec.X509EncodedKeySpec


object Crypto {
    //    fun initPair(): Pair<PublicKey, PrivateKey> {
//        val pairGenerator = KeyPairGenerator.getInstance("RSA")
//        pairGenerator.initialize(512)
//        val pair = pairGenerator.genKeyPair()
//        return pair.public to pair.private
//    }
    private const val alias = "SOK and SPK"
    fun initPair(): Pair<PublicKey, PrivateKey> {

        val keySize = 512
        val kpg = KeyPairGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore"
        )

        kpg.initialize(
            KeyGenParameterSpec.Builder(
                alias,
                KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
            )
                .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                .setKeySize(keySize)
                .build()
        )

        val keyPair = kpg.generateKeyPair()
        return keyPair.public to keyPair.private
    }

    fun hash(vararg strings: String?): ByteArray {
        val salt = "eRgjPi235ps1"
        var v = salt

        for (value in strings.filterNotNull()) {
            v += "|$value"
        }

        return v.sha256()
    }

    fun signature(hexHash: ByteArray, privateKey: PrivateKey): String {
        val privateSignature: Signature = Signature.getInstance("SHA256withRSA")
        privateSignature.initSign(privateKey)
        privateSignature.update(hexHash)

        val signature: ByteArray = privateSignature.sign()
        return Base64.encodeToString(signature, Base64.DEFAULT)
    }

    fun verifySignature(hexHash: ByteArray, signature: String, publicKey: PublicKey): Boolean {
        val publicSignature = Signature.getInstance("SHA256withRSA")
        publicSignature.initVerify(publicKey)
        publicSignature.update(hexHash)

        val signatureBytes = Base64.decode(signature, Base64.DEFAULT)

        return publicSignature.verify(signatureBytes)
    }

    private fun String.sha256(): ByteArray = MessageDigest
        .getInstance("SHA-256")
        .digest(toByteArray())

    fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it) }

    fun getPublicKeyFromStore(): PublicKey {
        val keyStore: KeyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        val entry: KeyStore.Entry = keyStore.getEntry(alias, null)
        return keyStore.getCertificate(alias).publicKey
    }
}

//val alias = "SOK and SPK"
//val keyStore: KeyStore = KeyStore.getInstance("AndroidKeyStore")
//keyStore.load(null)
//val entry: KeyStore.Entry = keyStore.getEntry(alias, null)
//val privateKey: PrivateKey = (entry as KeyStore.PrivateKeyEntry).privateKey
//val publicKey: PublicKey = keyStore.getCertificate(alias).publicKey


fun loadPrivateKey(stored: String): PrivateKey {
    val keySpec = PKCS8EncodedKeySpec(
        Base64.decode(
            stored.toByteArray(StandardCharsets.UTF_8),
            Base64.DEFAULT
        )
    )
    val keyFactory = KeyFactory.getInstance("RSA")
    return keyFactory.generatePrivate(keySpec)
}

fun loadPublicKey(stored: String): PublicKey {
    Log.d("base", stored)
//    val data = Base64.decode(stored.toByteArray(StandardCharsets.UTF_8), Base64.DEFAULT)
//    //val data2 = java.util.Base64.getDecoder().decode(stored.toByteArray(StandardCharsets.UTF_8))
//    Log.d("base", data.toString())
//   // Log.d("base", data2.toString())
//    var nInt = BigInteger(1, data)
//    val spec = X509EncodedKeySpec(data)
//
    val subjectPublicKeyInfo =
        PEMParser(StringReader(stored)).readObject() as SubjectPublicKeyInfo

    val pubKey: PublicKey

    if (PKCSObjectIdentifiers.rsaEncryption === subjectPublicKeyInfo.algorithm.algorithm) {
        val der = subjectPublicKeyInfo.parsePublicKey().toASN1Primitive() as DLSequence
        val modulus = der.getObjectAt(0) as ASN1Object
        val exponent = der.getObjectAt(1) as ASN1Object
        val spec = RSAPublicKeySpec(BigInteger(modulus.toString()), BigInteger(exponent.toString()))
        val keyFactory = KeyFactory.getInstance("RSA")
        pubKey = keyFactory.generatePublic(spec)
        return pubKey
    } else {
        throw Exception("loadPublicKey error")
    }

//    val keyFactory = KeyFactory.getInstance("RSA")
//    return keyFactory.generatePublic(spec)
}

fun PublicKey.getString(): String {
    val keyFactory = KeyFactory.getInstance("RSA")
    val spec = keyFactory.getKeySpec(this, X509EncodedKeySpec::class.java)
    return Base64.encodeToString(spec.encoded, Base64.DEFAULT)
}


fun PrivateKey.getString(): String {
    val spec = PKCS8EncodedKeySpec(encoded)
    return Base64.encodeToString(spec.encoded, Base64.DEFAULT)
}
