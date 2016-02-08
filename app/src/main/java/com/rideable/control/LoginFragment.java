package com.rideable.control;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.rideable.R;
import com.rideable.model.User;
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
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class LoginFragment extends Fragment implements View.OnClickListener {

    private EditText userEmail;
    private EditText userPassword;

    private String email;
    private String password;

    private User aUser = null;

    private Button login;

    private String SERVICE_URL = Types.USER_URL;

    private GoogleCloudMessaging gcm;
    private String regid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate( R.layout.fragment_login, container, false);

        userEmail = (EditText)rootView.findViewById(R.id.login_name);
        userPassword = (EditText)rootView.findViewById(R.id.login_password);
        login = (Button)rootView.findViewById(R.id.login_button);
        login.setOnClickListener(this);

        return rootView;

    }

    @Override
    public void onClick(View v) {

        email = userEmail.getText().toString();
        password = userPassword.getText().toString();


        if(v == login){
            UserResource uR = new UserResource(UserResource.GET_TASK);
            uR.execute(new String[]{SERVICE_URL + "/" + email});

        }
    }

    public void getRegId(){
        new AsyncTask<Void, Void, String>(){

            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getActivity().getApplicationContext());
                    }
                    regid = gcm.register(Types.PROJECT_NUMBER);
                    msg = "Device registered, registration ID=" + regid;
                    Log.i("GCM",  msg);

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();

                }
                return msg;
            }
            @Override
            protected void onPostExecute(String msg) {
                Log.d("REG ID: ", msg);
                if(!regid.equals(aUser.getRegId())){
                    (new UpdateRegID()).execute(SERVICE_URL + "/new_regid/" + aUser.getUserEmail() + "/" + regid);
                }
            }
        }.execute(null, null, null);
    }

    private class UpdateRegID extends AsyncTask<String, Integer, String>{

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
                Log.e("USER  RESOURCE", e.getLocalizedMessage(), e);
            }

            return response;
        }


    }
    private class UserResource extends AsyncTask<String, Integer, String> {

        public static final int POST_TASK = 1;
        public static final int GET_TASK = 2;

        private static final int CONN_TIMEOUT = 3000;
        private static final int SOCKET_TIMEOUT = 5000;

        private int taskType = 0;

        private ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

        public UserResource(int taskType){
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

        }

        @Override
        protected void onPostExecute(String result) {
           aUser = createUser(result);
            if(aUser == null || aUser.getUserEmail().equals("")){
                Toast.makeText(getActivity(), "Account with email " + email + " does not exist", Toast.LENGTH_SHORT).show();
            }else{
                if(aUser.getUserPassword().equals(password)){
                    Toast.makeText(getActivity(), "Account Successfully Logged In", Toast.LENGTH_SHORT).show();
                    getRegId();
                    Intent mainIntent = new Intent(getActivity(), MainActivity.class);
                    Types.aUser = aUser;
                    getActivity().startActivity(mainIntent);
                }
                else{
                    Log.d("INCORRECT PASSWORD", aUser.getUserPassword());
                    Log.d("Entered Password", password);
                    Toast.makeText(getActivity(), "Incorrect Password", Toast.LENGTH_SHORT).show();
                }
            }

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
                        response = httpclient.execute(httppost);
                        break;
                    case GET_TASK:
                        HttpGet httpget = new HttpGet(url);
                        response = httpclient.execute(httpget);
                        break;
                }
            } catch (Exception e) {

                Log.e("USER  RESOURCE", e.getLocalizedMessage(), e);

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
                Log.e("ASYNC TASK", e.getLocalizedMessage(), e);
            }
            // Return full string
            return total.toString();
        }

        private User createUser(String response) {

            User user = null;
            try {

                JSONObject jso = new JSONObject(response);
                String firstName = jso.getString("firstName");
                String lastName = jso.getString("lastName");
                String email = jso.getString("userEmail");
                String password = jso.getString("userPassword");
                String regId = jso.getString("regId");

                user = new User(firstName, lastName, email, password);
                user.setRegId(regId);

            } catch (Exception e) {
                Log.e("SIGN UP FRAGMENT", e.getLocalizedMessage(), e);
            }
            return user;
        }

    }

}
