package com.dev.followme;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Entity
@SequenceGenerator(
        name="SEARCH_SEQ_GEN", //시퀀스 제너레이터 이름
        sequenceName="SEARCH_SEQ", //시퀀스 이름
        initialValue=1, //시작값
        allocationSize=1 //메모리를 통해 할당할 범위 사이즈
        )
public class MySearchHist {
	@Id
	@GeneratedValue(
			strategy=GenerationType.SEQUENCE,
			generator="SEARCH_SEQ_GEN"
			)
	private Integer seq;
	
	@Column(length=50,nullable=false)
	private String id;
	
	@Column(length=100,nullable=false)
	private String keyWord;

	@Column(length=16,nullable=false)
	private String searchDt;
	
	public MySearchHist() { super(); }
	public MySearchHist(Integer seq,String id,String keyWord,String searchDt) {
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
