package com.shiza.spaskrovi

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_phone_test.*

class PhoneCallActivityTest : AppCompatActivity() {
    val number = "tel:89059928516"//пишите свой номер
    val REQUEST_CALL_PHONE = 1
    val REQUEST_SEND_SMS = 1
    val messageText = "Test message"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_test)

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.SEND_SMS), REQUEST_SEND_SMS)
        }
        if (ActivityCompat.checkSelfPermission(this,android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CALL_PHONE), REQUEST_CALL_PHONE)
        }

        button_phonecall_test.setOnClickListener(){
            button_phonecall_test(number)
        }
    }

    fun button_phonecall_test(number: String): Boolean {
        try {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.SEND_SMS), REQUEST_SEND_SMS)
            }
            SmsManager.getDefault()
                .sendTextMessage(number, null, messageText.toString(), null, null);
            val intent = Intent(Intent.ACTION_CALL)
            intent.setData(Uri.parse(number))
            if (ActivityCompat.checkSelfPermission(this,android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                val toast = Toast.makeText(applicationContext, " check permission", Toast.LENGTH_SHORT)
                toast.show()
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CALL_PHONE), REQUEST_CALL_PHONE)
            }
            startActivity(intent)
            SmsManager.getDefault()
                .sendTextMessage(number, null, messageText.toString(), null, null);
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
}