package ca.uwaterloo.cs349;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SharedViewModel extends ViewModel {
    private final int N = 128;
    private MutableLiveData<ArrayList<Gesture>> gestures;
    private MutableLiveData<Gesture[]> top3;

    public SharedViewModel() {
        gestures = new MutableLiveData<>(new ArrayList<Gesture>());
        top3 = new MutableLiveData<>(new Gesture[3]);
    }

    public LiveData<ArrayList<Gesture>> getGestures() {
        return gestures;
    }

    public MutableLiveData<Gesture[]> getTop3() { return top3; }

    public void recognize(Path path) {

        // resample to N points
        ArrayList<Point> samplePoints = new ArrayList<>();
        float[] pCoor = {0, 0};
        Path sampledPath = new Path(path);
        PathMeasure pathMeasure = new PathMeasure(sampledPath, false);

        // Rotate centroid
        RectF bounds = new RectF();
        sampledPath.computeBounds(bounds, true);
        Matrix m = new Matrix();
        pathMeasure.getPosTan(0, pCoor, null);
        float x = Math.abs(pCoor[0] - bounds.centerX());
        float y = Math.abs(pCoor[1] - bounds.centerY());
        float deg = (float) Math.toDegrees(Math.atan2(y, x));
        m.setRotate(deg, bounds.centerX(), bounds.centerY());
        sampledPath.transform(m);

        // Translate and scale
        sampledPath.computeBounds(bounds, true);
        m.setTranslate(-bounds.left, -bounds.top);
        sampledPath.transform(m);
        sampledPath.computeBounds(bounds, true);
        float sx = 100 / bounds.width();
        float sy = 100 / bounds.height();
        m.setScale(sx, sy, bounds.centerX(), bounds.centerY());
        sampledPath.transform(m);

        // get points
        double dist = pathMeasure.getLength() / (N - 1);
        for (int i = 0; i < N; i++) {
            pathMeasure.getPosTan((float) dist, pCoor, null);
            dist *= 2;
            samplePoints.add(new Point((int) pCoor[0], (int) pCoor[1]));
        }

        // apply formula for each gesture
        Point p;
        Point T;
        double numerator;
        ArrayList<Pair<Double, Gesture>> distances = new ArrayList<>();
        for (Gesture g : gestures.getValue()) {
            numerator = 0;
            for (int i = 0; i < N; i++) {
                p = samplePoints.get(i);
                T = new Point(g.xVals.get(i), g.yVals.get(i));
                numerator += Math.hypot(p.x - T.x, p.y - T.y);
            }
            distances.add(new Pair<>(numerator / N, g));
        }

        if (!distances.isEmpty()) {
            Gesture[] top3_ = new Gesture[]{null, null, null};
            Collections.sort(distances, new Comparator<Pair<Double, Gesture>>() {
                @Override
                public int compare(Pair<Double, Gesture> o1, Pair<Double, Gesture> o2) {
                    return (int) (o1.first - o2.first);
                }
            });

            // update top3
            for (int i = 0; i < 3; i++) {
                if (i >= distances.size()) break;
                top3_[i] = distances.get(i).second;
            }
            top3.setValue(top3_);
        }
    }

    public void addGesture(Path path, String name, Bitmap bitmap) throws IOException {
        ArrayList<Point> samplePoints = new ArrayList<>();
        float[] pCoor = {0, 0};
        Path sampledPath = new Path(path);
        PathMeasure pathMeasure = new PathMeasure(sampledPath, false);

        // Rotate centroid
        RectF bounds = new RectF();
        sampledPath.computeBounds(bounds, true);
        Matrix m = new Matrix();
        pathMeasure.getPosTan(0, pCoor, null);
        float x = Math.abs(pCoor[0] - bounds.centerX());
        float y = Math.abs(pCoor[1] - bounds.centerY());
        float deg = (float) Math.toDegrees(Math.atan2(y, x));
        m.setRotate(deg, bounds.centerX(), bounds.centerY());
        sampledPath.transform(m);

        // Translate and scale
        sampledPath.computeBounds(bounds, true);
        m.setTranslate(-bounds.left, -bounds.top);
        sampledPath.transform(m);
        sampledPath.computeBounds(bounds, true);
        float sx = 100 / bounds.width();
        float sy = 100 / bounds.height();
        m.setScale(sx, sy, bounds.centerX(), bounds.centerY());
        sampledPath.transform(m);

        // get points
        double dist = pathMeasure.getLength() / (N - 1);
        for (int i = 0; i < N; i++) {
            pathMeasure.getPosTan((float) dist, pCoor, null);
            dist *= 2;
            samplePoints.add(new Point((int) pCoor[0], (int) pCoor[1]));
        }

        // add the gesture to the livedata
        ArrayList<Gesture> tmpGestures = gestures.getValue();
        Gesture g = new Gesture(name, samplePoints, bitmap);
        tmpGestures.add(g);
        gestures.setValue(tmpGestures);
    }

    public void setGestures(ArrayList<Gesture> gestures) {
        this.gestures.setValue(gestures);
    }

    public void deleteGesture(int position) {
        ArrayList<Gesture> tmpGestures = gestures.getValue();
        tmpGestures.remove(position);
        gestures.setValue(tmpGestures);
    }

    public void editGesture(int position, String name, MyDrawingView dv) {
        ArrayList<Point> samplePoints = new ArrayList<>();
        float[] pCoor = {0, 0};
        Path sampledPath = new Path(dv.getPath());
        PathMeasure pathMeasure = new PathMeasure(sampledPath, false);

        // Rotate centroid
        RectF bounds = new RectF();
        sampledPath.computeBounds(bounds, true);
        Matrix m = new Matrix();
        pathMeasure.getPosTan(0, pCoor, null);
        float x = Math.abs(pCoor[0] - bounds.centerX());
        float y = Math.abs(pCoor[1] - bounds.centerY());
        float deg = (float) Math.toDegrees(Math.atan2(y, x));
        m.setRotate(deg, bounds.centerX(), bounds.centerY());
        sampledPath.transform(m);

        // Translate and scale
        sampledPath.computeBounds(bounds, true);
        m.setTranslate(-bounds.left, -bounds.top);
        sampledPath.transform(m);
        sampledPath.computeBounds(bounds, true);
        float sx = 100 / bounds.width();
        float sy = 100 / bounds.height();
        m.setScale(sx, sy, bounds.centerX(), bounds.centerY());
        sampledPath.transform(m);

        // get points
        double dist = pathMeasure.getLength() / (N - 1);
        for (int i = 0; i < N; i++) {
            pathMeasure.getPosTan((float) dist, pCoor, null);
            dist *= 2;
            samplePoints.add(new Point((int) pCoor[0], (int) pCoor[1]));
        }

        // replace the gesture in the livedata
        ArrayList<Gesture> tmpGestures = gestures.getValue();
        Gesture g = new Gesture(name, samplePoints, dv.createBitmap());
        tmpGestures.set(position, g);
        gestures.setValue(tmpGestures);
    }
}