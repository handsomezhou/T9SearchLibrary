package com.handsomezhou.t9searchdemo.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;

import com.handsomezhou.t9searchdemo.application.T9SearchApplication;
import com.handsomezhou.t9searchdemo.model.Contacts;
import com.handsomezhou.t9searchdemo.model.Contacts.SearchByType;
import com.t9search.util.*;
import com.t9search.model.*;

public class ContactsHelper {
	private static final String TAG="ContactsHelper";
	private Context mContext;
	private static ContactsHelper mInstance = null;
	private List<Contacts> mBaseContacts = null;	//The basic data used for the search
	private List<Contacts> mSearchContacts=null;	//The search results from the basic data
	/*save the first input string which search no result.
		mFirstNoSearchResultInput.size<=0, means that the first input string which search no result not appear.
		mFirstNoSearchResultInput.size>0, means that the first input string which search no result has appeared, 
		it's  mFirstNoSearchResultInput.toString(). 
		We can reduce the number of search basic data by the first input string which search no result.
	*/
	private StringBuffer  mFirstNoSearchResultInput=null;
	private AsyncTask<Object, Object, List<Contacts>> mLoadTask = null;
	private OnContactsLoad mOnContactsLoad = null;
	private OnContactsChanged mOnContactsChanged=null;
	private ContentObserver mContentObserver;
	private boolean mContactsChanged = true;
	private Handler mContactsHandler=new Handler();

	public interface OnContactsLoad {
		void onContactsLoadSuccess();

		void onContactsLoadFailed();
	}
	
	public interface OnContactsChanged{
		void onContactsChanged();
	}

	private ContactsHelper() {
		initContactsHelper();
		//registerContentObserver();
	}

	public static ContactsHelper getInstance() {
		if (null == mInstance) {
			mInstance = new ContactsHelper();
		}
		
		return mInstance;
	}

	public void destroy(){
		if(null!=mInstance){
			//unregisterContentObserver();
			mInstance=null;//the system will free other memory. 
		}
	}
	
	public List<Contacts> getBaseContacts() {
		return mBaseContacts;
	}

	// public void setBaseContacts(List<Contacts> baseContacts) {
	// mBaseContacts = baseContacts;
	// }
	
	public List<Contacts> getSearchContacts() {
		return mSearchContacts;
	}

//	public void setSearchContacts(List<Contacts> searchContacts) {
//		mSearchContacts = searchContacts;
//	}

	public OnContactsLoad getOnContactsLoad() {
		return mOnContactsLoad;
	}

	public void setOnContactsLoad(OnContactsLoad onContactsLoad) {
		mOnContactsLoad = onContactsLoad;
	}

	
	private boolean isContactsChanged() {
		return mContactsChanged;
	}

	private void setContactsChanged(boolean contactsChanged) {
		mContactsChanged = contactsChanged;
	}

