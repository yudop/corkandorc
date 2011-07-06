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
import android.view.KeyEvent;
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

public class QeePinboard extends Activity implements OnGestureListener
{
	public static final int ENVIRONMENT_X = 1400;    
	public static final int ENVIRONMENT_Y = 1860;  
	
	// constants for Note Edit Activity Result
	public static final int RESULT_NOTHING = 0;
	public static final int RESULT_TEXT = 1;
	public static final int RESULT_SWITCH = 2;
	public static final int RESULT_COLOR_PICKED = 3;
	// constants to declare which input field is asking for edit
	public static final int EDIT = 0;
	public static final int EDIT_HEAD = 1; 
	public static final int EDIT_CONTENT = 2;
	public static final int EDIT_CONTENT_FROM_NOTE_DETAILS_VIEW = 3;  
	
	public static final String STORAGE_FILE_STR = "corkandorc.sav";
	
	public static boolean noteDetailsEdited = false; 
	
	public static boolean colorChanged = false;   
	public static int colorValue = 0;
	
	public static QeePinboard qeeReference;
    
    private boolean menuFlag = false; // Menu visible or not visible
    private boolean editFlag = false; // when true, the instant editing window is open (scrolling is disabled)
    private boolean detailsFlag = false; // when true, the note's details view is open (scrolling is disabled)
    
    private boolean positioningFlag = false; // when true, note is in positioning mode
    private boolean moveFlag = false;
    private boolean copyFlag = false;
   
    private MenuLayout menu;
    private MoveLayout positioningLayout;
	
	private GestureDetector gestureScanner;   
	
	private Resources res;
    
	private HackedWebView mWebView;    
    private FrameLayout main; 
    
    private WebViewClient wvc;
    
    private Display localDisplay; 
    public static int displayX;
    public static int displayY;
    
    public static int scrollX = 0;
    public static int scrollY = 0;
    public static float coordX = 0;
    public static float coordY = 0; 
    public static float selectednoteX = 0;
    public static float selectednoteY = 0; 
    public static float moveselectednoteX = 0;
    public static float moveselectednoteY = 0; 
    
    private ScrollReceiver scrollreceiver;
    private EditButtonListener mEditButtonListener;
    
    private PinboardManager pinboardManager;
    private PinboardItem pinboard;
    private ArrayList<Element> noteList;
    private ArrayList<HashMap<String,String>> representativeList;
    
    private Animation fade_in;
    private Animation fade_out;
    
    private InstantEditView ieview;
    private NoteDetailsView ndv;
    
    private Element semiCreatedElement;      
    private Element selectedElement;
    private Element moveSelectedElement;
    
    public static String info;
    
