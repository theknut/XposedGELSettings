package de.theknut.xposedgelsettings.hooks.appdrawer.tabsandfolders;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import de.theknut.xposedgelsettings.R;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Classes;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Fields;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Methods;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;
import de.theknut.xposedgelsettings.hooks.Utils;
import de.theknut.xposedgelsettings.ui.AllAppsList;
import de.theknut.xposedgelsettings.ui.AllWidgetsList;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.getIntField;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.newInstance;
import static de.robv.android.xposed.XposedHelpers.setIntField;

/**
 * Created by Alexander Schulz on 28.10.2014.
 */
public final class TabHelperNew extends TabHelper implements View.OnClickListener, View.OnLongClickListener {

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

    private static final TabHelperNew INSTANCE = new TabHelperNew();
    private static Context XGELSContext;
    public static final int MOVE_LEFT = -1;
    public static final int MOVE_RIGHT = 1;

    private FrameLayout tabHost;
    private RelativeLayout tabsContainer;
    private HorizontalScrollView hsv;
    private AlertDialog tabSettingsDialog;
    private ImageView addButton;
    private LayoutInflater inflater;

    int currentTabId;
    float inactiveTabTranslationX;

    boolean hasBeenInflated = false;

    public static TabHelperNew getInstance() {
        return INSTANCE;
    }

    public FrameLayout getTabHost() {
        return tabHost;
    }

    public void init(FrameLayout tabhost) {

        XGELSContext = Common.XGELSCONTEXT;
        inflater = LayoutInflater.from(XGELSContext);
        inactiveTabTranslationX = XGELSContext.getResources().getDimension(R.dimen.tabhost_overlap);

        this.tabHost = tabhost;

        if (!hasBeenInflated) {
            this.tabs = new ArrayList<>();
            initTabs();
        }

        addTabBar(PreferencesHelper.moveTabHostBottom);
        addTabs(false);

        showTabBar();

        int id = tabhost.getContext().getResources().getIdentifier("market_button", "id", Common.HOOKED_PACKAGE);
        tabhost.removeView(tabhost.findViewById(id));

        hasBeenInflated = true;
    }

    public void showTabBar() {
        if (PreferencesHelper.enableAppDrawerTabs) {
            hsv.setVisibility(View.VISIBLE);
            int startDelay = 1;

            Tab currTab = getCurrentTabData();
            animateShowTab(tabsContainer.findViewById(currTab.getLayoutId()), 0);

            for (int i = currTab.getIndex() - 1, j = currTab.getIndex(); i >= 0 || j < tabs.size(); i--, j++, startDelay++) {
                if (i >= 0) animateShowTab(tabsContainer.findViewById(tabs.get(i).getLayoutId()), startDelay);
                if (j < tabs.size()) animateShowTab(tabsContainer.findViewById(tabs.get(j).getLayoutId()), startDelay);
                else if (j == tabs.size()) animateShowTab(addButton, startDelay);
            }
        } else {
            hsv.setVisibility(View.GONE);
        }
    }

    private boolean animateShowTab(View view, int startDelayFactor) {
        view.setVisibility(View.VISIBLE);
        view.setTranslationY(view.getHeight() * 1.5f);
        view.animate()
                .translationY(0f)
                .setInterpolator(new AccelerateInterpolator())
                .setDuration(300)
                .setStartDelay(150 * startDelayFactor);
        return true;
    }

    public void hideTabBar() {
        hsv.setVisibility(PreferencesHelper.enableAppDrawerTabs ? View.INVISIBLE : View.GONE);
    }

    private void addTabBar(boolean alignBottom) {
        // this is freaking dirty but ain't nobody got time for that
        int layout = alignBottom ? R.layout.tab_host_bottom : R.layout.tab_host;
        RelativeLayout rl = (RelativeLayout) inflater.inflate(R.layout.tab_host, null, false);
        hsv = (HorizontalScrollView) rl.findViewById(R.id.horizontalScrollView);

        int statusbarHeight;
        int resourceId = XGELSContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusbarHeight = XGELSContext.getResources().getDimensionPixelSize(resourceId);
        } else {
            statusbarHeight = Utils.dpToPx(25);
        }
        rl.setPadding(0, statusbarHeight, 0, 0);