	/**
	 * Provides an function to start load contacts
	 * 
	 * @return start load success return true, otherwise return false
	 */
	public boolean startLoadContacts() {
		if (true == isLoading()) {
			return false;
		}
		
		if(false==isContactsChanged()){
			return false;
		}
		
		initContactsHelper();

		mLoadTask = new AsyncTask<Object, Object, List<Contacts>>() {

			@Override
			protected List<Contacts> doInBackground(Object... params) {
				return loadContacts(mContext);
			}

			@Override
			protected void onPostExecute(List<Contacts> result) {
				parseContacts(result);
				super.onPostExecute(result);
				setContactsChanged(false);
				mLoadTask = null;
			}
		}.execute();

		return true;
	}

	
	/**
	 * @description search base data according to string parameter
	 * @param keyword (valid characters include:'0'~'9','*','#')
	 * @return void
	 *
	 * 
	 */
	public void t9Search(String keyword){
		if(null==keyword){//add all base data to search
			if(null!=mSearchContacts){
				mSearchContacts.clear();
			}else{
				mSearchContacts=new ArrayList<Contacts>();
			}
			
			for(Contacts contacts:mBaseContacts){
				contacts.setSearchByType(SearchByType.SearchByNull);
				contacts.clearMatchKeywords();
			}
			
			mSearchContacts.addAll(mBaseContacts);
			mFirstNoSearchResultInput.delete(0, mFirstNoSearchResultInput.length());
			Log.i(TAG,"null==search,mFirstNoSearchResultInput.length()="+mFirstNoSearchResultInput.length());
			return;
		}
		
		if(mFirstNoSearchResultInput.length()>0){
			if(keyword.contains(mFirstNoSearchResultInput.toString())){
				Log.i(TAG,"no need  to search,null!=search,mFirstNoSearchResultInput.length()="+mFirstNoSearchResultInput.length()+"["+mFirstNoSearchResultInput.toString()+"]"+";searchlen="+keyword.length()+"["+keyword+"]");
				return;
			}else{
				Log.i(TAG,"delete  mFirstNoSearchResultInput, null!=search,mFirstNoSearchResultInput.length()="+mFirstNoSearchResultInput.length()+"["+mFirstNoSearchResultInput.toString()+"]"+";searchlen="+keyword.length()+"["+keyword+"]");
				mFirstNoSearchResultInput.delete(0, mFirstNoSearchResultInput.length());
			}
		}
		
		if(null!=mSearchContacts){
			mSearchContacts.clear();	
		}else{
			mSearchContacts=new ArrayList<Contacts>();
		}
		
		int contactsCount=mBaseContacts.size();
		
		/**
		 * search process:
		 * 1:Search by name
		 *  (1)Search by name pinyin characters(org name->name pinyin characters)	('0'~'9','*','#')
		 *  (2)Search by org name		('0'~'9','*','#')
		 * 2:Search by phone number		('0'~'9','*','#')
		 */
		for(int i=0; i<contactsCount; i++){
			PinyinSearchUnit pinyinSearchUnit=mBaseContacts.get(i).getNamePinyinSearchUnits();
		
			if(true==T9Util.match(pinyinSearchUnit, keyword)){//search by name;
				mBaseContacts.get(i).setSearchByType(SearchByType.SearchByName);
				mBaseContacts.get(i).setMatchKeywords(pinyinSearchUnit.getMatchKeyword().toString());
				mBaseContacts.get(i).setMatchStartIndex(mBaseContacts.get(i).getName().indexOf(pinyinSearchUnit.getMatchKeyword().toString()));
				mBaseContacts.get(i).setMatchLength(mBaseContacts.get(i).getMatchKeywords().length());
				mSearchContacts.add(mBaseContacts.get(i));
				continue;
			}else{
				if(mBaseContacts.get(i).getPhoneNumber().contains(keyword)){	//search by phone number
					mBaseContacts.get(i).setSearchByType(SearchByType.SearchByPhoneNumber);
					mBaseContacts.get(i).setMatchKeywords(keyword);
					mBaseContacts.get(i).setMatchStartIndex(mBaseContacts.get(i).getPhoneNumber().indexOf(keyword));
					mBaseContacts.get(i).setMatchLength(keyword.length());
					mSearchContacts.add(mBaseContacts.get(i));
					//Log.i(TAG, "["+mBaseContacts.get(i).getPhoneNumber()+"]"+"["+mBaseContacts.get(i).getMatchKeywords().toString()+"]"+"["+mBaseContacts.get(i).getMatchStartIndex()+"]"+"["+mBaseContacts.get(i).getMatchLength()+"]");
					continue;
				}
		
			}
		}
		
		if(mSearchContacts.size()<=0){
			if(mFirstNoSearchResultInput.length()<=0){
				mFirstNoSearchResultInput.append(keyword);
				Log.i(TAG,"no search result,null!=search,mFirstNoSearchResultInput.length()="+mFirstNoSearchResultInput.length()+"["+mFirstNoSearchResultInput.toString()+"]"+";searchlen="+keyword.length()+"["+keyword+"]");
			}else{
				
			}
		}else{
			Collections.sort(mSearchContacts, Contacts.mSearchComparator);
		}
		
	}
	
	
	private void initContactsHelper(){
		mContext=T9SearchApplication.getContextObject();
		setContactsChanged(true);
		if (null == mBaseContacts) {
			mBaseContacts = new ArrayList<Contacts>();
		} else {
			mBaseContacts.clear();
		}
		
		if(null==mSearchContacts){
			mSearchContacts=new ArrayList<Contacts>();
		}else{
			mSearchContacts.clear();
		}
		
		if(null==mFirstNoSearchResultInput){
			mFirstNoSearchResultInput=new StringBuffer();
		}else{
			mFirstNoSearchResultInput.delete(0, mFirstNoSearchResultInput.length());
		}
	}
	
	private void registerContentObserver(){
		if(null==mContentObserver){
			mContentObserver=new ContentObserver(mContactsHandler) {

				@Override
				public void onChange(boolean selfChange) {
					// TODO Auto-generated method stub
					setContactsChanged(true);
					if(null!=mOnContactsChanged){
						Log.i("ActivityTest","mOnContactsChanged mContactsChanged="+mContactsChanged);
						mOnContactsChanged.onContactsChanged();
					}
					super.onChange(selfChange);
				}
				
			};
		}
		
		if(null!=mContext){
			mContext.getContentResolver().registerContentObserver(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, true,
					mContentObserver);
		}
	}
	
	private void unregisterContentObserver(){
		if(null!=mContentObserver){
			if(null!=mContext){
				mContext.getContentResolver().unregisterContentObserver(mContentObserver);
			}
		}
	}
	
	private boolean isLoading() {
		return (mLoadTask != null && mLoadTask.getStatus() == Status.RUNNING);
	}

	private List<Contacts> loadContacts(Context context) {

		List<Contacts> contacts = new ArrayList<Contacts>();
		Contacts cs = null;
		Cursor cursor = null;
		try {

			cursor = context.getContentResolver().query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
					null, null, "sort_key");

			while (cursor.moveToNext()) {
				String displayName = cursor
						.getString(cursor
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
				String phoneNumber = cursor
						.getString(cursor
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				
				cs = new Contacts(displayName, phoneNumber);
				PinyinUtil.parse(cs.getNamePinyinSearchUnits());
				
				contacts.add(cs);
			}
		} catch (Exception e) {

		} finally {
			if (null != cursor) {
				cursor.close();
				cursor = null;
			}
		}

		return contacts;
	}

	private void parseContacts(List<Contacts> contacts) {
		if (null == contacts || contacts.size() < 1) {
			if (null != mOnContactsLoad) {
				mOnContactsLoad.onContactsLoadFailed();
			}
			return;
		}

		for (Contacts contact : contacts) {
			if (!mBaseContacts.contains(contact)) {
				mBaseContacts.add(contact);
			}
		}

		if (null != mOnContactsLoad) {
			t9Search(null);
			mOnContactsLoad.onContactsLoadSuccess();
		}

		return;
	}
	
	
}
