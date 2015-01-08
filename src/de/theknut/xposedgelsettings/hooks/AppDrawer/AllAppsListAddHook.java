package de.theknut.xposedgelsettings.hooks.appdrawer;

import android.content.ComponentName;
import android.content.Intent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.robv.android.xposed.XC_MethodHook;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Classes;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Fields;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;
import de.theknut.xposedgelsettings.hooks.appdrawer.tabsandfolders.FolderHelper;
import de.theknut.xposedgelsettings.hooks.appdrawer.tabsandfolders.Tab;
import de.theknut.xposedgelsettings.hooks.appdrawer.tabsandfolders.TabHelper;
import de.theknut.xposedgelsettings.ui.CommonUI;
import de.theknut.xposedgelsettings.ui.SaveActivity;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.getStaticObjectField;

public final class AllAppsListAddHook extends XC_MethodHook {

    // http://androidxref.com/4.4.2_r1/xref/packages/apps/Launcher3/src/com/android/launcher3/AllAppsList.java#65
    // public void add(AppInfo info)

    List<String> packages = new ArrayList<String>();
    boolean init;
    final int APPINFOLIST = 0;

    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        if (Common.ALL_APPS == null) {
            Common.ALL_APPS = new ArrayList((ArrayList) param.args[0]);
            TabHelper.getInstance().updateTabs();

            if (!Common.IS_TREBUCHET) return;
        }

        if (PreferencesHelper.iconPackHide && !init && Common.LAUNCHER_CONTEXT != null) {
            init = true;
            packages = CommonUI.getIconPacks(Common.LAUNCHER_CONTEXT);
        }

        ArrayList<String> appsToHide = new ArrayList<String>(PreferencesHelper.hiddenApps);
        appsToHide.addAll(TabHelper.getInstance().getAppsToHide());
        appsToHide.addAll(FolderHelper.getInstance().getAppsToHide());
        appsToHide.addAll(getWorkspaceIcons());

        if (PreferencesHelper.hiddenApps.size() != 0 && PreferencesHelper.hiddenApps.iterator().next().contains("#")
                || PreferencesHelper.hiddenWidgets.size() != 0 && PreferencesHelper.hiddenWidgets.iterator().next().contains("#")) {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            intent.setComponent(new ComponentName(Common.PACKAGE_NAME, SaveActivity.class.getName()));
            intent.putExtra("mode", SaveActivity.CONVERT_APPSWIDGETS);
            Common.LAUNCHER_CONTEXT.startActivity(intent);
        }

        ArrayList appInfoList = (ArrayList) param.args[APPINFOLIST];
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
    }

    private ArrayList<String> getWorkspaceIcons() {
        ArrayList<String> appsToHide = new ArrayList<String>();

        if (PreferencesHelper.autoHideHomeIcons) {
            ArrayList workspaceItems = (ArrayList) getStaticObjectField(Classes.LauncherModel, Fields.lmWorkspaceItems);
            for (Object workspaceItem : workspaceItems) {
                if (workspaceItem.getClass().equals(Classes.ShortcutInfo)) {
                    Intent i = (Intent) callMethod(workspaceItem, "getIntent");
                    if (i != null) {
                        appsToHide.add(i.getComponent().flattenToString());
                    }
                }
            }

            Map<Long, Object> map = (HashMap<Long, Object>) getStaticObjectField(Classes.LauncherModel, Fields.lmFolders);
            for (Long key: map.keySet()) {
                Object item = map.get(key);
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

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        if (Common.IS_TREBUCHET) return;
        Tab tab = TabHelper.getInstance().getTabById(Tab.APPS_ID);
        Collections.sort((ArrayList) param.args[APPINFOLIST], tab.getSortComparator());
    }
}