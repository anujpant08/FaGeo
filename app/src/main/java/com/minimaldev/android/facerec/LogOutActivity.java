package com.minimaldev.android.facerec;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LogOutActivity extends AppCompatActivity {

    String getDate, getTime;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent=getIntent();
        final String user=intent.getStringExtra("username");
        setContentView(R.layout.activity_log_out);

         getDate =  new SimpleDateFormat("dd/MM/yyyy").format(new Date());

         getTime = new SimpleDateFormat("HH:mm:ss").format(new Date());

        if(!isNetworkAvailable())
        {
            Snackbar snackbar=Snackbar.make(LogOutActivity.this.findViewById(R.id.rellogout), "No network available !", Snackbar.LENGTH_LONG);
            View snack=snackbar.getView();
            TextView t=(TextView)snack.findViewById(android.support.design.R.id.snackbar_text);
            t.setTextColor(Color.parseColor("#ffffff"));

            snackbar.show();
        }

        SharedPreferences sr= PreferenceManager.getDefaultSharedPreferences(LogOutActivity.this);
        String imgurl="";
        imgurl=sr.getString("img_url",null);

        byte[] decode=null;

        decode= Base64.decode(imgurl, Base64.DEFAULT);
        Bitmap b = BitmapFactory.decodeByteArray(decode, 0, decode.length);

        ImageView imageView = (ImageView) findViewById(R.id.profile);
        imageView.setImageBitmap(b);

        SharedPreferences sharedp=PreferenceManager.getDefaultSharedPreferences(this);
        String name=sharedp.getString("name","Name");

        SharedPreferences usern= PreferenceManager.getDefaultSharedPreferences(this);
        username=usern.getString("username",null);


        TextView n=(TextView)findViewById(R.id.user);
        n.setText(name);

        if(!isNetworkAvailable()){
            Snackbar snackbar=Snackbar.make(LogOutActivity.this.findViewById(R.id.rellogin), "No internet connection !", Snackbar.LENGTH_LONG);
            View snack=snackbar.getView();
            TextView t=(TextView)snack.findViewById(android.support.design.R.id.snackbar_text);
            t.setTextColor(Color.parseColor("#ffffff"));
            snackbar.show();
        }
        else {

            Button button = (Button) findViewById(R.id.logoutbutton);
            final AlertDialog.Builder builder = new AlertDialog.Builder(LogOutActivity.this);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            final String getDate =  new SimpleDateFormat("yyyy-MM-dd").format(new Date());

                            final String getTime = new SimpleDateFormat("HH:mm:ss").format(new Date());


                            //String sendRegistration = user;
                            //Toast.makeText(getActivity(),sendRegistration,Toast.LENGTH_SHORT).show();
                            //String sendDate = getDate;
                            //Toast.makeText(getActivity(),sendDate,Toast.LENGTH_SHORT).show();
                            //String sendTime = getTime;
                            //Toast.makeText(getActivity(),sendTime,Toast.LENGTH_SHORT).show();
                            String url="http://attendancecom.000webhostapp.com/connect/logout.php";


                            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    //Toast.makeText(LogOutActivity.this, response, Toast.LENGTH_LONG).show();
                                    System.out.println(response);
                                    if(response.equals("GOOD BYE"))
                                    {
                                        //System.out.println(response);

                                        Toast.makeText(LogOutActivity.this, "Successfuly Checked out !", Toast.LENGTH_LONG).show();

                                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LogOutActivity.this);
                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.putInt("checkedin", 0);
                                        editor.apply();

                                        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(LogOutActivity.this);
                                        SharedPreferences.Editor e = p.edit();
                                        e.putInt("checkedout", 1);
                                        e.apply();

                                        Intent intent = new Intent(LogOutActivity.this, MainActivity.class);
                                        // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.putExtra("username", user);
                                        startActivity(intent);
                                        finish();

                                    }

                                }

                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                    if(error!=null && error.getMessage()!=null)
                                        Toast.makeText(LogOutActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
                                    else
                                        Toast.makeText(LogOutActivity.this,"Hello",Toast.LENGTH_SHORT).show();


                                }
                            }) {
                                @Override
                                protected Map<String, String> getParams() {
                                    Map<String, String> params = new HashMap<String, String>();
                                    params.put("registration", username);
                                    params.put("date", getDate);
                                    params.put("time", getTime);
                                    return params;
                                }

                                @Override
                                public Map<String, String> getHeaders() throws AuthFailureError {
                                    Map<String, String> Headers = new HashMap<String, String>();
                                    Headers.put("User-agent", "MiniProject");
                                    return Headers;
                                }
                            };

                            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                            MySingleton.getInstance(LogOutActivity.this).addToRequestQueue(stringRequest);

                            // Toast.makeText(getActivity(), "Checked In", Toast.LENGTH_SHORT).show();
                        }
                    });

                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    builder.setTitle("Check out");
                    builder.setMessage("Are you sure you want to check out?");
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();


                }
            });
        }

    }

    public boolean isNetworkAvailable()
    {
        ConnectivityManager connectivityManager=(ConnectivityManager) LogOutActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        return networkInfo!=null && networkInfo.isConnected();
    }


    @Override
    public void onBackPressed()
    {
        //do nothing
    }

    public void attend(View view)
    {

        Intent intent=new Intent(LogOutActivity.this, Attendance.class);
        startActivity(intent);


    }


}
