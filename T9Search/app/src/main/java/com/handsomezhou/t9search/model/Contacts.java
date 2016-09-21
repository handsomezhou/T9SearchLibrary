package com.handsomezhou.t9search.model;

import com.t9search.model.PinyinSearchUnit;

import java.util.Comparator;

public class Contacts extends BaseContacts{
	public enum SearchByType{
		SearchByNull,
		SearchByName,
		SearchByPhoneNumber,
	}
	
	private PinyinSearchUnit mNamePinyinSearchUnits; //save the mName converted to Pinyin characters.
	private SearchByType mSearchByType;			//Used to save the type of search
	private StringBuffer mMatchKeywords;		//Used to save the type of Match Keywords.(name or phoneNumber)
	private int mMatchStartIndex;				//the match start  position of mMatchKeywords in original string(name or phoneNumber).
	private int mMatchLength;					//the match length of mMatchKeywords in original string(name or phoneNumber).
	
	public Contacts(String name, String phoneNumber) {
		super();
	    setName(name);
	    setPhoneNumber(phoneNumber);
		setNamePinyinSearchUnits(new PinyinSearchUnit(name));
		setSearchByType(SearchByType.SearchByNull);
		mMatchKeywords=new StringBuffer();
		mMatchKeywords.delete(0, mMatchKeywords.length());
		setMatchStartIndex(-1);
		setMatchLength(0);
	}

	public static Comparator<Contacts> mSearchComparator = new Comparator<Contacts>() {

		@Override
		public int compare(Contacts lhs, Contacts rhs) {
		
			return (lhs.mMatchStartIndex-rhs.mMatchStartIndex);
		}
	};
	

	public PinyinSearchUnit getNamePinyinSearchUnits() {
		return mNamePinyinSearchUnits;
	}

	public void setNamePinyinSearchUnits(PinyinSearchUnit namePinyinSearchUnits) {
		mNamePinyinSearchUnits = namePinyinSearchUnits;
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
