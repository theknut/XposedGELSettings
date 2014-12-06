package de.theknut.xposedgelsettings.hooks.homescreen;

import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Methods;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Classes;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Fields;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;
import de.theknut.xposedgelsettings.hooks.appdrawer.ApplyFromApplicationInfoHook;
import de.theknut.xposedgelsettings.hooks.common.XGELSCallback;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.getIntField;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.setBooleanField;

public final class AddViewToCellLayoutHook extends XGELSCallback {

    // public boolean addViewToCellLayout(View child, int index, int childId, LayoutParams params, boolean markCells)
    // http://androidxref.com/4.4.2_r1/xref/packages/apps/Launcher3/src/com/android/launcher3/CellLayout.java#604

    private static int newAppLabelColor = Color.parseColor(ColorPickerPreference.convertToARGB(PreferencesHelper.homescreenIconLabelColor));
    private static int newFolderAppLabelColor = Color.parseColor(ColorPickerPreference.convertToARGB(PreferencesHelper.homescreenFolderAppTextColor));
    private static int newFolderNameColor = Color.parseColor(ColorPickerPreference.convertToARGB(PreferencesHelper.homescreenFolderNameTextColor));
    private static int newFolderBackgroundColor = Color.parseColor(ColorPickerPreference.convertToARGB(PreferencesHelper.homescreenFolderColor));
    private static int newFolderPreviewBackgroundColor = Color.parseColor(ColorPickerPreference.convertToARGB(PreferencesHelper.homescreenFolderPreviewColor));
    private static int newAppLabelColorAppDrawer = Color.parseColor(ColorPickerPreference.convertToARGB(PreferencesHelper.appdrawerIconLabelColor));;
    private static int iconPadding;

