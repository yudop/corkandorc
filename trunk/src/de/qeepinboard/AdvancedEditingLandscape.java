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
import android.webkit.*;
import android.text.InputType;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.graphics.*;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.widget.*;
import android.view.*;
import android.view.GestureDetector.OnGestureListener;
import android.view.ViewGroup.LayoutParams;
import android.content.*;
import java.util.*;

import android.view.animation.*;
import android.view.inputmethod.*;

public class AdvancedEditingLandscape extends Activity 
{	
	private static final int VDKGRAY = Color.rgb(0x33,0x33,0x33);
	
	private Display localDisplay;
	private int displayX;
	private int displayY;
	
	private Resources res;
	private Configuration config;
	
	private LinearLayout main;                
	private LinearLayout firstcolumn;         
	private View emptyspace1;			  			  
	private View emptyspace2;	
	private View emptyspace3;	
	private TextView button1;
	private TextView button2;			  
	private LinearLayout secondcolumn;  
	private EditText inputView;          
	private LinearLayout thirdcolumn;         			  
	
	private InputMethodManager mInputMethodManager;
	private EditButtonListener mEditButtonListener;
	private ClickReceiver mClickReceiver;
	private Intent mIntent;
	private Intent replyIntent;
	
	private String inputText = ""; 
	private int mode = -1;
	
	private boolean hasActiveHardKeyboard;
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setDisplay(); 
        mInputMethodManager = (InputMethodManager)getSystemService("input_method");
        checkIntent();
        checkConfig();
        setReceiver();
        
