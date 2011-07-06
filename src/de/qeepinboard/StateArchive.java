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

public class StateArchive implements Serializable
{
	// This class will be used to store application data
	
	public static final long serialVersionUID = 3L;
	
	private HashMap<String, PinboardArchive> pinboardArchiveMap;
	private ArrayList<String> archiveNames;
	// pinboard archive which is currently active (used in last session by the consumer)
	private PinboardArchive currentlyActivePinboardArchive;
	
	private int defaultNameCreationCounter = 1;
	
	public StateArchive()
	{
		super();
		pinboardArchiveMap = new HashMap<String, PinboardArchive>();
		archiveNames = new ArrayList<String>();
		
		currentlyActivePinboardArchive = new PinboardArchive("default");
		pinboardArchiveMap.put("default", currentlyActivePinboardArchive.clone());
		archiveNames.add("default");
	}
	
	public StateArchive(boolean parsed)
	{
		super();
		pinboardArchiveMap = new HashMap<String, PinboardArchive>();
		archiveNames = new ArrayList<String>();
	}
	
	public void setCurrentlyActivePinboardArchive(PinboardArchive currentlyActivePinboardArchive)
	{
		this.currentlyActivePinboardArchive = currentlyActivePinboardArchive;
	}
	
	public void setCurrentlyActivePinboardArchive(String name)
	{
		currentlyActivePinboardArchive = pinboardArchiveMap.get(name);
	}
	
	public PinboardArchive getCurrentlyActivePinboardArchive()
	{
		return currentlyActivePinboardArchive;
	}
	
	public ArrayList<String> getArchiveNames()
	{		
		return archiveNames; 
	}
	
	public void setArchiveNames(ArrayList<String> archiveNames)
	{
		this.archiveNames = archiveNames;
	}
	
	public PinboardArchive addNewPinboardArchive(PinboardArchive pa, String name)
	{		
		PinboardArchive pb = pa.clone();
		pb.setName(name);		
		archiveNames.add(0, name);		
		pinboardArchiveMap.put(name, pb);
		
		return pb;
	}
	
	public PinboardArchive addDefaultPinboardArchive(PinboardArchive pa)
	{
		return addNewPinboardArchive(pa, parseForDefaultNames());
	}
	
	public void makeFirstItemActive()
	{
		if(!archiveNames.isEmpty())
		{
			currentlyActivePinboardArchive = pinboardArchiveMap.get(archiveNames.get(0));
		}
	}
	
	// returns a counter String for a default name that is not used yet
	private String parseForDefaultNames()
	{		
		for(int i=0; i<archiveNames.size(); i++)
		{
			String tmpName = archiveNames.get(i);
			
			try 
			{
				String sub1 = tmpName.substring(0, 5);
				String sub2 = tmpName.substring(tmpName.length()-5, tmpName.length());
				String sub3 = tmpName.substring(5, tmpName.length()-5);
			
				if((sub1.equals("state")) && (sub2.equals(".save")))
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
			return ("state" + new Integer(returnable).toString() + ".save");
		}
		else
		{
			return ("state0" + new Integer(returnable).toString() + ".save");
		}
	}
	
	public void put(PinboardArchive item)
	{
		if((!pinboardArchiveMap.containsValue(item)) && (!archiveNames.contains(item.getName())))
		{
			archiveNames.add(item.getName());
		}
		pinboardArchiveMap.put(item.getName(), item);
	}
	
	public void remove(PinboardArchive item)
	{
		String temp = item.getName();
		pinboardArchiveMap.remove(temp); 
		
		if(!archiveNames.isEmpty())
		{
			for(int i=0; i<archiveNames.size(); i++)
			{
				if(temp.equals(archiveNames.get(i)))
				{
					archiveNames.remove(i);
					return;
				}
			}
		}
	}
	
	public void remove(String key)
	{
		pinboardArchiveMap.remove(key);
		
		if(!archiveNames.isEmpty())
		{
			for(int i=0; i<archiveNames.size(); i++)
			{
				if(key.equals(archiveNames.get(i)))
				{
					archiveNames.remove(i);
					return;
				}
			}
		}
	}
	
	public void deleteArchive(PinboardArchive delItem)
	{
		for(int i=0; i<archiveNames.size(); i++)
		{
			if(archiveNames.get(i).equals(delItem.getName()))
			{
				archiveNames.remove(i);
				break;
			}
		}
		
		remove(delItem.getName());
	}
	
	public void softDeleteArchive(PinboardArchive delItem)
	{
		String temp = delItem.getName();
		pinboardArchiveMap.remove(temp);
	}
	
	public PinboardArchive get(String key)
	{
		return pinboardArchiveMap.get(key);
	}
}
