package com.example.kurtiscc.weatherapp;

import android.content.Intent;
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

import com.squareup.picasso.Picasso;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class MainActivity extends ActionBarActivity {
    public final static String EXTRA_MESSAGE = "com.example.kurtiscc.weatherapp.MESSAGE";
    private Button get_weather_button;
    private TextView WeatherTV, TempTV, humidityTV, feels_likeTV, LocationTV;
    public String weather, temp, feelsLike, humidity, wind, icon, iconURL, location_string;
    public EditText zipCode;
    public ImageView iconIV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        get_weather_button = (Button) findViewById(R.id.get_weather);
        WeatherTV = (TextView) findViewById(R.id.WeatherTV);
        TempTV = (TextView) findViewById(R.id.TempTV);
        zipCode = (EditText) findViewById(R.id.zipCode);
        iconIV = (ImageView) findViewById(R.id.iconIV);
        humidityTV = (TextView) findViewById(R.id.humidityTV);
        feels_likeTV = (TextView) findViewById(R.id.feels_likeTV);
        LocationTV = (TextView) findViewById(R.id.LocationTV);

    }

    public void DisplayJSON() {
        WeatherTV.setText(weather);
        TempTV.setText(temp);
        humidityTV.setText(humidity);
        feels_likeTV.setText(feelsLike);
        LocationTV.setText(location_string);

        Uri uri = Uri.parse(iconURL);

        Picasso.with(MainActivity.this)
                .load(uri)
                .resize(150,150)
                .centerCrop()
                .into(iconIV);

    }


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

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.get_weather:
                String zipcode = String.valueOf(zipCode.getText());
                GetWeather get_weather_stuff = new GetWeather(zipcode);
                get_weather_stuff.execute();
                zipCode.setText("");
                break;

            default:
                return;

        }
    }

    public void getForecast(View v) {
        Intent intent = new Intent(this, ForecastActivity.class);
        Button getForecast = (Button) findViewById(R.id.get_forecast);
        String message = getForecast.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);

        }

    public void getGraph(View v) {
        Intent intent = new Intent(this, GraphActivity.class);
        Button getGraph = (Button) findViewById(R.id.get_graph);
        String message = getGraph.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);

    }


        public class GetWeather extends AsyncTask<Void, Void, Boolean> {

            JSONObject jsonObject = null;
            String zipcode;
            public GetWeather (String zipcode) {
                this.zipcode = zipcode;
            }
            @Override
            protected Boolean doInBackground(Void... params) {
                String postURL = "http://api.wunderground.com/api/011531b65971abde/conditions/q/" + zipcode + ".json";
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
                            JSONObject response = jsonObject.getJSONObject("current_observation");

                            JSONObject location = response.getJSONObject("display_location");

                            location_string = location.get("full").toString();
                            weather = response.get("weather").toString();
                            temp = response.get("temperature_string").toString();
                            feelsLike = response.get("feelslike_string").toString();
                            humidity = response.get("relative_humidity").toString();
                            wind = response.get("wind_string").toString();
                            icon = response.get("icon").toString();
                            iconURL = response.get("icon_url").toString();


                            DisplayJSON();
                            Log.v("Weather", weather);
                            Log.v("Temperature", temp);

                        } catch (JSONException e) {
                            Log.v("Error", e.toString());
                        }

                    }

                    else {

                    }
            }
        }
    }

