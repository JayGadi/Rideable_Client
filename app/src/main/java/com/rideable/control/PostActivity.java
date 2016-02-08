package com.rideable.control;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rideable.R;
import com.rideable.model.Ad;
import com.rideable.resources.SimpleAddressAdapter;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PostActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    private GoogleApiClient mGoogleApiClient;

    private EditText mSearchEditText;
    private Toolbar toolbar;
    private Button departureLocation;
    private Button arrivalLocation;
    private PopupWindow postPopup;
    private TimePicker departureTime;
    private Address location;
    private TextView popupLabel;
    private CalendarView departureDate;
    private GoogleMap mMap;
    private Button setTime;
    private EditText price;
    private EditText passengers;
    private PopupWindow addressWindow;

    private double departureLongitude;
    private double departureLatitude;
    private double arrivalLongitude;
    private double arrivalLatitude;
    private double mPrice;

    private int departureYear;
    private int departureMonth;
    private int departureDay;
    private int departureHour;
    private int departureMin;
    private int noPassengers;

    private String departureCity;
    private String arrivalCity;

    private Date dDate;

    private Ad postAd;

    private SimpleAddressAdapter adapter;

    private static final int ERROR_DIALOG_REQUEST = 1 ;
    private static final String SERVICE_URL = Types.AD_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        toolbar = (Toolbar) findViewById(R.id.options_toolbar);
        departureLocation = (Button) findViewById(R.id.depart);
        arrivalLocation = (Button) findViewById(R.id.arrival);


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

    }

    public void setDepartureLocation(View v){
        departureLatitude = location.getLatitude();
        departureLongitude = location.getLongitude();
        departureCity = location.getLocality();

        Log.d("DEPARTURE CITY", departureCity);
        departureLocation.setVisibility(View.GONE);
        arrivalLocation.setVisibility(View.VISIBLE);
    }

    public void setArrivalLocation(View v){

        arrivalLatitude = location.getLatitude();
        arrivalLongitude = location.getLongitude();
        arrivalCity = location.getLocality();
        Log.d("ARRIVAL CITY", arrivalCity);

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.post_pop_up, (ViewGroup)findViewById(R.id.post_pop_up_menu));

        postPopup = new PopupWindow(layout, 1000, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        postPopup.showAtLocation(layout, Gravity.CENTER, 0,0);

        //final CalendarView departureDate = (CalendarView)layout.findViewById(R.id.departureDate);
        //departureTime = (TimePicker) layout.findViewById(R.id.departureTime);
       // popupLabel = (TextView) layout.findViewById(R.id.pop_label);
       // setTime = (Button) layout.findViewById(R.id.set_time);
        //passengers = (EditText) layout.findViewById(R.id.passengers);
        //price = (EditText) layout.findViewById(R.id.price);

        departureDate.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                departureYear = year;
                departureMonth = month;
                departureDay = dayOfMonth;
                popupLabel.setText("Enter Departure Time");
                departureDate.setVisibility(View.GONE);
                departureTime.setVisibility(View.VISIBLE);
                setTime.setVisibility(View.VISIBLE);
                passengers.setVisibility(View.VISIBLE);
                price.setVisibility(View.VISIBLE);
            }
        });
    }

    public void setTime(View v){
        departureHour = departureTime.getCurrentHour();
        departureMin = departureTime.getCurrentMinute();
        noPassengers = Integer.parseInt(passengers.getText().toString());
        mPrice = Double.parseDouble(price.getText().toString());

        dDate = new Date(departureYear, departureMonth, departureDay, departureHour, departureMin);

        AdResource adResource = new AdResource(AdResource.POST_TASK);
        adResource.addNameValuePair("departureLongitude", String.valueOf(departureLongitude));
        adResource.addNameValuePair("departureLatitude", String.valueOf(departureLatitude));
        adResource.addNameValuePair("arrivalLongitude", String.valueOf(arrivalLongitude));
        adResource.addNameValuePair("arrivalLatitude", String.valueOf(arrivalLatitude));
        adResource.addNameValuePair("passengers", String.valueOf(noPassengers));
        adResource.addNameValuePair("departureDate", dDate.toString());
        adResource.addNameValuePair("departureCity", departureCity);
        adResource.addNameValuePair("arrivalCity", arrivalCity);
        adResource.addNameValuePair("userEmail", Types.aUser.getUserEmail());
        adResource.addNameValuePair("price", String.valueOf(mPrice));

        adResource.execute(new String[]{SERVICE_URL});

        Toast.makeText(this, "Ride Successfully Created", Toast.LENGTH_SHORT).show();

        Intent mainIntent = new Intent(this, PostActivity.class);
        startActivity(mainIntent);

    }
    public void onSearchButtonClicked(View view){
        Geocoder departureAddress = new Geocoder(this);
        String address = mSearchEditText.getText().toString();
        List<Address> addresses;

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.search_addresses_pop_up, (ViewGroup) findViewById(R.id.address_list_pop_up_menu));
        final ListView listView = (ListView)layout.findViewById(R.id.address_list);

        addressWindow= new PopupWindow(layout, 1000, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        addressWindow.showAtLocation(layout, Gravity.CENTER, 0, 0);

        try {
            addresses = departureAddress.getFromLocationName(address, 10);
            adapter = new SimpleAddressAdapter(addresses, this);


            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    location = (Address) listView.getAdapter().getItem(position);
                    addressWindow.dismiss();
                    handleNewLocation(location.getLatitude(), location.getLongitude());
                }
            });
           // location = addresses.get(0);

        }catch(Exception e){
            Log.d("POST ACTIVITY",e.getLocalizedMessage());
        }

    }

    private void gotoLocation(double lat,double lng,float zoom) {
        LatLng latLng=new LatLng(lat,lng);
        CameraUpdate update= CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        mMap.moveCamera(update);
    }

    /*
  Checking the google play services is available
   */
    private boolean checkServices() {
        //returns a integer value
        int isAvailable= GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        //if connection is available
        if (isAvailable== ConnectionResult.SUCCESS){
            return true;
        }else if (GooglePlayServicesUtil.isUserRecoverableError(isAvailable)){
            Dialog dialog= GooglePlayServicesUtil.getErrorDialog(isAvailable, this, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else {
            Toast.makeText(this, "Cannot connnect to mapping Service", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.post_menu, menu);

        View searchRef = menu.findItem(R.id.action_search).getActionView();
        mSearchEditText = (EditText) searchRef.findViewById(R.id.searchText);

        mSearchEditText.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    onSearchButtonClicked(mSearchEditText);
                    return true;
                }
                return false;
            }
        });

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            return true;
        }else if(id == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean initMap() {
        if (mMap == null) {
            SupportMapFragment mapFragment= (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mMap=mapFragment.getMap();
        }
        return (mMap!=null);
    }

    private void handleNewLocation(double latitudeValue, double longitudeValue) {
        //getting the latitude value
        if (checkServices()) {
            if (initMap()) {
                //update the map with the current location
                gotoLocation(latitudeValue, longitudeValue, 15);

                // Other supported types include: MAP_TYPE_NORMAL,
                // MAP_TYPE_TERRAIN, MAP_TYPE_HYBRID and MAP_TYPE_NONE
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                mMap.setMyLocationEnabled(true);

                //Setting up the marker
                MarkerOptions marker = new MarkerOptions()
                        .position(new LatLng(latitudeValue, longitudeValue))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                mMap.addMarker(marker);
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        handleNewLocation(0, 0);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("GettingLocation", "onConnectionFailed");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("GettingLocation", "onConnectionFailed");
    }
    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private class AdResource extends AsyncTask<String, Integer, String> {

        public static final int POST_TASK = 1;
        public static final int GET_TASK = 2;

        private static final int CONN_TIMEOUT = 3000;
        private static final int SOCKET_TIMEOUT = 5000;

        private int taskType = 0;

        private ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

        public AdResource(int taskType){
            this.taskType = taskType;

        }
        public void addNameValuePair(String name, String value){
            params.add(new BasicNameValuePair(name, value));
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //displayProgressBar("Downloading...");
        }

        @Override
        protected String doInBackground(String... urls) {
            String url = urls[0];
            String result = "";

            HttpResponse response = doResponse(url);

            if (response == null) {
                return result;
            } else {

                try {

                    result = inputStreamToString(response.getEntity().getContent());

                } catch (IllegalStateException e) {
                    Log.e("ASYNC TASK", e.getLocalizedMessage(), e);

                } catch (IOException e) {
                    Log.e("ASYNC TASK", e.getLocalizedMessage(), e);
                }

            }

            return result;
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            //userProgressBar.setProgress(values[0]);

        }

        @Override
        protected void onPostExecute(String result) {
            //handleResponse(result);
            //signUp.setVisibility(View.VISIBLE);
            //userProgressBar.setVisibility(View.GONE);

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
                switch (taskType) {

                    case POST_TASK:
                        HttpPost httppost = new HttpPost(url);
                        // Add parameters
                        httppost.setEntity(new UrlEncodedFormEntity(params));
                        response = httpclient.execute(httppost);
                        break;
                    case GET_TASK:
                        HttpGet httpget = new HttpGet(url);
                        response = httpclient.execute(httpget);
                        break;
                }
            } catch (Exception e) {

                Log.e("AD RESOURCE", e.getLocalizedMessage(), e);

            }

            return response;
        }

        private String inputStreamToString(InputStream is) {

            String line = "";
            StringBuilder total = new StringBuilder();

            // Wrap a BufferedReader around the InputStream
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));

            try {
                // Read response until the end
                while ((line = rd.readLine()) != null) {
                    total.append(line);
                }
            } catch (IOException e) {
                Log.e("AD RESOURCE", e.getLocalizedMessage(), e);
            }
            // Return full string
            return total.toString();
        }
    }


}
