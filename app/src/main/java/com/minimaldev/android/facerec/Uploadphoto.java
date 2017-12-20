package com.minimaldev.android.facerec;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

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

/**
 * Created by ANUJ on 18/07/2017.
 */

public class Uploadphoto extends AppCompatActivity{

    final  String key="My_prefs";

    String new_url="https://api.kairos.com/enroll";
    String saveUrl;

    String path,imageFileName,b64url,originalb64;
    Context context;

    //private  static String key="My Pref";

    NavigationView navigationView;

    private static String countstring="count";

    SharedPreferences sharedPreferences,sharedPreferences2;

    Bitmap b,pic,photo;
    File image,file;

    int flag=0,decide=0,count=0,getCounter=0;


    String myid="MY_PHOTO_ID";

    String username;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        //setContentView(R.layout.upload);




        //toolbar.setTitle("Upload Photo");


        //toolbar.setElevation(0);


        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_upload);

        //toolbar.setElevation(0);


        //setSupportActionBar(toolbar);


        final SharedPreferences sharedPreferences2 = Uploadphoto.this.getSharedPreferences(myid, MODE_PRIVATE);

        saveUrl=sharedPreferences2.getString("saveb64","null");


        byte[] decode=Base64.decode(saveUrl,Base64.DEFAULT);
        b= BitmapFactory.decodeByteArray(decode,0,decode.length);


        //ImageView imageView = (ImageView) findViewById(R.id.photoupload);
        //imageView.setImageBitmap(b);

        //BottomBar bottomBar=(BottomBar) findViewById(R.id.bottomupload);

        /*bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {


                        switch (tabId)
                        {
                            case R.id.upload:

                                break;

                            case R.id.attendance:


                                break;


                            case R.id.username:

                                Intent intent = new Intent(Uploadphoto.this, MainActivity.class);
                                startActivity(intent);
                                finish();

                                break;

                        }
                    }
                }); */



        /*final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floating);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();



                try {


                    SharedPreferences sharedPreferences1 = Uploadphoto.this.getSharedPreferences(key, MODE_PRIVATE);
                    String value = sharedPreferences1.getString("count", "null");
                    getCounter = Integer.parseInt(value);

                    if (getCounter == 3) {
                        Toast.makeText(Uploadphoto.this, "You can only take upto 3 images", Toast.LENGTH_LONG).show();

                    } else {
                        flag=0;
                        dispatchTakePictureIntent();

                    }
                }
                catch (Exception e)
                {
                    Log.e("Error: ",e.getMessage());
                    //Toast.makeText(MainActivity.this, " Upload your image first !",Toast.LENGTH_LONG).show();
                    flag=0;
                    dispatchTakePictureIntent();

                }

                // encodeAndSend();

            }
        }); */

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
                Uri photoURI = FileProvider.getUriForFile(Uploadphoto.this,"com.example.android.fileprovider",photoFile );


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
            pic = (Bitmap) data.getExtras().get("data");
            photo = pic;

            ImageView imageView = (ImageView) findViewById(R.id.photoupload);
            imageView.setImageBitmap(pic);
            imageView.buildDrawingCache();

                /*ImageView imageView=(ImageView) findViewById(R.id.image);
                Bitmap b= BitmapFactory.decodeResource(this.getResources(), R.drawable.mypic);
                ByteArrayOutputStream byteStream=new ByteArrayOutputStream();
                b.compress(Bitmap.CompressFormat.JPEG,100,byteStream);
                imageView.setImageBitmap(b);
                imageView.buildDrawingCache(); */

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Bitmap bitmap = imageView.getDrawingCache();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] imagebytes = byteArrayOutputStream.toByteArray();
            originalb64 = Base64.encodeToString(imagebytes, Base64.DEFAULT);

            String b64=originalb64.substring(originalb64.indexOf(",")+1);

            sharedPreferences2 = this.getSharedPreferences(myid, MODE_APPEND);
            sharedPreferences2.edit().putString("saveb64",b64).apply();


            Toast.makeText(this, originalb64, Toast.LENGTH_LONG).show();
            send();
            count++;

            sharedPreferences = this.getSharedPreferences(key, MODE_APPEND);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("count", Integer.toString(count));

            editor.apply();

        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        imageFileName = "JPEG_" + timeStamp + "_" + Math.random();
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        path = image.getAbsolutePath();

        // String photoPath = Environment.getExternalStorageDirectory()+"/"+imageFileName;

        Toast.makeText(this,path,Toast.LENGTH_LONG).show();

        //String img=encodeImage(path);
        //Toast.makeText(this,img,Toast.LENGTH_SHORT).show();

        return image;
    }

    public void send() {

        Asynctsk asynctsk=new Asynctsk(this,new_url);
        asynctsk.execute();


    }

    class Asynctsk extends AsyncTask<String ,Void,String> {

        Uploadphoto mainActivity;
        String url;
        public Asynctsk(Uploadphoto Main, String url) {

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
            if(res.equals("{\"Errors\":[{\"Message\":\"no faces found in the image\",\"ErrCode\":5002}]}"))
            {

                Toast.makeText(Uploadphoto.this,"No face detected. Please choose surroundings with proper lighting and try again", Toast.LENGTH_LONG).show();
                count--;

                sharedPreferences=Uploadphoto.this.getSharedPreferences(key,MODE_APPEND);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString("count", Integer.toString(count));

                editor.apply();

                flag=0;
                dispatchTakePictureIntent();


            }

            Toast.makeText(Uploadphoto.this,res, Toast.LENGTH_LONG).show();
            Intent intent=new Intent(Uploadphoto.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void backpressed(View view)

    {
        finish();
    }



    }
