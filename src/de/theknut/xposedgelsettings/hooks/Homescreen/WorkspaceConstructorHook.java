package de.theknut.xposedgelsettings.hooks.homescreen;

import de.robv.android.xposed.XC_MethodHook;
import de.theknut.xposedgelsettings.hooks.Common;

public class WorkspaceConstructorHook extends XC_MethodHook {
	
	// http://androidxref.com/4.4.2_r1/xref/packages/apps/Launcher3/src/com/android/launcher3/Workspace.java#295
	// public Workspace(Context context, AttributeSet attrs, int defStyle)
	
	protected void afterHookedMethod(MethodHookParam param) throws Throwable {
		// save the workspace instance
		Common.WORKSPACE_INSTANCE = param.thisObject;		
	};
}
