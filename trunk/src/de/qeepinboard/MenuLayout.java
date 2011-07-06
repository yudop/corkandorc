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
import android.view.View;
import android.view.ViewGroup;
import android.graphics.*;
import android.widget.*;
import android.view.*;

public class MenuLayout 
{
	private static final int VDKGRAY = Color.rgb(0x33,0x33,0x33);
	
	private Context context;
	private Resources res; 
	private FrameLayout parent;
	
	private LinearLayout main; 
	
	private LinearLayout top;
	private LinearLayout middle;  
	private LinearLayout bottom;
	
	private TextView top_one; 
	private TextView top_two;
	private TextView top_three;
	
	private TextView bottom_one; 
	private TextView bottom_two;
	private TextView bottom_three; 
	 
	private View[] mfreespace = new View[10]; 
	private View[] freespace = new View[10];
	private View[] horizontalpatch = new View[2];
	private View verticalpatch;
	
	private int x;
	private int y;
	
	private int sizeState;
	
	private Rect sizeoneRect;
	private Rect sizetwoRect;
	private Rect editRect;
	private Rect backRect;
	private Rect deleteRect;
	private Rect detailsRect;
	
	public MenuLayout(Context context, FrameLayout parent, int x, int y, int sizeState) 
	{
		super();
		this.context = context;
		this.res = context.getResources();
		this.parent = parent;
		this.x = x;
		this.y = y;
		this.sizeState = sizeState;
		makeGUI();
		makeRects();
	}
	
