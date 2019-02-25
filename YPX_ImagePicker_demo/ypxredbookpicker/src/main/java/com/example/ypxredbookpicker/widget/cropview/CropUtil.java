/*******************************************************************************
 * Copyright 2016 oginotihiro
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.example.ypxredbookpicker.widget.cropview;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import java.io.Closeable;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CropUtil {
	private static final boolean DEBUG = false;
	private static final String TAG = CropUtil.class.getSimpleName();

	private static final String SCHEME_FILE = "file";
	private static final String SCHEME_CONTENT = "content";

	public static File getFromMediaUri(Context context, Uri uri) {
		if (uri == null) return null;

		if (SCHEME_FILE.equals(uri.getScheme())) {
			return new File(uri.getPath());
		} else if (SCHEME_CONTENT.equals(uri.getScheme())) {
			final String[] filePathColumn = {MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME};
			Cursor cursor = null;
			try {
				cursor = context.getContentResolver().query(uri, filePathColumn, null, null, null);
				if (cursor != null && cursor.moveToFirst()) {
					final int columnIndex = (uri.toString().startsWith("content://com.google.android.gallery3d")) ?
							cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME) :
							cursor.getColumnIndex(MediaStore.MediaColumns.DATA);
					// Picasa images on API 13+
					if (columnIndex != -1) {
						String filePath = cursor.getString(columnIndex);
						if (!TextUtils.isEmpty(filePath)) {
							return new File(filePath);
						}
					}
				}
			} catch (IllegalArgumentException e) {
				// Google Drive images
				return getFromMediaUriPfd(context, uri);
			} catch (SecurityException ignored) {
				// Nothing we can do
			} finally {
				if (cursor != null) cursor.close();
			}
		}
		return null;
	}

	private static File getFromMediaUriPfd(Context context, Uri uri) {
		if (uri == null) return null;

		FileInputStream input = null;
		FileOutputStream output = null;
		try {
			ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
			FileDescriptor fd = pfd.getFileDescriptor();
			input = new FileInputStream(fd);

			String tempFilename = getTempFilename(context);
			output = new FileOutputStream(tempFilename);

			int read;
			byte[] bytes = new byte[4096];
			while ((read = input.read(bytes)) != -1) {
				output.write(bytes, 0, read);
			}
			return new File(tempFilename);
		} catch (IOException ignored) {
			// Nothing we can do
		} finally {
			closeSilently(input);
			closeSilently(output);
		}
		return null;
	}

	private static String getTempFilename(Context context) throws IOException {
		File outputDir = context.getCacheDir();
		File outputFile = File.createTempFile("image", "tmp", outputDir);
		return outputFile.getAbsolutePath();
	}

	public static void closeSilently(Closeable c) {
		if (c == null) return;
		try {
			c.close();
		} catch (Throwable t) {
			// Do nothing
		}
	}

	public static int getExifRotation(File imageFile) {
		if (imageFile == null) return 0;
		try {
			ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
			// We only recognize a subset of orientation tag values
			switch (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					return 90;
				case ExifInterface.ORIENTATION_ROTATE_180:
					return 180;
				case ExifInterface.ORIENTATION_ROTATE_270:
					return 270;
				default:
					return ExifInterface.ORIENTATION_UNDEFINED;
			}
		} catch (IOException e) {
			return 0;
		}
	}

	public static boolean copyExifRotation(File sourceFile, File destFile) {
		if (sourceFile == null || destFile == null) return false;
		try {
			ExifInterface exifSource = new ExifInterface(sourceFile.getAbsolutePath());
			ExifInterface exifDest = new ExifInterface(destFile.getAbsolutePath());
			exifDest.setAttribute(ExifInterface.TAG_ORIENTATION, exifSource.getAttribute(ExifInterface.TAG_ORIENTATION));
			exifDest.saveAttributes();
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public static int calculateBitmapSampleSize(Context context, Uri uri) throws IOException {
		InputStream is = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		try {
			is = context.getContentResolver().openInputStream(uri);
			BitmapFactory.decodeStream(is, null, options); // Just get image size
		} finally {
			closeSilently(is);
		}

		int maxSize = getMaxImageSize(context);
		int sampleSize = 1;
		while (options.outHeight / sampleSize > maxSize || options.outWidth / sampleSize > maxSize) {
			sampleSize = sampleSize << 1;
		}

		return sampleSize;
	}

	private static int getMaxImageSize(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();

		Point size = new Point();
		int width, height;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			display.getSize(size);
			width = size.x;
			height = size.y;
		} else {
			width = display.getWidth();
			height = display.getHeight();
		}
		return (int) Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2));
	}

	public static Bitmap decodeRegionCrop(Context context, Uri sourceUri, Rect rect, int outWidth, int outHeight, int exifRotation) {
		InputStream is = null;
		Bitmap croppedImage = null;
		try {
			is = context.getContentResolver().openInputStream(sourceUri);
			BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(is, false);

			final int width = decoder.getWidth();
			final int height = decoder.getHeight();

			if (DEBUG) {
				Log.i(TAG, "image width：" + width + " height：" + height);
				Log.i(TAG, "crop width：" + rect.width() + " height：" + rect.height());
				Log.i(TAG, "out width：" + outWidth + " height：" + outHeight);
				Log.i(TAG, "exif rotation：" + exifRotation);
			}

			if (exifRotation != 0) {
				// Adjust crop area to account for image rotation
				Matrix matrix = new Matrix();
				matrix.setRotate(-exifRotation);

				RectF adjusted = new RectF();
				matrix.mapRect(adjusted, new RectF(rect));

				// Adjust to account for origin at 0,0
				adjusted.offset(adjusted.left < 0 ? width : 0, adjusted.top < 0 ? height : 0);
				rect = new Rect((int) adjusted.left, (int) adjusted.top, (int) adjusted.right, (int) adjusted.bottom);
			}

			if (DEBUG) {
				Log.i(TAG, "rotate crop width：" + rect.width() + " height：" + rect.height());
			}

			try {
				int maxSize = getMaxImageSize(context);
				int sampleSize = 1;
				while (rect.width() / sampleSize > maxSize || rect.height() / sampleSize > maxSize) {
					sampleSize = sampleSize << 1;
				}

				if (DEBUG) {
					Log.i(TAG, "max size：" + maxSize + " sample size：" + sampleSize);
				}

				BitmapFactory.Options option = new BitmapFactory.Options();
				option.inSampleSize = sampleSize;

				croppedImage = decoder.decodeRegion(rect, option);

				if (DEBUG) {
					Log.i(TAG, "cropped image width：" + croppedImage.getWidth() + " height：" + croppedImage.getHeight());
				}

				boolean isRequired = false;

				Matrix matrix = new Matrix();
				if (exifRotation != 0) {
					matrix.postRotate(exifRotation);

					isRequired = true;
				}

				if (outWidth > 0 && outHeight > 0) {
					RotateBitmap rotateBitmap = new RotateBitmap(croppedImage, exifRotation);
					matrix.postScale((float) outWidth / rotateBitmap.getWidth(), (float) outHeight / rotateBitmap.getHeight());

					isRequired = true;
				}

				if (isRequired) {
					croppedImage = Bitmap.createBitmap(croppedImage, 0, 0, croppedImage.getWidth(), croppedImage.getHeight(), matrix, true);

					if (DEBUG) {
						Log.i(TAG, "is required cropped image width：" + croppedImage.getWidth() + " height：" + croppedImage.getHeight());
					}
				}
			} catch (IllegalArgumentException e) {
				croppedImage = null;
			}
		} catch (FileNotFoundException e) {
			croppedImage = null;
		} catch (IOException e) {
			croppedImage = null;
		} catch (OutOfMemoryError e) {
			croppedImage = null;
		} finally {
			closeSilently(is);
		}
		return croppedImage;
	}

	public static boolean saveOutput(Context context, Uri saveUri, Bitmap croppedImage, int quality) {
		if (saveUri != null) {
			OutputStream outputStream = null;
			try {
				outputStream = context.getContentResolver().openOutputStream(saveUri);
				if (outputStream != null) {
					croppedImage.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
				}
			} catch (FileNotFoundException e) {
				return false;
			} finally {
				closeSilently(outputStream);
			}
			return true;
		}
		return false;
	}
}