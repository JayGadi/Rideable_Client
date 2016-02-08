package com.rideable.resources;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.android.gms.maps.MapView;
import com.rideable.R;
import com.rideable.model.Ad;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Jay on 12/1/2015.
 */
public class SimpleAdapter extends ArrayAdapter<Ad> {

    private List<Ad> ads;
    private Context context;
    private MapView map;

    public SimpleAdapter(List<Ad> ads, Context context){
        super(context, android.R.layout.simple_list_item_1, ads);
        this.ads = ads;
        this.context = context;
    }

    public int getCount(){
        if(ads != null)
            return ads.size();
        return 0;
    }

    public Ad getAd(int position){
        if(ads != null){
            return ads.get(position);
        }
        return null;
    }

    public long getItemID(int position){
        if(ads != null)
            return ads.get(position).hashCode();
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View v = convertView;
        if(v == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.ad_list, null);
        }
        DateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
        Calendar myCalendar = Calendar.getInstance();
        Date mDate = null;
        Ad ad = ads.get(position);

        try {
            mDate = format.parse(ad.getDepartureDate());
        }catch(Exception e){
            e.printStackTrace();
        }
        TextView departureCity = (TextView) v.findViewById(R.id.departure_city);
        departureCity.setText(ad.getDepartureCity());
        TextView arrivalCity = (TextView) v.findViewById(R.id.arrival_city);
        arrivalCity.setText(ad.getArrivalCity());
        TextView price = (TextView) v.findViewById(R.id.price);
        price.setText("$"+String.valueOf(ad.getPrice()));
        TextView date = (TextView) v.findViewById(R.id.date);
        String myFormat = "MM/dd/yy, HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        myCalendar.setTime(mDate);
        date.setText(sdf.format(myCalendar.getTime()) + " " + myCalendar.getTime().getHours() + ":" +  myCalendar.getTime().getMinutes());

        return v;
    }

    public List<Ad> getAdList(){
        return ads;
    }

    public void setAdList(List<Ad> ads){
        this.ads = ads;
    }
}