package de.theknut.xposedgelsettings.hooks.pagindicator;

import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.setObjectField;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import de.robv.android.xposed.XC_MethodHook;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Fields;

public class OnAttachedToWindowHook extends XC_MethodHook {
	
	// http://androidxref.com/4.4.2_r1/xref/packages/apps/Launcher3/src/com/android/launcher3/PagedView.java#339
	// protected void onAttachedToWindow()
	
	@Override
	protected void afterHookedMethod(MethodHookParam param) throws Throwable {
		
		ViewGroup pageIndicator = ((ViewGroup)getObjectField(param.thisObject, Fields.pvPageIndicator));
		
		if (pageIndicator != null) {
			LayoutParams lp = pageIndicator.getLayoutParams();
			lp.height = 0;
			lp.width = 0;
			pageIndicator.setLayoutParams(lp);
			
			setObjectField(param.thisObject, Fields.pvPageIndicator, pageIndicator);
		}
	}
}