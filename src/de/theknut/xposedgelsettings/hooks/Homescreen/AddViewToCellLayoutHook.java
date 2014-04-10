package de.theknut.xposedgelsettings.hooks.homescreen;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.getBooleanField;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import de.robv.android.xposed.XC_MethodHook;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;

public final class AddViewToCellLayoutHook extends XC_MethodHook {
	
	// public boolean addViewToCellLayout(View child, int index, int childId, LayoutParams params, boolean markCells)
	// http://androidxref.com/4.4.2_r1/xref/packages/apps/Launcher3/src/com/android/launcher3/CellLayout.java#604
	
	private static int newAppLabelColor = Color.parseColor(ColorPickerPreference.convertToARGB(PreferencesHelper.homescreenIconLabelColor));
	private static int newFolderAppLabelColor = Color.parseColor(ColorPickerPreference.convertToARGB(PreferencesHelper.homescreenFolderAppTextColor));
	private static int newFolderNameColor = Color.parseColor(ColorPickerPreference.convertToARGB(PreferencesHelper.homescreenFolderNameTextColor));
	private static int newFolderBackgroundColor = Color.parseColor(ColorPickerPreference.convertToARGB(PreferencesHelper.homescreenFolderColor));
	private static int newFolderPreviewBackgroundColor = Color.parseColor(ColorPickerPreference.convertToARGB(PreferencesHelper.homescreenFolderPreviewColor));
	
	@Override
	protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
		if (param.args[0].getClass().getName().contains("BubbleTextView")) {
			
			// apps in folders don't have a shadow so we can filter that for future customization
			if (!getBooleanField(param.args[0], "mShadowsEnabled")) {
				if (PreferencesHelper.homescreenFolderSwitch) {
					callMethod(param.args[0], "setTextColor", newFolderAppLabelColor);
				}
			} else {
				if (PreferencesHelper.iconSettingsSwitchHome) {
					callMethod(param.args[0], "setShadowsEnabled", PreferencesHelper.homescreenIconLabelShadow);
					callMethod(param.args[0], "setTextColor", newAppLabelColor);
				}
			}
			
			if (PreferencesHelper.iconSettingsSwitchHome && PreferencesHelper.hideIconLabelHome) {
				callMethod(param.args[0], "setShadowsEnabled", false);
				callMethod(param.args[0], "setTextColor", Color.argb(0, 0, 0, 0));
			}
		}
		else if (param.args[0].getClass().getName().contains("FolderIcon")) {
			Object folderName = getObjectField(param.args[0], "mFolderName");
			
			if (PreferencesHelper.iconSettingsSwitchHome) {
				callMethod(folderName, "setShadowsEnabled", PreferencesHelper.homescreenIconLabelShadow);
				callMethod(folderName, "setTextColor", newAppLabelColor);
				
				if (PreferencesHelper.hideIconLabelHome) {
					callMethod(folderName, "setShadowsEnabled", false);
					callMethod(folderName, "setTextColor", Color.TRANSPARENT);
				}
			}
			
			if (PreferencesHelper.homescreenFolderSwitch) {
				Object mFolder = getObjectField(param.args[0], "mFolder");
				
				LinearLayout ll = (LinearLayout) mFolder;
				Drawable d = ll.getBackground();
				d.setColorFilter(newFolderBackgroundColor, Mode.MULTIPLY);
				ll.setBackground(d);
				
				EditText mFolderName = (EditText) getObjectField(mFolder, "mFolderName");
				mFolderName.setTextColor(newFolderNameColor);
				
				ImageView prevBackground = (ImageView) getObjectField(param.args[0], "mPreviewBackground");
				prevBackground.setVisibility(View.VISIBLE);
				Drawable i = prevBackground.getDrawable();
				i.setColorFilter(newFolderPreviewBackgroundColor, Mode.MULTIPLY);
				prevBackground.setImageDrawable(i);	
			}
		}
	}
}
