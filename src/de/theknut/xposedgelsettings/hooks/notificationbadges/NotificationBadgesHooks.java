package de.theknut.xposedgelsettings.hooks.notificationbadges;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Classes;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Fields;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Methods;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.getLongField;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

public class NotificationBadgesHooks extends NotificationBadgesHelper {
	
	public static void initAllHooks(LoadPackageParam lpparam) {
		
		if (!PreferencesHelper.enableBadges) return;
		
		XposedBridge.hookAllMethods(Classes.Launcher, "onCreate", new XC_MethodHook() {
			
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
		
		XposedBridge.hookAllMethods(Classes.Launcher, "onDestroy", new XC_MethodHook() {
			
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
		
		findAndHookMethod(Classes.Workspace, Methods.wOnDragEnd, requestCountersHook);
		findAndHookMethod(Classes.Launcher, Methods.lOpenFolder, Classes.FolderIcon, requestCountersHook);
		findAndHookMethod(Classes.Launcher, Methods.lFinishBindingItems, boolean.class, requestCountersHook);
		
		findAndHookMethod(Classes.Workspace, Methods.wOnLauncherTransitionEnd, Classes.Launcher, boolean.class, boolean.class, new XC_MethodHook() {
			
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
		
		findAndHookMethod(Classes.FolderIcon, "dispatchDraw", Canvas.class, new XC_MethodHook() {
			
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
		
		XposedBridge.hookAllMethods(Classes.Launcher, "onResume", new XC_MethodHook() {
			
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				
				if (activityManager == null) {
					activityManager = (ActivityManager) Common.LAUNCHER_CONTEXT.getSystemService(Context.ACTIVITY_SERVICE);
				}
				
				List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
				if (Common.PACKAGE_NAMES.contains(appProcesses.get(0).processName)) {
					
					if (DEBUG) log(param, "Request Counters");
					requestCounters();
				}
			}			
		});
		
		findAndHookMethod(Classes.CellLayout, Methods.celllayoutAddViewToCellLayout, View.class, Integer.TYPE, Integer.TYPE, Classes.CellLayoutLayoutParams, boolean.class, new XC_MethodHook() {
			
			int CHILD = 0, CHILDID = 2;
			
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				
				int childID = (Integer) param.args[CHILDID];
				
				if (param.args[CHILD].getClass().getName().contains(Fields.bubbleTextView)) {
					
					for (Iterator<Shortcut> it = shortcutsDesktop.iterator(); it.hasNext();) {
						
						Shortcut shortcut = it.next();
						if (shortcut.childID == childID) {
							it.remove();
						}
					}
					
					shortcutsDesktop.add(new Shortcut(param.args[CHILD]));
				} else if (param.args[CHILD].getClass().getName().contains(Fields.folderIcon)) {
					
					for (Iterator<FolderIcon> it = folders.iterator(); it.hasNext();) {
						
						FolderIcon folder = it.next();
						if (folder.childID == childID) {
							it.remove();
						}
					}
					
					folders.add(new FolderIcon(param.args[CHILD]));
				} else if (param.args[CHILD].getClass().getName().contains(Fields.pagedViewIcon)) {
					
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
		
		XposedBridge.hookAllMethods(Classes.CellLayout, "removeView", new XC_MethodHook() {
			
			int VIEW = 0;
			
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				if (DEBUG) log(param, "ID" + ((View) param.args[VIEW]).getId());
				
				int childID = ((View) param.args[VIEW]).getId();
				
				if (param.args[VIEW].getClass().getName().contains(Fields.bubbleTextView)) {
					
					for (Iterator<Shortcut> it = shortcutsDesktop.iterator(); it.hasNext();) {
						
						Shortcut shortcut = it.next();
						if (shortcut.childID == childID) {
							it.remove();
							if (DEBUG) log(param, "Removed " + shortcut.pgkName + " (ID" + shortcut.childID + ")");
						}
					}
				} else if (param.args[VIEW].getClass().getName().contains(Fields.folderIcon)) {
					
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
		
		XC_MethodHook onDragStartMethodHook = new XC_MethodHook() {
			
			int INFO = 1;
			
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {	
				
				if (param.args[INFO].getClass().getName().contains(Fields.shortcutInfoClass)) {
					
					long childID = getLongField(param.args[INFO], Fields.iiID);
					
					for (Shortcut shortcut : shortcutsDesktop) {
						if (shortcut.childID == childID) {
							shortcut.setBadgeVisible(false);
							break;
						}
					}
				} else if (param.args[INFO].getClass().getName().contains(Fields.folderIcon)) {
					
					int childID = ((View) param.args[INFO]).getId();
					
					for (FolderIcon folder : folders) {
						if (folder.childID == childID) {
							folder.setBadgeVisible(false);
							break;
						}
					}
				}
			}		
		};
		
		if (Common.PACKAGE_OBFUSCATED) {
			findAndHookMethod(Classes.Workspace, Methods.wOnDragStart, Classes.DragSource, Object.class, onDragStartMethodHook);
		} else {
			XposedBridge.hookAllMethods(Classes.Workspace, Methods.wOnDragStart, onDragStartMethodHook);
		}
		
		findAndHookMethod(Classes.Folder, Methods.fOnAdd, Classes.ShortcutInfo, new XC_MethodHook() {
			
			int ITEM = 0;
			
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {	
				if (DEBUG) log(param, "Add " + getObjectField(param.args[ITEM], Fields.itemInfoTitle) + " to folder " + ((View) getObjectField(param.thisObject, Fields.fFolderIcon)).getId());
				
				Object folderIcon = getObjectField(param.thisObject, Fields.fFolderIcon);
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
		
		findAndHookMethod(Classes.Folder, Methods.fOnRemove, Classes.ShortcutInfo, new XC_MethodHook() {
			
			int ITEM = 0;
			
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {	
				if (DEBUG) log(param, "Remove " + getObjectField(param.args[ITEM], Fields.itemInfoTitle) + " from folder " + ((View) getObjectField(param.thisObject, Fields.fFolderIcon)).getId());
				
				Object folderIcon = getObjectField(param.thisObject, Fields.fFolderIcon);
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
		
		XposedBridge.hookAllMethods(Classes.Folder, Methods.fReplaceFolderWithFinalItem, new XC_MethodHook() {
			
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {	
				
				Object folderIcon = getObjectField(param.thisObject, Fields.fFolderIcon);
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
		
		XposedBridge.hookAllMethods(Classes.Folder, Methods.fGetItemsInReadingOrder, new XC_MethodHook() {
			
			@SuppressWarnings("rawtypes")
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				
				if (DEBUG) log(param, "hide badges for folder preview");
				
				Object folderIcon = getObjectField(param.thisObject, Fields.fFolderIcon);
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