package com.handsomezhou.t9searchdemo.activity;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.handsomezhou.t9searchdemo.R;
import com.handsomezhou.t9searchdemo.adapter.ContactsAdapter;
import com.handsomezhou.t9searchdemo.model.Contacts;
import com.handsomezhou.t9searchdemo.util.ContactsHelper;
import com.handsomezhou.t9searchdemo.util.ContactsHelper.OnContactsLoad;
import com.handsomezhou.t9searchdemo.view.T9TelephoneDialpadView;
import com.handsomezhou.t9searchdemo.view.T9TelephoneDialpadView.OnT9TelephoneDialpadView;

import com.t9search.model.*;
/**
 * @description Main activity
 * @author handsomezhou
 * @date 2014.11.09
 */
public class MainActivity extends Activity implements OnT9TelephoneDialpadView,
		OnContactsLoad{
	private static final String TAG = "MainActivity";
	private Context mContext;
	private ListView mContactsLv;
	private View mLoadContactsView;
	private TextView mSearchResultPromptTv;
	private T9TelephoneDialpadView mT9TelephoneDialpadView;
	private Button mDialpadOperationBtn;

	private ContactsAdapter mContactsAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mContext = this;
		initView();
		initData();
		initListener();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		//moveTaskToBack(true);
	}
	
	private void initView() {
		mContactsLv = (ListView) findViewById(R.id.contacts_list_view);
		mLoadContactsView = findViewById(R.id.load_contacts);
		mSearchResultPromptTv = (TextView) findViewById(R.id.search_result_prompt_text_view);
		mT9TelephoneDialpadView = (T9TelephoneDialpadView) findViewById(R.id.t9_telephone_dialpad_layout);
		mT9TelephoneDialpadView.setOnT9TelephoneDialpadView(this);

		mDialpadOperationBtn = (Button) findViewById(R.id.dialpad_operation_btn);
		mDialpadOperationBtn.setText(R.string.hide_keyboard);

		showView(mContactsLv);
		hideView(mLoadContactsView);
		hideView(mSearchResultPromptTv);

	}

	private void initData() {
		ContactsHelper.getInstance().setOnContactsLoad(this);
		boolean startLoad = ContactsHelper.getInstance().startLoadContacts();
		if (true == startLoad) {
			showView(mLoadContactsView);
		}
		mContactsAdapter = new ContactsAdapter(mContext,
				R.layout.contacts_list_item, ContactsHelper.getInstance()
						.getSearchContacts());
		mContactsLv.setAdapter(mContactsAdapter);
	}

	private void initListener() {
		mDialpadOperationBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				clickDialpad();
			}
		});
		
		mContactsLv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Contacts contacts=ContactsHelper.getInstance().getSearchContacts().get(position);
				 String uri = "tel:" + contacts.getPhoneNumber() ;
				 Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse(uri));
				// intent.setData(Uri.parse(uri));
				 startActivity(intent);
				
			}
		});
	}

	private void clickDialpad() {
		if (mT9TelephoneDialpadView.getT9TelephoneDialpadViewVisibility() == View.VISIBLE) {
			mT9TelephoneDialpadView.hideT9TelephoneDialpadView();
			mDialpadOperationBtn.setText(R.string.display_keyboard);
		} else {
			mT9TelephoneDialpadView.showT9TelephoneDialpadView();
			mDialpadOperationBtn.setText(R.string.hide_keyboard);
		}
	}

	@Override
	public void onAddDialCharacter(String addCharacter) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onDeleteDialCharacter(String deleteCharacter) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onDialInputTextChanged(String curCharacter) {
		
		if(TextUtils.isEmpty(curCharacter)){
			ContactsHelper.getInstance().parseT9InputSearchContacts(null);
		}else{
			ContactsHelper.getInstance().parseT9InputSearchContacts(curCharacter);
		}
		updateContactsList();
	}

	@Override
	public void onHideT9TelephoneDialpadView() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onContactsLoadSuccess() {
		hideView(mLoadContactsView);
		updateContactsList();
		
		int contactsCount=ContactsHelper.getInstance().getBaseContacts().size();
		for(int i=0; i<contactsCount; i++){
			String name=ContactsHelper.getInstance().getBaseContacts().get(i).getName();
			Log.i(TAG,"++++++++++++++++++++++++++++++:["+name+"]"+"++++++++++++++++++++++++++++++");
			List<PinyinUnit> pinyinUnit=ContactsHelper.getInstance().getBaseContacts().get(i).getNamePinyinUnits();
			int pinyinUnitCount=pinyinUnit.size();
			for(int j=0; j<pinyinUnitCount; j++){
				PinyinUnit pyUnit=pinyinUnit.get(j);
				Log.i(TAG,"j="+j+",isPinyin["+pyUnit.isPinyin()+"],startPosition=["+pyUnit.getStartPosition()+"]");
				List<T9PinyinUnit> stringIndex=pyUnit.getT9PinyinUnitIndex();
				int stringIndexLength=stringIndex.size();
				for(int k=0; k<stringIndexLength; k++){
					Log.i(TAG,"k="+k+"["+stringIndex.get(k).getPinyin()+"]+["+stringIndex.get(k).getNumber()+"]" );
				}
				
			}
			
			
		}
	}

	@Override
	public void onContactsLoadFailed() {

		hideView(mLoadContactsView);
		showView(mContactsLv);
	}

	private void hideView(View view) {
		if (null == view) {
			return;
		}
		if (View.GONE != view.getVisibility()) {
			view.setVisibility(View.GONE);
		}

		return;
	}

	private int getViewVisibility(View view) {
		if (null == view) {
			return View.GONE;
		}

		return view.getVisibility();
	}

	private void showView(View view) {
		if (null == view) {
			return;
		}

		if (View.VISIBLE != view.getVisibility()) {
			view.setVisibility(View.VISIBLE);
		}
	}

	private void updateContactsList(){
		if(null==mContactsLv){
			return;
		}
		
		BaseAdapter contactsAdapter=(BaseAdapter) mContactsLv.getAdapter();
		if(null!=contactsAdapter){
			contactsAdapter.notifyDataSetChanged();
			if(contactsAdapter.getCount()>0){
				showView(mContactsLv);
				hideView(mSearchResultPromptTv);
				
			}else{
				hideView(mContactsLv);
				showView(mSearchResultPromptTv);
				
			}
		}
	}

}
