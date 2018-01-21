package com.intertapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


public class NFCActivity extends Activity implements NfcAdapter.CreateNdefMessageCallback {

    private EditText amount;
    private final String accessID = "CA1TAaPamkkrCsZQ";
    private final String registration = "CA1AReggCJXVeGF2";
    private final String secretKey = "XyhKlB3ySVGpeNHAWR7XwkASvpiymC/Sr4GQDyZs+YM="; //lul
    private final String salt = "uthacks";
    private String accessToken;
    private Button getStarted;
    private ImageButton backbutton;
    private TextView mTextView, instr, xD, sendm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);
        mTextView = (TextView) findViewById(R.id.textView2);
        amount = (EditText) findViewById(R.id.edit_text_field);
        instr = (TextView) findViewById(R.id.textView);
        xD= (TextView) findViewById(R.id.textView3);
        sendm = (TextView)  findViewById(R.id.editText3);
        //String str = "Send money in an instant while";
        //mTextView.setText((str));
        Typeface face = Typeface.createFromAsset(getAssets(),
                "fonts/futur.ttf");
        mTextView.setTypeface(face);
        amount.setTypeface(face);
        xD.setTypeface(face);
        instr.setTypeface(face);
        sendm.setTypeface(face);
        NfcAdapter mAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mAdapter == null) {
            mTextView.setText("Sorry this device does not have NFC.");
            return;
        }

        if (!mAdapter.isEnabled()) {
            Toast.makeText(this, "Please enable NFC via Settings.", Toast.LENGTH_LONG).show();
        }

        mAdapter.setNdefPushMessageCallback(this, this);
        backbutton = (ImageButton) findViewById(R.id.imageButton2);//get id of button 1
        getStarted = (Button) findViewById(R.id.button3);//get id of button 2

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            startActivity(new Intent(NFCActivity.this, MainActivity.class));
            }
        });
        getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            mTextView.setVisibility(View.INVISIBLE);
            xD.setVisibility(View.INVISIBLE);
            amount.setVisibility(View.VISIBLE);
            instr.setVisibility(View.INVISIBLE);
            getStarted.setVisibility(View.INVISIBLE);
            }
        });
    }

    /**
     * Ndef Record that will be sent over via NFC
     * @param nfcEvent
     * @return
     */
    @Override
    public NdefMessage createNdefMessage(NfcEvent nfcEvent) {

        String request = amount.getText().toString();
        NdefRecord ndefRecord = NdefRecord.createMime("text/plain", request.getBytes());
        NdefMessage ndefMessage = new NdefMessage(ndefRecord);
        return ndefMessage;
    }


}
