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
import android.view.*;
import android.graphics.*;
import android.widget.*;
import android.graphics.drawable.*;
import java.util.*;

public class Element extends LinearLayout 
{
	private HashMap<String,String> mapRepresentative;
	
	public static final int SIZE_SMALL = 0;
	public static final int SIZE_MEDIUM = 1;
	public static final int SIZE_LARGE = 2;	
	
	private Context context;
	
	private int x;
	private int y;
	private int width;
	private int height;
	private Rect mRect;
	private Point aContainedPoint;
	
	private TextView tv;
	private View fillspaceX;
	private View fillspaceY;
	private LinearLayout horizontalspace;
	
	private int sizeState;
	private int color = EditButtonListener.COLORACTIVITY_YELLOW;
	private String headText = "";
	private String contentText = "";
	
	public Element(Context context, int x, int y, int width, int height)
	{
		super(context);
		
		this.context = context;
		this.x = x;   
		this.y = y;   
		this.width = width;   
		this.height = height;
		mRect = new Rect(x,y,x+width,y+height); 
		aContainedPoint = new Point(x+10,y+10);
		
		createGUI(context);
		createMapRepresentative(x,y,width,height);
	}
	
	public Element(Context context, HashMap<String,String> mapRepresentative)
	{
		super(context);
		parseMapRepresentative(mapRepresentative);
		this.mapRepresentative = mapRepresentative;
		this.context = context;
		mRect = new Rect(x,y,x+width,y+height); 
		aContainedPoint = new Point(x+10,y+10);
		createGUI(context);
	}
	
	public Element(Context context, int x, int y, HashMap<String,String> mapRepresentative)
	{
		super(context);
		this.x = x;
		this.y = y;
		parseMapRepresentativeWithoutXY(mapRepresentative);
		this.context = context;
		mRect = new Rect(x,y,x+width,y+height); 
		aContainedPoint = new Point(x+10,y+10);
		createMapRepresentative(x,y,width,height);
		createGUI(context);
	}
	
	private void createMapRepresentative(int x, int y, int width, int height)
	{
		mapRepresentative = new HashMap<String,String>();
			
		mapRepresentative.put("x", new Integer(x).toString());
		mapRepresentative.put("y", new Integer(y).toString());
		mapRepresentative.put("width", new Integer(width).toString());
		mapRepresentative.put("height", new Integer(height).toString());
		mapRepresentative.put("sizeState", new Integer(sizeState).toString());
		mapRepresentative.put("color", new Integer(color).toString());
		mapRepresentative.put("headText", headText);
		mapRepresentative.put("contentText", contentText);	
	}
	
	private void parseMapRepresentative(HashMap<String,String> mapRepresentative)
	{
		String map_x = mapRepresentative.get("x");
		String map_y = mapRepresentative.get("y");
		String map_width = mapRepresentative.get("width");
		String map_height = mapRepresentative.get("height");
		String map_sizeState = mapRepresentative.get("sizeState");
		String map_color = mapRepresentative.get("color");
		
		headText = mapRepresentative.get("headText");
		contentText = mapRepresentative.get("contentText");	
		x = Integer.parseInt(map_x);
		y = Integer.parseInt(map_y);
		width = Integer.parseInt(map_width);
		height = Integer.parseInt(map_height);
		sizeState = Integer.parseInt(map_sizeState);
		color = Integer.parseInt(map_color);
	}
	
	private void parseMapRepresentativeWithoutXY(HashMap<String,String> mapRepresentative)
	{
		String map_width = mapRepresentative.get("width");
		String map_height = mapRepresentative.get("height");
		String map_sizeState = mapRepresentative.get("sizeState");
		String map_color = mapRepresentative.get("color");
		
		headText = mapRepresentative.get("headText");
		contentText = mapRepresentative.get("contentText");	
		width = Integer.parseInt(map_width);
		height = Integer.parseInt(map_height);
		sizeState = Integer.parseInt(map_sizeState);
		color = Integer.parseInt(map_color);
	}
	
