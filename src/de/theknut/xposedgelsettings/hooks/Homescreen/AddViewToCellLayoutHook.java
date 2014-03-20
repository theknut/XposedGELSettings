package de.theknut.xposedgelsettings.hooks.homescreen;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.getBooleanField;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

import net.margaritov.preference.colorpicker.ColorPickerPreference;
import android.graphics.Color;

import de.robv.android.xposed.XC_MethodHook;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;

public final class AddViewToCellLayoutHook extends XC_MethodHook {
	
	// public boolean addViewToCellLayout(View child, int index, int childId, LayoutParams params, boolean markCells)
	// http://androidxref.com/4.4.2_r1/xref/packages/apps/Launcher3/src/com/android/launcher3/CellLayout.java#604
	
	private static int newColor = Color.parseColor(ColorPickerPreference.convertToARGB(PreferencesHelper.homescreenIconLabelColor));
	
	@Override
	protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
		
		if (param.args[0].getClass().getName().contains("BubbleTextView")) {
			
			// apps in folders don't have a shadow so we can filter that for future customization
			if (getBooleanField(param.args[0], "mShadowsEnabled")) {
				callMethod(param.args[0], "setShadowsEnabled", PreferencesHelper.homescreenIconLabelShadow);
				callMethod(param.args[0], "setTextColor", newColor);
			}
			
			if (PreferencesHelper.hideIconLabelHome) {
				callMethod(param.args[0], "setShadowsEnabled", false);
				callMethod(param.args[0], "setTextColor", Color.argb(0, 0, 0, 0));
			}
		}
		else if (param.args[0].getClass().getName().contains("FolderIcon")) {
			Object folderName = getObjectField(param.args[0], "mFolderName");
			
			callMethod(folderName, "setShadowsEnabled", PreferencesHelper.homescreenIconLabelShadow);
			callMethod(folderName, "setTextColor", newColor);
			
			if (PreferencesHelper.hideIconLabelHome) {
				callMethod(folderName, "setShadowsEnabled", false);
				callMethod(folderName, "setTextColor", Color.argb(0, 0, 0, 0));
			}
		}
	}
}