	private void makeGUI()
	{
		main = new LinearLayout(context);
		main.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, QeePinboard.ENVIRONMENT_Y));
		main.setOrientation(LinearLayout.VERTICAL);
		
		if(y>0)
		{
			verticalpatch = new View(context);
			verticalpatch.setLayoutParams(new ViewGroup.LayoutParams(QeePinboard.ENVIRONMENT_X, y));
			main.addView(verticalpatch);
		}
		else
		{
			freespace[0] = new View(context);
			freespace[0].setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.displayX, 2));
			main.addView(freespace[0]);
		}
		
		top = new LinearLayout(context);
		top.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, 60));
		top.setOrientation(LinearLayout.HORIZONTAL);
		
		if(x>0)
		{
			horizontalpatch[0] = new View(context);
			horizontalpatch[0].setLayoutParams(new ViewGroup.LayoutParams(x, 60));
			top.addView(horizontalpatch[0]);
		}
		
		mfreespace[0] = new View(context);
		mfreespace[0].setLayoutParams(new LinearLayout.LayoutParams(4, 60));
		top.addView(mfreespace[0]);
		
		top_one = new TextView(context);
		top_one.setGravity(Gravity.CENTER);
		top_one.setLayoutParams(new LinearLayout.LayoutParams(100, 60));
		top_one.setTextSize(18);
		top_one.setTextColor(VDKGRAY);
		top_one.setShadowLayer(1, 1, 1, Color.WHITE);
		if(sizeState == Element.SIZE_SMALL)
		{ 
			top_one.setBackgroundDrawable(res.getDrawable(R.drawable.buttony));
			top_one.setText(res.getString(R.string.menu_medium));
		}
		else 
		{
			top_one.setBackgroundDrawable(res.getDrawable(R.drawable.buttony));
			top_one.setText(res.getString(R.string.menu_small));
		}
			
		top.addView(top_one);
		
		freespace[1] = new View(context);
		freespace[1].setLayoutParams(new LinearLayout.LayoutParams(5, 60));
		top.addView(freespace[1]);
		
		top_two = new TextView(context);
		top_two.setGravity(Gravity.CENTER);
		top_two.setLayoutParams(new LinearLayout.LayoutParams(100, 60));
		top_two.setTextSize(18);
		top_two.setTextColor(VDKGRAY);
		top_two.setShadowLayer(1, 1, 1, Color.WHITE);
		if(sizeState == Element.SIZE_LARGE)
		{
			top_two.setBackgroundDrawable(res.getDrawable(R.drawable.buttony));
			top_two.setText(res.getString(R.string.menu_medium));
		}
		else
		{
			top_two.setBackgroundDrawable(res.getDrawable(R.drawable.buttony));
			top_two.setText(res.getString(R.string.menu_large));
		}
		top.addView(top_two);
		
		freespace[2] = new View(context);
		freespace[2].setLayoutParams(new LinearLayout.LayoutParams(5, 60));
		top.addView(freespace[2]);
		
		top_three = new TextView(context);
		top_three.setGravity(Gravity.CENTER);
		top_three.setLayoutParams(new LinearLayout.LayoutParams(100, 60));
		top_three.setBackgroundDrawable(res.getDrawable(R.drawable.buttony));
		top_three.setText(res.getString(R.string.menu_back));
		top_three.setTextSize(18);
		top_three.setTextColor(VDKGRAY);
		top_three.setShadowLayer(1, 1, 1, Color.WHITE);
		top.addView(top_three);
		
		main.addView(top);
		
		middle = new LinearLayout(context);
		middle.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, QeePinboard.displayY - 125));  
		main.addView(middle);
		
		bottom = new LinearLayout(context);
		bottom.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, 60)); 
		bottom.setOrientation(LinearLayout.HORIZONTAL);
		
		if(x>0)
		{
			horizontalpatch[1] = new View(context);
			horizontalpatch[1].setLayoutParams(new ViewGroup.LayoutParams(x, 60));
			bottom.addView(horizontalpatch[1]);
		}
		
		mfreespace[1] = new View(context);
		mfreespace[1].setLayoutParams(new LinearLayout.LayoutParams(4, 60));
		bottom.addView(mfreespace[1]);
		
		bottom_one = new TextView(context);   
		bottom_one.setGravity(Gravity.CENTER);
		bottom_one.setLayoutParams(new LinearLayout.LayoutParams(100, 60)); 
		bottom_one.setBackgroundDrawable(res.getDrawable(R.drawable.buttony));
		bottom_one.setText(res.getString(R.string.menu_edit));  
		bottom_one.setTextSize(18);
		bottom_one.setTextColor(VDKGRAY);
		bottom_one.setShadowLayer(1, 1, 1, Color.WHITE);
		bottom.addView(bottom_one); 

		freespace[3] = new View(context);
		freespace[3].setLayoutParams(new LinearLayout.LayoutParams(5, 60));           
		bottom.addView(freespace[3]); 
		
		bottom_two = new TextView(context);   
		bottom_two.setGravity(Gravity.CENTER);
		bottom_two.setLayoutParams(new LinearLayout.LayoutParams(100, 60));
		if(QeePinboard.displayY > 500)
		{
			bottom_two.setBackgroundDrawable(res.getDrawable(R.drawable.buttony_details));
		}
		else
		{
			bottom_two.setBackgroundDrawable(res.getDrawable(R.drawable.buttony_details_mini));
		}
		
		bottom_two.setTextSize(18);
		bottom_two.setTextColor(VDKGRAY);
	         
		bottom_two.setShadowLayer(1, 1, 1, Color.WHITE);
		bottom.addView(bottom_two); 
		
		freespace[4] = new View(context);
		freespace[4].setLayoutParams(new LinearLayout.LayoutParams(5, 60));           
		bottom.addView(freespace[4]); 
		
		bottom_three = new TextView(context); 
		bottom_three.setGravity(Gravity.CENTER);
		bottom_three.setLayoutParams(new LinearLayout.LayoutParams(100, 60));
		bottom_three.setBackgroundDrawable(res.getDrawable(R.drawable.buttony));
		bottom_three.setText(res.getString(R.string.menu_delete));
		bottom_three.setTextSize(18);
		bottom_three.setTextColor(VDKGRAY);
		bottom_three.setShadowLayer(1, 1, 1, Color.WHITE);
		bottom.addView(bottom_three);
		
		main.addView(bottom);
	}
	
	private void makeRects()
	{
		sizeoneRect =   new Rect(  0, 0,100, 60);
		sizetwoRect =   new Rect(105, 0,205, 60);
		backRect =      new Rect(210, 0,315, 60);
		editRect =      new Rect(  0, QeePinboard.displayY - 65, 100, QeePinboard.displayY);
		detailsRect =   new Rect(105, QeePinboard.displayY - 65, 205, QeePinboard.displayY);
		deleteRect =    new Rect(200, QeePinboard.displayY - 65, QeePinboard.displayX, QeePinboard.displayY);		
	}
	
	public Rect getEditRect()
	{
		return editRect;
	}
	
	public Rect getSizeoneRect()
	{
		return sizeoneRect;
	}
	
	public Rect getSizetwoRect()
	{
		return sizetwoRect;
	}
	
	public Rect getDeleteRect()
	{
		return deleteRect;
	}
	
	public Rect getDetailsRect()
	{
		return detailsRect;
	}
	
	public Rect getBackRect()
	{
		return backRect;
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
	}
}
