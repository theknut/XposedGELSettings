package de.theknut.xposedgelsettings.hooks.appdrawer.tabsandfolders;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import de.theknut.xposedgelsettings.R;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Classes;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Fields;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Methods;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;
import de.theknut.xposedgelsettings.hooks.Utils;
import de.theknut.xposedgelsettings.hooks.appdrawer.tabsandfolders.AppDrawerItem.SortType;
import de.theknut.xposedgelsettings.ui.AllAppsList;
import de.theknut.xposedgelsettings.ui.AllWidgetsList;
import de.theknut.xposedgelsettings.ui.SaveActivity;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.getIntField;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.newInstance;
import static de.robv.android.xposed.XposedHelpers.setIntField;
import static de.robv.android.xposed.XposedHelpers.setObjectField;

/**
 * Created by Alexander Schulz on 21.08.2014.
 */
public final class TabHelperLegacy extends TabHelper implements View.OnClickListener, View.OnLongClickListener {

    public enum ContentType {
        Applications,
        User,
        Xposed,
        Google,
        Widgets,
        IconPacks,
        NewUpdated,
        NewApps
    }

    private static final TabHelperLegacy INSTANCE = new TabHelperLegacy();
    private static Context XGELSContext;
    public static final int MOVE_LEFT = -1;
    public static final int MOVE_RIGHT = 1;

    private Tab tmpWidgetTab;

    TabHost tabHost;
    ArrayList<Tab> tabs;
    HorizontalScrollView hsv;
    ImageView addButton;
    FrameLayout tabsContainer;
    View progressBar;
    View tabHostDivider;
    AlertDialog tabSettingsDialog;

    public static TabHelperLegacy getInstance() {
        return INSTANCE;
    }

    public TabHost getTabHost() {
        return tabHost;
    }

    public void init(TabHost tabhost) {

        XGELSContext = Common.XGELSCONTEXT;

        this.tabHost = tabhost;
        this.tabs = new ArrayList<Tab>();

        addHorizontalScrollView(PreferencesHelper.moveTabHostBottom);
        //addProgressBar();

        showTabBar();

        tabHost.setOnTabChangedListener(null);
        initTabs();
        addTabs(false);
        tabHost.setOnTabChangedListener((TabHost.OnTabChangeListener) tabhost);
        tabHost.setCurrentTab(0);
        tabhost.getTabWidget().setCurrentTab(0);

        int id = tabhost.getContext().getResources().getIdentifier("market_button", "id", Common.HOOKED_PACKAGE);
        tabhost.removeView(tabhost.findViewById(id));
    }

