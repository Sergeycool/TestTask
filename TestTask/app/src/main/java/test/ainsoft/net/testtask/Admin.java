package test.ainsoft.net.testtask;


import android.graphics.Color;

import java.util.ArrayList;

public class Admin {

    String name;
    int image;
    boolean box;
    ArrayList<Points> points;
    int color;
    float fColor;




    Admin(String _describe, int _image, boolean _box, ArrayList<Points> _points, int _color, float _fColor) {
        name = _describe;
        image = _image;
        box = _box;
        points = _points;
        color = _color;
        fColor = _fColor;

    }

    public float getfColor() {
        return fColor;
    }

    public void setfColor(float fColor) {
        this.fColor = fColor;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public boolean isBox() {
        return box;
    }

    public void setBox(boolean box) {
        this.box = box;
    }

    public ArrayList<Points> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<Points> points) {
        this.points = points;
    }



}