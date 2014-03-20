package de.theknut.xposedgelsettings.hooks.systemui;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getBooleanField;
import static de.robv.android.xposed.XposedHelpers.getIntField;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.HooksBaseClass;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;

public class SystemUIHooks extends HooksBaseClass {
	
	static ActivityManager activityManager = null;
	
	public static void initAllHooks(LoadPackageParam lpparam) {
		
		if (!PreferencesHelper.hideClock && !PreferencesHelper.dynamicBackbutton && !PreferencesHelper.dynamicHomebutton) 
		{
			return;
		}
		
		final Class<?> PagedViewClass = findClass(Common.PAGED_VIEW, lpparam.classLoader);
		final Class<?> LauncherClass = findClass(Common.LAUNCHER, lpparam.classLoader);
		
		if ((PreferencesHelper.dynamicBackbutton || PreferencesHelper.dynamicIconBackbutton)) {
			XposedBridge.hookAllMethods(PagedViewClass, "snapToPage", new XC_MethodHook() {
				
				boolean gnow = true;
				
				@Override
				protected void afterHookedMethod(MethodHookParam param) throws Throwable {
					
	//				if (Common.NOW_OVERLAY_INSTANCE != null && Common.GEL_INSTANCE != null && !gnow) {
	//					gnow = getBooleanField(Common.GEL_INSTANCE, "mNowEnabled");
	//				}
					
					gnow = (Boolean) callMethod(Common.LAUNCHER_INSTANCE, "hasCustomContentToLeft");
					
					try {
						if (!((Boolean) callMethod(Common.LAUNCHER_INSTANCE, "isAllAppsVisible"))
							&& !getObjectField(Common.WORKSPACE_INSTANCE, "mState").toString().equals("OVERVIEW")
							&& getObjectField(param.thisObject, "mState").toString().equals("NORMAL")) {
							
							Intent myIntent = new Intent();
							
							if ((Integer) param.args[0] == (PreferencesHelper.defaultHomescreen - 1)) {
								myIntent.putExtra(Common.XGELS_ACTION, "ON_DEFAULT_HOMESCREEN");
								myIntent.setAction(Common.XGELS_INTENT);
								Common.LAUNCHER_CONTEXT.sendBroadcast(myIntent);
							}
							else if ((Integer) param.args[0] == 0 && gnow) {
								myIntent.putExtra(Common.XGELS_ACTION, "HOME_ORIG");
								myIntent.setAction(Common.XGELS_INTENT);
								Common.LAUNCHER_CONTEXT.sendBroadcast(myIntent);
							}
						}
					} catch (Throwable e) { }
				}
			});
			
			final Class<?> WorkspaceClass = findClass(Common.WORKSPACE, lpparam.classLoader);
			XposedBridge.hookAllMethods(WorkspaceClass, "onPageBeginMoving", new XC_MethodHook() {
				
				@Override
				protected void afterHookedMethod(MethodHookParam param) throws Throwable {
					log("onPageBeginMoving");
					if (!((Boolean) callMethod(Common.LAUNCHER_INSTANCE, "isAllAppsVisible"))
						&& !getObjectField(Common.WORKSPACE_INSTANCE, "mState").toString().equals("OVERVIEW")) {
						
						int currentPage = getIntField(param.thisObject, "mCurrentPage");
						
						Intent myIntent = new Intent();
						
						if (currentPage == (PreferencesHelper.defaultHomescreen - 1)) {
							myIntent.putExtra(Common.XGELS_ACTION, "HOME_ORIG");
							myIntent.setAction(Common.XGELS_INTENT);
							Common.LAUNCHER_CONTEXT.sendBroadcast(myIntent);
						}
					}
				}
			});
			
			XposedBridge.hookAllMethods(WorkspaceClass, "enterOverviewMode", new XC_MethodHook() {
				
				@Override
				protected void afterHookedMethod(MethodHookParam param) throws Throwable {
					log("enterOverviewMode");
					Intent myIntent = new Intent();
					myIntent.putExtra(Common.XGELS_ACTION, "HOME_ORIG");
					myIntent.setAction(Common.XGELS_INTENT);
					Common.LAUNCHER_CONTEXT.sendBroadcast(myIntent);
				}
			});
			
			XposedBridge.hookAllMethods(LauncherClass, "onPause", new XC_MethodHook() {
				
				@Override
				protected void afterHookedMethod(MethodHookParam param) throws Throwable {
					log("onPause");
					if (!getObjectField(Common.WORKSPACE_INSTANCE, "mState").toString().equals("OVERVIEW")) {
						Intent myIntent = new Intent();
						myIntent.putExtra(Common.XGELS_ACTION, "HOME_ORIG");						
						myIntent.setAction(Common.XGELS_INTENT);
						Common.LAUNCHER_CONTEXT.sendBroadcast(myIntent);
					}
				}
			});
			
			XposedBridge.hookAllMethods(LauncherClass, "onStop", new XC_MethodHook() {
				
				@Override
				protected void afterHookedMethod(MethodHookParam param) throws Throwable {
					log("onStop " + getBooleanField(param.thisObject, "mPaused"));
					if (!getObjectField(Common.WORKSPACE_INSTANCE, "mState").toString().equals("OVERVIEW")) {
						Intent myIntent = new Intent();
						myIntent.putExtra(Common.XGELS_ACTION, "HOME_ORIG");						
						myIntent.setAction(Common.XGELS_INTENT);
						Common.LAUNCHER_CONTEXT.sendBroadcast(myIntent);
					}
				}			
			});

			XposedBridge.hookAllMethods(LauncherClass, "onStart", new XC_MethodHook() {
				
				@Override
				protected void afterHookedMethod(MethodHookParam param) throws Throwable {
					boolean isDefaultHomescreen = getIntField(Common.WORKSPACE_INSTANCE, "mCurrentPage") == (PreferencesHelper.defaultHomescreen - 1);
					log("onStart");
					if (activityManager == null) {
						activityManager = (ActivityManager) Common.LAUNCHER_CONTEXT.getSystemService(Context.ACTIVITY_SERVICE);
					}
					
					List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
									
					if (!(Boolean) callMethod(param.thisObject, "isAllAppsVisible")
						&& isDefaultHomescreen
						&& Common.PACKAGE_NAMES.contains(appProcesses.get(0).processName.replace(":search", ""))
						&& !getObjectField(Common.WORKSPACE_INSTANCE, "mState").toString().equals("OVERVIEW")) {
						
						Intent myIntent = new Intent();
						myIntent.putExtra(Common.XGELS_ACTION, "ON_DEFAULT_HOMESCREEN");						
						myIntent.setAction(Common.XGELS_INTENT);
						Common.LAUNCHER_CONTEXT.sendBroadcast(myIntent);
					}				
				}
			});
			
			XposedBridge.hookAllMethods(LauncherClass, "onResume", new XC_MethodHook() {
				
				@Override
				protected void afterHookedMethod(MethodHookParam param) throws Throwable {
					log("onResume " + getIntField(Common.WORKSPACE_INSTANCE, "mCurrentPage"));
					
					if (activityManager == null) {
						activityManager = (ActivityManager) Common.LAUNCHER_CONTEXT.getSystemService(Context.ACTIVITY_SERVICE);
					}
					
					if (!((Boolean) callMethod(param.thisObject, "isAllAppsVisible"))
						&& !(Boolean) callMethod(Common.WORKSPACE_INSTANCE, "isOnOrMovingToCustomContent")
						&& !getObjectField(Common.WORKSPACE_INSTANCE, "mState").toString().equals("OVERVIEW")) {
						int currentPage = getIntField(Common.WORKSPACE_INSTANCE, "mCurrentPage");
						
						if (currentPage == (PreferencesHelper.defaultHomescreen - 1)) {
							
							Intent myIntent = new Intent();
							myIntent.putExtra(Common.XGELS_ACTION, "ON_DEFAULT_HOMESCREEN");						
							myIntent.setAction(Common.XGELS_INTENT);
							Common.LAUNCHER_CONTEXT.sendBroadcast(myIntent);
						}
					}
				}
			});
			
			XposedBridge.hookAllMethods(LauncherClass, "onWorkspaceShown", new XC_MethodHook() {
				
				@Override
				protected void afterHookedMethod(MethodHookParam param) throws Throwable {
					int currentPage = getIntField(Common.WORKSPACE_INSTANCE, "mCurrentPage");
					log("onWorkspaceShown");
					if (activityManager == null) {
						activityManager = (ActivityManager) Common.LAUNCHER_CONTEXT.getSystemService(Context.ACTIVITY_SERVICE);
					}
					
					List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
					
					log(getBooleanField(param.thisObject, "mPaused") + " onWorkspaceShown " + (PreferencesHelper.defaultHomescreen - 1) + " " + appProcesses.get(0).processName);
					
					if (currentPage == (PreferencesHelper.defaultHomescreen - 1)
						&& Common.PACKAGE_NAMES.contains(appProcesses.get(0).processName.replace(":search", ""))
						&& !getBooleanField(param.thisObject, "mPaused")) {	
						
						Intent myIntent = new Intent();
						myIntent.putExtra(Common.XGELS_ACTION, "ON_DEFAULT_HOMESCREEN");						
						myIntent.setAction(Common.XGELS_INTENT);
						Common.LAUNCHER_CONTEXT.sendBroadcast(myIntent);
					}
				}
			});
			
			XposedBridge.hookAllMethods(LauncherClass, "moveToCustomContentScreen", new XC_MethodHook() {
				
				@Override
				protected void afterHookedMethod(MethodHookParam param) throws Throwable {
					log("moveToCustomContentScreen");
					
					Intent myIntent = new Intent();
					myIntent.putExtra(Common.XGELS_ACTION, "HOME_ORIG");						
					myIntent.setAction(Common.XGELS_INTENT);
					Common.LAUNCHER_CONTEXT.sendBroadcast(myIntent);
				}
			});
		}
		
		if (PreferencesHelper.dynamicBackbutton) {
			
			if (PreferencesHelper.dynamicIconBackbutton) {
				XposedBridge.hookAllMethods(LauncherClass, "openFolder", new XC_MethodHook() {
					
					@Override
					protected void afterHookedMethod(MethodHookParam param) throws Throwable {
						log("openFolder");
						int currentPage = getIntField(Common.WORKSPACE_INSTANCE, "mCurrentPage");
						
						if (currentPage == (PreferencesHelper.defaultHomescreen - 1)) {					
							Intent myIntent = new Intent();
							myIntent.putExtra(Common.XGELS_ACTION, "BACK_ORIG");						
							myIntent.setAction(Common.XGELS_INTENT);
							Common.LAUNCHER_CONTEXT.sendBroadcast(myIntent);
						}
					}
				});
				
				XposedBridge.hookAllMethods(LauncherClass, "closeFolder", new XC_MethodHook() {
					
					@Override
					protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
						log("closeFolder");
						
						int currentPage = getIntField(Common.WORKSPACE_INSTANCE, "mCurrentPage");
						
						if (callMethod(Common.WORKSPACE_INSTANCE, "getOpenFolder") != null
							&& currentPage == (PreferencesHelper.defaultHomescreen - 1)) {
							Intent myIntent = new Intent();
							myIntent.putExtra(Common.XGELS_ACTION, "BACK_POWER_OFF");						
							myIntent.setAction(Common.XGELS_INTENT);
							Common.LAUNCHER_CONTEXT.sendBroadcast(myIntent);
						}
					}
				});
			}
			
			XposedBridge.hookAllMethods(LauncherClass, "onBackPressed", new XC_MethodHook() {
				
				@Override
				protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
					boolean isDefaultHomescreen = (getIntField(Common.WORKSPACE_INSTANCE, "mCurrentPage") == (PreferencesHelper.defaultHomescreen - 1))
							|| (getIntField(Common.WORKSPACE_INSTANCE, "mNextPage") == (PreferencesHelper.defaultHomescreen - 1));
					
					log("onBackPressed " + (PreferencesHelper.defaultHomescreen - 1) + " : " + isDefaultHomescreen);
					if (!((Boolean) callMethod(param.thisObject, "isAllAppsVisible"))
						&& callMethod(Common.WORKSPACE_INSTANCE, "getOpenFolder") == null
						&& (isDefaultHomescreen || PreferencesHelper.dynamicBackButtonOnEveryScreen)
						&& !getObjectField(Common.WORKSPACE_INSTANCE, "mState").toString().equals("OVERVIEW")) {
						Intent myIntent = new Intent();
						myIntent.putExtra(Common.XGELS_ACTION, "GO_TO_SLEEP");						
						myIntent.setAction(Common.XGELS_INTENT);
						Common.LAUNCHER_CONTEXT.sendBroadcast(myIntent);
					}
				}
			});
		}
		
		if (PreferencesHelper.dynamicHomebutton) {
			XposedBridge.hookAllMethods(LauncherClass, "onNewIntent", new XC_MethodHook() {
				
				@Override
				protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
					log("onNewIntent");
					if (Intent.ACTION_MAIN.equals(((Intent)param.args[0]).getAction())
						&& !((Boolean) callMethod(param.thisObject, "isAllAppsVisible"))) {
						int currentPage = getIntField(Common.WORKSPACE_INSTANCE, "mCurrentPage");
						
						if ((currentPage == (PreferencesHelper.defaultHomescreen - 1)
								|| getIntField(Common.WORKSPACE_INSTANCE, "mNextPage") == (PreferencesHelper.defaultHomescreen - 1))
							&& getBooleanField(param.thisObject, "mHasFocus")
							&& !getObjectField(Common.WORKSPACE_INSTANCE, "mState").toString().equals("OVERVIEW")) {
							
							callMethod(Common.LAUNCHER_INSTANCE, "showAllApps", true, Common.CONTENT_TYPE, true);
							
							Intent myIntent = new Intent();
							myIntent.putExtra(Common.XGELS_ACTION, "HOME_ORIG");						
							myIntent.setAction(Common.XGELS_INTENT);
							Common.LAUNCHER_CONTEXT.sendBroadcast(myIntent);
							
							param.setResult(null);
						}					
					}
				}
			});
		}
	}
}