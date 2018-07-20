package com.x62.xplugin.utils;

import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.content.res.XmlResourceParser;
import android.text.TextUtils;

/**
 * 解析AndroidManifest.xml
 * 
 * @author GSXL
 *
 */
public class ManifestParser
{
	public String packageName;
	public List<String> launchActivity=new ArrayList<String>();

	private static final String ANDROID_RESOURCES="http://schemas.android.com/apk/res/android";
	private static final String ANDROID_MANIFEST_FILENAME="AndroidManifest.xml";

	public ManifestParser(AssetManager assetManager)
	{
		parse(assetManager);
	}

	private void parse(AssetManager assetManager)
	{
		XmlResourceParser parser=null;
		try
		{
			parser=assetManager.openXmlResourceParser(ANDROID_MANIFEST_FILENAME);
			parseManifest(parser);
			parser.close();
			parser=null;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(parser!=null)
			{
				parser.close();
				parser=null;
			}
		}
	}

	private void parseManifest(XmlResourceParser parser) throws Exception
	{
		int outerDepth=parser.getDepth();
		int type;
		while((type=parser.next())!=XmlPullParser.END_DOCUMENT
				&&(type!=XmlPullParser.END_TAG||parser.getDepth()>outerDepth))
		{
			if(type==XmlPullParser.END_TAG||type==XmlPullParser.TEXT)
			{
				continue;
			}
			String tagName=parser.getName();
			if(TextUtils.isEmpty(tagName))
			{
				continue;
			}

			if("manifest".equals(tagName))
			{
				for(int i=0;i<parser.getAttributeCount();i++)
				{
					if("package".equals(parser.getAttributeName(i)))
					{
						packageName=parser.getAttributeValue(i);
					}
				}
			}

			if(tagName.equals("activity"))// ||tagName.equals("receiver")||tagName.equals("service")
			{
				String activityName=parser.getAttributeValue(ANDROID_RESOURCES,"name");
				if(!TextUtils.isEmpty(activityName))
				{
					parseActivity(activityName,parser);
				}
			}
		}
	}

	private void parseActivity(String activityName,XmlResourceParser parser) throws Exception
	{
		int outerDepth=parser.getDepth();
		int type;
		while((type=parser.next())!=XmlPullParser.END_DOCUMENT
				&&(type!=XmlPullParser.END_TAG||parser.getDepth()>outerDepth))
		{
			if(type==XmlPullParser.END_TAG||type==XmlPullParser.TEXT)
			{
				continue;
			}
			String tagName=parser.getName();
			if(TextUtils.isEmpty(tagName))
			{
				continue;
			}

			if(tagName.equals("intent-filter"))
			{
				IntentFilter mFilter=new IntentFilter();
				parseIntentFilter(mFilter,parser);
				boolean isMain=mFilter.hasAction(Intent.ACTION_MAIN);
				boolean isLaunch=mFilter.hasCategory(Intent.CATEGORY_LAUNCHER);
				if(isMain&&isLaunch)
				{
					if(activityName.charAt(0)=='.')
					{
						activityName=packageName+activityName;
					}
					launchActivity.add(activityName);
				}
			}
		}
	}

	/**
	 * 解析Intent-Filter字段
	 * 
	 * @param mFilter
	 * @param attrs
	 * @throws Exception
	 */
	private void parseIntentFilter(IntentFilter mFilter,XmlResourceParser attrs) throws Exception
	{
		int outerDepth=attrs.getDepth();
		int type;
		while((type=attrs.next())!=XmlPullParser.END_DOCUMENT
				&&(type!=XmlPullParser.END_TAG||attrs.getDepth()>outerDepth))
		{
			if(type==XmlPullParser.END_TAG||type==XmlPullParser.TEXT)
			{
				continue;
			}
			String nodeName=attrs.getName();
			if(TextUtils.isEmpty(nodeName))
			{
				continue;
			}

			if(nodeName.equals("action"))
			{
				String value=attrs.getAttributeValue(ANDROID_RESOURCES,"name");
				if(!TextUtils.isEmpty(value))
				{
					mFilter.addAction(value);
				}
			}
			else if(nodeName.equals("category"))
			{
				String value=attrs.getAttributeValue(ANDROID_RESOURCES,"name");
				if(!TextUtils.isEmpty(value))
				{
					mFilter.addCategory(value);
				}
			}
		}
	}
}