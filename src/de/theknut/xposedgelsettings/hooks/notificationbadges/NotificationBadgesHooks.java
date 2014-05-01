package de.theknut.xposedgelsettings.hooks.notificationbadges;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getLongField;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.view.View;
import android.widget.TextView;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;

public class NotificationBadgesHooks extends NotificationBadgesHelper {
	
	public static void initAllHooks(LoadPackageParam lpparam) {
		
		if (!PreferencesHelper.enableBadges) return;
		
		final Class<?> FolderClass = findClass(Common.FOLDER, lpparam.classLoader);
		final Class<?> FolderIconClass = findClass(Common.LAUNCHER3 + "FolderIcon", lpparam.classLoader);
		final Class<?> LauncherClass = findClass(Common.LAUNCHER, lpparam.classLoader);
		final Class<?> CellLayoutClass = findClass(Common.CELL_LAYOUT, lpparam.classLoader);
		final Class<?> WorkspaceClass = findClass(Common.WORKSPACE, lpparam.classLoader);
		
		XposedBridge.hookAllMethods(LauncherClass, "onCreate", new XC_MethodHook() {
			
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				
				final Context mContext = (Context) callMethod(Common.LAUNCHER_INSTANCE, "getApplicationContext");
				
		    	IntentFilter intentFilter = new IntentFilter();
		    	intentFilter.addAction(Intent.ACTION_BOOT_COMPLETED);
		        intentFilter.addAction(Common.MISSEDIT_COUNTERS_STATUS);
		        intentFilter.addAction(Common.MISSEDIT_CALL_NOTIFICATION);
		        intentFilter.addAction(Common.MISSEDIT_SMS_NOTIFICATION);
		        intentFilter.addAction(Common.MISSEDIT_APP_NOTIFICATION);
		        intentFilter.addAction(Common.MISSEDIT_GMAIL_NOTIFICATION);
		        
		        mContext.registerReceiver(notificationReceiver, intentFilter);
			}
		});
		
