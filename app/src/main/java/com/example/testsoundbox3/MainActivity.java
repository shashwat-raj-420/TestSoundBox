package com.example.testsoundbox3;

import androidx.appcompat.app.AppCompatActivity;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.util.Locale;
import android.widget.EditText;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private TextToSpeech textToSpeech;
    private EditText bankName;
    String bankNameString;
    Button resetButton,startButton;
    Boolean initialized=false;

    String sender_name,if_credited;
    final int REQUEST_CODE_ASK_PERMISSIONS = 1001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        if(ContextCompat.checkSelfPermission(getBaseContext(),"Manifest.permission.READ_SMS")
//                == PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(MainActivity.this,
//                    new String[]{"android.permission.READ_SMS"},REQUEST_CODE_ASK_PERMISSIONS);
//        }

        resetButton = (Button) findViewById(R.id.btnReset);
        bankName = (EditText) findViewById(R.id.bankName);
        startButton = (Button) findViewById(R.id.buttonSubmit);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bankNameString = bankName.getText().toString();
                initSpeakOut();
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bankName.setText("");
                bankNameString = bankName.getText().toString();
            }
        });



        bind_L();
    }

    private void initSpeakOut(){
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.US);
                    initialized = true;
                } else {
                    Toast.makeText(MainActivity.this, "Error initializing TextToSpeech.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void speakOut(String text) {
        if (initialized) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

    public void bind_L(){
        SmsReceiver.bindListener(new SmsListener() {
            @Override
            public void messageReceived(String messageText,String nameText) {
                Log.d("data3", "messageReceived: d3");
                sender_name=nameText;
                if_credited=messageText;

                setAmount();
            }
        });

    }

    public void setAmount(){
        if (sender_name.contains(bankNameString) && !bankNameString.isEmpty()){
            Log.d("data12", "messageReceived: 12");
            speakOut(if_credited);
        }
    }
}