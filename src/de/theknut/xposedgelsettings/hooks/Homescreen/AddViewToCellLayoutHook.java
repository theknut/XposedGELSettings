package de.theknut.xposedgelsettings.hooks.homescreen;

import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Classes;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Fields;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Methods;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;
import de.theknut.xposedgelsettings.hooks.appdrawer.ApplyFromApplicationInfoHook;
import de.theknut.xposedgelsettings.hooks.common.XGELSCallback;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.getBooleanField;
import static de.robv.android.xposed.XposedHelpers.getIntField;
import static de.robv.android.xposed.XposedHelpers.getLongField;
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
        Object tag = child.getTag();
        long containerType = -1;

        if (tag != null) {
            try {
                containerType = getLongField(tag, Fields.iiContainer);
            } catch (Exception e) {}
        }

        boolean showInDock = containerType == -101 && PreferencesHelper.appdockShowLabels;
        boolean isAppDrawerItem = param.thisObject.getClass().equals(Classes.AppsCustomizeCellLayout);

        if (child.getClass().equals(Classes.BubbleTextView)) {

            if (isAppDrawerItem) {
                if (PreferencesHelper.hideIconLabelApps) {
                    callMethod(child, "setTextColor", Color.TRANSPARENT);
                }
                else {
                    callMethod(child, "setTextColor", ApplyFromApplicationInfoHook.newColor);

                    if (!PreferencesHelper.appdrawerIconLabelShadow) {
                        ((TextView) child).getPaint().clearShadowLayer();
                    }
                }
            } else if (!getBooleanField(child, Fields.btvShadowsEnabled)) {
                // apps in folders don't have a shadow so we can filter that for future customization
                if (PreferencesHelper.homescreenFolderSwitch) {
                    if (PreferencesHelper.homescreenFolderNoLabel) {
                        ((TextView) child).getPaint().clearShadowLayer();
                        callMethod(child, "setTextColor", Color.TRANSPARENT);
                    } else {
                        callMethod(child, "setTextColor", newFolderAppLabelColor);
                    }
                }
            } else {
                if (PreferencesHelper.iconSettingsSwitchHome) {
                    if (Common.PACKAGE_OBFUSCATED) {
                        if (Common.IS_PRE_GNL_4) {
                            if (!PreferencesHelper.homescreenIconLabelShadow) {
                                callMethod(child, Methods.btvSetShadowsEnabled, PreferencesHelper.homescreenIconLabelShadow);
                            }
                        } else {
                            setBooleanField(child, Fields.btvShadowsEnabled, PreferencesHelper.homescreenIconLabelShadow);
                        }
                    } else {
                        callMethod(child, Methods.btvSetShadowsEnabled, PreferencesHelper.homescreenIconLabelShadow);
                    }

                    if (PreferencesHelper.hideIconLabelHome && !showInDock) {
                        ((TextView) child).getPaint().clearShadowLayer();
                        callMethod(child, "setTextColor", Color.TRANSPARENT);
                    } else {
                        callMethod(child, "setTextColor", newAppLabelColor);
                    }
                }

                iconPadding = ((TextView) child).getCompoundDrawablePadding();
            }

            if (PreferencesHelper.appdockSettingsSwitch) {
                ViewGroup parent = (ViewGroup) ((View) param.thisObject).getParent().getParent();
                if (parent.getClass().equals(Classes.Folder)) {

                }
            }

        } else if (child.getClass().equals(Classes.FolderIcon)) {
            boolean isAppDrawerFolder = ((View) param.thisObject).getParent().getClass().equals(Classes.AppsCustomizePagedView);
            Object folderName = getObjectField(child, Fields.fiFolderName);
            if (isAppDrawerFolder) {
                if (PreferencesHelper.iconSettingsSwitchApps) {
                    callMethod(folderName, "setTextColor", newAppLabelColorAppDrawer);
                    if (PreferencesHelper.hideIconLabelApps) {
                        ((TextView) folderName).getPaint().clearShadowLayer();
                        callMethod(folderName, "setTextColor", Color.TRANSPARENT);
                    }
                }
            } else {
                if (PreferencesHelper.iconSettingsSwitchHome) {
                    if (Common.PACKAGE_OBFUSCATED) {
                        if (Common.IS_PRE_GNL_4) {
                            if (!PreferencesHelper.homescreenIconLabelShadow) {
                                callMethod(folderName, Methods.btvSetShadowsEnabled, PreferencesHelper.homescreenIconLabelShadow);
                            }
                        } else {
                            setBooleanField(folderName, Fields.btvShadowsEnabled, PreferencesHelper.homescreenIconLabelShadow);
                        }
                    } else {
                        callMethod(folderName, Methods.btvSetShadowsEnabled, PreferencesHelper.homescreenIconLabelShadow);
                    }

                    callMethod(folderName, "setTextColor", newAppLabelColor);

                    if (PreferencesHelper.appdockShowLabels) {
                        ((View) folderName).setVisibility(View.VISIBLE);
                    }

                    if (PreferencesHelper.hideIconLabelHome && !showInDock) {
                        ((TextView) folderName).getPaint().clearShadowLayer();
                        callMethod(folderName, "setTextColor", Color.TRANSPARENT);
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
        }
    }
}