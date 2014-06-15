package de.theknut.xposedgelsettings.hooks.appdrawer;

import android.graphics.Color;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

import de.robv.android.xposed.XC_MethodHook;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;

import static de.robv.android.xposed.XposedHelpers.callMethod;

public class AppsCustomizeLayoutConstructorHook extends XC_MethodHook{
	
	// https://github.com/CyanogenMod/android_packages_apps_Trebuchet/blob/cm-11.0/src/com/android/launcher3/AppsCustomizeLayout.java#L40
	// public AppsCustomizeLayout(Context context, AttributeSet attrs)
	
	private static int newColor = Color.parseColor(ColorPickerPreference.convertToARGB(PreferencesHelper.appdrawerBackgroundColor));
	
	@Override
	protected void afterHookedMethod(MethodHookParam param) throws Throwable {
		callMethod(param.thisObject, "setBackgroundColor", newColor);
	}
}