        tabsContainer = (RelativeLayout) rl.findViewById(R.id.tabscontainer);

        View contents = (View) getObjectField(tabHost, Fields.acthContent);
        tabHost.removeView(contents);
        ((ViewGroup) rl.findViewById(R.id.appdrawer_contents)).addView(contents);
        hsv.bringToFront();

        addButton = (ImageView) rl.findViewById(R.id.addbutton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPremium()) {
                    setupTabSettings(null);
                } else {
                    Utils.showPremiumOnly();
                }
            }
        });
        addButton.setTranslationY(hsv.getHeight() * 1.5f);

        tabHost.addView(rl);
    }

    public void initTabs() {
        if (PreferencesHelper.enableAppDrawerTabs) {
            for (String item : PreferencesHelper.appdrawerTabData) {
                tabs.add(new Tab(item, false));
            }
        }

        boolean hasApps = false;
        for (Tab tab : tabs) {
            if (tab.isAppsTab()) {
                hasApps = true;
            }
        }

        if (!hasApps) {
            String appsTabName;
            int id = Common.LAUNCHER_CONTEXT.getResources().getIdentifier("all_apps_button_label", "string", Common.HOOKED_PACKAGE);
            if (id != 0) {
                appsTabName = Common.LAUNCHER_CONTEXT.getResources().getString(id);
            } else {
                appsTabName = "Apps";
            }

            Tab apps = new Tab("idx=" + 0 + "|id=" + Tab.APPS_ID + "|contenttype=" + ContentType.Applications + "|title=" + appsTabName + "|hide=" + false, false);
            if (!PreferencesHelper.enableAppDrawerTabs) {
                apps.setColor(PreferencesHelper.appdrawerFolderStyleBackgroundColor);
            }
            tabs.add(0, apps);
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

        currentTabId = tabs.get(0).getLayoutId();
        organizeTabs();
    }

    public void addTab(Tab tab) {
        tabs.add(tab);
        addTabInternal(tab, true);
        Toast.makeText(tabHost.getContext(), XGELSContext.getString(R.string.toast_appdrawer_tabadded_title), Toast.LENGTH_LONG).show();
        Toast.makeText(tabHost.getContext(), XGELSContext.getString(R.string.toast_appdrawer_tabadded_title), Toast.LENGTH_LONG).show();
    }

    private void addTabInternal(final Tab tab, boolean focus) {
        if (DEBUG) log("Add tab " + tab.toString());

        TextView tabView = (TextView) inflater.inflate(R.layout.tab_layout, null, false);
        tabView.setId(tab.getLayoutId());
        tabView.setText(tab.getTitle());
        tabView.setTag(tab);
        tabView.setContentDescription(tab.getTitle());
        tabView.setOnLongClickListener(this);
        tabView.setOnClickListener(this);

        if (false) {// && PreferencesHelper.moveTabHostBottom) {
            tabView.setBackground(tabView.getContext().getResources().getDrawable(R.drawable.tab_indicator_background_bottom));
        }

        if (Common.PACKAGE_OBFUSCATED && Common.GNL_VERSION >= ObfuscationHelper.GNL_4_0_26
                && Color.alpha(tab.getPrimaryColor()) == 0) {

            tabView.setBackground(tabView.getContext().getResources().getDrawable(R.drawable.tab_indicator));
        }

        tabView.getBackground().setColorFilter(tab.getPrimaryColor(), PorterDuff.Mode.MULTIPLY);
        tabView.setTextColor(tab.getContrastColor());
        tabView.setTranslationY(hsv.getHeight() * 1.5f);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        if (tab.getIndex() != 0) {
            params.addRule(RelativeLayout.RIGHT_OF, tabs.get(tab.getIndex() - 1).getLayoutId());
            params.setMargins(-Utils.dpToPx(25), 0, 0, 0);
            tabView.setLayoutParams(params);
            hsv.requestLayout();
        }
        tabsContainer.addView(tabView);

        if (focus) {
            setCurrentTab(tab);
            scroll();
            animateShowTab(tabView, 0);
        }
    }

    @Override
    public void removeTab(Tab tab) {
        tabsContainer.removeAllViews();

        if (tabs.contains(tab)) {
            tabs.remove(tab);
            onTabsDataChanged();
        }

        addTabs(false);

        int newId = tab.getIndex() - 1;
        setCurrentTab(newId < 0 ? tabs.get(0) : tabs.get(newId));

        if (tab.hideFromAppsPage()) {
            ArrayList allApps = FolderHelper.getInstance().getAllApps();
            allApps.addAll(tab.getData());
            callMethod(Common.APP_DRAWER_INSTANCE, Methods.acpvSetApps, allApps);
        }

        scroll();
    }

    public void renameTab(long tabId, String tabName) {
        TextView tabView = (TextView) tabsContainer.findViewById((int) tabId);
        if (tabView == null) return;

        ((Tab) tabView.getTag()).setTitle(tabName);
        tabView.setText(((Tab) tabView.getTag()).getTitle());
    }

    public void moveTab(Tab tabToMove, int direction) {
        int newIdx = tabToMove.getIndex() + direction;
        if (newIdx == -1 || newIdx == tabsContainer.getChildCount()) return;

        Iterator<Tab> it = tabs.iterator();
        while(it.hasNext()) {
            Tab tab = it.next();
            if (tab.getIndex() == newIdx) {
                tab.setIndex(tabToMove.getIndex());
                tabToMove.setIndex(newIdx);
            }
        }

        onTabsDataChanged();

        tabsContainer.removeAllViews();

        addTabs(false);
        setCurrentTab(tabToMove.getLayoutId());

        Intent intent = getBaseIntent(false, tabToMove.getId(), null);
        Common.LAUNCHER_CONTEXT.startActivity(intent);
    }
//
//    public boolean setContentTypeImmediate(Object contentType) {
//        if (contentType.toString().equals("Widgets")) {
//            for (Tab tab : tabs) {
//                if (tab.isWidgetsTab()) {
//                    setCurrentTab(tab.getIndex());
//                    return true;
//                }
//            }
//
//            hideTabBar();
//            tmpWidgetTab = new Tab("idx=" + tabHost.getTabWidget().getTabCount() + "|id=" + Tab.WIDGETS_ID + "|contenttype=" + ContentType.Widgets + "|title=" + "Widgets", false);
//            addTabInternal(tmpWidgetTab, true);
//            return true;
//        } else {
//            if (!PreferencesHelper.appdrawerRememberLastPosition) {
//                setCurrentTab(getTabById(Tab.APPS_ID).getIndex());
//            }
//        }
//
//        if (tmpWidgetTab != null) {
//            removeTab(tmpWidgetTab);
//        }
//
//        showTabBar();
//        invalidate();
//        return false;
//    }

    @Override
    public boolean handleScroll(float overscroll) {
        Tab tab = getCurrentTabData();
        if (overscroll > 100.0) {
            int newIdx = tab.getIndex() + 1;
            setCurrentTab(newIdx >= tabs.size() ? tabs.get(0) : tabs.get(newIdx));
            return true;
        } else if (overscroll < -100.0) {
            int newIdx = tab.getIndex() - 1;
            setCurrentTab(newIdx < 0 ? tabs.get(tabs.size() - 1) : tabs.get(newIdx));
            return true;
        }
        return false;
    }

    @Override
    public void invalidate() {
        setTabColor(getCurrentTabData().getPrimaryColor());
        callMethod(Common.APP_DRAWER_INSTANCE, Methods.acpvInvalidatePageData, PreferencesHelper.appdrawerRememberLastPosition ? Common.APPDRAWER_LAST_PAGE_POSITION : 0, true);
    }

    @Override
    public void setTabColor(int color) {
        getCurrentTabData().setColor(color);
        tabsContainer.findViewById(currentTabId).getBackground().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        ((TextView) tabsContainer.findViewById(currentTabId)).setTextColor(Utils.getContrastColor(color));
        addButton.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
    }

    public void setCurrentTab(Tab tab) {
        setContentType(tab);
        setCurrentTab(tab.getLayoutId());
    }

    public void setCurrentTab(int layoutId) {
        setCurrentTab(layoutId, false);
    }

    public void setCurrentTab(int layoutId, boolean onlySetInternal) {
        currentTabId = layoutId;

        tabsContainer.findViewById(layoutId).bringToFront();
        setTabColor(tabs.get(((Tab) tabsContainer.findViewById(layoutId).getTag()).getIndex()).getPrimaryColor());

        if (onlySetInternal) {
            callMethod(Common.APP_DRAWER_INSTANCE, Methods.acpvInvalidatePageData, 0, false);
            organizeTabs();
            scroll();
            return;
        }

        hsv.bringToFront();
        callMethod(Common.APP_DRAWER_INSTANCE, Methods.acpvSyncPages);
        callMethod(Common.APP_DRAWER_INSTANCE, Methods.acpvInvalidatePageData, 0, false);

        organizeTabs();
        scroll();
        hsv.setTranslationX(0);
    }

    @Override
    public void saveTabData() {
        Intent intent = getBaseIntent(false, 0, null);
        Common.LAUNCHER_CONTEXT.startActivity(intent);
    }

    private void onTabsDataChanged() {
        organizeTabs();
        syncIndexes();
    }

    private void organizeTabs() {
        Tab currTab = null;
        for (Tab tab : tabs) {
            if (tab.getLayoutId() == currentTabId) {
                currTab = tab;
                break;
            }
        }

        if (currTab != null) {
            for (int i = 0; i < currTab.getIndex(); i++) {
                Tab tab = tabs.get(i);
                View tabView = tabsContainer.findViewById(tab.getLayoutId());
                tabView.setTranslationY(-inactiveTabTranslationX);
                tabView.setScaleX(0.95f);
                tabView.setScaleY(0.95f);
                tabView.getBackground().setColorFilter(tab.getSecondaryColor(), PorterDuff.Mode.MULTIPLY);
                tabView.bringToFront();
            }

            for (int i = tabs.size() - 1; i > currTab.getIndex(); i--) {
                Tab tab = tabs.get(i);
                View tabView = tabsContainer.findViewById(tab.getLayoutId());
                tabView.setTranslationY(-inactiveTabTranslationX);
                tabView.setScaleX(0.95f);
                tabView.setScaleY(0.95f);
                tabView.getBackground().setColorFilter(tab.getSecondaryColor(), PorterDuff.Mode.MULTIPLY);
                tabView.bringToFront();
            }

            View tabView = tabsContainer.findViewById(currTab.getLayoutId());
            tabView.setTranslationY(0f);
            tabView.setScaleX(1f);
            tabView.setScaleY(1f);
            tabView.getBackground().setColorFilter(currTab.getPrimaryColor(), PorterDuff.Mode.MULTIPLY);
            tabView.bringToFront();
        }
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

    public void scroll() {
        if (tabsContainer == null) return;

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                View tab = tabsContainer.findViewById(currentTabId);
                if (tab != null) {
                    hsv.smoothScrollTo((tab.getLeft() + tab.getWidth() / 2) - (hsv.getWidth() / 2), 0);
                }
            }
        });
    }

