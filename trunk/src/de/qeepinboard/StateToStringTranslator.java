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

import java.util.*;
import java.io.*;

public class StateToStringTranslator 
{
	// tags
	public static final String CURRENTLY_ACTIVE_PINBOARD_ARCHIVE = "<a>";
	public static final String CURRENTLY_ACTIVE_PINBOARD = "<b>";
	public static final String PINBOARD_ARCHIVE = "<c>";
	public static final String PINBOARD_ARCHIVE_NAME = "<d>";
	public static final String PINBOARD = "<e>";
	public static final String NAME = "<f>";
	public static final String SCROLLX = "<g>";
	public static final String SCROLLY = "<h>";
	public static final String NOTE = "<i>";
	public static final String X = "<j>";
	public static final String Y = "<k>";
	public static final String WIDTH = "<l>";
	public static final String HEIGHT = "<m>";
	public static final String SIZE_STATE = "<n>";
	public static final String COLOR = "<o>";
	public static final String HEAD_TEXT = "<p>";
	public static final String CONTENT_TEXT = "<q>";
	
	public static final String END = "<r>";
	
	public static final String CL_CURRENTLY_ACTIVE_PINBOARD_ARCHIVE = "<A>";
	public static final String CL_CURRENTLY_ACTIVE_PINBOARD = "<B>";
	public static final String CL_PINBOARD_ARCHIVE = "<C>";
	public static final String CL_PINBOARD_ARCHIVE_NAME = "<D>";
	public static final String CL_PINBOARD = "<E>";
	public static final String CL_NAME = "<F>";
	public static final String CL_SCROLLX = "<G>";
	public static final String CL_SCROLLY = "<H>";
	public static final String CL_NOTE = "<I>";
	public static final String CL_X = "<J>";
	public static final String CL_Y = "<K>";
	public static final String CL_WIDTH = "<L>";
	public static final String CL_HEIGHT = "<M>";
	public static final String CL_SIZE_STATE = "<N>";
	public static final String CL_COLOR = "<O>";
	public static final String CL_HEAD_TEXT = "<P>";
	public static final String CL_CONTENT_TEXT = "<Q>"; 
	
	private StateArchive mStateArchive;
	private StringBuilder strBuilder;
	private String stateString = "";
	
	public StateToStringTranslator(StateArchive mStateArchive)
	{
		this.mStateArchive = mStateArchive;
		strBuilder = new StringBuilder();
	}
	
	public String translateToString()
	{		
		if(mStateArchive == null)
		{
			return "";
		}
		
		 appendCurrentlyActivePinboard();
		 appendPinboards(mStateArchive.getCurrentlyActivePinboardArchive());
		 strBuilder.append(END);
		 String ret = strBuilder.toString();
		 strBuilder = new StringBuilder();
		 return ret;
		 
	}
	
	/* #1
	private void appendCurrentlyActivePinboardArchive()
	{
		String tmp = "";
		
		strBuilder.append(CURRENTLY_ACTIVE_PINBOARD_ARCHIVE);
		tmp = getCurrentlyActivePinboardArchiveName();
		strBuilder.append(tmp);
		strBuilder.append(CL_CURRENTLY_ACTIVE_PINBOARD_ARCHIVE);
	}
	*/
	
	// #1b
	private void appendCurrentlyActivePinboard()
	{
		String tmp = "";
		
		strBuilder.append(CURRENTLY_ACTIVE_PINBOARD);
		tmp = getCurrentlyActivePinboardName();
		strBuilder.append(tmp);
		strBuilder.append(CL_CURRENTLY_ACTIVE_PINBOARD);
	}
	
	/*
	private void appendPinboardArchives()
	{
		ArrayList<String> archiveNames = mStateArchive.getArchiveNames();
		for(String mStr : archiveNames)
		{
			strBuilder.append(PINBOARD_ARCHIVE);
			strBuilder.append(PINBOARD_ARCHIVE_NAME);
			strBuilder.append(mStr);
			strBuilder.append(CL_PINBOARD_ARCHIVE_NAME);
			
			appendPinboards(mStateArchive.get(mStr));
			
			strBuilder.append(CL_PINBOARD_ARCHIVE);
		}
	}
	*/
	
	
	private void appendPinboards(PinboardArchive pa)
	{
		ArrayList<String> pinboardNames = pa.getPinboardNames();
		for(String mStr : pinboardNames)
		{
			appendPinboardItem(pa.get(mStr));
		}
	}
	
	// #2b 
	private void appendPinboardItem(PinboardItem pi)
	{
		strBuilder.append(PINBOARD);
		
		strBuilder.append(NAME);
		strBuilder.append(pi.getName());
		strBuilder.append(CL_NAME);
		
		strBuilder.append(SCROLLX);
		strBuilder.append(new Integer(pi.getScrollX()).toString());
		strBuilder.append(CL_SCROLLX);
		
		strBuilder.append(SCROLLY);
		strBuilder.append(new Integer(pi.getScrollY()).toString());
		strBuilder.append(CL_SCROLLY);
		
		ArrayList<HashMap<String,String>> noteList = pi.getNoteList();
		for(HashMap<String,String> rep : noteList)
		{
			strBuilder.append(NOTE);
			appendNoteProps(rep);
			strBuilder.append(CL_NOTE);
		}
		
		strBuilder.append(CL_PINBOARD);
	}
	
	private void appendNoteProps(HashMap<String,String> rep)
	{
		strBuilder.append(X);
		strBuilder.append(rep.get("x"));
		strBuilder.append(CL_X);
		
		strBuilder.append(Y);
		strBuilder.append(rep.get("y"));
		strBuilder.append(CL_Y);
		
		strBuilder.append(WIDTH);
		strBuilder.append(rep.get("width"));
		strBuilder.append(CL_WIDTH);
		
		strBuilder.append(HEIGHT);
		strBuilder.append(rep.get("height"));
		strBuilder.append(CL_HEIGHT);
		
		strBuilder.append(SIZE_STATE);
		strBuilder.append(rep.get("sizeState"));
		strBuilder.append(CL_SIZE_STATE);
		
		strBuilder.append(COLOR);
		strBuilder.append(rep.get("color"));
		strBuilder.append(CL_COLOR);
		
		strBuilder.append(HEAD_TEXT);
		strBuilder.append(rep.get("headText"));
		strBuilder.append(CL_HEAD_TEXT);
		
		strBuilder.append(CONTENT_TEXT);
		strBuilder.append(rep.get("contentText"));
		strBuilder.append(CL_CONTENT_TEXT);
	}
	
	private PinboardItem getCurrentlyActivePinboard()
	{
		return mStateArchive.getCurrentlyActivePinboardArchive().getCurrentlyActivePinboard();
	}
	
	private String getCurrentlyActivePinboardName()
	{
		return mStateArchive.getCurrentlyActivePinboardArchive().getCurrentlyActivePinboard().getName();
	}
	
	private String getCurrentlyActivePinboardArchiveName()
	{
		return mStateArchive.getCurrentlyActivePinboardArchive().getName();
	}
	
	public void clean()
	{
		strBuilder = new StringBuilder();
	}
}