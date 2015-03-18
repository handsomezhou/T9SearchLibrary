package com.handsomezhou.t9searchdemo.model;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import com.t9search.model.*;

public class Contacts {
	public enum SearchByType{
		SearchByNull,
		SearchByName,
		SearchByPhoneNumber,
	}
	
	private String mName;			
	private String mPhoneNumber;
	private List<PinyinUnit> mNamePinyinUnits;	//save the mName converted to Pinyin characters.
	
	private SearchByType mSearchByType;			//Used to save the type of search
	private StringBuffer mMatchKeywords;		//Used to save the type of Match Keywords.(name or phoneNumber)
	private int mMatchStartIndex;				//the match start  position of mMatchKeywords in original string(name or phoneNumber).
	private int mMatchLength;					//the match length of mMatchKeywords in original string(name or phoneNumber).
	
	public Contacts(String name, String phoneNumber) {
		//super();
		mName = name;
		mPhoneNumber = phoneNumber;
		setNamePinyinUnits(new ArrayList<PinyinUnit>());
		setSearchByType(SearchByType.SearchByNull);
		mMatchKeywords=new StringBuffer();
		mMatchKeywords.delete(0, mMatchKeywords.length());
		mMatchStartIndex=-1;
		mMatchLength=0;
	}

	public static Comparator<Contacts> mSearchComparator = new Comparator<Contacts>() {

		@Override
		public int compare(Contacts lhs, Contacts rhs) {
		
			return (lhs.mMatchStartIndex-rhs.mMatchStartIndex);
		}
	};
	
	public String getName() {
		return mName;
	}
	
	public void setName(String name) {
		mName = name;
	}
	
	public List<PinyinUnit> getNamePinyinUnits() {
		return mNamePinyinUnits;
	}

	public void setNamePinyinUnits(List<PinyinUnit> namePinyinUnits) {
		mNamePinyinUnits = namePinyinUnits;
	}
	
	public String getPhoneNumber() {
		return mPhoneNumber;
	}
	
	public void setPhoneNumber(String phoneNumber) {
		mPhoneNumber = phoneNumber;
	}

	public SearchByType getSearchByType() {
		return mSearchByType;
	}

	public void setSearchByType(SearchByType searchByType) {
		mSearchByType = searchByType;
	}
	
	public StringBuffer getMatchKeywords() {
		return mMatchKeywords;
	}

//	public void setMatchKeywords(StringBuffer matchKeywords) {
//		mMatchKeywords = matchKeywords;
//	}
	
	public void setMatchKeywords(String matchKeywords){
		mMatchKeywords.delete(0, mMatchKeywords.length());
		mMatchKeywords.append(matchKeywords);
	}
	
	public void clearMatchKeywords(){
		mMatchKeywords.delete(0, mMatchKeywords.length());
	}
	
	public int getMatchStartIndex() {
		return mMatchStartIndex;
	}

	public void setMatchStartIndex(int matchStartIndex) {
		mMatchStartIndex = matchStartIndex;
	}
	
	public int getMatchLength() {
		return mMatchLength;
	}

	public void setMatchLength(int matchLength) {
		mMatchLength = matchLength;
	}
}
