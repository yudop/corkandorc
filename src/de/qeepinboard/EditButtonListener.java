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

public class EditButtonListener implements View.OnClickListener
{
	public static final int INSTANTEDITVIEW_ADD = 0;
	public static final int INSTANTEDITVIEW_CANCEL = 1;
	public static final int INSTANTEDITVIEW_COLOR_DISPLAY = 2;
	public static final int INSTANTEDITVIEW_SIZE_DISPLAY = 3;
	
	public static final int ADVANCEDEDITING_DONE = 4;
	public static final int ADVANCEDEDITING_CLEAR = 5;
	public static final int ADVANCEDEDITING_CANCEL = 6;
	
	public static final int ADVANCEDEDITINGLANDSCAPE_DONE = 7;
	public static final int ADVANCEDEDITINGLANDSCAPE_CLEAR = 8;
	
	public static final int COLORACTIVITY_WHITE = 9;
	public static final int COLORACTIVITY_GRAY = 10;
	public static final int COLORACTIVITY_RED = 11;
	public static final int COLORACTIVITY_YELLOW = 12;
	public static final int COLORACTIVITY_ORANGE = 13;
	public static final int COLORACTIVITY_VIOLETTE = 14;
	public static final int COLORACTIVITY_GREEN = 15;
	public static final int COLORACTIVITY_BLUE = 16;
	
	public static final int NOTEDETAILSVIEW_EDIT = 17;
	public static final int NOTEDETAILSVIEW_BACK = 18;
	public static final int NOTEDETAILSVIEW_COLOR_DISPLAY = 19;
	
	private QeePinboard parent;
	private AdvancedEditing parent2;
	private AdvancedEditingLandscape parent3;
	private ColorActivity parent4;
	private NoteDetailsView parent5;
	
	public EditButtonListener(QeePinboard parent)
	{
		this.parent = parent;
	}
	
	public EditButtonListener(AdvancedEditing parent2)
	{
		this.parent2 = parent2;
	}
	
	public EditButtonListener(AdvancedEditingLandscape parent3)
	{
		this.parent3 = parent3;
	}
	 
	public EditButtonListener(ColorActivity parent4)
	{
		this.parent4 = parent4;
	}
	
	public EditButtonListener(NoteDetailsView parent5)
	{
		this.parent5 = parent5;
	}
	
	public void onClick(View v)
	{
		if(v.getId() == INSTANTEDITVIEW_ADD)
		{
			parent.iev_onAdd();
		}
		else if(v.getId() == INSTANTEDITVIEW_CANCEL)
		{
			parent.iev_onCancel();
		}
		else if(v.getId() == INSTANTEDITVIEW_COLOR_DISPLAY)
		{
			parent.iev_onColorDisplay();
		}
		else if(v.getId() == INSTANTEDITVIEW_SIZE_DISPLAY)
		{
			parent.iev_onSizeDisplay();
		}
		else if(v.getId() == ADVANCEDEDITING_DONE)
		{
			parent2.aev_onDone();
		}
		else if(v.getId() == ADVANCEDEDITING_CLEAR)
		{
			parent2.aev_onClear();
		}
		else if(v.getId() == ADVANCEDEDITING_CANCEL)
		{
			parent2.aev_onCancel();
		}
		else if(v.getId() == ADVANCEDEDITINGLANDSCAPE_DONE)
		{
			parent3.aelv_onDone();
		}
		else if(v.getId() == ADVANCEDEDITINGLANDSCAPE_CLEAR)
		{
			parent3.aelv_onClear();
		}
		else if(v.getId() == COLORACTIVITY_WHITE)
		{
			parent4.ca_onColor(COLORACTIVITY_WHITE);
		}
		else if(v.getId() == COLORACTIVITY_GRAY)
		{
			parent4.ca_onColor(COLORACTIVITY_GRAY);
		}
		else if(v.getId() == COLORACTIVITY_YELLOW)
		{
			parent4.ca_onColor(COLORACTIVITY_YELLOW);
		}
		else if(v.getId() == COLORACTIVITY_GREEN)
		{
			parent4.ca_onColor(COLORACTIVITY_GREEN);
		}
		else if(v.getId() == COLORACTIVITY_ORANGE)
		{
			parent4.ca_onColor(COLORACTIVITY_ORANGE);
		}
		else if(v.getId() == COLORACTIVITY_VIOLETTE)
		{
			parent4.ca_onColor(COLORACTIVITY_VIOLETTE);
		}
		else if(v.getId() == COLORACTIVITY_BLUE)
		{
			parent4.ca_onColor(COLORACTIVITY_BLUE);
		}
		else if(v.getId() == COLORACTIVITY_RED)
		{
			parent4.ca_onColor(COLORACTIVITY_RED);
		}
		else if(v.getId() == NOTEDETAILSVIEW_EDIT)
		{
			parent.ndv_onEdit();
		}
		else if(v.getId() == NOTEDETAILSVIEW_BACK)
		{
			parent.ndv_onBack();
		}
		else if(v.getId() == NOTEDETAILSVIEW_COLOR_DISPLAY)
		{
			parent.ndv_onColorDisplay();
		}
	}
}
