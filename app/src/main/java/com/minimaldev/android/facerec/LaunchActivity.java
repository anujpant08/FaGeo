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
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import static android.os.Build.VERSION_CODES.M;
import static com.minimaldev.android.facerec.R.layout.activity_launch;

public class LaunchActivity extends AppCompatActivity {


    SharedPreferences sharedPreferences,sharedPreferences1;

    String [] PER={Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
            , Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CAMERA, Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_launch);

        sharedPreferences = getSharedPreferences("prefs", 0);

        if (sharedPreferences.getBoolean("first_time", true)) {

            if (!permission(this, PER)) {

                ActivityCompat.requestPermissions(this, PER, 1);


                setContentView(activity_launch);

                if (!isNetworkAvailable()) {
                    Snackbar snackbar = Snackbar.make(LaunchActivity.this.findViewById(R.id.relalaunch), "No network available !", Snackbar.LENGTH_LONG);
                    View snack = snackbar.getView();
                    TextView t = (TextView) snack.findViewById(android.support.design.R.id.snackbar_text);
                    t.setTextColor(Color.parseColor("#ffffff"));

                    snackbar.show();
                } else {

                    sharedPreferences.edit().putBoolean("first_time", false).apply();

                }
            }
        }
        else {


                SharedPreferences s = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                SharedPreferences name = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                SharedPreferences login = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                int logout = sp.getInt("logout", 0);
                int code = s.getInt("checkedin", 0);
                int c = p.getInt("checkedout", 0);
                int l = login.getInt("login", 0);

                String reg = name.getString("username", null);

                if (code == 1) {
                    Intent intent = new Intent(LaunchActivity.this, LogOutActivity.class);
                    //intent.putExtra("username",reg);
                    startActivity(intent);
                    finish();
                } else if (c == 1) {
                    Intent intent = new Intent(LaunchActivity.this, MainActivity.class);
                    intent.putExtra("username", reg);
                    startActivity(intent);
                    finish();
                } else if (l == 1) {
                    Intent intent = new Intent(LaunchActivity.this, MainActivity.class);
                    intent.putExtra("username", reg);
                    startActivity(intent);
                    finish();
                } else if (logout == 1) {
                    setContentView(activity_launch);
                } else {
                    setContentView(activity_launch);
                }

            }
        }


    public boolean isNetworkAvailable()
    {
        ConnectivityManager connectivityManager=(ConnectivityManager) LaunchActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        return networkInfo!=null && networkInfo.isConnected();
    }


    public void signup(View view)
    {

        Intent intent=new Intent(LaunchActivity.this,SignUpActivity.class);
        startActivity(intent);
        finish();
    }

    public void login(View view)
    {
        Intent intent=new Intent(LaunchActivity.this,LoginActivity.class);
        startActivityForResult(intent,1);
        finish();
    }

    @Override
    protected  void onActivityResult(int reqcode,int resultcode,Intent data)
    {

        super.onActivityResult(reqcode,resultcode,data);
        Intent intent=getIntent();
        int code=intent.getIntExtra("login",0);

        SharedPreferences loggedin= PreferenceManager.getDefaultSharedPreferences(LaunchActivity.this);
        final SharedPreferences.Editor editor=loggedin.edit();

        editor.putInt("login",code).apply();

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
}
