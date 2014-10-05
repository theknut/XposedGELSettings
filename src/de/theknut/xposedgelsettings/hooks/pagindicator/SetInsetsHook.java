package de.theknut.xposedgelsettings.hooks.pagindicator;

import android.graphics.Rect;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

import de.robv.android.xposed.XC_MethodHook;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Fields;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

public class SetInsetsHook extends XC_MethodHook {
	
	// http://androidxref.com/4.4.2_r1/xref/packages/apps/Launcher3/src/com/android/launcher3/AppsCustomizeTabHost.java#86
	// public void setInsets(Rect insets)
	
	public boolean isTrebuchet;
	
	public SetInsetsHook (boolean isTrebuchet) {
		this.isTrebuchet = isTrebuchet;
	}
	
	@Override
	protected void afterHookedMethod(MethodHookParam param) throws Throwable {
		Object mContent = getObjectField(param.thisObject, Fields.acthContent);
		Rect insets = (Rect) param.args[0];
		
		if (mContent != null && !PreferencesHelper.moveTabHostBottom) {
			FrameLayout.LayoutParams flp = (LayoutParams) callMethod(mContent, "getLayoutParams");
			flp.topMargin = insets.top;
			
			if (PreferencesHelper.hideIconLabelApps) {
				if (isTrebuchet) flp.bottomMargin = 0;
				else flp.bottomMargin = insets.top;
			}
			else {
				flp.bottomMargin = insets.bottom;
			}
			
			flp.leftMargin = insets.left;
			flp.rightMargin = insets.right;
			callMethod(mContent, "setLayoutParams", flp);
		}
	}
}