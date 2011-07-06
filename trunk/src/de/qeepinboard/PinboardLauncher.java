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

public class PinboardLauncher extends Activity
{
	 private Display localDisplay;
	 private static int displayX = 0;
	 private static int displayY = 0;
	
	 @Override
	 public void onCreate(Bundle savedInstanceState) 
	 {
		 super.onCreate(savedInstanceState);
		 
		 setDisplay();
		 
		 Intent check = getIntent();
		 
		 if(check.getIntExtra("checkorientation", -1) == -1)
		 {				 
			 Intent aIntent = new Intent();
		     aIntent.setClass(this, QeePinboard.class);
		     startActivity(aIntent);  				 
					 
		 }
		 else
		 {	  
			 Intent bIntent = new Intent();   
	    	 if(displayX > displayY)
	    	 {
	    		 bIntent.setClass(this, AdvancedEditingLandscape.class);
	    	 }
	    	 else
	    	 {
	    		 bIntent.setClass(this, AdvancedEditing.class);     
	    	 }
	    	 
	    	 bIntent.putExtra("text", check.getStringExtra("text"));
	    	 bIntent.putExtra("mode", check.getIntExtra("mode", -1));
	    	 
	    	 startActivity(bIntent);
		 }
		 
		 finish();
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
	 
	 public static boolean is240x320()
	 {
	    return (((displayX > 300 && displayX < 340) && (displayY < 460)) 
	    			|| ((displayX < 460) && (displayY > 300 && displayY < 340)));
	 }
	 
	 public static boolean is320x480()
	 {
	    return (((displayX > 300 && displayX < 340) && (displayY > 460 && displayY < 500)) 
	           || ((displayX > 460 && displayX < 500) && (displayY > 300 && displayY < 340)));
	 }
	    
	 public static boolean is480x800()
	 {
	    return (((displayX > 300 && displayX < 340) && (displayY > 500 && displayY < 545)) 
	           || ((displayX > 500 && displayX < 545) && (displayY > 300 && displayY < 340)));
	 }
	    
	 public static boolean is480x854()
	 {
	    return (((displayX > 300 && displayX < 340) && (displayY >= 545)) 
	    	   || ((displayX >= 545) && (displayY > 300 && displayY < 340))); 
	 }
}
