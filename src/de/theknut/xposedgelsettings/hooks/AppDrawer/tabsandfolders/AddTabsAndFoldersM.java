package de.theknut.xposedgelsettings.hooks.appdrawer.tabsandfolders;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.HooksBaseClass;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Classes;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Fields;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Methods;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;
import de.theknut.xposedgelsettings.hooks.common.CommonHooks;
import de.theknut.xposedgelsettings.hooks.common.XGELSCallback;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.newInstance;
import static de.robv.android.xposed.XposedHelpers.setObjectField;

public class AddTabsAndFoldersM extends HooksBaseClass {

    public static void initAllHooks(final LoadPackageParam lpparam) {

        PreferencesHelper.moveTabHostBottom = false;
        findAndHookMethod(Classes.AllAppsContainerView, "onFinishInflate", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                TabHelperM.getInstance().init((LinearLayout) param.thisObject);
            }
        });

        findAndHookMethod(Classes.AlphabeticalAppsList, "getFiltersAppInfos", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (getObjectField(param.thisObject, "mSearchResults") != null) return;

                Tab tab = TabHelper.getInstance().getCurrentTabData();
                if (tab != null && tab.getData() != null) {
                    ArrayList data = new ArrayList(tab.getData());
                    ArrayList<Folder> foldersForTab = FolderHelper.getInstance().getFoldersForTab(tab.id);

                    if (foldersForTab != null) {
                        for (Folder folder : foldersForTab) {
                            Object appInfo = newInstance(Classes.AppInfo);
                            setObjectField(appInfo, "itemType", FolderM.FOLDER_ITEM_ID + Math.round(folder.getId()));
                            setObjectField(appInfo, "title", folder.getTitle());
                            data.add(0, appInfo);
                        }
                    }
                    param.setResult(data);
                }
            }
        });

        findAndHookMethod(Classes.BubbleTextView, "applyFromApplicationInfo", Classes.AppInfo, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                TextView iconName = (TextView) param.thisObject;

                if (PreferencesHelper.iconSettingsSwitchApps) {
                    if (PreferencesHelper.hideIconLabelApps) {
                        iconName.setTextColor(Color.TRANSPARENT);
                        return;
                    }

                    iconName.setTextColor(PreferencesHelper.appdrawerIconLabelColor);
                } else {
                    Tab tab = TabHelperM.getInstance().getCurrentTabData();
                    if (tab.getPrimaryColor() >= Tab.DEFAULT_COLOR || tab.getPrimaryColor() == Color.WHITE) {
                        iconName.setTextColor(Tab.DEFAULT_TEXT_COLOR);
                    } else {
                        iconName.setTextColor(tab.getContrastColor());
                    }
                }
            }
        });

        findAndHookMethod(Classes.AllAppsGridAdapter, "getItemViewType", Integer.TYPE, new XC_MethodHook() {

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                ArrayList adapterItems = (ArrayList) getObjectField(Common.ALPHABETICAL_APPS_LIST, "mAdapterItems");
                Object appInfo = getObjectField(adapterItems.get((Integer) param.args[0]), "appInfo");

                if (appInfo == null) return;

                Integer viewType = (Integer) getObjectField(appInfo, "itemType");
                if (viewType >= FolderM.FOLDER_ITEM_ID) {
                    param.setResult(viewType);
                }
            }
        });

        XposedBridge.hookAllMethods(Classes.AllAppsGridAdapter, "onCreateViewHolder", new XC_MethodHook() {

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                int viewType = (Integer) param.args[1];
                if (viewType >= FolderM.FOLDER_ITEM_ID) {
                    param.setResult(newInstance(
                            findClass(ObfuscationHelper.ClassNames.ALL_APPS_GRID_ADAPTER + "$ViewHolder", lpparam.classLoader),
                            FolderHelper.getInstance().getFolder(viewType - FolderM.FOLDER_ITEM_ID).makeFolderIcon((ViewGroup) param.args[0])
                    ));
                }
            }
        });

        XposedBridge.hookAllMethods(Classes.AllAppsGridAdapter, "onBindViewHolder", new XC_MethodHook() {
            Object mSearchResults = null;
            ArrayList adapterItems = null;

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (mSearchResults == null) {
                    mSearchResults = getObjectField(Common.ALPHABETICAL_APPS_LIST, "mSearchResults");
                    adapterItems = (ArrayList) getObjectField(Common.ALPHABETICAL_APPS_LIST, "mAdapterItems");
                }

                if (mSearchResults != null) return;

                Object adapterItem = adapterItems.get((Integer) param.args[1]);
                Object appInfo = getObjectField(adapterItem, "appInfo");
                if (appInfo == null) return;

                if ((Integer) getObjectField(appInfo, "itemType") >= FolderM.FOLDER_ITEM_ID) {
                    View folderIcon = (View) getObjectField(param.args[0], "mContent");

                    TextView folderName = (TextView) getObjectField(folderIcon, Fields.fiFolderName);
                    if (PreferencesHelper.iconSettingsSwitchApps) {
                        folderName.setTextColor(PreferencesHelper.hideIconLabelApps ? Color.TRANSPARENT : PreferencesHelper.appdrawerIconLabelColor);
                    } else {
                        Tab tab = TabHelperM.getInstance().getCurrentTabData();
                        if (tab.getPrimaryColor() >= Tab.DEFAULT_COLOR || tab.getPrimaryColor() == Color.WHITE) {
                            folderName.setTextColor(Tab.DEFAULT_TEXT_COLOR);
                        } else {
                            folderName.setTextColor(tab.getContrastColor());
                        }
                    }
                    
                    param.setResult(null);
                }
            }
        });

        findAndHookMethod(findClass(ObfuscationHelper.ClassNames.ALL_APPS_GRID_ADAPTER + "$GridSpanSizer", lpparam.classLoader), "getSpanSize", Integer.TYPE, new XC_MethodHook() {

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                ArrayList adapterItems = (ArrayList) getObjectField(Common.ALPHABETICAL_APPS_LIST, "mAdapterItems");
                Integer viewType = (Integer) getObjectField(adapterItems.get((Integer) param.args[0]), "viewType");
                if (viewType >= FolderM.FOLDER_ITEM_ID) param.setResult(1);
            }
        });

        XC_MethodHook closeHook = new XC_MethodHook() {

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if ((Boolean) callMethod(Common.LAUNCHER_INSTANCE, Methods.lIsAllAppsVisible)) {
                    Folder folder = FolderHelper.getInstance().findOpenFolder();
                    if (folder != null) {
                        folder.closeFolder();
                        param.setResult(null);
                    }
                }
            }
        };

        XposedBridge.hookAllMethods(Classes.Launcher, "onNewIntent", closeHook);
        XposedBridge.hookAllMethods(Classes.Launcher, "onBackPressed", closeHook);

        CommonHooks.OpenFolderListeners.add(new XGELSCallback() {
            @Override
            public void onBeforeHookedMethod(MethodHookParam param) throws Throwable {
                if (!(Boolean) callMethod(Common.LAUNCHER_INSTANCE, Methods.lIsAllAppsVisible))
                    return;

                Object folderIcon = param.args[0];
                View folder = (View) getObjectField(folderIcon, "mFolder");
                Object openFolder = Common.WORKSPACE_INSTANCE != null ? callMethod(Common.WORKSPACE_INSTANCE, "getOpenFolder") : null;
                if (openFolder != null && openFolder != folder) {
                    callMethod(Common.LAUNCHER_INSTANCE, Methods.lCloseFolder);
                }

                setObjectField(getObjectField(folder, "mInfo"), "opened", true);
                if (folder.getParent() == null) {
                    Common.DRAG_LAYER.addView(folder);
                    callMethod(getObjectField(Common.LAUNCHER_INSTANCE, "mDragController"), "addDropTarget", folder);
                } else {
                    Log.w("XGELS", "Opening folder (" + folder + ") which already has a parent (" +
                            folder.getParent() + ").");
                }
                callMethod(folder, "animateOpen");

                folder.sendAccessibilityEvent(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED);
                Common.DRAG_LAYER.sendAccessibilityEvent(AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED);
                param.setResult(null);
            }
        });

        if (true) return;

        findAndHookMethod(Classes.Launcher, Methods.lDispatchOnLauncherTransitionStart, View.class, boolean.class, boolean.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if ((Boolean) param.args[2]) {
                    TabHelperL.getInstance().hideTabBar();
                } else {
                    TabHelperL.getInstance().hideTabBar();
                }
            }
        });

        findAndHookMethod(Classes.Launcher, Methods.lDispatchOnLauncherTransitionEnd, View.class, boolean.class, boolean.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (!(Boolean) param.args[2]) {
                    TabHelperL.getInstance().showTabBar();
                }
            }
        });

        CommonHooks.GetCenterDeltaInScreenSpaceListener.add(new XGELSCallback() {
            @Override
            public void onAfterHookedMethod(MethodHookParam param) throws Throwable {
                if (getObjectField(Common.APP_DRAWER_INSTANCE, Fields.acpvContentType).toString().equals("Widgets")) {
                    return;
                }

                int color = PreferencesHelper.appdrawerFolderStyleBackgroundColor;
                if (PreferencesHelper.enableAppDrawerTabs) {
                    color = TabHelperL.getInstance().getCurrentTabData().getPrimaryColor();
                }
                ((View) param.args[0]).getBackground().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
            }
        });
    }
}