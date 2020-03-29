package com.example.toomanyfriends;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.os.Bundle;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity  implements View.OnClickListener {
    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + "AAAAAahQTLY:APA91bESwdiRbKjEeeE7_P67Dl8b0N5arJnFGi9dS2vIfAvB2aye4BxBN8h0rfIdeF9um8KkMYggVfZkEdZ9Alw01281Mwh68fgy4Z--LgBpSKdoLmzndHoc1kCd0_i3QtN0heRIeA7k";
    final private String contentType = "application/json";
    final String TAG = "NOTIFICATION TAG";

    String NOTIFICATION_TITLE;
    String NOTIFICATION_MESSAGE;
    String TOPIC;

    @Override
    public void onClick(View v) {
        ImageView image = findViewById(v.getId());
        Boolean somebodyThere = (image.getColorFilter() != null);

        switch (v.getId()) {
            case R.id.bathroom:
                NOTIFICATION_TITLE = "bathroom";
                break;
            case R.id.garage:
                NOTIFICATION_TITLE = "garage";
                break;
            case R.id.bedroom:
                NOTIFICATION_TITLE = "bedroom";
                break;
            case R.id.kitchen:
                NOTIFICATION_TITLE = "kitchen";
                break;
        }

            TOPIC = "/topics/userABC"; //topic must match with what the receiver subscribed to
            if (somebodyThere){
                NOTIFICATION_MESSAGE = "The" +  NOTIFICATION_TITLE + " is now empty, enjoy!";
            }else{
                NOTIFICATION_MESSAGE = "I'm going to the " +  NOTIFICATION_TITLE + ", Watch out!";
            }


            JSONObject notification = new JSONObject();
            JSONObject notifcationBody = new JSONObject();
            try {
                notifcationBody.put("title", NOTIFICATION_TITLE);
                notifcationBody.put("message", NOTIFICATION_MESSAGE);

                notification.put("to", TOPIC);
                notification.put("data", notifcationBody);
            } catch (JSONException e) {
                Log.e(TAG, "onCreate: " + e.getMessage() );
            }
            sendNotification(notification);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView bathroom = findViewById(R.id.bathroom);
        bathroom.setOnClickListener(this);
        ImageView kitchen = findViewById(R.id.kitchen);
        kitchen.setOnClickListener(this);
        ImageView garage = findViewById(R.id.garage);
        garage.setOnClickListener(this);
        ImageView bedroom = findViewById(R.id.bedroom);
        bedroom.setOnClickListener(this);
    }

    private void sendNotification(JSONObject notification) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "onResponse: " + response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Request error", Toast.LENGTH_LONG).show();
                        Log.i(TAG, "onErrorResponse: Didn't work");
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }


    public BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String nameOfResource = intent.getStringExtra("action");
            int resourceId = getResources().getIdentifier(nameOfResource, "id", getPackageName());
            ImageView image = findViewById(resourceId);
            Boolean somebodyThere = (image.getColorFilter() != null);
            if (somebodyThere) {
                image.clearColorFilter();
            } else {
                image.setColorFilter(Color.BLACK);
            }
        }

        };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(myReceiver, new IntentFilter("FBR-IMAGE"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(myReceiver);
    }

}
