package de.theknut.xposedgelsettings.hooks.appdrawer;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getFloatField;
import static de.robv.android.xposed.XposedHelpers.getIntField;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.setFloatField;
import static de.robv.android.xposed.XposedHelpers.setIntField;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;
import de.theknut.xposedgelsettings.hooks.general.OnPackagesUpdatedHook;

public class AppDrawerHooks {
	
	public static void initAllHooks(LoadPackageParam lpparam) {
			
		if (PreferencesHelper.iconSettingsSwitchApps) {
			// changing the appearence of the icons in the app drawer
			final Class<?> PagedViewIcon = findClass(Common.PAGED_VIEW_ICON, lpparam.classLoader);
			XposedBridge.hookAllMethods(PagedViewIcon, "applyFromApplicationInfo", new ApplyFromApplicationInfoHook());
			
//			if (!PreferencesHelper.appdrawerIconLabelShadow) {
//				XposedBridge.hookAllMethods(PagedViewIcon, "draw", new DrawHook());
//			}
		}
		
		if (PreferencesHelper.changeGridSizeApps) {
			final Class<?> DeviceProfileClass = findClass(Common.DEVICE_PROFILE, lpparam.classLoader);
			
			// modify app drawer grid
			XposedBridge.hookAllMethods(DeviceProfileClass, "updateFromConfiguration", new UpdateFromConfigurationHook());
		}
		
		if (Common.HOOKED_PACKAGE.equals(Common.TREBUCHET_PACKAGE)) {
			// set the background color of the app drawer
			final Class<?> AppsCustomizeLayoutClass = findClass(Common.APPS_CUSTOMIZE_LAYOUT, lpparam.classLoader);
			XposedBridge.hookAllConstructors(AppsCustomizeLayoutClass, new AppsCustomizeLayoutConstructorHook());
		}
		else {
			// set the background color of the app drawer
			final Class<?> AppsCustomizeTabHostClass = findClass(Common.APPS_CUSTOMIZE_TAB_HOST, lpparam.classLoader);
			XposedBridge.hookAllMethods(AppsCustomizeTabHostClass, "onTabChangedEnd", new OnTabChangedHook());
		}
		
		final Class<?> AppsCustomizePagedViewClass = findClass(Common.APPS_CUSTOMIZE_PAGED_VIEW, lpparam.classLoader);
		
		if (PreferencesHelper.continuousScroll) {
			// open app drawer on overscroll of last page
			XposedBridge.hookAllConstructors(AppsCustomizePagedViewClass, new AppsCustomizePagedViewConstructorHook());
			XposedBridge.hookAllMethods(AppsCustomizePagedViewClass, "overScroll", new OverScrollAppDrawerHook());
		}
		
		if (PreferencesHelper.closeAppdrawerAfterAppStarted) {
			XposedBridge.hookAllMethods(AppsCustomizePagedViewClass, "onClick", new OnClickHook());
		}
		
		final Class<?> AllAppsListClass = findClass(Common.ALL_APPS_LIST, lpparam.classLoader);
		// hiding apps from the app drawer
		XposedBridge.hookAllMethods(AllAppsListClass, "add", new AllAppsListAddHook());
		
		
//		final Class<?> CellLayoutClass = findClass(Common.CELL_LAYOUT, lpparam.classLoader);
//		XposedBridge.hookAllMethods(CellLayoutClass, "onSizeChanged", new XC_MethodHook() {
//			@Override
//			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//				XposedBridge.log("XGELS onSizeChanged ###############");
//				
//				XposedBridge.log("XGELS onSizeChanged width: " + param.args[0]);
//				XposedBridge.log("XGELS onSizeChanged height: " + param.args[1]);
//				XposedBridge.log("XGELS onSizeChanged oldwidth: " + param.args[2]);
//				XposedBridge.log("XGELS onSizeChanged oldheight: " + param.args[3]);
//				
//				Rect fg = (Rect) getObjectField(param.thisObject, "mForegroundRect");
//				Rect bg = (Rect) getObjectField(param.thisObject, "mBackgroundRect");
//				
//				XposedBridge.log("XGELS onSizeChanged mForegroundRect width: " + fg.width());
//				XposedBridge.log("XGELS onSizeChanged mForegroundRect height: " + fg.height());
//				XposedBridge.log("XGELS onSizeChanged mForegroundRect left: " + fg.left);
//				XposedBridge.log("XGELS onSizeChanged mForegroundRect right: " + fg.right);
//				XposedBridge.log("XGELS onSizeChanged mForegroundRect bottom: " + fg.bottom);
//				XposedBridge.log("XGELS onSizeChanged mForegroundRect top: " + fg.top);
//				
//				XposedBridge.log("XGELS onSizeChanged mBackgroundRect width: " + bg.width());
//				XposedBridge.log("XGELS onSizeChanged mBackgroundRect height: " + bg.height());
//				XposedBridge.log("XGELS onSizeChanged mBackgroundRect left: " + bg.left);
//				XposedBridge.log("XGELS onSizeChanged mBackgroundRect right: " + bg.right);
//				XposedBridge.log("XGELS onSizeChanged mBackgroundRect bottom: " + bg.bottom);
//				XposedBridge.log("XGELS onSizeChanged mBackgroundRect top: " + bg.top);
//			}
//		});
		
//		final Class<?> CellLayoutClass = findClass(Common.CELL_LAYOUT, lpparam.classLoader);
//		XposedBridge.hookAllMethods(CellLayoutClass, "onLayout", new XC_MethodHook() {
//			@Override
//			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//				XposedBridge.log("XGELS onLayout ############### " + param.args[0]);
//				
//				//if (!(Boolean) param.args[0]) return;
//				
//				ViewGroup grid = (ViewGroup) param.thisObject;
////				int offset = grid.getMeasuredWidth() - grid.getPaddingLeft() - grid.getPaddingRight() - (getIntField(param.thisObject, "mCountX") * getIntField(param.thisObject, "mCellWidth"));
////				XposedBridge.log("XGELS onLayout offset " + offset);
//				
//				grid.setPadding(0, 0, 0, 0);
//				XposedBridge.log("XGELS onLayout l " + param.args[1]);
//				XposedBridge.log("XGELS onLayout r " + param.args[3]);
//			}
//			
//			@Override
//			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
////				if (!(Boolean) param.args[0]) return;
////				ViewGroup grid = (ViewGroup) param.thisObject;
////				int count = (Integer) callMethod(param.thisObject, "getChildCount");
////				for (int i = 0; i < count; i++) {
////				    View child = (View) callMethod(param.thisObject, "getChildAt", i);
////				    child.layout(0, grid.getPaddingTop(), 10, 10);
////				}
//			}
//		});
//		
//		final Class<?> ShortcutAndWidgetContainerClass = findClass(Common.LAUNCHER3 + "ShortcutAndWidgetContainer", lpparam.classLoader);
//		XposedBridge.hookAllMethods(ShortcutAndWidgetContainerClass, "setCellDimensions", new XC_MethodHook() {
//			@Override
//			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//				XposedBridge.log("XGELS setCellDimensions ############### ");
//				
//				XposedBridge.log("XGELS setCellDimensions bottom " + param.args[2]);
//				XposedBridge.log("XGELS setCellDimensions right " + param.args[3]);
//				
//				
//			}
//			
////			@Override
////			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
////				if (!(Boolean) param.args[0]) return;
////				ViewGroup grid = (ViewGroup) param.thisObject;
////				int count = (Integer) callMethod(param.thisObject, "getChildCount");
////				for (int i = 0; i < count; i++) {
////				    View child = (View) callMethod(param.thisObject, "getChildAt", i);
////				    child.layout(0, grid.getPaddingTop(), 10, 10);
////				}
//			//}
//		});
//		
//		XposedBridge.hookAllMethods(AppsCustomizePagedViewClass, "updatePageCounts", new XC_MethodHook() {
//			@Override
//			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//				XposedBridge.log("XGELS updatePageCounts");
//				
//				if (Common.LAUNCHER_CONTEXT.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
//					setIntField(param.thisObject, "mCellCountY", 4);
//					setIntField(param.thisObject, "mCellCountX", 9);
//				} else {
//					 setIntField(param.thisObject, "mCellCountY", 7);
//					setIntField(param.thisObject, "mCellCountX", 4);
//				}
//			}
//		});
	}
}