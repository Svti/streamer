package com.streamer.service.view;

public class PaginationIndex {

	private long startIndex;

	private long endIndex;

	public PaginationIndex(long s, long e) {
		this.startIndex = s;
		this.endIndex = e;
	}

	public static PaginationIndex getPainationIndex(long viewIndexCount, long currentPage, long totalPage) {
		long s_startIndex = currentPage - (viewIndexCount % 2 == 0 ? viewIndexCount / 2 - 1 : viewIndexCount / 2);
		long e_endIndex = currentPage + viewIndexCount / 2;
		if (s_startIndex < 1) {
			s_startIndex = 1;
			if (totalPage >= viewIndexCount)
				e_endIndex = viewIndexCount;
			else
				e_endIndex = totalPage;
		}
		if (e_endIndex > totalPage) {
			e_endIndex = totalPage;
			if ((e_endIndex - viewIndexCount) > 0)
				s_startIndex = e_endIndex - viewIndexCount + 1;
			else
				s_startIndex = 1;
		}
		return new PaginationIndex(s_startIndex, e_endIndex);
	}

	public long getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(long startIndex) {
		this.startIndex = startIndex;
	}

	public long getEndIndex() {
		return endIndex;
	}

	public void setEndIndex(long endIndex) {
		this.endIndex = endIndex;
	}
}
