package edu.sjsu.android.starbucks_ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


public class TotalOrderAmountActivity extends AppCompatActivity {

    private EditText txt_total_amount;
    private Button btn_payment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_order_amount);

        Bundle bundle = getIntent().getExtras();
        String amount = bundle.getString("OrderAmount");
        Log.i("---------------------",amount);
        txt_total_amount = (EditText)findViewById(R.id.populateAmount);
        txt_total_amount.setText("$"+amount);

         btn_payment = (Button)findViewById(R.id.payAmount);

        final String serviceURL = "http://ec2-54-185-174-206.us-west-2.compute.amazonaws.com:5000/payments";

        btn_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TotalOrderAmountActivity.HTTPAsyncTask runner = new TotalOrderAmountActivity.HTTPAsyncTask();

                runner.execute(serviceURL);
            }
        });
    }

    private class HTTPAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            // params comes from the execute() call: params[0] is the url.
            try {
                try {
                    return HttpPost(urls[0]);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return "Error!";
                }
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        @Override
        protected void onPostExecute(String result) {

            if(result.equals("Username already exists"))
            {
                Toast.makeText(getApplicationContext(), "User already registered", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Successfully Registered", Toast.LENGTH_SHORT).show();
            }
            Intent intent = new Intent(TotalOrderAmountActivity.this, CardActivity.class);
            startActivity(intent);
            finish();

        }
    }

    private String HttpPost(String myUrl) throws IOException, JSONException {
        String result = "";
        String readLine = null;
        String apiResonse = null;

        URL url = new URL(myUrl);

        // 1. create HttpURLConnection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

        // 2. build JSON object
        JSONObject jsonObject = buidJsonObject();

        // 3. add JSON content to POST request body
        setPostRequestContent(conn, jsonObject);

        // 4. make POST request to the given URL
        conn.connect();

        // 5. return response messag
        String responseMsg = conn.getResponseMessage();
        int code = conn.getResponseCode();
        if (code == 200)
        {
            //Log.v("***func", "Message okay");
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuffer response = new StringBuffer();
            while ((readLine = in.readLine()) != null)
            {
                response.append(readLine);
            }
            in.close();
            apiResonse = response.toString();
        }
        responseMsg += "";

        Log.i(MainActivity.class.toString(), apiResonse + "   " + code);
        JSONObject response = new JSONObject(apiResonse);
        String message = response.getString("result");
        Log.i(MainActivity.class.toString(), message + "   YAYAYAYAYAY  " + code);
        return message;
    }

    private JSONObject buidJsonObject() throws JSONException {

        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate("username", txt_username.getText().toString());
        jsonObject.accumulate("password",  txt_password.getText().toString());
        jsonObject.accumulate("fname",  txt_fname.getText().toString());
        jsonObject.accumulate("lname",  txt_lname.getText().toString());
        jsonObject.accumulate("phone",  txt_phone.getText().toString());
        jsonObject.accumulate("city",  txt_city.getText().toString());
        return jsonObject;
    }

    private void setPostRequestContent(HttpURLConnection conn,
                                       JSONObject jsonObject) throws IOException {

        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        writer.write(jsonObject.toString());
        Log.i(MainActivity.class.toString(), jsonObject.toString());
        writer.flush();
        writer.close();
        os.close();
    }

}
