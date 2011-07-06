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

import android.app.*;
import android.widget.*;
import android.util.*;
import android.os.*;

public class PinboardManager 
{	
	public static StateArchive staticStateArchive;
	public static PinboardArchive staticPinboardArchive;
	public static boolean restoreFromOptionsActivity = false;
	
	private StateArchive mStateArchive;
	private PinboardArchive mPinboardArchive;
	private PinboardItem mPinboardItem;
	
	public String msg = "";
	
	public PinboardItem getPinboardItem()
	{
		return mPinboardItem;
	}
	
	public StateArchive getStateArchive()
	{
		return mStateArchive;
	}
	
	public void refresh(PinboardItem item)
	{		
		mPinboardItem = item;
		mPinboardArchive.put(item);
		mPinboardArchive = mPinboardArchive.clone();
		
		mStateArchive.setCurrentlyActivePinboardArchive(mPinboardArchive);  
		
		ArrayList<String> an = mStateArchive.getArchiveNames();
		
		for(int i=0; i < an.size(); i++)
		{
			PinboardArchive pa2 = mStateArchive.get(an.get(i));
			PinboardArchive pa2Clone = pa2.clone();
			mStateArchive.softDeleteArchive(pa2);
			mStateArchive.put(pa2Clone);
		}
		
		staticStateArchive = mStateArchive;
		staticPinboardArchive = mPinboardArchive;
	}
	
	public void setStatic()
	{		
		staticStateArchive = mStateArchive;
		staticPinboardArchive = mPinboardArchive;
	}
	
	public void refreshFromStatic()
	{
		mStateArchive = staticStateArchive;
		mPinboardArchive = staticPinboardArchive;
		mPinboardItem = mPinboardArchive.getCurrentlyActivePinboard();
		mStateArchive.setCurrentlyActivePinboardArchive(mPinboardArchive);
	}
	
	public void cloneStateItems()
	{
		PinboardArchive pa = mStateArchive.getCurrentlyActivePinboardArchive().clone();
		mStateArchive.setCurrentlyActivePinboardArchive(pa);
		
		ArrayList<String> an = mStateArchive.getArchiveNames();
		
		for(int i=0; i < an.size(); i++)
		{
			PinboardArchive pa2 = mStateArchive.get(an.get(i));
			PinboardArchive pa2Clone = pa2.clone();
			mStateArchive.softDeleteArchive(pa2);
			mStateArchive.put(pa2Clone);
		}
	}
	
	public void loadAppData(Activity parent)
	{
		InputStreamReader isr;
		FileReader fr;
		BufferedReader breader;
		
		StringBuilder strbuild = new StringBuilder();
		
		String localpath = QeePinboard.STORAGE_FILE_STR;
		boolean lpexists = true;
		File sddirectory = null;
		String sdpath = "";
		String loadpath = ""; 
		File localfile = new File(localpath); 
		File sdfile;
		
		File pinnwand_sav_private = parent.getFileStreamPath(QeePinboard.STORAGE_FILE_STR);
		File pinnwand_sav_sd = new File(Environment.getExternalStorageDirectory().getPath() + "/corkandorc/" + QeePinboard.STORAGE_FILE_STR);
		File corkdroid_ser_sd = new File(Environment.getExternalStorageDirectory().getPath() + "/corkandorc/Corkdroid.ser");
		
		// retain notes from old version, then delete old storage file
		if(corkdroid_ser_sd.isFile() && (!pinnwand_sav_private.isFile()) && (!pinnwand_sav_sd.isFile()))
		{
			FileInputStream fileIn;
			ObjectInputStream objIn;
			
			try
	    	{				
				fileIn = new FileInputStream(Environment.getExternalStorageDirectory().getPath() + "/corkandorc/Corkdroid.ser");				
				
	    		objIn = new ObjectInputStream(fileIn);
	    		Object obj = objIn.readObject();
	    		mStateArchive = (StateArchive)obj;
	    		objIn.close();
	    		corkdroid_ser_sd.delete();	    		
	    	}
	    	catch(Exception ex)
	    	{
	    		 
	    	}  
		}
		// no old storage file available, or new storage file already created
		else
		{		
			try
			{
				parent.openFileInput(QeePinboard.STORAGE_FILE_STR);
			}
			catch(Exception e)
			{
				lpexists = false;
			}
		
			String state = Environment.getExternalStorageState();
	    	if (Environment.MEDIA_MOUNTED.equals(state))
	    	{
	    		sdpath = Environment.getExternalStorageDirectory().getPath() + "/corkandorc/" + QeePinboard.STORAGE_FILE_STR ;
	    		sddirectory = new File(Environment.getExternalStorageDirectory().getPath() + "/corkandorc") ;
	    		sdfile = new File(sdpath);
	    	
	    		if(sdfile.isFile())
	    		{    		
	    			if(lpexists)
	    			{                   
	    				loadpath = localpath;
	    			}
	    			else
	    			{
	    				loadpath = sdpath;	    			
	    			}
	    		}
	    		else
	    		{
	    			try
	    			{
	    				sddirectory.mkdir();
	    				sdfile.createNewFile();
	    			}
	    			catch(Exception e)
	    			{
	    				
	    			}	    		
	    		
	    			loadpath = localpath;	    			    	
	    		}
	    	}
	    	else
	    	{
    			loadpath = localpath;      		
	    	}
		
			try
    		{
				if(loadpath.equals(localpath))
				{
    		    	isr = new InputStreamReader(parent.openFileInput(QeePinboard.STORAGE_FILE_STR)); 
    		    	breader = new BufferedReader(isr); 
				}
				else
				{
					fr = new FileReader("/sdcard/corkandorc/" + QeePinboard.STORAGE_FILE_STR);
					breader = new BufferedReader(fr); 
				}
			
				char[] buf = new char[8192];
				while((-1 != breader.read(buf)))		
				{
					strbuild.append(buf);
				}
    		}
    		catch(Exception ex)
    		{
    		  
    		}   
    	
    		StringToStateParser parser = new StringToStateParser(parent);
			parser.setString(strbuild.toString());		
		
			mStateArchive = parser.parse();		
		}
        
    	if(mStateArchive == null)
    	{
    		mStateArchive = new StateArchive(); 
    	}
    	
    	mPinboardArchive = mStateArchive.getCurrentlyActivePinboardArchive();
		mPinboardItem = mPinboardArchive.getCurrentlyActivePinboard();	
		refresh(mPinboardItem);
	}
	
