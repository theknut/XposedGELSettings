package de.theknut.xposedgelsettings.hooks.homescreen;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import net.margaritov.preference.colorpicker.ColorPickerPreference;
import android.graphics.Color;
import android.widget.FrameLayout;
import de.robv.android.xposed.XC_MethodHook;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;

public class HotseatConstructorHook extends XC_MethodHook {
	
	// http://androidxref.com/4.4.2_r1/xref/packages/apps/Launcher3/src/com/android/launcher3/Hotseat.java#35
	// public Hotseat(Context context, AttributeSet attrs, int defStyle)
	
	private int newColor = Color.parseColor(ColorPickerPreference.convertToARGB(PreferencesHelper.appDockBackgroundColor));
	
	@Override
	protected void afterHookedMethod(MethodHookParam param) throws Throwable {
	    
	    callMethod(param.thisObject, "setBackgroundColor", newColor);
        ((FrameLayout) param.thisObject).setPadding(0, 0, 0, 0);
	}
}
