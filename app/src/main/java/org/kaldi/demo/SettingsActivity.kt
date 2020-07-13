package org.kaldi.demo

import android.app.Activity
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.shiza.spaskrovi.R
import kotlinx.android.synthetic.main.activity_settings.*
import org.kaldi.demo.PersistantStorage.*


class SettingsActivity : AppCompatActivity() {

    val STORAGE_NAME = "StorageName"

    private val APP_PREFERENCES = "settings_spasscrovi"//название файла
    private val APP_PREFERENCES_FirstName = "FirstName"
    private val APP_PREFERENCES_SecondName = "SecondName"
    private val APP_PREFERENCES_MiddleName = "MiddleName"
    private val APP_PREFERENCES_BloodNumber = "BloodNumber"
    private val APP_PREFERENCES_BloodRes = "BloodRes"
    private val APP_PREFERENCES_Number = "Number"
    private val APP_PREFERENCES_Message = "Message"
    private val APP_PREFERENCES_PhoneNumbers = "PhoneNumbers" //имя для setа номеров телефонов
    private val APP_PREFERENCES_PhoneNumbersCount = "PhoneNumbersCount"//количество номеров телефонов
    var numbers: MutableSet<String> = java.util.HashSet()//set номеров для смс
    var count = 0//количество номеров
    var sharedClass = PersistantStorage.init(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val toolbar: Toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Settings"
        PersistantStorage.init(this)

        printData()
        buttonAddNumber.setOnClickListener{
            addNumber()
        }

        buttonSave.setOnClickListener{
            saveData()
        }

        buttonDeleteNumbers.setOnClickListener{
            deleteNumbers()
        }
    }

    fun deleteNumbers(){
        //var SetNumbers = getPropertySet(APP_PREFERENCES_PhoneNumbers)
        //SetNumbers.elementAt(0)
        //Toast.makeText(this, SetNumbers.elementAt(0), Toast.LENGTH_SHORT).show()
        //Toast.makeText(this, SetNumbers.elementAt(1), Toast.LENGTH_SHORT).show()
        val mySharedPreferences = getSharedPreferences(STORAGE_NAME, Activity.MODE_PRIVATE)
        val editor: Editor = mySharedPreferences.edit()
        editor.remove(APP_PREFERENCES_PhoneNumbers)
        editor.apply()
        //delete(APP_PREFERENCES_PhoneNumbers)
    }

     fun addNumber(){
         count++
         numbers.add(selectedPhoneForCall.getText().toString())
         selectedPhoneForCall.setText("")
         //for (c in 0..count ) {
          //   numbers.add(TextInputLayoutNumber[c].toString())
         //}
        //TextInputLayoutNumber.addView( q,1)

        }

    fun saveData(){
        var name = name.getText().toString() // здесь содержится текст, введенный в текстовом поле
        var surname = surname.getText().toString()
        var middlename = middleName.getText().toString()
        var blood = selectedBloodGroup.selectedItemPosition
        var bloodFactor = selectedBloodFactor.selectedItemPosition
        //var number = selectedPhoneForCall.getText().toString()
        var message = message.getText().toString()

        //for (c in 0..count ) {
        //    numbers.add(TextInputLayoutNumber[c].toString())
        //}
        var currentNumbers: MutableSet<String> = getPropertySet(APP_PREFERENCES_PhoneNumbers)
        currentNumbers.addAll(numbers)
        addPropertySet(APP_PREFERENCES_PhoneNumbers,currentNumbers)
        addProperty(APP_PREFERENCES_FirstName,name)
        addProperty(APP_PREFERENCES_SecondName,surname)
        addProperty(APP_PREFERENCES_MiddleName,middlename)
        addPropertyInt(APP_PREFERENCES_BloodNumber,blood)
        addPropertyInt(APP_PREFERENCES_BloodRes,bloodFactor)
        //addProperty(APP_PREFERENCES_Number,number)
        addProperty(APP_PREFERENCES_Message,message)
        addPropertyInt(APP_PREFERENCES_PhoneNumbersCount,currentNumbers.size)
        Toast.makeText(this, "save", Toast.LENGTH_SHORT).show()
    }

    fun printData(){

        name.setText(getProperty(APP_PREFERENCES_FirstName))
        surname.setText(getProperty(APP_PREFERENCES_SecondName));
        middleName.setText(getProperty(APP_PREFERENCES_MiddleName));
        selectedBloodGroup.setSelection(getPropertyInt(APP_PREFERENCES_BloodNumber))
        selectedBloodFactor.setSelection(getPropertyInt(APP_PREFERENCES_BloodRes))
        //selectedPhoneForCall.setText(getProperty(APP_PREFERENCES_Number))
        message.setText(getProperty(APP_PREFERENCES_Message))
        var currentCount = getPropertyInt(APP_PREFERENCES_PhoneNumbersCount)

        var SetNumbers = getPropertySet(APP_PREFERENCES_PhoneNumbers)
        //Toast.makeText(this, SetNumbers.elementAtOrNull(0), Toast.LENGTH_SHORT).show()

        var StrokeNumbers = ""
        if (SetNumbers.size > 0) {
            for (number in 0..SetNumbers.size-1) {
                //StrokeNumbers = StrokeNumbers + SetNumbers.elementAt(0) + " "
                Toast.makeText(this, SetNumbers.elementAtOrNull(number), Toast.LENGTH_SHORT).show()
            }
        }
        selectedPhoneForCall.setText(StrokeNumbers)
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