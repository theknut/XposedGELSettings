package de.theknut.xposedgelsettings.hooks.general;

import java.util.ArrayList;
import java.util.Iterator;

import android.appwidget.AppWidgetProviderInfo;
import de.robv.android.xposed.XC_MethodHook;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;

public class OnPackagesUpdatedHook extends XC_MethodHook {
	
	// http://androidxref.com/4.4.2_r1/xref/packages/apps/Launcher3/src/com/android/launcher3/AppsCustomizePagedView.java#432
	// public void onPackagesUpdated(ArrayList<Object> widgetsAndShortcuts)
	
	@Override
	protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
		
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
