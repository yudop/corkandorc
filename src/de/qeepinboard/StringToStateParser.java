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

import android.view.*;
import android.widget.*;
import android.app.*;
import android.content.*;
import java.io.*;
import java.util.*;

public class StringToStateParser 
{
	public static final int TAGSPACE = 3;
	
	private Activity parent;
	
	private String ret = "";
	private int pointer = 0;
	private StringBuilder strbuild;
	
	private String currentlyActivePinboard_name = "";
	private ArrayList<String> pinboardSections = null;
	
	public StringToStateParser(Activity parent)
	{
		// loadAppData();
		this.parent = parent;
	}
	
	public StringToStateParser(String ret)
	{
		this.ret = ret;
	}
	
	private void loadAppData()
	{		
		try
    	{
			strbuild = new StringBuilder();
			strbuild.append("");
			
			FileReader filereader = new FileReader("/sdcard/corkandorc/corkandorc.sav");
			BufferedReader breader = new BufferedReader(filereader); 
			char[] buf = new char[256];
			while((-1 != breader.read(buf)))		
			{
				strbuild.append(buf);
			}
    	}
    	catch(Exception ex)
    	{
    		 
    	}  
    	
    	ret = strbuild.toString();
	}
	
	public void setString(String ret)
	{
		this.ret = ret;
	}
	
	public String getAppData()
	{
		return ret;
	}
	
	public StateArchive parse()
	{
		StateArchive xStateArchive = null;
		
		try
		{
			xStateArchive = mparse();
		}
		catch(Exception x)
		{
			
		}
		
		return xStateArchive; 
	}
	
	public StateArchive mparse() 
	{	
		if(ret.equals("") || (ret == null))
		{
			return null;
		}
		
		StateArchive mStateArchive = new StateArchive(true);
		PinboardArchive mPinboardArchive = new PinboardArchive(true);			
		
		currentlyActivePinboard_name = parseCurrentlyActivePinboard(); // 1.)	
		pinboardSections = splitPinboardSections(); // 2.)			
		
		if(pinboardSections.isEmpty()) // 3.)
		{
			return null;
		}
		else
		{		
			PinboardItem[] pi = new PinboardItem[pinboardSections.size()]; 
			
			for(int i=0; i<pinboardSections.size(); i++)
			{
				String pbstr = pinboardSections.get(i);
				String pbname = parsePinboardName(pbstr); 				  
				String pbscrollx = parsePinboardScrollX(pbstr);				  
				String pbscrolly = parsePinboardScrollY(pbstr);	
				
				ArrayList<String> notesections = parseNoteSections(pbstr);		
							
				//
				//
				pi[i] = new PinboardItem(pbname);				
				
				try
			    {
					pi[i].setScrollX(Integer.parseInt(pbscrollx));
			    }
				catch(NumberFormatException ex)
				{
					pi[i].setScrollX(0);
			    }
				
				try
				{
					pi[i].setScrollY(Integer.parseInt(pbscrolly));
				}
				catch(NumberFormatException ex)
				{
					pi[i].setScrollY(0);
				}
				//
				//							
				
				if(notesections.isEmpty())
				{					
					
				}
				else
				{
					// StringBuilder test = new StringBuilder();			
					
					for(int j=0; j<notesections.size(); j++)
					{
						String parameter = notesections.get(j);
						
						String x = parseX(parameter);
						String y = parseY(parameter);
						String width = parseWidth(parameter);
						String height = parseHeight(parameter);
						String sizestate = parseSizeState(parameter);
						String color = parseColor(parameter);
						String headtext = parseHeadText(parameter);
						String contenttext = parseContentText(parameter);
						
						HashMap<String,String> rep = new HashMap<String,String>();
						
						rep.put("x", x);
						rep.put("y", y);
						rep.put("width", width);
						rep.put("height", height);
						rep.put("sizeState", sizestate);
						rep.put("color", color);
						rep.put("headText", headtext);
						rep.put("contentText", contenttext);	
						
						pi[i].add(rep); 
					}
					// QeePinboard.info = test.toString();
				}
				mPinboardArchive.put(pi[i]);				
			}			
		}		
		
		mStateArchive.put(mPinboardArchive);
		mStateArchive.setCurrentlyActivePinboardArchive(mPinboardArchive);
		mPinboardArchive.setCurrentlyActivePinboard(currentlyActivePinboard_name);	
		
		return mStateArchive;
	}
	
	
	public String parseCurrentlyActivePinboard()
	{
		String tmp = "";
		
		int start = ret.indexOf(StateToStringTranslator.CURRENTLY_ACTIVE_PINBOARD, 0);
		int end = ret.indexOf(StateToStringTranslator.CL_CURRENTLY_ACTIVE_PINBOARD, 0);
		
		if((start == -1) || (end == -1))
		{
			return tmp;
		}
		else
		{
			start = start + TAGSPACE;
			pointer = end + TAGSPACE;
			
			try
			{
				tmp = ret.substring(start, end);
			}
			catch(IndexOutOfBoundsException ioobe)
			{
				
			}
			
			return tmp;
		}
	}
	
