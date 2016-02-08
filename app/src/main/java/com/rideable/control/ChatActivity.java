package com.rideable.control;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.rideable.R;
import com.rideable.resources.Message;
import com.rideable.resources.MessageListAdapter;
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
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private String adId;
    private String userEmail = Types.aUser.getUserEmail();

    private MessageListAdapter adapter;
    private List<Message> listMessages;
    private ListView listViewMessages;
    private Toolbar toolbar;

    private String newMessage;
    private String name;

    private TextView mMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        toolbar = (Toolbar) findViewById(R.id.options_toolbar);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            adId = bundle.getString("adId");
            newMessage = bundle.getString("message");
            name = bundle.getString("name");
        }

        //Log.d("MESSAGE OUTSIDE", newMessage);
        listMessages = new ArrayList<>();
        mMessage = (TextView)findViewById(R.id.inputMsg);
        listViewMessages = (ListView) findViewById(R.id.list_view_messages);
        adapter = new MessageListAdapter(this, listMessages);
        listViewMessages.setAdapter(adapter);

        Message receiveMessage = new Message(name, newMessage, false);
        appendMessage(receiveMessage);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

    }

    private void appendMessage(final Message m) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listMessages.add(m);
                adapter.notifyDataSetChanged();

            }
        });
    }

    public void send(View v){

        String userMessage = mMessage.getText().toString();

        mMessage.setText("");
        Message selfMessage = new Message(Types.aUser.getFirstName(), userMessage, true);
        listMessages.add(selfMessage);
        adapter.notifyDataSetChanged();
        MessageResource messageResource = new MessageResource(MessageResource.POST_TASK);
        messageResource.addNameValuePair("adId", adId);
        messageResource.addNameValuePair("email", userEmail);
        messageResource.addNameValuePair("message", userMessage);

        messageResource.execute(Types.MESSAGE_URL);

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

    private class MessageResource extends AsyncTask<String, Integer, String> {

        public static final int POST_TASK = 1;
        public static final int GET_TASK = 2;

        private static final int CONN_TIMEOUT = 3000;
        private static final int SOCKET_TIMEOUT = 5000;

        private int taskType = 0;

        private ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

        public MessageResource(int taskType){
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
