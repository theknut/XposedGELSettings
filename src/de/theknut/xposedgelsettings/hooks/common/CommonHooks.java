package de.theknut.xposedgelsettings.hooks.common;

import android.animation.TimeInterpolator;
import android.graphics.Canvas;
import android.view.View;

import java.util.ArrayList;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Classes;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Methods;

import static de.robv.android.xposed.XposedBridge.hookAllMethods;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

/**
 * Created by Alexander Schulz on 08.11.2014.
 */
public class CommonHooks {

    static public ArrayList<XGELSCallback> AddViewToCellLayoutListeners = new ArrayList<XGELSCallback>();
    static public ArrayList<XGELSCallback> SnapToPageListeners = new ArrayList<XGELSCallback>();
    static public ArrayList<XGELSCallback> LauncherOnResumeListeners = new ArrayList<XGELSCallback>();
    static public ArrayList<XGELSCallback> LauncherFinishBindingItems = new ArrayList<XGELSCallback>();
    static public ArrayList<XGELSCallback> LauncherOnPauseListeners = new ArrayList<XGELSCallback>();
    static public ArrayList<XGELSCallback> LauncherOnStartListeners = new ArrayList<XGELSCallback>();
    static public ArrayList<XGELSCallback> LauncherOnCreateListeners = new ArrayList<XGELSCallback>();
    static public ArrayList<XGELSCallback> OnLauncherTransitionEndListeners = new ArrayList<XGELSCallback>();
    static public ArrayList<XGELSCallback> PageBeginMovingListeners = new ArrayList<XGELSCallback>();
    static public ArrayList<XGELSCallback> PageEndMovingListeners = new ArrayList<XGELSCallback>();
    static public ArrayList<XGELSCallback> MoveToDefaultScreenListeners = new ArrayList<XGELSCallback>();
    static public ArrayList<XGELSCallback> OpenFolderListeners = new ArrayList<XGELSCallback>();
    static public ArrayList<XGELSCallback> AppsCustomizePagedViewOverScrollListeners = new ArrayList<XGELSCallback>();
    static public ArrayList<XGELSCallback> OnNowShowListeners = new ArrayList<XGELSCallback>();
    static public ArrayList<XGELSCallback> OnDragStartListeners = new ArrayList<XGELSCallback>();
    static public ArrayList<XGELSCallback> OnDragEndListeners = new ArrayList<XGELSCallback>();
    static public ArrayList<XGELSCallback> GetWorkspacePaddingListeners = new ArrayList<XGELSCallback>();
    static public ArrayList<XGELSCallback> FolderIconDispatchDrawListeners = new ArrayList<XGELSCallback>();
    static public ArrayList<XGELSCallback> DeviceProfileConstructorListeners = new ArrayList<XGELSCallback>();
    public static ArrayList<XGELSCallback> EnterOverviewModeListeners = new ArrayList<XGELSCallback>();
    public static ArrayList<XGELSCallback> GetCenterDeltaInScreenSpaceListener = new ArrayList<XGELSCallback>();

