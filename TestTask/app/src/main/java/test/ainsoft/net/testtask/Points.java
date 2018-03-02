package test.ainsoft.net.testtask;


import java.util.Date;

public class Points {

    double latitude;
    double longitude;
    String date;

    //long curDate = System.currentTimeMillis();


    Points(double _lat, double _lon, String _date) {

        latitude = _lat;
        longitude = _lon;
        date = _date;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getDate() {
        return date;
    }
}
