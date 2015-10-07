package de.theknut.xposedgelsettings.hooks.appdrawer;

import android.content.ComponentName;
import android.content.Intent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.HooksBaseClass;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Classes;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Fields;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;
import de.theknut.xposedgelsettings.hooks.appdrawer.tabsandfolders.FolderHelper;
import de.theknut.xposedgelsettings.ui.CommonUI;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.getStaticObjectField;

public final class AllAppsListAddMHook extends HooksBaseClass {

    // http://androidxref.com/4.4.2_r1/xref/packages/apps/Launcher3/src/com/android/launcher3/AllAppsList.java#65
    // public void add(AppInfo info)

    List<String> packages = new ArrayList<String>();
    boolean init;

    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        log("addhook");
        if (Common.ALL_APPS == null) {
            Common.ALL_APPS = new ArrayList(((HashMap) getObjectField(param.thisObject, "mComponentToAppMap")).values());
            //TabHelper.getInstance().updateTabs();
            log("bail " + Common.ALL_APPS.size());
            return;
        }
    }

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        //Tab tab = TabHelper.getInstance().getTabById(Tab.APPS_ID);
        //Collections.sort((ArrayList) param.args[APPINFOLIST], tab.getSortComparator());

        if (PreferencesHelper.iconPackHide && !init && Common.LAUNCHER_CONTEXT != null) {
            init = true;
            packages = CommonUI.getIconPacks(Common.LAUNCHER_CONTEXT);
        }

        ArrayList<String> appsToHide = new ArrayList<String>(PreferencesHelper.hiddenApps);
        //appsToHide.addAll(TabHelper.getInstance().getAppsToHide());
        appsToHide.addAll(FolderHelper.getInstance().getAppsToHide());
        appsToHide.addAll(getWorkspaceIcons());

        ArrayList appInfoList = (ArrayList) getObjectField(param.thisObject, Fields.acpvAllApps);
        Iterator it = appInfoList.iterator();

        while(it.hasNext()) {
            Object appInfo = it.next();
            ComponentName componentName = (ComponentName) getObjectField(appInfo, Fields.aiComponentName);
            if (appsToHide.contains(componentName.flattenToString())
                    || packages.contains(componentName.getPackageName())) {
                // remove it from the allAppsList if it is in our list
                it.remove();
            }
        }

        callMethod(param.thisObject, "updateAdapterItems");
    }

    private ArrayList<String> getWorkspaceIcons() {
        ArrayList<String> appsToHide = new ArrayList<String>();

        if (PreferencesHelper.autoHideHomeIcons) {
            ArrayList workspaceItems = (ArrayList) getStaticObjectField(Classes.LauncherModel, Fields.lmWorkspaceItems);
            for (Object workspaceItem : workspaceItems) {
                if (workspaceItem.getClass().equals(Classes.ShortcutInfo)) {
                    Intent i = (Intent) callMethod(workspaceItem, "getIntent");
                    if (i != null && i.getComponent() != null) {
                        appsToHide.add(i.getComponent().flattenToString());
                    }
                }
            }

            Iterator it = (Iterator) callMethod(getStaticObjectField(Classes.LauncherModel, Fields.lmFolders), "iterator");
            while(it.hasNext()) {
                Object item = it.next();
                if (!item.getClass().equals(Classes.FolderInfo)) continue;
                for (Object folderItem : ((ArrayList) getObjectField(item, Fields.fiContents))) {
                    if (folderItem.getClass().equals(Classes.ShortcutInfo)) {
                        Intent i = (Intent) callMethod(folderItem, "getIntent");
                        if (i != null && i.getComponent() != null) {
                            appsToHide.add(i.getComponent().flattenToString());
                        }
                    }
                }
            }
        }
        return appsToHide;
    }
}