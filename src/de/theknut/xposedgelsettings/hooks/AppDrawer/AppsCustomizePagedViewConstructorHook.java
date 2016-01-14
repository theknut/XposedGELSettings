package de.theknut.xposedgelsettings.hooks.appdrawer;

import android.view.View;

import de.robv.android.xposed.XC_MethodHook;
import de.theknut.xposedgelsettings.hooks.Common;

import static de.robv.android.xposed.XposedHelpers.getObjectField;

public class AppsCustomizePagedViewConstructorHook extends XC_MethodHook {
    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        Common.APP_DRAWER_INSTANCE = (View) param.thisObject;
        Common.ALPHABETICAL_APPS_LIST = getObjectField(param.thisObject, "mApps");
    }
}