    public static void initAllHooks(LoadPackageParam lpparam) {

        if (SnapToPageListeners.size() != 0) {
            if (Common.PACKAGE_OBFUSCATED) {
                findAndHookMethod(Classes.PagedView, Methods.pvSnapToPage, Integer.TYPE, Integer.TYPE, Integer.TYPE, boolean.class, TimeInterpolator.class, new XGELSHook(SnapToPageListeners));
            } else {
                findAndHookMethod(Classes.PagedView, Methods.pvSnapToPage, Integer.TYPE, Integer.TYPE, Integer.TYPE, new XGELSHook(SnapToPageListeners));
            }
        }

        if (OnDragStartListeners.size() != 0) {
            if (Common.PACKAGE_OBFUSCATED) {
                // this is actually not DragSource but the parameter type is unknown as of now
                findAndHookMethod(Classes.SearchDropTargetBar, Methods.sdtbOnDragStart, Classes.DragSource, Object.class, new XGELSHook(OnDragStartListeners));
            } else {
                hookAllMethods(Classes.SearchDropTargetBar, Methods.sdtbOnDragStart, new XGELSHook(OnDragStartListeners));
            }
        }
        if (OnDragEndListeners.size() != 0) hookAllMethods(Classes.SearchDropTargetBar, Methods.sdtbOnDragEnd, new XGELSHook(OnDragEndListeners));

        if (AddViewToCellLayoutListeners.size() != 0) {
            findAndHookMethod(Classes.CellLayout, Methods.clAddViewToCellLayout, View.class, Integer.TYPE, Integer.TYPE, Classes.CellLayoutLayoutParams, boolean.class, new XGELSHook(AddViewToCellLayoutListeners));
        }
        if (LauncherOnResumeListeners.size() != 0) {
            findAndHookMethod(Classes.Launcher, "onResume", new XGELSHook(LauncherOnResumeListeners));
        }
        if (LauncherOnPauseListeners.size() != 0) {
            findAndHookMethod(Classes.Launcher, "onPause", new XGELSHook(LauncherOnPauseListeners));
        }
        if (LauncherOnStartListeners.size() != 0) {
            findAndHookMethod(Classes.Launcher, "onStart", new XGELSHook(LauncherOnStartListeners));
        }
        if (LauncherFinishBindingItems.size() != 0) {
            findAndHookMethod(Classes.Launcher, Methods.lFinishBindingItems, boolean.class, new XGELSHook(LauncherFinishBindingItems));
        }
        if (LauncherOnCreateListeners.size() != 0) {
            XposedBridge.hookAllMethods(Classes.Launcher, "onCreate", new XGELSHook(LauncherOnCreateListeners));
        }
        if (PageBeginMovingListeners.size() != 0) {
            hookAllMethods(Classes.PagedView, Methods.pvPageBeginMoving, new XGELSHook(PageBeginMovingListeners));
        }
        if (PageEndMovingListeners.size() != 0) {
            hookAllMethods(Classes.PagedView, Methods.pvPageEndMoving, new XGELSHook(PageEndMovingListeners));
        }
        if (MoveToDefaultScreenListeners.size() != 0) {
            findAndHookMethod(Classes.Workspace, Methods.wMoveToDefaultScreen, boolean.class, new XGELSHook(MoveToDefaultScreenListeners));
        }
        if (OnLauncherTransitionEndListeners.size() != 0) {
            findAndHookMethod(Classes.Workspace, Methods.wOnLauncherTransitionEnd, Classes.Launcher, boolean.class, boolean.class, new XGELSHook(OnLauncherTransitionEndListeners));
        }
        if (OpenFolderListeners.size() != 0) {
            findAndHookMethod(Classes.Launcher, Methods.lOpenFolder, Classes.FolderIcon, new XGELSHook(OpenFolderListeners));
        }
        if (AppsCustomizePagedViewOverScrollListeners.size() != 0) {
            findAndHookMethod(Classes.AppsCustomizePagedView, Methods.pvOverScroll, float.class, new XGELSHook(AppsCustomizePagedViewOverScrollListeners));
        }
        if (OnNowShowListeners.size() != 0) {
            findAndHookMethod(Classes.NowOverlay, Methods.noOnShow, boolean.class, boolean.class, new XGELSHook(OnNowShowListeners));
        }
        if (GetWorkspacePaddingListeners.size() != 0) {
            findAndHookMethod(Classes.DeviceProfile, Methods.dpGetWorkspacePadding, Integer.TYPE, new XGELSHook(GetWorkspacePaddingListeners));
        }
        if (FolderIconDispatchDrawListeners.size() != 0) {
            findAndHookMethod(Classes.FolderIcon, "dispatchDraw", Canvas.class, new XGELSHook(FolderIconDispatchDrawListeners));
        }
        if (DeviceProfileConstructorListeners.size() != 0) {
            XposedBridge.hookAllConstructors(Classes.DeviceProfile, new XGELSHook(DeviceProfileConstructorListeners));
        }
        if (EnterOverviewModeListeners.size() != 0) {
            XposedBridge.hookAllMethods(Classes.Workspace, Methods.wEnterOverviewMode, new XGELSHook(EnterOverviewModeListeners));
        }
        if (GetCenterDeltaInScreenSpaceListener.size() != 0) {
            findAndHookMethod(Classes.Utilities, Methods.uGetCenterDeltaInScreenSpace, View.class, View.class, int[].class, new XGELSHook(GetCenterDeltaInScreenSpaceListener));
        }
    }
}