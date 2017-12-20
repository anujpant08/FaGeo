package com.minimaldev.android.facerec;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


public class AttendanceFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    String username;
    View view;

    public AttendanceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AttendanceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AttendanceFragment newInstance(String param1, String param2) {
        AttendanceFragment fragment = new AttendanceFragment();
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

        SharedPreferences sharedp= PreferenceManager.getDefaultSharedPreferences(getActivity());
        username=sharedp.getString("username",null);

        attend();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_attendance, container, false);
        //return inflater.inflate(R.layout.fragment_attendance, container, false);
        return view;
    }

    public void attend()
    {
        Toast.makeText(getActivity(), "Loading Attendance. Please wait...", Toast.LENGTH_LONG).show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://attendancecom.000webhostapp.com/connect/getAttendance.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
                System.out.println(response);



                TextView textView=(TextView)view.findViewById(R.id.textatt);

                if(response.equals("new guest"))
                {

                    textView.setText("N/A");

                }
                else {

                    float att=Float.parseFloat(response);
                    if (att < 75.0) {
                        textView.setTextColor(Color.parseColor("#FFD32F2F"));
                        textView.setText(response + " %");
                    } else if (att == 75.0) {
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
        MySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }


}
