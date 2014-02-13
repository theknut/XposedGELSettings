package de.theknut.xposedgelsettings.hooks.Icons;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

import android.graphics.Color;
import de.robv.android.xposed.XC_MethodHook;

public final class CellLayoutHook extends XC_MethodHook {
	
	@Override
	protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
		
		if (param.args[0].getClass().getName().contains("BubbleTextView")) {
			callMethod(param.args[0], "setShadowsEnabled", false);
			callMethod(param.args[0], "setTextColor", Color.argb(0,0,0,0));
		}
		else if (param.args[0].getClass().getName().contains("FolderIcon")) {
			Object folderName = getObjectField(param.args[0], "mFolderName");
			callMethod(folderName, "setShadowsEnabled", false);
			callMethod(folderName, "setTextColor", Color.argb(0,0,0,0));
		}
	}
}
