package test.ainsoft.net.testtask;


public class Points {

    private double latitude;
    private double longitude;
    private String date;

    //long curDate = System.currentTimeMillis();


    Points(double _lat, double _lon, String _date) {

        latitude = _lat;
        longitude = _lon;
        date = _date;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setDate(String date) {
        this.date = date;
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
