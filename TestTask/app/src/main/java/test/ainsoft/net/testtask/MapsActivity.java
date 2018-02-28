package test.ainsoft.net.testtask;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {


    ArrayList<Admin> admins = new ArrayList<Admin>();
    BoxAdapter boxAdapter;

    private GoogleMap mMap;
    private static final LatLng CENTER = new LatLng(47.817485, 35.184378);
    Button button;


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

    }


    public static Drawable tintIcon(Context context, Drawable icon, int color) {
        icon = DrawableCompat.wrap(icon).mutate();
        DrawableCompat.setTintList(icon, ContextCompat.getColorStateList(context, color));
        DrawableCompat.setTintMode(icon, PorterDuff.Mode.SRC_IN);
        return icon;
    }

    public void setData(){





    }



    // генерируем данные для адаптера

    void fillData() {
        for (int i = 1; i <= 10; i++) {
            admins.add(new Admin("Admin " + i,
                    tintIcon(this, getResources().getDrawable(R.drawable.ic_admin), getResources().getColor(R.color.colorPrimary) ),false));
        }
    }


    // выводим информацию о корзине
    public void showResult(View v) {
        String result = "Выбраны админы:";
        for (Admin p : boxAdapter.getBox()) {
            if (p.box)
                result += "\n" + p.name;
        }
        Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.setMinZoomPreference(8.0f);
        mMap.setMaxZoomPreference(18.0f);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(13), 2000, null);



        start();

    }

    private void start(){


        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(CENTER,13));



    }

}
