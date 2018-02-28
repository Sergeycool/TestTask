package test.ainsoft.net.testtask;


import android.graphics.drawable.Drawable;

import java.util.ArrayList;

public class Admin {

    String name;
    Drawable image;
    boolean box;
   // ArrayList<Points> points;


    Admin(String _describe, Drawable _image, boolean _box) {
        name = _describe;
        image = _image;
        //points = _points;
        box = _box;
    }

}