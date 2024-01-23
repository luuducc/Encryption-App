package com.example.project3

import android.content.Context
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import javax.crypto.spec.SecretKeySpec

fun playWithRawFiles(context: Context, resourceId: Int):String {
    val buf = StringBuffer()
    val inputStream: InputStream = context.resources.openRawResource(resourceId)

    // new code
    val outputFile = File(context.filesDir, "decrypted_file.txt")
    val outputStream: OutputStream = FileOutputStream(outputFile)
    val keyBytes = "12345678901234567890123456789012".toByteArray()
    val secretKey = SecretKeySpec(keyBytes, "AES")
    encrypt(inputStream, outputStream, secretKey)
    val outputFileInputStream: InputStream = FileInputStream(outputFile)
    val newInputStream: InputStream = context.resources.openRawResource(resourceId) // read again after encrypt
//    val reader = BufferedReader(InputStreamReader(outputFileInputStream))
    //
    
    val reader = BufferedReader(InputStreamReader(newInputStream))
    var str:String? = reader.readLine()
    while(str != null) {
        buf.append("$str\n")
        str = reader.readLine()
    }
    reader.close()
    inputStream.close()
    return buf.toString()
}