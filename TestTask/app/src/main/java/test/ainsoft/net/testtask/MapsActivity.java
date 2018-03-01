package test.ainsoft.net.testtask;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {


    ArrayList<Admin> admins = new ArrayList<Admin>();
    ArrayList<Points> points = new ArrayList<Points>();
    BoxAdapter boxAdapter;
    Random random = new Random();
    private int count;
    private int mCount;

    private GoogleMap mMap;
    private static final LatLng CENTER = new LatLng(47.832910, 35.192243);
    Button button;

    float colors[] = {BitmapDescriptorFactory.HUE_AZURE,
            BitmapDescriptorFactory.HUE_BLUE,
            BitmapDescriptorFactory.HUE_VIOLET,
            BitmapDescriptorFactory.HUE_GREEN,
            BitmapDescriptorFactory.HUE_MAGENTA,
            BitmapDescriptorFactory.HUE_ORANGE,
            BitmapDescriptorFactory.HUE_RED};


    int intCols[] = {R.color.HUE_AZURE,
            R.color.HUE_BLUE,
            R.color.HUE_VIOLET,
            R.color.HUE_GREEN,
            R.color.HUE_MAGENTA,
            R.color.HUE_ORANGE,
            R.color.HUE_RED};

    int icons[] = {R.drawable.ic_admin_azure,
            R.drawable.ic_admin_blue,
            R.drawable.ic_admin_violet,
            R.drawable.ic_admin_green,
            R.drawable.ic_admin_magenta,
            R.drawable.ic_admin_orange,
            R.drawable.ic_admin_red};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        button = findViewById(R.id.play);


        // создаем адаптер

        fillData();
        boxAdapter = new BoxAdapter(this, admins);

        // настраиваем список
        ListView lvMain = (ListView) findViewById(R.id.lvMain);
        lvMain.setAdapter(boxAdapter);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new PolyLineBuilder().start();
            }
        });
    }


    private void getPolyLine(Points start, Points end) {

        LatLng startL = new LatLng(start.getLatitude(), start.getLongitude());
        LatLng endL = new LatLng(end.getLatitude(), end.getLongitude());

        // Getting URL to the Google Directions API
        String url = getDirectionsUrl(startL, endL);

        DownloadTask downloadTask = new DownloadTask();

        // Start downloading json data from Google Directions API
        downloadTask.execute(url);
    }


    public void setDataPoint() {
        points = new ArrayList<>();
        double max = 0.01;
        for (int i = 1; i <= 7; i++) {
            points.add(new Points(47.832910 + random.nextDouble() + max,
                    35.192243 + random.nextDouble() + max, "12:00"));
        }
    }

    // генерируем данные для адаптера
    void fillData() {


        for (int i = 0; i < 7; i++) {
            setDataPoint();
            admins.add(new Admin("Admin " + (i + 1),
                    icons[i], false, points, intCols[i]));
        }
    }

    private class PolyLineBuilder extends Thread {
        private ArrayList<Admin> mCheckedAdmin = showResult();
        @Override
        public void run() {
            count = 0;
            //mCount = 0;
            for (int i = 0; i < mCheckedAdmin.size();i++ ) {
                //mCount = i;

                //Cannot find local variable 'i'  !!!!!!!!!!!!
                final Admin admin = mCheckedAdmin.get(i);

                for (int j = 1; j < mCheckedAdmin.size(); j++) {
                    count = j;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                                getPolyLine(admin.points.get(count - 1), admin.points.get(count));

                                //simple points or getPoints !!!
                        }
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }


    public ArrayList<Admin> showResult() {
        ArrayList<Admin> checkedAdmin = new ArrayList<>();
        String result = "Выбраны админы:";
        for (Admin p : boxAdapter.getBox()) {
            if (p.box) {
                result += "\n" + p.name;
                checkedAdmin.add(p);
            }
        }

        Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
//        Admin admin = admins.get(0);
//
//        for (int i = 1; i < admin.getPoints().size(); i++) {
//            if (i == 1)
//                getPolyLine(admin.getPoints().get(0), admin.getPoints().get(i));
//            else
//                getPolyLine(admin.getPoints().get(i - 1), admin.getPoints().get(i));
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
        return checkedAdmin;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.setMaxZoomPreference(18.0f);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(13), 2000, null);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(CENTER, 8));


        pinMarkers();


        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                //remove ArrayList of Polyline

                mMap.clear();

            }
        });


    }

    public void pinMarkers() {

        for (int i = 0; i < admins.size(); i++) {

            for (int j = 0; j < points.size(); j++) {
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(admins.get(i).points.get(j).latitude, admins.get(i).points.get(j).getLongitude()))
                        .title(admins.get(i).points.get(j).date)
                        .icon(BitmapDescriptorFactory.defaultMarker(colors[j])));
            }

        }

    }


    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();


            parserTask.execute(result);

        }
    }


    /**
     * A class to parse the Google Places in JSON format
     */

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList points = null;
            Color color;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList();
                lineOptions = new PolylineOptions().geodesic(true);

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(8);
                lineOptions.color(getResources().getColor(intCols[i]));
                lineOptions.geodesic(true);

            }

// Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null)
                mMap.addPolyline(lineOptions);
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }


}
