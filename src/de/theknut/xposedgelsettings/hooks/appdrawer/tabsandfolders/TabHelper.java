package de.theknut.xposedgelsettings.hooks.appdrawer.tabsandfolders;

import java.util.ArrayList;

import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.HooksBaseClass;

/**
 * Created by Alexander Schulz on 05.11.2014.
 */
public abstract class TabHelper extends HooksBaseClass {

    public static TabHelper getInstance() {
        return Common.IS_PRE_GNL_4 ? TabHelperLegacy.getInstance() : TabHelperNew.getInstance();
    }

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
}