    @Override
    public void onBeforeHookedMethod(MethodHookParam param) throws Throwable {
        View child = (View) param.args[0];

        if (child.getClass().equals(Classes.BubbleTextView)) {
            if (param.thisObject.getClass().equals(Classes.AppsCustomizeCellLayout)) {
                callMethod(child, "setTextColor", PreferencesHelper.hideIconLabelApps ? Color.TRANSPARENT : ApplyFromApplicationInfoHook.newColor);
                maybeHideShadow(child, !PreferencesHelper.appdrawerIconLabelShadow || PreferencesHelper.hideIconLabelApps);
            } else if (((View) param.thisObject).getParent().getClass().equals(ScrollView.class)) {
                // apps inside folders are added to a ScrollView
                if (PreferencesHelper.homescreenFolderSwitch) {
                    callMethod(child, "setTextColor", PreferencesHelper.homescreenFolderNoLabel ? Color.TRANSPARENT : newFolderAppLabelColor);
                }
            } else if (((View) param.thisObject).getParent().getClass().equals(Classes.Workspace)) {
                if (PreferencesHelper.iconSettingsSwitchHome) {
                    callMethod(child, "setTextColor", PreferencesHelper.hideIconLabelHome ? Color.TRANSPARENT : newAppLabelColor);
                    maybeHideShadow(child, !PreferencesHelper.homescreenIconLabelShadow || PreferencesHelper.hideIconLabelHome);
                }
                iconPadding = ((TextView) child).getCompoundDrawablePadding();
            } else if (((View) param.thisObject).getParent().getClass().equals(Classes.Hotseat)) {
                if (PreferencesHelper.iconSettingsSwitchHome) {
                    callMethod(child, "setTextColor", PreferencesHelper.hideIconLabelHome ? Color.TRANSPARENT : newAppLabelColor);
                    maybeHideShadow(child, !PreferencesHelper.homescreenIconLabelShadow || PreferencesHelper.hideIconLabelHome);
                }
            }
        } else if (child.getClass().equals(Classes.FolderIcon)) {
            boolean isAppDrawerFolder = ((View) param.thisObject).getParent().getClass().equals(Classes.AppsCustomizePagedView);
            Object folderName = getObjectField(child, Fields.fiFolderName);

            if (isAppDrawerFolder) {
                if (PreferencesHelper.iconSettingsSwitchApps) {
                    callMethod(folderName, "setTextColor", PreferencesHelper.hideIconLabelApps ? Color.TRANSPARENT : newAppLabelColorAppDrawer);
                    maybeHideShadow(folderName, !PreferencesHelper.appdrawerIconLabelShadow || PreferencesHelper.hideIconLabelApps);
                }
            } else {
                if (PreferencesHelper.iconSettingsSwitchHome) {
                    callMethod(folderName, "setTextColor", PreferencesHelper.hideIconLabelHome ? Color.TRANSPARENT : newAppLabelColor);
                    maybeHideShadow(folderName, !PreferencesHelper.homescreenIconLabelShadow || PreferencesHelper.hideIconLabelHome);
                    if (PreferencesHelper.appdockShowLabels
                            && ((View) param.thisObject).getParent().getClass().equals(Classes.Hotseat)) {
                        ((View) folderName).setVisibility(View.VISIBLE);
                    }
                }
            }

            if (PreferencesHelper.homescreenFolderSwitch ||
                    (isAppDrawerFolder && PreferencesHelper.iconSettingsSwitchApps)) {
                Object mFolder = getObjectField(child, Fields.fiFolder);

                LinearLayout ll = (LinearLayout) mFolder;
                Drawable d = ll.getBackground();
                d.setColorFilter(newFolderBackgroundColor, Mode.MULTIPLY);
                ll.setBackground(d);

                EditText mFolderName = (EditText) getObjectField(mFolder, Fields.fFolderEditText);
                mFolderName.setTextColor(newFolderNameColor);

                ImageView prevBackground = (ImageView) getObjectField(child, Fields.fiPreviewBackground);
                prevBackground.setVisibility(View.VISIBLE);
                Drawable i = prevBackground.getDrawable();
                i.setColorFilter(newFolderPreviewBackgroundColor, Mode.MULTIPLY);
                prevBackground.setImageDrawable(i);
            }
        } else if (PreferencesHelper.appdockShowLabels && child.getClass().equals(TextView.class)) {
            // all apps button
            TextView allAppsButton = ((TextView) child);
            allAppsButton.setVisibility(View.VISIBLE);
            int id = Common.LAUNCHER_CONTEXT.getResources().getIdentifier("all_apps_button_label", "string", Common.HOOKED_PACKAGE);
            if (id != 0) {
                allAppsButton.setText(Common.LAUNCHER_CONTEXT.getResources().getString(id));
            } else {
                allAppsButton.setText("Apps");
            }

            try {
                allAppsButton.setCompoundDrawablePadding(getIntField(Common.DEVICE_PROFIL, Fields.dpIconDrawablePaddingPx));
            } catch (Exception e) {
                if (DEBUG) log("Execption: Set padding alternatively");
                allAppsButton.setCompoundDrawablePadding(iconPadding);
            } catch (Error e) {
                if (DEBUG) log("Error: Set padding alternatively");
                allAppsButton.setCompoundDrawablePadding(iconPadding);
            }

            callMethod(child, "setTextColor", PreferencesHelper.hideIconLabelHome ? Color.TRANSPARENT : newAppLabelColor);
            maybeHideShadow(child, !PreferencesHelper.homescreenIconLabelShadow || PreferencesHelper.hideIconLabelHome);
        }
    }

    @Override
    public void onAfterHookedMethod(MethodHookParam param) throws Throwable {
        if (PreferencesHelper.appdockShowLabels
                && ((View) param.thisObject).getParent().getClass().equals(Classes.Hotseat)) {
            setBooleanField(param.thisObject, Fields.clIsHotseat, true);
            View child = (View) param.args[0];
            child.setScaleX((Float) callMethod(param.thisObject, Methods.clGetChildrenScale));
            child.setScaleY((Float) callMethod(param.thisObject, Methods.clGetChildrenScale));
            setBooleanField(param.thisObject, Fields.clIsHotseat, false);
        }
    }

    public void maybeHideShadow(Object child, boolean hide) {
        if (hide) {
            if (child.getClass().equals(Classes.BubbleTextView)) {
                setBooleanField(child, Fields.btvShadowsEnabled, false);
            }
            ((TextView) child).getPaint().clearShadowLayer();
        }
    }
}