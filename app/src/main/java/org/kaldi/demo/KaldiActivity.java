// Copyright 2019 Alpha Cephei Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.kaldi.demo;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.shiza.spaskrovi.R;

import org.kaldi.Assets;
import org.kaldi.Model;
import org.kaldi.RecognitionListener;
import org.kaldi.SpeechRecognizer;
import org.kaldi.Vosk;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Locale;

public class KaldiActivity extends AppCompatActivity implements
        RecognitionListener {

    static {
        System.loadLibrary("kaldi_jni");
    }

    // звонок и сообщение
    private String numberChoose = "tel:";//пишите свой номер
    static private final String numberPolice = "tel:89137573584";//пишите свой номер
    static private final String numberAmbulance = "tel:89137573584";//пишите свой номер
    static private final String numberFireService = "tel:89137573584";//пишите свой номер
    private String messageText = "Проверка работы";
    private String groupBlood = " Группа крови ";
    private String resBlood = " резус ";

    public static final String STORAGE_NAME = "StorageName";
    //private SharedPreferences sharedPrefs;
    //private static SharedPreferences settings = null;
    //private static SharedPreferences.Editor editor = null;
    //private static Context context = null;

    static private final int STATE_START = 0;
    static private final int STATE_READY = 1;
    static private final int STATE_DONE = 2;
    static private final int STATE_FILE = 3;
    static private final int STATE_MIC = 4;

    private static final int NOTIFY_ID = 101;
    private static final String CHANNEL_ID = "CHANNEL_ID";

    private static final String CODE_WORD = "no";

    private static final String TAG1 = "MyApp";


    /* Used to handle permission request */
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    static private final int PERMISSIONS_REQUEST_CALL_PHONE = 1;
    static private final int PERMISSIONS_REQUEST_SEND_SMS = 1;

    private Model model;
    private SpeechRecognizer recognizer;

    int RequestPermissionCode = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    TextToSpeech mTTS;
    String text = "";
    String latitude;
    String longitude;
    LocationRequest locationRequest = new LocationRequest();

    //переменные для получения данных
    private String APP_PREFERENCES_FirstName = "FirstName";
    private String APP_PREFERENCES_SecondName = "SecondName";
    private String APP_PREFERENCES_MiddleName = "MiddleName";
    private String APP_PREFERENCES_BloodNumber = "BloodNumber";
    private String APP_PREFERENCES_BloodRes = "BloodRes";
    private String APP_PREFERENCES_Number = "Number";
    private String APP_PREFERENCES_Message = "Message";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        return true;
    }


    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setup layout
        setUiState(STATE_START);

        findViewById(R.id.recognize_mic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recognizeMicrophone();
            }
        });

        findViewById(R.id.call_ambulance).setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 phoneCall(numberAmbulance, false);
             }
        });

        findViewById(R.id.call_fire).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneCall(numberFireService, false);
            }
        });

        findViewById(R.id.call_police).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneCall(numberPolice, false);
            }
        });

        // Check if user has given permission to record audio, call phone and send sms
        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.SEND_SMS}, PERMISSIONS_REQUEST_SEND_SMS);
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CALL_PHONE}, PERMISSIONS_REQUEST_CALL_PHONE);
        }
        // Recognizer initialization is a time-consuming and it involves IO,
        // so we execute it in async task
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        EnableRuntimePermission();
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    latitude = String.valueOf(location.getLatitude());
                    longitude = String.valueOf(location.getLongitude());
                }
            }
        };
        startLocationUpdates();
        mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = mTTS.setLanguage(Locale.ENGLISH);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    }
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            }
        });
        mTTS.setLanguage(Locale.ENGLISH);
        /*text = "Uh, summa-lumma, dooma-lumma, you assumin' I'm a human\n" +
                "What I gotta do to get it through to you I'm superhuman?\n" +
                "Innovative and I'm made of rubber so that anything you say is ricochetin' off of me and it'll glue to you and\n" +
                "I'm devastating, more than ever demonstrating\n" +
                "How to give a motherfuckin' audience a feeling like it's levitating\n" +
                "Never fading, and I know the haters are forever waiting\n" +
                "For the day that they can say I fell off, they'll be celebrating\n" +
                "'Cause I know the way to get 'em motivated\n" +
                "I make elevating music, you make elevator music"*/
        float pitch = 1f;
        float speed = 1f;
        mTTS.setPitch(pitch);
        mTTS.setSpeechRate(speed);
        longitude = "0";
        latitude = "0";
        new SetupTask(this).execute();
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }
    public void speak() {
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
            return;
        }
        Toast.makeText(this, latitude + " " + longitude, Toast.LENGTH_LONG).show();
        text = "I need your help\n" +
                "I am in danger\n" +
                "My latitude\n" +
                "is\n" +
                latitude + "\n" +
                "and\n" +
                "Longitude\n" +
                "is\n" +
                longitude + "\n";
        new CountDownTimer(30000, 5000) {
            public void onTick(long millisUntilFinished) {
                mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            }
            public void onFinish() {
            }
        }.start();

    }
    public void stop() {
        if (mTTS != null) {
            mTTS.stop();
        }
    }
    private static class SetupTask extends AsyncTask<Void, Void, Exception> {
        WeakReference<KaldiActivity> activityReference;

        SetupTask(KaldiActivity activity) {
            this.activityReference = new WeakReference<>(activity);
        }

        @Override
        protected Exception doInBackground(Void... params) {
            try {
                Assets assets = new Assets(activityReference.get());
                File assetDir = assets.syncAssets();
                Log.d("KaldiDemo", "Sync files in the folder " + assetDir.toString());

                Vosk.SetLogLevel(0);

                activityReference.get().model = new Model(assetDir.toString() + "/model-android");
            } catch (IOException e) {
                return e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Exception result) {
            if (result != null) {
                activityReference.get().setErrorState(String.format(activityReference.get().getString(R.string.failed), result));
            } else {
                activityReference.get().setUiState(STATE_READY);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_RECORD_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Recognizer initialization is a time-consuming and it involves IO,
                // so we execute it in async task
                new SetupTask(this).execute();
            } else {
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (recognizer != null) {
            recognizer.cancel();
            recognizer.shutdown();
        }
    }


    @Override
    public void onResult(String hypothesis) {


        // Сюда добавляем все, что начинает работать после код слова, лучше после таймера.
        if(hypothesis.contains(CODE_WORD)){
            notification();
            new CountDownTimer(10000, 1000) {
                public void onTick(long millisUntilFinished) {
                }
                public void onFinish() {


                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Таймер прошел!", Toast.LENGTH_SHORT);
                    toast.show();
                    phoneCall(numberPolice, true);
                }
            }.start();
        }
        Log.i(TAG1, hypothesis );
    }


    public void notification(){

        //  Уведомления
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(getApplicationContext(), KaldiActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(KaldiActivity.this,
                0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                        .setAutoCancel(true)
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setUsesChronometer(true)
                        .setContentIntent(contentIntent)
                        .setContentTitle("Вы в опасности? Еще можно отменить вызов.")
                        .setTimeoutAfter(10500)
                        .setContentText("До вызова спецслужб 10 секунд.")
                        .setPriority(NotificationCompat.PRIORITY_MAX);
        createChannelIfNeeded(notificationManager);
        notificationManager.notify(NOTIFY_ID, notificationBuilder.build());
    }

    // нужно для первого запуска уведомлений
    public static void createChannelIfNeeded(NotificationManager manager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(notificationChannel);
        }
    }

    @Override
    public void onPartialResult(String hypothesis) {
        Log.i(TAG1, hypothesis );
    }

    @Override
    public void onError(Exception e) {
        setErrorState(e.getMessage());
    }

    @Override
    public void onTimeout() {
        recognizer.cancel();
        recognizer = null;
        setUiState(STATE_READY);
    }

    private void setUiState(int state) {
        switch (state) {
            case STATE_START:
            case STATE_FILE:
                findViewById(R.id.recognize_mic).setEnabled(false);
                break;
            case STATE_READY:
                ((Button) findViewById(R.id.recognize_mic)).setText(R.string.recognize_microphone);
                findViewById(R.id.recognize_mic).setEnabled(true);
                break;
            case STATE_DONE:
                findViewById(R.id.recognize_mic).setEnabled(true);
                break;
            case STATE_MIC:
                ((Button) findViewById(R.id.recognize_mic)).setText(R.string.stop_microphone);
                findViewById(R.id.recognize_mic).setEnabled(true);
                break;
        }
    }

    private void setErrorState(String message) {
        ((Button) findViewById(R.id.recognize_mic)).setText(R.string.recognize_microphone);
        findViewById(R.id.recognize_mic).setEnabled(false);
    }



    public void recognizeMicrophone() {
        if (recognizer != null) {
            setUiState(STATE_DONE);
            recognizer.cancel();
            recognizer = null;
        } else {
            setUiState(STATE_MIC);
            try {
                recognizer = new SpeechRecognizer(model);
                recognizer.addListener(this);
                recognizer.startListening();
            } catch (IOException e) {
                setErrorState(e.getMessage());
            }
        }
    }

    public boolean phoneCall(String number, Boolean flag) {
        try {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.SEND_SMS}, PERMISSIONS_REQUEST_SEND_SMS);
            }

            SharedPreferences mySharedPreferences = getSharedPreferences(STORAGE_NAME, Activity.MODE_PRIVATE);

                numberChoose = "tel:" + mySharedPreferences.getString(APP_PREFERENCES_Number, "0");
                Toast.makeText(this, number, Toast.LENGTH_SHORT).show();
                int bloodgroup = mySharedPreferences.getInt(APP_PREFERENCES_BloodNumber,5);
                int bloodres = mySharedPreferences.getInt(APP_PREFERENCES_BloodRes,3);
                String name = mySharedPreferences.getString(APP_PREFERENCES_FirstName,"имя");
                String surname = mySharedPreferences.getString(APP_PREFERENCES_SecondName,"фамилия");

                String bloodFact = "5";
                if (bloodres == 0)
                    bloodFact = "+";
                if (bloodres == 1)
                    bloodFact = "-";

                String bloodGroupPrint = "4";
                if (bloodgroup == 0)
                    bloodGroupPrint = "I";
                if (bloodgroup == 1)
                    bloodGroupPrint = "II";
                if (bloodgroup == 2)
                    bloodGroupPrint = "III";
                if (bloodgroup == 3)
                    bloodGroupPrint = "IV";
                messageText = mySharedPreferences.getString(APP_PREFERENCES_Message, "_")+" "+ name +" "+ surname+" "+ groupBlood + bloodGroupPrint +bloodFact ;
                Toast.makeText(this, messageText, Toast.LENGTH_SHORT).show();
                SmsManager.getDefault()
                        .sendTextMessage(numberChoose, null, messageText, null, null);// закомментированы смс чтобы не тратить деньги, код рабочий

            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse(number));
            if (ActivityCompat.checkSelfPermission(this,android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CALL_PHONE}, PERMISSIONS_REQUEST_CALL_PHONE);
            }
            startActivity(intent);
            new CountDownTimer(6000, 1000) {
                public void onTick(long millisUntilFinished) {
                }
                public void onFinish() {
                    speak();
                }
            }.start();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public void EnableRuntimePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(this, "ACCESS_FINE_LOCATION permission allows us to Access GPS in app", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, RequestPermissionCode);
        }
    }
}
