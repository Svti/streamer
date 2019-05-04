package com.streamer.worker.view;

import java.util.List;

public class PaginationView<E> {

	private List<E> records;		//数据
	private PaginationIndex paginationIndex;
	private long totalPage = 1;		//总页数
	private int maxResult = 10;		//每页显示多少条
	private int currentPage = 1;	//当前页
	private long totalRecord;		//总记录数
	private int pageIndexCount = 10;   	//索引显示数量
	
	public PaginationView(){
		this.maxResult = Integer.MAX_VALUE;
	}
	
	public PaginationView(int maxResult, int currentPage){
		this.maxResult = maxResult;
		this.currentPage = currentPage;
	}
	
	public int getFirstResult() {
		return (this.currentPage-1)*this.maxResult;
	}

	public int getMaxResultCount(){
		return this.maxResult;
	}
	
	public PaginationIndex getPaginationIndex() {
		return paginationIndex;
	}

	public void setPaginationIndex(PaginationIndex paginationIndex) {
		this.paginationIndex = paginationIndex;
	}

	public long getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(long totalPage) {
		this.totalPage = totalPage;
		this.paginationIndex = PaginationIndex.getPainationIndex(pageIndexCount, currentPage, totalPage);
	}

	public int getMaxResult() {
		return maxResult;
	}

	public void setMaxResult(int maxResult) {
		this.maxResult = maxResult;
	}

	public int getCurrentPage() {
		return currentPage > totalPage ? 1 : currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public long getTotalRecord() {
		return totalRecord;
	}

	public void setTotalRecord(long totalRecord) {
		this.totalRecord = totalRecord;
		setTotalPage(this.totalRecord%this.maxResult == 0 ? this.totalRecord/this.maxResult : this.totalRecord/this.maxResult + 1);
	}

	public int getPageIndexCount() {
		return pageIndexCount;
	}

	public void setPageIndexCount(int pageIndexCount) {
		this.pageIndexCount = pageIndexCount;
	}
	
	public void setPaginationResult(PaginationResult<E> pr){
		setRecords(pr.getListData());
		setTotalRecord(pr.getTotalRecord());
	}

	public List<E> getRecords() {
		return records;
	}

	public void setRecords(List<E> records) {
		this.records = records;
	}
}
