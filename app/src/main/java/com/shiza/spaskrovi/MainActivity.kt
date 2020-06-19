package com.shiza.spaskrovi

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.util.*

class MainActivity : AppCompatActivity() {
    val RequestPermissionCode = 1
    val floatNumForGPS: Float = 7.0F
    lateinit var pendingIntent: PendingIntent
    private lateinit var mTTS: TextToSpeech
    private var speakButton: Int = R.id.button
    var text = ""
    var location: Location? = null
    lateinit var locationManager: LocationManager
    var GpsStatus = false
    var criteria: Criteria? = null
    var Holder: String? = null
    lateinit var context: Context
    lateinit var latitude: String
    lateinit var longitude: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        EnableRuntimePermission()
        var intent1 = createIntent("action 1", "extra 1")
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent1, 0)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        criteria = Criteria()

        Holder = locationManager.getBestProvider(criteria, false)
        context = applicationContext
        checkGpsStatus()
        mTTS = TextToSpeech(
            this,
            TextToSpeech.OnInitListener() {
                fun onInit(status: Int) {
                    if (status == TextToSpeech.SUCCESS) {
                        var result = mTTS.setLanguage(Locale.ENGLISH)
                        if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED
                        ) {
                            Log.e("TTS", "Language not supported")
                        }
                    } else {
                        Log.e("TTS", "Initialization failed")
                    }
                }
            })
        mTTS.language = Locale.ENGLISH
        /*text = "Uh, summa-lumma, dooma-lumma, you assumin' I'm a human\n" +
                "What I gotta do to get it through to you I'm superhuman?\n" +
                "Innovative and I'm made of rubber so that anything you say is ricochetin' off of me and it'll glue to you and\n" +
                "I'm devastating, more than ever demonstrating\n" +
                "How to give a motherfuckin' audience a feeling like it's levitating\n" +
                "Never fading, and I know the haters are forever waiting\n" +
                "For the day that they can say I fell off, they'll be celebrating\n" +
                "'Cause I know the way to get 'em motivated\n" +
                "I make elevating music, you make elevator music"*/
        var pitch = 1f
        var speed = 1f
        mTTS.setPitch(pitch)
        mTTS.setSpeechRate(speed)
        longitude = "Gps not"
        latitude = "Working"
    }
    override fun onDestroy() {
        super.onDestroy()
        mTTS.shutdown()
    }

    fun speak(view: View) {
        checkGpsStatus()
        if(GpsStatus) {
            if (Holder != null) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                location = locationManager.getLastKnownLocation(Holder)
                locationManager.requestLocationUpdates(Holder, 12000, floatNumForGPS, pendingIntent)
                if (location != null) {
                    longitude = location!!.longitude.toString()
                    latitude = location!!.latitude.toString()
                }
                text = "Latitude\n" +
                        "is\n" +
                        latitude + "\n" +
                        "and\n" +
                        "Longitude\n" +
                        "is\n" +
                        longitude + "\n"
                mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null)
            } else {
                mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null)
            }
        } else {
            Toast.makeText(this, "Please Enable GPS First", Toast.LENGTH_LONG).show()
        }
    }
    fun stop(view: View) {
        if (mTTS != null) {
            mTTS.stop()
        }
    }
    fun checkGpsStatus() {
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
    fun createIntent(action: String, extra: String): Intent {
        var intent = Intent(this, MainActivity::class.java)
        intent.setAction(action)
        intent.putExtra("extra", extra)
        return intent
    }
    fun EnableRuntimePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this@MainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            Toast.makeText(
                this@MainActivity,
                "ACCESS_FINE_LOCATION permission allows us to Access GPS in app",
                Toast.LENGTH_LONG
            ).show()
        } else {
            ActivityCompat.requestPermissions(
                this@MainActivity, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION
                ), RequestPermissionCode
            )
        }
    }
}
