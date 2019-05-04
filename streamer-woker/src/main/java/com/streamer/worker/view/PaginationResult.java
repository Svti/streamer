package com.streamer.worker.view;

import java.util.List;

public class PaginationResult<T> {

	private List<T> listData;
	
	private long totalRecord;

	public List<T> getListData() {
		return listData;
	}

	public void setListData(List<T> listData) {
		this.listData = listData;
	}

	public long getTotalRecord() {
		return totalRecord;
	}

	public void setTotalRecord(long totalRecord) {
		this.totalRecord = totalRecord;
	}
}
