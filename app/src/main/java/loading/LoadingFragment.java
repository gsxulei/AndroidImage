package loading;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import app.MainActivity;
import commons.base.BaseFragment;
import commons.widget.AskDialog;

/**
 * Loading页,在此页面中做权限请求
 */
public class LoadingFragment extends BaseFragment implements AskDialog.OnOperateListener
{
	static final int PERMISSIONS_REQUEST=10;

	final String READ_EXTERNAL_STORAGE=Manifest.permission.READ_EXTERNAL_STORAGE;
	final String WRITE_EXTERNAL_STORAGE=Manifest.permission.WRITE_EXTERNAL_STORAGE;
	final String[] permissions={READ_EXTERNAL_STORAGE,WRITE_EXTERNAL_STORAGE};

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onStart()
	{
		super.onStart();
		checkPermission();
	}

	/**
	 * 检查权限<br/>
	 * 总共需要2个权限：读/写SD卡
	 */
	private void checkPermission()
	{
		if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M)
		{
			jump();
			return;
		}

		final int PERMISSION_GRANTED=PackageManager.PERMISSION_GRANTED;
		boolean flag=ContextCompat.checkSelfPermission(mContext,READ_EXTERNAL_STORAGE)!=PERMISSION_GRANTED;
		flag=flag||ContextCompat.checkSelfPermission(mContext,WRITE_EXTERNAL_STORAGE)!=PERMISSION_GRANTED;

		if(!flag)
		{
			jump();
			return;
		}

		AskDialog dialog=new AskDialog(mContext);
		dialog.setContent("APP运行需要一些权限，请授权");
		dialog.setOnOperateListener(this);
		dialog.show();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,@NonNull String[] permissions,@NonNull int[] grantResults)
	{
		super.onRequestPermissionsResult(requestCode,permissions,grantResults);
		if(requestCode!=PERMISSIONS_REQUEST)
		{
			return;
		}

		final int PERMISSION_GRANTED=PackageManager.PERMISSION_GRANTED;
		if(grantResults.length==2&&grantResults[0]==PERMISSION_GRANTED&&grantResults[1]==PERMISSION_GRANTED)
		{
			jump();
		}
		else
		{
			boolean b=true;
			for(String permission : permissions)
			{
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
				{
					if(!shouldShowRequestPermissionRationale(permission))
					{
						b=false;
						break;
					}
				}
			}

			if(b)
			{
				checkPermission();
			}

			//当用户拒绝了权限并且勾选了"不再询问"
			else
			{
				Intent intent=new Intent();
				intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
				Uri uri=Uri.fromParts("package",mContext.getPackageName(),null);
				intent.setData(uri);
				startActivity(intent);
			}
		}
	}

	private void jump()
	{
		Intent intent=new Intent(mContext,MainActivity.class);
		//intent.putExtra("clazz",MainFragment.class);
		startActivity(intent);
		//		intent.putExtra("clazz",LoginFragment.class);
		//		Bundle bundle=new Bundle();
		//		bundle.putString("url","http://zu.jiaoyimao.cn/");
		//		intent.putExtra("PageParams",bundle);
		//		startActivity(intent);
		mContext.finish();
	}

	@Override
	public void onConfirm()
	{
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
		{
			//请求权限
			requestPermissions(permissions,PERMISSIONS_REQUEST);
		}
	}

	@Override
	public void onCancel()
	{
		mContext.finish();
	}
}