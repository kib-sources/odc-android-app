package npo.kib.odc_demo.feature_app.domain.core

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import npo.kib.odc_demo.feature_app.domain.util.logOut
import org.bouncycastle.asn1.ASN1Object
import org.bouncycastle.asn1.DLSequence
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
import org.bouncycastle.openssl.PEMParser
import java.io.StringReader
import java.math.BigInteger
import java.security.*
import java.security.spec.RSAPublicKeySpec
import java.util.*

object Crypto {
    private const val SIM_KEYS_ALIAS = "SOK and SPK"
    private const val KEY_STORE_TYPE = "AndroidKeyStore"

    fun initSKP() = initPair(SIM_KEYS_ALIAS)

    fun initOTKP(uuid: UUID) = initPair(uuid.toString()).first

    private fun initPair(alias: String): Pair<PublicKey, PrivateKey> {
        val keySize = 512
        val kpg = KeyPairGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_RSA, KEY_STORE_TYPE
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
        val keyStore = KeyStore.getInstance(KEY_STORE_TYPE)
        keyStore.load(null)
        val entry = keyStore.getEntry(SIM_KEYS_ALIAS, null)
        val privateKey = (entry as KeyStore.PrivateKeyEntry).privateKey
        val publicKey = keyStore.getCertificate(SIM_KEYS_ALIAS).publicKey
        return publicKey to privateKey
    }

    fun getOtpk(uuid: UUID): PrivateKey {
        val keyStore = KeyStore.getInstance(KEY_STORE_TYPE)
        keyStore.load(null)
        val entry = keyStore.getEntry(uuid.toString(), null)
        return (entry as KeyStore.PrivateKeyEntry).privateKey
    }

    fun deleteOneTimeKeys(uuid: UUID) {
        val keyStore = KeyStore.getInstance(KEY_STORE_TYPE)
        keyStore.load(null)
        keyStore.deleteEntry(uuid.toString())
    }

    fun hash(vararg strings: String?): ByteArray {
        return strings.joinToString(separator = " ", postfix = "]!L3bP9a@GM6U*LL")
            .logOut("To hash: ").sha256().logOut("Hash result: ")
    }

    fun signature(hexHash: ByteArray, privateKey: PrivateKey): String {
        val privateSignature = Signature.getInstance("SHA256withRSA")
        privateSignature.initSign(privateKey)
        privateSignature.update(hexHash)
        return privateSignature.sign().toHex()
    }

    fun verifySignature(hexHash: ByteArray, signature: String, publicKey: PublicKey): Boolean {
        val publicSignature = Signature.getInstance("SHA256withRSA")
        publicSignature.initVerify(publicKey)
        publicSignature.update(hexHash)
        val signatureBytes = signature.decodeHex()
        return publicSignature.verify(signatureBytes)
    }

    fun String.sha256(): ByteArray =
        MessageDigest.getInstance("SHA-256").digest(toByteArray())

    fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it) }
}

/**
 * Returns a PublicKey from the string representation
 */
fun String.loadPublicKey(): PublicKey {
    val subjectPublicKeyInfo = if (take(30) == "-----BEGIN RSA PUBLIC KEY-----")
        PEMParser(StringReader(this)).readObject() as SubjectPublicKeyInfo
    else PEMParser(
        StringReader(
            "-----BEGIN RSA PUBLIC KEY-----\n${replace(" ", "\n")}-----END RSA PUBLIC KEY-----"
        )
    ).readObject() as SubjectPublicKeyInfo

    if (PKCSObjectIdentifiers.rsaEncryption == subjectPublicKeyInfo.algorithm.algorithm) {
        val der = subjectPublicKeyInfo.parsePublicKey().toASN1Primitive() as DLSequence
        val modulus = der.getObjectAt(0) as ASN1Object
        val exponent = der.getObjectAt(1) as ASN1Object
        val spec = RSAPublicKeySpec(BigInteger(modulus.toString()), BigInteger(exponent.toString()))
        val keyFactory = KeyFactory.getInstance("RSA")
        return keyFactory.generatePublic(spec)
    } else {
        throw Exception("loadPublicKey error")
    }
}

/**
 * Returns a string representation of PublicKey
 */
fun PublicKey.getString(): String {
    val pubBytes = encoded
    val spkInfo = SubjectPublicKeyInfo.getInstance(pubBytes)
    val primitive = spkInfo.parsePublicKey()
    val publicKeyPKCS1 = primitive.encoded
    return Base64.encodeToString(publicKeyPKCS1, Base64.DEFAULT)
}

/**
 * Returns a string representation of PublicKey in PEM file format
 */
fun PublicKey.getStringPem(): String {
    return "-----BEGIN RSA PUBLIC KEY-----\n${getString()}-----END RSA PUBLIC KEY-----"
}