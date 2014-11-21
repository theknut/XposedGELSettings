package de.theknut.xposedgelsettings.hooks.common;

import de.robv.android.xposed.XC_MethodHook;

/**
 * Created by Alexander Schulz on 08.11.2014.
 */
public interface ICallback {
    public abstract void onBeforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable;
    public abstract void onAfterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable;
}