		XposedBridge.hookAllMethods(LauncherClass, "onDestroy", new XC_MethodHook() {
			
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				
				Common.LAUNCHER_CONTEXT.unregisterReceiver(notificationReceiver);
			}
		});
		
		XC_MethodHook requestCountersHook = new XC_MethodHook() {
			
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {	
				if (DEBUG) log(param, "Request Counters");
				requestCounters();
			}			
		};
		
		XposedBridge.hookAllMethods(WorkspaceClass, "onDragEnd", requestCountersHook);
		XposedBridge.hookAllMethods(LauncherClass, "openFolder", requestCountersHook);
		XposedBridge.hookAllMethods(LauncherClass, "finishBindingItems", requestCountersHook);
		
		XposedBridge.hookAllMethods(WorkspaceClass, "onLauncherTransitionEnd", new XC_MethodHook() {
			
			int TOWORKSPACE = 2;
			
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {	
				
				if ((Boolean) param.args[TOWORKSPACE]) {				
					if (DEBUG) log(param, "Transitioning to Workspace - do nothing");
				} else {
					if (DEBUG) log(param, "Transitioning to All Apps - Request Counters");
					requestCounters();
				}
			}			
		});
		
		XposedBridge.hookAllMethods(FolderIconClass, "dispatchDraw", new XC_MethodHook() {
			
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {	
				
				Canvas c = (Canvas) param.args[0];
				int childID = ((View) param.thisObject).getId();
				
				for (FolderIcon folder : folders) {
					
					if (folder.childID == childID && folder.totalcnt != 0) {
						folder.setCanvas(c);
					}
				}
			}			
		});
		
		XposedBridge.hookAllMethods(LauncherClass, "onResume", new XC_MethodHook() {
			
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				
				if (activityManager == null) {
					activityManager = (ActivityManager) Common.LAUNCHER_CONTEXT.getSystemService(Context.ACTIVITY_SERVICE);
				}
				
				List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
				if (appProcesses.get(0).processName.contains(Common.GEL_PACKAGE)) {
					
					if (DEBUG) log(param, "Request Counters");
					requestCounters();
				}
			}			
		});
		
		XposedBridge.hookAllMethods(CellLayoutClass, "addViewToCellLayout", new XC_MethodHook() {
			
			int CHILD = 0, CHILDID = 2;
			
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				
				int childID = (Integer) param.args[CHILDID];
				
				if (param.args[CHILD].getClass().getName().contains("BubbleTextView")) {
					
					for (Iterator<Shortcut> it = shortcutsDesktop.iterator(); it.hasNext();) {
						
						Shortcut shortcut = it.next();
						if (shortcut.childID == childID) {
							it.remove();
						}
					}
					
					shortcutsDesktop.add(new Shortcut(param.args[CHILD]));
				} else if (param.args[CHILD].getClass().getName().contains("FolderIcon")) {
					
					for (Iterator<FolderIcon> it = folders.iterator(); it.hasNext();) {
						
						FolderIcon folder = it.next();
						if (folder.childID == childID) {
							it.remove();
						}
					}
					
					folders.add(new FolderIcon(param.args[CHILD]));
				} else if (param.args[CHILD].getClass().getName().contains("PagedViewIcon")) {
					
					for (Iterator<Shortcut> it = shortcutsAppDrawer.iterator(); it.hasNext();) {
						
						Shortcut shortcut = it.next();
						if (shortcut.childID == childID) {
							it.remove();
						}
					}
					
					shortcutsAppDrawer.add(new Shortcut(param.args[CHILD]));
				}
			}			
		});
		
		XposedBridge.hookAllMethods(CellLayoutClass, "removeView", new XC_MethodHook() {
			
			int VIEW = 0;
			
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				if (DEBUG) log(param, "ID" + ((View) param.args[VIEW]).getId());
				
				int childID = ((View) param.args[VIEW]).getId();
				
				if (param.args[VIEW].getClass().getName().contains("BubbleTextView")) {
					
					for (Iterator<Shortcut> it = shortcutsDesktop.iterator(); it.hasNext();) {
						
						Shortcut shortcut = it.next();
						if (shortcut.childID == childID) {
							it.remove();
							if (DEBUG) log(param, "Removed " + shortcut.pgkName + " (ID" + shortcut.childID + ")");
						}
					}
				} else if (param.args[VIEW].getClass().getName().contains("FolderIcon")) {
					
					for (Iterator<FolderIcon> it = folders.iterator(); it.hasNext();) {
						
						FolderIcon folder = it.next();
						if (folder.childID == childID) {
							it.remove();
							if (DEBUG) log(param, "Removed " + folder.folderName + " (ID" + folder.childID + ")");
						}
					}				
				}
			}			
		});
		
		XposedBridge.hookAllMethods(WorkspaceClass, "onDragStart", new XC_MethodHook() {
			
			int INFO = 1;
			
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {	
				
				if (param.args[INFO].getClass().getName().contains("ShortcutInfo")) {
					
					long childID = getLongField(param.args[INFO], "id");
					log(param, "ID" + childID);
					for (Shortcut shortcut : shortcutsDesktop) {
						if (shortcut.childID == childID) {
							shortcut.setBadgeVisible(false);
							break;
						}
					}
				} else if (param.args[INFO].getClass().getName().contains("FolderIcon")) {
					
					int childID = ((View) param.args[INFO]).getId();
					
					for (FolderIcon folder : folders) {
						if (folder.childID == childID) {
							folder.setBadgeVisible(false);
							break;
						}
					}
				}
			}		
		});
		
		XposedBridge.hookAllMethods(FolderClass, "onAdd", new XC_MethodHook() {
			
			int ITEM = 0;
			
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {	
				if (DEBUG) log(param, "Add " + getObjectField(param.args[ITEM], "title") + " to folder " + ((View) getObjectField(param.thisObject, "mFolderIcon")).getId());
				
				Object folderIcon = getObjectField(param.thisObject, "mFolderIcon");
				int childID = ((View) folderIcon).getId();
				
				for (Iterator<FolderIcon> it = folders.iterator(); it.hasNext(); ) {
					
					FolderIcon folder = it.next();
					if (folder.childID == childID) {
						folder.resetBadges(true);
						it.remove();
					}
				}
				
				folders.add(new FolderIcon(folderIcon));
				requestCounters();
			}			
		});
		
		XposedBridge.hookAllMethods(FolderClass, "onRemove", new XC_MethodHook() {
			
			int ITEM = 0;
			
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {	
				if (DEBUG) log(param, "Remove " + getObjectField(param.args[ITEM], "title") + " from folder " + ((View) getObjectField(param.thisObject, "mFolderIcon")).getId());
				
				Object folderIcon = getObjectField(param.thisObject, "mFolderIcon");
				int childID = ((View) folderIcon).getId();
				
				for (Iterator<FolderIcon> it = folders.iterator(); it.hasNext(); ) {
					
					FolderIcon folder = it.next();
					if (folder.childID == childID) {
						folder.resetBadges(true);
						it.remove();
					}
				}
				
				folders.add(new FolderIcon(folderIcon));
				requestCounters();
			}			
		});
		
		XposedBridge.hookAllMethods(FolderClass, "replaceFolderWithFinalItem", new XC_MethodHook() {
			
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {	
				
				Object folderIcon = getObjectField(param.thisObject, "mFolderIcon");
				int childID = ((View) folderIcon).getId();
				
				if (DEBUG) log(param, "Remove folder with ID" + childID);
				
				for (Iterator<FolderIcon> it = folders.iterator(); it.hasNext();) {
					
					FolderIcon folder = it.next();
					if (folder.childID == childID) {
						it.remove();
						if (DEBUG) log(param, "Removed folder with ID" + childID);
					}
				}
				
				requestCounters();
			}			
		});
		
		XposedBridge.hookAllMethods(FolderClass, "getItemsInReadingOrder", new XC_MethodHook() {
			
			@SuppressWarnings("rawtypes")
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				
				if (DEBUG) log(param, "sdf");
				
				Object folderIcon = getObjectField(param.thisObject, "mFolderIcon");
				int childID = ((View) folderIcon).getId();
				
				
				ArrayList result = (ArrayList) param.getResult();
				
				FolderIcon folder = null;
				for (Iterator<FolderIcon> it = folders.iterator(); it.hasNext(); ) {
					
					folder = it.next();
					if (folder.childID == childID) {
						break;
					}
				}
				
				if (folder == null) return;
				
				for (Object shortcut : result) {
					int tmpChildID = ((View) shortcut).getId();
					for (Shortcut s : folder.children) {
						if (tmpChildID == s.childID) {							
							((TextView) shortcut).setCompoundDrawablesWithIntrinsicBounds(null, s.origIcon, null, null);
						}
					}
				}
			}			
		});
	}
}