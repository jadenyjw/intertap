package com.intertapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.telephony.TelephonyManager;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.zip.GZIPInputStream;


public class NFCDisplayActivity extends Activity {

    private TextView mTextView;
    private String strippedPhone;
    private final String accessID = "CA1TAaPamkkrCsZQ";
    private final String registration = "CA1AReggCJXVeGF2";
    private final String secretKey = "XyhKlB3ySVGpeNHAWR7XwkASvpiymC/Sr4GQDyZs+YM="; //lul
    private final String salt = "uthacks";
    private String accessToken;
    private RequestQueue mRequestQueue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc_display);
        mTextView = (TextView) findViewById(R.id.text_view);
        TelephonyManager tMgr = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        String mPhoneNumber = tMgr.getLine1Number();
        //mTextView.setText(mPhoneNumber);
        strippedPhone = mPhoneNumber.substring(1);
        System.out.println(strippedPhone);



    }

    @Override
    protected void onResume(){
        super.onResume();
        Intent intent = getIntent();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Parcelable[] rawMessages = intent.getParcelableArrayExtra(
                    NfcAdapter.EXTRA_NDEF_MESSAGES);

            NdefMessage message = (NdefMessage) rawMessages[0]; // only one message transferred

            System.out.println(new String(message.getRecords()[0].getPayload()));
            final String request = new String(message.getRecords()[0].getPayload());
            final double amount = Double.parseDouble(request);

            String url = "https://gateway-web.beta.interac.ca/publicapi/api/v1/access-tokens";

            Network network = new BasicNetwork(new HurlStack());
            Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
            mRequestQueue = new RequestQueue(cache, network);
            mRequestQueue.start();

            JSONObject obj;

            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            System.out.println(response.toString());
                            try {
                                accessToken = (String) response.get("access_token");
                                final String requestID = String.valueOf(UUID.randomUUID()).substring(0,8);
                                System.out.println(accessToken + " is the token");
                                String json = "{ \"sourceMoneyRequestId\": \""+ requestID + "\", \"requestedFrom\": { \"contactName\": \"UofT Hacks\", \"language\": \"en\", \"notificationPreferences\": [ { \"handle\": \""+ strippedPhone + "\", \"handleType\": \"sms\", \"active\": true } ] }, \"amount\": "+amount +", \"currency\": \"CAD\", \"supressResponderNotifications\": false, \"expiryDate\": \"2018-02-30T16:12:12.721Z\", \"editableFulfillAmount\": false }";
                                System.out.println(json);
                                String url = "https://gateway-web.beta.interac.ca/publicapi/api/v2/money-requests/send";
                                try {
                                    JSONObject obj = new JSONObject(json);
                                    JsonObjectRequest jsObjRequest = new JsonObjectRequest
                                            (Request.Method.POST, url, obj, new Response.Listener<JSONObject>() {

                                                @Override
                                                public void onResponse(JSONObject response) {
                                                    System.out.println(response.toString());
                                                    String url;


                                                    try {
                                                        url = (String) response.get("paymentGatewayUrl");
                                                        //Do something with URL
                                                        
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }

                                                }
                                            }, new Response.ErrorListener() {

                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    // TODO Auto-generated method stub
                                                    //System.out.println(error.networkResponse.data);
                                                    System.out.println(error);
                                                    /*
                                                        try {
                                                            final GZIPInputStream gStream = new GZIPInputStream(new ByteArrayInputStream(error.networkResponse.data));
                                                            final InputStreamReader reader = new InputStreamReader(gStream);
                                                            final BufferedReader in = new BufferedReader(reader);
                                                            String read;
                                                            String output = "";
                                                            while ((read = in.readLine()) != null) {
                                                                output += read;
                                                            }
                                                            reader.close();
                                                            in.close();
                                                            gStream.close();
                                                            System.out.println(output);
                                                        } catch (IOException e) {
                                                            e.printStackTrace();
                                                        }
                                                        catch (NullPointerException e){
                                                            e.printStackTrace();
                                                        }
                                                        */
                                                    }

                                            }){

                                        @Override
                                        public Map<String, String> getHeaders() throws AuthFailureError {
                                            Map<String, String>  params = new HashMap<String, String>();
                                            params.put("accept", "application/json");
                                            params.put("Content-Type", "application/json");
                                            params.put("accessToken", "Bearer " + accessToken);
                                            System.out.println("Bearer " + accessToken);
                                            params.put("Accept-Encoding", "gzip,deflate");
                                            params.put("requestId", requestID);
                                            System.out.println(requestID);
                                            params.put("thirdPartyAccessId", accessID);
                                            System.out.println(accessID);
                                            params.put("apiRegistrationId", registration);
                                            System.out.println(registration);
                                            params.put("deviceId", Build.SERIAL);
                                            System.out.println(Build.SERIAL);
                                            System.out.println(strippedPhone);
                                            System.out.println(amount);

                                            return params;
                                        }


                                        @Override
                                        protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                                            String output = ""; // note: better to use StringBuilder
                                            try {
                                                final GZIPInputStream gStream = new GZIPInputStream(new ByteArrayInputStream(response.data));
                                                final InputStreamReader reader = new InputStreamReader(gStream);
                                                final BufferedReader in = new BufferedReader(reader);
                                                String read;
                                                while ((read = in.readLine()) != null) {
                                                    output += read;
                                                }
                                                reader.close();
                                                in.close();
                                                gStream.close();
                                            } catch (IOException e) {
                                                output = response.data.toString();
                                            }
                                            JSONObject payload = null;
                                            try {
                                                payload = new JSONObject(output);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            return Response.success(payload, HttpHeaderParser.parseCacheHeaders(response));
                                        }
                                    };

                                    mRequestQueue.add(jsObjRequest);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO Auto-generated method stub
                            System.out.println(error);

                        }
                    }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String>  params = new HashMap<String, String>();
                    params.put("secretKey", secretKey);
                    params.put("salt", salt);
                    params.put("thirdPartyAccessId", accessID);

                    return params;
                }
            };
            mRequestQueue.add(jsObjRequest);



        } else
            mTextView.setText("Waiting for NDEF Message");

    }
}
