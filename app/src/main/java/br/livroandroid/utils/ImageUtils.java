package br.livroandroid.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ImageUtils {

	private static final String TAG = ImageUtils.class.getName();

	public static Bitmap getBitmap(Context context, File file) {
		Bitmap bitmap;
		try {
			bitmap = Media.getBitmap(context.getContentResolver(),
					Uri.fromFile(file));
			return bitmap;
		} catch (FileNotFoundException e) {
			Log.e(TAG, e.getMessage(), e);
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
		}
		return null;
	}

	public static Bitmap resizeBitmap(Bitmap bitmap, float newWidth,
			float newHeight) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		float scaleX = newWidth / bitmap.getWidth();
		float scaleY = newHeight / bitmap.getHeight();

		Matrix matrix = new Matrix();
		matrix.setScale(scaleX, scaleY);

		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,
				matrix, true);
		return resizedBitmap;
	}

	public static Bitmap getResizedImage(Uri uriFile, int width, int heigth) {
		Bitmap bitmap = BitmapFactory.decodeFile(uriFile.getPath());

		Bitmap resize = ImageUtils.resizeBitmap(bitmap, width, heigth);

		// clean memory
		bitmap.recycle();
		bitmap = null;

		Bitmap rotate = adjustOrientation(uriFile, resize);

		return rotate;
	}

	public static Bitmap adjustOrientation(Uri uriFile, Bitmap bitmap) {
		try {
			int w = 0;
			int h = 0;
			Matrix mtx = new Matrix();

			w = bitmap.getWidth();
			h = bitmap.getHeight();
			mtx = new Matrix();

			ExifInterface exif = new ExifInterface(uriFile.getPath());

			// pega a orienta�‹o real da imagem
			int orientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);

			// Rotate bitmap
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_180:
				mtx.postRotate(180);
				break;
			case ExifInterface.ORIENTATION_ROTATE_90:
				mtx.postRotate(90);
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				mtx.postRotate(270);
				break;
			default: // ORIENTATION_ROTATE_0
				mtx.postRotate(0);
				break;
			}

			// cria vari‡vel com a imagem rotacionada
			Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx,
					true);
			bitmap.recycle();
			bitmap = null;

			return rotatedBitmap;
		} catch (IOException e) {
			return bitmap;
		}
	}
	
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
		}
		
		return inSampleSize  + 2;
	}

	/**
	 * Retorna uma imagem com o tamanho correto escalado, conforme tamanho do
	 * ImageView
	 * 
	 * @param bytes
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static Bitmap getBitmap(byte[] bytes, ImageView imageview) {
		Bitmap bitmap = getBitmap(bytes, imageview.getWidth(), imageview.getHeight());
		return bitmap;
	}

	/**
	 * Retorna uma imagem com o tamanho correto escalado, conforme tamanho
	 * fornecido
	 * 
	 * @param bytes
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static Bitmap getBitmap(byte[] bytes, int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
		return bitmap;
	}
	
	/**
	 * Retorna uma imagem com o tamanho correto escalado, conforme tamanho
	 * fornecido
	 * 
	 * @param bytes
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static Bitmap getBitmap(Context context, int resDrawable, int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;

		BitmapFactory.decodeResource(context.getResources(),resDrawable, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),resDrawable, options);
		return bitmap;
	}
}
