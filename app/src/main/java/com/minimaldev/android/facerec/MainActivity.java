package com.minimaldev.android.facerec;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

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

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    float d1[] = new float[1],d2[]=new float[1], d3[]=new float[1];



    String new_url = "https://api.kairos.com/enroll";
    String recog_url = "https://api.kairos.com/recognize";

    String path, imageFileName, b64url, originalb64,la,lo;

    private static String key = "My Pref";

    private static String countstring = "count";

    SharedPreferences sharedPreferences;

    Context context;
    Bitmap b, pic, photo;
    NavigationView navigationView;

    File  file;
    int fl = 0, decide = 0, count = 0, getCounter = 0;

    Button button;

    File image;
    boolean inside;

    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters

    private double latitude, longitude;

    private Location mLastLocation, mCurrentLocation;
    private GoogleApiClient mGoogleApiClient;
    private boolean mRequestingLocationUpdates = false;
    private LocationRequest mLocationRequest;

    boolean flag;
    LocationManager lm;
    //ProgressDialog dialog;


    double latitiude_current, longitude_current, lat_srmUB = 12.823094, long_srmUB = 80.044344, lat_srmMain=12.820622, long_srmMain=80.039574
            ,lathome=26.898131,longhome=80.966903;
    BottomBar bottomBar;

    String myid = "MY_PHOTO_ID";

    String username;

    boolean enabled,enablefrag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        final Intent intent = getIntent();
        username = intent.getStringExtra("username");

        //Intent i=getIntent();

        getLocation();

        enablefrag=lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

        setContentView(R.layout.activity_main);

        //flag=displocation();

        Bundle bundle=new Bundle();
        bundle.putBoolean("inside",flag);

        ScanFragment sc=new ScanFragment();
        sc.setArguments(bundle);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        bottomBar=(BottomBar) findViewById(R.id.botoom_roughlike);

        bottomBar.setDefaultTabPosition(1);

        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {

            @Override
            public void onTabSelected(@IdRes int tabId) {

                switch (tabId)
                {
                    case R.id.upload:

                            UploadFragment u = new UploadFragment();
                            getSupportFragmentManager().beginTransaction().replace(R.id.linear_replace, u).commitAllowingStateLoss();


                        break;

                    case R.id.username:

                        ScanFragment s=new ScanFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.linear_replace,s).commitAllowingStateLoss();

                        break;

                    case R.id.attendance:

                        AttendanceFragment attendanceFragment=new AttendanceFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.linear_replace,attendanceFragment).commitAllowingStateLoss();

                        break;

                }
            }
        });

        /*bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(@IdRes int tabId) {

                switch (tabId)
                {
                    case R.id.upload:

                        break;

                    case R.id.username:

                        getLocation();
                        ScanFragment s=new ScanFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.linear_replace,s).commitAllowingStateLoss();

                        break;

                    case R.id.attendance:


                        break;

                }

            }
        }); */

    }

    public  boolean getInside()
    {
        return flag;
    }

    public  boolean getLocation()
    {

        lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        buildGoogleApiClient();
        createLocationRequest();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        return displocation();

    }


    protected synchronized void buildGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API).build();
        }
    }


    public boolean displocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(this,"No permisssion to access Location Services !",Toast.LENGTH_LONG).show();
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
            la = Double.toString(latitude);
            lo = Double.toString(longitude);

            //Toast.makeText(this,la+" "+lo,Toast.LENGTH_LONG).show();

            lm.removeUpdates(locationListener);

            Location.distanceBetween(latitude,longitude,lat_srmUB,long_srmUB,d1);

            System.out.println(d1[0]);

            Location.distanceBetween(latitude,longitude,lat_srmMain,long_srmMain,d2);

            System.out.println(d2[0]);

            Location.distanceBetween(lathome,longhome,lathome,longhome,d3);

            System.out.println(d3[0]);

           // Toast.makeText(this,Float.toString(d1[0]),Toast.LENGTH_LONG).show();

            //Toast.makeText(this,Float.toString(d2[0]),Toast.LENGTH_LONG).show();

            if(d1[0]>250 && d2[0]>100 ) {

                flag=false;
                //Toast.makeText(this,"You're not inside the campus !",Toast.LENGTH_LONG).show();
            }
            else {
                flag = true;
                //Toast.makeText(this, "Welcome to SRM University !", Toast.LENGTH_LONG).show();
            }


        }
        return flag;

    }


    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            mCurrentLocation = location;

            // Displaying the new location on UI
            la = String.valueOf(mCurrentLocation.getLatitude());
            lo = String.valueOf(mCurrentLocation.getLongitude());
            displocation();
            //swipeRefreshLayout.setRefreshing(false);
            lm.removeUpdates(locationListener);


        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

            MainActivity.this.enabled=true;

        }

        @Override
        public void onProviderDisabled(String provider) {


            MainActivity.this.enabled=false;
           // Toast.makeText(MainActivity.this,"Location services aren't enabled !",Toast.LENGTH_LONG).show();

            //onStop();

        }
    };


    @Override
    public void onStart()
    {
        super.onStart();
        mGoogleApiClient.connect();
        getLocation();
    }

    @Override
    public void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
        getLocation();

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
        lm.removeUpdates(locationListener);
        //swipeRefreshLayout.setEnabled(false);
        //swipeRefreshLayout.setRefreshing(false);


    }

    protected void createLocationRequest() {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }


    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(MainActivity.this,"com.example.android.fileprovider",photoFile );


                //takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 1);

                String g="";
               // String p= photoFile.getAbsolutePath();

                    //g=encodeImage(photoFile);



                //
                // Toast.makeText(this,g,Toast.LENGTH_LONG).show();
            }
        }


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            pic =(Bitmap) data.getExtras().get("data");
            photo = pic;




                ImageView im = (ImageView) findViewById(R.id.image2);
                im.setImageBitmap(pic);
                im.buildDrawingCache();


                /*ImageView im=(ImageView) findViewById(R.id.image2);
                ByteArrayOutputStream byteStream=new ByteArrayOutputStream();
                Bitmap b= BitmapFactory.decodeResource(this.getResources(), R.drawable.compare);
                b.compress(Bitmap.CompressFormat.JPEG,100, byteStream);
                im.setImageBitmap(b);
                im.buildDrawingCache(); */

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                //Bitmap bitmap = im.getDrawingCache();
                photo.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                byte[] imagebytes = byteArrayOutputStream.toByteArray();
                b64url = Base64.encodeToString(imagebytes, Base64.DEFAULT);

                //Toast.makeText(this, b64url, Toast.LENGTH_LONG).show();

                verify();


        }
        else {

           // Toast.makeText(this,"No preview",Toast.LENGTH_LONG).show();
        }


    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        imageFileName = "JPEG_" + timeStamp + "_" + username;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        path = image.getAbsolutePath();

       // String photoPath = Environment.getExternalStorageDirectory()+"/"+imageFileName;

       // Toast.makeText(this,path,Toast.LENGTH_LONG).show();

        //String img=encodeImage(path);
        //Toast.makeText(this,img,Toast.LENGTH_SHORT).show();

        return image;
    }

    public void verify()
    {

        Verifytask verifytask=new Verifytask(this,recog_url);
        verifytask.execute();

    }




    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(MainActivity.this, "Permission granted", Toast.LENGTH_LONG).show();

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(MainActivity.this, "Permission denied to camera", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }






    @Override
    public void onBackPressed() {
        /*DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } */

        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);


                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.putInt("logout",0);
                        editor.apply();

                        SharedPreferences p= PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                        SharedPreferences.Editor e=p.edit();
                        e.putInt("checkedout",1);
                        e.apply();

                        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                        SharedPreferences.Editor edtr=preferences.edit();
                        edtr.putInt("checkedin",0);
                        edtr.apply();

                        SharedPreferences na= PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                        final SharedPreferences.Editor edi=na.edit();
                        edi.putInt("login",0);
                        edi.apply();
                        finish();

                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                builder.setTitle("Exit App");
                builder.setMessage("Are you sure you want to exit app?");
                AlertDialog alertDialog = builder.create();
                alertDialog.show();



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.upload:

                try {

                    SharedPreferences sharedPreferences1 = MainActivity.this.getSharedPreferences(key, MODE_PRIVATE);
                    String value = sharedPreferences1.getString("count", "null");
                    getCounter = Integer.parseInt(value);

                    if (getCounter == 3) {
                        Toast.makeText(MainActivity.this, "You can only take upto 3 images", Toast.LENGTH_LONG).show();

                    } else
                        {
                        Intent intent = new Intent(MainActivity.this, Uploadphoto.class);
                        startActivity(intent);
                    }
                }
                catch (Exception e)
                {
                    Log.e("Error: ",e.getMessage());
                    //Toast.makeText(MainActivity.this, " Upload your image first !",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(MainActivity.this, Uploadphoto.class);
                    startActivity(intent);


                }


                    break;

            case R.id.attendance:

                break;

            case R.id.username:

                break;


        }

        /*DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START); */
        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    class Asynctsk extends AsyncTask<String ,Void,String>{

        MainActivity mainActivity;
        String url;
        public Asynctsk(MainActivity Main, String url) {

            this.mainActivity = Main;
            url=this.url;
           // dialog=new ProgressDialog(Main);
        }


        @Override
        protected String doInBackground(String... params) {


            String d="";


            String response = "";
            try {
                HttpURLConnection con = (HttpURLConnection) (new URL(new_url)).openConnection();



                JSONObject object = new JSONObject();
                try {

                    object.put("image",originalb64 );
                    object.put("subject_id", "myphoto");
                    object.put("gallery_name", "mygallery");
                    d=object.toString();


                } catch (Exception ex) {

                }
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("app_id", "b7aec414");
                con.setRequestProperty("app_key", "487a800bc7f2a2e30f335ce09010599f");
                con.setDoInput(true);
                con.setDoOutput(false);
                con.connect();
                //HttpResponse execute = client.execute(httpGet);

                OutputStream outputStream=con.getOutputStream();
                BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
                writer.write(d);
                writer.close();
                outputStream.close();


                InputStream content = (InputStream) con.getContent();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                String s;
                while ((s = buffer.readLine()) != null) {
                    response = response + s;
                }
                int code=con.getResponseCode();

                Log.e("Response code: ", String.valueOf(code));
                Log.e("Response: ", response);

                con.disconnect();
            } catch (Exception e) {

                e.printStackTrace();
            }

            return response;
        }

        @Override
        public   void onPostExecute(String res)
        {

            try {
                JSONObject j=new JSONObject(res);

                JSONArray jsonArray=j.getJSONArray("Errors");


                Toast.makeText(MainActivity.this,"No face detected. Please choose surroundings with proper lighting and try again", Toast.LENGTH_LONG).show();
                count--;

                sharedPreferences=MainActivity.this.getSharedPreferences(key,MODE_APPEND);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString("count", Integer.toString(count));

                editor.apply();

               // int flag=0;
                dispatchTakePictureIntent();


            } catch (JSONException e) {

                Log.e("",e.getMessage());

            }

            //Toast.makeText(MainActivity.this,res, Toast.LENGTH_LONG).show();
        }
    }



    public void send() {

        Asynctsk asynctsk=new Asynctsk(this,new_url);
        asynctsk.execute();


    }


    private class Verifytask extends AsyncTask<String ,Void,String>{

        MainActivity mainActivity;
        String url;

        String status;
        public Verifytask(MainActivity Main, String url) {

            this.mainActivity = Main;
            url=this.url;
            // dialog=new ProgressDialog(Main);
        }


        @Override
        protected String doInBackground(String... params) {


            String d="";
            String response = "";
            try {


                JSONObject object = new JSONObject();
                try {

                    object.put("image",b64url );

                    object.put("gallery_name", "mygallery");
                    object.put("threshold","0.85");
                    d=object.toString();


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

                OutputStream outputStream=con.getOutputStream();
                BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
                writer.write(d);
                writer.close();
                outputStream.close();


                InputStream content = (InputStream) con.getContent();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                String s;
                while ((s = buffer.readLine()) != null) {
                    response = response + s;
                }
                int code=con.getResponseCode();

                Log.e("Response code: ", String.valueOf(code));
                Log.e("Response: ", response);

                con.disconnect();
            } catch (Exception e) {

                e.printStackTrace();
            }

            if(response!=null)
            {
                try{
                    JSONObject jsonObject=new JSONObject(response);

                    JSONArray jsonArray=jsonObject.getJSONArray("images");

                    JSONObject j=jsonArray.getJSONObject(0);

                    JSONObject o=j.getJSONObject("transaction");

                    status=o.getString("status");


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


            return response;
        }

        @Override
        public   void onPostExecute(String res) {
            Toast.makeText(MainActivity.this, res, Toast.LENGTH_LONG).show();

            try {
                if (status.equals("success")) {
                    //Toast.makeText(MainActivity.this, "Successful", Toast.LENGTH_LONG).show();

                    Snackbar snackbar= Snackbar.make(MainActivity.this.findViewById(R.id.linear_main), "Successful", Snackbar.LENGTH_LONG);
                    View snack=snackbar.getView();
                    TextView t=(TextView)snack.findViewById(android.support.design.R.id.snackbar_text);
                    t.setTextColor(Color.parseColor("#df204a"));

                    snackbar.show();

                    button.setEnabled(true);

                } else {
                    Snackbar snackbar = Snackbar.make(MainActivity.this.findViewById(R.id.linear_main), "Unsuccessful", Snackbar.LENGTH_LONG);
                    View snack = snackbar.getView();
                    TextView t = (TextView) snack.findViewById(android.support.design.R.id.snackbar_text);
                    t.setTextColor(Color.parseColor("#df204a"));

                    snackbar.show();

                    button.setEnabled(true);
                }
            }
            catch (Exception e)
            {
                Log.e("Error :" ,e.getMessage());
                Toast.makeText(MainActivity.this, "No faces found !",Toast.LENGTH_LONG).show();
            }
        }
    }


    public void checkedin(View view)

    {



    }

}
