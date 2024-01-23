package com.example.project3
import android.util.Log
import org.bouncycastle.crypto.AsymmetricCipherKeyPair
import org.bouncycastle.crypto.SecretWithEncapsulation
import org.bouncycastle.pqc.crypto.frodo.FrodoKEMExtractor
import org.bouncycastle.pqc.crypto.frodo.FrodoKEMGenerator
import org.bouncycastle.pqc.crypto.frodo.FrodoKeyGenerationParameters
import org.bouncycastle.pqc.crypto.frodo.FrodoKeyPairGenerator
import org.bouncycastle.pqc.crypto.frodo.FrodoParameters
import org.bouncycastle.pqc.crypto.frodo.FrodoPrivateKeyParameters
import org.bouncycastle.pqc.crypto.frodo.FrodoPublicKeyParameters
import java.io.InputStream
import java.io.OutputStream
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.crypto.Mac
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec


fun genKeyPair(customParam: String):AsymmetricCipherKeyPair { // custom KeyPairGen function

    lateinit var myParam: FrodoParameters
    Log.v("debug", "myParam: $customParam")
    when(customParam) {
        "frodokem640aes" -> myParam = FrodoParameters.frodokem640aes
        "frodokem640shake" -> myParam = FrodoParameters.frodokem640shake
        "frodokem976aes" -> myParam = FrodoParameters.frodokem976aes
        "frodokem976shake" -> myParam = FrodoParameters.frodokem976shake
        "frodokem1344aes" -> myParam = FrodoParameters.frodokem1344aes
        "frodokem1344shake" -> myParam = FrodoParameters.frodokem1344shake
//        else -> myParam = FrodoParameters.frodokem640aes
    }
    // In order to generate a keypair you must first create the generator parameters,
    // using a source of randomness and the algorithms parameters.
    val random = SecureRandom()
    val genParam = FrodoKeyGenerationParameters(random, myParam)

    // Now you can create the key pair generator and initialize it with the parameter.
    val keyPairGenerator = FrodoKeyPairGenerator()
    keyPairGenerator.init(genParam)

    // Generate the keypair
    val keyPair = keyPairGenerator.generateKeyPair()
    return keyPair
}

fun encapsulateKey(pubParameters: FrodoPublicKeyParameters): SecretWithEncapsulation? {
    val random = SecureRandom()

    val frodoEncCipher = FrodoKEMGenerator(random)
    val secWenc = frodoEncCipher.generateEncapsulated(pubParameters)

    return secWenc
}

fun decapsulateKey(privParameters: FrodoPrivateKeyParameters, generated_cipher_text: ByteArray ): ByteArray? {
    val frodoDecCipher = FrodoKEMExtractor(privParameters)
    val recipientSecret = frodoDecCipher.extractSecret(generated_cipher_text)
    return recipientSecret
}

// AES encryption
fun encrypt(input: InputStream, output: OutputStream, secretKey: SecretKey) {
    try {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val ivParams = IvParameterSpec(byteArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15))
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParams)

        val outputCipher = CipherOutputStream(output, cipher)

        val buffer = ByteArray(1024)
        var bytesRead: Int
        while (input.read(buffer).also { bytesRead = it } != -1) {
            outputCipher.write(buffer, 0, bytesRead)
        }

        outputCipher.close()
        output.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

// AES decryption
fun decrypt(input: InputStream, output: OutputStream, secretKey: SecretKey) {
    try {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val ivParams = IvParameterSpec(byteArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15))
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParams)

        val inputCipher = CipherInputStream(input, cipher)

        val buffer = ByteArray(1024)
        var bytesRead: Int
        while (inputCipher.read(buffer).also { bytesRead = it } != -1) {
            output.write(buffer, 0, bytesRead)
        }

        inputCipher.close()
        output.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

// HMAC generalization
fun generateHMAC(input: InputStream, secretKey: SecretKey): ByteArray {
    val hmac: Mac = Mac.getInstance("HmacSHA512")
    hmac.init(secretKey)

    val buffer = ByteArray(1024)
    var bytesRead: Int
    while (input.read(buffer).also { bytesRead = it } != -1) {
        hmac.update(buffer, 0, bytesRead)
    }
    return hmac.doFinal()
}
// HMAC verifycation
fun verifyHMAC(input: InputStream, receivedHMAC: ByteArray, secretKey: SecretKey): Boolean {
    val hmac: Mac = Mac.getInstance("HmacSHA512")
    hmac.init(secretKey)

    val buffer = ByteArray(1024)
    var bytesRead: Int
    while (input.read(buffer).also { bytesRead = it } != -1) {
        hmac.update(buffer, 0, bytesRead)
    }
    val generatedHMAC = hmac.doFinal()

    // So sánh hai mã HMAC
    return MessageDigest.isEqual(generatedHMAC, receivedHMAC)
}

