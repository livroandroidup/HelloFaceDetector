package br.com.up.hellofacedetector;

import java.io.File;

import br.livroandroid.utils.ImageUtils;
import br.livroandroid.utils.SDCardUtils;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.media.FaceDetector;
import android.media.FaceDetector.Face;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * http://www.edumobile.org/android/android-programming-tutorials/face-detection-example-tutorials-in-android/
 * 
 * @author Ricardo Lecheta
 *
 */
public class CameraFaceDetectorActivity extends Activity {
	private static final int TAKE_PICTURE_CODE = 100;
	private static final int MAX_FACES = 5;
	// Caminho para salvar o arquivo
		private File file;

	private Bitmap cameraBitmap = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera_face_detector);

		((Button) findViewById(R.id.take_picture)).setOnClickListener(btnClick);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (TAKE_PICTURE_CODE == requestCode) {
			processCameraImage(data);
		}
	}

	private void openCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// Cria o caminho do arquivo no sdcard
		file = SDCardUtils.getSdCardFile("up", "foto.jpg");
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
		startActivityForResult(intent, TAKE_PICTURE_CODE);
	}

	private void processCameraImage(Intent intent) {

		((Button) findViewById(R.id.detect_face)).setOnClickListener(btnClick);

		ImageView imageView = (ImageView) findViewById(R.id.image_view);

		cameraBitmap = ImageUtils.getBitmap(this, file);
		imageView.setImageBitmap(cameraBitmap);
	}

	private void detectFaces() {
		if (null != cameraBitmap) {
			int width = cameraBitmap.getWidth();
			int height = cameraBitmap.getHeight();

			FaceDetector detector = new FaceDetector(width, height, CameraFaceDetectorActivity.MAX_FACES);
			Face[] faces = new Face[CameraFaceDetectorActivity.MAX_FACES];

			Bitmap bitmap565 = Bitmap.createBitmap(width, height, Config.RGB_565);
			Paint ditherPaint = new Paint();
			Paint drawPaint = new Paint();

			ditherPaint.setDither(true);
			drawPaint.setColor(Color.RED);
			drawPaint.setStyle(Paint.Style.STROKE);
			drawPaint.setStrokeWidth(2);

			Canvas canvas = new Canvas();
			canvas.setBitmap(bitmap565);
			canvas.drawBitmap(cameraBitmap, 0, 0, ditherPaint);

			drawPaint.setColor(Color.GREEN);
			drawPaint.setStyle(Paint.Style.STROKE);
			drawPaint.setStrokeWidth(10);
			
			int facesFound = detector.findFaces(bitmap565, faces);
			PointF midPoint = new PointF();
			float eyeDistance = 0.0f;
			float confidence = 0.0f;

			Log.i("FaceDetector", "Number of faces found: " + facesFound);
			Toast.makeText(this, "Number of faces found: " + facesFound, Toast.LENGTH_SHORT).show();

			if (facesFound > 0) {
				for (int index = 0; index < facesFound; ++index) {
					faces[index].getMidPoint(midPoint);
					eyeDistance = faces[index].eyesDistance();
					confidence = faces[index].confidence();

					Log.i("FaceDetector", "Confidence: " + confidence + ", Eye distance: " + eyeDistance + ", Mid Point: (" + midPoint.x
							+ ", " + midPoint.y + ")");

					canvas.drawRect((int) midPoint.x - eyeDistance, (int) midPoint.y - eyeDistance, (int) midPoint.x + eyeDistance,
							(int) midPoint.y + eyeDistance, drawPaint);
				}
			}

			ImageView imageView = (ImageView) findViewById(R.id.image_view);
			imageView.setImageBitmap(bitmap565);
		}
	}

	private View.OnClickListener btnClick = new View.OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.take_picture:
				openCamera();
				break;
			case R.id.detect_face:
				detectFaces();
				break;
			}
		}
	};
}