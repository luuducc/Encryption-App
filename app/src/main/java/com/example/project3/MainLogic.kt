import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.project3.decapsulateKey
import com.example.project3.decrypt
import com.example.project3.encapsulateKey
import com.example.project3.encrypt
import com.example.project3.genKeyPair
import com.example.project3.generateHMAC
import com.example.project3.verifyHMAC
import org.bouncycastle.pqc.crypto.frodo.FrodoPrivateKeyParameters
import org.bouncycastle.pqc.crypto.frodo.FrodoPublicKeyParameters
import org.bouncycastle.util.Arrays
import org.bouncycastle.util.encoders.Hex
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import javax.crypto.spec.SecretKeySpec

fun mainLogic(context: Context, userInput: String, selectedParam: String) {

    val filesDir = context.filesDir

    // Receiver
    val keyPair = genKeyPair(selectedParam) // gen Key Pair
    val pubParameters = keyPair.public as FrodoPublicKeyParameters  // share public key

    // Sender
    val secWenc = encapsulateKey(pubParameters) // encapsulate using public key
    val initiatorSecret = secWenc?.secret // the newly random choosen shared_secret
    val generated_cipher_text = secWenc?.encapsulation // share the encapsulation

    // Receiver
    val privParameters = keyPair.private as FrodoPrivateKeyParameters // get the private key
    val recipientSecret = generated_cipher_text?.let { decapsulateKey(privParameters, it) } // decrypt the shared_secret using private key

    Log.v("debug","Initiator Generated Secret: " + Hex.toHexString(initiatorSecret))
    Log.v("debug","Recipient Generated Secret: " + Hex.toHexString(recipientSecret))
    Log.v("debug","Public Key Length: " + pubParameters.publicKey.size)
    Log.v("debug","Generated Secret Length: " + initiatorSecret?.size)
    Log.v("debug","Generated Secret matches: " + Arrays.areEqual(initiatorSecret, recipientSecret))

    val inputFile = File(filesDir, "input.txt")

    // add userInput to file
    inputFile.writeText(userInput)

    val outputFile = File(filesDir, "output_encrypted.txt")
    val decryptedFile = File(filesDir, "decrypted_output.txt")

    val inputStream: InputStream = FileInputStream(inputFile)
    val outputStream: OutputStream = FileOutputStream(outputFile)
    val secretKey = SecretKeySpec(initiatorSecret, "AES")
    encrypt(inputStream, outputStream, secretKey)

    // generate HMAC from encrypted file
    val hmacBytes = generateHMAC(FileInputStream(outputFile), secretKey)

    // Ghi HMAC vào tệp xuất
    val hmacOutputFile = File(filesDir, "hmacOutputFile.txt")
    hmacOutputFile.writeBytes(hmacBytes)

    val encryptedInputStream: InputStream = FileInputStream(outputFile)
    val decryptedOutputStream: OutputStream = FileOutputStream(decryptedFile)
    decrypt(encryptedInputStream, decryptedOutputStream, secretKey)

    val checkMac = verifyHMAC(FileInputStream(outputFile), hmacBytes, secretKey)
    Log.v("debug","checkMac: $checkMac")

    if(checkMac) {
        Toast.makeText(context, "succeed", Toast.LENGTH_SHORT).show()
    } else {
        Toast.makeText(context, "failed", Toast.LENGTH_SHORT).show()
    }

}