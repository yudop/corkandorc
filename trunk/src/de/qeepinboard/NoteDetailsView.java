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

import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.widget.*;
import android.view.*;
import android.graphics.drawable.*;

public class NoteDetailsView 
{
	private static final int VDKGRAY = Color.rgb(0x33,0x33,0x33);
	
	private Context context;
	private Resources res;
	private QeePinboard manager;
	private Element mElement;
	private FrameLayout parent;
	
	private int color;
	private int x;
	private int y;
	
	public static String registeredString = "";
	
	private LinearLayout main;
	private LinearLayout top;
	private LinearLayout bottom;
	public static TextView detailsView;
	
	private ScrollView mScrollView;
	private LinearLayout detailsHolder;
	
	private View[] horizontalpatch = new View[15]; 
	private View verticalpatch;
	private View freespace;
	private View freespace1;
	private View freespace2;
	private View mfreespace;
	
	private Button button1;
	private Button button2;
	private LinearLayout colorArea;
	private ImageView colorDisplay; 
	
	public NoteDetailsView(QeePinboard manager, Element mElement, int x, int y)
	{
		super();

		this.manager = manager;
		this.context = manager.getApplicationContext();
		this.res = manager.getApplicationContext().getResources();
		this.mElement = mElement;
		registeredString = mElement.getContentText();
		this.parent = manager.getMainPanel();
		this.x = x;
		this.y = y;
		this.color = mElement.getColor();
		createGUI();
	}
	
