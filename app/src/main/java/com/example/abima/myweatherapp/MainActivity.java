package com.example.abima.myweatherapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.button);
        final EditText editText = (EditText) findViewById(R.id.city);


        final String baseURL = "http://api.openweathermap.org/data/2.5/weather?q=";
        final String apiKey = "&appid=5c995609a89b061055a3f7ac186f5eee";

        final TextView temp = (TextView) findViewById(R.id.temp);
        final TextView desc = (TextView) findViewById(R.id.desc);
        final TextView main = (TextView) findViewById(R.id.main);
        final String weatherIcon = "http://openweathermap.org/img/w/";

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isConnected()) {
                    String city = editText.getText().toString();
                    if (city.matches("")) {
                        Toast.makeText(MainActivity.this, "Enter the city ", Toast.LENGTH_SHORT).show();
                    } else {
                        String myUrl = baseURL + city + apiKey;
                        setWeather(myUrl);
                    }
                }
                else Toast.makeText(MainActivity.this, "You are not connected to Internet", Toast.LENGTH_SHORT).show();

            }

            private void setImage(String imgUrl) {
                ImageRequest imageRequest = new ImageRequest(imgUrl,
                        new Response.Listener<Bitmap>() {
                            @Override
                            public void onResponse(Bitmap response) {
                                ImageView climateIcon = (ImageView) findViewById(R.id.icon);
                                climateIcon.setImageBitmap(response);
                            }
                        }, 100, 100, ImageView.ScaleType.FIT_CENTER, null,
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.i("ImageRequest", "ImageRequest onErrorResponse: " + error);
                            }
                        });
                MySingleton.getInstance(MainActivity.this).addToRequestQueue(imageRequest);
            }

            private void setWeather(String myUrl) {
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, myUrl, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    //To get the temperature
                                    String jsonObj = response.getString("main");
                                    JSONObject jo = new JSONObject(jsonObj);
                                    double temperature = Double.parseDouble(jo.getString("temp")) - 273.15;
                                    temp.setText("Temperature : " + String.format("%.2f", temperature) + " deg celcious");

                                    //To get the Weather description
                                    String jsonObjdes = response.getString("weather");
                                    JSONArray jsonArray = new JSONArray(jsonObjdes);
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsobj = jsonArray.getJSONObject(i);
                                        String description = jsobj.getString("description");
                                        String climate = jsobj.getString("main");
                                        String icon = jsobj.getString("icon");
                                        desc.setText("Description : " + description);
                                        main.setText("Climate : " + climate);
                                        setImage(weatherIcon + icon + ".png");
                                    }


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.i("TAG", "onErrorResponse: " + error);
                                Toast.makeText(MainActivity.this, "Enter a proper city ", Toast.LENGTH_SHORT).show();
                            }
                        });
                MySingleton.getInstance(MainActivity.this).addToRequestQueue(jsonObjectRequest);
            }

        });


    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return networkInfo != null && networkInfo.isConnected();
    }
}