	private void createGUI(Context context)
	{
		setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X,QeePinboard.ENVIRONMENT_Y)); 
		setOrientation(LinearLayout.VERTICAL);
			
		fillspaceY = new View(context);
		fillspaceY.setLayoutParams(new ViewGroup.LayoutParams(QeePinboard.ENVIRONMENT_X,y)); 
		addView(fillspaceY);
			
		horizontalspace = new LinearLayout(context);
		horizontalspace.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X,QeePinboard.ENVIRONMENT_Y-y)); 
		horizontalspace.setOrientation(LinearLayout.HORIZONTAL);
			
		fillspaceX = new View(context);
		fillspaceX.setLayoutParams(new ViewGroup.LayoutParams(x,QeePinboard.ENVIRONMENT_Y-y));
		horizontalspace.addView(fillspaceX);
			
		tv = new TextView(context);
		tv.setLayoutParams(new ViewGroup.LayoutParams(width,height));
		tv.setText(headText);
		tv.setTypeface(Typeface.DEFAULT_BOLD); 
		tv.setShadowLayer(1, 1, 1, Color.GRAY);
		tv.setTextSize(16);
		tv.setPadding(15, 25, 22, 32);
		tv.setGravity(Gravity.CENTER);
		tv.setBackgroundDrawable(pickColoredNote()); 
		setAppropriateTextColor();
			
		horizontalspace.addView(tv); 
			 
		addView(horizontalspace);		
	}
	
	public Drawable pickColoredNote()
	{
		if(color == EditButtonListener.COLORACTIVITY_BLUE)
		{
			if(contentText.equals(""))
			{
				return context.getResources().getDrawable(R.drawable.notelite_blue);
			}
			else
			{
				return context.getResources().getDrawable(R.drawable.notelite_blue_details);
			}
		}
		else if(color == EditButtonListener.COLORACTIVITY_GRAY)
		{
			if(contentText.equals(""))
			{
				return context.getResources().getDrawable(R.drawable.notelite_gray);
			}
			else
			{
				return context.getResources().getDrawable(R.drawable.notelite_gray_details);
			}
		}
		else if(color == EditButtonListener.COLORACTIVITY_GREEN)
		{
			if(contentText.equals(""))
			{
				return context.getResources().getDrawable(R.drawable.notelite_green);
			}
			else
			{
				return context.getResources().getDrawable(R.drawable.notelite_green_details);          
			}
		}
		else if(color == EditButtonListener.COLORACTIVITY_RED)
		{
			if(contentText.equals(""))
			{
				return context.getResources().getDrawable(R.drawable.notelite_red);
			}
			else
			{
				return context.getResources().getDrawable(R.drawable.notelite_red_details);
			}
		}
		else if(color == EditButtonListener.COLORACTIVITY_YELLOW)
		{
			if(contentText.equals(""))
			{
				return context.getResources().getDrawable(R.drawable.notelite_yellow);
			}
			else
			{
				return context.getResources().getDrawable(R.drawable.notelite_yellow_details); 
			}
		}
		else if(color == EditButtonListener.COLORACTIVITY_ORANGE)
		{
			if(contentText.equals(""))
			{
				return context.getResources().getDrawable(R.drawable.notelite_orange);
			}
			else
			{
				return context.getResources().getDrawable(R.drawable.notelite_orange_details);
			}
		}
		else if(color == EditButtonListener.COLORACTIVITY_VIOLETTE)
		{
			if(contentText.equals(""))
			{
				return context.getResources().getDrawable(R.drawable.notelite_violette);
			}
			else
			{
				return context.getResources().getDrawable(R.drawable.notelite_violette_details);
			}
		}
		else 
		{
			if(contentText.equals(""))
			{
				return context.getResources().getDrawable(R.drawable.notelite_white);
			}
			else
			{
				return context.getResources().getDrawable(R.drawable.notelite_white_details);  
			}
		}
	}
	
	public void setAppropriateTextColor()
	{
		if((color == EditButtonListener.COLORACTIVITY_BLUE) || (color ==  EditButtonListener.COLORACTIVITY_RED) || (color == EditButtonListener.COLORACTIVITY_VIOLETTE))
		{
			tv.setTextColor(Color.WHITE);
		}
		else 
		{
			tv.setTextColor(Color.DKGRAY);
		}
	}
	
	public void add(FrameLayout parent)
	{
		parent.addView(this);
	}
	
	public void remove(FrameLayout parent)
	{
		parent.removeView(this);
	}
	
	public HashMap<String,String> getMapRepresentative()
	{
		return mapRepresentative;
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public int getWidthX()
	{
		return width;
	}
	
	public int getHeightY()
	{
		return height;
	}
	
	public int getSizeState()
	{
		return sizeState;
	}
	
	public Rect getRect()
	{
		return mRect;
	}
	
	public Point getAContainedPoint()
	{
		return aContainedPoint;
	}
	
	public String getHeadText()
	{
		return headText;
	}
	
	public String getContentText()
	{
		return contentText;
	}
	
	public int getColor()
	{
		return color;
	}
	
	public void setWidthX(int width)
	{
		this.width = width;
		mapRepresentative.put("width", new Integer(width).toString());
	}
	
	public void setHeightY(int height)
	{
		this.height = height;
		mapRepresentative.put("height", new Integer(height).toString());
	}
	
	public void setSizeState(int sizeState)
	{
		this.sizeState = sizeState;
		mapRepresentative.put("sizeState", new Integer(sizeState).toString());
		
		if(sizeState == SIZE_SMALL)
		{
			width = 150;
			height = 168;
			tv.setLayoutParams((new LinearLayout.LayoutParams(150,168)));
		}
		else if(sizeState == SIZE_MEDIUM)
		{
			width = 200;
			height = 220;
			tv.setLayoutParams((new LinearLayout.LayoutParams(200,220)));
		}
		else if(sizeState == SIZE_LARGE)
		{
			width = 240;
			height = 275; 
			tv.setLayoutParams((new LinearLayout.LayoutParams(240,275)));   
		}
		
		mapRepresentative.put("width", new Integer(width).toString());
		mapRepresentative.put("height", new Integer(height).toString());
		
		mRect = new Rect(x,y,x+width,y+height);
	}
	
	public void setNonSelectedDrawable()
	{		
		tv.setBackgroundDrawable(pickColoredNote());
		setAppropriateTextColor();
	}
	
	public void setSelectedDrawable()
	{
		if(contentText.equals(""))
		{
			tv.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.note_selected));
		}
		else
		{
			tv.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.note_selected_details));
		}
		tv.setTextColor(Color.WHITE);
	}
	
	public void setMoveDrawable()
	{
		if(contentText.equals(""))
		{
			tv.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.note_move));
		}
		else
		{
			tv.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.note_move_details));
		}
		tv.setTextColor(Color.WHITE);
	}
	
	public void setCopyDrawable()
	{
		if(contentText.equals(""))
		{
			tv.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.note_copy));
		}
		else
		{
			tv.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.note_copy_details));
		}
		tv.setTextColor(Color.WHITE);
	}
	
	public void setHeadText(String text)
	{
		this.headText = text;
		mapRepresentative.put("headText", headText);
		tv.setText(text);
	}
	
	public void setContentText(String text)
	{
		this.contentText = text;
		mapRepresentative.put("contentText", contentText);
	}
	
	public void setColor(int color)
	{
		this.color = color;
		mapRepresentative.put("color", new Integer(color).toString());
	}
	
	public TextView getTV()
	{
		return tv;
	}
}
