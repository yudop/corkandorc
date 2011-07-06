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

import android.widget.*;

import java.util.*;
import java.io.*;

public class PinboardArchive implements Serializable, Cloneable
{
	public static final long serialVersionUID = 2L;
	
	public HashMap<String, PinboardItem> pinboardMap;
	private ArrayList<String> pinboardNames;
	private PinboardItem currentlyActivePinboard;
	private String name = "";
	
	private int defaultNameCreationCounter = 1;
	
	public PinboardArchive(String name)
	{
		super();
		
		this.name = name;
		pinboardMap = new HashMap<String, PinboardItem>(); 
		pinboardNames = new ArrayList<String>();
		
		currentlyActivePinboard = new PinboardItem("cork01.board");
		initBasicPinboard(currentlyActivePinboard);
		pinboardMap.put("cork01.board", currentlyActivePinboard);
		pinboardNames.add("cork01.board");
	}
	
	public PinboardArchive(boolean parsed)
	{
		super();
		
		pinboardMap = new HashMap<String, PinboardItem>(); 
		pinboardNames = new ArrayList<String>();
		name = "default";
	}
	
	@Override
	public PinboardArchive clone()
	{
		try
		{
			super.clone();
			PinboardArchive paClone = new PinboardArchive(name);
			
			PinboardItem currentlyActivePinboardClone = currentlyActivePinboard.clone();
			ArrayList<String> pinboardNamesClone = new ArrayList<String>();
			HashMap<String, PinboardItem> pinboardMapClone = new HashMap<String, PinboardItem>();
			
			for(int i=0; i<pinboardNames.size();i++)
			{
				String str = pinboardNames.get(i);
				pinboardNamesClone.add(str);
				PinboardItem pi = pinboardMap.get(str).clone();
				pinboardMapClone.put(str, pi);
			}			
			
			paClone.setCurrentlyActivePinboard(currentlyActivePinboardClone);
			paClone.setPinboardNames(pinboardNamesClone);
			paClone.pinboardMap = pinboardMapClone;
			
			return paClone;
		}
		catch(Exception e)
		{
			return null;
		}
	}
	
	public void setCurrentlyActivePinboard(PinboardItem currentlyActivePinboard)
	{
		this.currentlyActivePinboard = currentlyActivePinboard;
	}
	
	public void setCurrentlyActivePinboard(String name)
	{
		currentlyActivePinboard = pinboardMap.get(name);
	} 
	
	public void setPinboardNames(ArrayList<String> pinboardNames)
	{
		this.pinboardNames = pinboardNames;
	}
	
	public ArrayList<String> getPinboardNames()
	{
		return pinboardNames;
	}
	
	public PinboardItem getCurrentlyActivePinboard()
	{
		return currentlyActivePinboard;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public PinboardItem addNewPinboard(String name)
	{
		PinboardItem mPinboardItem = new PinboardItem(name);
		
		pinboardNames.add(0, name);
		
		pinboardMap.put(name, mPinboardItem);
		return mPinboardItem;
	}
	
	public PinboardItem addDefaultPinboard()
	{
		return addNewPinboard(parseForDefaultNames());
	}
	
	// returns a counter String for a default name that is not used yet
	private String parseForDefaultNames()
	{		
		for(int i=0; i<pinboardNames.size(); i++)
		{
			String tmpName = pinboardNames.get(i);
			
			try 
			{
				String sub1 = tmpName.substring(0, 4);
				String sub2 = tmpName.substring(tmpName.length()-6, tmpName.length());
				String sub3 = tmpName.substring(4, tmpName.length()-6);
			
				if((sub1.equals("cork")) && (sub2.equals(".board")))
				{					
					int c = Integer.parseInt(sub3);
					
					if(c >= defaultNameCreationCounter)
					{
						defaultNameCreationCounter = c + 1;
					}
				}
			}
			catch(Exception ex)
			{
				continue; // continue with next if something is wrong
			}
		}
		
		int returnable = defaultNameCreationCounter;
		defaultNameCreationCounter = 1;
		
		if(returnable > 9)
		{
			return ("cork" + new Integer(returnable).toString() + ".board");
		}
		else
		{
			return ("cork0" + new Integer(returnable).toString() + ".board");
		}
	}
	
	public void put(PinboardItem item)
	{
		if((!pinboardMap.containsValue(item)) && (!pinboardNames.contains(item.getName())))
		{
			pinboardNames.add(item.getName());
		}
		pinboardMap.put(item.getName(), item);
	}
	
	public void deletePinboard(PinboardItem delItem)
	{
		for(int i=0; i<pinboardNames.size(); i++)
		{
			if(pinboardNames.get(i).equals(delItem.getName()))
			{
				pinboardNames.remove(i);
				break;
			}
		}
		
		remove(delItem.getName());
	}
	
	public void remove(String key)
	{
		pinboardMap.remove(key); 
		
		if(!pinboardNames.isEmpty())
		{
			for(int i=0; i<pinboardNames.size(); i++)
			{
				if(key.equals(pinboardNames.get(i)))
				{
					pinboardNames.remove(i);
					return;
				}
			}
		}
	}
	
	public PinboardItem get(String key)
	{
		return (PinboardItem)pinboardMap.get(key);
	}
	
	private void initBasicPinboard(PinboardItem it)
	{
		HashMap<String,String> mapRepresentative = new HashMap<String,String>();
		
		mapRepresentative.put("x", "30");
		mapRepresentative.put("y", "30");
		mapRepresentative.put("width", "150" );
		mapRepresentative.put("height", "168");  
		mapRepresentative.put("sizeState", new Integer(Element.SIZE_SMALL).toString());
		mapRepresentative.put("color", new Integer(EditButtonListener.COLORACTIVITY_BLUE).toString());
		mapRepresentative.put("headText", "Tap Cork to Add New Note");  
		mapRepresentative.put("contentText", "");		
		it.add(mapRepresentative);
		
		HashMap<String,String> mapRepresentative2 = new HashMap<String,String>();
		
		mapRepresentative2.put("x", "180"); 
		mapRepresentative2.put("y", "30"); 
		mapRepresentative2.put("width", "150" );  
		mapRepresentative2.put("height", "168"); 
		mapRepresentative2.put("sizeState", new Integer(Element.SIZE_SMALL).toString());
		mapRepresentative2.put("color", new Integer(EditButtonListener.COLORACTIVITY_GREEN).toString());
		mapRepresentative2.put("headText", "Tap Note to View its Menu");
		mapRepresentative2.put("contentText", "I'm a sample note :-)");		 
		it.add(mapRepresentative2);
		
		HashMap<String,String> mapRepresentative3 = new HashMap<String,String>(); 
		
		mapRepresentative3.put("x", "90");
		mapRepresentative3.put("y", "215"); 
		mapRepresentative3.put("width", "200" );
		mapRepresentative3.put("height", "220");
		mapRepresentative3.put("sizeState", new Integer(Element.SIZE_MEDIUM).toString());
		mapRepresentative3.put("color", new Integer(EditButtonListener.COLORACTIVITY_GRAY).toString());  
		mapRepresentative3.put("headText", "Check\njerrysource.com mobile page for user guide and further information. Thanks for your support.");
		mapRepresentative3.put("contentText", "");		
		it.add(mapRepresentative3);
	}
}
