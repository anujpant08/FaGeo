package com.minimaldev.android.facerec;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

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
import java.util.Iterator;
import java.util.Map;

import static android.content.Context.MODE_APPEND;
import static android.content.Context.MODE_PRIVATE;


public class UploadFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    final  String key="My_prefs";

    String new_url="https://api.kairos.com/enroll";
    String saveUrl="";

    Boolean f;
    String path,imageFileName,b64url,originalb64;
    Context context;

    View view;
    //private  static String key="My Pref";

    NavigationView navigationView;

    private static String countstring="count";

    SharedPreferences sharedPreferences,sharedPreferences2;

    Bitmap b,pic,photo;
    File image,file;

    int flag=0,decide=0,count=0,getCounter=0;

    int inside;
    ProgressDialog progressBar;

    String myid="MY_PHOTO_ID";

    String username;



    // private OnFragmentInteractionListener mListener;

    public UploadFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
    * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UploadFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UploadFragment newInstance(String param1, String param2) {
        UploadFragment fragment = new UploadFragment();
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

        final Intent intent=getActivity().getIntent();
        final SharedPreferences sharedPreferences2 = getActivity().getSharedPreferences(myid, MODE_PRIVATE);

        SharedPreferences name= PreferenceManager.getDefaultSharedPreferences(getActivity());
        username=name.getString("username",null);

        //Toast.makeText(getActivity(),username,Toast.LENGTH_SHORT).show();


        //saveUrl=sharedPreferences2.getString("saveb64","null");

        progressBar=new ProgressDialog(getActivity());
        // progressBar.setTitle("Loading");
        progressBar.setMessage("Uploading Image...");
        progressBar.setCancelable(false);
        progressBar.setCanceledOnTouchOutside(false);





    }
    public void check()
    {

        SharedPreferences name= PreferenceManager.getDefaultSharedPreferences(getActivity());
        String imgurl="";
        imgurl=name.getString("img_url",null);

        byte[] decode=null;

        decode=Base64.decode(imgurl, Base64.DEFAULT);
        Bitmap b = BitmapFactory.decodeByteArray(decode, 0, decode.length);

        ImageView imageView = (ImageView) view.findViewById(R.id.photoupload);
        imageView.setImageBitmap(b);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view =inflater.inflate(R.layout.fragment_upload,container,false);

        SharedPreferences sharedp=PreferenceManager.getDefaultSharedPreferences(getActivity());
        String name=sharedp.getString("name","Name");

        TextView n=(TextView)view.findViewById(R.id.profilename);
        n.setText(name);


        check();



       /* f=((MainActivity)getActivity()).getInside();


        final FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.floating);



        if(!f) {

            inside=1;
            //fab.setEnabled(false);
           //Toast.makeText(getActivity(),"You are not inside the campus !",Toast.LENGTH_LONG).show();

            Snackbar snackbar=Snackbar.make(getActivity().findViewById(R.id.linear_main), "You aren't inside the campus !", Snackbar.LENGTH_LONG);
            View snack=snackbar.getView();
            TextView t=(TextView)snack.findViewById(android.support.design.R.id.snackbar_text);
            t.setTextColor(Color.parseColor("#1f7ae1"));

            snackbar.show();


            //Snackbar.make(view, "You're not inside the campus ! !", Snackbar.LENGTH_LONG).setAction("Action", null).show();

        }


            else
            {

                inside=0;
                //Snackbar snackbar=Snackbar.make(getActivity().findViewById(R.id.linear_main), "Welcome to SRM University", Snackbar.LENGTH_LONG);
                View snack=snackbar.getView();
                TextView t=(TextView)snack.findViewById(android.support.design.R.id.snackbar_text);
                t.setTextColor(Color.parseColor("#1f7ae1"));

                snackbar.show();

                //Snackbar.make(view, "Welcome to SRM University !", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    try {


                        SharedPreferences sharedPreferences1 = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        getCounter = sharedPreferences1.getInt("count", 0);


                        if (getCounter == 1) {
                            Toast.makeText(getActivity(), "You can upload image only once !", Toast.LENGTH_LONG).show();

                        } else {
                            //flag=0;
                            dispatchTakePictureIntent();

                        }
                    } catch (Exception e) {
                        Log.e("Error: ", e.getMessage());
                        //Toast.makeText(MainActivity.this, " Upload your image first !",Toast.LENGTH_LONG).show();
                        //flag=0;
                        dispatchTakePictureIntent();

                    }

                    // encodeAndSend();

                }
            });
        }*/


        return view;
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
                Uri photoURI = FileProvider.getUriForFile(getActivity(),"com.example.android.fileprovider",photoFile );

                //takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 1);

            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            pic = (Bitmap) data.getExtras().get("data");
            photo = pic;

            ImageView imagev = (ImageView)view.findViewById(R.id.photoupload);
            imagev.setImageBitmap(pic);
            imagev.buildDrawingCache();

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Bitmap bitmap = imagev.getDrawingCache();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] imagebytes = byteArrayOutputStream.toByteArray();
            originalb64 = Base64.encodeToString(imagebytes, Base64.DEFAULT);

            String b64=originalb64.substring(originalb64.indexOf(",")+1);

            sharedPreferences2 = getActivity().getSharedPreferences(myid, MODE_APPEND);
            sharedPreferences2.edit().putString("saveb64",b64).apply();


            //Toast.makeText(getActivity(), originalb64, Toast.LENGTH_LONG).show();
            send();

            count++;

            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("count", count);
            editor.apply();

        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        imageFileName = "JPEG_" + timeStamp + "_" + Math.random();
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        path = image.getAbsolutePath();

        // String photoPath = Environment.getExternalStorageDirectory()+"/"+imageFileName;

        //Toast.makeText(getActivity(),path,Toast.LENGTH_LONG).show();

        return image;
    }

    public void send() {

        Asynctsk asynctsk=new Asynctsk((MainActivity) getActivity(),new_url);
        asynctsk.execute();


    }

    class Asynctsk extends AsyncTask<String ,Void,String> {

        MainActivity mainActivity;
        String url;


        public Asynctsk(MainActivity Main, String url) {

            this.mainActivity = Main;
            url=this.url;
            // dialog=new ProgressDialog(Main);
        }

        @Override
        protected void onPreExecute(){

            progressBar.show();

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
                    object.put("subject_id", username);
                    object.put("gallery_name", username);
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
                Iterator<String> keys=j.keys();

                String str=keys.next();

                String r=j.optString(str);
                if(r.equals("Errors"))
                {

                    Toast.makeText(getActivity(),"No face detected. Please choose surroundings with proper lighting and try again", Toast.LENGTH_LONG).show();
                    count--;

                    sharedPreferences=getActivity().getSharedPreferences(key,MODE_APPEND);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString("count", Integer.toString(count));

                    editor.apply();

                    flag=0;
                    dispatchTakePictureIntent();

                }

                else
                {

                   // Toast.makeText(getActivity(),res, Toast.LENGTH_LONG).show();

                    //**********UPDATE IMG_URL COLUMN IN DATABASE*********

                    String u="http://attendancecom.000webhostapp.com/connect/url.php";
                    StringRequest stringRequest = new StringRequest(Request.Method.POST,u , new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response)
                        {
                            System.out.println(response);
                           // Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
                            if(response.equals("IMAGE UPLOADED"))
                            {

                                progressBar.dismiss();
                                Toast.makeText(getActivity(),"Image Uploaded Successfully!",Toast.LENGTH_SHORT).show();

                                Intent intent=new Intent(getActivity(), MainActivity.class);
                                startActivity(intent);

                            }
                            else
                                Toast.makeText(getActivity(),"Error! Check network connection.",Toast.LENGTH_SHORT).show();
                        }

                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams()
                        {
                            Map<String, String> params = new HashMap<String, String>();
                            System.out.println(username);
                            //System.out.println(originalb64);
                            params.put("img_url", originalb64);
                            params.put("registration",username);
                            return params;
                        }

                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> Headers = new HashMap<String, String>();
                            Headers.put("User-agent", "MiniProject");
                            return Headers;
                        }
                    };
                    MySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);



                    //**************************************



                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


           // getActivity().finish();
        }
    }

}
