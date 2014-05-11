package de.theknut.xposedgelsettings.hooks.general;

import android.content.Context;
import android.widget.Toast;

import de.theknut.xposedgelsettings.R;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.HooksBaseClass;

public class StartDragHook extends HooksBaseClass {
	
	// http://androidxref.com/4.4.2_r1/xref/packages/apps/Launcher3/src/com/android/launcher3/Workspace.java#2380
	// void startDrag(CellLayout.CellInfo cellInfo)
	
	@Override
	protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
		if (DEBUG) log(param, "Don't allow dragging");
		
		Context context = Common.LAUNCHER_CONTEXT.createPackageContext(Common.PACKAGE_NAME, Context.CONTEXT_IGNORE_SECURITY);
		Toast.makeText(Common.LAUNCHER_CONTEXT, context.getString(R.string.toast_desktop_locked), Toast.LENGTH_LONG).show();
		param.setResult(null);
	}
}