package de.theknut.xposedgelsettings.hooks.common;

import de.robv.android.xposed.XC_MethodHook;
import de.theknut.xposedgelsettings.hooks.HooksBaseClass;

/**
 * Created by Alexander Schulz on 08.11.2014.
 */
public class XGELSCallback extends HooksBaseClass implements ICallback {

    @Override
    public void onBeforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable { }
    @Override
    public void onAfterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable { }
}