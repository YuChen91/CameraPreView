package com.cy.camerapreview.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

public class ImageUtils {


	/**
	 * 放大缩小图片(基本不会出现锯齿)
	 * 
	 * @param bitmap
	 * @param reqWidth
	 *            要求的宽度
	 * @param reqHeight
	 *            要求的高度
	 * @return
	 */
	public static Bitmap zoomBitmap(Bitmap bitmap, int reqWidth, int reqHeight) {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.JPEG, 95, bout);// 可以是CompressFormat.PNG

		// 图片原始数据
		byte[] byteArr = bout.toByteArray();

		// 计算sampleSize
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;

		// 调用方法后，option已经有图片宽高信息
		BitmapFactory.decodeByteArray(byteArr, 0, byteArr.length, options);

		// 计算最相近缩放比例
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);
		options.inJustDecodeBounds = false;

		// 这个Bitmap out只有宽高
		Bitmap out = BitmapFactory.decodeByteArray(byteArr, 0, byteArr.length,
				options);

		return bitmap;
	}

	/** 计算图片的缩放值 */
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 3;

		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}

	/**
	 * 压缩图片到指定位置(默认JPG格式)
	 * 
	 * @param bitmap
	 *            需要压缩的图片
	 * @param compressPath
	 *            生成文件路径(例如: /storage/imageCache/1.jpg)
	 * @param quality
	 *            图片质量，0~100
	 * @return if true,保存成功
	 */
	public static boolean compressBiamp(Bitmap bitmap, String compressPath,
			int quality) {
		FileOutputStream stream = null;
		try {
			stream = new FileOutputStream(new File(compressPath));

			bitmap.compress(CompressFormat.JPEG, quality, stream);// (0-100)压缩文件

			return true;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return false;
	}
	
	public static Bitmap compressImage(Bitmap image) {  
		  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        image.compress(CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 50;  
//        while ( baos.toByteArray().length / 1024>100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩         
//            baos.reset();//重置baos即清空baos  
            image.compress(CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
//            options -= 10;//每次都减少10  
//        }  
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中  
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片  
        return bitmap;  
    }  
	
	public static Bitmap comp(Bitmap image) {  
	      
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();         
	    image.compress(CompressFormat.JPEG, 100, baos);
	    if( baos.toByteArray().length / 1024>1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出    
	        baos.reset();//重置baos即清空baos  
	        image.compress(CompressFormat.JPEG, 50, baos);//这里压缩50%，把压缩后的数据存放到baos中
	    }  
	    ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());  
	    BitmapFactory.Options newOpts = new BitmapFactory.Options();  
	    //开始读入图片，此时把options.inJustDecodeBounds 设回true了  
	    newOpts.inJustDecodeBounds = true;  
	    Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);  
	    newOpts.inJustDecodeBounds = false;  
	    int w = newOpts.outWidth;  
	    int h = newOpts.outHeight;  
	    //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为  
	    float hh = 800f;//这里设置高度为800f  
	    float ww = 480f;//这里设置宽度为480f  
	    //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可  
	    int be = 1;//be=1表示不缩放  
	    if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放  
	        be = (int) (newOpts.outWidth / ww);  
	    } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放  
	        be = (int) (newOpts.outHeight / hh);  
	    }  
	    if (be <= 0)  
	        be = 1;  
	    newOpts.inSampleSize = be;//设置缩放比例  
	    //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了  
	    isBm = new ByteArrayInputStream(baos.toByteArray());  
	    bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);  
	    return compressImage(bitmap);//压缩好比例大小后再进行质量压缩  
	}  
	
	
	/**
	 * 读取照片exif信息中的旋转角度
	 * 
	 * @param path
	 *            照片路径
	 * @return角度  获取从相册中选中图片的角度
	 */
	public static int readPictureDegree(String path) {
		if (TextUtils.isEmpty(path)) {
			return 0;
		}
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (Exception e) {
		}
		return degree;
	}

	public static String getPath(Activity activity, Uri uri)
    {    
       String[] projection = {MediaStore.Images.Media.DATA };    
       Cursor cursor = activity.managedQuery(uri, projection, null, null, null);    
       int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
       cursor.moveToFirst();
       return cursor.getString(column_index);    
    } 
	
	public static boolean saveBitmap(Bitmap bitmap, File file) {
        if (bitmap == null)
            return false;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bitmap.compress(CompressFormat.PNG, 100, fos);
            fos.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
	
	public static boolean saveBitmap(Bitmap bitmap, String absPath) {
        return saveBitmap(bitmap, new File(absPath));
    }

	/**
	 * 保存图片到本地
	 * @param bitmap Bitmap
	 * @return 图片文件路径
	 */
	public static String saveImage(Bitmap bitmap) {
        String status = Environment.getExternalStorageState();
        if(status.equals(Environment.MEDIA_MOUNTED)){
            SimpleDateFormat timeFormatter = new SimpleDateFormat(
                    "yyyyMMdd_HHmmss", Locale.CHINA);
            long time = System.currentTimeMillis();
            String imageName = timeFormatter.format(new Date(time));

            // 创建文件夹
            File appDir = new File(Environment.getExternalStorageDirectory(), "CameraPreViewPhoto");
            if (!appDir.exists()) {
                appDir.mkdir();
            }

            // 创建一个位于SD卡上的文件
            File file = new File(appDir, imageName + ".jpg");
            FileOutputStream outStream = null;
            try {
                // 打开指定文件对应的输出流
                outStream = new FileOutputStream(file);
                // 把位图输出到指定文件中
                bitmap.compress(CompressFormat.JPEG, 100, outStream);
                outStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 其次把文件插入到系统图库
//		    try {
//		        MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), imageName, null);
//		    } catch (FileNotFoundException e) {
//		        e.printStackTrace();
//		    }
            // 最后通知图库更新
//		    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getAbsolutePath())));
            return file.getAbsolutePath();
        }else {
            return null;
        }
	}

	/**
	 * 旋转Bitmap
	 * @param b Bitmap
	 * @param rotateDegree 选择角度
	 * @return 返回旋转后的Bitmap
	 */
	public static Bitmap getRotateBitmap(Bitmap b, float rotateDegree) {
		Matrix matrix = new Matrix();
		matrix.postRotate((float) rotateDegree);
		Bitmap rotaBitmap = Bitmap.createBitmap(b, 0, 0, b.getWidth(),
				b.getHeight(), matrix, false);
		return rotaBitmap;
	}

	/**
	 * 获取缩放后的Bitmap
	 * @param byteArr 字节数组
	 * @param reqWidth 宽度
	 * @param reqHeight 高度
	 * @return 缩放Bitmap
	 */
	public Bitmap getSampleBitmap(byte[] byteArr, int reqWidth, int reqHeight) {
		// 计算sampleSize
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;

		// 调用方法后，option已经有图片宽高信息
		BitmapFactory.decodeByteArray(byteArr, 0, byteArr.length, options);

		// 计算最相近缩放比例
		options.inSampleSize = ImageUtils.calculateInSampleSize(options,
				reqWidth, reqHeight);
		options.inJustDecodeBounds = false;

		// 这个Bitmap out只有宽高
		return BitmapFactory.decodeByteArray(byteArr, 0, byteArr.length,
				options);
	}

}
