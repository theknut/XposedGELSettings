package de.theknut.xposedgelsettings.hooks.general;

import android.appwidget.AppWidgetProviderInfo;

import java.util.ArrayList;
import java.util.Iterator;

import de.theknut.xposedgelsettings.hooks.HooksBaseClass;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;

public class OnPackagesUpdatedHook extends HooksBaseClass {
	
	// http://androidxref.com/4.4.2_r1/xref/packages/apps/Launcher3/src/com/android/launcher3/AppsCustomizePagedView.java#432
	// public void onPackagesUpdated(ArrayList<Object> widgetsAndShortcuts)
	
	@SuppressWarnings("unchecked")
	@Override
	protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
		if (DEBUG) log(param, "Hide Widgets");
		
		ArrayList<Object> widgets = (ArrayList<Object>) param.args[0];
		for (Iterator<Object> it = widgets.iterator(); it.hasNext(); ) {
			Object widget = it.next();
		    
			if (widget instanceof AppWidgetProviderInfo) {
				String tag = ((AppWidgetProviderInfo) widget).provider.getPackageName() + "#" + ((AppWidgetProviderInfo) widget).provider.getShortClassName();
				
				if (PreferencesHelper.hiddenWidgets.contains(tag)) {
					it.remove();
				}
			}
		}
		
		param.args[0] = widgets;
	};
}
