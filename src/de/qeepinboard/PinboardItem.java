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

public class PinboardItem implements Serializable, Cloneable
{
	public static final long serialVersionUID = 1L;
	
	private ArrayList<HashMap<String,String>> noteList;
	private String name = "";
	private int scrollX = 0;
	private int scrollY = 0;
	
	public PinboardItem(String name)
	{
		super();
		noteList = new ArrayList<HashMap<String,String>>(); 
		this.name = name; 
	}
	
	@Override
	public PinboardItem clone()
	{
		try
		{
			PinboardItem piClone = new PinboardItem(name);
			ArrayList<HashMap<String,String>> noteListClone = new ArrayList<HashMap<String,String>>();
			piClone.setName(name.toString());
			piClone.setScrollX(scrollX);
			piClone.setScrollY(scrollY);
			
			for(int i=0; i<noteList.size(); i++)
			{
				HashMap<String,String> it = noteList.get(i);				
				HashMap<String,String> cloneIt = new HashMap<String,String>();
				
				cloneIt.put("x", it.get("x").toString());
				cloneIt.put("y", it.get("y").toString());
				cloneIt.put("width", it.get("width").toString());
				cloneIt.put("height", it.get("height").toString());
				cloneIt.put("sizeState", it.get("sizeState").toString());
				cloneIt.put("color", it.get("color").toString());
				cloneIt.put("headText", it.get("headText").toString());
				cloneIt.put("contentText", it.get("contentText").toString());
				
				noteListClone.add(cloneIt);
			}
			
			piClone.setNoteList(noteListClone);
			
			return piClone;
		}
		catch(Exception e)
		{
			return null;
		}
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public void setNoteList(ArrayList<HashMap<String,String>> noteList)
	{
		this.noteList = noteList;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setScrollX(int scrollX)
	{
		this.scrollX = scrollX;
	}
	
	public void setScrollY(int scrollY)
	{
		this.scrollY = scrollY;
	}
	
	public int getScrollX()
	{
		return scrollX;
	}
	
	public int getScrollY()
	{
		return scrollY;
	}
	
	public ArrayList<HashMap<String,String>> getNoteList()
	{
		return noteList;
	}
	
	public void add(HashMap<String,String> element)
	{
		noteList.add(element);
	}
	
	public boolean isEmpty()
	{
		return noteList.isEmpty();
	}
	
	public int size()
	{
		return noteList.size();
	}
	
	public HashMap<String,String> get(int location)
	{
		return noteList.get(location);
	}
	
	public HashMap<String,String> remove(int location)
	{
		return noteList.remove(location);
	}
}
