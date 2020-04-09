package com.example.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {

    private FusedLocationProviderClient client;
    private static HttpURLConnection connection;
    double lat;
    double lon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this,new String[]{ACCESS_FINE_LOCATION},1);


        client = LocationServices.getFusedLocationProviderClient(this);

        }

    public void onClick(View view) {

        if (ActivityCompat.checkSelfPermission(MainActivity.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {

            return;
        }
        client.getLastLocation().addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                if(location != null) {
                    //TextView textView = findViewById(R.id.location);
                    //textView.setText(location.toString());
                    TextView latView = findViewById(R.id.latitude);
                    latView.setText("Latitude: " + Double.toString(location.getLatitude()));
                    TextView longView = findViewById(R.id.longitude);
                    longView.setText("Longitude: " + Double.toString(location.getLongitude()));
                    lat = Math.round(location.getLatitude()*100.0)/100.0;
                    lon = Math.round(location.getLongitude()*100.0)/100.0;
                    //lat = location.getLatitude();
                    //lon = location.getLongitude();
                    getJson();
                }
                else{
                    TextView textView = findViewById(R.id.location);
                    textView.setText("The location is null!");
                }

            }
        });

    }
    private void requestPermission(){
        ActivityCompat.requestPermissions(this,new String[]{ACCESS_FINE_LOCATION},1);
    }
    private void getJson() {
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    BufferedReader reader;
                    String line;
                    StringBuffer responseContent = new StringBuffer();
                    try {
                        System.out.println(lat);
                        System.out.println(lon);
                        URL url = new URL("https://ofmpub.epa.gov/echo/echo_rest_services.get_facility_info?output=JSON&p_lat=" + lat + "&p_long="+ lon + "&p_radius=4");
                        //URL url = new URL("https://jsonplaceholder.typicode.com/albums");
                        connection = (HttpURLConnection) url.openConnection();

                        connection.setRequestMethod("GET");
                        connection.setConnectTimeout(5000);
                        connection.setReadTimeout(5000);

                        int status = connection.getResponseCode();
                        System.out.println(status);

                        if (status> 299) {
                            reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                            while((line = reader.readLine()) != null) {
                                responseContent.append(line);
                            }
                            reader.close();
                        }
                        else {
                            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                            while((line = reader.readLine()) != null) {
                                responseContent.append(line);
                                //System.out.println(line);
                            }
                            reader.close();
                        }

                        //System.out.println(responseContent.toString());
                        //System.out.println(responseContent.length());
                        //System.out.println(parse(responseContent.toString()));
                        parse(responseContent.toString());
                        //JSONArray facilities = new JSONArray(responseContent.toString());
                        //System.out.println(facilities.getJSONObject(0).toString());
                        //System.out.println(facilities.toString());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (ProtocolException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {

                }
            }
        });
        thread.start();

    }
    public static String parse(String responseBody) throws JSONException {
        System.out.println(responseBody.length());
        JSONObject facilities = new JSONObject(responseBody);
        System.out.println(facilities.length());
        System.out.println(facilities.toString());
        //System.out.println(facilities.get("Results"));
        JSONObject Results = new JSONObject(facilities.get("Results").toString());
        System.out.println(Results.toString());
        System.out.println(Results.get("QueryParameters"));
        System.out.println(Results.get("Facilities"));
        JSONArray facList = new JSONArray(Results.get("Facilities").toString());
        System.out.println(facList.toString());
        JSONObject obj = facList.getJSONObject(0);
        System.out.println(obj.toString());
        System.out.println(obj.getString("FacName"));
        //System.out.println(Results.getClass()); //JSONObject
        //System.out.println(Results.get("Facilities").getClass());
        //JSONObject facList = new JSONObject(Results.get("Facilities"));
        //System.out.println(facList.toString());

        //System.out.println(facilities.getJSONObject(0).toString());
        /*for (int i = 1; i < facilities.length(); i++) {
            JSONObject facility = facilities.getJSONObject(i);
            String name = facility.getString("Results");
            System.out.println(name);
            System.out.println("test2");
        }*/
        return "placeholder";
    }
}
