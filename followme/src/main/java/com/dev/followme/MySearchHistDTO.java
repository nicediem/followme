package com.dev.followme;

public class MySearchHistDTO {
	
	private Integer seq;
	private String id;
	private String keyWord;
	private String searchDt;
	
	public MySearchHistDTO() { super(); }
	public MySearchHistDTO(Integer seq,String id,String keyWord,String searchDt) {
		this.seq = seq;
		this.id = id;
		this.keyWord = keyWord;
		this.searchDt = searchDt;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Integer getSeq() {
		return seq;
	}
	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	public String getKeyWord() {
		return keyWord;
	}
	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}
	public String getSearchDt() {
		return searchDt;
	}
	public void setSearchDt(String searchDt) {
		this.searchDt = searchDt;
	}
}
