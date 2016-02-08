package com.rideable.control;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.rideable.R;
import com.rideable.model.Ad;
import com.rideable.resources.SimpleAdapter;
import com.rideable.resources.Types;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class ChatFragment extends Fragment {

    private ListView chatListView;
    private SimpleAdapter adapter;
    private String AD_URL = Types.AD_URL;
    private List<Ad> ads;



    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView =  (ViewGroup)inflater.inflate(R.layout.fragment_chat, container, false);

        (new FindAdResource()).execute(AD_URL + "/passenger/get/" + Types.aUser.getUserEmail());

        adapter = new SimpleAdapter(ads = new ArrayList<Ad>(), rootView.getContext());
        chatListView = (ListView) rootView.findViewById(R.id.chat_list);
        chatListView.setAdapter(adapter);

        chatListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent chatIntent = new Intent(getActivity(), ChatActivity.class);
                chatIntent.putExtra("adId", String.valueOf(adapter.getAd(position).getId()));
                Log.d("AD ID CHAT FRAGMENT", String.valueOf(adapter.getAd(position).getId()) );
                getActivity().startActivity(chatIntent);
            }
        });


        return rootView;
    }

    private class FindAdResource extends AsyncTask<String, Integer, List<Ad>> {

        private ProgressDialog pDlg = null;

        private void showProgressDialog() {

            pDlg = new ProgressDialog(getActivity());
            pDlg.setMessage("Retrieving Chats");
            pDlg.setProgressDrawable(getActivity().getWallpaper());
            pDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDlg.setCancelable(false);
            pDlg.show();

        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
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
            pDlg.dismiss();
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
            //Log.d("AD ID", String.valueOf(ad.getId()));
            return ad;
        }
    }

}
