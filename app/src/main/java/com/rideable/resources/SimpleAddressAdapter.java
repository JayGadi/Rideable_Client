package com.rideable.resources;

import android.content.Context;
import android.location.Address;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.rideable.R;
import com.rideable.model.Ad;

import java.util.List;

/**
 * Created by Jay on 12/3/2015.
 */
public class SimpleAddressAdapter extends ArrayAdapter<Address> {
    private List<Address> addresses;
    private Context context;

    public SimpleAddressAdapter(List<Address> addresses, Context context){

        super(context, android.R.layout.simple_list_item_1, addresses);
        this.addresses = addresses;
        this.context = context;
    }

    public int getCount(){
        if(addresses != null)
            return addresses.size();
        return 0;
    }

    public Address getAddress(int position){
        if(addresses != null){
            return addresses.get(position);
        }
        return null;
    }

    public long getItemID(int position){
        if(addresses != null)
            return addresses.get(position).hashCode();
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View v = convertView;
        if(v == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.address_list_layout, null);
        }

        Address address = addresses.get(position);
        TextView addressLine = (TextView) v.findViewById(R.id.address_line);
        addressLine.setText(address.getAddressLine(0));
        TextView locality = (TextView) v.findViewById(R.id.locality);
        locality.setText(address.getLocality() +", " + address.getAdminArea() + ", " + address.getPostalCode());

        return v;
    }

    public List<Address> getAddressList(){
        return addresses;
    }

    public void setAddressList(List<Address> addressess){
        this.addresses = addressess;
    }
}
