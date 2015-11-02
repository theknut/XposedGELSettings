package de.theknut.xposedgelsettings.hooks.appdrawer.tabsandfolders;

import android.content.ComponentName;
import android.content.pm.PackageInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Fields;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;
import de.theknut.xposedgelsettings.hooks.Utils;

import static de.robv.android.xposed.XposedHelpers.getLongField;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

/**
 * Created by Alexander Schulz on 26.09.2014.
 */
public class AppDrawerItem {

    public enum SortType {
        Alphabetically,
        LastUpdate,
        LastInstall
    }

    protected SortType sortType = SortType.Alphabetically;
    protected ArrayList data;
    protected ArrayList<String> rawData;
    protected String title;
    protected long id;
    protected int idx;
    protected boolean hideFromAppsPage;

    public ArrayList getData() {
        return data;
    }

    public int getIndex() {
        return idx;
    }

    public void setIndex(int newIdx) {
        this.idx = newIdx;
    }

    public long getId() {
        return id;
    }

    public int getLayoutId() {
        return 0x80 + (int) getId();
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public SortType getSortType() {
        return sortType;
    }

    public void setSortType(SortType type) {
        this.sortType = type;
    }

    public boolean hideFromAppsPage() {
        return this.hideFromAppsPage;
    }

    public void setHideFromAppsPage(boolean hide) {
        this.hideFromAppsPage = hide;
    }

    protected ArrayList<String> getRawData(String keyPrefix) {
        if (this.rawData == null) {
            PreferencesHelper.prefs.reload();
            return new ArrayList<String>(PreferencesHelper.prefs.getStringSet(keyPrefix + "_" + getId(), new HashSet<String>()));
        }

        return this.rawData;
    }

    public void invalidateRawData() {
        this.rawData = null;
    }

    protected void parseData(String keyPrefix) {
        PreferencesHelper.prefs.reload();
        data = new ArrayList();
        ArrayList<String> tabData = new ArrayList<String>(PreferencesHelper.prefs.getStringSet(keyPrefix + "_" + getId(), null));
        for (String tab : tabData) {
            try {
                Object app = Utils.createAppInfo(ComponentName.unflattenFromString(tab));
                if (app != null) data.add(app);
            } catch (Exception e) { }
        }
        sort(data);
    }

    protected void sort() {
        Collections.sort(getData(), getSortComparator());
    }

    protected void sort(final ArrayList apps) {
        Collections.sort(apps, getSortComparator());
    }

    public Comparator getSortComparator() {
        if (getSortType() == SortType.LastInstall) {
            return new Comparator<Object>() {
                public final int compare(Object a, Object b) {
                    if (getLongField(a, "firstInstallTime") < getLongField(b, "firstInstallTime")) return 1;
                    if (getLongField(a, "firstInstallTime") > getLongField(b, "firstInstallTime")) return -1;
                    return 0;
                }
            };
        } else if (getSortType() == SortType.LastUpdate) {
            return new Comparator<Object>() {
                public final int compare(Object a, Object b) {
                    try {
                        PackageInfo app1 = Common.LAUNCHER_CONTEXT.getPackageManager().getPackageInfo(((ComponentName) getObjectField(a, Fields.aiComponentName)).getPackageName(), 0);
                        PackageInfo app2 = Common.LAUNCHER_CONTEXT.getPackageManager().getPackageInfo(((ComponentName) getObjectField(b, Fields.aiComponentName)).getPackageName(), 0);

                        if (app1.lastUpdateTime < app2.lastUpdateTime) return 1;
                        if (app1.lastUpdateTime > app2.lastUpdateTime) return -1;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return 0;
                }
            };
        } else {
            return TabHelper.getInstance().getAppNameComparator();
        }
    }

    @Override
    public String toString() {
        return    "idx=" + getIndex() + "|"
                + "id=" + getId() + "|"
                + "title=" + getTitle() + "|"
                + "sort=" + getSortType() + "|"
                + "hide=" + hideFromAppsPage();
    }
}