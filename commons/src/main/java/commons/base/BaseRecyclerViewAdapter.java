package commons.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseRecyclerViewAdapter<T,HV extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<HV>
{
	protected ArrayList<T> data=new ArrayList<>();
	protected Context context;
	protected LayoutInflater mLayoutInflater;
	private OnItemClickListener<T> listener;

	public BaseRecyclerViewAdapter(Context context)
	{
		this.context=context;
		mLayoutInflater=LayoutInflater.from(context);
	}

	public void clear()
	{
		data.clear();
	}

	public void setData(List<T> data)
	{
		clear();
		addData(data);
	}

	public void addData(List<T> data)
	{
		this.data.addAll(data);
		notifyDataSetChanged();
	}

	public ArrayList<T> getData()
	{
		return data;
	}

	@Override
	public int getItemCount()
	{
		return data.size();
	}

	@Override
	public void onBindViewHolder(final HV holder,final int position)
	{
		holder.itemView.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(listener!=null)
				{
					listener.onItemClick(holder.itemView,position,data.get(position));
				}
			}
		});
	}

	public void setOnItemClickListener(OnItemClickListener<T> listener)
	{
		this.listener=listener;
	}

	protected abstract int getLayout();

	public interface OnItemClickListener<T>
	{
		void onItemClick(View view,int position,T t);
	}
}