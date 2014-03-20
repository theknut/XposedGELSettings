package de.theknut.xposedgelsettings.hooks.appdrawer;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import net.margaritov.preference.colorpicker.ColorPickerPreference;
import android.graphics.Color;
import de.robv.android.xposed.XC_MethodHook;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;

public final class ApplyFromApplicationInfoHook extends XC_MethodHook {
	
	// http://androidxref.com/4.4.2_r1/xref/packages/apps/Launcher3/src/com/android/launcher3/PagedViewIcon.java#68
	// public void applyFromApplicationInfo(AppInfo info, boolean scaleUp, PagedViewIcon.PressedCallback cb)
	
	private static int newColor = Color.parseColor(ColorPickerPreference.convertToARGB(PreferencesHelper.appdrawerIconLabelColor));
	
	@Override
	protected void afterHookedMethod(MethodHookParam param) throws Throwable {
		
		if (PreferencesHelper.hideIconLabelApps) {
			callMethod(param.thisObject, "setTextColor", Color.argb(0, 0, 0, 0));
		}
		else {
			callMethod(param.thisObject, "setTextColor", newColor);
		}
	}
}