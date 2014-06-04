package com.sourcereport.todolist;

import com.parse.Parse;

import android.app.Application;
import android.util.Log;

public class ToDoList extends Application 
{
	private static String TAG = ToDoList.class.getSimpleName(); 
	@Override
	public void onCreate() 
	{	
		super.onCreate();
		Log.i(TAG,"applicatoin oncreate");
		Parse.initialize(this,getString(R.string.parse_app_id),getString(R.string.parse_client_key));
	}
	
}
