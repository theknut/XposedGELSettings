package de.theknut.xposedgelsettings.hooks.appdrawer.tabsandfolders;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;
import de.theknut.xposedgelsettings.hooks.Utils;
import de.theknut.xposedgelsettings.hooks.appdrawer.tabsandfolders.TabHelper.ContentType;
import de.theknut.xposedgelsettings.hooks.appdrawer.tabsandfolders.TabHelper.SortType;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.getLongField;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

/**
 * Created by Alexander Schulz on 21.08.2014.
 */
public class Tab {

    private ContentType contentType = ContentType.User;
    private String title;
    private ArrayList data;
    private ArrayList rawData;
    private long id;
    private int idx;
    private boolean hideFromAppsPage;
    private SortType sortType = SortType.Alphabetically;

    public static final int APPS_ID = 0xABB5;
    public static final int WIDGETS_ID = 0xBEEF;

    public Tab(String tabCfg) {
        this(tabCfg, true);
    }

    public Tab(String tabCfg, boolean initData) {
        String[] settings = tabCfg.split("\\|");

        for (String setting : settings) {
            if (setting.startsWith("idx=")) {
                this.idx = Integer.parseInt(setting.split("=")[1]);
            } else if (setting.startsWith("id=")) {
                this.id = Long.parseLong(setting.split("=")[1]);
            } else if (setting.startsWith("title=")) {
                this.title = setting.split("=")[1];
            } else if (setting.startsWith("contenttype=")) {
                this.contentType = ContentType.valueOf(setting.split("=")[1]);
            } else if (setting.startsWith("hide=")) {
                this.hideFromAppsPage = Boolean.parseBoolean(setting.split("=")[1]);
            } else if (setting.startsWith("sort=")) {
                this.sortType = SortType.valueOf(setting.split("=")[1]);
            }
        }

        if (initData) initData();
    }

    public Tab(Intent intent, boolean initData) {
        this.id = intent.getLongExtra("itemid", -1);
        this.idx = intent.getIntExtra("tabindex", -1);
        this.title = intent.getStringExtra("tabname");
        this.contentType = ContentType.valueOf(intent.getStringExtra("contenttype"));
        this.hideFromAppsPage = intent.getBooleanExtra("hide", false);

        if (initData) initData();
    }

