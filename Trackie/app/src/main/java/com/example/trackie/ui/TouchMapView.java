package com.example.trackie.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.trackie.R;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// wrapper class for the map image, which is to be used in both map and test mode in the app
// builds the existing TouchImageView class by Mike Ortiz
// inlcudes more functionality such

public class TouchMapView {
    private Context context;
    private int mode;
    public final static int TEST_MODE = 0;
    public final static int MAP_MODE = 1;
    private SubsamplingScaleImageView mapImageView;
    private Bitmap mapBitmap;
    private Canvas mapCanvas;


    public TouchMapView(Context context, int mode, SubsamplingScaleImageView mapImageView, Bitmap mapBitmap) {
        this.context = context;
        this.mode = mode;
        this.mapImageView = mapImageView;
        this.mapBitmap = mapBitmap;
        this.mapCanvas = new Canvas(mapBitmap.copy(mapBitmap.getConfig(), true));
        mapImageView.setImage(ImageSource.bitmap(mapBitmap));
        mapImageView.setOnTouchListener(createMappingTouchListener());
    }

    private Bitmap makeFillerPadding(Bitmap bitmap, double scalePaddingX, double scalePaddingY) {
        int padding_w = (int) (bitmap.getWidth() * (scalePaddingX));
        int padding_h = (int) (bitmap.getHeight() * (scalePaddingY));
        Bitmap outputBitmap = Bitmap.createBitmap(padding_w + (int)bitmap.getWidth(),
                padding_h + (int)bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(outputBitmap);
        canvas.drawColor(context.getColor(R.color.white));
        canvas.drawBitmap(bitmap.copy(bitmap.getConfig(), false),
                0, 0, null);
        return outputBitmap;
    }


    // create touch listener for mapping of points. Main use - create mapping of point when user long presses on the map
    private View.OnTouchListener createMappingTouchListener() {
        GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                if (mapImageView.isReady()) {
                    PointF sCoord = mapImageView.viewToSourceCoord(e.getX(), e.getY());
                    String s = "Pressed - x: " + sCoord.x + ", y: " + sCoord.y;
                    Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }
        });

        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        };

    }

}
