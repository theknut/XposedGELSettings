package de.theknut.xposedgelsettings.hooks.common;

import java.util.ArrayList;

import de.theknut.xposedgelsettings.hooks.HooksBaseClass;

/**
 * Created by Alexander Schulz on 08.11.2014.
 */
public class XGELSHook extends HooksBaseClass {

    private ArrayList<XGELSCallback> listeners;

    public XGELSHook(ArrayList<XGELSCallback> listeners) {
        this.listeners = listeners;
    }

    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        for (XGELSCallback listener : listeners) {
            listener.onBeforeHookedMethod(param);
        }
    }

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        for (XGELSCallback listener : listeners) {
            listener.onAfterHookedMethod(param);
        }
    }
}