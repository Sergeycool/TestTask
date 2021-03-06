package test.ainsoft.net.testtask;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,BoxAdapter.OnCheckItemListener {

    ArrayList<Admin> admins = new ArrayList<Admin>();
    ArrayList<Points> points = new ArrayList<Points>();
    BoxAdapter boxAdapter;
    Random random = new Random();
//    private int count;
//    private int mCount;

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


    private void getPolyLine(Points start, Points end, int color) {

        LatLng startL = new LatLng(start.getLatitude(), start.getLongitude());
        LatLng endL = new LatLng(end.getLatitude(), end.getLongitude());

        // Getting URL to the Google Directions API
        String url = getDirectionsUrl(startL, endL);

        DownloadTask downloadTask = new DownloadTask();
        downloadTask.setColor(color);
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
                    icons[i], false, points, intCols[i],colors[i]));
        }
    }

    @Override
    public void onChecked(boolean isChecked, int position) {
        if(isChecked){
            pinMarkers(position);

            ArrayList<Admin> chAds = showResult();

            for (int j = 0; j < chAds.size(); j++) {


                LatLngBounds.Builder latLngBuilder = new LatLngBounds.Builder();
                for (int i = 0; i < 7; i++) {
                    LatLng latLng = new LatLng(chAds.get(j).getPoints().get(i).getLatitude(),
                            chAds.get(j).getPoints().get(i).getLongitude());
                    latLngBuilder.include(latLng);
                }

                int size = getResources().getDisplayMetrics().widthPixels;
                LatLngBounds latLngBounds = latLngBuilder.build();
                CameraUpdate track = CameraUpdateFactory.newLatLngBounds(latLngBounds, size, size, 25);
                mMap.moveCamera(track);

            }
        }else {

            mMap.clear();



            ArrayList<Admin> mCheckedAdmin = showResult();
            for(Admin admin: mCheckedAdmin ){
                pinMarkers(admin);

            }



        }

    }

    private class PolyLineBuilder extends Thread {
        private ArrayList<Admin> mCheckedAdmin = showResult();
        private int count;
        private int mCount;
        @Override
        public void run() {
            count = 0;
            mCount = 0;
            Log.i("PolyLineBuilder", "mCheckedAdmin size: " + mCheckedAdmin.size());


            for (int i = 0; i < mCheckedAdmin.size(); i++) {
                mCount = i;
//                final Admin admin = mCheckedAdmin.get(i);

                for (int j = 0; j < mCheckedAdmin.get(mCount).getPoints().size(); j++) {
                    count = j;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (count != 0) {
                                getPolyLine(mCheckedAdmin.get(mCount).getPoints().get(count - 1),
                                        mCheckedAdmin.get(mCount).getPoints().get(count),
                                        mCheckedAdmin.get(mCount).getColor());
                            }
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
        for (Admin p : boxAdapter.getBox()) {
            if (p.isBox()) {
                checkedAdmin.add(p);


            }
        }


        return checkedAdmin;
    }


    public void pinMarkers(int position) {
//        ArrayList<Admin> checkedAdmin = showResult();
        //for (int i = 0; i < checkedAdmin.size(); i++) {
        ArrayList<Points> mPoints = admins.get(position).getPoints();

        for (int j = 0; j < mPoints.size(); j++) {
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(mPoints.get(j).getLatitude(), mPoints.get(j).getLongitude()))
                    .title(mPoints.get(j).getDate())
                    .icon(BitmapDescriptorFactory.defaultMarker(admins.get(position).getfColor())));
        }

    }

    public void pinMarkers(Admin admin) {
//        ArrayList<Admin> checkedAdmin = showResult();
        //for (int i = 0; i < checkedAdmin.size(); i++) {
        ArrayList<Points> mPoints = admin.getPoints();

        for (int j = 0; j < mPoints.size(); j++) {
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(mPoints.get(j).getLatitude(), mPoints.get(j).getLongitude()))
                    .title(mPoints.get(j).getDate())
                    .icon(BitmapDescriptorFactory.defaultMarker(admin.getfColor())));
        }

    }


    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.setMaxZoomPreference(18.0f);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(13), 2000, null);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(CENTER, 8));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                mMap.clear();


            }
        });


    }


    private class DownloadTask extends AsyncTask<String, Void, String> {
        private int color;

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }

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
            parserTask.setColor(color);
            parserTask.execute(result);

        }
    }


    /**
     * A class to parse the Google Places in JSON format
     */

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        private int color;

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }

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
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();
            //ArrayList<Admin> mCheckedAdmin = showResult();

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
                lineOptions.color(getResources().getColor(color));
                lineOptions.width(8);
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
