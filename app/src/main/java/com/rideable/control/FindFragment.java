package com.rideable.control;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.rideable.R;
import com.rideable.model.Ad;
import com.rideable.model.User;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class FindFragment extends Fragment implements View.OnClickListener {

    private TextView titleLabel;
    private TextView departureCityLabel;
    private TextView arrivalCityLabel;

    private EditText departureCity;
    private EditText arrivalCity;

    private Button search;




    public FindFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_find, container, false);

        //departureCityLabel = (TextView) rootView.findViewById(R.id.d_city_label);
        //arrivalCityLabel = (TextView) rootView.findViewById(R.id.a_city_label);

        departureCity = (EditText) rootView.findViewById(R.id.departure_location);
        arrivalCity = (EditText) rootView.findViewById(R.id.arrival_location);


        search = (Button) rootView.findViewById(R.id.find_ride);
        search.setOnClickListener(this);

        return rootView;
    }


    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.find_ride){
            String dCity = departureCity.getText().toString();
            String aCity = arrivalCity.getText().toString();

            Intent i = new Intent(getActivity(), FindActivity.class);
            i.putExtra("dCity", dCity);
            i.putExtra("aCity", aCity);
            getActivity().startActivity(i);
        }
    }
}
