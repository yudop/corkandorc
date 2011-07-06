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
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.graphics.*;
import android.widget.*;
import android.view.*;
import android.view.GestureDetector.OnGestureListener;
import android.view.ViewGroup.LayoutParams;
import android.content.*;
import java.util.*;
import android.view.animation.*;
import android.content.pm.*;

public class ColorActivity extends Activity
{
	private EditButtonListener mEditButtonListener;
	
	private Resources res;
	private Display localDisplay;
	private int displayX;
	private int displayY;
	
	private LinearLayout main;
	private LinearLayout row1;
	private LinearLayout row2;
		private LinearLayout cell21;
			private ImageView imv21;
		private LinearLayout cell22;
			private ImageView imv22;
	private LinearLayout row3;
		private LinearLayout cell31;
			private ImageView imv31;
		private LinearLayout cell32;
			private ImageView imv32;
	private LinearLayout row4;
		private LinearLayout cell41;
			private ImageView imv41;
		private LinearLayout cell42;
			private ImageView imv42;
	private LinearLayout row5;
		private LinearLayout cell51;
			private ImageView imv51;
		private LinearLayout cell52;
			private ImageView imv52;
	private LinearLayout row6;
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setDisplay(); 
        
        res = getResources();      
        createUI();
    }
	
	private void createUI()
	{
		mEditButtonListener = new EditButtonListener(this);
		
		main = new LinearLayout(this);
		// main.setBackgroundDrawable(getResources().getDrawable(R.drawable.color_activity_background));
		main.setBackgroundDrawable(res.getDrawable(R.drawable.color_activity_b));   
		main.setLayoutParams(new LinearLayout.LayoutParams(displayX, displayY));
		main.setOrientation(LinearLayout.VERTICAL);
		
		row1 = new LinearLayout(this);
		row1.setLayoutParams(new LinearLayout.LayoutParams(displayX, (int)((displayY-400)/2)));
		row1.setOrientation(LinearLayout.HORIZONTAL);
		
		row2 = new LinearLayout(this);
		row2.setLayoutParams(new LinearLayout.LayoutParams(displayX, 100));
		row2.setOrientation(LinearLayout.HORIZONTAL);
		
		cell21 = new LinearLayout(this);
		cell21.setLayoutParams(new LinearLayout.LayoutParams(displayX/2, 100));
		cell21.setGravity(Gravity.CENTER);
		cell21.setOnClickListener(mEditButtonListener);
		cell21.setId(EditButtonListener.COLORACTIVITY_YELLOW);
		
		imv21 = new ImageView(this);
		imv21.setLayoutParams(new LinearLayout.LayoutParams(72, 72));
		imv21.setBackgroundDrawable(res.getDrawable(R.drawable.buttonyyellow));
		cell21.addView(imv21);
		row2.addView(cell21);
		
		cell22 = new LinearLayout(this);
		cell22.setLayoutParams(new LinearLayout.LayoutParams(displayX/2, 100));
		cell22.setGravity(Gravity.CENTER);
		cell22.setOnClickListener(mEditButtonListener);
		cell22.setId(EditButtonListener.COLORACTIVITY_ORANGE);
		
		imv22 = new ImageView(this);
		imv22.setLayoutParams(new LinearLayout.LayoutParams(72, 72));
		imv22.setBackgroundDrawable(res.getDrawable(R.drawable.buttonyorange));
		cell22.addView(imv22);
		row2.addView(cell22);
		
		row3 = new LinearLayout(this);
		row3.setLayoutParams(new LinearLayout.LayoutParams(displayX, 100));
		row3.setOrientation(LinearLayout.HORIZONTAL);
		
		cell31 = new LinearLayout(this);
		cell31.setLayoutParams(new LinearLayout.LayoutParams(displayX/2, 100));
		cell31.setGravity(Gravity.CENTER);
		cell31.setOnClickListener(mEditButtonListener);
		cell31.setId(EditButtonListener.COLORACTIVITY_GREEN);
		
		imv31 = new ImageView(this);
		imv31.setLayoutParams(new LinearLayout.LayoutParams(72, 72));
		imv31.setBackgroundDrawable(res.getDrawable(R.drawable.buttonygreen));
		cell31.addView(imv31);
		row3.addView(cell31);
		
		cell32 = new LinearLayout(this);
		cell32.setLayoutParams(new LinearLayout.LayoutParams(displayX/2, 100));
		cell32.setGravity(Gravity.CENTER);
		cell32.setOnClickListener(mEditButtonListener);
		cell32.setId(EditButtonListener.COLORACTIVITY_RED);
		
		imv32 = new ImageView(this);
		imv32.setLayoutParams(new LinearLayout.LayoutParams(72, 72));
		imv32.setBackgroundDrawable(res.getDrawable(R.drawable.buttonyred));
		cell32.addView(imv32);
		row3.addView(cell32);
		
		row4 = new LinearLayout(this);
		row4.setLayoutParams(new LinearLayout.LayoutParams(displayX, 100));
		row4.setOrientation(LinearLayout.HORIZONTAL);
		
		cell41 = new LinearLayout(this);
		cell41.setLayoutParams(new LinearLayout.LayoutParams(displayX/2, 100));
		cell41.setGravity(Gravity.CENTER);
		cell41.setOnClickListener(mEditButtonListener);
		cell41.setId(EditButtonListener.COLORACTIVITY_BLUE);
		
		imv41 = new ImageView(this);
		imv41.setLayoutParams(new LinearLayout.LayoutParams(72, 72));
		imv41.setBackgroundDrawable(res.getDrawable(R.drawable.buttonyblue));
		cell41.addView(imv41);
		row4.addView(cell41);
		
		cell42 = new LinearLayout(this);
		cell42.setLayoutParams(new LinearLayout.LayoutParams(displayX/2, 100));
		cell42.setGravity(Gravity.CENTER);
		cell42.setOnClickListener(mEditButtonListener);
		cell42.setId(EditButtonListener.COLORACTIVITY_VIOLETTE);
		
		imv42 = new ImageView(this);
		imv42.setLayoutParams(new LinearLayout.LayoutParams(72, 72));
		imv42.setBackgroundDrawable(res.getDrawable(R.drawable.buttonyviolette));
		cell42.addView(imv42);
		row4.addView(cell42);
		
		row5 = new LinearLayout(this);
		row5.setLayoutParams(new LinearLayout.LayoutParams(displayX, 100));
		row5.setOrientation(LinearLayout.HORIZONTAL);
		
		cell51 = new LinearLayout(this);
		cell51.setLayoutParams(new LinearLayout.LayoutParams(displayX/2, 100));
		cell51.setGravity(Gravity.CENTER);
		cell51.setOnClickListener(mEditButtonListener);
		cell51.setId(EditButtonListener.COLORACTIVITY_GRAY);
		
		imv51 = new ImageView(this);
		imv51.setLayoutParams(new LinearLayout.LayoutParams(72, 72));
		imv51.setBackgroundDrawable(res.getDrawable(R.drawable.buttongray));		
		cell51.addView(imv51);
		row5.addView(cell51);
		
		cell52 = new LinearLayout(this);
		cell52.setLayoutParams(new LinearLayout.LayoutParams(displayX/2, 100));
		cell52.setGravity(Gravity.CENTER);
		cell52.setOnClickListener(mEditButtonListener);
		cell52.setId(EditButtonListener.COLORACTIVITY_WHITE);
		
		imv52 = new ImageView(this);
		imv52.setLayoutParams(new LinearLayout.LayoutParams(72, 72));
		imv52.setBackgroundDrawable(res.getDrawable(R.drawable.buttonwhite));		
		cell52.addView(imv52);
		row5.addView(cell52);
		
		row6 = new LinearLayout(this);
		row6.setLayoutParams(new LinearLayout.LayoutParams(displayX, (int)((displayY-400)/2)));
		row6.setOrientation(LinearLayout.HORIZONTAL);
		
		main.addView(row1);
		main.addView(row2);
		main.addView(row3);
		main.addView(row4);
		main.addView(row5);
		main.addView(row6);
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
    
    public void ca_onColor(int colorConst)
    {
    	QeePinboard.colorChanged = true;
    	QeePinboard.colorValue = colorConst;
    	
    	finish();
    }
}
