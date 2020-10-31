package commons.halt;

import android.text.TextUtils;

import commons.widget.Toaster;

public class HaltException extends RuntimeException
{
	public String mText;
	public int mTextId;

	public HaltException(String text,int id)
	{
		mText=text;
		mTextId=id;
	}

	public void onHalt()
	{
		if(!TextUtils.isEmpty(mText))
		{
			Toaster.show(mText);
		}
		else if(mTextId>0)
		{
			Toaster.show(mTextId);
		}
	}
}