	public ArrayList<String> splitPinboardSections()
	{
		ArrayList<String> sections = new ArrayList<String>();
		
		int flag = 0;
		int cl_flag = 0;
		int counter = 0;
		while((flag != -1) && (cl_flag != -1))
		{
			flag = ret.indexOf(StateToStringTranslator.PINBOARD, counter);
			cl_flag = ret.indexOf(StateToStringTranslator.CL_PINBOARD, counter);
			
			if((flag != -1) && (cl_flag != -1))
			{
				try
				{
					sections.add(ret.substring(flag+TAGSPACE, cl_flag));
					counter = cl_flag + TAGSPACE;
				}
				catch(IndexOutOfBoundsException ioobe)
				{
					break;
				}
			}
		}		
				
		return sections;			
	}
	
	public String parsePinboardName(String pbstr)
	{
		String tmp = "";
		
		int start = pbstr.indexOf(StateToStringTranslator.NAME, 0);
		int end = pbstr.indexOf(StateToStringTranslator.CL_NAME, 0);
		
		if((start == -1) || (end == -1))
		{
			return tmp;
		}
		else
		{
			start = start + TAGSPACE;
			pointer = end + TAGSPACE;
			
			try
			{
				tmp = pbstr.substring(start, end);
			}
			catch(IndexOutOfBoundsException ioobe)
			{
				
			}
			
			return tmp;
		}
	}
	
	public String parsePinboardScrollX(String pbstr)
	{
		String tmp = "";
		
		int start = pbstr.indexOf(StateToStringTranslator.SCROLLX, 0);
		int end = pbstr.indexOf(StateToStringTranslator.CL_SCROLLX, 0);
		
		if((start == -1) || (end == -1))
		{
			return tmp;
		}
		else
		{
			start = start + TAGSPACE;
			pointer = end + TAGSPACE;
			
			try
			{
				tmp = pbstr.substring(start, end);
			}		
			catch(IndexOutOfBoundsException ioobe)
			{
				
			}
			return tmp;
		}
	}
	
	public String parsePinboardScrollY(String pbstr)
	{
		String tmp = "";
		
		int start = pbstr.indexOf(StateToStringTranslator.SCROLLY, 0);
		int end = pbstr.indexOf(StateToStringTranslator.CL_SCROLLY, 0);
		
		if((start == -1) || (end == -1))
		{
			return tmp;
		}
		else
		{
			start = start + TAGSPACE;
			pointer = end + TAGSPACE;
			
			try
			{
				tmp = pbstr.substring(start, end);
			}
			catch(IndexOutOfBoundsException ioobe)
			{
				
			}
			
			return tmp;
		}
	}
	
	public ArrayList<String> parseNoteSections(String pbstr)
	{
		ArrayList<String> notesections = new ArrayList<String>();
		
		int flag = 0;
		int cl_flag = 0;
		int counter = 0;
		while((flag != -1) && (cl_flag != -1))
		{
			flag = pbstr.indexOf(StateToStringTranslator.NOTE, counter);
			cl_flag = pbstr.indexOf(StateToStringTranslator.CL_NOTE, counter);
			
			if((flag != -1) && (cl_flag != -1))
			{
				try
				{
					notesections.add(pbstr.substring(flag+TAGSPACE, cl_flag));
					counter = cl_flag + TAGSPACE;
				}
				catch(IndexOutOfBoundsException ioobe)
				{
					break;
				}				
			}
		}		
			
		return notesections;
			
	}
	
	public String parseX(String notestr)
	{
		String tmp = "";
		
		int start = notestr.indexOf(StateToStringTranslator.X, 0);
		int end = notestr.indexOf(StateToStringTranslator.CL_X, 0);
		
		if((start == -1) || (end == -1))
		{
			return tmp;
		}
		else
		{
			start = start + TAGSPACE;
			pointer = end + TAGSPACE;
			
			try
			{
				tmp = notestr.substring(start, end);
			}
			catch(IndexOutOfBoundsException ioobe)
			{
				
			}
			
			return tmp;
		}
	}
	
