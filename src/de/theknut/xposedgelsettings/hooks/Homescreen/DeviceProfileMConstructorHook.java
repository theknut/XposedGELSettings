package de.theknut.xposedgelsettings.hooks.homescreen;

import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Fields;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;
import de.theknut.xposedgelsettings.hooks.common.XGELSCallback;

import static de.robv.android.xposed.XposedHelpers.getIntField;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.setIntField;
import static de.robv.android.xposed.XposedHelpers.setObjectField;

public final class DeviceProfileMConstructorHook extends XGELSCallback {

    public static Float originalIconSize;

    @Override
    public void onAfterHookedMethod(MethodHookParam param) throws Throwable {

        Object InvDevProf = param.args[1];

        if (PreferencesHelper.changeGridSizeHome) {
            if (PreferencesHelper.yCountHomescreenVertical != -1) {
                // set custom row count
                setObjectField(InvDevProf, "numRows", PreferencesHelper.yCountHomescreenVertical);
            }

            if (PreferencesHelper.xCountHomescreenVertical != -1) {
                // set custom column count
                setObjectField(InvDevProf, "numColumns", PreferencesHelper.xCountHomescreenVertical);
            }
        }

        if (PreferencesHelper.iconSettingsSwitchHome) {
            // calculating custom sizes
            originalIconSize = (Float) getObjectField(InvDevProf, "iconSize");
            float newIconSize = (float) (Math.ceil(originalIconSize * (PreferencesHelper.iconSize / 100.0)));
            float newIconTextSize = (float) (Math.ceil((Float) getObjectField(InvDevProf, "iconTextSize") * (PreferencesHelper.iconTextSize / 100.0)));

            // some validation
            if (newIconSize > 0.0) {
                setObjectField(InvDevProf, "iconSize", newIconSize);
            } else {
                log("Didn't change icon size! Value was " + newIconSize);
            }

            // some validation
            if (newIconTextSize > 0.0) {
                setObjectField(InvDevProf, "iconTextSize", newIconTextSize);
            } else {
                log("Didn't change icon text size! Value was " + newIconTextSize);
            }
        }

        if (PreferencesHelper.appdockSettingsSwitch) {
            float newHotseatIconSize = (float) (Math.ceil((Float) getObjectField(InvDevProf, "hotseatIconSize") * (PreferencesHelper.appdockIconSize / 100.0)));

            // some validation
            if (newHotseatIconSize > 0.0) {
                setObjectField(InvDevProf, "hotseatIconSize", newHotseatIconSize);
            } else {
                log("Didn't change hotseat icon size! Value was " + newHotseatIconSize);
            }

            int hotseatBarHeight = (Integer) getObjectField(param.thisObject, "iconSizePx") + 4 * (Integer) getObjectField(param.thisObject, "edgeMarginPx");
            setIntField(param.thisObject, Fields.dpHotseatBarHeightPx, hotseatBarHeight);
        }

        if (PreferencesHelper.noAllAppsButton) {
            setIntField(InvDevProf, Fields.dpHotseatAllAppsRank, 50);
        } else {
            if (PreferencesHelper.homescreenAllAppsPosition != -1) {
                setIntField(InvDevProf, Fields.dpHotseatAllAppsRank, PreferencesHelper.homescreenAllAppsPosition - 1);
            }
        }

        if (PreferencesHelper.appDockCount != -1) {
            setIntField(InvDevProf, Fields.dpNumHotseatIcons, PreferencesHelper.appDockCount);
        }

        if (Common.APP_DRAWER_ICON_SIZE == -1) {
            if (PreferencesHelper.iconSettingsSwitchApps) {
                Common.APP_DRAWER_ICON_SIZE = (int) Math.round(getIntField(param.thisObject, Fields.dpAllAppsIconSize) * (PreferencesHelper.iconSizeAppDrawer / 100.0));
            } else {
                Common.APP_DRAWER_ICON_SIZE = getIntField(param.thisObject, Fields.dpAllAppsIconSize);
            }
        }

        if (PreferencesHelper.iconSettingsSwitchApps) {
            setIntField(param.thisObject, Fields.dpAllAppsIconSize, Common.APP_DRAWER_ICON_SIZE);
        }
    }
}