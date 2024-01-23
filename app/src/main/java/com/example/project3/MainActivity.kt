package com.example.project3

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import mainLogic
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {
    lateinit private var editText: EditText
    lateinit private var button: Button
    lateinit private var spinner: Spinner
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editText = findViewById(R.id.editText)
        button = findViewById(R.id.button)
        spinner = findViewById(R.id.spinner)

        // Tạo một ArrayAdapter từ mảng các tham số
        val params = arrayOf("frodokem640aes", "frodokem640shake", "frodokem976aes", "frodokem976shake", "frodokem1344aes", "frodokem1344shake")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, params)

        // Đặt giao diện cho dropdown list
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Đặt ArrayAdapter cho Spinner
        spinner.adapter = adapter

        button.setOnClickListener {
            try {
                val selectedParam = spinner.selectedItem.toString()
                val userInput = editText.text.toString()
                mainLogic(this, userInput, selectedParam)
            } catch(e: IOException) {
            }
        }
    }
}