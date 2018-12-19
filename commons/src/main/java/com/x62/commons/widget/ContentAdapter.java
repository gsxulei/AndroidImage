package com.x62.commons.widget;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class ContentAdapter extends RecyclerView.Adapter
{
	private RecyclerView.Adapter adapter;

	private List<View> headers=new ArrayList<>();
	private List<View> footers=new ArrayList<>();

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,int viewType)
	{
		if(viewType==-1)
		{
			return adapter.onCreateViewHolder(parent,viewType);
		}
		else
		{
			//int position=viewType;
			if(isHeaderViewPosition(viewType))
			{
				return new ContentViewHolder(headers.get(viewType));
			}
			else
			{
				int location=viewType-headers.size()-getRealItemCount();
				return new ContentViewHolder(headers.get(location));
			}
		}
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder,int position)
	{
		if(isHeaderViewPosition(position)||isFooterViewPosition(position))
		{
			return;
		}
		if(adapter!=null)
		{
			adapter.onBindViewHolder(holder,position);
		}
	}


	private boolean isHeaderViewPosition(int position)
	{
		return position<headers.size();
	}

	private boolean isFooterViewPosition(int position)
	{
		return position >= headers.size()+getRealItemCount();
	}

	@Override
	public int getItemViewType(int position)
	{
		if(isHeaderViewPosition(position)||isFooterViewPosition(position))
		{
			return position;
		}
		return -1;
	}

	@Override
	public int getItemCount()
	{
		return headers.size()+getRealItemCount()+footers.size();
	}

	@Override
	public void onAttachedToRecyclerView(RecyclerView recyclerView)
	{
		//解决网格布局头部尾部错乱问题
		//super.onAttachedToRecyclerView(recyclerView);
		adapter.onAttachedToRecyclerView(recyclerView);

		final RecyclerView.LayoutManager layoutManager=recyclerView.getLayoutManager();
		if(layoutManager instanceof GridLayoutManager)
		{
			final GridLayoutManager gridLayoutManager=(GridLayoutManager)layoutManager;
			gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup()
			{
				@Override
				public int getSpanSize(int position)
				{
					return isHeaderViewPosition(position)||isFooterViewPosition(position)?((GridLayoutManager)
							layoutManager).getSpanCount():1;
				}
			});
		}
	}

	public void setAdapter(RecyclerView.Adapter adapter)
	{
		this.adapter=adapter;
	}

	public void addHeader(View view)
	{
		headers.add(view);
	}

	private int getRealItemCount()
	{
		int count=0;
		if(adapter!=null)
		{
			count=adapter.getItemCount();
		}
		return count;
	}

	public void addFooter(View view)
	{
		footers.add(view);
	}

	class ContentViewHolder extends RecyclerView.ViewHolder
	{
		ContentViewHolder(View itemView)
		{
			super(itemView);
		}
	}
}