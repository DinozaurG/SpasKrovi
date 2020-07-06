package org.kaldi.demo

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.os.PersistableBundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.shiza.spaskrovi.R
import kotlinx.android.synthetic.main.activity_settings.*
import org.kaldi.demo.PersistantStorage.*


class SettingsActivity : AppCompatActivity() {

    private val APP_PREFERENCES = "settings_spasscrovi"//название файла
    private val APP_PREFERENCES_FirstName = "FirstName"
    private val APP_PREFERENCES_SecondName = "SecondName"
    private val APP_PREFERENCES_MiddleName = "MiddleName"
    private val APP_PREFERENCES_BloodNumber = "BloodNumber"
    private val APP_PREFERENCES_BloodRes = "BloodRes"
    private val APP_PREFERENCES_Number = "Number"
    private val APP_PREFERENCES_Message = "Message"
    var sharedClass = PersistantStorage.init(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val toolbar: Toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Settings"
        PersistantStorage.init(this);
        printData()

        buttonSave.setOnClickListener{
            saveData()
        }
    }

    fun saveData(){
        var name = name.getText().toString() // здесь содержится текст, введенный в текстовом поле
        var surname = surname.getText().toString()
        var middlename = middleName.getText().toString()
        var blood = selectedBloodGroup.selectedItemPosition
        var bloodFactor = selectedBloodFactor.selectedItemPosition
        var number = selectedPhoneForCall.getText().toString()
        var message = message.getText().toString()

        addProperty(APP_PREFERENCES_FirstName,name)
        addProperty(APP_PREFERENCES_SecondName,surname)
        addProperty(APP_PREFERENCES_MiddleName,middlename)
        addPropertyInt(APP_PREFERENCES_BloodNumber,blood)
        addPropertyInt(APP_PREFERENCES_BloodRes,bloodFactor)
        addProperty(APP_PREFERENCES_Number,number)
        addProperty(APP_PREFERENCES_Message,message)

        Toast.makeText(this, "save", Toast.LENGTH_SHORT).show()
    }

    fun printData(){

        name.setText(getProperty(APP_PREFERENCES_FirstName))
        surname.setText(getProperty(APP_PREFERENCES_SecondName));
        middleName.setText(getProperty(APP_PREFERENCES_MiddleName));
        selectedBloodGroup.setSelection(getPropertyInt(APP_PREFERENCES_BloodNumber))
        
        selectedBloodFactor.setSelection(getPropertyInt(APP_PREFERENCES_BloodRes))
        selectedPhoneForCall.setText(getProperty(APP_PREFERENCES_Number))
        message.setText(getProperty(APP_PREFERENCES_Message))
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume(){
        super.onResume()
        printData()
    }

}