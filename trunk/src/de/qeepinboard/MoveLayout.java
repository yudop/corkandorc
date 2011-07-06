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
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.*;
import android.widget.*;

public class MoveLayout 
{
	private static final int VDKGRAY = Color.rgb(0x33,0x33,0x33);
	
	private Context context;
	private Resources res;
	private FrameLayout parent;
	
	private LinearLayout main;
	private LinearLayout container;
	
	private View verticalpatch;
	private View[] horizontalpatch = new View[2];
	
	private TextView button_one; 
	private TextView button_two;
	private TextView button_three;
	private View[] freespace = new View[10];
	
	private Rect moveRect;
	private Rect copyRect;
	private Rect backRect;
	
	private int x;
	private int y;
	
	public MoveLayout(Context context, FrameLayout parent, int x, int y) 
	{
		super();
		this.context = context;
		this.res = context.getResources();
		this.parent = parent;
		this.x = x;
		this.y = y;
		
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
		
		container = new LinearLayout(context);
		container.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, 60));
		container.setOrientation(LinearLayout.HORIZONTAL);
		
		if(x>0)
		{
			horizontalpatch[0] = new View(context);
			horizontalpatch[0].setLayoutParams(new ViewGroup.LayoutParams(x, 60));
			container.addView(horizontalpatch[0]);
		}
		
		freespace[1] = new View(context);
		freespace[1].setLayoutParams(new LinearLayout.LayoutParams(4, 60));
		container.addView(freespace[1]);
		
		button_one = new TextView(context);
		button_one.setGravity(Gravity.CENTER);
		button_one.setLayoutParams(new LinearLayout.LayoutParams(100, 60));
		button_one.setTextSize(18);
		button_one.setTextColor(VDKGRAY);
		button_one.setBackgroundDrawable(res.getDrawable(R.drawable.buttony)); 
		button_one.setShadowLayer(1, 1, 1, Color.WHITE);
		button_one.setText(res.getString(R.string.move_move));
		container.addView(button_one);
		
		freespace[2] = new View(context);
		freespace[2].setLayoutParams(new LinearLayout.LayoutParams(5, 60));
		container.addView(freespace[2]);
		
		button_two = new TextView(context);
		button_two.setGravity(Gravity.CENTER);
		button_two.setLayoutParams(new LinearLayout.LayoutParams(100, 60));
		button_two.setTextSize(18);
		button_two.setTextColor(VDKGRAY);
		button_two.setBackgroundDrawable(res.getDrawable(R.drawable.buttony));
		button_two.setShadowLayer(1, 1, 1, Color.WHITE);
		button_two.setText(res.getString(R.string.move_copy));
		container.addView(button_two);
		
		freespace[3] = new View(context);
		freespace[3].setLayoutParams(new LinearLayout.LayoutParams(5, 60));
		container.addView(freespace[3]);
		
		button_three = new TextView(context);
		button_three.setGravity(Gravity.CENTER);
		button_three.setLayoutParams(new LinearLayout.LayoutParams(100, 60));
		button_three.setTextSize(18);
		button_three.setTextColor(VDKGRAY);
		button_three.setBackgroundDrawable(res.getDrawable(R.drawable.buttony));
		button_three.setShadowLayer(1, 1, 1, Color.WHITE);
		button_three.setText(res.getString(R.string.move_back));
		container.addView(button_three);
		
		main.addView(container);
	}
	
	private void makeRects()
	{
		moveRect =   new Rect(  0, 0,100, 60);
		copyRect =   new Rect(105, 0,205, 60);
		backRect =   new Rect(210, 0,315, 60);
	}
	
	public Rect getMoveRect()
	{
		return moveRect;
	}
	
	public Rect getCopyRect()
	{
		return copyRect;
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
