package com.t9search.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @description PinyinUnit as a base data structure to save the string that Chinese characters  converted to Pinyin characters.
 * for example:
 * Reference: http://www.cnblogs.com/bomo/archive/2012/12/25/2833081.html
 * Chinese characters:"Hi你说了什么git???"
 * Pinyin:
 * 		Hi-Hi					===>mPinyin=false, 	mT9PinyinUnitIndex.size=1, mStartPosition=0	
 * 		{[mT9PinyinUnitIndex.get(0).getPinyin()="Hi",mT9PinyinUnitIndex.get(0).getNumber="Hi"];}
 * 
 * 		你->ni3					===>mPinyin=true, 	mT9PinyinUnitIndex.size=1,	mStartPosition=2
 * 		{[mT9PinyinUnitIndex.get(0).getPinyin()="ni",mT9PinyinUnitIndex.get(0).getNumber="64"];}
 * 
 *     	说->shuo1,shui4,yue4 	===>mPinyin=true,	mT9PinyinUnitIndex.size=3,	mStartPosition=3
 *     	{
 *     	[mT9PinyinUnitIndex.get(0).getPinyin()="shuo",mT9PinyinUnitIndex.get(0).getNumber="7486"];
 *     	[mT9PinyinUnitIndex.get(1).getPinyin()="shui",mT9PinyinUnitIndex.get(1).getNumber="7484"];
 *     	[mT9PinyinUnitIndex.get(2).getPinyin()="yue",mT9PinyinUnitIndex.get(2).getNumber="983"];}
 *     
 *      了->le5,liao3,liao4  	===>mPinyin=true, 	mT9PinyinUnitIndex.size=2,	mStartPosition=4
 *     	{
 *     	[mT9PinyinUnitIndex.get(0).getPinyin()="le",mT9PinyinUnitIndex.get(0).getNumber="53"];
 *     	[mT9PinyinUnitIndex.get(1).getPinyin()="liao",mT9PinyinUnitIndex.get(1).getNumber="5426"];}
 *     
 * 		什->shen2,shi2,she2		===>mPinyin=true, 	mT9PinyinUnitIndex.size=3, mStartPosition=5
 * 		{
 *     	[mT9PinyinUnitIndex.get(0).getPinyin()="shen",mT9PinyinUnitIndex.get(0).getNumber="7436"];
 *     	[mT9PinyinUnitIndex.get(1).getPinyin()="shi",mT9PinyinUnitIndex.get(1).getNumber="744"];
 *     	[mT9PinyinUnitIndex.get(2).getPinyin()="she",mT9PinyinUnitIndex.get(2).getNumber="743"];}
 * 
 * 		么->me5,ma5,yao1			===>mPinyin=true,	mT9PinyinUnitIndex.size=3, mStartPosition=6
 * 		{
 *     	[mT9PinyinUnitIndex.get(0).getPinyin()="me",mT9PinyinUnitIndex.get(0).getNumber="63"];
 *     	[mT9PinyinUnitIndex.get(1).getPinyin()="ma",mT9PinyinUnitIndex.get(1).getNumber="62"];
 *     	[mT9PinyinUnitIndex.get(2).getPinyin()="yao",mT9PinyinUnitIndex.get(2).getNumber="926"];}
 * 
 * 		git???->git???				===>mPinyin=false, 	mT9PinyinUnitIndex.size=1, mStartPosition=7	
 * 		{[mT9PinyinUnitIndex.get(0).getPinyin()="git???",mT9PinyinUnitIndex.get(0).getNumber="448???"];}
 * 
 * @author handsomezhou
 * @date 2014-11-11
 */

public class PinyinUnit {
	//Whether Pinyin
	private boolean mPinyin;
	private int mStartPosition; //save starting index position that the variables in the original string. 
	/*
	 * save the string which single Chinese characters Pinyin(include Multiple Pinyin),or continuous non-kanji characters.
	 * if mT9PinyinUnitIndex.size not more than 1, it means the is not Polyphonic characters.
	 */
	private List<T9PinyinUnit> mT9PinyinUnitIndex;

	public PinyinUnit() {
		mPinyin=false;
		mStartPosition=-1;
		mT9PinyinUnitIndex=new ArrayList<T9PinyinUnit>();
	}

	public boolean isPinyin() {
		return mPinyin;
	}

	public void setPinyin(boolean pinyin) {
		mPinyin = pinyin;
	}

	public int getStartPosition() {
		return mStartPosition;
	}

	public void setStartPosition(int startPosition) {
		mStartPosition = startPosition;
	}
	
	public List<T9PinyinUnit> getT9PinyinUnitIndex() {
		return mT9PinyinUnitIndex;
	}

	public void setStringIndex(List<T9PinyinUnit> stringIndex) {
		mT9PinyinUnitIndex = stringIndex;
	}
}
