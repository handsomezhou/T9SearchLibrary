
package com.handsomezhou.t9search.fragment;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handsomezhou.t9search.R;
import com.handsomezhou.t9search.adapter.ContactsAdapter;
import com.handsomezhou.t9search.dialog.BaseProgressDialog;
import com.handsomezhou.t9search.helper.ContactsHelper;
import com.handsomezhou.t9search.helper.ContactsHelper.OnContactsLoad;
import com.handsomezhou.t9search.model.Contacts;
import com.handsomezhou.t9search.util.ViewUtil;
import com.handsomezhou.t9search.view.T9TelephoneDialpadView;
import com.handsomezhou.t9search.view.T9TelephoneDialpadView.OnT9TelephoneDialpadView;

public class  MainFragment extends BaseFragment implements OnT9TelephoneDialpadView,
        OnContactsLoad {
    private static final String TAG=MainFragment.class.getSimpleName();
    private ListView mContactsLv;
 
    private TextView mSearchResultPromptTv;
    private T9TelephoneDialpadView mT9TelephoneDialpadView;

    private View mKeyboardSwitchLayout;
    private ImageView mKeyboardSwitchIv;
    private ContactsAdapter mContactsAdapter;
    private BaseProgressDialog mBaseProgressDialog;
    
    
    @Override
    public void onResume() {
        refreshView();
        super.onResume();
    }

    @Override
    protected void initData() {
        setContext(getActivity());
        ContactsHelper.getInstance().setOnContactsLoad(this);
        boolean startLoad = ContactsHelper.getInstance().startLoadContacts();
        if (true == startLoad) {
            getBaseProgressDialog().show(getContext().getString(R.string.loading_contacts));
        }

    }

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        mContactsLv = (ListView) view.findViewById(R.id.contacts_list_view);
        mContactsAdapter = new ContactsAdapter(getContext(),
                R.layout.contacts_list_item, ContactsHelper.getInstance()
                        .getSearchContacts());
        mContactsLv.setAdapter(mContactsAdapter);

     
        mSearchResultPromptTv = (TextView) view.findViewById(R.id.search_result_prompt_text_view);
        mT9TelephoneDialpadView = (T9TelephoneDialpadView) view
                .findViewById(R.id.t9_telephone_dialpad_layout);
        mT9TelephoneDialpadView.setOnT9TelephoneDialpadView(this);

        mKeyboardSwitchLayout = view.findViewById(R.id.keyboard_switch_layout);
        mKeyboardSwitchIv = (ImageView) view.findViewById(R.id.keyboard_switch_image_view);
        showKeyboard();
       
        return view;
    }

    @Override
    protected void initListener() {
        mKeyboardSwitchLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switchKeyboard();
            }
        });

        mKeyboardSwitchIv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switchKeyboard();
            }
        });

        mContactsLv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                Contacts contacts = ContactsHelper.getInstance().getSearchContacts().get(position);
                String uri = "tel:" + contacts.getPhoneNumber();
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(uri));
                // intent.setData(Uri.parse(uri));
                startActivity(intent);

            }
        });

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

        if (TextUtils.isEmpty(curCharacter)) {
            ContactsHelper.getInstance().t9Search(null);
        } else {
            ContactsHelper.getInstance().t9Search(curCharacter);
        }
        refreshContactsLv();
    }

    @Override
    public void onHideT9TelephoneDialpadView() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onContactsLoadSuccess() {
        getBaseProgressDialog().hide();
        refreshContactsLv();
       
    }

    @Override
    public void onContactsLoadFailed() {

        getBaseProgressDialog().hide();
        ViewUtil.showView(mContactsLv);
    }

    public BaseProgressDialog getBaseProgressDialog() {
        if (null == mBaseProgressDialog) {
            mBaseProgressDialog = new BaseProgressDialog(getContext());
        }
        return mBaseProgressDialog;
    }

    public void setBaseProgressDialog(BaseProgressDialog baseProgressDialog) {
        mBaseProgressDialog = baseProgressDialog;
    }

    
    private void switchKeyboard() {
        if (ViewUtil.getViewVisibility(mT9TelephoneDialpadView) == View.GONE) {
            showKeyboard();
        } else {
            hideKeyboard();
        }
    }

    private void hideKeyboard() {
        ViewUtil.hideView(mT9TelephoneDialpadView);
        mKeyboardSwitchIv
                .setBackgroundResource(R.drawable.keyboard_show_selector);
    }

    private void showKeyboard() {
        ViewUtil.showView(mT9TelephoneDialpadView);
        mKeyboardSwitchIv
                .setBackgroundResource(R.drawable.keyboard_hide_selector);
    }
    
    public void refreshView(){
        
        refreshContactsLv();
    }
    
    private void refreshContactsLv() {
        if (null == mContactsLv) {
            return;
        }

        BaseAdapter contactsAdapter = (BaseAdapter) mContactsLv.getAdapter();
        if (null != contactsAdapter) {
            contactsAdapter.notifyDataSetChanged();
            if (contactsAdapter.getCount() > 0) {
                ViewUtil.showView(mContactsLv);
                ViewUtil.hideView(mSearchResultPromptTv);

            } else {
                ViewUtil.hideView(mContactsLv);
                ViewUtil.showView(mSearchResultPromptTv);

            }
        }
    }

}
