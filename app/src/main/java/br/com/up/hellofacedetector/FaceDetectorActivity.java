package br.com.up.hellofacedetector;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.media.FaceDetector;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;


public class FaceDetectorActivity extends Activity {
    private static final int imagem = R.drawable.face;
    private int NUMBER_OF_FACES = 5;
    private FaceDetector.Face[] detectedFaces;
    private MyView myView;
    private Bitmap bitmap;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myView = new MyView(this);
        setContentView(myView);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
// DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
// bitmap = ImageUtils.getBitmap(this, imagem,
// displayMetrics.widthPixels, displayMetrics.heightPixels);
        bitmap = BitmapFactory.decodeResource(getResources(), imagem, opts);
        detectFaces();
    }
    private void detectFaces() {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        detectedFaces = new FaceDetector.Face[NUMBER_OF_FACES];
        FaceDetector faceDetector = new FaceDetector(width, height, NUMBER_OF_FACES);
        NUMBER_OF_FACES = faceDetector.findFaces(bitmap, detectedFaces);
        Toast.makeText(this, "OK: " + NUMBER_OF_FACES, Toast.LENGTH_SHORT).show();
        myView.setBitmap(bitmap);
    }

    private class MyView extends View {
        private Bitmap bitmap;
        public MyView(Context context) {
            super(context);
        }
        public void setBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
            invalidate();
        }
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            Toast.makeText(getContext(), "Detect!", Toast.LENGTH_SHORT).show();
            detectFaces();
            return super.onTouchEvent(event);
        }
        @Override
        protected void onDraw(Canvas canvas) {
            if (bitmap != null) {
// Desenha a foto
                canvas.drawBitmap(this.bitmap, 0, 0, null);
// Configura o pincel
                Paint paint = new Paint();
                paint.setColor(Color.GREEN);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(3);
                if (detectedFaces != null) {
                    for (int count = 0; count < NUMBER_OF_FACES; count++) {
                        FaceDetector.Face face = detectedFaces[count];
                        PointF midPoint = new PointF();
                        face.getMidPoint(midPoint);
// Desenha um circulo no centro dos olhos
                        canvas.drawCircle(midPoint.x, midPoint.y, 10, paint);
// Distância dos olhos
                        float eyeDistance = face.eyesDistance();
                        canvas.drawRect(midPoint.x - eyeDistance, midPoint.y -
                                eyeDistance, midPoint.x + eyeDistance, midPoint.y
                                + eyeDistance, paint);
                    }
                }
            }
        }
    }
}
