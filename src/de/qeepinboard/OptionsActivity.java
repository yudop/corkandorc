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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.*;

public class OptionsActivity extends Activity implements OnGestureListener, View.OnClickListener
{
	public static final int OPEN_PINBOARD   = 0;
	public static final int ADD_PINBOARD    = 1;
	public static final int DELETE_PINBOARD = 2;
	public static final int SAVE_STATE      = 3;
	public static final int LOAD_STATE      = 4;
	public static final int BACKUP_STATE    = 5;
	public static final int COPY_BOARD      = 6;
	public static final int BACKUP_MANAGER  = 7;  
	
	public static final int RENAME_PINBOARD = 4;
	public static final int RENAME_STATE    = 5;
	
	private static final int VDKGRAY = Color.rgb(0x33,0x33,0x33);
	private static final boolean PINBOARD = true;
	private static final boolean STATE = false;
	
	public static boolean backgroundFlag = false;
	
	private Resources res;
	
	private GestureDetector gestureScanner;
	private Display localDisplay; 
    public static int displayX;
    public static int displayY;
    
    public static String registeredString = "";
    public static boolean commitedPinboardRename = false;
    public static boolean commitedStateRename = false;
    public static boolean commitedStateLoad = false; // backed up state loaded from BackupActivity
    
    private boolean viewFlag = PINBOARD;
    
    private LinearLayout main;
    private FrameLayout holder;
    private ScrollView mScrollView;
    private ScrollView archiveScrollView;
    private LinearLayout listarea;
    private LinearLayout archivelistarea;
    private LinearLayout buttonrowone;
    private View freespace;
    private View[] mfreespace = new View[2];
    private View[] horizontalfreespace = new View[15];
    private LinearLayout buttonrowtwo;
    
    private Toast messageToast;
    
    private TextView row1_button1;
    private TextView row1_button2;
    private TextView row1_button3;
    private TextView row2_button1;
    private TextView row2_button2;
    private TextView row2_button3;
    
    private ArrayList<ListItem> pinboardList;
    private ArrayList<ListItem> archiveList;
    private ListItem selectedListItem; // list item, that has just been pressed by the user
    private RenameLabel selectedRename; // will be initialized if a rename label has been selected, otherwise null
    private ListItem pendingListItem; // list item that has absorbed an unfiltered touch event
    private RenameLabel pendingRename;
    
    private StateArchive mStateArchive;
	private PinboardArchive mPinboardArchive;
	private ArrayList<String> pinboardNames;
	private ArrayList<String> archiveNames;
	private PinboardItem mPinboardItem; 
	
	private TaskReceiver taskreceiver;
	
	public static StateArchive staticStateArchive;
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        gestureScanner = new GestureDetector(this);
        res = getResources();
        messageToast = Toast.makeText(this, "", 500);
        messageToast.setGravity(Gravity.CENTER, 0, -60);
        mStateArchive = PinboardManager.staticStateArchive;
        
        mPinboardArchive = PinboardManager.staticPinboardArchive;
        mStateArchive.setCurrentlyActivePinboardArchive(mPinboardArchive);
        mPinboardItem = mPinboardArchive.getCurrentlyActivePinboard().clone();
        archiveNames = mStateArchive.getArchiveNames();
        pinboardNames = mPinboardArchive.getPinboardNames();        
        pinboardList = new ArrayList<ListItem>();
        archiveList = new ArrayList<ListItem>();  
        cloneStateItems();
        
        taskreceiver = new TaskReceiver();
		
        IntentFilter filter = new IntentFilter();
		filter.addAction("do task");
		registerReceiver(taskreceiver, filter);
		
