package com.x62.image;

import com.x62.commons.base.BaseBean;

import java.util.ArrayList;
import java.util.List;

public class PhotoAlbumBean extends BaseBean
{
	public String id;
	public String name;
	public String cover;
	public int size;
	public String lastModified;
	public List<String> photos=new ArrayList<>();

	public boolean isLoadMore()
	{
		return size>photos.size();
	}
}