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

import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Fields;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Methods;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.HooksBaseClass;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;

public final class AddViewToCellLayoutHook extends HooksBaseClass {
	
	// public boolean addViewToCellLayout(View child, int index, int childId, LayoutParams params, boolean markCells)
	// http://androidxref.com/4.4.2_r1/xref/packages/apps/Launcher3/src/com/android/launcher3/CellLayout.java#604
	
	private static int newAppLabelColor = Color.parseColor(ColorPickerPreference.convertToARGB(PreferencesHelper.homescreenIconLabelColor));
	private static int newFolderAppLabelColor = Color.parseColor(ColorPickerPreference.convertToARGB(PreferencesHelper.homescreenFolderAppTextColor));
	private static int newFolderNameColor = Color.parseColor(ColorPickerPreference.convertToARGB(PreferencesHelper.homescreenFolderNameTextColor));
	private static int newFolderBackgroundColor = Color.parseColor(ColorPickerPreference.convertToARGB(PreferencesHelper.homescreenFolderColor));
	private static int newFolderPreviewBackgroundColor = Color.parseColor(ColorPickerPreference.convertToARGB(PreferencesHelper.homescreenFolderPreviewColor));
	
	@Override
	protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
		
		if (param.args[0].getClass().getName().contains(Fields.bubbleTextView)) {
			
			// apps in folders don't have a shadow so we can filter that for future customization
			if (!getBooleanField(param.args[0], Fields.btvShadowsEnabled)) {
			    if (PreferencesHelper.homescreenFolderSwitch) {
			        if (PreferencesHelper.homescreenFolderNoLabel) {
			            callMethod(param.args[0], "setTextColor", Color.TRANSPARENT);
			        } else {
			            callMethod(param.args[0], "setTextColor", newFolderAppLabelColor);
			        }
				}
			} else {
				if (PreferencesHelper.iconSettingsSwitchHome) {
					if (Common.PACKAGE_OBFUSCATED) {
						if (!PreferencesHelper.homescreenIconLabelShadow) {
							callMethod(param.args[0], Methods.btvSetShadowsEnabled, PreferencesHelper.homescreenIconLabelShadow);
						}
					} else {
						callMethod(param.args[0], Methods.btvSetShadowsEnabled, PreferencesHelper.homescreenIconLabelShadow);
					}
					
					if (PreferencesHelper.hideIconLabelHome) {
		                callMethod(param.args[0], Methods.btvSetShadowsEnabled, false);
		                callMethod(param.args[0], "setTextColor", Color.TRANSPARENT);
		            } else {
		                callMethod(param.args[0], "setTextColor", newAppLabelColor);
		            }
				}
			}
			
		} else if (param.args[0].getClass().getName().contains(Fields.fiFolderIcon)) {
			Object folderName = getObjectField(param.args[0], Fields.fiFolderName);
			
			if (PreferencesHelper.iconSettingsSwitchHome) {
				if (Common.PACKAGE_OBFUSCATED) {
					if (!PreferencesHelper.homescreenIconLabelShadow) {
						callMethod(folderName, Methods.btvSetShadowsEnabled, PreferencesHelper.homescreenIconLabelShadow);
					}
				} else {
					callMethod(folderName, Methods.btvSetShadowsEnabled, PreferencesHelper.homescreenIconLabelShadow);
				}
				
				callMethod(folderName, "setTextColor", newAppLabelColor);
				
				if (PreferencesHelper.hideIconLabelHome) {
					callMethod(folderName, Methods.btvSetShadowsEnabled, false);
					callMethod(folderName, "setTextColor", Color.TRANSPARENT);
				}
			}
			
			if (PreferencesHelper.homescreenFolderSwitch) {
				Object mFolder = getObjectField(param.args[0], Fields.fiFolder);
				
				LinearLayout ll = (LinearLayout) mFolder;
				Drawable d = ll.getBackground();
				d.setColorFilter(newFolderBackgroundColor, Mode.MULTIPLY);
				ll.setBackground(d);

				EditText mFolderName = (EditText) getObjectField(mFolder, Fields.fiFolderEditText);
				mFolderName.setTextColor(newFolderNameColor);
				
				ImageView prevBackground = (ImageView) getObjectField(param.args[0], Fields.fiPreviewBackground);
				prevBackground.setVisibility(View.VISIBLE);
				Drawable i = prevBackground.getDrawable();
				i.setColorFilter(newFolderPreviewBackgroundColor, Mode.MULTIPLY);
				prevBackground.setImageDrawable(i);	
			}
		}
	}
}
