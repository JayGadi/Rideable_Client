package com.rideable.control;

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

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.EditText;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.rideable.R;
import com.rideable.model.User;
import com.rideable.resources.Types;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {

    private Button signUp;

    private EditText userEmail;
    private EditText userPassword;
    private EditText userConfirmPassword;
    private EditText firstName;
    private EditText lastName;
    private ProgressBar userProgressBar;

    private String fName;
    private String lName;
    private String email;
    private String password;
    private String confirmPassword;

    private String lResponseMessage = "";

    private User newUser;
    private User aUser = null;



    private String SERVICE_URL = Types.USER_URL;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_sign_up, container, false);


        userEmail = (EditText) rootView.findViewById(R.id.login_name);
        userPassword = (EditText) rootView.findViewById(R.id.login_password);
        userConfirmPassword = (EditText) rootView.findViewById(R.id.login_confirm_password);
        firstName = (EditText)rootView.findViewById(R.id.first_name);
        lastName = (EditText)rootView.findViewById(R.id.last_name);
        userProgressBar=(ProgressBar)rootView.findViewById(R.id.userProgressBar);
        userProgressBar.setVisibility(View.GONE);

        signUp = (Button) rootView.findViewById(R.id.sign_up_button);

        signUp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                email = userEmail.getText().toString();

                password = userPassword.getText().toString();
                confirmPassword = userConfirmPassword.getText().toString();

                fName = firstName.getText().toString();
                lName = lastName.getText().toString();

                if (email.equals("") || !email.matches(emailPattern)) {
                    Toast.makeText(getActivity(), "Please Enter a Valid Email", Toast.LENGTH_SHORT).show();
                } else if (password.equals("") || confirmPassword.equals("")) {
                    Toast.makeText(getActivity(), "Please Enter a Password", Toast.LENGTH_SHORT).show();
                } else if (password.length() < 8) {
                    Toast.makeText(getActivity(), "Your Password is less than 8 Characters", Toast.LENGTH_SHORT).show();
                } else if (!password.matches(confirmPassword)) {
                    Toast.makeText(getActivity(), "Your Passwords Do Not Match!", Toast.LENGTH_SHORT).show();
                } else if (fName.equals("")) {
                    Toast.makeText(getActivity(), "Please Enter Your First Name", Toast.LENGTH_SHORT).show();
                } else if (lName.equals("")) {
                    Toast.makeText(getActivity(), "Please Enter Your Last Name", Toast.LENGTH_SHORT).show();
                } else {
                    userProgressBar.setVisibility(View.VISIBLE);
                    signUp.setVisibility(View.GONE);
                    UserResource exists = new UserResource(UserResource.GET_TASK);
                    exists.execute(new String[]{SERVICE_URL + "/" + email});
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable(){
                        public void run(){
                            if(aUser == null || aUser.getUserEmail().equals("")) {

                                UserResource aU = new UserResource(UserResource.POST_TASK);

                                aU.addNameValuePair("firstName", fName);
                                aU.addNameValuePair("lastName", lName);
                                aU.addNameValuePair("userEmail", email.toLowerCase());
                                aU.addNameValuePair("userPassword", password);
                                aU.execute(new String[]{SERVICE_URL});
                                Toast.makeText(getActivity(), "Account Created", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(getActivity(), "User with email " + aUser.getUserEmail() + " already exists", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, 1000);



                }

            }

        });

        return rootView;
    }

    public void handleResponse(String response) {

        try {

            JSONObject jso = new JSONObject(response);

            String firstName = jso.getString("firstName");
            String lastName = jso.getString("lastName");
            String email = jso.getString("userEmail");
            String password = jso.getString("userPassword");

            aUser = new User(firstName, lastName, email, password);

        } catch (Exception e) {
            Log.e("SIGN UP FRAGMENT", e.getLocalizedMessage(), e);
        }

    }
    private class UserResource extends AsyncTask<String, Integer, String>{

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
            userProgressBar.setProgress(values[0]);

        }

        @Override
        protected void onPostExecute(String result) {
            handleResponse(result);
            signUp.setVisibility(View.VISIBLE);
            userProgressBar.setVisibility(View.GONE);

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

    }




}
