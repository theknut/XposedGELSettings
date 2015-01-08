package de.theknut.xposedgelsettings.hooks.appdrawer;

import de.robv.android.xposed.XC_MethodHook;
import de.theknut.xposedgelsettings.hooks.Common;

public class AppsCustomizePagedViewConstructorHook extends XC_MethodHook {
    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        Common.APP_DRAWER_INSTANCE = (android.view.View) param.thisObject;
    }
}