//    public void showProgressBar() {
//        if (!TabHelper.getInstance().isProgressBarVisible()) {
//            Common.LAUNCHER_INSTANCE.runOnUiThread(
//                    new Runnable() {
//                        @Override
//                        public void run() {
//                            progressBar.setVisibility(View.VISIBLE);
//                            progressBar.bringToFront();
//                        }
//                    });
//        }
//    }
//
//    public void hideProgressBar() {
//        Common.LAUNCHER_INSTANCE.runOnUiThread(
//                new Runnable() {
//                    @Override
//                    public void run() {
//                        progressBar.setVisibility(View.GONE);
//                    }
//                });
//    }
//
//    public boolean isProgressBarVisible() {
//        return progressBar.getVisibility() == View.VISIBLE;
//    }

    @Override
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
        Tab tab = (Tab) v.getTag();
        setCurrentTab(tab);
    }

    private void setContentType(Tab tab) {
        Object contentType;
        if (tab.isWidgetsTab()) {
            contentType = callMethod(tabHost, Methods.acthGetContentTypeForTabTag, "WIDGETS");
        } else {
            contentType = callMethod(tabHost, Methods.acthGetContentTypeForTabTag, "APPS");
        }
        callMethod(Common.APP_DRAWER_INSTANCE, Methods.acpvSetContentType, contentType);
    }

    @Override
    public boolean onLongClick(View v) {
        if (checkPremium()) {
            Tab tab = (Tab) v.getTag();
            setCurrentTab(tab);
            setupTabSettings(tab);
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
            return numAppPages;
        }

        return -1;
    }

    @Override
    public Tab getCurrentTabData() {
        return (Tab) tabsContainer.findViewById(currentTabId).getTag();
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
        } else if (curTab.isAppsTab()) {
            syncAppsPageItems(thisObject, new ArrayList(FolderHelper.getInstance().getAllApps()), page);
            return true;
        }

        if (curTab.isAppsTab()) {
            ViewGroup appsCustomizeCellLayout = (ViewGroup) callMethod(thisObject, Methods.pvGetPageAt, page);
            appsCustomizeCellLayout.getBackground().setColorFilter(curTab.getPrimaryColor(), PorterDuff.Mode.MULTIPLY);
        }

        return false;
    }

    private void syncAppsPageItems(Object thisObject, ArrayList apps, int page) {
        final boolean isRtl = (Boolean) callMethod(thisObject, Methods.pvIsLayoutRtl);
        Tab currTab = getCurrentTabData();
        LayoutInflater mLayoutInflater = (LayoutInflater) getObjectField(thisObject, "mLayoutInflater");

        int mCellCountX = getIntField(thisObject, Fields.acpvCellCountX);
        int mCellCountY = getIntField(thisObject, Fields.acpvCellCountY);
        int apps_customize_application = Common.LAUNCHER_CONTEXT.getResources().getIdentifier("apps_customize_application", "layout", Common.HOOKED_PACKAGE);

        int numCells = mCellCountX * mCellCountY;
        int startIndex = page * numCells;
        int endIndex = Math.min(startIndex + numCells, apps.size());
        ViewGroup appsCustomizeCellLayout = (ViewGroup) callMethod(thisObject, Methods.pvGetPageAt, page);
        appsCustomizeCellLayout.getBackground().setColorFilter(currTab.getPrimaryColor(), PorterDuff.Mode.MULTIPLY);

        callMethod(appsCustomizeCellLayout, Methods.acpvRemoveAllViewsOnPage);
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
                callMethod(icon, Methods.btvApplyFromApplicationInfo, info);
                icon.setOnClickListener((View.OnClickListener) Common.LAUNCHER_INSTANCE);
                icon.setOnLongClickListener((View.OnLongClickListener) thisObject);
                icon.setOnTouchListener((View.OnTouchListener) thisObject);
                icon.setOnKeyListener((View.OnKeyListener) thisObject);
                icon.setOnFocusChangeListener((View.OnFocusChangeListener) getObjectField(appsCustomizeCellLayout, Fields.acclFocusHandlerView));
            }

            callMethod(appsCustomizeCellLayout, Methods.clAddViewToCellLayout, icon, -1, i, newInstance(Classes.CellLayoutLayoutParams, x, y, 1, 1), false);

            if (!PreferencesHelper.hideIconLabelApps) {
                TextView iconName;
                if (!(info instanceof Folder)) {
                    iconName = (TextView) icon;
                } else {
                    iconName = (TextView) getObjectField(icon, Fields.fiFolderName);
                }

                if (PreferencesHelper.iconSettingsSwitchApps) {
                    iconName.setTextColor(PreferencesHelper.appdrawerIconLabelColor);
                } else {
                    iconName.setTextColor(currTab.getContrastColor());
                }
            }
        }

        callMethod(thisObject, Methods.acpvEnableHwLayersOnVisiblePages);
    }
