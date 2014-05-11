package de.theknut.xposedgelsettings.hooks.general;

import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetProviderInfo;
import de.theknut.xposedgelsettings.hooks.HooksBaseClass;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Methods;

public final class AddViewToCellLayoutHook extends HooksBaseClass {
	
	// public boolean addViewToCellLayout(View child, int index, int childId, LayoutParams params, boolean markCells)
	// http://androidxref.com/4.4.2_r1/xref/packages/apps/Launcher3/src/com/android/launcher3/CellLayout.java#604
	
	@Override
	protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
		
		if (param.args[0].getClass().getName().contains(Methods.launcherAppWidgetHostView)) {
			if (DEBUG) log(param, "Make widget resizeable");
			
			AppWidgetHostView widget = (AppWidgetHostView) param.args[0];
			AppWidgetProviderInfo widgetProviderInfo = widget.getAppWidgetInfo();
			widgetProviderInfo.resizeMode = AppWidgetProviderInfo.RESIZE_BOTH;
			
			int tmp = 5;
			widgetProviderInfo.minResizeWidth = tmp;
			widgetProviderInfo.minResizeHeight = tmp;
			widgetProviderInfo.minHeight = tmp;
			widgetProviderInfo.minWidth = tmp;
		}
	}
}