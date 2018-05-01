/*
 * Copyright 2014 http://Bither.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.bither.util;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Rect;

public class NativeUtil
{
	private static int DEFAULT_QUALITY=85;

	public static void compressBitmap(Bitmap bit,String fileName,boolean optimize)
	{
		compressBitmap(bit,DEFAULT_QUALITY,fileName,optimize);
	}

	public static void compressBitmap(Bitmap bit,int quality,String fileName,boolean optimize)
	{
		Bitmap argb=bit;
		if(bit.getConfig()!=Config.ARGB_8888)
		{
			argb=Bitmap.createBitmap(bit.getWidth(),bit.getHeight(),Config.ARGB_8888);
			Canvas canvas=new Canvas(argb);
			Rect rect=new Rect(0,0,bit.getWidth(),bit.getHeight());
			canvas.drawBitmap(argb,null,rect,null);
		}
		saveBitmap(argb,quality,fileName,optimize);
		argb.recycle();
		bit.recycle();
		Runtime.getRuntime().gc();
	}

	private static void saveBitmap(Bitmap bit,int quality,String fileName,boolean optimize)
	{
		compressBitmap(bit,bit.getWidth(),bit.getHeight(),quality,fileName.getBytes(),optimize);
	}

	public static native String compressBitmap(Bitmap bit,int w,int h,int quality,byte[] fileNameBytes,
			boolean optimize);

	static
	{
		System.loadLibrary("jpegbither");
		System.loadLibrary("bitherjni");
	}
}