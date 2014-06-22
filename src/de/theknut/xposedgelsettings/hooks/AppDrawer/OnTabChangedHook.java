package de.theknut.xposedgelsettings.hooks.appdrawer;

import android.graphics.Color;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

import de.theknut.xposedgelsettings.hooks.HooksBaseClass;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;

import static de.robv.android.xposed.XposedHelpers.callMethod;

public class OnTabChangedHook extends HooksBaseClass {
	
	// http://androidxref.com/4.4.2_r1/xref/packages/apps/Launcher3/src/com/android/launcher3/AppsCustomizeTabHost.java#207
	// public void onTabChanged(String tabId)
	
	private static int newColor = Color.parseColor(ColorPickerPreference.convertToARGB(PreferencesHelper.appdrawerBackgroundColor));
	
	protected void afterHookedMethod(MethodHookParam param) throws Throwable {
		// set the app drawer background
		callMethod(param.thisObject, "setBackgroundColor", newColor);
	}
}