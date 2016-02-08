package com.rideable.control;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.rideable.R;
import com.rideable.resources.Types;

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
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PostFragment extends Fragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener, OnTimeSetListener {

    private Button departureLocation;
    private Button arrivalLocation;
    private Button postRide;
    private TextView departureDateLabel;
    private TextView departureTimeLabel;
    private TextView noPassengersLabel;
    private TextView priceLabel;
    private TextView departureDate;
    private TextView departureTime;
    private TextView noPassengers;
    private TextView price;
    private CalendarView departureDateView;
    private Calendar myCalendar;

    private double locationLongitude;
    private double locationLatitude;
    private double arrivalLongitude;
    private double arrivalLatitude;
    private double departureLongitude;
    private double departureLatitude;
    private double mPrice;
    private int mPassengers;

    private String departureCity;
    private String arrivalCity;
    private String locationAddress;

    private long mDate;
    PopupWindow datePopup;


    private static final int REQUEST_CODE_GET_DEPARTURE_LOCATION= 1;
    private static final int REQUEST_CODE_GET_ARRIVAL_LOCATION= 2;
    private static final String SERVICE_URL = Types.AD_URL;

    public PostFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_post, container, false);

        departureLocation = (Button) rootView.findViewById(R.id.departure_location);
        arrivalLocation = (Button) rootView.findViewById(R.id.arrival_location);
        postRide = (Button) rootView.findViewById(R.id.post_ride);

        departureDateLabel = (TextView) rootView.findViewById(R.id.departure_date_label);
        departureTimeLabel = (TextView) rootView.findViewById(R.id.departure_time_label);
        noPassengersLabel = (TextView) rootView.findViewById(R.id.passengers_label);
        priceLabel = (TextView) rootView.findViewById(R.id.price_label);

        departureDate = (TextView) rootView.findViewById(R.id.departure_date);
        departureTime = (TextView) rootView.findViewById(R.id.departure_time);
        noPassengers = (TextView) rootView.findViewById(R.id.passengers);
        price = (TextView) rootView.findViewById(R.id.price);

        myCalendar = Calendar.getInstance();

        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        departureDate.setText(sdf.format(myCalendar.getTime()));
        departureTime.setText(Calendar.HOUR + ":" + Calendar.MINUTE);

        departureLocation.setOnClickListener(this);
        arrivalLocation.setOnClickListener(this);
        departureDateLabel.setOnClickListener(this);
        departureDate.setOnClickListener(this);
        departureTimeLabel.setOnClickListener(this);
        departureTime.setOnClickListener(this);
        noPassengersLabel.setOnClickListener(this);
        noPassengers.setOnClickListener(this);
        priceLabel.setOnClickListener(this);
        price.setOnClickListener(this);
        postRide.setOnClickListener(this);

        postRide.setEnabled(false);

        return rootView;
    }


    @Override
    public void onClick(View v) {
        if(v == departureLocation){
            Intent departIntent = new Intent(getActivity(), DeparturePostActivity.class);
            startActivityForResult(departIntent, REQUEST_CODE_GET_DEPARTURE_LOCATION);
           }
        else if(v == arrivalLocation){
            Intent arriveIntent = new Intent(getActivity(), DeparturePostActivity.class);
            startActivityForResult(arriveIntent, REQUEST_CODE_GET_ARRIVAL_LOCATION);
        }
        else if(v == departureDateLabel || v ==  departureDate){
            DatePickerDialog dialog =  new DatePickerDialog(getContext(), this, myCalendar.get(Calendar.YEAR),
                    myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH));
            dialog.show();

        }else if(v == departureTimeLabel || v == departureTime){
            TimePickerDialog dialog = new TimePickerDialog(getContext(), this, myCalendar.get(Calendar.HOUR),
                    myCalendar.get(Calendar.MINUTE), false);
            dialog.show();
        }else if(v == noPassengersLabel || v == noPassengers){
            final NumberPicker picker = new NumberPicker(getActivity());
            picker.setMinValue(1);
            picker.setMaxValue(10);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Passengers")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mPassengers = picker.getValue();
                            noPassengers.setText(String.valueOf(picker.getValue()));
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            builder.setView(picker);
            builder.create().show();
        }else if(v == priceLabel || v == price){
            final NumberPicker picker = new NumberPicker(getActivity());
            picker.setMinValue(1);
            picker.setMaxValue(1000);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Passengers")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mPrice = picker.getValue();
                            price.setText("$" + String.valueOf(picker.getValue()));
                            postRide.setEnabled(true);
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            builder.setView(picker);
            builder.create().show();
        }else if(v == postRide){

            AdResource adResource = new AdResource();
            adResource.addNameValuePair("departureLongitude", String.valueOf(departureLongitude));
            adResource.addNameValuePair("departureLatitude", String.valueOf(departureLatitude));
            adResource.addNameValuePair("arrivalLongitude", String.valueOf(arrivalLongitude));
            adResource.addNameValuePair("arrivalLatitude", String.valueOf(arrivalLatitude));
            adResource.addNameValuePair("passengers", String.valueOf(mPassengers));
            adResource.addNameValuePair("departureDate", String.valueOf(myCalendar.getTime()));
            adResource.addNameValuePair("departureCity", departureCity);
            adResource.addNameValuePair("arrivalCity", arrivalCity);
            adResource.addNameValuePair("userEmail", Types.aUser.getUserEmail());
            adResource.addNameValuePair("price", String.valueOf(mPrice));

            adResource.execute(new String[]{SERVICE_URL});
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (REQUEST_CODE_GET_DEPARTURE_LOCATION == requestCode) {
          if (Activity.RESULT_OK == resultCode) {
              Bundle locationInformation = data.getExtras();
              departureLatitude = locationInformation.getDouble("locationLatitude");
              departureLongitude = locationInformation.getDouble("locationLongitude");
              departureCity = locationInformation.getString("locationCity");
              locationAddress = locationInformation.getString("locationAddress");
              departureLocation.setText(locationAddress);
            }
        }else if(REQUEST_CODE_GET_ARRIVAL_LOCATION == requestCode){
            if (Activity.RESULT_OK == resultCode) {
                Bundle locationInformation = data.getExtras();
                arrivalLatitude = locationInformation.getDouble("locationLatitude");
                arrivalLongitude = locationInformation.getDouble("locationLongitude");
                arrivalCity = locationInformation.getString("locationCity");
                Log.d("Arrival City", arrivalCity);
                locationAddress = locationInformation.getString("locationAddress");
                arrivalLocation.setText(locationAddress);
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

        myCalendar.set(Calendar.YEAR, year);
        myCalendar.set(Calendar.MONTH, monthOfYear);
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        Log.d("Date", String.valueOf(myCalendar.getTime()));
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        departureDate.setText(sdf.format(myCalendar.getTime()));


    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        myCalendar.set(Calendar.HOUR, hourOfDay);
        myCalendar.set(Calendar.MINUTE, minute);
        Log.d("Date", String.valueOf(myCalendar.getTime()));
        departureTime.setText(hourOfDay + ": " + minute);
    }

    private class AdResource extends AsyncTask<String, Integer, String> {

        private static final int CONN_TIMEOUT = 3000;
        private static final int SOCKET_TIMEOUT = 5000;

        private ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

        private ProgressDialog pDlg = null;

        private void showProgressDialog() {

            pDlg = new ProgressDialog(getActivity());
            pDlg.setMessage("Posting Ride");
            pDlg.setProgressDrawable(getActivity().getWallpaper());
            pDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDlg.setCancelable(false);
            pDlg.show();

        }

        public void addNameValuePair(String name, String value){
            params.add(new BasicNameValuePair(name, value));
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected String doInBackground(String... urls) {
            String url = urls[0];
            String result = "";

            HttpResponse response = doResponse(url);
            return result;
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            //userProgressBar.setProgress(values[0]);

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(getActivity(), "Ride Successfully Created", Toast.LENGTH_SHORT).show();
            pDlg.dismiss();

        }
        private HttpParams getHttpParams() {

            HttpParams htpp = new BasicHttpParams();

            HttpConnectionParams.setConnectionTimeout(htpp, CONN_TIMEOUT);
            HttpConnectionParams.setSoTimeout(htpp, SOCKET_TIMEOUT);

            return htpp;
        }

        private HttpResponse doResponse(String url) {
            HttpClient httpclient = new DefaultHttpClient(getHttpParams());

            HttpResponse response = null;

            try {

                HttpPost httppost = new HttpPost(url);
                httppost.setEntity(new UrlEncodedFormEntity(params));
                response = httpclient.execute(httppost);

            } catch (Exception e) {

                Log.e("AD RESOURCE", e.getLocalizedMessage(), e);

            }

            return response;
        }

    }

}
