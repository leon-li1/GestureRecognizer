package ca.uwaterloo.cs349;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.IOException;

public class MyDrawingView extends View {
    private final int paintColor = getResources().getColor(R.color.colorPrimary);
    private final int STROKE_WIDTH = 15;

    private SharedViewModel sharedViewModel;
    private Paint drawPaint;
    private Path path;
    private String postDraw;
    private Canvas canvas;
    private Bitmap bitmap;

    public MyDrawingView(Context context, SharedViewModel mViewModel, String postDraw) {
        super(context);
        setupPaint();
        sharedViewModel = mViewModel;
        this.postDraw = postDraw;
    }

    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path = new Path();
                path.moveTo(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(x, y);
                break;
            case MotionEvent.ACTION_UP:
                if (path != null) {
                    if (postDraw.equals("add"))
                        showAlert();
                    else if (postDraw.equals("recognize"))
                        sharedViewModel.recognize(path);
                }
                break;
            default:
                return false;
        }
        postInvalidate();
        return true;
    }

    private void showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Gesture");
        View viewInflated = LayoutInflater.from(getContext())
                .inflate(R.layout.add_gesture_dialogue, null, false);
        final EditText input = viewInflated.findViewById(R.id.gestureNameInput);
        final ImageView preview = viewInflated.findViewById(R.id.gesturePreviewI);
        preview.setImageBitmap(createBitmap());

        builder.setView(viewInflated);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    String name = input.getText().toString();
                    sharedViewModel.addGesture(path, name, bitmap);
                    path.reset();
                    postInvalidate();
                    dialog.dismiss();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                path.reset();
                postInvalidate();
                dialog.cancel();
            }
        });
        builder.show();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (path != null)
            canvas.drawPath(path, drawPaint);
    }

    private void setupPaint() {
        drawPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        drawPaint.setColor(paintColor);
        drawPaint.setStrokeWidth(STROKE_WIDTH);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    public Path getPath() {
        return path;
    }

    public Bitmap createBitmap() {
        bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        this.draw(canvas);
        return bitmap;
    }
}