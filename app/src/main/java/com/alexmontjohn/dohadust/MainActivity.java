package com.alexmontjohn.dohadust;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    // Create object to hold data from Xively
    private CurrentData mCurrentData;

    @Bind(R.id.concentrationLabel) TextView mConcentrationLabel;
    @Bind(R.id.summaryLabel) TextView mSummaryLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this); // Cnnect ButterKnife to this activity

        updateData();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Refreshing sensor data…", Snackbar.LENGTH_LONG).show();
                updateData();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateDisplay();
                    }
                });

            }
        });
    }

    private void updateData() {
        String baseUrl = "https://api.xively.com/v2/feeds/";
        String feedId = "1254613424";
        String datastreamId = "concentration";
        String outputType = "json"; // Can be "xml", "json", or "csv"

        String forecastUrl = baseUrl + feedId + "/datastreams/" + datastreamId + "." + outputType;
        String apiKey = getString(R.string.api_key);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(forecastUrl)
                .header("X-ApiKey", apiKey)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                // TODO: Show failure message
                Log.v(TAG, "Something went wrong with enqueue.");
            }

            @Override
            public void onResponse(Response response) throws IOException {
                try {
                    String jsonData = response.body().string();
                    if (response.isSuccessful()) {
                        mCurrentData = getCurrentData(jsonData);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateDisplay();
                            }
                        });
                    }
                } catch (IOException | JSONException | ParseException e) {
                    Log.e(TAG, "Exception caught: ", e);
                }
            }
        });
    }

    private CurrentData getCurrentData(String jsonData) throws JSONException, ParseException {
        JSONObject data = new JSONObject(jsonData); // Convert passed string into JSON object
        CurrentData currentData = new CurrentData(); // Create new CurrentData object to hold data

        // Get values from JSON object and assign to CurrentData
        currentData.setConcentration(data.getString("current_value"));
        currentData.setTime(data.getString("at"));

        return currentData;
    }

    private void updateDisplay() {
        mConcentrationLabel.setText(mCurrentData.getConcentration() + "");
        mSummaryLabel.setText("Last refreshed on " + mCurrentData.getTimeAsString());
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */
}
