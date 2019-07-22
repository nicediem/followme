package com.dev.followme;

public class SearchHistDTO {
	
	private String keyWord;
	private Long cnt;
	
	public SearchHistDTO() { super(); }
	public SearchHistDTO(String keyWord,Long cnt) {
		this.keyWord = keyWord;
		this.cnt = cnt;
	}
	public String getKeyWord() {
		return keyWord;
	}
	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}
	public Long getCnt() {
		return cnt;
	}
	public void setCnt(Long cnt) {
		this.cnt = cnt;
	}	
}
