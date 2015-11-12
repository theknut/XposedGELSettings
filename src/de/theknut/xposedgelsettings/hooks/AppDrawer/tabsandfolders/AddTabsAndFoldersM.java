package de.theknut.xposedgelsettings.hooks.appdrawer.tabsandfolders;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.HooksBaseClass;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Classes;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Fields;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Methods;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;
import de.theknut.xposedgelsettings.hooks.common.CommonHooks;
import de.theknut.xposedgelsettings.hooks.common.XGELSCallback;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.getAdditionalInstanceField;
import static de.robv.android.xposed.XposedHelpers.getLongField;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.newInstance;
import static de.robv.android.xposed.XposedHelpers.setAdditionalInstanceField;
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
                            log("Create " + folder.getTitle());
                            Object appInfo = newInstance(Classes.AppInfo);
                            setObjectField(appInfo, "itemType", 0xF01DE5);
                            setObjectField(appInfo, "title", folder.getTitle());
                            setAdditionalInstanceField(appInfo, "xgelsfolder", folder);
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

        XposedBridge.hookAllMethods(Classes.AllAppsGridAdapter, "onBindViewHolder", new XC_MethodHook() {
            Object mSearchResults = null;
            ArrayList adapterItems = null;

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (mSearchResults == null) {
                    mSearchResults = getObjectField(getObjectField(param.thisObject, "mApps"), "mSearchResults");
                    adapterItems = (ArrayList) getObjectField(getObjectField(param.thisObject, "mApps"), "mAdapterItems");
                }

                if (mSearchResults != null) return;

                Object adapterItem = adapterItems.get((Integer) param.args[1]);
                if ((Integer) getObjectField(adapterItem, "viewType") == 1) {
                    FolderM xgelsfolder = (FolderM) getAdditionalInstanceField(getObjectField(adapterItem, "appInfo"), "xgelsfolder");
                    if (xgelsfolder != null) {
                        log("Replace viewHolder for " + xgelsfolder.getTitle());
                        setObjectField(param.args[0], "mContent", xgelsfolder.makeFolderIcon((ViewGroup) Common.APP_DRAWER_INSTANCE));
                        param.setResult(null);
                    }
                }
            }
        });
/*
        XposedBridge.hookAllMethods(Classes.FolderIcon, "dispatchDraw", new XC_MethodHook() {

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                log("Folder " + getObjectField(param.thisObject, "mFolder"));
                log("getItemCount " + callMethod(getObjectField(getObjectField(param.thisObject, "mFolder"), "mContent"), "getItemCount"));
                log("mAnimating " + getObjectField(param.thisObject, "mAnimating"));
                log("Parent " + ((View) param.thisObject).getParent());
            }
        });*/

        if (true) return;
        findAndHookMethod(Classes.Launcher, "startWorkspaceStateChangeAnimation", Classes.WorkspaceState, Integer.TYPE, boolean.class, HashMap.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                HashMap layerViews = (HashMap) param.args[3];
                HorizontalScrollView hsv = TabHelperM.getInstance().getTabHost();
                hsv.setVisibility(View.VISIBLE);
                hsv.setAlpha(0.0F);

                int[] space = (int[]) callStaticMethod(Classes.Utilities, "getCenterDeltaInScreenSpace", getObjectField(Common.APP_DRAWER_INSTANCE, "mRevealView"), getObjectField(Common.LAUNCHER_INSTANCE, "mAllAppsButton"), null);
                hsv.setTranslationY(space[1]);

                layerViews.put(hsv, 1);
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

        findAndHookMethod(Classes.Workspace, Methods.wGetScreenWithId, long.class, new XC_MethodHook() {

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if ((Long) param.args[0] == -1) {
                    param.setResult(callMethod(Common.APP_DRAWER_INSTANCE, Methods.pvGetPageAt, 0));
                }
            }
        });

        findAndHookMethod(Classes.Workspace, Methods.wGetViewForTag, Object.class, new XC_MethodHook() {

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if(getLongField(param.args[0], Fields.iiScreenId) == Folder.FOLDER_ID) {
                    Folder folder = FolderHelper.getInstance().findOpenFolder();
                    if (folder != null) {
                        param.setResult(folder.getFolderIcon());
                    }
                }
            }
        });

        findAndHookMethod(Classes.Workspace, Methods.wGetFolderForTag, Object.class, new XC_MethodHook() {

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (getLongField(param.args[0], Fields.iiScreenId) == Folder.FOLDER_ID) {
                    Folder folder = FolderHelper.getInstance().findOpenFolder();
                    if (folder != null) {
                        param.setResult(getObjectField(folder.getFolderIcon(), Fields.fiFolder));
                    }
                }
            }
        });

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