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
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import java.util.*

class MainActivity : AppCompatActivity() {
    val RequestPermissionCode = 1
    private lateinit var mTTS: TextToSpeech
    var text = ""
    lateinit var latitude: String
    lateinit var longitude: String
    //lateinit var location: Task<Location>
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    val locationRequest = LocationRequest.create()?.apply {
        interval = 10000
        fastestInterval = 5000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        EnableRuntimePermission()
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations){
                    latitude = location.latitude.toString()
                    longitude = location.longitude.toString()
                }
            }
        }
        fusedLocationClient.requestLocationUpdates(locationRequest,
            locationCallback,
            Looper.getMainLooper())
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
        longitude = "0"
        latitude = "0"
    }
    override fun onDestroy() {
        super.onDestroy()
        mTTS.shutdown()
    }

    fun speak(view: View) {
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
        /*fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                if (location != null) {
                    latitude = location.latitude.toString()
                    longitude = location.longitude.toString()
                }
            }*/
        Toast.makeText(this,"$latitude $longitude", Toast.LENGTH_LONG).show();
        text = "Latitude\n" +
                "is\n" +
                latitude + "\n" +
                "and\n" +
                "Longitude\n" +
                "is\n" +
                longitude + "\n"

        mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null)
    }
    fun stop(view: View) {
        if (mTTS != null) {
            mTTS.stop()
        }
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