    private Object obj; 
    private Object nooobj;    
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);          
        qeeReference = this;
        
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);  
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        res = getResources();
        mEditButtonListener = new EditButtonListener(this);
        pinboardManager = new PinboardManager();
       
        pinboardManager.loadAppData(this);        
        
        pinboard = pinboardManager.getPinboardItem();
        representativeList = pinboard.getNoteList();
        scrollX = pinboard.getScrollX();
        scrollY = pinboard.getScrollY();
        gestureScanner = new GestureDetector(this);
        
        fade_in = AnimationUtils.loadAnimation(this, R.anim.fade);
        fade_out = AnimationUtils.loadAnimation(this, R.anim.outfade); 
        
        setDisplay();  
        
        mWebView = new HackedWebView(this);
        mWebView.setLayoutParams(new LinearLayout.LayoutParams(ENVIRONMENT_X,ENVIRONMENT_Y));  

        WebSettings webSettings = mWebView.getSettings(); 
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
        webSettings.setJavaScriptEnabled(true); 
         
        wvc = new MyWebViewClient();         
 
        WebChromeClient wcc = new MyWebChromeClient();             
        mWebView.setWebChromeClient(wcc);
        mWebView.setWebViewClient(wvc); 
        mWebView.loadUrl("file:///android_asset/index.html"); 
         
        main = new FrameLayout(this);
        main.setLayoutParams(new LinearLayout.LayoutParams(ENVIRONMENT_X,ENVIRONMENT_Y));   
        main.setBackgroundDrawable(getResources().getDrawable(R.drawable.pinboard_bigger));        
        mWebView.addView(main);              
        
        scrollreceiver = new ScrollReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("do scroll");
		registerReceiver(scrollreceiver, filter); 
          
        setContentView(mWebView);
        
        noteList = restoreElements();
        
        delayedUITask();         
    }
    
    @Override
    public void onResume()
    {
    	super.onResume();
    	
    	// coming back from [Menu/Options -Activity] to [Pinboard Activity] 
    	if(PinboardManager.restoreFromOptionsActivity == true)
    	{
    		PinboardManager.restoreFromOptionsActivity = false;
    		pinboardManager.refreshFromStatic();    		
    		
    		main.removeAllViews();
    		
    		pinboard = pinboardManager.getPinboardItem(); 
    		scrollX = pinboard.getScrollX();
    		scrollY = pinboard.getScrollY(); 
    		
    		representativeList = pinboard.getNoteList();
    		noteList = restoreElements();
    		
    		mWebView.scrollTo(scrollX,scrollY);  
    	}
    	// coming back from [Note Details/Content -Activity] to [Pinboard Activity]
    	else if(noteDetailsEdited == true)
    	{
    		noteDetailsEdited = false; 
    		ndv.onRefresh();
    	}
    	else if(colorChanged == true)
    	{
    		colorChanged = false;
    		int color = colorValue;
    		colorValue = 0;
  
    		if(editFlag == true)
    		{
    			if(color == EditButtonListener.COLORACTIVITY_BLUE)
				{
					ieview.setColorSelection(getResources().getDrawable(R.drawable.buttonyblue));
				}
				else if(color == EditButtonListener.COLORACTIVITY_WHITE)
				{
					ieview.setColorSelection(getResources().getDrawable(R.drawable.buttonwhite));
				}
				else if(color == EditButtonListener.COLORACTIVITY_GRAY)
				{
					ieview.setColorSelection(getResources().getDrawable(R.drawable.buttongray));
				}
				else if(color == EditButtonListener.COLORACTIVITY_YELLOW)
				{
					ieview.setColorSelection(getResources().getDrawable(R.drawable.buttonyyellow));
				}
				else if(color == EditButtonListener.COLORACTIVITY_ORANGE)
				{
					ieview.setColorSelection(getResources().getDrawable(R.drawable.buttonyorange));
				}
				else if(color == EditButtonListener.COLORACTIVITY_RED)
				{
					ieview.setColorSelection(getResources().getDrawable(R.drawable.buttonyred));
				}
				else if(color == EditButtonListener.COLORACTIVITY_GREEN)
				{
					ieview.setColorSelection(getResources().getDrawable(R.drawable.buttonygreen));    					
				}
				else if(color == EditButtonListener.COLORACTIVITY_VIOLETTE)
				{
					ieview.setColorSelection(getResources().getDrawable(R.drawable.buttonyviolette));
				}
			
				ieview.setColor(color);
    		}
    		else if(detailsFlag == true)
    		{
    			if(color == EditButtonListener.COLORACTIVITY_BLUE)
				{
					ndv.setColorSelection(getResources().getDrawable(R.drawable.buttonyblue));
				}
				else if(color == EditButtonListener.COLORACTIVITY_WHITE)
				{
					ndv.setColorSelection(getResources().getDrawable(R.drawable.buttonwhite));
				}
				else if(color == EditButtonListener.COLORACTIVITY_GRAY)
				{
					ndv.setColorSelection(getResources().getDrawable(R.drawable.buttongray));
				}
				else if(color == EditButtonListener.COLORACTIVITY_YELLOW)
				{
					ndv.setColorSelection(getResources().getDrawable(R.drawable.buttonyyellow));
				}
				else if(color == EditButtonListener.COLORACTIVITY_ORANGE)
				{
					ndv.setColorSelection(getResources().getDrawable(R.drawable.buttonyorange));
				}
				else if(color == EditButtonListener.COLORACTIVITY_RED)
				{
					ndv.setColorSelection(getResources().getDrawable(R.drawable.buttonyred));
				}
				else if(color == EditButtonListener.COLORACTIVITY_GREEN)
				{
					ndv.setColorSelection(getResources().getDrawable(R.drawable.buttonygreen));    					
				}
				else if(color == EditButtonListener.COLORACTIVITY_VIOLETTE)
				{
					ndv.setColorSelection(getResources().getDrawable(R.drawable.buttonyviolette));
				}
			
				ndv.setColor(color);
			}
    	}
    }
    
    @Override
    public void onStop()
    {
    	store();
    	super.onStop();
    } 
    
    @Override   
    public boolean onKeyDown(int keyCode, KeyEvent ev)  
    {
    	if(keyCode == KeyEvent.KEYCODE_BACK)
    	{
    		  if(editFlag)
    		  {
    			  iev_onCancel();    			  
    		  }
    		  else if(menuFlag)
    		  {
    			  menuFlag = false;
    	    	  menu.getView().startAnimation(fade_out);
    	    	  selectedElement.setNonSelectedDrawable();
    	    	  selectedElement = null;
    	    	  menu.remove();
    		  }
    		  else if(positioningFlag)
    		  {
    			  positioningFlag = false;
    			  positioningLayout.getView().startAnimation(fade_out);
      	    	  moveSelectedElement.setNonSelectedDrawable();
      	    	  positioningLayout.remove();
    		  }
    		  else if(moveFlag)
    	      {
    	    	  moveFlag = false;
    	    	  moveSelectedElement.setNonSelectedDrawable();
    	      }
    	      else if(copyFlag)
    	      {
    	    	  copyFlag = false;
    	    	  moveSelectedElement.setNonSelectedDrawable();
    	      }
    		  else if(detailsFlag)
    		  {
    			  ndv_onBack();
    		  }
    		  else
    		  {
    			  pinboardManager.refresh(pinboard);
    			  store();
    			  finish();
    			  // System.exit(0);
    		  }
    		  
    		  return true;
    	}
    	else if(keyCode == KeyEvent.KEYCODE_MENU)
    	{
    		injectNotesToPinboardItem();
    		pinboardManager.refresh(pinboard);
    		menuFlag = false; 
    		editFlag = false; 
    		detailsFlag = false; 
    		
    		Intent oIntent = new Intent();
    		oIntent.setClass(this, OptionsActivity.class);
    		startActivity(oIntent);
    		return true;
    	}
    	else
    	{
    		return super.onKeyDown(keyCode, ev);    		
    	}
    }
    
    @Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);   
	}
    
    public static void toastr(String str)
    {
    	Toast.makeText(qeeReference, str, 500).show();
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
    
    private void delayedUITask()
	{
		Timer t = new Timer();
		TimerTask task = new TimerTask()
		{
			public void run()
			{
				Intent intent = new Intent(); 
				intent.setAction("do scroll");   
				sendBroadcast(intent);
			}			
		};
		t.schedule(task, 100);		
	}
    
    public EditButtonListener getEditButtonListener()
    {
    	return mEditButtonListener;
    }
    
    public FrameLayout getMainPanel()
    {
    	return main;
    }
    
    // Instant Edit View -Add button pressed
    public void iev_onAdd()
    {
    	
    	String text;
    	Element mElement = ieview.getElement();
    	
    	text = ieview.getHeadText();
    	
    	mElement.setHeadText(text);
    	
    	text = ieview.getContentText();
    	mElement.setContentText(text);
    	
    	mElement.setSizeState(ieview.getNoteSize());
    	
    	mElement.setColor(ieview.getColor());
    	mElement.setAppropriateTextColor();
    	mElement.getTV().setBackgroundDrawable(mElement.pickColoredNote());
    	
    	ieview.remove();
    	ieview = null;
    	 
    	editFlag = false;
    }
    
    // Instant Edit View -Cancel button pressed
    public void iev_onCancel()
    {
    	// only remove semi-created note, if InstantEditView is in CREATE mode
    	if(ieview.getMode() == InstantEditView.MODE_CREATE)
    	{
    		Point mPoint = ieview.getElement().getAContainedPoint();
    		ieview.getElement().remove(main);
    	
    		if(!noteList.isEmpty())
    		{
    			for(int i= noteList.size()-1; i>= 0; i= i - 1)  
    			{
    				Element element = noteList.get(i);    		
    				if(element.getRect().contains(mPoint.x, mPoint.y))
    				{      
    					noteList.remove(i);		    		
    					break;
    				}
    			}
    		}
    	}
    	
    	ieview.remove();
    	ieview.setElement(null);
    	ieview = null;
    	System.gc();
    	
    	editFlag = false;
    }
    
    // Instant Edit View -Color button pressed
    public void iev_onColorDisplay()
    {
    	Intent colorIntent = new Intent();
    	colorIntent.setClass(this, ColorActivity.class);
    	startActivity(colorIntent); 
    }
    
    // Instant Edit View -Size button pressed
    public void iev_onSizeDisplay()
    {
    	ieview.swapNoteSize();
    }
    
    // Note Details View -Edit button pressed
    public void ndv_onEdit()
    {
    	ndv.onEdit();
    }
    
    // Note Details View -Back button pressed
    public void ndv_onBack()
    {
    	ndv.onCancel();
    	// ndv.getView().startAnimation(fade_out);
    	ndv.remove(); 
    	detailsFlag = false;
    }
    
    // Note Details View -Color button pressed
    public void ndv_onColorDisplay()
    {
    	Intent colorIntent = new Intent();
    	colorIntent.setClass(this, ColorActivity.class);
    	startActivity(colorIntent);
    }
    
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
    {  
    	if(menuFlag)
    	{
    		menuFlag = false;
    		menu.getView().startAnimation(fade_out);
    		selectedElement.setNonSelectedDrawable();
    		selectedElement = null;
    		menu.remove();
    	}
    	else if(positioningFlag)
    	{
    		positioningFlag = false;
    		positioningLayout.getView().startAnimation(fade_out);
    		moveSelectedElement.setNonSelectedDrawable();
    		positioningLayout.remove();
    	}
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
    	if(moveFlag)
    	{
    		moveFlag = false;
    		moveSelectedElement.setNonSelectedDrawable();
    		return;
    	}
    	else if(copyFlag)
    	{
    		copyFlag = false;
    		moveSelectedElement.setNonSelectedDrawable();
    		return;
    	}
    	else if(positioningFlag)
    	{
    		positioningFlag = false;
    		positioningLayout.getView().startAnimation(fade_out);
    		moveSelectedElement.setNonSelectedDrawable();
    		positioningLayout.remove();
    		return;
    	}
    	else if((!noteList.isEmpty()) && (!positioningFlag))
		{
			// make Menu visible (if a note has been pressed)
			for(int i= noteList.size()-1; i>= 0; i= i - 1)  
			{
				Element element = noteList.get(i);    		
				if(element.getRect().contains((int)coordX, (int)coordY))
				{    				
					if(menuFlag == false)
			    	{
						positioningFlag = true;
			    		moveselectednoteX = coordX;
			    		moveselectednoteY = coordY;
			    		moveSelectedElement = element;
			    		moveSelectedElement.setSelectedDrawable();
			    		positioningLayout = new MoveLayout(this, main, scrollX, scrollY);
			    		positioningLayout.add();
			    		positioningLayout.getView().startAnimation(fade_in);   
			    	}
			     	    		
					return;
				}
			}
		}
    }
    
    public void onShowPress(MotionEvent e) 
    {  
    	
    }    
    
    public boolean onSingleTapUp(MotionEvent e)    
    {     
    	// MOVE OR COPY NOTE TO NEW POSITION OR CANCEL
    	if(positioningFlag == true)
    	{
    		int tx = (int)e.getX();
    		int ty = (int)e.getY();
    		
    		if(positioningLayout.getMoveRect().contains(tx, ty))
    		{
    			positioningFlag = false; 
    			positioningLayout.getView().startAnimation(fade_out);   
    			positioningLayout.remove();
    	    	
    			moveSelectedElement.setMoveDrawable(); 
    			moveFlag = true;
    			
    			return true;
    		}
    		else if(positioningLayout.getCopyRect().contains(tx, ty))
    		{
    			positioningFlag = false; 
    			positioningLayout.getView().startAnimation(fade_out);
    	    	positioningLayout.remove();
    	    	
    	    	moveSelectedElement.setCopyDrawable(); 
    			copyFlag = true;
    			
    			return true; 
    		}
    		else 
    		{
    			positioningFlag = false;
    			positioningLayout.getView().startAnimation(fade_out);
    	    	moveSelectedElement.setNonSelectedDrawable();
    	    	positioningLayout.remove();
    	    	
    	    	return true; 
    		}
    	}
    	
    	
    	if(moveFlag == true)
    	{
    		int foundIndex = -1;
    		
    		if(!noteList.isEmpty())
    		{
    			// make Menu visible (if a note has been pressed)
    			for(int i= noteList.size()-1; i>= 0; i= i - 1)  
    			{
    				Element element = noteList.get(i);    		
    				if(element.getRect().contains((int)coordX, (int)coordY))
    				{    
    					moveSelectedElement.setNonSelectedDrawable();
    					moveFlag = false;
    					return true;
    				}
    				
    				if(element.equals(moveSelectedElement))
    				{
    					foundIndex = i;
    				}
    			}
    		}
    		
    		if(foundIndex >= 0)
    		{
    			noteList.remove(foundIndex);
    			moveSelectedElement.remove(main);
    	    	Element el = new Element(this,(int)coordX - 50, (int)coordY - 50, moveSelectedElement.getMapRepresentative());
    	    	el.getTV().startAnimation(fade_in);
    	    	el.add(main);
    	    	noteList.add(el);
    	    	moveFlag = false;
    		}
    		
    		return true;
    	}
    	
    	
    	if(copyFlag == true)
    	{
    		if(!noteList.isEmpty())
    		{
    			// make Menu visible (if a note has been pressed)
    			for(int i= noteList.size()-1; i>= 0; i= i - 1)  
    			{
    				Element element = noteList.get(i);    		
    				if(element.getRect().contains((int)coordX, (int)coordY))
    				{    
    					moveSelectedElement.setNonSelectedDrawable();
    					copyFlag = false;
    					return true;
    				}
    			}
    		}
    		
    		Element el = new Element(this,(int)coordX - 50, (int)coordY - 50, moveSelectedElement.getMapRepresentative());
    		el.getTV().startAnimation(fade_in);
    		el.add(main);
	    	noteList.add(el);
	    	moveSelectedElement.setNonSelectedDrawable();
	    	copyFlag = false;
	    	  		
    		return true;
    	}
    	
    	
    	if(menuFlag == true)
    	{
    		int tx = (int)e.getX();
    		int ty = (int)e.getY();
    		
    		// EDIT NOTE
    		if(menu.getEditRect().contains(tx, ty))
    		{
    			ieview = new InstantEditView(this,this,selectedElement,scrollX,scrollY,InstantEditView.MODE_EDIT);
    			
    			menuFlag = false;
        		selectedElement.setNonSelectedDrawable();
        		selectedElement = null;
        		menu.remove();
    			
    	    	ieview.add(); // add instant editing popup to view
    	    	
    	    	editFlag = true; // scrolling will be locked as long as instant editing window is open
    		}
    		else if(menu.getBackRect().contains(tx, ty))
    		{
    			
    	    	menuFlag = false;
    	    	menu.getView().startAnimation(fade_out);
    	    	selectedElement.setNonSelectedDrawable();
    	    	selectedElement = null;
    	    	menu.remove();
    	    	
    	    	return true; 
    		}
    		// Menu > [SIZE ONE] BUTTON pressed
    		else if(menu.getSizeoneRect().contains(tx, ty))
    		{
    			if(!noteList.isEmpty())
        		{
        			for(int i= noteList.size()-1; i>= 0; i= i - 1)  
        			{
        				Element element = noteList.get(i);    		
        				if(element.getRect().contains((int)selectednoteX, (int)selectednoteY))
        				{      
        					int sizeState = element.getSizeState();
        					
        					// MEDIUM +++ LARGE
        					if(sizeState == Element.SIZE_SMALL)
        					{
        						element.setSizeState(Element.SIZE_MEDIUM);
        					}
        					// SMALL +++ LARGE
        					else if(sizeState == Element.SIZE_MEDIUM)
        					{
        						element.setSizeState(Element.SIZE_SMALL);
        					}
        					// SMALL +++ MEDIUM
        					else
        					{
        						element.setSizeState(Element.SIZE_SMALL);
        					}
        					
        					menuFlag = false;
        		    		menu.getView().startAnimation(fade_out);
        		    		selectedElement.setNonSelectedDrawable();
        		    		selectedElement = null;
        		    		menu.remove();
        		    		
        					return true;
        				}
        			}
        		}
    		}
    		// Menu > [SIZE TWO] BUTTON pressed
    		else if(menu.getSizetwoRect().contains(tx, ty))
    		{
    			if(!noteList.isEmpty())
        		{
        			for(int i= noteList.size()-1; i>= 0; i= i - 1)  
        			{
        				Element element = noteList.get(i);    		
        				if(element.getRect().contains((int)selectednoteX, (int)selectednoteY))
        				{      
        					int sizeState = element.getSizeState();
        					
        					// MEDIUM +++ LARGE
        					if(sizeState == Element.SIZE_SMALL)
        					{
        						element.setSizeState(Element.SIZE_LARGE);
        					}
        					// SMALL +++ LARGE
        					else if(sizeState == Element.SIZE_MEDIUM)
        					{
        						element.setSizeState(Element.SIZE_LARGE);
        					}
        					// SMALL +++ MEDIUM
        					else
        					{
        						element.setSizeState(Element.SIZE_MEDIUM);
        					}
        					
        					menuFlag = false;
        		    		menu.getView().startAnimation(fade_out);
        		    		selectedElement.setNonSelectedDrawable();
        		    		selectedElement = null;
        		    		menu.remove();
        		    		
        					return true;
        				}
        			}
        		}
    		}
    		// Menu > [SIZE TWO] BUTTON pressed
    		else if(menu.getDetailsRect().contains(tx, ty))
    		{    	    			
    			ndv = new NoteDetailsView(this,selectedElement,scrollX,scrollY);
    			ndv.add();   	
    			// ndv.getView().startAnimation(fade_in);
    			detailsFlag = true;
    			
    			menuFlag = false;
        		selectedElement.setNonSelectedDrawable();
        		selectedElement = null;
        		menu.remove();
    		}
    		// DELETE NOTE
    		else if(menu.getDeleteRect().contains(tx, ty))
    		{
    			if(!noteList.isEmpty())
        		{
        			for(int i= noteList.size()-1; i>= 0; i= i - 1)  
        			{
        				Element element = noteList.get(i);    		
        				if(element.getRect().contains((int)selectednoteX, (int)selectednoteY))
        				{      
        					element.startAnimation(fade_out);
        					element.remove(main);
        					noteList.remove(i);
        					
        					menuFlag = false;
        		    		menu.getView().startAnimation(fade_out);
        		    		selectedElement.setNonSelectedDrawable();
        		    		selectedElement = null;
        		    		menu.remove();
        		    		
        					return true;
        				}
        			}
        		}
    		}
    		// CLOSE MENU
    		else
    		{    	
    			mcluster:
    			{
    				if(!noteList.isEmpty())
    				{
    					for(int i= noteList.size()-1; i>= 0; i= i - 1)  
    					{
    						Element element = noteList.get(i);    		
    						if(element.getRect().contains((int)coordX, (int)coordY))
    						{    	
    							if(element.equals(selectedElement))
    							{
    								break mcluster; 
    							}
    							selectedElement.setNonSelectedDrawable();
    							selectednoteX = coordX;
    							selectednoteY = coordY; 
    							selectedElement = element;
    							selectedElement.setSelectedDrawable();
    							menu.remove();
        			    		menu = new MenuLayout(this,main,scrollX,scrollY,element.getSizeState());
        			    		menu.add();
        			    		menu.getView().startAnimation(fade_in);         			    	
        			     	    		
        			    		return true;
    						}
    					}
    				}
    			}
    			
    			menuFlag = false;
        		menu.getView().startAnimation(fade_out);
        		selectedElement.setNonSelectedDrawable();
        		selectedElement = null;
        		menu.remove();    			
    		}
    	}
    	// SHOW MENU
    	else
    	{    	
    		if(!noteList.isEmpty())
    		{
    			// make Menu visible (if a note has been pressed)
    			for(int i= noteList.size()-1; i>= 0; i= i - 1)  
    			{
    				Element element = noteList.get(i);    		
    				if(element.getRect().contains((int)coordX, (int)coordY))
    				{    				
    					if(menuFlag == false)
    			    	{
    			    		menuFlag = true;
    			    		selectednoteX = coordX;
    			    		selectednoteY = coordY;
    			    		selectedElement = element;
    			    		selectedElement.setSelectedDrawable();
    			    		menu = new MenuLayout(this,main,scrollX,scrollY,element.getSizeState());
    			    		menu.add();
    			    		menu.getView().startAnimation(fade_in);   
    			    	}
    			     	    		
    					return true;
    				}
    			}
    		}
    	
    		// open Note creation Window (if pinboard has been pressed)
    		
    		createNote();
    	}
    	
        return true;
    }
    
    private Element createNote()
    {
    	Element el = new Element(this, (int)coordX-50, (int)coordY-50,150,168);
    	el.setSizeState(Element.SIZE_SMALL);
    	semiCreatedElement = el;
    	el.add(main);
    	noteList.add(el);
    	
    	ieview = new InstantEditView(this,this,semiCreatedElement,scrollX,scrollY,InstantEditView.MODE_CREATE);
    	ieview.add(); // add instant editing popup to view
    	
    	editFlag = true; // scrolling will be locked as long as instant editing window is open
    	
    	return el;
    }
    
    // transform Elements to Map Representative Mode
    private void injectNotesToPinboardItem()
    {    
    	pinboard.setScrollX(scrollX);
    	pinboard.setScrollY(scrollY);
    	
    	representativeList = new ArrayList<HashMap<String,String>>();
    	
    	if(!noteList.isEmpty())
		{    		
    		for(int i=0; i<noteList.size(); i++)
    		{
    			Element el = noteList.get(i);
    			HashMap<String,String> tmp = el.getMapRepresentative();
    			representativeList.add(tmp);
    		}
		}
    	
    	pinboard.setNoteList(representativeList);
    }
    
    // restore elements from representative map
    public ArrayList<Element> restoreElements()
    {
    	ArrayList<Element> mList = new ArrayList<Element>();
    	
    	if(representativeList.isEmpty())
    	{
    		return mList;
    	}
    	else
    	{    	
    		for(int i=0; i< representativeList.size(); i++)
    		{
    			HashMap<String,String> representative = representativeList.get(i);
    			Element el = new Element(this, representative);
    			mList.add(el);
				el.add(main);    			
    		}    		
    		
    		return mList;
    	}    	
  
    	// Toast.makeText(this, "not empty", 2000).show();
    }
    
    public void store()
    {
    	injectNotesToPinboardItem();
		pinboardManager.saveAppData(this); // store pinboard stats
    }
    
    public void checktv(String mStr)
    {
    	/*
    	StateToStringTranslator ms = new StateToStringTranslator(pinboardManager.getStateArchive());
		ms.translateToString();
		String mStr = ms.getStateString();
		*/
    	
    	/*
    	long sta = System.currentTimeMillis();
    	StringToStateParser ms = new StringToStateParser();
    	String mStr = ms.getAppData();
    	long stp = System.currentTimeMillis();
    	mStr = new Long(stp-sta).toString() + mStr;
    	*/
    	
    	TextView tv = new TextView(this);
    	tv.setLayoutParams(new ViewGroup.LayoutParams(displayX,displayY));
    	tv.setBackgroundColor(Color.WHITE);
    	tv.setTextColor(Color.BLACK);
    	tv.setText(mStr);
    	setContentView(tv);
    }
    
    class HackedWebView extends WebView 
    {
    	private Context context;
    	
    	public HackedWebView(Context context) 
    	{
    		super(context); 
    		this.context = context;  
    	}
    	
    	@Override
    	public boolean onKeyDown(int keyCode, KeyEvent ev)
    	{
    		if((keyCode == KeyEvent.KEYCODE_DPAD_CENTER) 
        			|| (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)
        			|| (keyCode == KeyEvent.KEYCODE_DPAD_LEFT)
        			|| (keyCode == KeyEvent.KEYCODE_DPAD_UP)
        			|| (keyCode == KeyEvent.KEYCODE_DPAD_DOWN))    		
        	{        		
        		if(menuFlag || editFlag || detailsFlag)
        		{
        			return true;
        		}
        		else
        		{
        			super.onKeyDown(keyCode, ev);
        			return QeePinboard.this.onKeyDown(keyCode, ev);      
        		}
        	}
    		else
    		{
    			super.onKeyDown(keyCode, ev);
    			return QeePinboard.this.onKeyDown(keyCode, ev);
    		}
    	}
    	
    	@Override 
    	public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event)
    	{
    		if((keyCode == KeyEvent.KEYCODE_DPAD_CENTER) 
        			|| (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)
        			|| (keyCode == KeyEvent.KEYCODE_DPAD_LEFT)
        			|| (keyCode == KeyEvent.KEYCODE_DPAD_UP)
        			|| (keyCode == KeyEvent.KEYCODE_DPAD_DOWN))    		
        	{        		
        		if(menuFlag || editFlag || detailsFlag)
        		{
        			return true;
        		}
        		else
        		{
        			super.onKeyMultiple(keyCode, repeatCount, event);
        			return QeePinboard.this.onKeyDown(keyCode, event);
        		}
        	}
    		else
    		{
    			super.onKeyMultiple(keyCode, repeatCount, event);
    			return QeePinboard.this.onKeyDown(keyCode, event);
    		}
    	}
    	
    	@Override
        public boolean onTouchEvent(MotionEvent me) 
        {   
    		if(!editFlag && !detailsFlag)
    		{
    			super.onTouchEvent(me); 
    			
    			int tmpScrollX = scrollX;
    			int tmpScrollY = scrollY;
    		
    			scrollX = getScrollX(); 
    			scrollY = getScrollY();
    			coordX = me.getX() + getScrollX(); 
    			coordY = me.getY() + getScrollY();
    			
    			if(menuFlag)
    			{
    				if((scrollX != tmpScrollX) || (scrollY != tmpScrollY))
    				{
    					menuFlag = false;
    		    		menu.getView().startAnimation(fade_out);
    		    		selectedElement.setNonSelectedDrawable();
    		    		selectedElement = null;
    		    		menu.remove();
    				}
    			}
    			else if(positioningFlag)
    			{
    				if((scrollX != tmpScrollX) || (scrollY != tmpScrollY))
    				{
    					positioningFlag = false;
    					positioningLayout.getView().startAnimation(fade_out);
    		    		moveSelectedElement.setNonSelectedDrawable();
    		    		positioningLayout.remove();
    				}
    			}
    			
    			return gestureScanner.onTouchEvent(me);
    		}
    		
        	return true; 
        }
    }
    
    class MyWebViewClient extends WebViewClient
    {
    	@Override
    	public void onPageFinished(WebView view, String url)
    	{ 
    		/*
    		mWebView.loadUrl("javascript:(function() { " +  
    				                "document.getElementsByTagName('body')[0].style.width = '1380px'; " +  
    				                "document.getElementsByTagName('body')[0].style.height = '1840px'; " +
    				                 "})()"); 
    		 */     				                 
    	}
    	
    }   

    final class MyWebChromeClient extends WebChromeClient 
    {     
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            result.confirm();
            return true;
        }
    }
    
    class ScrollReceiver extends BroadcastReceiver
	{
		@Override 
	    public void onReceive(Context context, Intent intent)
		{
			if(intent.getAction().equals("do scroll"))
			{
				mWebView.scrollTo(scrollX,scrollY); 
			}
	   }
	}
}