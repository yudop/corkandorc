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
import android.text.InputType;
import android.graphics.*;
import android.widget.*;
import android.view.*;
import android.graphics.drawable.*;
import android.view.GestureDetector.OnGestureListener;

/* InstandEditView.java 
 * 
 * Class for instant note editing (when user taps on the cork). This view is only for initial note edits, thus 
 * doesn't contain note body editing, just it's head (which is directly visible on the note graphics). This view is
 * also only for portrait mode to avoid complexity.
 * There is an option for Advanced Editing which has an own activity and provides additional editing features such as
 * body editing etc. Advanced editing can be used both in portrait and landscape mode.
 *  
 */

public class InstantEditView 
{
	public static final int INPUT_HEAD = 2;
	public static final int INPUT_CONTENT = 3;
	
	public static final int MODE_CREATE = 0;
	public static final int MODE_EDIT = 1;
	
	private static final int VDKGRAY = Color.rgb(0x33,0x33,0x33);
	
	public static String registeredString = "";
	
	private Context context;
	private Resources res;
	private FrameLayout parent; 
	private QeePinboard manager;
	private int x;
	private int y;
	private int mode;
	
	private String headText = "";
	private String contentText = "";
	private int noteSize;
	private int color = EditButtonListener.COLORACTIVITY_YELLOW;
	
	private LinearLayout main;
	private View[] horizontalpatch = new View[15]; 
	private View verticalpatch;
	
	private LinearLayout emptySpace1;                // 1
	private LinearLayout colorRow;                   // 2
		private TextView colorLabel;                 // 2.1
		private View colorFreespace;                 // 2.2
		private ImageView colorDisplay;              // 2.3
	private LinearLayout emptySpace2;                // 3
	private LinearLayout sizeRow;                    // 4
		private TextView sizeLabel;                  // 4.1
		private View sizeFreespace;                  // 4.2
		private TextView sizeDisplay;                // 4.3
	private LinearLayout emptySpace3;                // 5
	private LinearLayout headLabelRow;               // 6
		private TextView headLabelLabel;             // 6.1
	private LinearLayout emptySpace4;                // 7
	private LinearLayout editRow;                    // 8
		private LinearLayout editFullarea;           // 8.1
		public static InputView editInput;           // 8.2            
	private LinearLayout emptySpace5;                // 9
	private LinearLayout contentLabelRow;            // 10
		private TextView contentLabelLabel;          // 10.1
	private LinearLayout emptySpace6;                // 11
	private LinearLayout contentEditRow;             // 12
		private LinearLayout contentEditFullarea;    // 12.1
		public static InputView contentEditInput;    // 12.2	
	private LinearLayout emptySpace7;                // 13
	private LinearLayout firstButtonRow;             // 14
		private Button button1;             		 // 14.1
		private View firstButtonFreespace;           // 14.2
		private Button button2;             		 // 14.3
		
	private Element mElement;
	
	private Panel240x320 mPanel240x320;
	private Panel320x480 mPanel320x480;
	private Panel480x854 mPanel480x854;
	
	public InstantEditView(Context context, QeePinboard manager, Element mElement, int x, int y, int mode)
	{
		super();
		registeredString = ""; 
		this.context = context;
		this.res = context.getResources();
		this.mElement = mElement;
		this.manager = manager;
		this.parent = manager.getMainPanel();
		this.x = x;
		this.y = y;
		this.mode = mode;
		this.color = mElement.getColor();
		this.noteSize = mElement.getSizeState();   
		
		if(PinboardLauncher.is240x320())
		{
			mPanel240x320 = new Panel240x320();
			mPanel240x320.createGUI();
		}
		else if(PinboardLauncher.is320x480())
		{
			mPanel320x480 = new Panel320x480();
			mPanel320x480.createGUI();
		}
		else if(PinboardLauncher.is480x854())
		{
			mPanel480x854 = new Panel480x854();
			mPanel480x854.createGUI();
		}
		else
		{
			createGUI();
		}  
	}
	
