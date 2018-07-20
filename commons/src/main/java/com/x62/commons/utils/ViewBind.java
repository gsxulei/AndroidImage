package com.x62.commons.utils;

import android.app.Activity;
import android.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 界面UI注入
 */
public class ViewBind
{
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Bind
	{
		int id();

		boolean click() default false;
	}

	public static void bind(Object target,View root)
	{
		try
		{
			//由于代码混淆可能将R文件去掉导致注入失败
			// Context context=root.getContext();
			// String className=context.getPackageName()+".R$id";
			// Class<?> id=Class.forName(className);
			Map<String,Field> map=new HashMap<String,Field>();
			Field[] fields=target.getClass().getDeclaredFields();
			for(Field f : fields)
			{
				if(!View.class.isAssignableFrom(f.getType()))
				{
					continue;
				}
				try
				{
					Bind bind=f.getAnnotation(Bind.class);
					if(bind!=null)
					{
						map.put(bind.id()+"",f);
					}

					// Field idField=id.getDeclaredField(f.getName());
					// if(idField!=null)
					// {
					// map.put(idField.get(null)+"",f);
					// }
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			setValue(target,root,map);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private static void setValue(Object target,View root,Map<String,Field> map)
	{
		if((!(root instanceof ViewGroup)))// ||map.isEmpty()
		{
			return;
		}

		try
		{
			ViewGroup vg=(ViewGroup)root;
			int count=vg.getChildCount();
			boolean flag=(target instanceof View.OnClickListener);
			for(int i=0;i<count;i++)
			{
				View v=vg.getChildAt(i);
				Field f=map.get(""+v.getId());
				if(f!=null)
				{
					f.setAccessible(true);
					f.set(target,v);
					map.remove(""+v.getId());
				}

				if(flag&&v.isClickable()&&(v.getId()!=-1)&&!(v instanceof AdapterView)&&(!v.hasOnClickListeners()))
				{
					v.setOnClickListener((View.OnClickListener)target);
				}

				if(v instanceof ViewGroup)
				{
					setValue(target,v,map);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void bind(Activity activity)
	{
		bind(activity,activity.getWindow().getDecorView());
	}

	public static void bind(View view)
	{
		bind(view,view);
	}

	public static void bind(Fragment fragment)
	{
		//bind(fragment,fragment.getActivity().getWindow().getDecorView());
		bind(fragment,fragment.getView());
	}

	public static void bind(android.support.v4.app.Fragment fragment)
	{
		//bind(fragment,fragment.getActivity().getWindow().getDecorView());
		bind(fragment,fragment.getView());
	}
}