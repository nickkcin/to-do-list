package com.sourcereport.todolist;

import java.util.ArrayList;
import java.util.List;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.os.Build;

public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}		
	}
		
	public static class PlaceholderFragment extends Fragment implements OnClickListener
	{
		private Button mBtnSend;
		private EditText mTextNote;
		private ListView mList;
		private LayoutInflater mInflater;
		private Handler mHandler;
		private static final int mUpdate = 0x01;
		private NoteItemAdapter mAdapter;
		public PlaceholderFragment() {		
		}
		
		@Override
		public void onAttach(Activity activity) 
		{
			super.onAttach(activity);
			mInflater = LayoutInflater.from(activity);
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {		
			mAdapter = new NoteItemAdapter();
			mHandler = new Handler()
			{
				@Override
				public void handleMessage(Message msg) 
				{
					super.handleMessage(msg);
					switch(msg.what)
					{
						case mUpdate:
							retriveNoteData();
						break;
					}
				}
				
			};
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			mBtnSend = (Button) rootView.findViewById(R.id.btn_save);
			mTextNote = (EditText) rootView.findViewById(R.id.et_note);
			mList = (ListView) rootView.findViewById(android.R.id.list);
			mList.setAdapter(mAdapter);
			mBtnSend.setOnClickListener(this);
			return rootView;
		}
		
		@Override
		public void onViewCreated(View view, Bundle savedInstanceState) 
		{		
			super.onViewCreated(view, savedInstanceState);
			retriveNoteData();
		}

		private void retriveNoteData()
		{
			ParseQuery pq = new ParseQuery("toDo");
			pq.whereNotEqualTo("note","##");
			pq.findInBackground(new FindCallback<ParseObject>() 
			{
				@Override
				public void done(List<ParseObject> dataList, ParseException pex) 
				{				
					if(pex == null)
					{
						mAdapter.addAll(dataList);
					}
				}
			});
		}
		class NoteItemAdapter extends BaseAdapter
		{
			private List<ParseObject> mData = new ArrayList<ParseObject>();
			public void addAll(List<ParseObject> data)
			{
				mData.clear();
				mData.addAll(data);
				notifyDataSetChanged();
			}
			@Override
			public int getCount() 
			{			
				return mData.size();
			}

			@Override
			public ParseObject getItem(int position) 
			{			
				return mData.get(position);
			}

			@Override
			public long getItemId(int position) 
			{			
				return position;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) 
			{	
				CheckBox cbNote = null;
				ParseObject data = null;
				if(convertView == null)
				{
					convertView = mInflater.inflate(R.layout.item_note,parent,false);				
				}
				cbNote = (CheckBox) convertView.findViewById(R.id.ck_note);
				data = getItem(position);
				cbNote.setText(data.getString("note"));
				cbNote.setTag(data);
				cbNote.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
					{		
						ParseObject data = null;
						if(isChecked)
						{
							data = (ParseObject) buttonView.getTag();
							data.deleteInBackground(new DeleteCallback() 
							{								
								@Override
								public void done(ParseException pex) 
								{							
									if(pex == null)
									{
										//notify data set change..
										mHandler.sendEmptyMessage(mUpdate);
									}
								}
							});
						}
					}
				});
				return convertView;
			}			
		}
		@Override
		public void onClick(View v) 
		{		
			switch(v.getId())
			{
				case R.id.btn_save:
					String note = mTextNote.getEditableText().toString();
					ParseObject data = new ParseObject("toDo");
					data.put("note",note);
					data.saveInBackground(new SaveCallback() {
						
						@Override
						public void done(ParseException pex) {
							// TODO Auto-generated method stub
							if(pex == null)
							{
								retriveNoteData();
							}
						}
					});
				break;									
			}
		}
	}

}
