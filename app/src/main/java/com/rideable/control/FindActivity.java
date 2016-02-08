package com.rideable.control;


import android.app.Dialog;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.rideable.R;
import com.rideable.model.Ad;
import com.rideable.resources.SimpleAdapter;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class FindActivity extends AppCompatActivity {

    private List<Ad> ads;
    private SimpleAdapter adapter;
    private String URL = Types.AD_URL;
    private String dCity;
    private String aCity;
    private PopupWindow viewAd;
    private Geocoder geocoder;
    private List<Address> departureAddresses;
    private List<Address> arrivalAddresses;
    private Date dDate;
    private int adID;
    private Ad ad;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);

        adapter = new SimpleAdapter(ads = new ArrayList<>(), this);
        ListView listView = (ListView) findViewById(R.id.list);
        toolbar = (Toolbar) findViewById(R.id.options_toolbar);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.view_ad_pop_up, (ViewGroup) findViewById(R.id.view_ad_pop_up_menu));

                TextView departureLocation = (TextView) layout.findViewById(R.id.departure_location);
                TextView arrivalLocation = (TextView) layout.findViewById(R.id.arrival_location);
                TextView departureDate = (TextView) layout.findViewById(R.id.departure_date);
                TextView departureTime = (TextView) layout.findViewById(R.id.departure_time);
                TextView price = (TextView)layout.findViewById(R.id.price);
                TextView passengers = (TextView)layout.findViewById(R.id.passengers);



                viewAd = new PopupWindow(layout, 1000, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                viewAd.showAtLocation(layout, Gravity.CENTER, 0,0);
                ad = adapter.getAd(position);
                adID = ad.getId();
                geocoder = new Geocoder(FindActivity.this, Locale.getDefault());
                DateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy",Locale.ENGLISH);
                try {
                    departureAddresses = geocoder.getFromLocation(ad.getDepartueLatitude(), ad.getDepartureLongitude(), 1);
                    arrivalAddresses = geocoder.getFromLocation(ad.getArrivalLatitude(), ad.getArrivalLongitude(), 1);
                    dDate = format.parse(ad.getDepartureDate());
                    Log.d("DEPART DATE", dDate.toString());
                }catch(Exception e){
                    e.printStackTrace();
                }
                departureLocation.setText(departureAddresses.get(0).getAddressLine(0) + ", " + departureAddresses.get(0).getLocality() + ", " + departureAddresses.get(0).getAdminArea());
                arrivalLocation.setText(arrivalAddresses.get(0).getAddressLine(0) + ", " + arrivalAddresses.get(0).getLocality() + ", " + arrivalAddresses.get(0).getAdminArea());
                //departureDate.setText(dDate.getDate());
                //departureTime.setText(dDate.getHours() + ":" + dDate.getMinutes());
                price.setText(String.valueOf(ad.getPrice()));
                passengers.setText(String.valueOf(ad.getPassengers()));


            }
        });
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Bundle bundle = getIntent().getExtras();

        if(bundle != null){
            dCity = bundle.getString("dCity");
            aCity = bundle.getString("aCity");
        }

        (new FindAdResource()).execute(URL + "/" + dCity + "/" + aCity);
    }


    public void confirmRide(View v){
        (new UpdateAdResource()).execute(URL + "/passenger/" + adID + "/" + Types.aUser.getUserEmail());
        viewAd.dismiss();

    }
    public void cancel(View v){
        viewAd.dismiss();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
       if(id == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private class UpdateAdResource extends AsyncTask<String, Integer, String> {

        private static final int CONN_TIMEOUT = 3000;
        private static final int SOCKET_TIMEOUT = 5000;


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
            return result;
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            Toast.makeText(FindActivity.this, "Ride Successfully Booked", Toast.LENGTH_SHORT).show();
            //userProgressBar.setProgress(values[0]);

        }

        @Override
        protected void onPostExecute(String result) {
           super.onPostExecute(result);
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
                response = httpclient.execute(httppost);

            } catch (Exception e) {

                Log.e("AD RESOURCE", e.getLocalizedMessage(), e);

            }

            return response;
        }

    }


    private class FindAdResource extends AsyncTask<String, Integer, List<Ad>> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //displayProgressBar("Downloading...");
        }

        @Override
        protected List<Ad> doInBackground(String... urls) {

            List<Ad> result = new ArrayList<Ad>();

            try {
                URL url = new URL(urls[0]);
                HttpURLConnection response = (HttpURLConnection) url.openConnection();

                response.setRequestMethod("GET");
                response.connect();
                InputStream is = response.getInputStream();

                byte[] b = new byte[1024];
                ByteArrayOutputStream boas = new ByteArrayOutputStream();

                while(is.read(b)!= -1){
                    boas.write(b);
                }

                String JSONResp = new String(boas.toByteArray());
                JSONArray arr = new JSONArray(JSONResp);

                for(int i=0; i < arr.length(); i++){
                    result.add(convertAd(arr.getJSONObject(i)));
                }
                for(Ad ad: result){
                    if(ad.getPassengers() == 0){
                        result.remove(ad);
                    }
                }

                return result;
            }catch(Throwable t){
                t.printStackTrace();
            }


            return null;
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            //userProgressBar.setProgress(values[0]);

        }

        @Override
        protected void onPostExecute(List<Ad> ads) {
            super.onPostExecute(ads);
            adapter.setAdList(ads);
            adapter.notifyDataSetChanged();
        }


        private Ad convertAd(JSONObject obj) throws JSONException {
            double dLong = Double.parseDouble(obj.getString("departureLongitude"));
            double dLat = Double.parseDouble(obj.getString("departureLatitude"));
            double aLong = Double.parseDouble(obj.getString("arrivalLongitude"));
            double aLat = Double.parseDouble(obj.getString("arrivalLatitude"));
            String dCity = obj.getString("departureCity");
            String aCity = obj.getString("arrivalCity");
            double price = Double.parseDouble(obj.getString("price"));
            int passengers = Integer.parseInt(obj.getString("passengers"));
            String date = obj.getString("departureDate");
            Ad ad = new Ad(dLong, dLat, aLong, aLat, dCity, aCity, price, passengers, date);
            ad.setId(Integer.parseInt(obj.getString("id")));
            Log.d("AD ID", String.valueOf(ad.getId()));
            return ad;
        }
    }

}
