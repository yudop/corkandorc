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

import java.io.*;
import java.util.*;

import android.app.*;
import android.widget.*;
import android.util.*;
import android.os.*;
import android.view.*;
import android.content.*;

public class BackupManager 
{
	public static final String DIRECTORY_CORCANDORC = "/corkandorc/";
	public static final String DIRECTORY_BACKUP = "cbackup/";
	public static final String FILE_INFO = "info.txt";
	public static final String FILE_AUTOSAVE = "autosave.sav";
	
	private String sdpath = "";
	private String corkpath = "";
	private String backuppath = "";
	private String infopath = "";
	
	private File corkdirectory;
	private File backupdirectory;
	private File infofile;
	private File savefile;
	
	public BackupManager()
	{
		init();   
	}
	
	private void init()
	{
		sdpath = Environment.getExternalStorageDirectory().getPath();
		corkpath = sdpath + DIRECTORY_CORCANDORC;
		backuppath = corkpath + DIRECTORY_BACKUP;
		infopath = backuppath + FILE_INFO;
		
		corkdirectory = new File(corkpath);
		backupdirectory = new File(backuppath);
		infofile = new File(infopath);
		
		if(!corkdirectory.isDirectory())
		{
			corkdirectory.mkdir(); 
		}
		
		if(!backupdirectory.isDirectory())
		{
			backupdirectory.mkdir();
		}
		
		if(!infofile.isFile())
		{
			try
			{
				infofile.createNewFile();
			}
			catch(IOException ex)
			{
				
			}
		}
	}
	
	public void autosave()
	{
		File mfile = new File(backuppath + FILE_AUTOSAVE);
		
		String timedata = "";
		timedata = loadline(FILE_INFO);
		long ctime;
		
		try
		{
			ctime = Long.parseLong(timedata);
		}
		catch(Exception ex)
		{
			ctime = -1L;
			timedata = "";
		}
		
		if(timedata.equals("") || (timedata == null) || (!mfile.isFile()))
		{
			if(!mfile.isFile())
			{
				try
				{
					mfile.createNewFile();
				}
				catch(IOException ex)
				{
					
				}
			}
			
			save(FILE_AUTOSAVE, new StateToStringTranslator(OptionsActivity.staticStateArchive).translateToString());
			
			long time = System.currentTimeMillis();
			String timestr = new Long(time).toString();
			save(FILE_INFO, timestr);
		}
		else
		{
			long ntime = System.currentTimeMillis();
			long dtime = ntime - ctime;
			
			// 5 min elapsed since last autosave -> new autosave
			if(dtime >= 300000L)
			{
				save(FILE_AUTOSAVE, new StateToStringTranslator(OptionsActivity.staticStateArchive).translateToString());
				
				long time = System.currentTimeMillis();
				String timestr = new Long(time).toString();
				save(FILE_INFO, timestr);
			}
		}
	}
	