    private void addHorizontalScrollView(boolean alignBottom) {
        TabWidget tabWidget = tabHost.getTabWidget();
        tabsContainer = (FrameLayout) tabWidget.getParent();
        tabsContainer.removeView(tabWidget);

        LayoutParams lptw = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        lptw.gravity = Gravity.START;

        RelativeLayout relativeLayout = (RelativeLayout) LayoutInflater.from(XGELSContext).inflate(R.layout.tab_widget, null, false);
        hsv = (HorizontalScrollView) relativeLayout.findViewById(R.id.horizontalscrollview);
        hsv.setSmoothScrollingEnabled(true);

        hsv.addView(tabWidget, lptw);

        addButton = (ImageView) relativeLayout.findViewById(R.id.addbutton);
        addButton.setOnClickListener(this);

        tabHostDivider = relativeLayout.findViewById(R.id.tab_host_divider);

        if (alignBottom) {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) hsv.getLayoutParams();
            layoutParams.setMargins(0, 0, 0, Utils.dpToPx(-2));
            hsv.requestLayout();

            FrameLayout tabContent = (FrameLayout) tabHost.findViewById(android.R.id.tabcontent);
            ViewGroup parent = (ViewGroup) tabContent.getParent();
            parent.removeView(tabContent);
            parent.addView(tabContent, 0, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1f));
        } else {
            relativeLayout.removeView(tabHostDivider);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(tabHostDivider.getLayoutParams());
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, relativeLayout.getId());
            relativeLayout.addView(tabHostDivider, params);
        }

        tabsContainer.addView(relativeLayout);
    }

    private void addProgressBar() {

        FrameLayout tabContent = (FrameLayout) tabHost.findViewById(android.R.id.tabcontent);
        ViewGroup parent = (ViewGroup) tabContent.getParent();
        parent.removeView(tabContent);

        RelativeLayout tabContentWithProgressBar = (RelativeLayout) LayoutInflater.from(XGELSContext).inflate(R.layout.tab_content_progressbar, null, false);
        tabContentWithProgressBar.addView(tabContent);

        progressBar = tabContentWithProgressBar.findViewById(R.id.tab_progressbar);
        progressBar.bringToFront();

        parent.addView(tabContentWithProgressBar);
    }

    public void showTabBar() {
        tabsContainer.setVisibility(PreferencesHelper.enableAppDrawerTabs ? View.VISIBLE : View.GONE);
    }

    public void hideTabBar() {
        tabsContainer.setVisibility(View.GONE);
    }

    public void initTabs() {

        if (!PreferencesHelper.enableAppDrawerTabs) {
            tabs.add(new Tab("idx=" + 0 + "|id=" + Tab.APPS_ID + "|contenttype=" + ContentType.Applications + "|title=" + tabHost.getTabWidget().getChildTabViewAt(0).getContentDescription().toString() + "|hide=" + false, false));
            tabs.add(new Tab("idx=" + 1 + "|id=" + Tab.WIDGETS_ID + "|contenttype=" + ContentType.Widgets + "|title=" + tabHost.getTabWidget().getChildTabViewAt(1).getContentDescription().toString() + "|hide=" + false, false));
            tabHost.getTabWidget().removeAllViews();
            return;
        }

        Tab appsTab = new Tab("idx=" + 0 + "|id=" + Tab.APPS_ID + "|contenttype=" + ContentType.Applications + "|title=" + tabHost.getTabWidget().getChildTabViewAt(0).getContentDescription().toString() + "|hide=" + false, false);
        tabHost.getTabWidget().removeAllViews();

        for (String item : PreferencesHelper.appdrawerTabData) {
            tabs.add(new Tab(item, false));
        }

        boolean hasApps = false;
        for (int i = 0; i < tabs.size(); i++) {
            if (tabs.get(i).isAppsTab()) {
                hasApps = true;
            }
        }

        if (!hasApps) {
            tabs.add(0, appsTab);
        }
    }

    private void addTabs(boolean focusLastTab) {
        Collections.sort(tabs, new Comparator<Tab>() {
            @Override
            public int compare(Tab lhs, Tab rhs) {
                return lhs.getIndex() - rhs.getIndex();
            }
        });

        for (Tab tab : tabs) {
            addTabInternal(tab, focusLastTab);
        }
    }

    public void addTab(Tab tab) {
        tabs.add(tab);
        addTabInternal(tab, true);
        Toast.makeText(tabHost.getContext(), XGELSContext.getString(R.string.toast_appdrawer_tabadded_title), Toast.LENGTH_LONG).show();
        Toast.makeText(tabHost.getContext(), XGELSContext.getString(R.string.toast_appdrawer_tabadded_title), Toast.LENGTH_LONG).show();
    }

    private void addTabInternal(final Tab tab, boolean focus) {
        if (DEBUG) log("Add tab " + tab.toString());
        TabHost.TabContentFactory contentFactory = new TabHost.TabContentFactory() {
            public View createTabContent(String tag) {
                return (View) getObjectField(tabHost, Fields.acthAppsCustomizePane);
            }
        };

        LayoutInflater mLayoutInflater = (LayoutInflater) getObjectField(tabHost, "mLayoutInflater");
        int tab_widget_indicator = Common.LAUNCHER_CONTEXT.getResources().getIdentifier("tab_widget_indicator", "layout", Common.HOOKED_PACKAGE);

        TextView tabView = (TextView) mLayoutInflater.inflate(tab_widget_indicator, tabHost.getTabWidget(), false);
        tabView.setBackground(XGELSContext.getResources().getDrawable(R.drawable.tab_indicator));
        tabView.setText(tab.getTitle());
        tabView.setTag(tab);
        tabView.setContentDescription(tab.getTitle());
        tabView.setOnLongClickListener(this);
        tabView.getBackground().setColorFilter(tab.getPrimaryColor(), PorterDuff.Mode.MULTIPLY);
        tabHost.addTab(tabHost.newTabSpec(tab.isWidgetsTab() ? "WIDGETS" : "APPS").setIndicator(tabView).setContent(contentFactory));

        if (focus) {
            tabHost.setCurrentTab(tabHost.getTabWidget().getTabCount() - 1);
            scroll();
        }
    }

    @Override
    public void removeTab(Tab tab) {
        tabHost.setOnTabChangedListener(null);

        tabHost.getTabWidget().removeAllViews();

        if (tabs.contains(tab)) {
            tabs.remove(tab);
            onTabsDataChanged();
        }

        addTabs(false);

        tabHost.setOnTabChangedListener((TabHost.OnTabChangeListener) tabHost);

        int newId = tab.getIndex() - 1;
        setCurrentTab(newId < 0 ? 0 : newId);
        tabHost.getTabWidget().setCurrentTab(newId < 0 ? 0 : newId);

        if (tab.hideFromAppsPage()) {
            Object mAppsCustomizePane = getObjectField(getTabHost(), Fields.acthAppsCustomizePane);
            ArrayList allApps = (ArrayList) getObjectField(mAppsCustomizePane, Fields.acpvAllApps);
            allApps.addAll(tab.getData());
            callMethod(mAppsCustomizePane, Methods.acpvSetApps, allApps);
        }

        scroll();
    }

    public void renameTab(long tabId, String tabName) {
        for (int i = 0; i < tabHost.getTabWidget().getTabCount(); i++) {
            Tab tab = (Tab) tabHost.getTabWidget().getChildTabViewAt(i).getTag();
            if (tab.getId() == tabId) {
                tab.setTitle(tabName);
                ((TextView) tabHost.getTabWidget().getChildAt(i)).setText(tab.getTitle());
                break;
            }
        }
    }

    public void moveTab(Tab tabToMove, int direction) {
        int newIdx = tabToMove.getIndex() + direction;
        if (newIdx == -1 || newIdx == tabHost.getTabWidget().getTabCount()) return;

        Iterator<Tab> it = tabs.iterator();
        while(it.hasNext()) {
            Tab tab = it.next();
            if (tab.getIndex() == newIdx) {
                tab.setIndex(tabToMove.getIndex());
                tabToMove.setIndex(newIdx);
            }
        }

        onTabsDataChanged();

        tabHost.setOnTabChangedListener(null);
        tabHost.getTabWidget().removeAllViews();

        addTabs(false);
        setCurrentTab(newIdx);

        tabHost.setOnTabChangedListener((TabHost.OnTabChangeListener) tabHost);

        Intent intent = getBaseIntent(false, tabToMove.getId(), null);
        Common.LAUNCHER_CONTEXT.startActivity(intent);
    }

    public boolean setContentTypeImmediate(Object contentType) {
        if (contentType.toString().equals("Widgets")) {
            for (Tab tab : tabs) {
                if (tab.isWidgetsTab()) {
                    setCurrentTab(tab.getIndex());
                    return true;
                }
            }

            hideTabBar();
            tmpWidgetTab = new Tab("idx=" + tabHost.getTabWidget().getTabCount() + "|id=" + Tab.WIDGETS_ID + "|contenttype=" + ContentType.Widgets + "|title=" + "Widgets", false);
            addTabInternal(tmpWidgetTab, true);
            return true;
        } else {
            if (!PreferencesHelper.appdrawerRememberLastPosition) {
                setCurrentTab(getTabById(Tab.APPS_ID).getIndex());
            }
        }

        if (tmpWidgetTab != null) {
            removeTab(tmpWidgetTab);
        }

        showTabBar();
        invalidate();
        return false;
    }

    @Override
    public boolean handleScroll(float overscroll) {
        if (overscroll > 100.0) {
            int newId = getCurrentTabData().getIndex() + 1;
            setCurrentTab(newId >= tabHost.getTabWidget().getChildCount() ? 0 : newId);
            return true;
        } else if (overscroll < -100.0) {
            int newId = getCurrentTabData().getIndex() - 1;
            setCurrentTab(newId < 0 ? (tabHost.getTabWidget().getChildCount() - 1) : newId);
            return true;
        }
        return false;
    }

    @Override
    public void updateTabs() {
        if (Common.IS_TREBUCHET) return;

        for (Tab tab : tabs) {
            tab.update();
        }
    }

    @Override
    public void invalidate() {
        setTabColor(getCurrentTabData().getPrimaryColor());
        Object mAppsCustomizePane = getObjectField(tabHost, Fields.acthAppsCustomizePane);
        callMethod(mAppsCustomizePane, Methods.acpvInvalidatePageData, PreferencesHelper.appdrawerRememberLastPosition ? Common.APPDRAWER_LAST_PAGE_POSITION : 0, true);
    }

    @Override
    public void setTabColor(int color) {
        getCurrentTabData().setColor(color);
        tabHostDivider.setBackgroundColor(color);
        tabHost.getCurrentTabView().getBackground().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        addButton.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
    }

    public void setCurrentTab(int idx) {
        setCurrentTab(idx, 0);
    }

    public void setCurrentTab(int idx, int page) {
        tabHost.setCurrentTab(idx);
        callMethod(getObjectField(tabHost, Fields.acthAppsCustomizePane), Methods.pvSetCurrentPage, page);
        setTabColor(tabs.get(idx).getPrimaryColor());
        scroll();
    }

    @Override
    public void saveTabData() {
        Intent intent = getBaseIntent(false, 0, null);
        Common.LAUNCHER_CONTEXT.startActivity(intent);
    }

    private void onTabsDataChanged() {
        syncIndexes();
    }

    private void syncIndexes() {
        int i = 0;

        Collections.sort(tabs, new Comparator<Tab>() {
            @Override
            public int compare(Tab lhs, Tab rhs) {
                return lhs.getIndex() - rhs.getIndex();
            }
        });

        Iterator<Tab> it = tabs.iterator();
        while (it.hasNext()) {
            it.next().setIndex(i++);
        }
    }

    @Override
    public void scroll() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                View tab = tabHost.getTabWidget().getChildTabViewAt(tabHost.getCurrentTab());
                hsv.smoothScrollTo((tab.getLeft() + tab.getWidth() / 2) - (hsv.getWidth() / 2) - (addButton.getWidth() / 2), 0);
            }
        });
    }

    public void showProgressBar() {
        if (!isProgressBarVisible()) {
            Common.LAUNCHER_INSTANCE.runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.VISIBLE);
                            progressBar.bringToFront();
                        }
                    });
        }
    }

    public void hideProgressBar() {
        Common.LAUNCHER_INSTANCE.runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    public boolean isProgressBarVisible() {
        return progressBar.getVisibility() == View.VISIBLE;
    }

    public Tab getTabById(long tabId) {
        if (tabs == null) return null;

        for (Tab tab : tabs) {
            if (tab.getId() == tabId) {
                return tab;
            }
        }

        return null;
    }

    public long getNewTabId() {
        int i = 0;
        long[] ids = new long[tabs.size()];

        Iterator<Tab> it = tabs.iterator();
        while (it.hasNext()) {
            ids[i++] = it.next().getId();
        }

        Arrays.sort(ids);
        long lastId = -1;
        for (long id : ids) {
            if (id > (lastId + 1)) {
                return ++lastId;
            }
            lastId = id;
        }

        return ++lastId;
    }

    @Override
    public void onClick(View v) {
        if (checkPremium()) {
            setupTabSettings(null);
        } else {
            Utils.showPremiumOnly();
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (checkPremium()) {
            setCurrentTab(((Tab) v.getTag()).getIndex());
            setupTabSettings((Tab) v.getTag());
            return true;
        }

        Utils.showPremiumOnly();
        return false;
    }

    public int setNumberOfPages(Object thisObject) {
        Tab curTab = getCurrentTabData();
        if (curTab == null) return -1;

        int numAppPages = getIntField(thisObject, Fields.acpvNumAppsPages);
        if ((curTab.isAppsTab() || curTab.isUserTab()) && FolderHelper.getInstance().hasFolder()) {
            int mCellCountX = getIntField(thisObject, Fields.acpvCellCountX);
            int mCellCountY = getIntField(thisObject, Fields.acpvCellCountY);
            int itemCnt = FolderHelper.getInstance().getFoldersForTab(curTab.getId()).size();
            itemCnt += curTab.isAppsTab() ? FolderHelper.getInstance().getAllApps().size() : curTab.getData().size();
            setIntField(thisObject, Fields.acpvNumAppsPages, (int) Math.ceil((float) itemCnt / (mCellCountX * mCellCountY)));
            return numAppPages;
        } else if (curTab.isCustomTab() && curTab.getData() != null) {
            int mCellCountX = getIntField(thisObject, Fields.acpvCellCountX);
            int mCellCountY = getIntField(thisObject, Fields.acpvCellCountY);
            setIntField(thisObject, Fields.acpvNumAppsPages, (int) Math.ceil((float) curTab.getData().size() / (mCellCountX * mCellCountY)));
            return numAppPages;
        } else if (curTab.isNewAppsTab() || curTab.isNewUpdatedTab()) {
            setIntField(thisObject, Fields.acpvNumAppsPages, 1);
            return 1;
        }

        return -1;
    }

    @Override
    public Tab getCurrentTabData() {
        return (Tab) tabHost.getCurrentTabView().getTag();
    }

    public boolean loadTabPage(Object thisObject, int page) {
        Tab curTab = getCurrentTabData();
        if (curTab == null) return false;

        if ((curTab.isAppsTab() || curTab.isUserTab()) && FolderHelper.getInstance().hasFolder()) {
            ArrayList items;
            if (curTab.isAppsTab()) {
                items = new ArrayList(FolderHelper.getInstance().getAllApps());
            } else if (curTab.isUserTab() && curTab.getData() != null) {
                items = new ArrayList(curTab.getData());
            } else {
                return false;
            }

            items.addAll(0, FolderHelper.getInstance().getFoldersForTab(curTab.getId()));
            syncAppsPageItems(thisObject, items, page);
            return true;
        } else if (curTab.isCustomTab() && curTab.getData() != null) {
            syncAppsPageItems(thisObject, curTab.getData(), page);
            return true;
        }

        return false;
    }

    private void syncAppsPageItems(Object thisObject, ArrayList apps, int page) {
        final boolean isRtl = (Boolean) callMethod(thisObject, Methods.pvIsLayoutRtl);
        LayoutInflater mLayoutInflater = (LayoutInflater) getObjectField(thisObject, "mLayoutInflater");

        int mCellCountX = getIntField(thisObject, Fields.acpvCellCountX);
        int mCellCountY = getIntField(thisObject, Fields.acpvCellCountY);
        int apps_customize_application = Common.LAUNCHER_CONTEXT.getResources().getIdentifier("apps_customize_application", "layout", Common.HOOKED_PACKAGE);

        int numCells = mCellCountX * mCellCountY;
        int startIndex = page * numCells;
        int endIndex = Math.min(startIndex + numCells, apps.size());
        ViewGroup appsCustomizeCellLayout = (ViewGroup) callMethod(thisObject, Methods.pvGetPageAt, page);

        callMethod(appsCustomizeCellLayout, Fields.acpvRemoveAllViewsOnPage);
        for (int i = startIndex; i < endIndex; ++i) {
            Object info = apps.get(i);
            View icon;

            int index = i - startIndex;
            int x = index % mCellCountX;
            int y = index / mCellCountX;
            if (isRtl) {
                x = mCellCountX - x - 1;
            }

            if (info instanceof Folder) {
                icon = ((Folder) info).makeFolderIcon(appsCustomizeCellLayout);
                if (icon == null) continue;
            } else {
                icon = mLayoutInflater.inflate(apps_customize_application, appsCustomizeCellLayout, false);
                if (Common.PACKAGE_OBFUSCATED) {
                    callMethod(icon, Methods.pviApplyFromApplicationInfo, info, thisObject);
                } else {
                    callMethod(icon, Methods.pviApplyFromApplicationInfo, info, true, thisObject);
                }
                icon.setOnClickListener((View.OnClickListener) thisObject);
                icon.setOnLongClickListener((View.OnLongClickListener) thisObject);
                icon.setOnTouchListener((View.OnTouchListener) thisObject);
                icon.setOnKeyListener((View.OnKeyListener) thisObject);
            }

            callMethod(appsCustomizeCellLayout, Methods.clAddViewToCellLayout, icon, -1, i, newInstance(Classes.CellLayoutLayoutParams, x, y, 1, 1), false);
        }

        callMethod(thisObject, Methods.acpvEnableHwLayersOnVisiblePages);
    }

    public void setContentType(Object thisObject) {
        Tab tab = getCurrentTabData();
        setObjectField(thisObject, Fields.acpvContentType, callMethod(tabHost, Methods.acthGetContentTypeForTabTag, tab.isWidgetsTab() ? "WIDGETS" : "APPS"));
        setTabColor(getCurrentTabData().getPrimaryColor());
        Object mAppsCustomizePane = getObjectField(tabHost, Fields.acthAppsCustomizePane);
        callMethod(mAppsCustomizePane, Methods.acpvInvalidatePageData, 0, true);
        scroll();
    }

    @Override
    public ArrayList<String> getAppsToHide() {
        ArrayList apps = new ArrayList();
        if (Common.IS_TREBUCHET || !Common.IS_PRE_GNL_4) return apps;

        for (Tab tab : tabs) {
            if (tab.hideFromAppsPage()) {
                apps.addAll(tab.getRawData());
            }
        }
        return apps;
    }

    public boolean isTabSettingsOpen() {
        if (tabSettingsDialog != null) {
            return tabSettingsDialog.isShowing();
        }
        return false;
    }

    public void closeTabSettings() {
        tabSettingsDialog.cancel();
    }

    private void setupTabSettings(final Tab tab) {

        final boolean newTab = tab == null;

        final ViewGroup tabSettingsView = (ViewGroup) LayoutInflater.from(XGELSContext).inflate(R.layout.tab_settings_view, null);
        if (newTab) {
            tabSettingsView.findViewById(R.id.tab_settings_bar).setVisibility(View.GONE);
            tabSettingsView.findViewById(R.id.tab_settings_additional).setVisibility(View.GONE);
        }

        final EditText editText = (EditText) tabSettingsView.findViewById(R.id.tabname);
        if (!newTab) editText.setText(tab.getTitle());

        int padding = Math.round(XGELSContext.getResources().getDimension(R.dimen.tab_menu_padding));
        tabSettingsDialog = new AlertDialog.Builder(Common.LAUNCHER_INSTANCE).create();
        tabSettingsDialog.setView(tabSettingsView, padding, padding, padding, padding);

        if (!newTab) {

            ImageView save = (ImageView) tabSettingsView.findViewById(R.id.tab_save_settings);
            Utils.setDrawableSelector(save);
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String newTabName = editText.getText().toString().trim();
                    if (newTabName.length() == 0) {
                        tabSettingsDialog.dismiss();
                        return;
                    }

                    if (!tab.getTitle().equals(newTabName)) {
                        renameTab(tab.getId(), newTabName);
                        Intent intent = getBaseIntent(false, tab.getId(), tab.getTitle());
                        Common.LAUNCHER_CONTEXT.startActivity(intent);
                    }

                    tabSettingsDialog.dismiss();
                }
            });

            final ImageView addfolder = (ImageView) tabSettingsView.findViewById(R.id.addfolder);
            if (tab.isAppsTab() || tab.isUserTab()) {
                addfolder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tabSettingsDialog.dismiss();
                        FolderHelper.getInstance().setupFolderSettings(null, tab.getId());
                    }
                });
                Utils.setDrawableSelector(addfolder);
            } else {
                addfolder.setVisibility(View.GONE);
            }

            final ImageView manageApps = (ImageView) tabSettingsView.findViewById(R.id.manageapps);
            if (!tab.isDynamicTab()) {
                manageApps.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tabSettingsDialog.dismiss();

                        if (tab.isUserTab()) {
                            Intent intent = getBaseIntent(true, tab.getId(), editText.getText().toString().trim());
                            intent.putStringArrayListExtra("items", tab.getRawData());
                            intent.putExtra("index", tab.getIndex());
                            intent.putExtra("contenttype", tab.getContentType().toString());
                            Common.LAUNCHER_CONTEXT.startActivity(intent);
                        } else {
                            if (tab.isWidgetsTab()) {
                                tabHost.setCurrentTab(0);
                                Intent startMain = new Intent(Intent.ACTION_MAIN);
                                startMain.addCategory(Intent.CATEGORY_HOME);
                                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                callMethod(Common.LAUNCHER_INSTANCE, "startActivity", startMain);
                            }

                            Intent intent = new Intent();
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                            intent.putExtra("mode", AllAppsList.MODE_PICK_APPS_TO_HIDE);
                            intent.putStringArrayListExtra("items", new ArrayList<String>(tab.isAppsTab() ? PreferencesHelper.hiddenApps : PreferencesHelper.hiddenWidgets));
                            intent.setComponent(new ComponentName(Common.PACKAGE_NAME, tab.isAppsTab() ? AllAppsList.class.getName() : AllWidgetsList.class.getName()));
                            Common.LAUNCHER_CONTEXT.startActivity(intent);
                        }
                    }
                });
                Utils.setDrawableSelector(manageApps);
            } else {
                manageApps.setVisibility(View.GONE);
            }

            final ImageView deleteTab = (ImageView) tabSettingsView.findViewById(R.id.deletetab);
            if (!tab.isAppsTab()) {
                deleteTab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(Common.LAUNCHER_CONTEXT, XGELSContext.getString(R.string.toast_tab_delete), Toast.LENGTH_LONG).show();
                    }
                });
                deleteTab.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        tabSettingsDialog.dismiss();
                        long itemid = tab.getId();

                        removeTab(tab);
                        ArrayList<Folder> folders = FolderHelper.getInstance().getFoldersForTab(tab.getId());
                        if (folders.size() != 0) {
                            FolderHelper.getInstance().removeFolders(folders);
                        }

                        Intent intent = getBaseIntent(false, itemid, null);
                        Common.LAUNCHER_CONTEXT.startActivity(intent);
                        return true;
                    }
                });
                Utils.setDrawableSelector(deleteTab);
            } else {
                deleteTab.setVisibility(View.GONE);
            }

            tabSettingsView.findViewById(R.id.tab_settings_divider).setBackgroundColor(tab.getPrimaryColor());

            final ImageView color = (ImageView) tabSettingsView.findViewById(R.id.tabcolor);
            color.setImageDrawable(setColorPreview());
            color.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = getBaseIntent(false, tab.getId(), tab.getTitle());
                    intent.putExtra("keep", true);
                    intent.putExtra("initcolor", tab.getPrimaryColor());
                    Common.LAUNCHER_CONTEXT.startActivity(intent);
                    tabSettingsDialog.cancel();
                }
            });

            final ImageView moveLeft = (ImageView) tabSettingsView.findViewById(R.id.movetableft);
            moveLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moveTab(tab, MOVE_LEFT);
                }
            });
            Utils.setDrawableSelector(moveLeft);

            final ImageView moveRight = (ImageView) tabSettingsView.findViewById(R.id.movetabright);
            moveRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moveTab(tab, MOVE_RIGHT);
                }
            });
            Utils.setDrawableSelector(moveRight);

            final CheckBox hideApps = (CheckBox) tabSettingsView.findViewById(R.id.tab_hide_apps);
            hideApps.setChecked(tab.hideFromAppsPage());
            if (tab.isCustomTab() && !(tab.isNewUpdatedTab() || tab.isNewAppsTab())) {
                hideApps.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        tab.setHideFromAppsPage(isChecked);
                        Intent intent = getBaseIntent(false, tab.getId(), tab.getTitle());
                        Common.LAUNCHER_CONTEXT.startActivity(intent);

                        Object mAppsCustomizePane = getObjectField(getTabHost(), Fields.acthAppsCustomizePane);
                        ArrayList allApps = (ArrayList) getObjectField(mAppsCustomizePane, Fields.acpvAllApps);
                        allApps.addAll(tab.getData());

                        callMethod(mAppsCustomizePane, Methods.acpvSetApps, allApps);
                        invalidate();
                    }
                });
            } else {
                if (tab.isWidgetsTab()) ((View) hideApps.getParent()).setVisibility(View.GONE);
                hideApps.setVisibility(View.GONE);
            }

            Spinner tabSort = (Spinner) tabSettingsView.findViewById(R.id.tabsort);
            String[] stringArray = XGELSContext.getResources().getStringArray(R.array.tabsort_values);
            final ArrayList<String> sortTypes = new ArrayList<String>(Arrays.asList(stringArray));
            tabSort.setSelection(sortTypes.indexOf(tab.getSortType().toString()));
            if (!(tab.isWidgetsTab() || tab.isNewAppsTab() || tab.isNewAppsTab())) {
                tabSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        SortType sortType = SortType.valueOf(sortTypes.get(position));
                        if (tab.getSortType().equals(sortType)) return;

                        tab.setSortType(sortType);
                        Object mAppsCustomizePane = getObjectField(tabHost, Fields.acthAppsCustomizePane);

                        if (tab.isAppsTab()) {
                            callMethod(mAppsCustomizePane, Methods.acpvSetApps, getObjectField(mAppsCustomizePane, Fields.acpvAllApps));
                        }

                        invalidate();

                        Intent intent = getBaseIntent(false, tab.getId(), null);
                        Common.LAUNCHER_CONTEXT.startActivity(intent);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            } else {
                tabSort.setVisibility(View.GONE);
            }
        } else {
            final Spinner spinner = (Spinner) tabSettingsView.findViewById(R.id.tabcontenttype);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position != 0) {
                        editText.setText(XGELSContext.getResources().getStringArray(R.array.tabcontent_names)[position]);
                    } else {
                        editText.setText("");
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            spinner.setVisibility(View.VISIBLE);

            tabSettingsView.findViewById(R.id.tab_save_settings).setVisibility(View.GONE);
            tabSettingsView.findViewById(R.id.tab_settings_divider).setVisibility(View.GONE);

            tabSettingsDialog.setButton(DialogInterface.BUTTON_POSITIVE, tabHost.getContext().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String newTabName = editText.getText().toString().trim();
                    if (newTabName.length() == 0) {
                        tabSettingsDialog.dismiss();
                        return;
                    }

                    long itemId = getNewTabId();

                    Spinner spinner = (Spinner) tabSettingsView.findViewById(R.id.tabcontenttype);
                    ContentType contentType = ContentType.valueOf(XGELSContext.getResources().getStringArray(R.array.tabcontent_values)[spinner.getSelectedItemPosition()]);
                    Intent intent = getBaseIntent(contentType == ContentType.User, itemId, newTabName);
                    int tabindex = tabHost.getTabWidget().getTabCount();

                    if (spinner.getSelectedItemPosition() != 0) {
                        addTab(new Tab("idx=" + tabindex
                                        + "|id=" + itemId
                                        + "|contenttype=" + contentType
                                        + "|title=" + newTabName
                                        + "|hide=" + false
                                        , true)
                        );
                    }

                    intent.putExtra("contenttype", contentType.toString());
                    intent.putExtra("new", true);
                    intent.putExtra("index", tabindex);

                    Common.LAUNCHER_CONTEXT.startActivity(intent);
                }
            });

            tabSettingsDialog.setButton(DialogInterface.BUTTON_NEGATIVE, tabHost.getContext().getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    tabSettingsDialog.dismiss();
                }
            });
        }

        tabSettingsDialog.show();
    }

    private Drawable setColorPreview() {
        Drawable canvas = XGELSContext.getResources().getDrawable(R.drawable.tabcolorpreview_canvas);
        canvas.setColorFilter(getCurrentTabData().getPrimaryColor(), PorterDuff.Mode.MULTIPLY);
        Drawable ring = XGELSContext.getResources().getDrawable(R.drawable.tabcolorpreview_ring);
        ring.setColorFilter(getCurrentTabData().getPrimaryColor(), PorterDuff.Mode.MULTIPLY);
        return new LayerDrawable(new Drawable[] {canvas, ring});
    }

    private Intent getBaseIntent(boolean openVisible, long itemid, String tabname) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        intent.setComponent(new ComponentName(Common.PACKAGE_NAME, openVisible ? AllAppsList.class.getName() : SaveActivity.class.getName()));
        intent.putExtra("mode", AllAppsList.MODE_MANAGE_TAB);
        intent.putExtra("itemid", itemid);
        intent.putExtra("name", tabname);
        ArrayList<String> data = new ArrayList<String>(tabs.size());
        for (Tab tab : tabs) {
            data.add(tab.toString());
        }
        intent.putExtra("tabsdata", data);
        return intent;
    }
}