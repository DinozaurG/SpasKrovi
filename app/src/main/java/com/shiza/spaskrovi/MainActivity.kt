package com.shiza.spaskrovi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Button
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var mTTS: TextToSpeech
    private var speakButton: Int = R.id.button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mTTS = TextToSpeech(
            this,
            TextToSpeech.OnInitListener() {
                fun onInit(status: Int) {
                    if (status == TextToSpeech.SUCCESS) {
                        var result = mTTS.setLanguage(Locale.GERMAN);
                        if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED
                        ) {
                            Log.e("TTS", "Language not supported");
                        }
                    } else {
                        Log.e("TTS", "Initialization failed");
                    }
                }
            })
    }
    override fun onDestroy() {
        super.onDestroy();
    }

    fun speak(view: View) {
        var text = "Uh, summa-lumma, dooma-lumma, you assumin' I'm a human\n" +
                "What I gotta do to get it through to you I'm superhuman?\n" +
                "Innovative and I'm made of rubber so that anything you say is ricochetin' off of me and it'll glue to you and\n" +
                "I'm devastating, more than ever demonstrating\n" +
                "How to give a motherfuckin' audience a feeling like it's levitating\n" +
                "Never fading, and I know the haters are forever waiting\n" +
                "For the day that they can say I fell off, they'll be celebrating\n" +
                "'Cause I know the way to get 'em motivated\n" +
                "I make elevating music, you make elevator music"
        var pitch = 2f
        var speed = 2f
        mTTS.setPitch(pitch);
        mTTS.setSpeechRate(speed);
        mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }
    fun stop(view: View) {
        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }
    }
}