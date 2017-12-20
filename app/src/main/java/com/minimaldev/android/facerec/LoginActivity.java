package com.minimaldev.android.facerec;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import static android.os.Build.VERSION_CODES.M;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity  {

  String username;
    EditText et_registration,et_password;
    Button Onsubmit;

    String SendRegistraion,SendPass;
    String url="http://attendancecom.000webhostapp.com/connect/login.php";

    SharedPreferences sharedPreferences,sharedPreferences1;

    android.content.SharedPreferences.Editor editor;

    String [] PER={Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
    , Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CAMERA, Manifest.permission.INTERNET};

    Handler handler=new Handler();
    SharedPreferences loggedin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_login);

        if(!isNetworkAvailable()){
            Snackbar snackbar=Snackbar.make(LoginActivity.this.findViewById(R.id.rellogin), "No internet connection !", Snackbar.LENGTH_LONG);
            View snack=snackbar.getView();
            TextView t=(TextView)snack.findViewById(android.support.design.R.id.snackbar_text);
            t.setTextColor(Color.parseColor("#ffffff"));
            snackbar.show();
        }

        et_registration = (EditText)findViewById(R.id.registration_no);
        et_password = (EditText)findViewById(R.id.password);

        loggedin= PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
        loggedin.edit().putInt("login",0).apply();
        editor=loggedin.edit();
        //file=new File(Environment.getExternalStorageDirectory()+"/android/data/com.minimaldev.android.facerec/files/"+"username.txt");



        // Save a file: path for use with ACTION_VIEW intents


        String c = "";

        sharedPreferences = getSharedPreferences("prefs", 0);

        //loggedin=getSharedPreferences("prefslogin",0);

        sharedPreferences1 = getSharedPreferences("prefs1", 0);


                c = sharedPreferences1.getString("username", "null");
                String name=sharedPreferences1.getString("Name","null");

                //setContentView(R.layout.welcome);
                //TextView textView = (TextView) findViewById(R.id.userid);
                //textView.setText(c);

            /*Intent intent = new Intent(LoginActivity.this, LaunchActivity.class);
            intent.putExtra("username", c);
            startActivity(intent);
            finish(); */

               /* final String finalC = c;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                    }
                }, 1500); */

    }


    public static boolean permission(Context context, String...permissions) {
        if (Build.VERSION.SDK_INT >= M) {

            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    public void logIn(View view) {

        if(!isNetworkAvailable()){
            Snackbar snackbar=Snackbar.make(LoginActivity.this.findViewById(R.id.rellogin), "No internet connection !", Snackbar.LENGTH_LONG);
            View snack=snackbar.getView();
            TextView t=(TextView)snack.findViewById(android.support.design.R.id.snackbar_text);
            t.setTextColor(Color.parseColor("#ffffff"));
            snackbar.show();
        }
        else {

            //boolean flag=false;
            int ca = 0, cs = 0;
            EditText editText = (EditText) findViewById(R.id.registration_no);

            final EditText edit = (EditText) findViewById(R.id.password);

            username = (String.valueOf(editText.getEditableText()));
            final String pass = (String.valueOf(edit.getEditableText()));

            SharedPreferences s = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

            String actualusername = s.getString("username", null);
            String actualpass = s.getString("password", null);


            // if (username.length() ==15 && username.charAt(0)=='R' && username.charAt(1)=='A') {

            String registration = et_registration.getText().toString().trim();
            String password = et_password.getText().toString().trim();

            if (registration.isEmpty() && password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "PLEASE FILL ALL THE FIELDS!!", Toast.LENGTH_SHORT).show();
            } else if (registration.isEmpty()) {
                Toast.makeText(LoginActivity.this, "PLEASE FILL REGISTRATION NUMBER!!", Toast.LENGTH_SHORT).show();
            } else if (password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "PLEASE FILL PASSWORD!!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(LoginActivity.this, "Please wait...", Toast.LENGTH_LONG).show();
                SendRegistraion = registration;
                SendPass = password;
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {
                        System.out.println(response);

                        if(response.contains("NO_USER"))
                        {
                            Toast.makeText(LoginActivity.this,"Error!  User does not exist.",Toast.LENGTH_LONG).show();

                            SharedPreferences n = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                            final SharedPreferences.Editor ed = n.edit();
                            ed.putInt("login", 0);
                            ed.apply();

                            SharedPreferences na = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                            final SharedPreferences.Editor edi = na.edit();
                            edi.putInt("logout", 1);
                            edi.apply();

                            SharedPreferences name = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                            final SharedPreferences.Editor e = name.edit();
                            e.putInt("checkedin", 0);
                            e.apply();

                        }
                        else if(response.contains("INCORRECT"))
                        {

                            Toast.makeText(LoginActivity.this,"Error! Check the details you entered.",Toast.LENGTH_LONG).show();

                                SharedPreferences n = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                                final SharedPreferences.Editor ed = n.edit();
                                ed.putInt("login", 0);
                                ed.apply();

                                SharedPreferences na = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                                final SharedPreferences.Editor edi = na.edit();
                                edi.putInt("logout", 1);
                                edi.apply();

                                SharedPreferences name = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                                final SharedPreferences.Editor e = name.edit();
                                e.putInt("checkedin", 0);
                                e.apply();

                        }
                        else
                            {
                            /*editor.putInt("checkedin",1);
                            editor.apply(); */

                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("name", response);
                            editor.apply();

                            SharedPreferences n = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                            final SharedPreferences.Editor ed = n.edit();
                            ed.putInt("login", 1);
                            ed.apply();

                            SharedPreferences name = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                            final SharedPreferences.Editor e = name.edit();
                            e.putString("username", username);
                            e.apply();

                            SharedPreferences na = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                            final SharedPreferences.Editor edi = na.edit();
                            edi.putInt("logout", 0);
                            edi.apply();

                            SharedPreferences nam = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                            final SharedPreferences.Editor editorr = nam.edit();
                            editorr.putInt("checkedin", 0);
                            editorr.apply();

                            check();
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
                        params.put("password", pass);
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
        }

    }

    public boolean isNetworkAvailable()
    {
        ConnectivityManager connectivityManager=(ConnectivityManager) LoginActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        return networkInfo!=null && networkInfo.isConnected();
    }

    public void check()
    {
        String u="http://attendancecom.000webhostapp.com/connect/imgcheck.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST,u , new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                //Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
                if(!response.equals(""))
                {

                    // Toast.makeText(getActivity(),"verified",Toast.LENGTH_SHORT).show();

                    SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString("img_url",response);
                    editor.apply();

                    System.out.print(response);

                    Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                    intent.putExtra("username",username);
                    startActivity(intent);
                    finish();

                        /*byte[] decode=null;

                        decode=Base64.decode(saveUrl, Base64.DEFAULT);
                        Bitmap b = BitmapFactory.decodeByteArray(decode, 0, decode.length);

                        ImageView imageView = (ImageView) view.findViewById(R.id.photoupload);
                        imageView.setImageBitmap(b); */



                }
                //else
                //  Toast.makeText(getActivity(),"not verified",Toast.LENGTH_SHORT).show();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        })
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("registration", username);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> Headers = new HashMap<String, String>();
                Headers.put("User-agent", "MiniProject");
                return Headers;
            }
        };
        MySingleton.getInstance(LoginActivity.this).addToRequestQueue(stringRequest);
    }

}

