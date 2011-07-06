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
import android.text.InputType;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.graphics.*;
import android.widget.*;
import java.util.*;
import android.view.animation.*;
import android.view.inputmethod.*;
import android.inputmethodservice.*;

public class AdvancedEditing extends Activity
{	
	private static final int VDKGRAY = Color.rgb(0x33,0x33,0x33);
	private static final int MOCK = Color.rgb(0x33,0x33,0x33);
	
	private Display localDisplay;
	private int displayX;
	private int displayY;
	
	private Intent mIntent;
	private Resources res;
	
	private FrameLayout main;
	private LinearLayout body;
	private View[] hfreespace = new View[10];
	private View[] vfreespace = new View[10];
	
	private LinearLayout buttonrow;
		private TextView button1;
		private TextView button2;
		private TextView button3; 
	private LinearLayout inputRow;	
		private ScrollView inputScroll;
		private EditText inputView;
		
	private EditButtonListener mEditButtonListener;
	
	private ClickReceiver mClickReceiver;
	private InputMethodManager mInputMethodManager;
	
	private List<InputMethodInfo> list;
	
	private String inputText = "";
	private int mode;
	
	private Panel480x854 mPanel480x854;
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState); 
        checkIntent();
        setDisplay(); 
        setReceiver();
        res = getResources();
        
        if(PinboardLauncher.is480x854())
        {
        	mPanel480x854 = new Panel480x854();
        	mPanel480x854.createUI();
        }
        else
        {
        	createUI();
        }
    }
	
	@Override
	public void onConfigurationChanged(Configuration newConfig)  
	{
		super.onConfigurationChanged(newConfig);
		
		mIntent.setClass(this,AdvancedEditingLandscape.class);
		mIntent.putExtra("text", inputView.getText().toString());
		mIntent.putExtra("mode", mode);
		startActivity(mIntent);
		
		finish();
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent  event)
    {
	   if(keyCode==KeyEvent.KEYCODE_BACK)
	   {
	       finish();
	   }
	    	
	   return true;
	}
	
	private void checkIntent()
	{
		mIntent = getIntent();
		String temp = mIntent.getStringExtra("text");
		
		if(temp != null)
		{
			inputText = temp;
		}
		
		mode = mIntent.getIntExtra("mode", -1);
	}
	
	private void createUI()
	{
		mEditButtonListener = new EditButtonListener(this); 
		
		main = new FrameLayout(this);
		main.setLayoutParams(new LinearLayout.LayoutParams(displayX, displayY));
		
		body = new LinearLayout(this);
		body.setLayoutParams(new LinearLayout.LayoutParams(displayX, displayY)); 
		body.setOrientation(LinearLayout.VERTICAL);
		
		vfreespace[0] = new View(this);
		vfreespace[0].setLayoutParams(new LinearLayout.LayoutParams(displayX, 5));
		body.addView(vfreespace[0]); 
		
		buttonrow = new LinearLayout(this);
		buttonrow.setLayoutParams(new LinearLayout.LayoutParams(displayX, 60));
		buttonrow.setOrientation(LinearLayout.HORIZONTAL);
		
		hfreespace[0] = new View(this);
		hfreespace[0].setLayoutParams(new LinearLayout.LayoutParams(5, 60));
		buttonrow.addView(hfreespace[0]);
		
		button1 = new TextView(this);
		button1.setGravity(Gravity.CENTER);
		button1.setLayoutParams(new LinearLayout.LayoutParams(100, 60));
		button1.setTextSize(18);
		button1.setId(EditButtonListener.ADVANCEDEDITING_DONE);
		button1.setOnClickListener(mEditButtonListener);
		button1.setTextColor(VDKGRAY);
		button1.setShadowLayer(1, 1, 1, Color.WHITE);
		button1.setBackgroundDrawable(res.getDrawable(R.drawable.buttony));
		button1.setText(res.getString(R.string.keyboard_done));
		buttonrow.addView(button1);
		
		hfreespace[1] = new View(this);
		hfreespace[1].setLayoutParams(new LinearLayout.LayoutParams(110, 60));
		buttonrow.addView(hfreespace[1]);
		
		button2 = new TextView(this);
		button2.setGravity(Gravity.CENTER);
		button2.setLayoutParams(new LinearLayout.LayoutParams(100, 60));
		button2.setTextSize(18);
		button2.setId(EditButtonListener.ADVANCEDEDITING_CLEAR);
		button2.setOnClickListener(mEditButtonListener);
		button2.setTextColor(VDKGRAY);
		button2.setShadowLayer(1, 1, 1, Color.WHITE);
		button2.setBackgroundDrawable(res.getDrawable(R.drawable.buttony));
		button2.setText(res.getString(R.string.keyboard_clear));
		buttonrow.addView(button2);
		
		body.addView(buttonrow);
		
		vfreespace[1] = new View(this);
		vfreespace[1].setLayoutParams(new LinearLayout.LayoutParams(displayX, 5));
		body.addView(vfreespace[1]);
		
		inputRow = new LinearLayout(this);
		inputRow.setLayoutParams(new LinearLayout.LayoutParams(displayX, QeePinboard.displayY - 275));
		inputRow.setOrientation(LinearLayout.HORIZONTAL);
		inputRow.setGravity(Gravity.CENTER_HORIZONTAL);
		
		inputView = new EditText(this); 
		inputView.setGravity(Gravity.LEFT); 
		inputView.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE|InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
		inputView.setImeOptions(EditorInfo.IME_ACTION_NONE); 
		inputView.setText(inputText);	
		inputView.setLayoutParams(new LinearLayout.LayoutParams(displayX - 20, QeePinboard.displayY - 273)); 
		inputView.setScrollContainer(true);
		inputView.setVerticalScrollBarEnabled(true);
		inputRow.addView(inputView);
		
		body.addView(inputRow);
		
		main.addView(body);		
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
	
	public void aev_onDone()
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
			if((!temp.equals("")) && (temp != null) && ((!temp.equals(inputText))))
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
	
	public void aev_onClear()
	{
		inputView.setText("");
	} 
	
	public void aev_onCancel()  
	{
		mInputMethodManager.hideSoftInputFromInputMethod(inputView.getApplicationWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY); 
		finish();
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
	
	
	class Panel480x854
	{
		public void createUI()
		{
			mEditButtonListener = new EditButtonListener(AdvancedEditing.this); 
			
			main = new FrameLayout(AdvancedEditing.this);
			main.setLayoutParams(new LinearLayout.LayoutParams(displayX, displayY));
			
			body = new LinearLayout(AdvancedEditing.this);
			body.setLayoutParams(new LinearLayout.LayoutParams(displayX, displayY)); 
			body.setOrientation(LinearLayout.VERTICAL);
			
			vfreespace[0] = new View(AdvancedEditing.this);
			vfreespace[0].setLayoutParams(new LinearLayout.LayoutParams(displayX, 5));
			body.addView(vfreespace[0]);
			
			buttonrow = new LinearLayout(AdvancedEditing.this);
			buttonrow.setLayoutParams(new LinearLayout.LayoutParams(displayX, 60));
			buttonrow.setOrientation(LinearLayout.HORIZONTAL);
			
			hfreespace[0] = new View(AdvancedEditing.this);
			hfreespace[0].setLayoutParams(new LinearLayout.LayoutParams(5, 60));
			buttonrow.addView(hfreespace[0]);
			
			button1 = new TextView(AdvancedEditing.this);
			button1.setGravity(Gravity.CENTER);
			button1.setLayoutParams(new LinearLayout.LayoutParams(100, 60));
			button1.setTextSize(18);
			button1.setId(EditButtonListener.ADVANCEDEDITING_DONE);
			button1.setOnClickListener(mEditButtonListener);
			button1.setTextColor(VDKGRAY);
			button1.setShadowLayer(1, 1, 1, Color.WHITE);
			button1.setBackgroundDrawable(res.getDrawable(R.drawable.buttony));
			button1.setText(res.getString(R.string.keyboard_done));
			buttonrow.addView(button1);
			
			hfreespace[1] = new View(AdvancedEditing.this);
			hfreespace[1].setLayoutParams(new LinearLayout.LayoutParams(110, 60));
			buttonrow.addView(hfreespace[1]);
			
			button2 = new TextView(AdvancedEditing.this);
			button2.setGravity(Gravity.CENTER);
			button2.setLayoutParams(new LinearLayout.LayoutParams(100, 60));
			button2.setTextSize(18);
			button2.setId(EditButtonListener.ADVANCEDEDITING_CLEAR);
			button2.setOnClickListener(mEditButtonListener);
			button2.setTextColor(VDKGRAY);
			button2.setShadowLayer(1, 1, 1, Color.WHITE);
			button2.setBackgroundDrawable(res.getDrawable(R.drawable.buttony));
			button2.setText(res.getString(R.string.keyboard_clear));  
			buttonrow.addView(button2);
			
			body.addView(buttonrow);
			
			vfreespace[1] = new View(AdvancedEditing.this);
			vfreespace[1].setLayoutParams(new LinearLayout.LayoutParams(displayX, 5));   
			body.addView(vfreespace[1]);
			
			inputRow = new LinearLayout(AdvancedEditing.this);
			inputRow.setLayoutParams(new LinearLayout.LayoutParams(displayX, QeePinboard.displayY - 310));
			inputRow.setOrientation(LinearLayout.HORIZONTAL);
			inputRow.setGravity(Gravity.CENTER_HORIZONTAL);       
			
			inputView = new EditText(AdvancedEditing.this); 
			inputView.setGravity(Gravity.LEFT);
			inputView.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE|InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
			inputView.setImeOptions(EditorInfo.IME_ACTION_NONE); 
			inputView.setText(inputText);		
			inputView.setLayoutParams(new LinearLayout.LayoutParams(displayX - 20, QeePinboard.displayY - 310)); 
			inputRow.addView(inputView);
			
			body.addView(inputRow);  
			
			main.addView(body);		
			setContentView(main);
			
			delayedClickTask(); 
		}
	}
}