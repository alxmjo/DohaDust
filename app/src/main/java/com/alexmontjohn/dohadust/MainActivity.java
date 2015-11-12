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

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private CurrentData mCurrentData;

    @Bind(R.id.concentrationLabel) TextView mConcentrationLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

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
                } catch (IOException e) {
                    Log.e(TAG, "Exception caught: ", e);
                } catch (JSONException e) {
                    Log.e(TAG, "Exception caught: ", e); // Log exception
                }
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Refreshing sensor data…", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });
    }

    private CurrentData getCurrentData(String jsonData) throws JSONException {
        JSONObject data = new JSONObject(jsonData);
        String concentration = data.getString("current_value");
        Log.i(TAG, "From JSON: " + concentration);

        CurrentData currentData = new CurrentData();

        String s = data.getString("current_value");
        double d = Double.parseDouble(s);
        int i = (int) d;

        currentData.setConcentration(i);

        return currentData;
    }

    private void updateDisplay() {
        mConcentrationLabel.setText(mCurrentData.getConcentration() + "");
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