//
//    public void setContentType(Object thisObject) {
//        Tab tab = getCurrentTabData();
//        setObjectField(thisObject, Fields.acpvContentType, callMethod(tabHost, Methods.acthGetContentTypeForTabTag, tab.isWidgetsTab() ? "WIDGETS" : "APPS"));
//        setTabColor(getCurrentTabData().getPrimaryColor());
//        Object mAppsCustomizePane = getObjectField(tabHost, Fields.acthAppsCustomizePane);
//        callMethod(mAppsCustomizePane, Methods.acpvInvalidatePageData, 0, true);
//        scroll();
//    }

    @Override
    public ArrayList<String> getAppsToHide() {
        ArrayList apps = new ArrayList();
        if (Common.IS_TREBUCHET) return apps;

        for (Tab tab : tabs) {
            if (tab.hideFromAppsPage()) {
                apps.addAll(tab.getRawData());
            }
        }
        return apps;
    }
//
//    public boolean isTabSettingsOpen() {
//        if (tabSettingsDialog != null) {
//            return tabSettingsDialog.isShowing();
//        }
//        return false;
//    }
//
//    public void closeTabSettings() {
//        tabSettingsDialog.cancel();
//    }
//
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
                        renameTab(tab.getLayoutId(), newTabName);
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
                                //setCurrentTab(tab);
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

                        ArrayList allApps = (ArrayList) getObjectField(Common.APP_DRAWER_INSTANCE, Fields.acpvAllApps);
                        if (!isChecked) {
                            allApps.addAll(tab.getData());
                        }

                        callMethod(Common.APP_DRAWER_INSTANCE, Methods.acpvSetApps, allApps);
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
                        AppDrawerItem.SortType sortType = AppDrawerItem.SortType.valueOf(sortTypes.get(position));
                        if (tab.getSortType().equals(sortType)) return;

                        tab.setSortType(sortType);
                        if (tab.isAppsTab()) {
                            callMethod(Common.APP_DRAWER_INSTANCE, Methods.acpvSetApps, getObjectField(Common.APP_DRAWER_INSTANCE, Fields.acpvAllApps));
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
                    int tabindex = tabsContainer.getChildCount();

                    int color = Tab.DEFAULT_COLOR;
                    if (spinner.getSelectedItemPosition() != 0) {
                        if (spinner.getSelectedItemPosition() == 1) {
                            color = Color.parseColor("#263238"); // Blue Grey 900
                        } else {
                            color = Utils.getRandomColor();
                        }

                        addTab(new Tab("idx=" + tabindex
                                        + "|id=" + itemId
                                        + "|contenttype=" + contentType
                                        + "|title=" + newTabName
                                        + "|hide=" + false
                                        + "|color=" + color
                                        , true)
                        );
                    }

                    Intent intent = getBaseIntent(contentType == ContentType.User, itemId, newTabName);
                    intent.putExtra("contenttype", contentType.toString());
                    intent.putExtra("new", true);
                    intent.putExtra("index", tabindex);
                    if (newTab) {
                        intent.putExtra("color", color);
                    }
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

    public void handleOverscroll(int overscroll) {
        if (Common.APP_DRAWER_PAGE_SWITCHED) return;

        if (overscroll > 0.0) {
            overscroll -= (overscroll / ((View) Common.APP_DRAWER_INSTANCE).getWidth()) * ((View) Common.APP_DRAWER_INSTANCE).getWidth();
        }
        hsv.setTranslationX(-overscroll);
    }
}