    public void initData() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                if (isUserTab()) {
                    parseData();
                } else if (isGoogleTab()) {
                    data = getGoogleApps();
                } else if (isXposedTab()) {
                    data = getXposedModules();
                } else if (isNewUpdatedTab()) {
                    data = getNewUpdatedApps();
                    setSortType(SortType.LastUpdate);
                } else if (isNewAppsTab()) {
                    data = getNewApps();
                    setSortType(SortType.LastInstall);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (!isAppsTab()) {
                    Object mAppsCustomizePane = getObjectField(TabHelper.getInstance().getTabHost(), ObfuscationHelper.Fields.acthAppsCustomizePane);
                    ArrayList allApps = (ArrayList) getObjectField(mAppsCustomizePane, ObfuscationHelper.Fields.acpvAllApps);
                    callMethod(mAppsCustomizePane, ObfuscationHelper.Methods.acpvSetApps, allApps);
                }
                TabHelper.getInstance().invalidate();
            }
        }.execute();
    }

    public ArrayList getData() {
        return data;
    }

    public int getIndex() {
        return idx;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public SortType getSortType() {
        return sortType;
    }

    public void setIndex(int newIdx) {
        this.idx = newIdx;
    }

    public boolean isUserTab() {
        return contentType.equals(ContentType.User);
    }

    public boolean isWidgetsTab() {
        return contentType.equals(ContentType.Widgets);
    }

    public boolean isXposedTab() {
        return contentType.equals(ContentType.Xposed);
    }

    public boolean isGoogleTab() {
        return contentType.equals(ContentType.Google);
    }

    public boolean isNewUpdatedTab() {
        return contentType.equals(ContentType.NewUpdated);
    }

    public boolean isNewAppsTab() {
        return contentType.equals(ContentType.NewApps);
    }

    public boolean isAppsTab() {
        return getId() == APPS_ID;
    }

    public boolean isCustomTab() {
        return isUserTab() || isDynamicTab();
    }

    public boolean isDynamicTab() {
        return isGoogleTab() || isXposedTab() || isNewUpdatedTab() || isNewAppsTab();
    }

    private void parseData() {
        PreferencesHelper.prefs.reload();
        data = new ArrayList();
        ArrayList<String> tabData = new ArrayList<String>(PreferencesHelper.prefs.getStringSet("tab_" + getId(), null));
        for (String tab : tabData) {
            try {
                Object app = Utils.createAppInfo(ComponentName.unflattenFromString(tab));
                if (app != null) data.add(app);
            } catch (Exception e) { }
        }
        sort(data);
    }

    public ArrayList getRawData() {
        if (this.rawData == null) {
            PreferencesHelper.prefs.reload();
            return new ArrayList<String>(PreferencesHelper.prefs.getStringSet("tab_" + getId(), new HashSet<String>()));
        }

        return this.rawData;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSortType(SortType type) {
        sortType = type;
        if (isCustomTab()) sort(getData());
    }

    private ArrayList getXposedModules() {
        final PackageManager pm = Common.LAUNCHER_CONTEXT.getPackageManager();
        this.rawData = new ArrayList();
        List<ApplicationInfo> apps = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        ArrayList modules = new ArrayList();

        for (ApplicationInfo app : apps) {
            if (app.metaData != null && app.metaData.getBoolean("xposedmodule", false)) {
                Intent i = pm.getLaunchIntentForPackage(app.packageName);
                if (i != null) {
                    modules.add(Utils.createAppInfo(i.getComponent()));
                    rawData.add(i.getComponent().flattenToString());
                }
            }
        }

        sort(modules);
        return modules;
    }

    private ArrayList getGoogleApps() {
        ArrayList modules = new ArrayList();
        this.rawData = new ArrayList();

        for (ResolveInfo app : Utils.getAllApps()) {
            if (app.activityInfo.packageName.contains("com.google.android.")) {
                ComponentName cmp = new ComponentName(app.activityInfo.packageName, app.activityInfo.name);
                modules.add(Utils.createAppInfo(cmp));
                rawData.add(cmp.flattenToString());
            }
        }

        sort(modules);
        return modules;
    }

    private ArrayList getNewUpdatedApps() {
        ArrayList apps = new ArrayList();
        this.rawData = new ArrayList();

        for (ResolveInfo app : Utils.getAllApps()) {
            ComponentName cmp = new ComponentName(app.activityInfo.packageName, app.activityInfo.name);
            apps.add(Utils.createAppInfo(cmp));
            rawData.add(cmp.flattenToString());
        }

        return apps;
    }

    private ArrayList getNewApps() {
        ArrayList apps = new ArrayList();
        this.rawData = new ArrayList();

        for (ResolveInfo app : Utils.getAllApps()) {
            ComponentName cmp = new ComponentName(app.activityInfo.packageName, app.activityInfo.name);
            apps.add(Utils.createAppInfo(cmp));
            rawData.add(cmp.flattenToString());
        }

        return apps;
    }

    private void sort(ArrayList apps) {
        Collections.sort(apps, getSortComparator());
    }

    public Comparator getSortComparator() {
        Comparator comparator;

        if (getSortType() == SortType.LastInstall) {
            comparator = new Comparator<Object>() {
                public final int compare(Object a, Object b) {
                    if (getLongField(a, "firstInstallTime") < getLongField(b, "firstInstallTime")) return 1;
                    if (getLongField(a, "firstInstallTime") > getLongField(b, "firstInstallTime")) return -1;
                    return 0;
                }
            };
        } else if (getSortType() == SortType.LastUpdate) {
            comparator = new Comparator<Object>() {
                public final int compare(Object a, Object b) {
                    try {
                        PackageInfo app1 = Common.LAUNCHER_CONTEXT.getPackageManager().getPackageInfo(((ComponentName) getObjectField(a, ObfuscationHelper.Fields.aiComponentName)).getPackageName(), 0);
                        PackageInfo app2 = Common.LAUNCHER_CONTEXT.getPackageManager().getPackageInfo(((ComponentName) getObjectField(b, ObfuscationHelper.Fields.aiComponentName)).getPackageName(), 0);

                        if (app1.lastUpdateTime < app2.lastUpdateTime) return 1;
                        if (app1.lastUpdateTime > app2.lastUpdateTime) return -1;
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }

                    return 0;
                }
            };
        } else {
            comparator = (Comparator) callStaticMethod(ObfuscationHelper.Classes.LauncherModel, ObfuscationHelper.Methods.lmGetAppNameComparator);
        }

        return comparator;
    }

    @Override
    public String toString() {
        return    "idx=" + getIndex() + "|"
                + "id=" + getId() + "|"
                + "contenttype=" + getContentType() + "|"
                + "title=" + getTitle() + "|"
                + "hide=" + hideFromAppsPage() + "|"
                + "sort=" + getSortType();
    }

    public boolean hideFromAppsPage() {
        return this.hideFromAppsPage;
    }

    public void setHideFromAppsPage(boolean hide) {
        this.hideFromAppsPage = hide;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void update() {
        initData();
    }
}
