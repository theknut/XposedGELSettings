package de.theknut.xposedgelsettings.hooks.appdrawer.tabsandfolders;

import android.content.ComponentName;
import android.content.Intent;

import java.util.ArrayList;

import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.HooksBaseClass;
import de.theknut.xposedgelsettings.ui.AllAppsList;
import de.theknut.xposedgelsettings.ui.SaveActivity;

/**
 * Created by Alexander Schulz on 05.11.2014.
 */
public abstract class TabHelper extends HooksBaseClass {

    public static TabHelper getInstance() {
        return Common.IS_PRE_GNL_4 ? TabHelperLegacy.getInstance() : TabHelperNew.getInstance();
    }

    protected ArrayList<Tab> tabs;

    abstract public Tab getTabById(long tabId);
    abstract public void invalidate();
    abstract public Tab getCurrentTabData();
    abstract public ArrayList<String> getAppsToHide();
    abstract public void scroll();
    abstract public void removeTab(Tab tab);
    abstract public void setTabColor(int color);
    abstract public void saveTabData();
    abstract public void updateTabs();
    abstract public boolean handleScroll(float overscroll);

    protected Intent getBaseIntent(boolean openVisible, long itemid, String tabname) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        intent.setComponent(new ComponentName(Common.PACKAGE_NAME, openVisible ? AllAppsList.class.getName() : SaveActivity.class.getName()));
        intent.putExtra("mode", AllAppsList.MODE_MANAGE_TAB);
        intent.putExtra("itemid", itemid);
        intent.putExtra("name", tabname);

        ArrayList<String> excludeApps = new ArrayList<String>();
        ArrayList<String> data = new ArrayList<String>(tabs.size());
        for (Tab tab : tabs) {
            data.add(tab.toString());

            if (openVisible && tab.isUserTab() && tab.getId() != itemid) {
                excludeApps.addAll(tab.getRawData());
            }
        }

        intent.putExtra("tabsdata", data);
        if (openVisible) {
            intent.putExtra("excludeapps", excludeApps);
        }
        return intent;
    }
}