        setDisplay();
        makeGUI();
    }
	
	 @Override
	 public void onResume()
	 {
	    super.onResume();
	    
	    // returning from AdvancedEditing Activity (renamed pin board)
	    if(commitedPinboardRename == true)
	    {
	    	commitedPinboardRename = false;
	    	String oldName = selectedListItem.getName();
	    	PinboardItem pItem = mPinboardArchive.get(oldName);	  
	    	
	    	int tmpCount = 1;
	    	boolean repeatFlag;
	    	String tmpRegisteredString = registeredString;
	    	
	    	// avoid duplicate item names by adding "(n)"
	    	do
	    	{
	    		repeatFlag = false;
	    		
	    		for(int i=0; i< pinboardNames.size(); i++)
	    		{
	    			String n = pinboardNames.get(i);
	    		
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
	    	
	    	for(int i=0; i< pinboardNames.size(); i++)
    		{
	    		String n = pinboardNames.get(i);
	    		
	    		if(n.equals(oldName))
	    		{
	    			pinboardNames.set(i, registeredString);
	    			break;
	    		}
	    	}	
	    	 	
	    	selectedListItem.setName(registeredString);
	    	pItem.setName(registeredString);
	    	mPinboardArchive.remove(oldName);
	    	mPinboardArchive.put(pItem);
	    	mPinboardArchive.setPinboardNames(pinboardNames);
	    	mPinboardArchive.setCurrentlyActivePinboard(pItem);
	    	
	    	registeredString = "";	    	
	    	
	    }
	    // returning from AdvancedEditing Activity (renamed state)
	    else if(commitedStateRename == true)
	    {
	    	commitedStateRename = false;
	    	
	    	String oldName = selectedListItem.getName();
	    	PinboardArchive pArchive = mStateArchive.get(oldName);	  
	    	
	    	int tmpCount = 1;
	    	boolean repeatFlag;
	    	String tmpRegisteredString = registeredString; 
	    	
	    	// avoid duplicate item names by adding "(n)" 
	    	do
	    	{
	    		repeatFlag = false;
	    		
	    		for(int i=0; i< archiveNames.size(); i++)
	    		{
	    			String n = archiveNames.get(i);
	    		
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
	    	
	    	for(int i=0; i< archiveNames.size(); i++)
    		{
	    		String n = archiveNames.get(i);
	    		
	    		if(n.equals(oldName))
	    		{
	    			archiveNames.set(i, registeredString);
	    			break;
	    		}
	    	}	
	    	
	    	selectedListItem.setName(registeredString);
	    	pArchive.setName(registeredString);
	    	mStateArchive.remove(oldName);
	    	mStateArchive.put(pArchive);
	    	mStateArchive.setArchiveNames(archiveNames);
	    	mStateArchive.setCurrentlyActivePinboardArchive(pArchive);
	    	
	    	registeredString = "";
	    }
	    // returning from BackupActivity; one of the backed up state is loaded from SD card
	    else if(commitedStateLoad == true)
	    {
	    	commitedStateLoad = false;
	    	
	    	mStateArchive = staticStateArchive; 
	    	
	    	mPinboardArchive = mStateArchive.getCurrentlyActivePinboardArchive();	      
	        mPinboardItem = mPinboardArchive.getCurrentlyActivePinboard().clone();
	        archiveNames = mStateArchive.getArchiveNames();
	        pinboardNames = mPinboardArchive.getPinboardNames();        
	        pinboardList = new ArrayList<ListItem>();
	        archiveList = new ArrayList<ListItem>();
	        cloneStateItems();
	        
	        if(main != null)
	        {
	        	main.removeAllViews();
	        }
	        
	        makeGUI();
	    }
	}
	 
	@Override
    public void onStop()
	{
		if(backgroundFlag == false)
		{  
			mPinboardArchive.setCurrentlyActivePinboard(mPinboardItem.clone());
			
			// if pinboard archive is empty and user presses HOME ##patch for fatal bug
			if(mPinboardArchive.getPinboardNames().isEmpty())
			{
				mPinboardArchive.put(mPinboardItem.clone());
			}
			else
			{
				// check if active pinboard valid or not; replace it if invalid
				String chkstr = mPinboardArchive.getCurrentlyActivePinboard().getName();
				ArrayList<String> chklist = mPinboardArchive.getPinboardNames();
				
				if(!chklist.contains(chkstr))
				{
					mPinboardArchive.setCurrentlyActivePinboard(chklist.get(0));
				}
			}
			
			mStateArchive.setCurrentlyActivePinboardArchive(mPinboardArchive);	
						
			storeInMainThread();
		}
		else
		{
			backgroundFlag = false;
		}
		
		super.onStop();
	} 
	
	@Override   
    public boolean onKeyDown(int keyCode, KeyEvent ev)  
    {
    	if(keyCode == KeyEvent.KEYCODE_BACK)
    	{
    		if(!viewFlag)
    		{
    			pressed_backToOptions();
    		}
    		else
    		{
    			setStatesAndReturn(); 
    		}    		  
    		  
    		return true;
    	}
    	else if(keyCode == KeyEvent.KEYCODE_MENU)
    	{
    		setStatesAndReturn();
    		return true;
    	}
    	else
    	{
    		return super.onKeyDown(keyCode, ev);
    	}
    }
	
	private void cloneStateItems()
	{
		PinboardArchive pa = mStateArchive.getCurrentlyActivePinboardArchive().clone();
		mStateArchive.setCurrentlyActivePinboardArchive(pa);
		
		ArrayList<String> an = mStateArchive.getArchiveNames();
		
		for(int i=0; i < an.size(); i++)
		{
			PinboardArchive pa2 = mStateArchive.get(an.get(i));
			PinboardArchive pa2Clone = pa2.clone();
			mStateArchive.softDeleteArchive(pa2);
			mStateArchive.put(pa2Clone);
		}
	}
	
	private void makeGUI()
	{		
		main = new LinearLayout(this);
		main.setLayoutParams(new LinearLayout.LayoutParams(displayX,displayY));  
		main.setOrientation(LinearLayout.VERTICAL);
		
		holder = new FrameLayout(this);
		holder.setLayoutParams(new LinearLayout.LayoutParams(displayX,displayY-145));
		
		View vfreespace = new View(this);
		vfreespace.setLayoutParams(new LinearLayout.LayoutParams(displayX,12));
		
		// createArchiveScrollView();		
		createPinboardScrollView();
		
		//
		//
		
		buttonrowone = new LinearLayout(this);
		buttonrowone.setLayoutParams(new LinearLayout.LayoutParams(displayX, 60));
		buttonrowone.setOrientation(LinearLayout.HORIZONTAL);
		
		mfreespace[0] = new View(this);
		mfreespace[0].setLayoutParams(new LinearLayout.LayoutParams((int)((displayX-205)/2), 60));
		buttonrowone.addView(mfreespace[0]);
		
		row1_button1 = new TextView(this);   
		row1_button1.setGravity(Gravity.CENTER);
		row1_button1.setLayoutParams(new LinearLayout.LayoutParams(100, 60)); 
		row1_button1.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttony));
		row1_button1.setText(R.string.options_openboard);
		row1_button1.setTextSize(16);
		row1_button1.setOnClickListener(this);
		row1_button1.setId(OPEN_PINBOARD);
		row1_button1.setTextColor(VDKGRAY);
		buttonrowone.addView(row1_button1); 
		
		horizontalfreespace[0] = new View(this);
		horizontalfreespace[0].setLayoutParams(new LinearLayout.LayoutParams(5, 60));
		buttonrowone.addView(horizontalfreespace[0]); 
		
		row1_button2 = new TextView(this);   
		row1_button2.setGravity(Gravity.CENTER);
		row1_button2.setLayoutParams(new LinearLayout.LayoutParams(100, 60)); 
		row1_button2.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttony));
		row1_button2.setText(res.getString(R.string.options_addboard));  
		row1_button2.setTextSize(16);
		row1_button2.setOnClickListener(this);
		row1_button2.setId(ADD_PINBOARD);
		row1_button2.setTextColor(VDKGRAY);
		buttonrowone.addView(row1_button2);
		
		/*
		horizontalfreespace[1] = new View(this);
		horizontalfreespace[1].setLayoutParams(new LinearLayout.LayoutParams(5, 60)); 
		buttonrowone.addView(horizontalfreespace[1]); 
		
		row1_button3 = new TextView(this);   
		row1_button3.setGravity(Gravity.CENTER);
		row1_button3.setLayoutParams(new LinearLayout.LayoutParams(100, 60)); 
		row1_button3.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttony));
		row1_button3.setText(R.string.options_deleteboard);
		row1_button3.setTextSize(16);
		row1_button3.setOnClickListener(this);
		row1_button3.setId(DELETE_PINBOARD);
		row1_button3.setTextColor(VDKGRAY);
		// bottom_one.setTypeface(Typeface.DEFAULT_BOLD);
		// row1_button3.setShadowLayer(1, 1, 1, Color.WHITE);
		row1_button3.setVisibility(View.INVISIBLE);
		buttonrowone.addView(row1_button3); 
		*/
		
		//
		freespace = new View(this);
		freespace.setLayoutParams(new LinearLayout.LayoutParams(displayX, 7));
		
		//
		//
		buttonrowtwo = new LinearLayout(this);
		buttonrowtwo.setLayoutParams(new LinearLayout.LayoutParams(displayX, 60));
		buttonrowtwo.setOrientation(LinearLayout.HORIZONTAL);
		
		mfreespace[1] = new View(this);
		mfreespace[1].setLayoutParams(new LinearLayout.LayoutParams(5, 60));
		buttonrowtwo.addView(mfreespace[1]);
		
		row2_button1 = new TextView(this);   
		row2_button1.setGravity(Gravity.CENTER);
		row2_button1.setLayoutParams(new LinearLayout.LayoutParams(100, 60)); 
		row2_button1.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttony));
		row2_button1.setText(res.getString(R.string.options_copyboard));
		row2_button1.setTextSize(16);
		row2_button1.setOnClickListener(this); 
		row2_button1.setId(COPY_BOARD);
		row2_button1.setTextColor(VDKGRAY);
		buttonrowtwo.addView(row2_button1); 
		
		horizontalfreespace[3] = new View(this);
		horizontalfreespace[3].setLayoutParams(new LinearLayout.LayoutParams(5, 60));
		buttonrowtwo.addView(horizontalfreespace[3]);
		
		row2_button2 = new TextView(this);   
		row2_button2.setGravity(Gravity.CENTER);
		row2_button2.setLayoutParams(new LinearLayout.LayoutParams(100, 60)); 
		row2_button2.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttonyorange));
		row2_button2.setText(res.getString(R.string.options_backupmanager));
		row2_button2.setTextSize(16);
		row2_button2.setOnClickListener(this);
		row2_button2.setId(BACKUP_MANAGER);
		row2_button2.setTextColor(VDKGRAY);
		buttonrowtwo.addView(row2_button2);
		
		horizontalfreespace[4] = new View(this);
		horizontalfreespace[4].setLayoutParams(new LinearLayout.LayoutParams(5, 60));
		buttonrowtwo.addView(horizontalfreespace[4]);
		
		row2_button3 = new TextView(this);   
		row2_button3.setGravity(Gravity.CENTER);
		row2_button3.setLayoutParams(new LinearLayout.LayoutParams(100, 60)); 
		row2_button3.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttony));
		row2_button3.setText(res.getString(R.string.options_deleteboard)); 
		row2_button3.setTextSize(16);
		row2_button3.setOnClickListener(this);
		row2_button3.setId(DELETE_PINBOARD);
		row2_button3.setTextColor(VDKGRAY);
		buttonrowtwo.addView(row2_button3);
		
		//
		main.addView(holder);
		main.addView(vfreespace);
		main.addView(buttonrowone);
		main.addView(freespace);
		main.addView(buttonrowtwo);
		
		setContentView(main);
	}
	
	private void createPinboardScrollView()
	{
		mScrollView = new ScrollView(this);
		mScrollView.setLayoutParams(new LinearLayout.LayoutParams(displayX,displayY-145));
		mScrollView.setVisibility(View.VISIBLE);
		
		listarea = new LinearLayout(this);
		listarea.setOrientation(LinearLayout.VERTICAL);
		listarea.setLayoutParams(new LinearLayout.LayoutParams(displayX,displayY-145));
		
		View listfreespace = new View(this);
		listfreespace.setLayoutParams(new LinearLayout.LayoutParams(displayX,10));
		listarea.addView(listfreespace);
		
		for(int i=0; i< pinboardNames.size();i++)
        {
			String tmp = pinboardNames.get(i);
			PinboardItem pItem = mPinboardArchive.get(tmp);
			ListItem li = new ListItem(this,tmp);
			
			if(pItem.getName().equals(mPinboardArchive.getCurrentlyActivePinboard().getName()))
			{
				selectedListItem = li;
				determineActiveListItem(false);
				li.setSelected(true);
			}
			
        	pinboardList.add(li);
			listarea.addView(li);
        }
		
		mScrollView.addView(listarea);
		
		holder.addView(mScrollView);
	}
	
	private void createArchiveScrollView()
	{
		archiveScrollView = new ScrollView(this);
		archiveScrollView.setLayoutParams(new LinearLayout.LayoutParams(displayX,displayY-145));
		archiveScrollView.setVisibility(View.INVISIBLE);
		
		archivelistarea = new LinearLayout(this);
		archivelistarea.setOrientation(LinearLayout.VERTICAL);
		archivelistarea.setLayoutParams(new LinearLayout.LayoutParams(displayX,displayY-145));
		
		View archivelistfreespace = new View(this);
		archivelistfreespace.setLayoutParams(new LinearLayout.LayoutParams(displayX,10));
		archivelistarea.addView(archivelistfreespace);
		
		for(int i=0; i< archiveNames.size();i++)
        {
			String tmp = archiveNames.get(i);
			PinboardArchive pArchive = mStateArchive.get(tmp);
			ListItem li = new ListItem(this,tmp);
			li.setArchive(true);
			
			if(pArchive.getName().equals(mStateArchive.getCurrentlyActivePinboardArchive().getName()))
			{
				selectedListItem = li;
				determineActiveListItem(true); 
				li.setSelected(true);
			}
			
        	archiveList.add(li);
        	archivelistarea.addView(li);
        }         
		
		archiveScrollView.addView(archivelistarea);
		holder.addView(archiveScrollView);
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
    
    private void setStatesAndReturn()
    {
    	backgroundFlag = true;
    	
    	cloneStateItems(); 
    	PinboardArchive cPinboardArchive = mPinboardArchive;
    	
    	PinboardItem tmpItem = cPinboardArchive.getCurrentlyActivePinboard();
    	toastMessage(tmpItem.getName());
    	
    	mStateArchive.setCurrentlyActivePinboardArchive(cPinboardArchive); 
    	PinboardManager.staticStateArchive = mStateArchive;
    	PinboardManager.staticPinboardArchive = cPinboardArchive;
    	PinboardManager.restoreFromOptionsActivity = true;
    	
    	finish(); 
    }
    
    private void storeInMainThread()
    {
    	StateToStringTranslator ms = new StateToStringTranslator(mStateArchive);		
		String mStr = ms.translateToString();
    	
    	String sdpath = "";
		
		String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state))
	    {
	    	sdpath = Environment.getExternalStorageDirectory().getPath() + "/corkandorc/" + QeePinboard.STORAGE_FILE_STR ;
	    	
	    	FileOutputStream fileOut = null;
			BufferedOutputStream bout = null;
			PrintWriter pwriter = null;			
			
			try
	    	{    					
				fileOut = new FileOutputStream(sdpath);
		    	bout = new BufferedOutputStream(fileOut);
		    	pwriter = new PrintWriter(bout);
		    	pwriter.print(mStr);
		    	pwriter.close();	    		
	    	}
	    	catch(Exception ex)
	    	{ 	
	    		 
	    	}    
	    	finally
	    	{		    			    		
	    		if(pwriter != null)
		    	{	    		
		    		pwriter.flush();	    		
		    	}	  		
	    	} 
	    }
    	
	    FileOutputStream fileOut = null;
		BufferedOutputStream bout = null;
		PrintWriter pwriter = null;
    	
    	try
    	{    	
    		fileOut = openFileOutput(QeePinboard.STORAGE_FILE_STR, Activity.MODE_PRIVATE);			
	    	bout = new BufferedOutputStream(fileOut);
	    	pwriter = new PrintWriter(bout);
	    	pwriter.print(mStr);
	    	pwriter.close();  			    		
    	}
    	catch(Exception ex)
    	{ 	
    		 
    	}    
    	finally
    	{		    		
    		if(pwriter != null)
	    	{	    		
	    		pwriter.flush();	    		
	    	}			    		
    	} 	    	
	} 
    
    //
    // for buttons
    public void onClick(View v)
	{
    	if(v.getId() == OPEN_PINBOARD)
		{
    		if(viewFlag)
    		{
    			pressed_openPinboard();
    		}
    		else
    		{
    			// SAVE STATE
    			pressed_saveState();    			
    		}
		}
    	else if(v.getId() == ADD_PINBOARD)
		{   
    		if(viewFlag)
    		{
    			pressed_addPinboard();      			
    		}
    		else
    		{
    			// LOAD STATE
    			pressed_loadState();
    		}
		}
    	else if(v.getId() == DELETE_PINBOARD)
		{
    		if(viewFlag)
    		{
    			pressed_deletePinboard();  
    		}
    		else
    		{
    			pressed_deleteState();
    		}
		}
    	else if(v.getId() == COPY_BOARD)
		{
    		pressed_copyPinboard();
		}
    	else if(v.getId() == BACKUP_MANAGER)
		{
    		String state = Environment.getExternalStorageState();
    	    if (Environment.MEDIA_MOUNTED.equals(state))
    	    {
    	    	staticStateArchive = mStateArchive;
    	    	
    	    	Intent mIntent = new Intent(); 
    	    	mIntent.setClass(this, BackupActivity.class);   			
    	    	startActivity(mIntent);
    	    }
    	    else
    	    {
    	    	QeePinboard.toastr(res.getString(R.string.toast_sdcard_inactive));  
    	    }
		}
    	else if(v.getId() == SAVE_STATE)
		{
    		
		}
    	else if(v.getId() == LOAD_STATE)
		{
    		
		}
    	else if(v.getId() == BACKUP_STATE)
		{
    		if(viewFlag)
    		{
    			pressed_backupState();
    		}
    		else
    		{
    			pressed_backToOptions();
    		}
		}
	}
    
    private void pressed_openPinboard()
    {
    	setStatesAndReturn();
    }
    
    private void pressed_addPinboard()
    {
    	PinboardItem tmpItem = mPinboardArchive.addDefaultPinboard();
		pinboardNames = mPinboardArchive.getPinboardNames();
		
		listarea.removeAllViews();
		pinboardList.clear();
		
		View listfreespace = new View(this);
		listfreespace.setLayoutParams(new LinearLayout.LayoutParams(displayX,10));
		listarea.addView(listfreespace);
		 		
		for(int i=0; i< pinboardNames.size();i++)
		{
			String tmp = pinboardNames.get(i);
			PinboardItem pItem = mPinboardArchive.get(tmp);  
			
			ListItem li = new ListItem(this,tmp);
			pinboardList.add(li);
			listarea.addView(li);
			
			if(pinboardNames.size() == 1)
			{
				mPinboardArchive.setCurrentlyActivePinboard(pItem);
			}
			
			if(pItem.getName().equals(mPinboardArchive.getCurrentlyActivePinboard().getName()))
			{
				selectedListItem = li;				
				determineActiveListItem(false);				
			}			
		}
		
		toastMessage(res.getString(R.string.toast_pinboard_added));
    }
    
    private void pressed_copyPinboard()
    {
    	if(pinboardList.isEmpty())
		{
			return;
		}
    	
    	PinboardItem tmpItem = (mPinboardArchive.get(selectedListItem.getName()).clone());
    	
    	int tmpCount = 1;
    	boolean repeatFlag;
    	String nameString = tmpItem.getName();       
    	
    	String tmpNameString = nameString;   	
    	
    	// avoid duplicate item names by adding "(n)"
    	do
    	{
    		repeatFlag = false;
    		
    		for(int i=0; i< pinboardNames.size(); i++)
    		{
    			String n = pinboardNames.get(i); 
    		
    			if(n.equals(tmpNameString))
    			{
    				tmpNameString = nameString + "(" + new Integer(tmpCount).toString() + ")";
    				tmpCount = tmpCount + 1;	
    				repeatFlag = true;
    				break;
    			}
    		}
    	} while(repeatFlag == true);
    	
    	tmpItem.setName(tmpNameString);
    	
		pinboardNames = mPinboardArchive.getPinboardNames();
		mPinboardArchive.put(tmpItem);
	
		listarea.removeAllViews();
		pinboardList.clear();
		
		View listfreespace = new View(this);
		listfreespace.setLayoutParams(new LinearLayout.LayoutParams(displayX,10));
		listarea.addView(listfreespace);
		 		
		for(int i=0; i< pinboardNames.size();i++)
		{
			String tmp = pinboardNames.get(i);
			PinboardItem pItem = mPinboardArchive.get(tmp);  
			
			ListItem li = new ListItem(this,tmp);
			pinboardList.add(li);
			listarea.addView(li);
			
			if(pinboardNames.size() == 1)
			{
				mPinboardArchive.setCurrentlyActivePinboard(pItem);
			}
			
			if(pItem.getName().equals(mPinboardArchive.getCurrentlyActivePinboard().getName()))
			{
				selectedListItem = li;
				determineActiveListItem(false);
			}
		}
		
		toastMessage(nameString + " " + res.getString(R.string.toast_copied)); 
    }
    
    private void pressed_deletePinboard()
    {    	
    	if(pinboardList.isEmpty())  
		{
			return;
		}
		
		int tmp = -1;
		
		for(int i=0; i< pinboardList.size(); i++)
		{
			ListItem lit = pinboardList.get(i);
			
			if(lit.equals(selectedListItem))
			{
				toastMessage("'" + selectedListItem.getName() + "' " + res.getString(R.string.toast_deleted)); 
				
				mPinboardArchive.deletePinboard(mPinboardArchive.get(selectedListItem.getName()));
				pinboardNames = mPinboardArchive.getPinboardNames();
				
    			listarea.removeView(lit);
  
    			lit.setSelected(false);
    			tmp = i;
			}
		}
		
		if(tmp != -1)
		{
			pinboardList.remove(tmp);
			pinboardList.trimToSize();
		}
		
		if(pinboardList.isEmpty())
		{
			return;
		}
		
		ListItem li = pinboardList.get(0);
			
		if(li != null)
		{
			selectedListItem = li;
			li.setSelected(true);
			mPinboardArchive.setCurrentlyActivePinboard(li.getName());
		}
    }
    
    private void pressed_saveState()
    {    	
    	cloneStateItems();
    	PinboardArchive mArchive = mStateArchive.addDefaultPinboardArchive(mPinboardArchive); 
    	mPinboardArchive = mPinboardArchive.clone();
    	mStateArchive.makeFirstItemActive();    		
    	toastMessage("saved as " + "'" + mArchive.getName() + "'");    	
    	
    	archiveNames = mStateArchive.getArchiveNames();
    	
		archivelistarea.removeAllViews();
		archiveList.clear();
		
		View listfreespace = new View(this);
		listfreespace.setLayoutParams(new LinearLayout.LayoutParams(displayX,10));
		archivelistarea.addView(listfreespace);		
		
		for(int k=0; k< archiveNames.size();k++)
		{
			String tmp = archiveNames.get(k); 
			PinboardArchive pArchive = mStateArchive.get(tmp).clone();  
			
			ListItem li = new ListItem(this,tmp);
			li.setArchive(true);
			archiveList.add(li);
			archivelistarea.addView(li);
			
			if(archiveNames.size() == 1)
			{
				mStateArchive.setCurrentlyActivePinboardArchive(pArchive);
			}
			
			if(pArchive.getName().equals(mStateArchive.getCurrentlyActivePinboardArchive().getName()))
			{
				selectedListItem = li;
				determineActiveListItem(true);
			}
		}
		
		cloneStateItems();
    }
    
    private void delayedListViewCreation()
    {
    	archiveNames = mStateArchive.getArchiveNames();
    	
		archivelistarea.removeAllViews();
		archiveList.clear();
		
		View listfreespace = new View(this);
		listfreespace.setLayoutParams(new LinearLayout.LayoutParams(displayX,10));
		archivelistarea.addView(listfreespace);		
		
		for(int k=0; k< archiveNames.size();k++)
		{
			String tmp = archiveNames.get(k); 
			PinboardArchive pArchive = mStateArchive.get(tmp).clone();  
			
			ListItem li = new ListItem(this,tmp);
			li.setArchive(true);
			archiveList.add(li);
			archivelistarea.addView(li);
			
			if(archiveNames.size() == 1)
			{
				mStateArchive.setCurrentlyActivePinboardArchive(pArchive);
			}
			
			if(pArchive.getName().equals(mStateArchive.getCurrentlyActivePinboardArchive().getName()))
			{
				selectedListItem = li;
				determineActiveListItem(true);
			}
		}
		
		cloneStateItems();
    }
    
    private void pressed_loadState()
    {  
    	if(mStateArchive.getArchiveNames().isEmpty())
    	{
    		toastMessage("no states available");
    	}
    	else if(mStateArchive.get(selectedListItem.getName()) == null)
    	{
    		toastMessage("no state is selected");
    	}
    	else
    	{
    		// pressed_saveState(true);
    		
    		cloneStateItems();    		
    		PinboardArchive pArchive = mStateArchive.get(selectedListItem.getName()).clone();
    		mStateArchive.setCurrentlyActivePinboardArchive(pArchive);
    		mPinboardArchive = pArchive;
    		mPinboardArchive.setName(pArchive.getName());
    		mPinboardArchive.setPinboardNames(pArchive.getPinboardNames());
    		mPinboardArchive.setCurrentlyActivePinboard(pArchive.getCurrentlyActivePinboard().clone());
    		pinboardNames = pArchive.getPinboardNames();
    		cloneStateItems();
    		
    		toastMessage("-" + pArchive.getName() + " restored-");
    	
    		pressed_backToOptions(); 
    	}
    }
    
    private void pressed_backupState()
    {
    	viewFlag = STATE;
		mScrollView.setVisibility(View.INVISIBLE);
		archiveScrollView.setVisibility(View.VISIBLE);			
		
		// row1_button2.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttonyorange));			
		row2_button1.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttonyorange));
		row2_button2.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttonyorange));
		row2_button3.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttonyorange));
	    
		row2_button1.setText(res.getString(R.string.options_savestate)); 
	    row2_button2.setText(res.getString(R.string.options_restorestate));
	    row2_button3.setText(res.getString(R.string.options_deletestate));
	    // row1_button2.setText(res.getString(R.string.options_backtooptions));
	    
	    archivelistarea.removeAllViews();
	    archiveList.clear();
	    
	    View listfreespace = new View(this);
		listfreespace.setLayoutParams(new LinearLayout.LayoutParams(displayX,10));
		archivelistarea.addView(listfreespace);
	    
	    for(int i=0; i< archiveNames.size();i++)
        {
			String tmp = archiveNames.get(i);
			PinboardArchive pArchive = mStateArchive.get(tmp);
			ListItem li = new ListItem(this,tmp);
			li.setArchive(true);
			
			if(pArchive.getName().equals(mStateArchive.getCurrentlyActivePinboardArchive().getName()))
			{
				selectedListItem = li;
				determineActiveListItem(true); 
				li.setSelected(true);
			}
			
        	archiveList.add(li);
        	archivelistarea.addView(li);
        }     
    }
    
    private void pressed_deleteState()
    {
    	if(archiveList.isEmpty() || selectedListItem.getName().equals("factory.state"))
		{
			return;
		}
		
		int tmp = -1;
		
		for(int i=0; i< archiveList.size(); i++)
		{
			ListItem lit = archiveList.get(i);
			
			if(lit.equals(selectedListItem))
			{
				toastMessage("'" + selectedListItem.getName() + "' deleted");
				
				mStateArchive.deleteArchive(mStateArchive.get(selectedListItem.getName()));
				archiveNames = mStateArchive.getArchiveNames();
				
    			archivelistarea.removeView(lit);
    			// lit = null;
    			// selectedListItem = null;
    			lit.setSelected(false);
    			tmp = i;
			}
		}
		
		if(tmp != -1)
		{
			archiveList.remove(tmp);
			archiveList.trimToSize();
		}
		
		if(archiveList.isEmpty())
		{
			return;
		}
		
		ListItem li = archiveList.get(0);
			
		if(li != null)
		{
			selectedListItem = li;
			li.setSelected(true);
			mStateArchive.setCurrentlyActivePinboardArchive(li.getName());
		}
    }
    
    private void pressed_backToOptions()
    {
    	viewFlag = PINBOARD;
		mScrollView.setVisibility(View.VISIBLE);
		archiveScrollView.setVisibility(View.INVISIBLE);		
		
		// row1_button2.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttony));	
		
		row2_button1.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttony));
		row2_button2.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttony));
		row2_button3.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttony));
	    
	    row2_button1.setText(res.getString(R.string.options_openboard));
	    row2_button2.setText(res.getString(R.string.options_addboard));
	    row2_button3.setText(res.getString(R.string.options_deleteboard));
	    // row1_button2.setText(res.getString(R.string.options_backupstate));
	    
	    listarea.removeAllViews();
	    pinboardList.clear();
	    
	    View listfreespace = new View(this);
		listfreespace.setLayoutParams(new LinearLayout.LayoutParams(displayX,10));
		listarea.addView(listfreespace);
	    
	    for(int i=0; i< pinboardNames.size();i++)
        {
			String tmp = pinboardNames.get(i);
			PinboardItem pItem = mPinboardArchive.get(tmp);
			ListItem li = new ListItem(this,tmp);
			
			if(pItem.getName().equals( mPinboardArchive.getCurrentlyActivePinboard().getName()))
			{
				selectedListItem = li;
				determineActiveListItem(false);
				li.setSelected(true);
			}
			
        	pinboardList.add(li);
			listarea.addView(li);
        }
    }
    
    private void toastMessage(String message)
    {
    	messageToast.cancel(); 
		messageToast.setText(message);		
		messageToast.show();
    }
    
    //
    // for list items
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
    	if(pendingListItem != null)
    	{
    		selectedListItem = pendingListItem;
    		pendingListItem = null;
    	}    	
    	
    	if(pendingRename != null)
    	{
    		selectedRename = pendingRename;
    		pendingRename = null;    		
    	}
    	
    	determineActiveListItem(selectedListItem.isArchive());
    	
    	if(selectedRename != null)
    	{
    		selectedRename = null;
    		
    		// open input editor for renaming the selected pinboard
    		Intent mIntent = new Intent(); 
    		mIntent.setClass(this, PinboardLauncher.class);			
    		mIntent.putExtra("text", selectedListItem.getName());
    		mIntent.putExtra("checkorientation", 1);
    			
    		if(selectedListItem.isArchive())
        	{
    			mIntent.putExtra("mode", RENAME_STATE);
        	}
        	else
        	{
        		mIntent.putExtra("mode", RENAME_PINBOARD);
        	}
    			
    		startActivity(mIntent);
    	}
        
    	return true;
    }
    
    private void determineActiveListItem(boolean archive)
    {    	
    	if(archive)
    	{
    		for(ListItem lit : archiveList)
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
    	}
    	else
    	{
    		for(ListItem lit : pinboardList)
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
    	}
    }
    
    class ListItem extends LinearLayout
    {
    	private Context context;
    	private LinearLayout holder;
    	private TextView pinboardname;
    	private View freespace;
    	private View freespace1;
    	private RenameLabel renamelabel;
    	
    	private String pinboardstring;
    	private boolean selected = false;
    	private boolean archive = false; // indicates whether this list item represents pinboard archive or pinboard
    	
    	public ListItem(Context context, String pinboardstring)
    	{
    		super(context);
    		this.context = context;
    		this.pinboardstring = pinboardstring;
    		
    		setLayoutParams(new LinearLayout.LayoutParams(displayX, 55));
    		setOrientation(LinearLayout.VERTICAL);
    		
    		View fspace1 = new View(context);
    		fspace1.setLayoutParams(new LinearLayout.LayoutParams(displayX, 5));
    		addView(fspace1);
    		
    		holder = new LinearLayout(context);
    		holder.setLayoutParams(new LinearLayout.LayoutParams(displayX, 55));
    		holder.setGravity(Gravity.CENTER);
    		holder.setOrientation(LinearLayout.HORIZONTAL);
    		
    		pinboardname = new TextView(context);
    		pinboardname.setLayoutParams(new LinearLayout.LayoutParams(205, 50));
    		pinboardname.setText(pinboardstring);
    		pinboardname.setBackgroundDrawable(res.getDrawable(R.drawable.nameview_three));
    		pinboardname.setGravity(Gravity.CENTER);
    		pinboardname.setTextColor(VDKGRAY);
    		pinboardname.setTypeface(Typeface.DEFAULT_BOLD); 
    		pinboardname.setTextSize(16);
    		holder.addView(pinboardname);
    		
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
    	
    	public void setName(String pinboardstring)
    	{
    		this.pinboardstring = pinboardstring;
    		pinboardname.setText(pinboardstring);
    	}
    	
    	public String getName()
    	{
    		return pinboardstring;
    	}
    	
    	public void setSelected(boolean selected)
    	{
    		this.selected = selected;
    		
    		if(archive == true)
    		{
    			if(selected)
    			{
    				pinboardname.setBackgroundDrawable(res.getDrawable(R.drawable.stateview)); 
    				renamelabel.setBackgroundDrawable(res.getDrawable(R.drawable.stateview));
    			
    				mStateArchive.setCurrentlyActivePinboardArchive(pinboardstring);
    			}
    			else
    			{
    				pinboardname.setBackgroundDrawable(res.getDrawable(R.drawable.nameview_three)); 
    				renamelabel.setBackgroundDrawable(res.getDrawable(R.drawable.renamelabel));
    			}
    		}
    		else
    		{
    			if(selected)
    			{
    				pinboardname.setBackgroundDrawable(res.getDrawable(R.drawable.nameview)); 
    				renamelabel.setBackgroundDrawable(res.getDrawable(R.drawable.nameview));
    			
    				mPinboardArchive.setCurrentlyActivePinboard(pinboardstring);
    			}
    			else
    			{
    				pinboardname.setBackgroundDrawable(res.getDrawable(R.drawable.nameview_three)); 
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
    
    class TaskReceiver extends BroadcastReceiver
	{
		@Override 
	    public void onReceive(Context context, Intent intent)
		{
			if(intent.getAction().equals("do task"))
			{
				pressed_backToOptions();
			}
			else if(intent.getAction().equals("delayed method"))
			{
				delayedListViewCreation();
			}
	   }
	}	   
}