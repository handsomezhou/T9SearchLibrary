package com.handsomezhou.t9search.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.handsomezhou.t9search.R;
import com.handsomezhou.t9search.model.Contacts;
import com.handsomezhou.t9search.util.ViewUtil;

public class ContactsAdapter extends ArrayAdapter<Contacts> {
	private Context mContext;
	private int mTextViewResourceId;
	private List<Contacts> mContacts;

	public ContactsAdapter(Context context, int textViewResourceId,
			List<Contacts> contacts) {
		super(context, textViewResourceId, contacts);
		mContext = context;
		mTextViewResourceId = textViewResourceId;
		mContacts = contacts;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		ViewHolder viewHolder;
		Contacts contact = getItem(position);
		if (null == convertView) {
			view = LayoutInflater.from(mContext).inflate(mTextViewResourceId,
					null);
			viewHolder = new ViewHolder();
			viewHolder.mNameTv = (TextView) view
					.findViewById(R.id.name_text_view);
			viewHolder.mPhoneNumberTv = (TextView) view
					.findViewById(R.id.phone_number_text_view);
			view.setTag(viewHolder);
		} else {
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}

		switch (contact.getSearchByType()) {
		case SearchByNull:
			ViewUtil.showTextNormal(viewHolder.mNameTv, contact.getName());
			ViewUtil.showTextNormal(viewHolder.mPhoneNumberTv,
					contact.getPhoneNumber());
			break;
		case SearchByPhoneNumber:
			ViewUtil.showTextNormal(viewHolder.mNameTv, contact.getName());
			ViewUtil.showTextHighlight(viewHolder.mPhoneNumberTv, contact
					.getPhoneNumber(), contact.getMatchKeywords().toString());
			break;
		case SearchByName:
			ViewUtil.showTextHighlight(viewHolder.mNameTv, contact.getName(),
					contact.getMatchKeywords().toString());
			ViewUtil.showTextNormal(viewHolder.mPhoneNumberTv,
					contact.getPhoneNumber());
			break;
		default:
			break;
		}
		return view;
	}

	private class ViewHolder {
		TextView mNameTv;
		TextView mPhoneNumberTv;
	}
}
