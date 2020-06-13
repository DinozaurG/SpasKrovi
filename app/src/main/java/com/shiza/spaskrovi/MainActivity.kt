package com.shiza.spaskrovi

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        start()
    }
    fun start(): Boolean {
        try {
            val intent = Intent(this, PhoneCallActivityTest::class.java)
            val toast = Toast.makeText(applicationContext, "start phone activity", Toast.LENGTH_SHORT)
            toast.show()
            startActivity(intent)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
}