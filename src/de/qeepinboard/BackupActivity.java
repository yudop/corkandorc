/*
 * Copyright (C) 2010 Robert Kanzamar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.qeepinboard;

import android.app.*;
import android.content.*;
import android.content.res.*;
import android.os.*;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.graphics.*;
import android.widget.*;
import android.view.GestureDetector.OnGestureListener;

import java.io.*;
import java.util.*;

public class BackupActivity extends Activity implements OnGestureListener, View.OnClickListener
{
	public static final int BACK_TO_OPTIONS = 0;
	public static final int SAVE_TO_SD      = 1;  
	public static final int LOAD_FROM_SD    = 2;
	public static final int DELETE_FILE     = 3;
	public static final int BACKUP_INFO     = 4;
	
	public static final int RENAME_BACKUP     = 6;
	
	private static final int VDKGRAY = Color.rgb(0x33,0x33,0x33);
	
	private GestureDetector gestureScanner; 
	private Resources res;
	private Display localDisplay; 
    public static int displayX;
    public static int displayY;
    
    private BackupManager mbackup;
    
    private LinearLayout main;
    private TextView emptyinfo;
    
    private ScrollView mScrollView;
    private LinearLayout listarea;
    
    private LinearLayout buttonrowone;
    private View freespace;
    private View[] mfreespace = new View[2];
    private View[] horizontalfreespace = new View[15];
    private LinearLayout buttonrowtwo;
    
    private TextView row1_button1;
    private TextView row1_button2;
    private TextView row1_button3;
    private TextView row2_button1;
    private TextView row2_button2;
    private TextView row2_button3;
    
    private ArrayList<ListItem> itemList = null;
    private ListItem selectedListItem = null;
    private ListItem pendingListItem = null;
    private RenameLabel selectedRename = null;
    private RenameLabel pendingRename = null;
    
    private Toast messageToast;
    
    public static boolean commitedBackupRename = false;
    public static String registeredString = "";
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState); 
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        gestureScanner = new GestureDetector(this);
        mbackup = new BackupManager();
        itemList = new ArrayList<ListItem>();
        res = getResources();
        
        messageToast = Toast.makeText(this, "", 500);
        messageToast.setGravity(Gravity.CENTER, 0, -60);
        
        setDisplay();
        makeGUI();
    }
	
	 @Override
	 public void onResume()
	 {
	    super.onResume();
	    
	    if(commitedBackupRename == true)
	    {
	    	commitedBackupRename = false;
	    	String oldName = selectedListItem.getName();  
	    	
	    	int tmpCount = 1;
	    	boolean repeatFlag;
	    	String tmpRegisteredString = registeredString;
	    	
	    	// avoid duplicate item names by adding "(n)"
	    	do
	    	{
	    		repeatFlag = false;
	    		
	    		for(int i=0; i< itemList.size(); i++)
	    		{
	    			String n = itemList.get(i).getName();
	    		
	    			if(n.equals(tmpRegisteredString))
	    			{
	    				tmpRegisteredString = registeredString + "(" + new Integer(tmpCount).toString() + ")";
	    				tmpCount = tmpCount + 1;	
	    				repeatFlag = true;
	    				break;
	    			}
	    		}
	    	} while(repeatFlag == true);
	    	
	    	registeredString = tmpRegisteredString; 		    	 	
	    	selectedListItem.setName(registeredString);	  
	    	mbackup.renameFile(oldName, registeredString);
	    	refreshList();
	    	
	    	for(ListItem li : itemList)
	    	{
	    		if(li.getName().equals(registeredString))
	    		{
	    			selectedListItem = li;
	    			selectedListItem.setSelected(true);
	    		}
	    	}
	    	
	    	registeredString = "";	    	
	    }	    
	 }
	
	@Override   
    public boolean onKeyDown(int keyCode, KeyEvent ev)  
    {
    	if(keyCode == KeyEvent.KEYCODE_BACK)
    	{
    		finish();		  
    		  
    		return true;
    	}    	
    	else
    	{
    		return super.onKeyDown(keyCode, ev);
    	}
    }
	
	private void makeGUI()
	{
		main = new LinearLayout(this);
		main.setLayoutParams(new LinearLayout.LayoutParams(displayX,displayY));  
		main.setOrientation(LinearLayout.VERTICAL);
		
		View vfreespace = new View(this);
		vfreespace.setLayoutParams(new LinearLayout.LayoutParams(displayX,12));
		
		mScrollView = new ScrollView(this);
		mScrollView.setLayoutParams(new LinearLayout.LayoutParams(displayX,displayY-145));
		
		listarea = new LinearLayout(this);
		listarea.setOrientation(LinearLayout.VERTICAL);
		listarea.setLayoutParams(new LinearLayout.LayoutParams(displayX,displayY-145));
		
		createList();
		
		mScrollView.addView(listarea);		
		
		buttonrowone = new LinearLayout(this);
		buttonrowone.setLayoutParams(new LinearLayout.LayoutParams(displayX, 60));
		buttonrowone.setOrientation(LinearLayout.HORIZONTAL);
		
		mfreespace[0] = new View(this);
		mfreespace[0].setLayoutParams(new LinearLayout.LayoutParams(110, 60));
		buttonrowone.addView(mfreespace[0]);
		
		row1_button1 = new TextView(this);   
		row1_button1.setGravity(Gravity.CENTER);
		row1_button1.setLayoutParams(new LinearLayout.LayoutParams(100, 60)); 
		row1_button1.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttonyorange));
		row1_button1.setText(R.string.backup_savetosd);
		row1_button1.setTextSize(16);
		row1_button1.setOnClickListener(this);
		row1_button1.setId(SAVE_TO_SD);
		row1_button1.setTextColor(VDKGRAY);
		buttonrowone.addView(row1_button1); 			
		
		//
		freespace = new View(this);
		freespace.setLayoutParams(new LinearLayout.LayoutParams(displayX, 7));
		
		//
		//
		buttonrowtwo = new LinearLayout(this);
		buttonrowtwo.setLayoutParams(new LinearLayout.LayoutParams(displayX, 60));
		buttonrowtwo.setOrientation(LinearLayout.HORIZONTAL);
		buttonrowtwo.setGravity(Gravity.TOP);
		
		mfreespace[1] = new View(this);
		mfreespace[1].setLayoutParams(new LinearLayout.LayoutParams(5, 60));
		buttonrowtwo.addView(mfreespace[1]);
		
		row2_button1 = new TextView(this);   
		row2_button1.setGravity(Gravity.CENTER);
		row2_button1.setLayoutParams(new LinearLayout.LayoutParams(100, 60)); 
		row2_button1.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttonyorange));
		row2_button1.setText(res.getString(R.string.backup_loadfromsd));
		row2_button1.setTextSize(16); 
		row2_button1.setOnClickListener(this); 
		row2_button1.setId(LOAD_FROM_SD);
		row2_button1.setTextColor(VDKGRAY);
		buttonrowtwo.addView(row2_button1); 
		
		horizontalfreespace[3] = new View(this);
		horizontalfreespace[3].setLayoutParams(new LinearLayout.LayoutParams(5, 60));
		buttonrowtwo.addView(horizontalfreespace[3]);
		
		row2_button2 = new TextView(this);   
		row2_button2.setGravity(Gravity.CENTER);
		row2_button2.setLayoutParams(new LinearLayout.LayoutParams(100, 60)); 
		row2_button2.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttony));
		row2_button2.setText(res.getString(R.string.backup_backtooptions));
		row2_button2.setTextSize(16);
		row2_button2.setOnClickListener(this); 
		row2_button2.setId(BACK_TO_OPTIONS);
		row2_button2.setTextColor(VDKGRAY);
		buttonrowtwo.addView(row2_button2);
		
		horizontalfreespace[4] = new View(this);
		horizontalfreespace[4].setLayoutParams(new LinearLayout.LayoutParams(5, 60));
		buttonrowtwo.addView(horizontalfreespace[4]);
		
		row2_button3 = new TextView(this);   
		row2_button3.setGravity(Gravity.CENTER);
		row2_button3.setLayoutParams(new LinearLayout.LayoutParams(100, 60)); 
		row2_button3.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttonyorange));
		row2_button3.setText(res.getString(R.string.backup_deletefile)); 
		row2_button3.setTextSize(16); 
		row2_button3.setOnClickListener(this);
		row2_button3.setId(DELETE_FILE);
		row2_button3.setTextColor(VDKGRAY);
		buttonrowtwo.addView(row2_button3);
		
		main.addView(mScrollView);
		main.addView(vfreespace);
		main.addView(buttonrowone);
		main.addView(freespace);
		main.addView(buttonrowtwo);
				
		setContentView(main);
	}
	
	private void setDisplay() 
    {
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
    	
    	refreshDisplay(); 
    }
    
    public void refreshDisplay()  
    {
    	localDisplay = ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
    	
    	displayX = localDisplay.getWidth();           
    	displayY = localDisplay.getHeight();
    } 
    
    private void toastMessage(String message)
    {
    	messageToast.cancel(); 
		messageToast.setText(message);		
		messageToast.show();
    }
	
	public void onClick(View v)
	{
		if(v.getId() == BACK_TO_OPTIONS)
		{
			finish();
		}
		else if(v.getId() == SAVE_TO_SD)
		{
			if(sdcardready())
			{
				pressed_save();
			}
			else
			{
				QeePinboard.toastr(res.getString(R.string.toast_sdcard_inactive));
			}
		}
		else if(v.getId() == LOAD_FROM_SD)
		{
			if(sdcardready())
			{
				pressed_load();
			}
			else
			{
				QeePinboard.toastr(res.getString(R.string.toast_sdcard_inactive));
			}
		}
		else if(v.getId() == DELETE_FILE)
		{
			if(sdcardready())
			{
				pressed_delete();
			}
			else
			{
				QeePinboard.toastr(res.getString(R.string.toast_sdcard_inactive));
			}
		}
		else if(v.getId() == BACKUP_INFO)
		{
			
		}
	}
	
	// check if format of the future backup file is valid
	private boolean checkFormat(String str)
	{
		StringToStateParser tmpParser = new StringToStateParser(str);
		
		if(tmpParser.parse() == null)
		{
			return false;
		}
		else
		{
			String pa_name = OptionsActivity.staticStateArchive.getCurrentlyActivePinboardArchive().getCurrentlyActivePinboard().getName();
			ArrayList<String> nameslist = OptionsActivity.staticStateArchive.getCurrentlyActivePinboardArchive().getPinboardNames();
			
			for(String mstr : nameslist)
			{
				if(mstr.equals(pa_name))
				{
					return true;
				}
			}
			
			return false;
		}
	}
	
	private void pressed_save()
	{		
		StateToStringTranslator mTranslator = new StateToStringTranslator(OptionsActivity.staticStateArchive);
		String translated_nonchecked = mTranslator.translateToString();
		
		if(checkFormat(translated_nonchecked))
		{
			String tmp = mbackup.save(translated_nonchecked);
			refreshList();
			
			String trimmedstr = "";
			
			if(tmp.endsWith(".sav"))
			{
				try
				{
					trimmedstr = tmp.substring(0, tmp.length()-4);
				}
				catch(Exception ex)
				{
					
				}
			}
			else
			{
				trimmedstr = tmp;
			}
			
			for(ListItem li : itemList)
	    	{
	    		if(li.getName().equals(trimmedstr))
	    		{
	    			selectedListItem = li;
	    			selectedListItem.setSelected(true);
	    		}
	    	}
			
			if(tmp != null)
			{
				toastMessage(res.getString(R.string.toast_savedas) + " " + tmp);
			}
		}
		else
		{
			toastMessage(res.getString(R.string.toast_emptyarchive));  
		}
	}
	
	private void pressed_load()
	{
		if(selectedListItem != null)
		{
			String tmp = selectedListItem.getName()  + ".sav"; 
			selectedListItem = null;
			String loaded_data_raw = mbackup.load(tmp);
			
			if(loaded_data_raw.equals("") || (loaded_data_raw == null))
			{
				return;
			}
			
			StateArchive xStateArchive = null;
			StringToStateParser mparser = new StringToStateParser(loaded_data_raw);			
			
			xStateArchive = mparser.parse();
			
			if(xStateArchive == null)
			{
				toastMessage(tmp + " " + res.getString(R.string.toast_loadingfailure)); 
				refreshList();
				return;
			}
			else
			{
				if(!tmp.equals("autosave.sav"))
				{
					mbackup.autosave();
				}
				
				OptionsActivity.staticStateArchive = xStateArchive;
				OptionsActivity.commitedStateLoad = true;
			
				toastMessage(tmp + " " + res.getString(R.string.toast_loaded)); 
				finish(); 
			}
		}
		else
		{
			toastMessage(res.getString(R.string.toast_nofileselected));
		}
	}
	
	private void pressed_delete()
	{
		if(selectedListItem != null)
		{
			String tmp = selectedListItem.getName(); 
			mbackup.deleteFile(tmp);
			toastMessage(tmp + ".sav " + res.getString(R.string.toast_deleted));
			itemList.remove(selectedListItem);
			selectedListItem = null;
			
			refreshList();
		}
		else
		{
			toastMessage(res.getString(R.string.toast_nofileselected));
		}
	}
	
	private void createList()
	{
		String[] flist = mbackup.getBackupFilesList();
		
		if(flist == null)
		{
			return;
		}
		
		View listfreespace = new View(this);
		listfreespace.setLayoutParams(new LinearLayout.LayoutParams(displayX,10));
		listarea.addView(listfreespace);
		
		for(int i=0; i< flist.length; i++)
        {
			String tmp = flist[i];	
			ListItem li = null;
			
			if(tmp.endsWith(".sav"))
			{
				try
				{
					tmp = tmp.substring(0, tmp.length()-4);
				}
				catch(Exception ex)
				{
					
				}
			}
			
			if(!tmp.equals("info.txt"))
			{
				li = new ListItem(this,tmp);	    	
				listarea.addView(li);
				itemList.add(li);
			}
			
			if(selectedListItem != null)
			{
				if(selectedListItem.getName().equals(tmp))
				{
					selectedListItem.setSelected(true);
				}
			}	
		}
	}
	
	private void refreshList()
	{
		listarea.removeAllViews();
		createList();		
	}
	
	private boolean sdcardready()
	{
		String state = Environment.getExternalStorageState();
		
		if (Environment.MEDIA_MOUNTED.equals(state))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
    {  
		pendingListItem = null;
		pendingRename = null;
    	return true; 
    }
    
    public boolean onDown(MotionEvent e)
    {
    	pendingListItem = null;
    	pendingRename = null;
    	return true;
    }
    
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
    {
    	pendingListItem = null;
    	pendingRename = null;
    	return true;   
    }
    
    public void onLongPress(MotionEvent e)         
    {    
    	pendingListItem = null;
    	pendingRename = null;
    }
    
    public void onShowPress(MotionEvent e) 
    {  
    	pendingListItem = null;
    	pendingRename = null;
    }    
    
    public boolean onSingleTapUp(MotionEvent e)    
    {  
    	if(pendingRename != null)
    	{
    		selectedRename = pendingRename;
    		pendingRename = null;
    	}
    	
    	if(pendingListItem != null)
    	{
    		selectedListItem = pendingListItem;
    		pendingListItem = null;
    	}
    	
    	for(ListItem lit : itemList)
		{
			if(lit.equals(selectedListItem))
			{
				lit.setSelected(true);    			
			}
			else
			{
				lit.setSelected(false);
			}
		 }
    	
    	if(selectedRename != null)
    	{
    		selectedRename = null;
    		
    		// open input editor for renaming the selected pinboard
    		Intent mIntent = new Intent(); 
    		mIntent.setClass(this, PinboardLauncher.class);			
    		mIntent.putExtra("text", selectedListItem.getName());
    		mIntent.putExtra("checkorientation", 1);    			
    		
    		mIntent.putExtra("mode", RENAME_BACKUP);       	
    			
    		startActivity(mIntent);
    	}
    	
    	return true;
    }
    
    class ListItem extends LinearLayout
    {
    	private Context context;
    	private LinearLayout holder;
    	private TextView itemview;
    	private View freespace;
    	private View freespace1;
    	private RenameLabel renamelabel;
    	
    	private String filestring;
    	private boolean selected = false;
    	private boolean archive = false; // indicates whether this list item represents pinboard archive or pinboard
    	
    	public ListItem(Context context, String filestring)
    	{
    		super(context);
    		this.context = context;
    		this.filestring = filestring;
    		
    		setLayoutParams(new LinearLayout.LayoutParams(displayX, 55));
    		setOrientation(LinearLayout.VERTICAL);
    		
    		View fspace1 = new View(context);
    		fspace1.setLayoutParams(new LinearLayout.LayoutParams(displayX, 5));
    		addView(fspace1);
    		
    		holder = new LinearLayout(context);
    		holder.setLayoutParams(new LinearLayout.LayoutParams(displayX, 55));
    		holder.setGravity(Gravity.CENTER);
    		holder.setOrientation(LinearLayout.HORIZONTAL);
    		
    		itemview = new TextView(context);
    		itemview.setLayoutParams(new LinearLayout.LayoutParams(205, 50));
    		itemview.setText(filestring);
    		itemview.setBackgroundDrawable(res.getDrawable(R.drawable.nameview_three));
    		itemview.setGravity(Gravity.CENTER);
    		itemview.setTextColor(VDKGRAY);
    		itemview.setTypeface(Typeface.DEFAULT_BOLD); 
    		itemview.setTextSize(16);
    		holder.addView(itemview);
    		
    		freespace = new View(context);
    		freespace.setLayoutParams(new LinearLayout.LayoutParams(10, 50));
    		holder.addView(freespace);
    		
    		renamelabel = new RenameLabel(context,this);
    		renamelabel.setLayoutParams(new LinearLayout.LayoutParams(80, 44));
    		renamelabel.setBackgroundDrawable(res.getDrawable(R.drawable.renamelabel));
    		renamelabel.setGravity(Gravity.CENTER);
    		renamelabel.setTextColor(VDKGRAY); 
    		renamelabel.setText(res.getString(R.string.options_rename));  
    		renamelabel.setTextSize(15);
    		holder.addView(renamelabel);
    		
    		addView(holder);
    	}
    	
    	@Override
	    public boolean onTouchEvent(MotionEvent me) 
	    {   
    		// selectedListItem = this; 
    		pendingListItem = this;
	    	gestureScanner.onTouchEvent(me); 
	    	return true;    	    	
	    }
    	
    	public boolean isArchive()
    	{
    		return archive;
    	}
    	
    	public void setArchive(boolean archive)
    	{
    		this.archive = archive;
    	}
    	
    	public boolean isSelected()
    	{
    		return selected;
    	}
    	
    	public ListItem getListItem()
    	{
    		return this;
    	}
    	
    	public void setName(String filestring)
    	{
    		this.filestring = filestring;
    		itemview.setText(filestring);
    	}
    	
    	public String getName()
    	{
    		return filestring;
    	}
    	
    	public void setSelected(boolean selected)
    	{
    		this.selected = selected;
    		
    		if(archive == true)
    		{
    			if(selected)
    			{
    				itemview.setBackgroundDrawable(res.getDrawable(R.drawable.stateview)); 
    				renamelabel.setBackgroundDrawable(res.getDrawable(R.drawable.stateview));    			
    			}
    			else
    			{
    				itemview.setBackgroundDrawable(res.getDrawable(R.drawable.nameview_three)); 
    				renamelabel.setBackgroundDrawable(res.getDrawable(R.drawable.renamelabel));
    			}
    		}
    		else
    		{
    			if(selected)
    			{
    				itemview.setBackgroundDrawable(res.getDrawable(R.drawable.nameview)); 
    				renamelabel.setBackgroundDrawable(res.getDrawable(R.drawable.nameview));    			
    			}
    			else
    			{
    				itemview.setBackgroundDrawable(res.getDrawable(R.drawable.nameview_three)); 
    				renamelabel.setBackgroundDrawable(res.getDrawable(R.drawable.renamelabel));
    			}
    		}
    	}
    }
    
    class RenameLabel extends TextView
    {   
    	private ListItem holder;
    	
    	public RenameLabel(Context context, ListItem holder)
    	{
    		super(context);
    		this.holder = holder;
    	}
    	
    	@Override
	    public boolean onTouchEvent(MotionEvent me) 
	    {   
    		pendingRename = this;
    		pendingListItem = holder;
    		gestureScanner.onTouchEvent(me);
	    	return true;   	    	
	    }
    }	
}
