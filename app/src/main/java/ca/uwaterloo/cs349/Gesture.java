package ca.uwaterloo.cs349;

import android.graphics.Bitmap;
import android.graphics.Point;

import java.io.Serializable;
import java.util.ArrayList;

public class Gesture implements Serializable {

    public String name;
    public ArrayList<Integer> xVals;
    public ArrayList<Integer> yVals;
    public SerialBitmap bitmap;

    public Gesture(String name, ArrayList<Point> points, Bitmap bitmap) {
        this.name = name;
        xVals = new ArrayList<>();
        yVals = new ArrayList<>();
        for (Point p : points) {
            xVals.add(p.x);
            yVals.add(p.y);
        }
        this.bitmap = new SerialBitmap(bitmap);
    }
}
