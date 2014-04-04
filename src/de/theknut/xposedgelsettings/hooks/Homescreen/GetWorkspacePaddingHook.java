package de.theknut.xposedgelsettings.hooks.homescreen;

import static de.robv.android.xposed.XposedHelpers.getIntField;
import static de.robv.android.xposed.XposedHelpers.setIntField;

import javax.crypto.spec.OAEPParameterSpec;

import android.graphics.Rect;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;

public class GetWorkspacePaddingHook extends XC_MethodHook {
	
	// http://androidxref.com/4.4.2_r1/xref/packages/apps/Launcher3/src/com/android/launcher3/DynamicGrid.java#301
	// Rect getWorkspacePadding(int orientation)
	
	@Override
	protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
		
		if (PreferencesHelper.appdockSettingsSwitch && PreferencesHelper.hideAppDock) {
			
			int tmp = getIntField(param.thisObject, "hotseatBarHeightPx");
			if (tmp != 0) {
				Common.HOTSEAT_BAR_HEIGHT = tmp;
				setIntField(param.thisObject, "hotseatBarHeightPx", 0);
			}
		}
	}
	
	@Override
	protected void afterHookedMethod(MethodHookParam param) throws Throwable {
		
		// 0 = landscape
		// 1 = portrait
		int orientation;
		
		if (param.args.length == 0) {
			orientation = 1;
		} else {
			orientation = (Integer) param.args[0];
		}
		
		if (PreferencesHelper.changeGridSizeHome && orientation == 1) {
			Rect padding = (Rect) param.getResult();
			int multiplier = PreferencesHelper.workspaceRect;
			
			if (padding.left == 0 || padding.right == 0) {
				// give them something if they are 0
				padding.left = padding.right = 16;
			}
			
			padding.set(padding.left * multiplier, padding.top, padding.right * multiplier, padding.bottom);			
			param.setResult(padding);
		}
	}
}
