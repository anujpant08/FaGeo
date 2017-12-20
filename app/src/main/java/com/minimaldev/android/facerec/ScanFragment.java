package com.minimaldev.android.facerec;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class ScanFragment extends Fragment  {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Boolean f;

    String new_url = "https://api.kairos.com/enroll";
    String recog_url = "https://api.kairos.com/recognize";
    String la,lo;

    String b64url, imageFileName, path;

    float d1[] = new float[1];

    float d2[] = new float[1];

    int inside=0;
    File image;
    String username;
    SharedPreferences.Editor editor;
    boolean check;

    Boolean in;
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters

    private double latitude, longitude;

    private Location mLastLocation, mCurrentLocation;
    private GoogleApiClient mGoogleApiClient;
    private boolean mRequestingLocationUpdates = false;
    private LocationRequest mLocationRequest;

    View v;
    String date,time,registration;
    Button getData,Onsubmit;

    String sendRegistration,sendDate,sendTime;


    ProgressDialog progressBar;
    LocationManager lm;
    //ProgressDialog dialog;
    String user;

    double latitiude_current, longitude_current, lat_srmUB = 12.823414, long_srmUB = 80.042404, lat_srmMain=12.820622, long_srmMain=80.039574;
    View view;
    SharedPreferences sharedPreferences1;

    Button button;
    Bitmap pic, photo;

    TextView textView;
    //private OnFragmentInteractionListener mListener;

    public ScanFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ScanFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ScanFragment newInstance(String param1, String param2) {
        ScanFragment fragment = new ScanFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        //Intent intent = getActivity().getIntent();
        //username = intent.getStringExtra("username");


        SharedPreferences name= PreferenceManager.getDefaultSharedPreferences(getActivity());
        username=name.getString("username",null);

        //Toast.makeText(getActivity(),username,Toast.LENGTH_SHORT).show();

        progressBar=new ProgressDialog(getActivity());
       // progressBar.setTitle("Loading");
        progressBar.setMessage("Please wait...");
        progressBar.setCancelable(false);
        progressBar.setCanceledOnTouchOutside(false);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_scan, container, false);
        //button = (Button) view.findViewById(R.id.checkin);

        if(!isNetworkAvailable()){
            Snackbar snackbar=Snackbar.make(container, "No internet connection !", Snackbar.LENGTH_LONG);
            View snack=snackbar.getView();
            TextView t=(TextView)snack.findViewById(android.support.design.R.id.snackbar_text);
            t.setTextColor(Color.parseColor("#ffffff"));
            snackbar.show();
        }

        SharedPreferences sr= PreferenceManager.getDefaultSharedPreferences(getActivity());
        String imgurl="";
        imgurl=sr.getString("img_url",null);

        byte[] decode=null;

        decode= Base64.decode(imgurl, Base64.DEFAULT);
        Bitmap b = BitmapFactory.decodeByteArray(decode, 0, decode.length);


        ImageView imageView = (ImageView) view.findViewById(R.id.profile);
        imageView.setImageBitmap(b);


        in = ((MainActivity) getActivity()).getLocation();

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab2);

        final Button btn=(Button)view.findViewById(R.id.checkin);
        lm=(LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);

        final FloatingActionButton refreshbtn = (FloatingActionButton) view.findViewById(R.id.refreshbutton);

        Button textView=(Button) view.findViewById(R.id.logoutscan);

        SharedPreferences sharedp=PreferenceManager.getDefaultSharedPreferences(getActivity());
        String name=sharedp.getString("name","Hello");

        TextView n=(TextView)view.findViewById(R.id.username);
        n.setText(name);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(getActivity());
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.putInt("logout",1);
                        editor.apply();

                        SharedPreferences p= PreferenceManager.getDefaultSharedPreferences(getActivity());
                        SharedPreferences.Editor e=p.edit();
                        e.putInt("checkedout",0);
                        e.apply();

                        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(getActivity());
                        SharedPreferences.Editor edtr=preferences.edit();
                        edtr.putInt("checkedin",0);
                        edtr.apply();

                        SharedPreferences na= PreferenceManager.getDefaultSharedPreferences(getActivity());
                        final SharedPreferences.Editor edi=na.edit();
                        edi.putInt("login",0);
                        edi.apply();

                        Toast.makeText(getActivity(),"Successfully logged out",Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(getActivity(), LaunchActivity.class);
                        startActivity(intent);
                        getActivity().finish();

                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                builder.setTitle("Log out");
                builder.setMessage("Are you sure you want to log out?");
                AlertDialog alertDialog=builder.create();
                alertDialog.show();


            }
        });

        lm=(LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);

        final boolean en=lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(!en) {
            //fab.setEnabled(false);
            //btn.setEnabled(false);
            Snackbar snackbar=Snackbar.make(container, "Location services aren't enabled !", Snackbar.LENGTH_LONG);
            View snack=snackbar.getView();
            TextView t=(TextView)snack.findViewById(android.support.design.R.id.snackbar_text);
            t.setTextColor(Color.parseColor("#ffffff"));

            snackbar.show();

        }
        else {



            if (!in) {

                //inside = 1;
                final Snackbar snackbar = Snackbar.make(container, "You're not inside the campus !", Snackbar.LENGTH_LONG);
                View snack = snackbar.getView();
                TextView t = (TextView) snack.findViewById(android.support.design.R.id.snackbar_text);
                t.setTextColor(Color.parseColor("#ffffff"));

                snackbar.setAction("Refresh", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //snackbar.dismiss();
                        ((MainActivity) getActivity()).getLocation();

                        //Toast.makeText(getActivity(),"Refreshing...",Toast.LENGTH_SHORT).show();

                        Fragment fr=getActivity().getSupportFragmentManager().findFragmentById(R.id.linear_replace);
                        if(fr instanceof ScanFragment){
                            FragmentTransaction fragmentTransaction=(getActivity()).getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.detach(fr);
                            fragmentTransaction.attach(fr);
                            fragmentTransaction.commit();
                        }

                    }
                });
                snackbar.setActionTextColor(Color.parseColor("#1f7ae1"));
                snackbar.show();
            } else {
                //inside = 0;

                Snackbar snackbar = Snackbar.make(container, "Welcome to SRM University !", Snackbar.LENGTH_SHORT);
                View snack = snackbar.getView();
                TextView t = (TextView) snack.findViewById(android.support.design.R.id.snackbar_text);
                t.setTextColor(Color.parseColor("#ffffff"));

                snackbar.show();

            }
        }
        if(en && in) {

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    dispatchTakePictureIntent();

                }

            });
        }


        refreshbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Snackbar snackbar=Snackbar.make(container, "Refreshing...", Snackbar.LENGTH_LONG);
                View snack=snackbar.getView();
                TextView t=(TextView)snack.findViewById(android.support.design.R.id.snackbar_text);
                t.setTextColor(Color.parseColor("#ffffff"));
                snackbar.show();

                ((MainActivity) getActivity()).getLocation();

                //Toast.makeText(getActivity(),"Refreshing...",Toast.LENGTH_SHORT).show();

               Fragment fr=getActivity().getSupportFragmentManager().findFragmentById(R.id.linear_replace);
                if(fr instanceof ScanFragment){
                    FragmentTransaction fragmentTransaction=(getActivity()).getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.detach(fr);
                    fragmentTransaction.attach(fr);
                    fragmentTransaction.commit();
                }
            }
        });


        //**** Check-in Button and Camera button in one ****

        btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dispatchTakePictureIntent();

                    if (check) {


                        // Toast.makeText(getActivity(), "Checked In", Toast.LENGTH_SHORT).show();
                    }

                    else {




                    }
                }

            });
        return view;
    }


    @Override
    public void onResume()
    {
        super.onResume();

    }

    public boolean isNetworkAvailable()
    {
        ConnectivityManager connectivityManager=(ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        return networkInfo!=null && networkInfo.isConnected();
    }


    public void dispatchTakePictureIntent() {

        Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent

        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(), "com.example.android.fileprovider", photoFile);

                //takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 1);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            pic = (Bitmap) data.getExtras().get("data");
            photo = pic;


            ImageView im = (ImageView) view.findViewById(R.id.image2);
            im.setImageBitmap(pic);
            im.buildDrawingCache();

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            //Bitmap bitmap = im.getDrawingCache();
            photo.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] imagebytes = byteArrayOutputStream.toByteArray();
            b64url = Base64.encodeToString(imagebytes, Base64.DEFAULT);

            verify();

        } else {

            // Toast.makeText(getActivity(),"No preview",Toast.LENGTH_LONG).show();
        }


    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        imageFileName = "JPEG_" + timeStamp + "_" + username;
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        path = image.getAbsolutePath();
        System.out.println(path);

        return image;
    }

    public void verify() {

        Verifytask verifytask = new Verifytask((MainActivity) getActivity(), recog_url);
        verifytask.execute();

    }

    private class Verifytask extends AsyncTask<String, Void, String> {

        MainActivity mainActivity;
        String url;

        String status;

        public Verifytask(MainActivity Main, String url) {

            this.mainActivity = Main;
            url = this.url;
            // dialog=new ProgressDialog(Main);
        }

        @Override
        protected  void onPreExecute()
        {
            progressBar.show();
        }


        @Override
        protected String doInBackground(String... params) {


            String d = "";
            String response = "";
            try {


                JSONObject object = new JSONObject();
                try {

                    object.put("image", b64url);
                    object.put("gallery_name", username);
                    //object.put("subject_id", username);
                    object.put("threshold", "0.80");
                    d = object.toString();


                } catch (Exception ex) {

                }

                HttpURLConnection con = (HttpURLConnection) (new URL(recog_url)).openConnection();


                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("app_id", "b7aec414");
                con.setRequestProperty("app_key", "487a800bc7f2a2e30f335ce09010599f");
                con.setDoInput(true);
                con.setDoOutput(false);
                con.connect();
                //HttpResponse execute = client.execute(httpGet);

                OutputStream outputStream = con.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                writer.write(d);
                writer.close();
                outputStream.close();


                InputStream content = (InputStream) con.getContent();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                String s;
                while ((s = buffer.readLine()) != null) {
                    response = response + s;
                }
                int code = con.getResponseCode();

                Log.e("Response code: ", String.valueOf(code));
                Log.e("Response: ", response);

                con.disconnect();
            } catch (Exception e) {

                e.printStackTrace();
            }

            if (response != null) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    JSONArray jsonArray = jsonObject.getJSONArray("images");

                    JSONObject j = jsonArray.getJSONObject(0);

                    JSONObject o = j.getJSONObject("transaction");

                    status = o.getString("status");


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }




            return response;
        }

        @Override
        public void onPostExecute(String res) {
           // Toast.makeText(getActivity(), res, Toast.LENGTH_LONG).show();
            progressBar.dismiss();

            try {

               //sharedPreferences1= getActivity().getSharedPreferences("checkprefs", 0);
                if (status.equals("success")) {
                    //Toast.makeText(MainActivity.this, "Successful", Toast.LENGTH_LONG).show();

                    Snackbar snackbar=Snackbar.make(getActivity().findViewById(R.id.linear_main), "Successful", Snackbar.LENGTH_LONG);
                    View snack=snackbar.getView();
                    TextView t=(TextView)snack.findViewById(android.support.design.R.id.snackbar_text);
                    t.setTextColor(Color.parseColor("#ffffff"));

                    snackbar.show();

                    Toast.makeText(getActivity(),"Please wait...checking you in !",Toast.LENGTH_LONG).show();
                    String getDate =  new SimpleDateFormat("yyyy-MM-dd").format(new Date());

                    System.out.println(getDate);
                    String getTime = new SimpleDateFormat("HH:mm:ss").format(new Date());


                    sendRegistration = username;
                    //Toast.makeText(getActivity(),sendRegistration,Toast.LENGTH_SHORT).show();
                    sendDate = getDate;
                    //Toast.makeText(getActivity(),sendDate,Toast.LENGTH_SHORT).show();
                    sendTime = getTime;
                    //Toast.makeText(getActivity(),sendTime,Toast.LENGTH_SHORT).show();
                    String url="http://attendancecom.000webhostapp.com/connect/register.php";


                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
                            if(response.equals("ATTENDANCE MARKED!!"))
                            {
                                //System.out.println(response);


                                SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(getActivity());
                                SharedPreferences.Editor editor=sharedPreferences.edit();
                                editor.putInt("checkedin",1);
                                editor.apply();

                                SharedPreferences na= PreferenceManager.getDefaultSharedPreferences(getActivity());
                                final SharedPreferences.Editor edi=na.edit();
                                edi.putInt("login",1);
                                edi.apply();

                                Intent intent=new Intent(getActivity(),LogOutActivity.class);
                                intent.putExtra("username",username);
                                startActivity(intent);
                                getActivity().finish();

                            }

                        }

                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            if(error!=null && error.getMessage()!=null)
                                Toast.makeText(getActivity(),error.getMessage(),Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(getActivity(),"Hello",Toast.LENGTH_SHORT).show();


                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("registration", username);
                            params.put("date", sendDate);
                            params.put("time", sendTime);
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
                    MySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);


                    //sharedPreferences1.edit().putBoolean("check",true).apply();
                    //ScanFragment.this.check=true;

                } else {
                    Snackbar snackbar=Snackbar.make(getActivity().findViewById(R.id.linear_main), "Unsuccessful", Snackbar.LENGTH_LONG);
                    View snack=snackbar.getView();
                    TextView t=(TextView)snack.findViewById(android.support.design.R.id.snackbar_text);
                    t.setTextColor(Color.parseColor("#ffffff"));

                    snackbar.show();
                   // sharedPreferences1.edit().putBoolean("check",false).apply();
                   // ScanFragment.this.check=false;

                }

               // editor.apply();
            } catch (Exception e) {
                Log.e("Error :", e.getMessage());
                // Toast.makeText(getActivity(), "No faces found !",Toast.LENGTH_LONG).show();
            }
        }
    }



}
