package edu.sjsu.android.starbucks_ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Transaction extends AppCompatActivity {

    private Button btn_getdetails;
    private TextView txt_output;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        Bundle bundle = getIntent().getExtras();
        String username = bundle.getString("Username");

        txt_output = (TextView)findViewById(R.id.txt_output);
        txt_output.setMovementMethod(new ScrollingMovementMethod());

        btn_getdetails = (Button) findViewById(R.id.generate);
        btn_getdetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Bundle bundle = getIntent().getExtras();
                String username = bundle.getString("Username");
                final String serviceURL = "http://starbucks-elb-1199172796.us-west-2.elb.amazonaws.com:8080/transactions?username=" + username;

                Transaction.HttpGetRequest runner = new Transaction.HttpGetRequest();

                runner.execute(serviceURL);
            }
        });
    }

    public class HttpGetRequest extends AsyncTask<String, Void, String> {
        public static final String REQUEST_METHOD = "GET";
        public static final int READ_TIMEOUT = 15000;
        public static final int CONNECTION_TIMEOUT = 15000;

        @Override
        protected String doInBackground(String... params) {
            String stringUrl = params[0];

            try {
                try {
                    return HttpGET(params[0]);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return "Error!";
                }
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        private String HttpGET(String Url) throws IOException, JSONException {
            String apiResponse;
            String inputLine;

            //Create a URL object holding our url
            URL myUrl = new URL(Url);
            Log.i(MainActivity.class.toString(), Url + " -------------------------  ");
            //Create a connection
            HttpURLConnection connection = (HttpURLConnection)
                    myUrl.openConnection();

            //Set methods and timeouts
            connection.setRequestMethod(REQUEST_METHOD);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);

            //Connect to our url
            connection.connect();

            //Create a new InputStreamReader
            InputStreamReader streamReader = new
                    InputStreamReader(connection.getInputStream());

            //Create a new buffered reader and String Builder
            BufferedReader reader = new BufferedReader(streamReader);
            StringBuilder stringBuilder = new StringBuilder();

            //Check if the line we are reading is not null
            while ((inputLine = reader.readLine()) != null) {
                stringBuilder.append(inputLine);
            }

            //Close our InputStream and Buffered reader
            reader.close();
            streamReader.close();

            //Set our result equal to our stringBuilder
            apiResponse = stringBuilder.toString();
            Log.i(MainActivity.class.toString(), apiResponse + " -------------------------  ");

            return apiResponse;

        }

        protected void onPostExecute(String apiResponse) {
            try {

                super.onPostExecute(apiResponse);

                JSONArray array = new JSONArray(apiResponse);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject myObj = array.getJSONObject(i);
                    String tid = myObj.getString("tid");
                    String username = myObj.getString("username");
                    String cardno = myObj.getString("cardno");
                    String tamount = myObj.getString("tamount");
                    String datetime = myObj.getString("datetime");
                    String coffee = myObj.getString("qty1");
                    String expresso = myObj.getString("qty2");



                    String message = "Transaction Id: " + tid
                            + "\n" + "Username: " + username
                            + "\n" + "Card No: " + cardno
                            + "\n" + "Transaction Amount: " + "$" + tamount
                            + "\n" + "DateTime: " + datetime
                            + "\n" + "Coffee quantity: " + coffee
                            + "\n" + "Expresso quantity: " + expresso;


                    Log.v("My Users-------------", message);
                    txt_output.setText(message);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void open(View v){
        Intent intent = new Intent(Transaction.this, CardActivity.class);
        Bundle bundle = getIntent().getExtras();
        String username = bundle.getString("Username");
        intent.putExtra("Username",username);
        startActivity(intent);
        finish();
    }
}
