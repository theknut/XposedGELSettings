package de.theknut.xposedgelsettings.hooks.homescreen;

import android.graphics.Color;
import android.widget.FrameLayout;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

import de.robv.android.xposed.XC_MethodHook;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;

import static de.robv.android.xposed.XposedHelpers.callMethod;

public class HotseatConstructorHook extends XC_MethodHook {
	
	// http://androidxref.com/4.4.2_r1/xref/packages/apps/Launcher3/src/com/android/launcher3/Hotseat.java#35
	// public Hotseat(Context context, AttributeSet attrs, int defStyle)
	
	private int newColor = Color.parseColor(ColorPickerPreference.convertToARGB(PreferencesHelper.appDockBackgroundColor));
	
	@Override
	protected void afterHookedMethod(MethodHookParam param) throws Throwable {

        Common.HOTSEAT_INSTANCE = param.thisObject;
	    callMethod(param.thisObject, "setBackgroundColor", newColor);
        ((FrameLayout) param.thisObject).setPadding(0, 0, 0, 0);
	}
}
