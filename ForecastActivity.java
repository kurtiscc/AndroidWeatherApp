package com.example.kurtiscc.weatherapp;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;


public class ForecastActivity extends ActionBarActivity {

    public EditText zipCode_forecast;
    private Button get_forecast_button;
    private TextView day_one, day_two, day_three;
    private ImageView icon_one, icon_two, icon_three;
    private TextView high_low_one, high_low_two, high_low_three;
    public String[] days = new String[4];
    public String[] icons = new String[4];
    public String[] highLow = new String[4];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        get_forecast_button = (Button) findViewById(R.id.get_forecast);
        //ForecastTV = (TextView) findViewById(R.id.ForecastTV);
        zipCode_forecast = (EditText) findViewById(R.id.zipCode_forecast);
        day_one = (TextView) findViewById(R.id.day_one);
        day_two = (TextView) findViewById(R.id.day_two);
        day_three = (TextView) findViewById(R.id.day_three);
        //day_four = (TextView) findViewById(R.id.day_four);
        icon_one = (ImageView) findViewById(R.id.icon_one);
        icon_two = (ImageView) findViewById(R.id.icon_two);
        icon_three = (ImageView) findViewById(R.id.icon_three);
       // icon_four = (ImageView) findViewById(R.id.icon_four);
        high_low_one = (TextView) findViewById(R.id.high_low_one);
        high_low_two = (TextView) findViewById(R.id.high_low_two);
        high_low_three = (TextView) findViewById(R.id.high_low_three);
        //high_low_four = (TextView) findViewById(R.id.high_low_four);





    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_forecast, menu);
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

    public void DisplayForecastJSON() {
        //ForecastTV.setText(title);
        day_one.setText(days[0]);
        day_two.setText(days[1]);
        day_three.setText(days[2]);
        //day_four.setText(days[3]);

        Uri uri = Uri.parse(icons[0]);

        Picasso.with(ForecastActivity.this)
                .load(uri)
                .resize(100,100)
                .centerCrop()
                .into(icon_one);

        Uri uri1 = Uri.parse(icons[1]);

        Picasso.with(ForecastActivity.this)
                .load(uri1)
                .resize(100,100)
                .centerCrop()
                .into(icon_two);

        Uri uri2 = Uri.parse(icons[2]);

        Picasso.with(ForecastActivity.this)
                .load(uri2)
                .resize(100,100)
                .centerCrop()
                .into(icon_three);



        high_low_one.setText(highLow[0]);
        high_low_two.setText(highLow[1]);
        high_low_three.setText(highLow[2]);
       // high_low_four.setText(highLow[3]);
    }

    public void onClick_forecast(View v) {
        switch (v.getId()) {
            case R.id.get_forecast:
                String zipcode = String.valueOf(zipCode_forecast.getText());
                GetForecast get_forecast_stuff = new GetForecast(zipcode);
                get_forecast_stuff.execute();
                zipCode_forecast.setText("");
                break;

            default:
                return;

        }
    }

    public class GetForecast extends AsyncTask<Void, Void, Boolean> {

        JSONObject jsonObject = null;
        String zipcode;

        public GetForecast(String zipcode) {
            this.zipcode = zipcode;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String postURL = "http://api.wunderground.com/api/011531b65971abde/forecast10day/q/" + zipcode + ".json";
            try {
                //Instantiates an HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(postURL);


                try {

                    ResponseHandler<String> responseHandler = new BasicResponseHandler();

                    // Executes HTTP Post request and puts response into responseBody

                    String responseBody = httpclient.execute(httppost, responseHandler);

                    jsonObject = new JSONObject(responseBody);

                    Log.v("Response", jsonObject.toString());

                    return true;

                } catch (ClientProtocolException e) {
                    return false;
                } catch (IOException e) {
                    return false;
                }
            } catch (Throwable t) {
                return false;
            }
        }


        @Override
        protected void onPostExecute(final Boolean success) {

            if (success) {
                //Parse JSON Object here
                try {


                    JSONArray jsonDays = jsonObject.getJSONObject("forecast").getJSONObject("simpleforecast").getJSONArray("forecastday");

                   // Log.v("Array Length", String.valueOf(forecastArray.length()));

                    for (int i = 0; i < 4; i++) {


                        days[i] = jsonDays.getJSONObject(i).getJSONObject("date").getString("weekday_short");
                        icons[i] = jsonDays.getJSONObject(i).getString("icon_url");
                        String highLows = "High: "
                                + jsonDays.getJSONObject(i).getJSONObject("high").getString("fahrenheit")
                                + " Low: "
                                + jsonDays.getJSONObject(i).getJSONObject("low").getString("fahrenheit");

                        highLow[i] = highLows;
                        //Log.v("Title", title);
                    }

                    DisplayForecastJSON();
                }
                catch (JSONException jse) {
                    Log.e("error", "Error parsing JSON", jse);

                    return;
                }
            } else {
                Log.i("error",
                        "String returned was Null, check doInBackground for errors");
            }

            }
        }
    }