	public void saveAppData(Activity parent)
	{		
		if(mStateArchive != null)
		{
			saveInMainThread(parent);
		}
	}	
	
	public void saveInMainThread(Activity parent)
	{
		refresh(mPinboardItem); // !!
		
		// check if active pinboard valid or not; replace it if invalid
		String chkstr = getStateArchive().getCurrentlyActivePinboardArchive().getCurrentlyActivePinboard().getName();
		ArrayList<String> chklist = getStateArchive().getCurrentlyActivePinboardArchive().getPinboardNames();
		
		if(!chklist.contains(chkstr))
		{
			if(chklist.isEmpty())
			{
				
			}
			else
			{
				getStateArchive().getCurrentlyActivePinboardArchive().setCurrentlyActivePinboard(chklist.get(0));
			}
		}
		
		StateToStringTranslator ms = new StateToStringTranslator(getStateArchive());		
		String mStr = ms.translateToString();
		
		String sdpath = "";
		
		String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state))
	    {
	    	sdpath = Environment.getExternalStorageDirectory().getPath() + "/corkandorc/" + QeePinboard.STORAGE_FILE_STR ;
	    	
	    	FileOutputStream fileOut = null;
			BufferedOutputStream bout = null;
			PrintWriter pwriter = null;			
			
			try
	    	{    					
				fileOut = new FileOutputStream(sdpath);
		    	bout = new BufferedOutputStream(fileOut);
		    	pwriter = new PrintWriter(bout);
		    	pwriter.print(mStr);
		    	pwriter.close();
	    	}
	    	catch(Exception ex)
	    	{ 	
	    		
	    	}    
	    	finally
	    	{		    			    		
	    		if(pwriter != null)
		    	{	    		
		    		pwriter.flush();	    		
		    	}		    		
	    	} 
	    }
    	
	    FileOutputStream fileOut = null;
		BufferedOutputStream bout = null;
		PrintWriter pwriter = null;		
    	
    	try
    	{    	
    		fileOut = parent.openFileOutput(QeePinboard.STORAGE_FILE_STR, Activity.MODE_WORLD_READABLE);			
	    	bout = new BufferedOutputStream(fileOut);
	    	pwriter = new PrintWriter(bout);
	    	pwriter.print(mStr);
	    	pwriter.close();
    	}
    	catch(Exception ex)
    	{ 	
    		
    	}    
    	finally
    	{		    		
    		if(pwriter != null)
	    	{	    		
	    		pwriter.flush();	    		
	    	}	    		
    	} 	 
    	 
	} 	
	
}
