package de.theknut.xposedgelsettings.hooks.appdrawer.tabsandfolders;

import android.widget.TabHost;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.theknut.xposedgelsettings.hooks.HooksBaseClass;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Classes;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Fields;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Methods;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.setIntField;

public class AddTabsAndFolders extends HooksBaseClass {

    public static void initAllHooks(LoadPackageParam lpparam) {

        findAndHookMethod(Classes.AppsCustomizeTabHost, "onFinishInflate", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(final MethodHookParam param) throws Throwable {

                TabHelper.getInstance().init((TabHost) param.thisObject);
            }
        });

        findAndHookMethod(Classes.AppsCustomizePagedView, Methods.acpvSyncAppsPageItems, Integer.TYPE, new XC_MethodHook() {
            final int PAGE = 0;
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                if (TabHelper.getInstance().loadTabPage(param.thisObject, (Integer) param.args[PAGE])) {
                    param.setResult(null);
                }
            }
        });

        findAndHookMethod(Classes.AppsCustomizePagedView, Methods.acpvSetContentType, Classes.AppsCustomizeContentType, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                TabHelper.getInstance().setContentType(param.thisObject);
                param.setResult(null);
            }
        });

        findAndHookMethod(Classes.AppsCustomizePagedView, Methods.acpvSyncPages, new XC_MethodHook() {

            int numAppPages;

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                numAppPages = TabHelper.getInstance().setNumberOfPages(param.thisObject);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                if (numAppPages != -1) {
                    setIntField(param.thisObject, Fields.acpvNumAppsPages, numAppPages);
                }
            }
        });

        findAndHookMethod(Classes.AppsCustomizeTabHost, Methods.acthSetContentTypeImmediate, Classes.AppsCustomizeContentType, new XC_MethodHook() {
            final int CONTENTTYPE = 0;
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                if (TabHelper.getInstance().setContentTypeImmediate(param.args[CONTENTTYPE])) {
                    param.setResult(null);
                }
            }
        });

        findAndHookMethod(Classes.AppsCustomizeTabHost, "reset", new XC_MethodHook() {

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Tab tab = TabHelper.getInstance().getCurrentTabData();
                if (tab != null && tab.isCustomTab()) {
                    param.setResult(null);
                }
            }
        });

        findAndHookMethod(Classes.AppsCustomizePagedView, "reset", new XC_MethodHook() {

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Tab tab = TabHelper.getInstance().getCurrentTabData();
                if (tab != null && tab.isCustomTab()) {
                    param.setResult(null);
                }
            }
        });
    }
}