	private void createGUI()
	{		
		main = new LinearLayout(context);
		main.setBackgroundDrawable(res.getDrawable(R.drawable.partlytransparent));
		main.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, QeePinboard.ENVIRONMENT_Y));
		main.setOrientation(LinearLayout.VERTICAL);
		 
		if(y>0)
		{ 
			verticalpatch = new View(context);
			verticalpatch.setLayoutParams(new ViewGroup.LayoutParams(QeePinboard.ENVIRONMENT_X, y)); 
			main.addView(verticalpatch);
		}
		
		freespace = new View(context);
		freespace.setLayoutParams(new ViewGroup.LayoutParams(QeePinboard.ENVIRONMENT_X, 6));
		main.addView(freespace);
		
		//
		//
		top = new LinearLayout(context);
		top.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, QeePinboard.displayY - 76));
		
		if(x>0)
		{
			horizontalpatch[0] = new View(context);
			horizontalpatch[0].setLayoutParams(new ViewGroup.LayoutParams(x, QeePinboard.displayY - 70));
			top.addView(horizontalpatch[0]);
		}
		
		// correction for assymetric note appearance
		horizontalpatch[1] = new View(context);
		horizontalpatch[1].setLayoutParams(new ViewGroup.LayoutParams(3, QeePinboard.displayY - 70));
		top.addView(horizontalpatch[1]);
		
		detailsHolder = new LinearLayout(context);
		detailsHolder.setLayoutParams(new ViewGroup.LayoutParams(QeePinboard.displayX, QeePinboard.displayY - 70));
		detailsHolder.setPadding(36, 36, 36, 36); 
		detailsHolder.setBackgroundDrawable(pickColoredNote());
		
		mScrollView = new ScrollView(context);
		mScrollView.setLayoutParams(new ViewGroup.LayoutParams(QeePinboard.displayX - 63, QeePinboard.displayY - 142));
		mScrollView.setPadding(0, 0, 5, 0);
		mScrollView.setScrollContainer(false);
		
		detailsView = new TextView(context);
		detailsView.setGravity(Gravity.LEFT);
		detailsView.setLayoutParams(new ViewGroup.LayoutParams(QeePinboard.displayX - 63, QeePinboard.displayY - 142));
		detailsView.setTextSize(18);
		detailsView.setTypeface(Typeface.DEFAULT_BOLD);
		detailsView.setText(checkContentText());
		setAppropriateTextColor();
		
		mScrollView.addView(detailsView);
		detailsHolder.addView(mScrollView);		
		top.addView(detailsHolder);
		//
		
		//
		//
		bottom = new LinearLayout(context);
		bottom.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, 60));
		bottom.setOrientation(LinearLayout.HORIZONTAL);
		
		if(x>0)
		{
			horizontalpatch[2] = new View(context);
			horizontalpatch[2].setLayoutParams(new ViewGroup.LayoutParams(x, 60));
			bottom.addView(horizontalpatch[2]);
		}
		
		mfreespace = new View(context);
		mfreespace.setLayoutParams(new LinearLayout.LayoutParams(4, 60));
		bottom.addView(mfreespace);
		
		button1 = new Button(context);
		button1.setLayoutParams(new LinearLayout.LayoutParams(100, 60));
		button1.setBackgroundDrawable(res.getDrawable(R.drawable.buttony)); 
		button1.setOnClickListener(manager.getEditButtonListener());
		button1.setId(EditButtonListener.NOTEDETAILSVIEW_BACK);
		button1.setTextSize(16);
		button1.setTextColor(Color.BLACK);
		button1.setShadowLayer(1, 1, 1, Color.WHITE);		
		button1.setText(res.getString(R.string.details_done)); // or "Back" 
		bottom.addView(button1); 
		
		freespace1 = new View(context);
		freespace1.setLayoutParams(new ViewGroup.LayoutParams(5, 60));
		bottom.addView(freespace1);
		
		colorArea = new LinearLayout(context);
		colorArea.setLayoutParams(new LinearLayout.LayoutParams(100, 60));
		colorArea.setGravity(Gravity.CENTER);
		colorArea.setOnClickListener(manager.getEditButtonListener());
		colorArea.setId(EditButtonListener.NOTEDETAILSVIEW_COLOR_DISPLAY);
		
		colorDisplay = new ImageView(context);
		colorDisplay.setBackgroundDrawable(pickColoredBox());   
		colorDisplay.setLayoutParams(new ViewGroup.LayoutParams(50, 45));
		colorDisplay.setOnClickListener(manager.getEditButtonListener());
		colorDisplay.setId(EditButtonListener.NOTEDETAILSVIEW_COLOR_DISPLAY);
		colorArea.addView(colorDisplay);
		
		bottom.addView(colorArea);
		
		freespace2 = new View(context);
		freespace2.setLayoutParams(new ViewGroup.LayoutParams(5, 60));
		bottom.addView(freespace2);
		
		button2 = new Button(context);
		button2.setLayoutParams(new LinearLayout.LayoutParams(100, 60));
		button2.setBackgroundDrawable(res.getDrawable(R.drawable.buttony));
		button2.setOnClickListener(manager.getEditButtonListener());
		button2.setId(EditButtonListener.NOTEDETAILSVIEW_EDIT);
		button2.setTextSize(16);
		button2.setTextColor(Color.BLACK);
		button2.setShadowLayer(1, 1, 1, Color.WHITE);
		button2.setText(res.getString(R.string.details_edit)); 
		bottom.addView(button2);
		
		main.addView(top);
		main.addView(bottom);
	}
	
	private String checkContentText()
	{
		String temp = mElement.getContentText();
		
		if(temp.equals(""))
		{
			return "(empty)";
		}
		else
		{
			return temp;
		}
	}
	
	public Drawable pickColoredNote()
	{
		if(color == EditButtonListener.COLORACTIVITY_BLUE)
		{
			return res.getDrawable(R.drawable.notelite_blue_big);
		}
		else if(color == EditButtonListener.COLORACTIVITY_GRAY)
		{
			return res.getDrawable(R.drawable.notelite_gray_big);
		}
		else if(color == EditButtonListener.COLORACTIVITY_GREEN)
		{
			return res.getDrawable(R.drawable.notelite_green_big);
		}
		else if(color == EditButtonListener.COLORACTIVITY_RED)
		{
			return res.getDrawable(R.drawable.notelite_red_big); 
		}
		else if(color == EditButtonListener.COLORACTIVITY_YELLOW)
		{
			return res.getDrawable(R.drawable.notelite_yellow_big);
		}
		else if(color == EditButtonListener.COLORACTIVITY_ORANGE)
		{
			return res.getDrawable(R.drawable.notelite_orange_big);
		}
		else if(color == EditButtonListener.COLORACTIVITY_VIOLETTE)
		{
			return res.getDrawable(R.drawable.notelite_violette_big);
		}
		else 
		{
			return res.getDrawable(R.drawable.notelite_white_big); 
		}
	}
	
	public Drawable pickColoredBox()
	{
		if(color == EditButtonListener.COLORACTIVITY_BLUE)
		{
			return res.getDrawable(R.drawable.buttonyblue);
		}
		else if(color == EditButtonListener.COLORACTIVITY_GRAY)
		{
			return res.getDrawable(R.drawable.buttongray);
		}
		else if(color == EditButtonListener.COLORACTIVITY_GREEN)
		{
			return res.getDrawable(R.drawable.buttonygreen);
		}
		else if(color == EditButtonListener.COLORACTIVITY_RED)
		{
			return res.getDrawable(R.drawable.buttonyred);
		}
		else if(color == EditButtonListener.COLORACTIVITY_YELLOW)
		{
			return res.getDrawable(R.drawable.buttonyyellow);
		}
		else if(color == EditButtonListener.COLORACTIVITY_ORANGE)
		{
			return res.getDrawable(R.drawable.buttonyorange);
		}
		else if(color == EditButtonListener.COLORACTIVITY_VIOLETTE)
		{
			return res.getDrawable(R.drawable.buttonyviolette);
		}
		else 
		{
			return res.getDrawable(R.drawable.buttonwhite);
		}
	}
	
	public void setAppropriateTextColor()
	{
		if((color == EditButtonListener.COLORACTIVITY_BLUE) || (color ==  EditButtonListener.COLORACTIVITY_RED) || (color == EditButtonListener.COLORACTIVITY_VIOLETTE))
		{
			detailsView.setTextColor(Color.WHITE);
		}
		else 
		{
			detailsView.setTextColor(Color.DKGRAY);
		}
	}
	
	public void setColorSelection(Drawable d)
	{
		colorDisplay.setBackgroundDrawable(d);
	}
	
	public void setColor(int color)
	{
		this.color = color;
		
		mElement.setColor(color);
		mElement.setNonSelectedDrawable();
		
		detailsHolder.setBackgroundDrawable(pickColoredNote());
		setAppropriateTextColor();
	}
	
	public void onEdit()
	{
		mElement.setContentText(registeredString); // apply changes if any of them has occurred in last edit (without having pressed 'Done')
		
		Intent mIntent = new Intent();
		mIntent.setClass(context, PinboardLauncher.class);
		mIntent.putExtra("text", mElement.getContentText());
		mIntent.putExtra("mode", QeePinboard.EDIT_CONTENT_FROM_NOTE_DETAILS_VIEW);
		mIntent.putExtra("checkorientation", 1);
		manager.startActivity(mIntent);
	}
	
	public void onCancel()
	{
		if(!registeredString.equals(mElement.getContentText()))
		{
			mElement.setContentText(registeredString);			
			mElement.setNonSelectedDrawable();			
		}
	}
	
	public void onRefresh()
	{
		detailsHolder.removeView(mScrollView);
		
		mScrollView = new ScrollView(context);
		mScrollView.setLayoutParams(new ViewGroup.LayoutParams(QeePinboard.displayX - 63, QeePinboard.displayY - 142));
		mScrollView.setPadding(0, 0, 5, 0);
		mScrollView.setScrollContainer(false);
		
		detailsView = new TextView(context);
		detailsView.setGravity(Gravity.LEFT);
		detailsView.setLayoutParams(new ViewGroup.LayoutParams(QeePinboard.displayX - 63, QeePinboard.displayY - 142));
		detailsView.setTextSize(18);
		detailsView.setTypeface(Typeface.DEFAULT_BOLD);
		detailsView.setText(registeredString);
		setAppropriateTextColor();
		
		mScrollView.addView(detailsView);
		detailsHolder.addView(mScrollView);
	}
	
	public Element getElement()
	{
		return mElement;
	}
	
	public LinearLayout getView()
	{
		return main;
	}
	
	public void add()
	{
		parent.addView(main);
	}
	
	public void remove()
	{
		parent.removeView(main);
		System.gc();
	}
}