	// for 480x800
	private void createGUI()
	{
		main = new LinearLayout(context);
		main.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.partlytransparent));  
		main.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, QeePinboard.ENVIRONMENT_Y));
		main.setOrientation(LinearLayout.VERTICAL); 
	
		if(y>0)
		{
			verticalpatch = new View(context);
			verticalpatch.setLayoutParams(new ViewGroup.LayoutParams(QeePinboard.ENVIRONMENT_X, y));
			main.addView(verticalpatch);
		}
		
		//
		// 1
		emptySpace1 = new LinearLayout(context);
		emptySpace1.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, 20));
		
		//
		// 2
		colorRow = new LinearLayout(context);
		colorRow.setOrientation(LinearLayout.HORIZONTAL);
		colorRow.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, 45));
		
		if(x>0)
		{
			horizontalpatch[0] = new View(context);
			horizontalpatch[0].setLayoutParams(new ViewGroup.LayoutParams(x, 45));
			colorRow.addView(horizontalpatch[0]);
		}
		
		colorLabel = new TextView(context);
		colorLabel.setGravity(Gravity.CENTER_VERTICAL); 
		colorLabel.setLayoutParams(new ViewGroup.LayoutParams(100, 45));
		colorLabel.setTextColor(Color.LTGRAY);
		colorLabel.setTextSize(18);
		colorLabel.setTypeface(Typeface.DEFAULT_BOLD);
		colorLabel.setText("  " + res.getString(R.string.iev_color));
		colorRow.addView(colorLabel);
		
		colorFreespace = new View(context);
		colorFreespace.setLayoutParams(new ViewGroup.LayoutParams(QeePinboard.displayX-213, 45));
		colorRow.addView(colorFreespace);
		
		colorDisplay = new ImageView(context);
		colorDisplay.setBackgroundDrawable(pickColoredBox());   
		colorDisplay.setLayoutParams(new ViewGroup.LayoutParams(50, 45));
		colorDisplay.setOnClickListener(manager.getEditButtonListener());
		colorDisplay.setId(EditButtonListener.INSTANTEDITVIEW_COLOR_DISPLAY);
		colorRow.addView(colorDisplay);
		
		//
		// 3
		emptySpace2 = new LinearLayout(context);
		emptySpace2.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, 10));
		
		//
		// 4
		sizeRow = new LinearLayout(context);
		sizeRow.setOrientation(LinearLayout.HORIZONTAL);
		sizeRow.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, 45));
		
		if(x>0)
		{
			horizontalpatch[1] = new View(context);
			horizontalpatch[1].setLayoutParams(new ViewGroup.LayoutParams(x, 45));
			sizeRow.addView(horizontalpatch[1]);
		}
		
		sizeLabel = new TextView(context);
		sizeLabel.setGravity(Gravity.CENTER_VERTICAL);
		sizeLabel.setLayoutParams(new ViewGroup.LayoutParams(100, 45));
		sizeLabel.setTextColor(Color.LTGRAY);
		sizeLabel.setTextSize(18);
		sizeLabel.setTypeface(Typeface.DEFAULT_BOLD);
		sizeLabel.setText("  " + res.getString(R.string.iev_size)); 
		sizeRow.addView(sizeLabel);
		
		sizeFreespace = new View(context);
		sizeFreespace.setLayoutParams(new ViewGroup.LayoutParams(QeePinboard.displayX-230, 45));
		sizeRow.addView(sizeFreespace);
		
		sizeDisplay = new TextView(context);
		sizeDisplay.setBackgroundDrawable(res.getDrawable(R.drawable.buttony));
		sizeDisplay.setGravity(Gravity.CENTER);
		sizeDisplay.setLayoutParams(new ViewGroup.LayoutParams(85, 45));
		sizeDisplay.setTextColor(Color.BLACK);
		sizeDisplay.setTextSize(16);
		sizeDisplay.setShadowLayer(1, 1, 1, Color.WHITE);
		sizeDisplay.setOnClickListener(manager.getEditButtonListener());
		sizeDisplay.setId(EditButtonListener.INSTANTEDITVIEW_SIZE_DISPLAY);
		sizeDisplay.setText(sizeToString());
		sizeRow.addView(sizeDisplay);
		
		// 
		// 5
		emptySpace3 = new LinearLayout(context);
		emptySpace3.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, 4));
		
		//
		// 6
		headLabelRow = new LinearLayout(context);
		headLabelRow.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, 15));
		
		if(x>0)
		{
			horizontalpatch[2] = new View(context);
			horizontalpatch[2].setLayoutParams(new ViewGroup.LayoutParams(x, 15));
			headLabelRow.addView(horizontalpatch[2]);
		}
		
		headLabelLabel = new TextView(context);
		headLabelLabel.setGravity(Gravity.CENTER);
		headLabelLabel.setLayoutParams(new ViewGroup.LayoutParams(QeePinboard.displayX, 15));
		headLabelLabel.setTextColor(Color.LTGRAY);
		headLabelLabel.setTextSize(14);
		headLabelLabel.setTypeface(Typeface.DEFAULT_BOLD);
		headLabelLabel.setText(" " + res.getString(R.string.iev_head_label));
		headLabelRow.addView(headLabelLabel);
		
		// 
		// 7
		emptySpace4 = new LinearLayout(context);
		emptySpace4.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, 5));
		
		//
		// 8
		editRow = new LinearLayout(context);
		editRow.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, 90));
		
		if(x>0)
		{
			horizontalpatch[3] = new View(context);
			horizontalpatch[3].setLayoutParams(new ViewGroup.LayoutParams(x, 90));
			editRow.addView(horizontalpatch[3]);
		}
		
		editFullarea = new LinearLayout(context);
		editFullarea.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.displayX, 90));
		editFullarea.setGravity(Gravity.CENTER);
		editRow.addView(editFullarea);
		
		editInput = new InputView(context);
		editInput.setGravity(Gravity.LEFT); 
		editInput.setId(INPUT_HEAD);
		editInput.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
		editInput.setText(mElement.getHeadText());
		editInput.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.displayX-30, 90));
		editFullarea.addView(editInput);
		
		// 
		// 9
		emptySpace5 = new LinearLayout(context);
		emptySpace5.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, 2));
		
		//
		// 10
		contentLabelRow = new LinearLayout(context);
		contentLabelRow.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, 15));
		
		if(x>0)
		{
			horizontalpatch[4] = new View(context);
			horizontalpatch[4].setLayoutParams(new ViewGroup.LayoutParams(x, 15));
			contentLabelRow.addView(horizontalpatch[4]);
		}
		
		contentLabelLabel = new TextView(context);
		contentLabelLabel.setGravity(Gravity.CENTER);
		contentLabelLabel.setLayoutParams(new ViewGroup.LayoutParams(QeePinboard.displayX, 15));
		contentLabelLabel.setTextColor(Color.LTGRAY);
		contentLabelLabel.setTextSize(14);
		contentLabelLabel.setTypeface(Typeface.DEFAULT_BOLD);
		contentLabelLabel.setText(" " + res.getString(R.string.iev_details_label));
		contentLabelRow.addView(contentLabelLabel);		
		
		// 
		// 11
		emptySpace6 = new LinearLayout(context);
		emptySpace6.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, 5));
		
		//
		// 12
		contentEditRow = new LinearLayout(context);
		contentEditRow.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, 180));
		
		if(x>0)
		{
			horizontalpatch[5] = new View(context);
			horizontalpatch[5].setLayoutParams(new ViewGroup.LayoutParams(x, 180));
			contentEditRow.addView(horizontalpatch[5]);
		}
		
		contentEditFullarea = new LinearLayout(context); 
		contentEditFullarea.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.displayX, 180));
		contentEditFullarea.setGravity(Gravity.CENTER);
		contentEditRow.addView(contentEditFullarea);
		
		contentEditInput = new InputView(context);
		contentEditInput.setGravity(Gravity.LEFT); 
		contentEditInput.setId(INPUT_CONTENT);
		contentEditInput.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
		contentEditInput.setText(mElement.getContentText());
		contentEditInput.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.displayX-30, 180));
		contentEditFullarea.addView(contentEditInput);
		
		// 
		// 13
		emptySpace7 = new LinearLayout(context);
		emptySpace7.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, 15));		
		
		//
		// 14
		firstButtonRow = new LinearLayout(context);
		firstButtonRow.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, 60));
		
		if(x>0)
		{
			horizontalpatch[6] = new View(context);
			horizontalpatch[6].setLayoutParams(new ViewGroup.LayoutParams(x, 60));
			firstButtonRow.addView(horizontalpatch[6]);
		}
		
		button1 = new Button(context);
		button1.setLayoutParams(new LinearLayout.LayoutParams(100, 60));
		button1.setBackgroundDrawable(res.getDrawable(R.drawable.buttony)); 
		button1.setOnClickListener(manager.getEditButtonListener());
		button1.setId(EditButtonListener.INSTANTEDITVIEW_ADD);
		button1.setTextSize(16);
		button1.setTextColor(Color.BLACK);
		button1.setShadowLayer(1, 1, 1, Color.WHITE);
		if(mode == MODE_CREATE) 
		{
			button1.setText(res.getString(R.string.iev_create)); // or Add
		}
		else
		{
			button1.setText(res.getString(R.string.iev_done));
		}
		firstButtonRow.addView(button1);
		
		firstButtonFreespace = new View(context);
		firstButtonFreespace.setLayoutParams(new ViewGroup.LayoutParams(QeePinboard.displayX-205, 60));
		firstButtonRow.addView(firstButtonFreespace);
		
		button2 = new Button(context);
		button2.setLayoutParams(new LinearLayout.LayoutParams(100, 60));
		button2.setBackgroundDrawable(res.getDrawable(R.drawable.buttony));
		button2.setOnClickListener(manager.getEditButtonListener());
		button2.setId(EditButtonListener.INSTANTEDITVIEW_CANCEL);
		button2.setTextSize(16);
		button2.setTextColor(Color.BLACK);
		button2.setShadowLayer(1, 1, 1, Color.WHITE);
		button2.setText(res.getString(R.string.iev_cancel));
		firstButtonRow.addView(button2);		
		
		// ADD ROWS TO main
		main.addView(emptySpace1); // 1
		main.addView(colorRow); // 2
		main.addView(emptySpace2); // 3
		main.addView(sizeRow); // 4
		main.addView(emptySpace3); // 5
		main.addView(headLabelRow); // 6
		main.addView(emptySpace4); // 7
		main.addView(editRow); // 8
		main.addView(emptySpace5); // 9
		main.addView(contentLabelRow); // 10
		main.addView(emptySpace6); // 11
		main.addView(contentEditRow); // 12
		main.addView(emptySpace7); // 13
		main.addView(firstButtonRow); // 14
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
	
	private String sizeToString()
	{
		if(noteSize == Element.SIZE_SMALL)
		{
			return res.getString(R.string.iev_size_small); 
		}
		else if(noteSize == Element.SIZE_MEDIUM)
		{
			return res.getString(R.string.iev_size_medium);
		}
		else return res.getString(R.string.iev_size_large);
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
	
	public String getHeadText()
	{
		String str = "";
		String temp;
		
		temp = editInput.getText().toString();
		
		if(temp == null)
		{
			return str;
		}
		else
		{
			return (temp + str);
		}
	}
	
	public String getContentText()
	{
		String str = "";
		String temp;
		
		temp = contentEditInput.getText().toString();
		
		if(temp == null)
		{
			return str;
		}
		else
		{
			return (temp + str);
		}
	}
	
	public int getNoteSize()
	{
		return noteSize;
	}
	
	public int getMode()
	{
		return mode;
	}
	
	public Element getElement()
	{
		return mElement;
	}
	
	public int getColor()
	{
		return color;
	}
	
	public void setColorSelection(Drawable d)
	{
		colorDisplay.setBackgroundDrawable(d);
	}
	
	public void setColor(int color)
	{
		this.color = color;
	}
	
	public void setElement(Element mElement)
	{
		this.mElement = mElement;
	}
	
	public void swapNoteSize()
	{
		if(noteSize == Element.SIZE_SMALL)
		{
			noteSize = Element.SIZE_MEDIUM;
			sizeDisplay.setText(res.getString(R.string.iev_size_medium));
			mElement.setWidthX(200);
			mElement.setHeightY(220);
		}
		else if(noteSize == Element.SIZE_MEDIUM)
		{
			noteSize = Element.SIZE_LARGE;
			sizeDisplay.setText(res.getString(R.string.iev_size_large));
			mElement.setWidthX(240);
			mElement.setHeightY(275);
		}
		else if(noteSize == Element.SIZE_LARGE)
		{
			noteSize = Element.SIZE_SMALL;
			sizeDisplay.setText(res.getString(R.string.iev_size_small));
			mElement.setWidthX(150);
			mElement.setHeightY(168);
		}
	}
	
	class InputView extends EditText implements OnGestureListener
	{
		private Context context;
		private GestureDetector mGestureDetector;
		
		public InputView(Context context)
		{
			super(context);
			this.context = context;
			mGestureDetector = new GestureDetector(this);			
		}
		
		@Override
        public boolean onTouchEvent(MotionEvent me) 
        {   
        	return mGestureDetector.onTouchEvent(me); 
        }
		
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
	    {  
	    	return true; 
	    }
	    
	    public boolean onDown(MotionEvent e)
	    {
	    	return true;
	    }
	    
	    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
	    {
	    	return true;   
	    }
	    
	    public void onLongPress(MotionEvent e)         
	    {    
	    	super.onTouchEvent(e);
	    }
	    
	    public void onShowPress(MotionEvent e) 
	    {  
	    	super.onTouchEvent(e);
	    }    
	    
	    public boolean onSingleTapUp(MotionEvent e)    
	    { 
	    	Intent mIntent = new Intent(); 
			mIntent.setClass(context, PinboardLauncher.class);
			
	    	if(getId() == INPUT_HEAD)
	    	{
	    		mIntent.putExtra("text", editInput.getText().toString());
	    		mIntent.putExtra("mode", QeePinboard.EDIT_HEAD);
	    		mIntent.putExtra("checkorientation", 1);
	    		manager.startActivity(mIntent);
	    	}
	    	else
	    	{
	    		mIntent.putExtra("text", contentEditInput.getText().toString());
	    		mIntent.putExtra("mode", QeePinboard.EDIT_CONTENT);
	    		mIntent.putExtra("checkorientation", 1);
	    		manager.startActivity(mIntent);
	    	}
	    	
	    	return true; 
	    }	    	
	}
	
	
	class Panel320x480
	{
		public void createGUI()
		{
			main = new LinearLayout(context);
			main.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.partlytransparent)); 
			main.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, QeePinboard.ENVIRONMENT_Y));
			main.setOrientation(LinearLayout.VERTICAL);
		
			if(y>0)
			{
				verticalpatch = new View(context);
				verticalpatch.setLayoutParams(new ViewGroup.LayoutParams(QeePinboard.ENVIRONMENT_X, y));
				main.addView(verticalpatch);
			}
			
			//
			// 1
			emptySpace1 = new LinearLayout(context);
			emptySpace1.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, 10));
			
			//
			// 2
			colorRow = new LinearLayout(context);
			colorRow.setOrientation(LinearLayout.HORIZONTAL);
			colorRow.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, 45));
			
			if(x>0)
			{
				horizontalpatch[0] = new View(context);
				horizontalpatch[0].setLayoutParams(new ViewGroup.LayoutParams(x, 45));
				colorRow.addView(horizontalpatch[0]);
			}
			
			colorLabel = new TextView(context);
			colorLabel.setGravity(Gravity.CENTER_VERTICAL); 
			colorLabel.setLayoutParams(new ViewGroup.LayoutParams(100, 45));
			colorLabel.setTextColor(Color.LTGRAY);
			colorLabel.setTextSize(18);
			colorLabel.setTypeface(Typeface.DEFAULT_BOLD);
			colorLabel.setText("   " + res.getString(R.string.iev_color));
			colorRow.addView(colorLabel);
			
			colorFreespace = new View(context);
			colorFreespace.setLayoutParams(new ViewGroup.LayoutParams(QeePinboard.displayX-200, 45));
			colorRow.addView(colorFreespace);
			
			colorDisplay = new ImageView(context);
			colorDisplay.setBackgroundDrawable(pickColoredBox());   
			colorDisplay.setLayoutParams(new ViewGroup.LayoutParams(50, 45));
			colorDisplay.setOnClickListener(manager.getEditButtonListener());
			colorDisplay.setId(EditButtonListener.INSTANTEDITVIEW_COLOR_DISPLAY);
			colorRow.addView(colorDisplay);
			
			//
			// 3
			emptySpace2 = new LinearLayout(context);
			emptySpace2.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, 10));
			
			//
			// 4
			sizeRow = new LinearLayout(context);
			sizeRow.setOrientation(LinearLayout.HORIZONTAL);
			sizeRow.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, 45));
			
			if(x>0)
			{
				horizontalpatch[1] = new View(context);
				horizontalpatch[1].setLayoutParams(new ViewGroup.LayoutParams(x, 45));
				sizeRow.addView(horizontalpatch[1]);
			}
			
			sizeLabel = new TextView(context);
			sizeLabel.setGravity(Gravity.CENTER_VERTICAL);
			sizeLabel.setLayoutParams(new ViewGroup.LayoutParams(100, 45));
			sizeLabel.setTextColor(Color.LTGRAY);
			sizeLabel.setTextSize(18);
			sizeLabel.setTypeface(Typeface.DEFAULT_BOLD);
			sizeLabel.setText("   " + res.getString(R.string.iev_size)); 
			sizeRow.addView(sizeLabel);
			
			sizeFreespace = new View(context);
			sizeFreespace.setLayoutParams(new ViewGroup.LayoutParams(QeePinboard.displayX-217, 45));
			sizeRow.addView(sizeFreespace);
			
			sizeDisplay = new TextView(context);
			sizeDisplay.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.buttony));
			sizeDisplay.setGravity(Gravity.CENTER);
			sizeDisplay.setLayoutParams(new ViewGroup.LayoutParams(85, 45));
			sizeDisplay.setTextColor(Color.BLACK);
			sizeDisplay.setTextSize(16);
			sizeDisplay.setShadowLayer(1, 1, 1, Color.WHITE);
			sizeDisplay.setOnClickListener(manager.getEditButtonListener());
			sizeDisplay.setId(EditButtonListener.INSTANTEDITVIEW_SIZE_DISPLAY);
			sizeDisplay.setText(sizeToString());
			sizeRow.addView(sizeDisplay);
			
			// 
			// 5
			emptySpace3 = new LinearLayout(context);
			emptySpace3.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, 4));
			
			//
			// 6
			headLabelRow = new LinearLayout(context);
			headLabelRow.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, 15));
			
			if(x>0)
			{
				horizontalpatch[2] = new View(context);
				horizontalpatch[2].setLayoutParams(new ViewGroup.LayoutParams(x, 15));
				headLabelRow.addView(horizontalpatch[2]);
			}
			
			headLabelLabel = new TextView(context);
			headLabelLabel.setGravity(Gravity.CENTER);
			headLabelLabel.setLayoutParams(new ViewGroup.LayoutParams(QeePinboard.displayX, 15));
			headLabelLabel.setTextColor(Color.LTGRAY);
			headLabelLabel.setTextSize(14);
			headLabelLabel.setTypeface(Typeface.DEFAULT_BOLD);
			headLabelLabel.setText(" " + res.getString(R.string.iev_head_label));
			headLabelRow.addView(headLabelLabel);
			
			// 
			// 7
			emptySpace4 = new LinearLayout(context);
			emptySpace4.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, 5));
			
			//
			// 8
			editRow = new LinearLayout(context);
			editRow.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, 90));
			
			if(x>0)
			{
				horizontalpatch[3] = new View(context);
				horizontalpatch[3].setLayoutParams(new ViewGroup.LayoutParams(x, 90));
				editRow.addView(horizontalpatch[3]);
			}
			
			editFullarea = new LinearLayout(context);
			editFullarea.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.displayX, 90));
			editFullarea.setGravity(Gravity.CENTER);
			editRow.addView(editFullarea);
			
			editInput = new InputView(context);
			editInput.setGravity(Gravity.LEFT); 
			editInput.setId(INPUT_HEAD);
			editInput.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
			editInput.setText(mElement.getHeadText());
			editInput.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.displayX-30, 90));
			editFullarea.addView(editInput);
			
			// 
			// 9
			emptySpace5 = new LinearLayout(context);
			emptySpace5.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, 2));
			
			//
			// 10
			contentLabelRow = new LinearLayout(context);
			contentLabelRow.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, 15));
			
			if(x>0)
			{
				horizontalpatch[4] = new View(context);
				horizontalpatch[4].setLayoutParams(new ViewGroup.LayoutParams(x, 15));
				contentLabelRow.addView(horizontalpatch[4]);
			}
			
			contentLabelLabel = new TextView(context);
			contentLabelLabel.setGravity(Gravity.CENTER);
			contentLabelLabel.setLayoutParams(new ViewGroup.LayoutParams(QeePinboard.displayX, 15));
			contentLabelLabel.setTextColor(Color.LTGRAY);
			contentLabelLabel.setTextSize(14);
			contentLabelLabel.setTypeface(Typeface.DEFAULT_BOLD);
			contentLabelLabel.setText(" " + res.getString(R.string.iev_details_label));
			contentLabelRow.addView(contentLabelLabel);		
			
			// 
			// 11
			emptySpace6 = new LinearLayout(context);
			emptySpace6.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, 5));
			
			//
			// 12
			contentEditRow = new LinearLayout(context);
			contentEditRow.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, 145));
			
			if(x>0)
			{
				horizontalpatch[5] = new View(context);
				horizontalpatch[5].setLayoutParams(new ViewGroup.LayoutParams(x, 180));
				contentEditRow.addView(horizontalpatch[5]);
			}
			
			contentEditFullarea = new LinearLayout(context);
			contentEditFullarea.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.displayX, 145));
			contentEditFullarea.setGravity(Gravity.CENTER);
			contentEditRow.addView(contentEditFullarea);
			
			contentEditInput = new InputView(context);
			contentEditInput.setGravity(Gravity.LEFT); 
			contentEditInput.setId(INPUT_CONTENT);
			contentEditInput.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
			contentEditInput.setText(mElement.getContentText());
			contentEditInput.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.displayX-30, 145));
			contentEditFullarea.addView(contentEditInput);
			
			// 
			// 13
			emptySpace7 = new LinearLayout(context);
			emptySpace7.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, 20));		
			
			//
			// 14
			firstButtonRow = new LinearLayout(context);
			firstButtonRow.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, 60));
			
			if(x>0)
			{
				horizontalpatch[6] = new View(context);
				horizontalpatch[6].setLayoutParams(new ViewGroup.LayoutParams(x, 60));
				firstButtonRow.addView(horizontalpatch[6]);
			}
			
			button1 = new Button(context);
			button1.setLayoutParams(new LinearLayout.LayoutParams(100, 60));
			button1.setBackgroundDrawable(res.getDrawable(R.drawable.buttony)); 
			button1.setOnClickListener(manager.getEditButtonListener());
			button1.setId(EditButtonListener.INSTANTEDITVIEW_ADD);
			button1.setTextSize(16);
			button1.setTextColor(Color.BLACK);
			button1.setShadowLayer(1, 1, 1, Color.WHITE);
			if(mode == MODE_CREATE)
			{
				button1.setText(res.getString(R.string.iev_create)); // or Add
			}
			else
			{
				button1.setText(res.getString(R.string.iev_done));
			}
			firstButtonRow.addView(button1);
			
			firstButtonFreespace = new View(context);
			firstButtonFreespace.setLayoutParams(new ViewGroup.LayoutParams(QeePinboard.displayX-205, 60));
			firstButtonRow.addView(firstButtonFreespace);
			
			button2 = new Button(context);
			button2.setLayoutParams(new LinearLayout.LayoutParams(100, 60));
			button2.setBackgroundDrawable(res.getDrawable(R.drawable.buttony));
			button2.setOnClickListener(manager.getEditButtonListener());
			button2.setId(EditButtonListener.INSTANTEDITVIEW_CANCEL);
			button2.setTextSize(16);
			button2.setTextColor(Color.BLACK);
			button2.setShadowLayer(1, 1, 1, Color.WHITE);
			button2.setText(res.getString(R.string.iev_cancel));
			firstButtonRow.addView(button2);		
			
			// ADD ROWS TO main
			main.addView(emptySpace1); // 1
			main.addView(colorRow); // 2
			main.addView(emptySpace2); // 3
			main.addView(sizeRow); // 4
			main.addView(emptySpace3); // 5
			main.addView(headLabelRow); // 6
			main.addView(emptySpace4); // 7
			main.addView(editRow); // 8
			main.addView(emptySpace5); // 9
			main.addView(contentLabelRow); // 10
			main.addView(emptySpace6); // 11
			main.addView(contentEditRow); // 12
			main.addView(emptySpace7); // 13
			main.addView(firstButtonRow); // 14
		}
	}
	
	
	
	class Panel480x854
	{
		public void createGUI()
		{
			main = new LinearLayout(context);
			main.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.partlytransparent)); 
			main.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, QeePinboard.ENVIRONMENT_Y));
			main.setOrientation(LinearLayout.VERTICAL);
		
			if(y>0)
			{
				verticalpatch = new View(context);
				verticalpatch.setLayoutParams(new ViewGroup.LayoutParams(QeePinboard.ENVIRONMENT_X, y));
				main.addView(verticalpatch);
			}
			
			//
			// 1
			emptySpace1 = new LinearLayout(context);
			emptySpace1.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, 20));
			
			//
			// 2
			colorRow = new LinearLayout(context);
			colorRow.setOrientation(LinearLayout.HORIZONTAL);
			colorRow.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, 45));
			
			if(x>0)
			{
				horizontalpatch[0] = new View(context);
				horizontalpatch[0].setLayoutParams(new ViewGroup.LayoutParams(x, 45));
				colorRow.addView(horizontalpatch[0]);
			}
			
			colorLabel = new TextView(context);
			colorLabel.setGravity(Gravity.CENTER_VERTICAL); 
			colorLabel.setLayoutParams(new ViewGroup.LayoutParams(100, 45));
			colorLabel.setTextColor(Color.LTGRAY);
			colorLabel.setTextSize(18);
			colorLabel.setTypeface(Typeface.DEFAULT_BOLD);
			colorLabel.setText("    " + res.getString(R.string.iev_color));
			colorRow.addView(colorLabel);
			
			colorFreespace = new View(context);
			colorFreespace.setLayoutParams(new ViewGroup.LayoutParams(QeePinboard.displayX-198, 45));
			colorRow.addView(colorFreespace);
			
			colorDisplay = new ImageView(context);
			colorDisplay.setBackgroundDrawable(pickColoredBox());   
			colorDisplay.setLayoutParams(new ViewGroup.LayoutParams(50, 45));
			colorDisplay.setOnClickListener(manager.getEditButtonListener());
			colorDisplay.setId(EditButtonListener.INSTANTEDITVIEW_COLOR_DISPLAY);
			colorRow.addView(colorDisplay);
			
			//
			// 3
			emptySpace2 = new LinearLayout(context);
			emptySpace2.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, 10));
			
			//
			// 4
			sizeRow = new LinearLayout(context);
			sizeRow.setOrientation(LinearLayout.HORIZONTAL);
			sizeRow.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, 45));
			
			if(x>0)
			{
				horizontalpatch[1] = new View(context);
				horizontalpatch[1].setLayoutParams(new ViewGroup.LayoutParams(x, 45));
				sizeRow.addView(horizontalpatch[1]);
			}
			
			sizeLabel = new TextView(context);
			sizeLabel.setGravity(Gravity.CENTER_VERTICAL);
			sizeLabel.setLayoutParams(new ViewGroup.LayoutParams(100, 45));
			sizeLabel.setTextColor(Color.LTGRAY);
			sizeLabel.setTextSize(18);
			sizeLabel.setTypeface(Typeface.DEFAULT_BOLD);
			sizeLabel.setText("    " + res.getString(R.string.iev_size)); 
			sizeRow.addView(sizeLabel);
			
			sizeFreespace = new View(context);
			sizeFreespace.setLayoutParams(new ViewGroup.LayoutParams(QeePinboard.displayX-215, 45));
			sizeRow.addView(sizeFreespace);
			
			sizeDisplay = new TextView(context);
			sizeDisplay.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.buttony));
			sizeDisplay.setGravity(Gravity.CENTER);
			sizeDisplay.setLayoutParams(new ViewGroup.LayoutParams(85, 45));
			sizeDisplay.setTextColor(Color.BLACK);
			sizeDisplay.setTextSize(16);
			sizeDisplay.setShadowLayer(1, 1, 1, Color.WHITE);
			sizeDisplay.setOnClickListener(manager.getEditButtonListener());
			sizeDisplay.setId(EditButtonListener.INSTANTEDITVIEW_SIZE_DISPLAY);
			sizeDisplay.setText(sizeToString());
			sizeRow.addView(sizeDisplay);
			
			// 
			// 5
			emptySpace3 = new LinearLayout(context);
			emptySpace3.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, 6));
			
			//
			// 6
			headLabelRow = new LinearLayout(context);
			headLabelRow.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, 15));
			
			if(x>0)
			{
				horizontalpatch[2] = new View(context);
				horizontalpatch[2].setLayoutParams(new ViewGroup.LayoutParams(x, 15));
				headLabelRow.addView(horizontalpatch[2]);
			}
			
			headLabelLabel = new TextView(context);
			headLabelLabel.setGravity(Gravity.CENTER);
			headLabelLabel.setLayoutParams(new ViewGroup.LayoutParams(QeePinboard.displayX, 15));
			headLabelLabel.setTextColor(Color.LTGRAY);
			headLabelLabel.setTextSize(14);
			headLabelLabel.setTypeface(Typeface.DEFAULT_BOLD);
			headLabelLabel.setText(" " + res.getString(R.string.iev_head_label));
			headLabelRow.addView(headLabelLabel);
			
			// 
			// 7
			emptySpace4 = new LinearLayout(context);
			emptySpace4.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, 5));
			
			//
			// 8
			editRow = new LinearLayout(context);
			editRow.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, 100));
			
			if(x>0)
			{
				horizontalpatch[3] = new View(context);
				horizontalpatch[3].setLayoutParams(new ViewGroup.LayoutParams(x, 90));
				editRow.addView(horizontalpatch[3]);
			}
			
			editFullarea = new LinearLayout(context);
			editFullarea.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.displayX, 100));
			editFullarea.setGravity(Gravity.CENTER);
			editRow.addView(editFullarea);
			
			editInput = new InputView(context);
			editInput.setGravity(Gravity.LEFT); 
			editInput.setId(INPUT_HEAD);
			editInput.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
			editInput.setText(mElement.getHeadText());
			editInput.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.displayX-30, 100));
			editFullarea.addView(editInput);
			
			// 
			// 9
			emptySpace5 = new LinearLayout(context);
			emptySpace5.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, 2));
			
			//
			// 10
			contentLabelRow = new LinearLayout(context);
			contentLabelRow.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, 15));
			
			if(x>0)
			{
				horizontalpatch[4] = new View(context);
				horizontalpatch[4].setLayoutParams(new ViewGroup.LayoutParams(x, 15));
				contentLabelRow.addView(horizontalpatch[4]);
			}
			
			contentLabelLabel = new TextView(context);
			contentLabelLabel.setGravity(Gravity.CENTER);
			contentLabelLabel.setLayoutParams(new ViewGroup.LayoutParams(QeePinboard.displayX, 15));
			contentLabelLabel.setTextColor(Color.LTGRAY);
			contentLabelLabel.setTextSize(14);
			contentLabelLabel.setTypeface(Typeface.DEFAULT_BOLD);
			contentLabelLabel.setText(" " + res.getString(R.string.iev_details_label));
			contentLabelRow.addView(contentLabelLabel);		
			
			// 
			// 11
			emptySpace6 = new LinearLayout(context);
			emptySpace6.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, 8));
			
			//
			// 12
			contentEditRow = new LinearLayout(context);
			contentEditRow.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, 210));
			
			if(x>0)
			{
				horizontalpatch[5] = new View(context);
				horizontalpatch[5].setLayoutParams(new ViewGroup.LayoutParams(x, 210));
				contentEditRow.addView(horizontalpatch[5]);
			}
			
			contentEditFullarea = new LinearLayout(context);
			contentEditFullarea.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.displayX, 210));
			contentEditFullarea.setGravity(Gravity.CENTER);
			contentEditRow.addView(contentEditFullarea);
			
			contentEditInput = new InputView(context);
			contentEditInput.setGravity(Gravity.LEFT); 
			contentEditInput.setId(INPUT_CONTENT);
			contentEditInput.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
			contentEditInput.setText(mElement.getContentText());
			contentEditInput.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.displayX-30, 210));
			contentEditFullarea.addView(contentEditInput);
			
			// 
			// 13
			emptySpace7 = new LinearLayout(context);
			emptySpace7.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, 17));		
			
			//
			// 14
			firstButtonRow = new LinearLayout(context);
			firstButtonRow.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, 60));
			
			if(x>0)
			{
				horizontalpatch[6] = new View(context);
				horizontalpatch[6].setLayoutParams(new ViewGroup.LayoutParams(x, 60));
				firstButtonRow.addView(horizontalpatch[6]);
			}
			
			button1 = new Button(context);
			button1.setLayoutParams(new LinearLayout.LayoutParams(100, 60));
			button1.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.buttony)); 
			button1.setOnClickListener(manager.getEditButtonListener());
			button1.setId(EditButtonListener.INSTANTEDITVIEW_ADD);
			button1.setTextSize(16);
			button1.setTextColor(Color.BLACK);
			button1.setShadowLayer(1, 1, 1, Color.WHITE);
			if(mode == MODE_CREATE)
			{
				button1.setText(res.getString(R.string.iev_create)); // or Add
			}
			else
			{
				button1.setText(res.getString(R.string.iev_done));
			}
			firstButtonRow.addView(button1);
			
			firstButtonFreespace = new View(context);
			firstButtonFreespace.setLayoutParams(new ViewGroup.LayoutParams(QeePinboard.displayX-205, 60));
			firstButtonRow.addView(firstButtonFreespace);
			
			button2 = new Button(context);
			button2.setLayoutParams(new LinearLayout.LayoutParams(100, 60));
			button2.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.buttony));
			button2.setOnClickListener(manager.getEditButtonListener());
			button2.setId(EditButtonListener.INSTANTEDITVIEW_CANCEL);
			button2.setTextSize(16);
			button2.setTextColor(Color.BLACK);
			button2.setShadowLayer(1, 1, 1, Color.WHITE);
			button2.setText(res.getString(R.string.iev_cancel));
			firstButtonRow.addView(button2);		
			
			// ADD ROWS TO main
			main.addView(emptySpace1); // 1
			main.addView(colorRow); // 2
			main.addView(emptySpace2); // 3
			main.addView(sizeRow); // 4
			main.addView(emptySpace3); // 5
			main.addView(headLabelRow); // 6
			main.addView(emptySpace4); // 7
			main.addView(editRow); // 8
			main.addView(emptySpace5); // 9
			main.addView(contentLabelRow); // 10
			main.addView(emptySpace6); // 11
			main.addView(contentEditRow); // 12
			main.addView(emptySpace7); // 13
			main.addView(firstButtonRow); // 14
		}
	}
	
	
	class Panel240x320
	{
		public void createGUI()
		{
			main = new LinearLayout(context);
			main.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.partlytransparent)); 
			main.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, QeePinboard.ENVIRONMENT_Y));
			main.setOrientation(LinearLayout.VERTICAL);
		
			if(y>0)
			{
				verticalpatch = new View(context);
				verticalpatch.setLayoutParams(new ViewGroup.LayoutParams(QeePinboard.ENVIRONMENT_X, y));
				main.addView(verticalpatch);
			}
			
			//
			// 1
			emptySpace1 = new LinearLayout(context);
			emptySpace1.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, 10));
			
			//
			// 2
			colorRow = new LinearLayout(context);
			colorRow.setOrientation(LinearLayout.HORIZONTAL);
			colorRow.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, 45));
			
			if(x>0)
			{
				horizontalpatch[0] = new View(context);
				horizontalpatch[0].setLayoutParams(new ViewGroup.LayoutParams(x, 45));
				colorRow.addView(horizontalpatch[0]);
			}
			
			colorLabel = new TextView(context);
			colorLabel.setGravity(Gravity.CENTER_VERTICAL); 
			colorLabel.setLayoutParams(new ViewGroup.LayoutParams(100, 45));
			colorLabel.setTextColor(Color.LTGRAY);
			colorLabel.setTextSize(18);
			colorLabel.setTypeface(Typeface.DEFAULT_BOLD);
			colorLabel.setText("   " + res.getString(R.string.iev_color));
			colorRow.addView(colorLabel);
			
			colorFreespace = new View(context);
			colorFreespace.setLayoutParams(new ViewGroup.LayoutParams(QeePinboard.displayX-200, 45));
			colorRow.addView(colorFreespace);
			
			colorDisplay = new ImageView(context);
			colorDisplay.setBackgroundDrawable(pickColoredBox());   
			colorDisplay.setLayoutParams(new ViewGroup.LayoutParams(50, 45));
			colorDisplay.setOnClickListener(manager.getEditButtonListener());
			colorDisplay.setId(EditButtonListener.INSTANTEDITVIEW_COLOR_DISPLAY);
			colorRow.addView(colorDisplay);
			
			//
			// 3
			emptySpace2 = new LinearLayout(context);
			emptySpace2.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, 10));
			
			//
			// 4
			sizeRow = new LinearLayout(context);
			sizeRow.setOrientation(LinearLayout.HORIZONTAL);
			sizeRow.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, 45));
			
			if(x>0)
			{
				horizontalpatch[1] = new View(context);
				horizontalpatch[1].setLayoutParams(new ViewGroup.LayoutParams(x, 45));
				sizeRow.addView(horizontalpatch[1]);
			}
			
			sizeLabel = new TextView(context);
			sizeLabel.setGravity(Gravity.CENTER_VERTICAL);
			sizeLabel.setLayoutParams(new ViewGroup.LayoutParams(100, 45));
			sizeLabel.setTextColor(Color.LTGRAY);
			sizeLabel.setTextSize(18);
			sizeLabel.setTypeface(Typeface.DEFAULT_BOLD);
			sizeLabel.setText("   " + res.getString(R.string.iev_size)); 
			sizeRow.addView(sizeLabel);
			
			sizeFreespace = new View(context);
			sizeFreespace.setLayoutParams(new ViewGroup.LayoutParams(QeePinboard.displayX-217, 45));
			sizeRow.addView(sizeFreespace);
			
			sizeDisplay = new TextView(context);
			sizeDisplay.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.buttony));
			sizeDisplay.setGravity(Gravity.CENTER);
			sizeDisplay.setLayoutParams(new ViewGroup.LayoutParams(85, 45));
			sizeDisplay.setTextColor(Color.BLACK);
			sizeDisplay.setTextSize(16);
			sizeDisplay.setShadowLayer(1, 1, 1, Color.WHITE);
			sizeDisplay.setOnClickListener(manager.getEditButtonListener());
			sizeDisplay.setId(EditButtonListener.INSTANTEDITVIEW_SIZE_DISPLAY);
			sizeDisplay.setText(sizeToString());
			sizeRow.addView(sizeDisplay);
			
			// 
			// 5
			emptySpace3 = new LinearLayout(context);
			emptySpace3.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, 4));
			
			//
			// 6
			headLabelRow = new LinearLayout(context);
			headLabelRow.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, 15));
			
			if(x>0)
			{
				horizontalpatch[2] = new View(context);
				horizontalpatch[2].setLayoutParams(new ViewGroup.LayoutParams(x, 15));
				headLabelRow.addView(horizontalpatch[2]);
			}
			
			headLabelLabel = new TextView(context);
			headLabelLabel.setGravity(Gravity.CENTER);
			headLabelLabel.setLayoutParams(new ViewGroup.LayoutParams(QeePinboard.displayX, 15));
			headLabelLabel.setTextColor(Color.LTGRAY);
			headLabelLabel.setTextSize(12);
			headLabelLabel.setTypeface(Typeface.DEFAULT_BOLD);
			headLabelLabel.setText(" " + res.getString(R.string.iev_head_label));
			headLabelRow.addView(headLabelLabel);
			
			// 
			// 7
			emptySpace4 = new LinearLayout(context);
			emptySpace4.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, 5));
			
			//
			// 8
			editRow = new LinearLayout(context);
			editRow.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, 90));
			
			if(x>0)
			{
				horizontalpatch[3] = new View(context);
				horizontalpatch[3].setLayoutParams(new ViewGroup.LayoutParams(x, 90));
				editRow.addView(horizontalpatch[3]);
			}
			
			editFullarea = new LinearLayout(context);
			editFullarea.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.displayX, 90));
			editFullarea.setGravity(Gravity.CENTER);
			editRow.addView(editFullarea);
			
			editInput = new InputView(context);
			editInput.setGravity(Gravity.LEFT); 
			editInput.setId(INPUT_HEAD);
			editInput.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
			editInput.setText(mElement.getHeadText());
			editInput.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.displayX-30, 90));
			editFullarea.addView(editInput);
			
			// 
			// 9
			emptySpace5 = new LinearLayout(context);
			emptySpace5.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, 2));
			
			//
			// 10
			contentLabelRow = new LinearLayout(context);
			contentLabelRow.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, 15));
			
			if(x>0)
			{
				horizontalpatch[4] = new View(context);
				horizontalpatch[4].setLayoutParams(new ViewGroup.LayoutParams(x, 15));
				contentLabelRow.addView(horizontalpatch[4]);
			}
			
			contentLabelLabel = new TextView(context);
			contentLabelLabel.setGravity(Gravity.CENTER);
			contentLabelLabel.setLayoutParams(new ViewGroup.LayoutParams(QeePinboard.displayX, 15));
			contentLabelLabel.setTextColor(Color.LTGRAY);
			contentLabelLabel.setTextSize(12);
			contentLabelLabel.setTypeface(Typeface.DEFAULT_BOLD);
			contentLabelLabel.setText(" " + res.getString(R.string.iev_details_label));
			contentLabelRow.addView(contentLabelLabel);		
			
			// 
			// 11
			emptySpace6 = new LinearLayout(context);
			emptySpace6.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, 5));
			
			//
			// 12
			contentEditRow = new LinearLayout(context);
			contentEditRow.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, 100));
			
			if(x>0)
			{
				horizontalpatch[5] = new View(context);
				horizontalpatch[5].setLayoutParams(new ViewGroup.LayoutParams(x, 180));
				contentEditRow.addView(horizontalpatch[5]);
			}
			
			contentEditFullarea = new LinearLayout(context);
			contentEditFullarea.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.displayX, 100));
			contentEditFullarea.setGravity(Gravity.CENTER);
			contentEditRow.addView(contentEditFullarea);
			
			contentEditInput = new InputView(context);
			contentEditInput.setGravity(Gravity.LEFT); 
			contentEditInput.setId(INPUT_CONTENT);
			contentEditInput.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
			contentEditInput.setText(mElement.getContentText()); 
			contentEditInput.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.displayX-30, 100));
			contentEditFullarea.addView(contentEditInput);
			
			// 
			// 13
			emptySpace7 = new LinearLayout(context);
			emptySpace7.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, 14));		
			
			//
			// 14
			firstButtonRow = new LinearLayout(context);
			firstButtonRow.setLayoutParams(new LinearLayout.LayoutParams(QeePinboard.ENVIRONMENT_X, 60));
			
			if(x>0)
			{
				horizontalpatch[6] = new View(context);
				horizontalpatch[6].setLayoutParams(new ViewGroup.LayoutParams(x, 60));
				firstButtonRow.addView(horizontalpatch[6]);
			}
			
			button1 = new Button(context);
			button1.setLayoutParams(new LinearLayout.LayoutParams(100, 60));
			button1.setBackgroundDrawable(res.getDrawable(R.drawable.buttony)); 
			button1.setOnClickListener(manager.getEditButtonListener());
			button1.setId(EditButtonListener.INSTANTEDITVIEW_ADD);
			button1.setTextSize(16);
			button1.setTextColor(Color.BLACK);
			button1.setShadowLayer(1, 1, 1, Color.WHITE);
			if(mode == MODE_CREATE)
			{
				button1.setText(res.getString(R.string.iev_create)); // or Add
			}
			else
			{
				button1.setText(res.getString(R.string.iev_done));
			}
			firstButtonRow.addView(button1);
			
			firstButtonFreespace = new View(context);
			firstButtonFreespace.setLayoutParams(new ViewGroup.LayoutParams(QeePinboard.displayX-205, 60));
			firstButtonRow.addView(firstButtonFreespace);
			
			button2 = new Button(context);
			button2.setLayoutParams(new LinearLayout.LayoutParams(100, 60));
			button2.setBackgroundDrawable(res.getDrawable(R.drawable.buttony));
			button2.setOnClickListener(manager.getEditButtonListener());
			button2.setId(EditButtonListener.INSTANTEDITVIEW_CANCEL);
			button2.setTextSize(16);
			button2.setTextColor(Color.BLACK);
			button2.setShadowLayer(1, 1, 1, Color.WHITE);
			button2.setText(res.getString(R.string.iev_cancel));
			firstButtonRow.addView(button2);		
			
			// ADD ROWS TO main
			main.addView(emptySpace1); // 1
			main.addView(colorRow); // 2
			main.addView(emptySpace2); // 3
			main.addView(sizeRow); // 4
			main.addView(emptySpace3); // 5
			main.addView(headLabelRow); // 6
			main.addView(emptySpace4); // 7
			main.addView(editRow); // 8
			main.addView(emptySpace5); // 9
			main.addView(contentLabelRow); // 10
			main.addView(emptySpace6); // 11
			main.addView(contentEditRow); // 12
			main.addView(emptySpace7); // 13
			main.addView(firstButtonRow); // 14
		}
	}
}