	public void save(String filename, String savestr)
	{
		String savepath = "";
		String state = Environment.getExternalStorageState();
		
	    if (Environment.MEDIA_MOUNTED.equals(state))
	    {
	    	savepath = backuppath + filename;
	    	
	    	FileOutputStream fileOut = null;
			BufferedOutputStream bout = null;
			PrintWriter pwriter = null;			
			
			try
	    	{    					
				fileOut = new FileOutputStream(savepath);
		    	bout = new BufferedOutputStream(fileOut);
		    	pwriter = new PrintWriter(bout);
		    	pwriter.print(savestr);
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
	
	// save pinboard context into a file on sd card 
	public String save(String savestr)
	{
		String savepath = "";
		String filename = "";
		String state = Environment.getExternalStorageState();
		
	    if (Environment.MEDIA_MOUNTED.equals(state))
	    {
	    	filename = getUniqueFileName();
	    	savepath = backuppath + filename;
	    	
	    	FileOutputStream fileOut = null;
			BufferedOutputStream bout = null;
			PrintWriter pwriter = null;			
			
			try
	    	{    					
				fileOut = new FileOutputStream(savepath);
		    	bout = new BufferedOutputStream(fileOut);
		    	pwriter = new PrintWriter(bout);
		    	pwriter.print(savestr);
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
	    
	    return filename;
	}
	
	// load backed up file from sd card
	public String load(String filename)
	{
		String str = "";
		
		if(filename == null)
		{
			return str;
		}
		
		String loadpath = "";
		String state = Environment.getExternalStorageState();
		
		if (Environment.MEDIA_MOUNTED.equals(state))
		{
			loadpath = backuppath + filename;
			FileReader fr;
			BufferedReader breader;
			StringBuilder strbuild = new StringBuilder();
			strbuild.append("");
			
			try
			{
				fr = new FileReader(loadpath);
				breader = new BufferedReader(fr); 				
				
				char[] buf = new char[8192];
				while((-1 != breader.read(buf)))		
				{
					strbuild.append(buf);
				}
				
				fr.close();
			}
			catch(Exception ex)
			{
				
			}
			
			str = strbuild.toString();
		}
		
		return str;
	}
	
	// load backed up file from sd card
	public String loadline(String filename)
	{
		String str = "";
		
		if(filename == null)
		{
			return str;
		}
		
		String loadpath = "";
		String state = Environment.getExternalStorageState();
		
		if (Environment.MEDIA_MOUNTED.equals(state))
		{
			loadpath = backuppath + filename;
			FileReader fr;
			BufferedReader breader;
			
			try
			{
				fr = new FileReader(loadpath);	
				breader = new BufferedReader(fr);
				
				str = str + breader.readLine();
				
				fr.close();
			}
			catch(Exception ex)
			{
				
			}
		}
		
		return str;
	}
	
	public void renameFile(String oldname, String newname)
	{
		File mfile = new File(backuppath + oldname + ".sav");
		File newfile = new File(backuppath + newname + ".sav");
		
		mfile.renameTo(newfile);
	}
	
	public void deleteFile(String filename)
	{
		File mfile = new File(backuppath + filename + ".sav");
		mfile.delete();
	}
	
	public String[] getBackupFilesList()
	{
		String[] rawlist = backupdirectory.list();
		return alphabeticList(rawlist);
	}
	
	private String[] alphabeticList(String[] xlist)
	{
		if((xlist == null) || (xlist.length == 0))
		{
			return xlist;
		}
		
		String[] mlist = new String[xlist.length];
		String tmp1;
		String tmp2;
		
		for(int i=0; i < xlist.length; i++)
		{
			if(i == 0)
			{
				mlist[i] = xlist[i];
			}
			else
			{
				for(int j=i; j>0; j--)
				{
					int comparison = savTrim(xlist[i]).compareTo(savTrim(mlist[j-1]));					
					
					if(comparison < 0)
					{
						mlist[j] = mlist[j-1]; 
						
						if(j == 1)
						{
							mlist[j] = xlist[i];
						}
					}
					else
					{
						mlist[j] = xlist[i];
						break;
					}
				}
			}
		}
		
		return mlist;
	}
	
	// cuts suffix ".sav" off the string if one is contained
	private String savTrim(String str)
	{
		if(str.endsWith(".sav"))
		{
			try
			{
				str = str.substring(0, str.length()-4);
			}
			catch(Exception ex)
			{
				
			}
		}
		
		return str;
	}
	
	private String getUniqueFileName()
	{    	
    	int tmpCount = 1;
    	boolean repeatFlag;
    	String[] flist = getBackupFilesList();
    	String nameString = "mydata";       
    	
    	String tmpNameString = nameString;  
    	
    	if(flist == null)
    	{
    		return nameString;
    	}
    	
    	// avoid duplicate item names by adding "(n)"
    	do
    	{
    		repeatFlag = false;
    		
    		for(int i=0; i< flist.length; i++)
    		{
    			String n = flist[i]; 
    		
    			if(n.equals(tmpNameString  + ".sav"))
    			{
    				tmpNameString = nameString + "(" + new Integer(tmpCount).toString() + ")";
    				tmpCount = tmpCount + 1;	
    				repeatFlag = true;
    				break;
    			}
    		}
    	} while(repeatFlag == true);
    	
    	return (tmpNameString + ".sav");
	}
}
