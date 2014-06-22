package de.theknut.xposedgelsettings.hooks.appdrawer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.widget.TextView;

import de.robv.android.xposed.XC_MethodHook;

public final class DrawHook extends XC_MethodHook {
	
	// http://androidxref.com/4.4.2_r1/xref/packages/apps/Launcher3/src/com/android/launcher3/PagedViewIcon.java#108
	// public void draw(Canvas canvas)
	
	int color;
	
	@Override
	protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
		
		TextView iconText = (TextView) param.thisObject;
		color = iconText.getCurrentTextColor();
		iconText.setTextColor(Color.TRANSPARENT);
	}
	
	@Override
	protected void afterHookedMethod(MethodHookParam param) throws Throwable {
		
		TextView iconText = (TextView) param.thisObject;
		iconText.setTextColor(color);
		
		((Canvas)param.args[0]).restore();
	}
}