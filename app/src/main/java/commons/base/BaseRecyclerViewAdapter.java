package commons.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseRecyclerViewAdapter<T,HV extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<HV> implements View.OnClickListener
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
		//notifyDataSetChanged();
		notifyItemRangeChanged(0,this.data.size());
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
	public void onBindViewHolder(HV holder,int position)
	{
		holder.itemView.setId(position);
		holder.itemView.setOnClickListener(this);
	}

	public void setOnItemClickListener(OnItemClickListener<T> listener)
	{
		this.listener=listener;
	}

	@Override
	public void onClick(View v)
	{
		int position=v.getId();
		if(listener!=null)
		{
			listener.onItemClick(v,position,data.get(position));
		}
	}

	protected abstract int getLayout();

	public interface OnItemClickListener<T>
	{
		void onItemClick(View view,int position,T t);
	}
}