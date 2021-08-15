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


object Crypto {
    private const val simKeysAlias = "SOK and SPK"
    private const val oneTimeKeysAlias = "OTOK and OTPK"

    fun initSkp() = initPair(simKeysAlias)

    fun initOtkp() = initPair(oneTimeKeysAlias)

    private fun initPair(alias: String): Pair<PublicKey, PrivateKey> {
        Log.d("OpenDigitalCashA", alias)
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
                .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
                .setKeySize(keySize)
                .build()
        )
        val keyPair = kpg.generateKeyPair()
        return keyPair.public to keyPair.private
    }


    fun getSimKeys(): Pair<PublicKey, PrivateKey> {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        val entry = keyStore.getEntry(simKeysAlias, null)
        val privateKey = (entry as KeyStore.PrivateKeyEntry).privateKey
        val publicKey = keyStore.getCertificate(simKeysAlias).publicKey
        return publicKey to privateKey
    }

    fun hash(vararg strings: String?) = strings.joinToString(separator = " ", postfix = "]!L3bP9a@GM6U*LL").sha256()

    fun signature(hexHash: ByteArray, privateKey: PrivateKey): String {
        val privateSignature: Signature = Signature.getInstance("SHA256withRSA")
        privateSignature.initSign(privateKey)
        privateSignature.update(hexHash)
        Log.d("OpenDigitalCashH", hexHash.toHex())
        val signature: ByteArray = privateSignature.sign()
        //return Base64.encodeToString(signature, Base64.DEFAULT)
        return signature.toHex()
    }

    fun verifySignature(hexHash: ByteArray, signature: String, publicKey: PublicKey): Boolean {
        val publicSignature = Signature.getInstance("SHA256withRSA")
        publicSignature.initVerify(publicKey)
        publicSignature.update(hexHash)

       // val signatureBytes = Base64.decode(signature, Base64.DEFAULT)
        val signatureBytes = signature.decodeHex()
        return publicSignature.verify(signatureBytes)
    }

    private fun String.sha256(): ByteArray = MessageDigest
        .getInstance("SHA-256")
        .digest(toByteArray())

    fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it) }

    fun String.decodeHex(): ByteArray {
        check(length % 2 == 0) { "Must have an even length" }

        return chunked(2)
            .map { it.toInt(16).toByte() }
            .toByteArray()
    }

    fun getPublicKeyFromStore(): PublicKey {
        val keyStore: KeyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        val entry: KeyStore.Entry = keyStore.getEntry(simKeysAlias, null)
        return keyStore.getCertificate(simKeysAlias).publicKey
    }

    fun getPrivateKeyFromStore(): PrivateKey {
        val keyStore: KeyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        val entry: KeyStore.Entry = keyStore.getEntry(simKeysAlias, null)
        return (entry as KeyStore.PrivateKeyEntry).privateKey
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
//    val data = Base64.decode(stored.toByteArray(StandardCharsets.UTF_8), Base64.DEFAULT)
//    //val data2 = java.util.Base64.getDecoder().decode(stored.toByteArray(StandardCharsets.UTF_8))
//    var nInt = BigInteger(1, data)
//    val spec = X509EncodedKeySpec(data)
//
    val subjectPublicKeyInfo = if (stored.take(30) == "-----BEGIN RSA PUBLIC KEY-----")
        PEMParser(StringReader(stored)).readObject() as SubjectPublicKeyInfo
    else PEMParser(
        StringReader(
            "-----BEGIN RSA PUBLIC KEY-----\n${
                stored.replace(
                    " ",
                    "\n"
                )
            }-----END RSA PUBLIC KEY-----"
        )
    ).readObject() as SubjectPublicKeyInfo

    if (PKCSObjectIdentifiers.rsaEncryption === subjectPublicKeyInfo.algorithm.algorithm) {
        val der = subjectPublicKeyInfo.parsePublicKey().toASN1Primitive() as DLSequence
        val modulus = der.getObjectAt(0) as ASN1Object
        val exponent = der.getObjectAt(1) as ASN1Object
        val spec = RSAPublicKeySpec(BigInteger(modulus.toString()), BigInteger(exponent.toString()))
        val keyFactory = KeyFactory.getInstance("RSA")
        return keyFactory.generatePublic(spec)
    } else {
        throw Exception("loadPublicKey error")
    }

//    val keyFactory = KeyFactory.getInstance("RSA")
//    return keyFactory.generatePublic(spec)
}

fun PublicKey.getString(): String {
    val pubBytes = this.encoded
    val spkInfo = SubjectPublicKeyInfo.getInstance(pubBytes)
    val primitive = spkInfo.parsePublicKey()
    val publicKeyPKCS1 = primitive.encoded
    return Base64.encodeToString(publicKeyPKCS1, Base64.DEFAULT)
}


fun PrivateKey.getString(): String {
    val spec = PKCS8EncodedKeySpec(encoded)
    return Base64.encodeToString(spec.encoded, Base64.DEFAULT)
}