        createUI();
    }
	
	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);    
		
		replyIntent = new Intent(); 
		replyIntent.putExtra("text", inputView.getText().toString());
		replyIntent.putExtra("mode", mode);
		replyIntent.setClass(this,AdvancedEditing.class);
		startActivity(replyIntent);
		
		finish();
	}
	
	private void checkIntent()
	{
		mIntent = getIntent();
		String temp = mIntent.getStringExtra("text");
		
		if(!(temp.equals("") && temp == null))
		{
			inputText = temp;
		}
		
		mode = mIntent.getIntExtra("mode", -1);
	}
	
	private void checkConfig()
	{
		res = getResources();
		config = res.getConfiguration();
		
		if(config.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO)
		{
			hasActiveHardKeyboard = true;
		}
		else
		{
			hasActiveHardKeyboard = false;
		}
	}
	
	private void createUI()
	{
		if(hasActiveHardKeyboard)
		{
			createHardKeyboardUI();
		}
		else
		{
			createSoftKeyboardUI();
		}
	}
	
	// for devices w/o hardware keyboard
	private void createSoftKeyboardUI()
	{
		mEditButtonListener = new EditButtonListener(this);
		
		main = new LinearLayout(this);
		main.setLayoutParams(new LinearLayout.LayoutParams(displayX, displayY)); 
		main.setOrientation(LinearLayout.HORIZONTAL);
		
		firstcolumn = new LinearLayout(this);
		firstcolumn.setLayoutParams(new LinearLayout.LayoutParams(100, displayY)); 
		firstcolumn.setOrientation(LinearLayout.VERTICAL);
		
		emptyspace1 = new View(this);
		emptyspace1.setLayoutParams(new LinearLayout.LayoutParams(100, 35)); 
		firstcolumn.addView(emptyspace1);
		
		button1 = new TextView(this);
		button1.setGravity(Gravity.CENTER);
		button1.setLayoutParams(new LinearLayout.LayoutParams(100, 60));
		button1.setTextSize(18);
		button1.setId(EditButtonListener.ADVANCEDEDITINGLANDSCAPE_DONE);
		button1.setOnClickListener(mEditButtonListener);
		button1.setTextColor(VDKGRAY);
		button1.setShadowLayer(1, 1, 1, Color.WHITE);
		button1.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttony));
		button1.setText(res.getString(R.string.keyboard_done));
		firstcolumn.addView(button1);
		
		secondcolumn = new LinearLayout(this);
		secondcolumn.setGravity(Gravity.CENTER_HORIZONTAL);
		secondcolumn.setLayoutParams(new LinearLayout.LayoutParams(displayX-200, 129)); 
		secondcolumn.setOrientation(LinearLayout.VERTICAL);
	
		emptyspace2 = new View(this);
		emptyspace2.setLayoutParams(new LinearLayout.LayoutParams(100, 7)); 
		secondcolumn.addView(emptyspace2);
		
		inputView = new EditText(this);
		inputView.setGravity(Gravity.LEFT); 
		inputView.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE|InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
		inputView.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
		inputView.setText(inputText);
		if(PinboardLauncher.is240x320())
		{
			inputView.setLayoutParams(new LinearLayout.LayoutParams(displayX-206, 110));
		}
		else
		{
			inputView.setLayoutParams(new LinearLayout.LayoutParams(displayX-206, 122));   
		}
		inputView.setVerticalScrollBarEnabled(true);
		secondcolumn.addView(inputView);
		
		thirdcolumn = new LinearLayout(this);
		thirdcolumn.setLayoutParams(new LinearLayout.LayoutParams(100, displayY)); 
		thirdcolumn.setOrientation(LinearLayout.VERTICAL);
		
		emptyspace3 = new View(this);
		emptyspace3.setLayoutParams(new LinearLayout.LayoutParams(100, 35)); 
		thirdcolumn.addView(emptyspace3);
		
		button2 = new TextView(this);
		button2.setGravity(Gravity.CENTER);
		button2.setLayoutParams(new LinearLayout.LayoutParams(100, 60));
		button2.setTextSize(18);
		button2.setId(EditButtonListener.ADVANCEDEDITINGLANDSCAPE_CLEAR);
		button2.setOnClickListener(mEditButtonListener);
		button2.setTextColor(VDKGRAY);
		button2.setShadowLayer(1, 1, 1, Color.WHITE);
		button2.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttony));
		button2.setText(res.getString(R.string.keyboard_clear));
		thirdcolumn.addView(button2);
		
		main.addView(firstcolumn);
		main.addView(secondcolumn);
		main.addView(thirdcolumn);
		setContentView(main);
		
		delayedClickTask(); 
	}
	
	// for devices w/ hardware keyboard
	private void createHardKeyboardUI()
	{
		mEditButtonListener = new EditButtonListener(this);
		
		main = new LinearLayout(this);
		main.setLayoutParams(new LinearLayout.LayoutParams(displayX, displayY)); 
		main.setOrientation(LinearLayout.HORIZONTAL);
		
		secondcolumn = new LinearLayout(this);
		secondcolumn.setGravity(Gravity.CENTER);
		secondcolumn.setLayoutParams(new LinearLayout.LayoutParams(displayX-100, displayY)); 
		secondcolumn.setOrientation(LinearLayout.VERTICAL);
		
		emptyspace2 = new View(this);
		emptyspace2.setLayoutParams(new LinearLayout.LayoutParams(100, 7)); 
		secondcolumn.addView(emptyspace2);
		
		inputView = new EditText(this);
		inputView.setGravity(Gravity.LEFT); 
		inputView.setText(inputText);
		inputView.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE|InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
		inputView.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
		inputView.setLayoutParams(new LinearLayout.LayoutParams(displayX-103, displayY-4));   
		secondcolumn.addView(inputView);
		
		thirdcolumn = new LinearLayout(this);
		thirdcolumn.setLayoutParams(new LinearLayout.LayoutParams(100, displayY)); 
		thirdcolumn.setOrientation(LinearLayout.VERTICAL);
		
		emptyspace1 = new View(this);
		emptyspace1.setLayoutParams(new LinearLayout.LayoutParams(100, 5)); 
		thirdcolumn.addView(emptyspace1);
		
		button2 = new TextView(this);
		button2.setGravity(Gravity.CENTER);
		button2.setLayoutParams(new LinearLayout.LayoutParams(100, 60));
		button2.setTextSize(18);
		button2.setId(EditButtonListener.ADVANCEDEDITINGLANDSCAPE_CLEAR);
		button2.setOnClickListener(mEditButtonListener);
		button2.setTextColor(VDKGRAY);
		button2.setShadowLayer(1, 1, 1, Color.WHITE);
		button2.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttony));
		button2.setText(res.getString(R.string.keyboard_clear));
		thirdcolumn.addView(button2);
		
		emptyspace3 = new View(this);
		emptyspace3.setLayoutParams(new LinearLayout.LayoutParams(100, displayY - 125)); 
		thirdcolumn.addView(emptyspace3);
		
		button1 = new TextView(this);
		button1.setGravity(Gravity.CENTER);
		button1.setLayoutParams(new LinearLayout.LayoutParams(100, 60));
		button1.setTextSize(18);
		button1.setId(EditButtonListener.ADVANCEDEDITINGLANDSCAPE_DONE);
		button1.setOnClickListener(mEditButtonListener);
		button1.setTextColor(VDKGRAY);
		button1.setShadowLayer(1, 1, 1, Color.WHITE);
		button1.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttony));
		button1.setText(res.getString(R.string.keyboard_done));
		thirdcolumn.addView(button1);
		/*
		emptyspace4 = new View(this);
		emptyspace4.setLayoutParams(new LinearLayout.LayoutParams(100, 2)); 
		thirdcolumn.addView(emptyspace4);
		*/
		
		main.addView(secondcolumn);
		main.addView(thirdcolumn);
		setContentView(main);
		
		delayedClickTask(); 
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
    
	private void setReceiver()
	{
		mClickReceiver = new ClickReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("do click");
		registerReceiver(mClickReceiver, filter);
	}
	
	private void delayedClickTask()
	{
		Timer t = new Timer();
		TimerTask task = new TimerTask(){
			public void run()
			{
				Intent intent = new Intent(); 
				intent.setAction("do click");   
				sendBroadcast(intent);
			}
			
		};
		t.schedule(task,50);		
	}
    
    public void aelv_onDone()
    {
    	mInputMethodManager.hideSoftInputFromInputMethod(inputView.getApplicationWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
		String temp = inputView.getText().toString();
		
		if(mode == QeePinboard.EDIT_HEAD)
		{
			InstantEditView.registeredString = temp;
			InstantEditView.editInput.setText(InstantEditView.registeredString);
		}
		else if(mode == QeePinboard.EDIT_CONTENT)
		{
			InstantEditView.registeredString = temp;
			InstantEditView.contentEditInput.setText(InstantEditView.registeredString);
		}
		else if(mode == QeePinboard.EDIT_CONTENT_FROM_NOTE_DETAILS_VIEW)
		{
			NoteDetailsView.registeredString = temp;
			if(temp.equals(""))
			{
				QeePinboard.noteDetailsEdited = true;
				NoteDetailsView.detailsView.setText("(leer)");
			}
			else
			{
				QeePinboard.noteDetailsEdited = true;
				NoteDetailsView.detailsView.setText(NoteDetailsView.registeredString);
			}
		}
		else if(mode == OptionsActivity.RENAME_PINBOARD)
		{
			if((!temp.equals("")) && (temp != null) && (!temp.equals(inputText)))
			{
				OptionsActivity.registeredString = temp;
				OptionsActivity.commitedPinboardRename = true;
			}
		}
		else if(mode == OptionsActivity.RENAME_STATE)
		{
			if((!temp.equals("")) && (temp != null) && (!temp.equals(inputText)))
			{
				OptionsActivity.registeredString = temp;
				OptionsActivity.commitedStateRename = true;
			}
		}
		else if(mode == BackupActivity.RENAME_BACKUP)
		{
			if((!temp.equals("")) && (temp != null) && (!temp.equals(inputText)))
			{
				BackupActivity.registeredString = temp;
				BackupActivity.commitedBackupRename = true;
			}
		}
	
		finish();
    }
    
    public void aelv_onClear()
    {
    	inputView.setText("");
    }
    
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent  event)
    {
    	
    	if(keyCode==KeyEvent.KEYCODE_BACK)
    	{
    		mInputMethodManager.hideSoftInputFromInputMethod(inputView.getApplicationWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
    	
    		finish();
    	}
    	
    	return true;
    }
    
    class ClickReceiver extends BroadcastReceiver
	{
		@Override 
	    public void onReceive(Context context, Intent intent)
		{
			if(intent.getAction().equals("do click"))
			{
				mInputMethodManager = (InputMethodManager)getSystemService("input_method");
				mInputMethodManager.showSoftInput(inputView, InputMethodManager.SHOW_FORCED);
			}
	   }
	}	
}