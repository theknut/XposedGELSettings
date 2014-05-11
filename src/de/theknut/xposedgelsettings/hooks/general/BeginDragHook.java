package de.theknut.xposedgelsettings.hooks.general;

import android.widget.Toast;

import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.HooksBaseClass;

public class BeginDragHook extends HooksBaseClass {
	
	// http://androidxref.com/4.4.2_r1/xref/packages/apps/Launcher3/src/com/android/launcher3/AppsCustomizePagedView.java#774
	// beginDragging(final View v)
	
	@Override
	protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
		if (DEBUG) log(param, "Don't allow dragging");
		
		Toast.makeText(Common.LAUNCHER_CONTEXT, "XGELS: Desktop is locked!", Toast.LENGTH_LONG).show();
		param.setResult(null);
	}
}