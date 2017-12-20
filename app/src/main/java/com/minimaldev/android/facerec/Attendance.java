package com.minimaldev.android.facerec;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ANUJ on 05/10/2017.
 */

public class Attendance extends AppCompatActivity {

    String username;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.attendance);

        SharedPreferences usern= PreferenceManager.getDefaultSharedPreferences(this);
        username=usern.getString("username",null);

        attend();
    }

    public  void  attend()
    {

        Toast.makeText(this, "Loading Attendance. Please wait...", Toast.LENGTH_LONG).show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://attendancecom.000webhostapp.com/connect/getAttendance.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                TextView textView = (TextView) findViewById(R.id.textattnd);

                //Toast.makeText(Attendance.this, response, Toast.LENGTH_LONG).show();
                System.out.println(response);
                if(response.equals("new guest"))
                {
                    textView.setText("N/A");
                }
                else {

                    float att = Float.parseFloat(response);



                    if (att < 75.0) {
                        textView.setTextColor(Color.parseColor("#FFD32F2F"));
                        textView.setText(response + " %");
                    }

                    else if (att == 75.0) {
                        textView.setTextColor(Color.parseColor("#FFFDD835"));
                        textView.setText(response + " %");
                    } else {
                        textView.setTextColor(Color.parseColor("#FF55C90C"));
                        textView.setText(response + " %");
                    }
                }


            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("registration", username);
                params.put("date",new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> Headers = new HashMap<String, String>();
                Headers.put("User-agent", "MiniProject");
                return Headers;
            }
        };
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);


    }

    public void back(View view)
    {

       finish();

    }

}
