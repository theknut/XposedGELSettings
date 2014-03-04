package de.theknut.xposedgelsettings.hooks.General;

import android.widget.Toast;
import de.robv.android.xposed.XC_MethodHook;
import de.theknut.xposedgelsettings.hooks.Common;

public class BeginnDragHook extends XC_MethodHook {
	
	// http://androidxref.com/4.4.2_r1/xref/packages/apps/Launcher3/src/com/android/launcher3/AppsCustomizePagedView.java#774
	// beginDragging(final View v)
	
	@Override
	protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
		Toast.makeText(Common.LAUNCHER_CONTEXT, "XGELS: Desktop is locked!", Toast.LENGTH_LONG).show();
		param.setResult(null);
	}
}