	public String parseY(String notestr)
	{
		String tmp = "";
		
		int start = notestr.indexOf(StateToStringTranslator.Y, 0);
		int end = notestr.indexOf(StateToStringTranslator.CL_Y, 0);
		
		if((start == -1) || (end == -1))
		{
			return tmp;
		}
		else
		{
			start = start + TAGSPACE;
			pointer = end + TAGSPACE;
			
			try
			{
				tmp = notestr.substring(start, end);
			}
			catch(IndexOutOfBoundsException ioobe)
			{
			
			}
			
			return tmp;
		}
	}
	
	public String parseWidth(String notestr)
	{
		String tmp = "";
		
		int start = notestr.indexOf(StateToStringTranslator.WIDTH, 0);
		int end = notestr.indexOf(StateToStringTranslator.CL_WIDTH, 0);
		
		if((start == -1) || (end == -1))
		{
			return tmp;
		}
		else
		{
			start = start + TAGSPACE;
			pointer = end + TAGSPACE;
			
			try
			{
				tmp = notestr.substring(start, end);
			}
			catch(IndexOutOfBoundsException ioobe)
			{
				
			}
				
			return tmp;
		}
	}
	
	public String parseHeight(String notestr)
	{
		String tmp = "";
		
		int start = notestr.indexOf(StateToStringTranslator.HEIGHT, 0);
		int end = notestr.indexOf(StateToStringTranslator.CL_HEIGHT, 0);
		
		if((start == -1) || (end == -1))
		{
			return tmp;
		}
		else
		{
			start = start + TAGSPACE;
			pointer = end + TAGSPACE;
			
			try
			{
			
				tmp = notestr.substring(start, end);
			}
			catch(IndexOutOfBoundsException ioobe)
			{
				
			}
				
			return tmp;
		}
	}
	
	public String parseSizeState(String notestr)
	{
		String tmp = "";
		
		int start = notestr.indexOf(StateToStringTranslator.SIZE_STATE, 0);
		int end = notestr.indexOf(StateToStringTranslator.CL_SIZE_STATE, 0);
		
		if((start == -1) || (end == -1))
		{
			return tmp;
		}
		else
		{
			start = start + TAGSPACE;
			pointer = end + TAGSPACE;
			
			try
			{
				tmp = notestr.substring(start, end);
			}
			catch(IndexOutOfBoundsException ioobe)
			{
				
			}
			
			return tmp;
		}
	}
	
	public String parseColor(String notestr)
	{
		String tmp = "";
		
		int start = notestr.indexOf(StateToStringTranslator.COLOR, 0);
		int end = notestr.indexOf(StateToStringTranslator.CL_COLOR, 0);
		
		if((start == -1) || (end == -1))
		{
			return tmp;
		}
		else
		{
			start = start + TAGSPACE;
			pointer = end + TAGSPACE;
			
			try
			{
				tmp = notestr.substring(start, end);
			}
			catch(IndexOutOfBoundsException ioobe)
			{
				
			}
			
			return tmp;
		}
	}
	
	public String parseHeadText(String notestr)
	{
		String tmp = "";
		
		int start = notestr.indexOf(StateToStringTranslator.HEAD_TEXT, 0);
		int end = notestr.indexOf(StateToStringTranslator.CL_HEAD_TEXT, 0);
		
		if((start == -1) || (end == -1))
		{
			return tmp;
		}
		else
		{
			start = start + TAGSPACE;
			pointer = end + TAGSPACE;
			
			try
			{
				tmp = notestr.substring(start, end);
			}
			catch(IndexOutOfBoundsException ioobe)
			{
				
			}
			
			return tmp;
		}
	}
	
	public String parseContentText(String notestr)
	{
		String tmp = "";
		
		int start = notestr.indexOf(StateToStringTranslator.CONTENT_TEXT, 0);
		int end = notestr.indexOf(StateToStringTranslator.CL_CONTENT_TEXT, 0);
		
		if((start == -1) || (end == -1))
		{
			return tmp;
		}
		else
		{
			start = start + TAGSPACE;
			pointer = end + TAGSPACE;
			
			try
			{
			
				tmp = notestr.substring(start, end);
			}
			catch(IndexOutOfBoundsException ioobe)
			{
				
			}
			
			return tmp;
		}
	}
}