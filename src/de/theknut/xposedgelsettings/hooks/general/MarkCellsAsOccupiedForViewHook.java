package de.theknut.xposedgelsettings.hooks.general;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

public class MarkCellsAsOccupiedForViewHook extends XC_MethodHook {
	
	// http://androidxref.com/4.4.2_r1/xref/packages/apps/Launcher3/src/com/android/launcher3/CellLayout.java#3075
	// public void markCellsAsOccupiedForView(View view, boolean[][] occupied)
	
	@Override
	protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
		
		if (param.args[0].getClass().getName().contains("AppWidgetHostView")) {
			XposedBridge.log("XGELS: lol");
			param.setResult(null);
		}
	}	
}
