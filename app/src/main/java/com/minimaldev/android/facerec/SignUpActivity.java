package com.minimaldev.android.facerec;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
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

public class SignUpActivity extends AppCompatActivity {

    View v;
    EditText et_name,et_registration,et_confirm_reg,et_password,et_confirm_pass;
    Button Onsubmit;

    String SendName,SendRegistraion,SendPass;
    String url="http://attendancecom.000webhostapp.com/connect/sign_up.php";
    String registration;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign_up);

        et_name = (EditText)findViewById(R.id.name);
        et_registration = (EditText)findViewById(R.id.registration_no);
        et_confirm_reg = (EditText)findViewById(R.id.confirm_reg);
        et_password = (EditText)findViewById(R.id.password);
        et_confirm_pass = (EditText)findViewById(R.id.confirm_pass);




        if(savedInstanceState!=null) {
            et_name.setText(savedInstanceState.getString("name"));
            et_registration.setText(savedInstanceState.getString("reg"));
            et_confirm_reg.setText(savedInstanceState.getString("conreg"));
            et_password.setText(savedInstanceState.getString("pass"));
            et_confirm_pass.setText(savedInstanceState.getString("conpass"));
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState)
    {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("name",et_name.getText().toString().trim());
        savedInstanceState.putString("reg",et_registration.getText().toString().trim());
        savedInstanceState.putString("conreg",et_confirm_reg.getText().toString().trim());
        savedInstanceState.putString("pass",et_password.getText().toString().trim());
        savedInstanceState.putString("conpass",et_confirm_pass.getText().toString().trim());

    }


    public void upload(View view) {
        //registration = et_registration.getText().toString().trim();

        if (!isNetworkAvailable()) {
            Snackbar snackbar = Snackbar.make(SignUpActivity.this.findViewById(R.id.signuprel), "No internet connection !", Snackbar.LENGTH_LONG);
            View snack = snackbar.getView();
            TextView t = (TextView) snack.findViewById(android.support.design.R.id.snackbar_text);
            t.setTextColor(Color.parseColor("#ffffff"));
            snackbar.show();
        } else {
            final String name = et_name.getText().toString();
            registration = et_registration.getText().toString().trim();
            String confirm_reg = et_confirm_reg.getText().toString().trim();
            final String password = et_password.getText().toString().trim();
            String confirm_pass = et_confirm_pass.getText().toString().trim();

            if (name.isEmpty() || registration.isEmpty() || confirm_reg.isEmpty() || password.isEmpty() || confirm_pass.isEmpty()) {
                Toast.makeText(SignUpActivity.this, "PLEASE FILL ALL THE FIELDS!!", Toast.LENGTH_SHORT).show();
            } else {
                if (!registration.equals(confirm_reg) && !password.equals(confirm_pass)) {
                    Toast.makeText(SignUpActivity.this, "No Fields Match!!", Toast.LENGTH_SHORT).show();
                } else if (!registration.equals(confirm_reg)) {
                    Toast.makeText(SignUpActivity.this, "Registration Numbers do Not Match!!", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(confirm_pass)) {
                    Toast.makeText(SignUpActivity.this, "Passwords do Not Match!!", Toast.LENGTH_SHORT).show();
                } else {
                    SendName = name;
                    SendRegistraion = registration;
                    SendPass = password;
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Toast.makeText(SignUpActivity.this, response, Toast.LENGTH_SHORT).show();
                            if (response.equals("USER ADDED")) {



                                SharedPreferences name = PreferenceManager.getDefaultSharedPreferences(SignUpActivity.this);
                                final SharedPreferences.Editor e = name.edit();
                                e.putString("username", registration);
                                e.apply();

                                SharedPreferences sharedPreferences1 = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                                SharedPreferences.Editor ed = sharedPreferences1.edit();
                                ed.putInt("count", 0);
                                ed.apply();

                                System.out.println(registration);
                                Intent intent = new Intent(SignUpActivity.this, UploadFaceSignUp.class);
                                intent.putExtra("regno", registration);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);

                                UploadFaceSignUp uploadFaceSignUp = new UploadFaceSignUp();
                                int val = uploadFaceSignUp.f;
                                System.out.println(val);

                                finish();

                            }
                            else if (response.equals("USER EXISTS!!"))
                            {

                                Toast.makeText(SignUpActivity.this, "User already exists. Try again!", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(SignUpActivity.this, LaunchActivity.class);
                                startActivity(intent);
                                finish();

                            }
                            else
                            {
                                Toast.makeText(SignUpActivity.this, "Error uploading image! Try again", Toast.LENGTH_SHORT).show();
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
                            params.put("name", SendName);
                            params.put("registration", SendRegistraion);
                            params.put("password", SendPass);
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
    }

    public boolean isNetworkAvailable()
    {
        ConnectivityManager connectivityManager=(ConnectivityManager) SignUpActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        return networkInfo!=null && networkInfo.isConnected();
    }

}
