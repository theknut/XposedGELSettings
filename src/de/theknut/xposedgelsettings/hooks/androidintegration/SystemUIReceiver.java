package de.theknut.xposedgelsettings.hooks.androidintegration;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RecentTaskInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.PowerManager;
import android.os.SystemClock;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers.ClassNotFoundError;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.theknut.xposedgelsettings.R;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.HooksBaseClass;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;
import de.theknut.xposedgelsettings.ui.StatusBarTintApi;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.getStaticObjectField;

public class SystemUIReceiver extends HooksBaseClass {

    private static boolean IS_CLOCK_VISIBLE = true;
    public static View CLOCK_VIEW;
	public static ImageView HOME_BUTTON;
	public static ImageView BACK_BUTTON;
	//public static ImageView RECENTS_BUTTON;
	public static Drawable ALL_APPS_BUTTON;
	public static Drawable POWER_OFF_BUTTON;
	//public static Drawable CLEAR_BUTTON;
	public static Drawable HOME_BUTTON_ORIG;
	public static Drawable BACK_BUTTON_ORIG;
	//public static Drawable RECENTS_BUTTON_ORIG;
	public static ScaleType BACK_BUTTON_ORIG_SCALE;
	public static ActivityManager activityManager;
	public static boolean shown;
	public static boolean allowFlipPanel;
	public static int animationDuration = 300;
	
	public static Drawable TMP_HOME_BUTTON;
	public static Drawable TMP_BACK_BUTTON;
	public static ScaleType TMP_BACK_BUTTON_SCALE;
    public static boolean TMP_CLOCK_VISIBILITY;
	
	public static Object PHONE_STATUSBAR_OBJECT;
	public static View STATUS_BAR_VIEW;
	public static View NAVIGATION_BAR_VIEW;

    public static int BACKGROUND_COLOR = Color.TRANSPARENT;
    public static Drawable BACKGROUND_COLOR_DRAWABLE = new ColorDrawable(BACKGROUND_COLOR);
    public static Drawable ORIG_BACKGROUND_STATUSBAR;
    public static Drawable ORIG_BACKGROUND_NAVIGATIONBAR;
	
	public static Context systemUIContext;
	
	public static void initAllHooks(LoadPackageParam lpparam) {
		
		if (DEBUG) log("SystemUIReceiver: found SystemUI " + lpparam.packageName);
		
		final Class<?> PhoneStatusBarClass = findClass("com.android.systemui.statusbar.phone.PhoneStatusBar", lpparam.classLoader);
		final Class<?> PanelBarClass = findClass("com.android.systemui.statusbar.phone.PanelBar", lpparam.classLoader);
		Class<?> tmpnbe = null;
		
		// extra stuff we have to hook because CM is doing crazy stuff		
		try {
			if (android.os.Build.VERSION.SDK_INT == android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
				tmpnbe = findClass("com.android.systemui.statusbar.NavigationButtons", lpparam.classLoader);
			}
			else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
				tmpnbe = findClass("com.android.systemui.statusbar.phone.NavbarEditor", lpparam.classLoader);
			}
			else {
				log("SystemUIReceiver: YOUR ANDROID VERSION IS NOT SUPPORTED!!!");
				return;
			}
		} catch (ClassNotFoundError cnfe) {
			// not using CM -> going for AOSP then
		}
		
		final Class<?> nbe = tmpnbe;
		
		XposedBridge.hookAllMethods(PhoneStatusBarClass, "addNavigationBar", new XC_MethodHook() {
			
			@SuppressLint("NewApi")
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				
				long time = System.currentTimeMillis();
				PHONE_STATUSBAR_OBJECT = param.thisObject;
                STATUS_BAR_VIEW = (View) getObjectField(PHONE_STATUSBAR_OBJECT, "mStatusBarView");
                NAVIGATION_BAR_VIEW = (View) getObjectField(PHONE_STATUSBAR_OBJECT, "mNavigationBarView");
				
				systemUIContext = ((Context) getObjectField(PHONE_STATUSBAR_OBJECT, "mContext"));
				
				IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(Common.XGELS_INTENT);
                systemUIContext.registerReceiver(new BroadcastReceiver() {
                	
                	Context myContext = null;
                	
                	boolean init(Context context) {
                		try {
	                		myContext = context.createPackageContext(Common.PACKAGE_NAME, Context.CONTEXT_IGNORE_SECURITY);
							ALL_APPS_BUTTON = myContext.getResources().getDrawable(R.drawable.ic_home_all_apps_holo_dark);
							POWER_OFF_BUTTON = myContext.getResources().getDrawable(R.drawable.navbar_power_icon);
							//CLEAR_BUTTON = myContext.getResources().getDrawable(R.drawable.navbar_clear_icon);
							
							if (HOME_BUTTON == null || BACK_BUTTON == null) {
								return false;
							}
							
							return true;
                		} catch (Throwable e) {
                			log("SystemUIReceiver: Something went wrong while initializing context in makeStatusBarView\n" + e);
                			return false;
                		}
                	}
                	
                    @SuppressLint("SdCardPath")
					@Override
                    public void onReceive(Context context, Intent intent) {
                    	try {
	                        if (intent.getAction().equals(Common.XGELS_INTENT) && intent.hasExtra(Common.XGELS_ACTION)) {
	                        	
	                        	if (intent.hasExtra(Common.XGELS_ACTION_EXTRA)
	                    			&& intent.getStringExtra(Common.XGELS_ACTION_EXTRA).equals(Common.XGELS_ACTION_NAVBAR)) {
	                        		
	                        		if (myContext == null) {
	                            		
	                            		if (!init(context)) {                        		
	    	                        		if (!shown) {
	    	                        			XposedBridge.log("XGELS: Couldn't initialize in SystemUI Receiver");
	    	                        			shown = true;
	    	                        		}
	    	                        		
	    	                        		return;
	                            		}
	                        		}
	                            	
	                            	if (HOME_BUTTON == null || BACK_BUTTON == null) {                      		
	                            		if (!shown) {
	                            			log("SystemUIReceiver: Couldn't initialize in SystemUI Receiver");
	                            			shown = true;
	                            		}
	                            		
	                            		return;
	                        		}
	                        		
		                        	if (intent.getStringExtra(Common.XGELS_ACTION).equals("ON_DEFAULT_HOMESCREEN")) {
		                        		
										if (PreferencesHelper.dynamicHomebutton && PreferencesHelper.dynamicIconHomebutton
											&& !HOME_BUTTON.getDrawable().getConstantState().equals(ALL_APPS_BUTTON.getConstantState())) {
											
											setHomeButtonIcon(ALL_APPS_BUTTON);
										}
			                    		
			                    		if (PreferencesHelper.dynamicBackbutton && PreferencesHelper.dynamicIconBackbutton
			                    			&& !BACK_BUTTON.getDrawable().getConstantState().equals(POWER_OFF_BUTTON.getConstantState())) {
			                    			
			                    			setBackButtonIcon(POWER_OFF_BUTTON, ScaleType.FIT_CENTER);
			                    		}
			                    		
			                    		if (PreferencesHelper.hideClock) {
			                    			showHideClock(false);
			                    		}
		                        	} else if (intent.getStringExtra(Common.XGELS_ACTION).equals("BACK_HOME_ORIG")) {
		                        		
		                        		if (PreferencesHelper.dynamicHomebutton && PreferencesHelper.dynamicIconHomebutton
		                        			&& !HOME_BUTTON.getDrawable().getConstantState().equals(HOME_BUTTON_ORIG.getConstantState())) {
		                        			
		                        			setHomeButtonIcon(HOME_BUTTON_ORIG);
		                        		}
		                        		
		                        		if (PreferencesHelper.dynamicBackbutton && PreferencesHelper.dynamicIconBackbutton
		                        			&& !BACK_BUTTON.getDrawable().getConstantState().equals(BACK_BUTTON_ORIG.getConstantState())) {
		                        			
			                        		setBackButtonIcon(BACK_BUTTON_ORIG, BACK_BUTTON_ORIG_SCALE);
		                        		}
		                        		
		                        		if (PreferencesHelper.hideClock) {
		                        			showHideClock(true);
		                        		}
		                        	} else if (intent.getStringExtra(Common.XGELS_ACTION).equals("HOME_ORIG")) {
		                        		
		                        		if (PreferencesHelper.dynamicHomebutton && PreferencesHelper.dynamicIconHomebutton
		                        			&& !HOME_BUTTON.getDrawable().getConstantState().equals(HOME_BUTTON_ORIG.getConstantState())) {
		                        			
		                        			setHomeButtonIcon(HOME_BUTTON_ORIG);
		                        		}
		                        		
		                        		if (PreferencesHelper.hideClock) {
		                        			showHideClock(true);	                        			
		                        		}
		                        	} else if (intent.getStringExtra(Common.XGELS_ACTION).equals("BACK_ORIG")) {
		                        		
		                        		if (PreferencesHelper.dynamicBackbutton && PreferencesHelper.dynamicIconBackbutton
		                    				&& !BACK_BUTTON.getDrawable().getConstantState().equals(BACK_BUTTON_ORIG.getConstantState())) {
			                        		
			                        		setBackButtonIcon(BACK_BUTTON_ORIG, BACK_BUTTON_ORIG_SCALE);
		                        		}
		                        	} else if (intent.getStringExtra(Common.XGELS_ACTION).equals("BACK_POWER_OFF")) {
		                        		
		                        		if (PreferencesHelper.dynamicBackbutton && PreferencesHelper.dynamicIconBackbutton
		                    				&& !BACK_BUTTON.getDrawable().getConstantState().equals(POWER_OFF_BUTTON.getConstantState())) {
		                        			
				                    		setBackButtonIcon(POWER_OFF_BUTTON, ScaleType.FIT_CENTER);
		                        		}
		                        	}
	                        	} else if (intent.hasExtra(Common.XGELS_ACTION_EXTRA)
	                        				&& intent.getStringExtra(Common.XGELS_ACTION_EXTRA).equals(Common.XGELS_ACTION_OTHER)) {
	                        		
	                        		if (intent.getStringExtra(Common.XGELS_ACTION).equals("GO_TO_SLEEP")) {
	                            		
	    	                        	PowerManager pm = (PowerManager) systemUIContext.getSystemService(Context.POWER_SERVICE);
	    	    						pm.goToSleep(SystemClock.uptimeMillis());
	    	    						
	                            	} else if (intent.getStringExtra(Common.XGELS_ACTION).equals("GESTURE_LAST_APP")) {
	                	        		
	                            		if (activityManager == null) {
	                						activityManager = (ActivityManager) systemUIContext.getSystemService(Context.ACTIVITY_SERVICE);
	                					}                        		
	                					
	                					ArrayList<RecentTaskInfo> apps = (ArrayList<RecentTaskInfo>) activityManager.getRecentTasks(2, ActivityManager.RECENT_IGNORE_UNAVAILABLE);
	                					if (apps.size() == 2) {
	                						
	                						RecentTaskInfo app = apps.get(1);
	                						
	                						if (app.id != -1) {
	                							activityManager.moveTaskToFront(app.id, ActivityManager.MOVE_TASK_WITH_HOME);
	                						} else {
	                							systemUIContext.startActivity(app.baseIntent);
	                						}
	                					}
	                	        	} else if (intent.getStringExtra(Common.XGELS_ACTION).equals("SHOW_NOTIFICATION_BAR")) {
	                	        		
	                            		if (PHONE_STATUSBAR_OBJECT == null) return;
	                            		
	                            		if (DEBUG) log("SystemUIReceiver: Show Notification Bar");
	                            		callMethod(PHONE_STATUSBAR_OBJECT, "animateExpandNotificationsPanel");
	                            		
	                	        	} else if (intent.getStringExtra(Common.XGELS_ACTION).equals("SHOW_SETTINGS_PANEL")) {
	                	        		
	                            		if (PHONE_STATUSBAR_OBJECT == null) return;
	                					
	                            		if (DEBUG) log("SystemUIReceiver: Show Settings Panel");
	                            		
	                            		try {
	                            			callMethod(PHONE_STATUSBAR_OBJECT, "animateExpandSettingsPanel");
	                            		} catch (NoSuchMethodError nsme) {
	                            			callMethod(PHONE_STATUSBAR_OBJECT, "animateExpandSettingsPanel", false);
	                            		}
	                            		
	                	        	} else if (intent.getStringExtra(Common.XGELS_ACTION).equals("SHOW_RECENTS")) {
	                	        		
	                            		if (PHONE_STATUSBAR_OBJECT == null) return;
	                					
	                            		if (DEBUG) log("SystemUIReceiver: Show Recents");
	                            		callMethod(PHONE_STATUSBAR_OBJECT, "toggleRecentsActivity");

	                	        	} else if (intent.getStringExtra(Common.XGELS_ACTION).equals("SHADOWS")) {

                                        if (!isLauncherInForeground()
                                            && (STATUS_BAR_VIEW == null || NAVIGATION_BAR_VIEW == null)) return;

                                        if (DEBUG) log("SystemUIReceiver: Hide shadows");

                                        if (ORIG_BACKGROUND_NAVIGATIONBAR == null) {
                                            ORIG_BACKGROUND_NAVIGATIONBAR = NAVIGATION_BAR_VIEW.getBackground();
                                            ORIG_BACKGROUND_STATUSBAR = STATUS_BAR_VIEW.getBackground();

                                            STATUS_BAR_VIEW.setBackgroundColor(BACKGROUND_COLOR);
                                            STATUS_BAR_VIEW.setBackgroundDrawable(ORIG_BACKGROUND_STATUSBAR);
                                            NAVIGATION_BAR_VIEW.setBackgroundColor(BACKGROUND_COLOR);
                                            NAVIGATION_BAR_VIEW.setBackgroundDrawable(ORIG_BACKGROUND_NAVIGATIONBAR);
                                            StatusBarTintApi.sendColorChangeIntent(Color.TRANSPARENT, Color.WHITE, Color.TRANSPARENT, Color.WHITE, systemUIContext);
                                        }

                                        boolean show = intent.getBooleanExtra("SHOW", false);
                                        Drawable background = show ? ORIG_BACKGROUND_STATUSBAR : BACKGROUND_COLOR_DRAWABLE;

                                        STATUS_BAR_VIEW.setBackgroundDrawable(background);
                                        STATUS_BAR_VIEW.setBackgroundColor(BACKGROUND_COLOR);

                                        NAVIGATION_BAR_VIEW.setBackgroundDrawable(background);
                                        NAVIGATION_BAR_VIEW.setBackgroundColor(BACKGROUND_COLOR);

                                        int color = show ? Color.parseColor("#66000000") : Color.TRANSPARENT;
                                        StatusBarTintApi.sendColorChangeIntent(color, Color.WHITE, color, Color.WHITE, systemUIContext);
                                    }
	                        	}  else if (intent.hasExtra(Common.XGELS_ACTION_EXTRA)
                        				&& intent.getStringExtra(Common.XGELS_ACTION_EXTRA).equals(Common.XGELS_ACTION_APP_REQUEST)) {
	                        		
	                        		if (intent.getStringExtra(Common.XGELS_ACTION).equals("SAVE_LOGCAT")) {
	                            		
	                        			log("Start saving logcat");
	                        			
	                        			String logfilePath = "/mnt/sdcard/XposedGELSettings/logs/logcat_systemui.log";
	                    				File logfile = new File(logfilePath);
	                    				
	                    				if (logfile.exists()) {
	                    					logfile.delete();
	                    				}
	                    				
	                    				logfile.getParentFile().mkdirs();
	                    				logfile.createNewFile();
	                    				
	                    				try {
	                    					Process process = Runtime.getRuntime().exec("logcat -v time -d");
	                    					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
	                    					StringBuilder log = new StringBuilder();
	                    					String line;
	                    					
	                    					while ((line = bufferedReader.readLine()) != null) {
	                    						log.append(line).append('\n');
	                    					}
	                    					
	                    					FileWriter out = new FileWriter(logfile);
	                    		            out.write(log.toString());
	                    		            out.close();
	                    				} catch (IOException e) {}
	                    				
	                    				log("Finished saving logcat");
	                        		}
	                        	}
	                        }
                    	} catch (NoSuchMethodError nsme) {
                    		log("Something went wrong. Show this to the dev!");
                    		log(nsme.toString());
                    	} catch (Exception ex) {
                    		log("Something went wrong. Show this to the dev!");
                    		log(ex.toString());
                    	}
                    }
                }, intentFilter);
                
                Object navBar = null;
				
                try {
					try {
						
						navBar = getObjectField(PHONE_STATUSBAR_OBJECT, "mNavigationBarView");
						HOME_BUTTON = (ImageView) callMethod(navBar, "getHomeButton");
						BACK_BUTTON = (ImageView) callMethod(navBar, "getBackButton");
						//RECENTS_BUTTON = (ImageView) callMethod(navBar, "getRecentsButton");
						
						HOME_BUTTON_ORIG = HOME_BUTTON.getDrawable();
						BACK_BUTTON_ORIG = BACK_BUTTON.getDrawable();
						BACK_BUTTON_ORIG_SCALE = BACK_BUTTON.getScaleType();
						
					} catch (NoSuchMethodError nsme) {
						
						// probably running CM
						navBar = getObjectField(PHONE_STATUSBAR_OBJECT, "mNavigationBarView");
						View mCurrentView = (View) getObjectField(navBar, "mCurrentView");
						
						if (android.os.Build.VERSION.SDK_INT == android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
							HOME_BUTTON = (ImageView) mCurrentView.findViewWithTag(getStaticObjectField(nbe, "HOME"));
							BACK_BUTTON = (ImageView) mCurrentView.findViewWithTag(getStaticObjectField(nbe, "BACK"));
							//RECENTS_BUTTON = (ImageView) mCurrentView.findViewWithTag(getStaticObjectField(nbe, "RECENT"));
						}
						else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
							HOME_BUTTON = (ImageView) mCurrentView.findViewWithTag(getStaticObjectField(nbe, "NAVBAR_HOME"));
							BACK_BUTTON = (ImageView) mCurrentView.findViewWithTag(getStaticObjectField(nbe, "NAVBAR_BACK"));
							//RECENTS_BUTTON = (ImageView) mCurrentView.findViewWithTag(getStaticObjectField(nbe, "NAVBAR_RECENT"));
						}
						
						HOME_BUTTON_ORIG = HOME_BUTTON.getDrawable();
						BACK_BUTTON_ORIG = BACK_BUTTON.getDrawable();						
						BACK_BUTTON_ORIG_SCALE = BACK_BUTTON.getScaleType();
						//RECENTS_BUTTON_ORIG = RECENTS_BUTTON.getDrawable();
					}
                }catch (Exception ex) {
					// okay probably not
					log("SystemUIReceiver: Something went wrong when hooking to SystemUI. Changing the navigation bar icons will not work. Please show this to dev:");
					log("SystemUIReceiver: " + ex);
				}
                
                if (DEBUG) log("SystemUI hooks took " + (System.currentTimeMillis() - time) + "ms");
			};
		});
		
		XposedBridge.hookAllMethods(PhoneStatusBarClass, "flipToSettings", new XC_MethodHook() {
			
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
				if (PHONE_STATUSBAR_OBJECT != null) {
										
					boolean isExpanded = (Boolean) getObjectField(PHONE_STATUSBAR_OBJECT,"mExpandedVisible");
					
					if (!isExpanded) {
						
						if (DEBUG) log(param, "Don't flip Settings Panel");
						callMethod(PHONE_STATUSBAR_OBJECT, "switchToSettings");
						param.setResult(null);
						
					} else {
						
						if (DEBUG) log(param, "Allow flip Settings Panel");
					}
				}
			}
		});
		
		if ((PreferencesHelper.dynamicBackbutton && PreferencesHelper.dynamicIconBackbutton)
			|| (PreferencesHelper.dynamicHomebutton && PreferencesHelper.dynamicIconHomebutton)) {
		
			XposedBridge.hookAllMethods(PanelBarClass, "onPanelFullyOpened", new XC_MethodHook() {
				
				@Override
				protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
					
					if (isLauncherInForeground()) {

                        TMP_CLOCK_VISIBILITY = IS_CLOCK_VISIBLE;
						TMP_HOME_BUTTON = HOME_BUTTON.getDrawable();
						TMP_BACK_BUTTON = BACK_BUTTON.getDrawable();
						TMP_BACK_BUTTON_SCALE = BACK_BUTTON.getScaleType();
						
						Intent myIntent = new Intent();
						myIntent.putExtra(Common.XGELS_ACTION_EXTRA, Common.XGELS_ACTION_NAVBAR);
						myIntent.putExtra(Common.XGELS_ACTION, "BACK_HOME_ORIG");
						myIntent.setAction(Common.XGELS_INTENT);
						systemUIContext.sendBroadcast(myIntent);
						
						if (DEBUG) log(param, "Restore default navigation bar buttons");
					}
				}
			});
			
			XposedBridge.hookAllMethods(PanelBarClass, "collapseAllPanels", new XC_MethodHook() {
				
				@Override
				protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
					
					if (isLauncherInForeground()) {
						
						if (TMP_HOME_BUTTON != null && TMP_BACK_BUTTON != null && TMP_BACK_BUTTON_SCALE != null) {
							
							setHomeButtonIcon(TMP_HOME_BUTTON);
							setBackButtonIcon(TMP_BACK_BUTTON, TMP_BACK_BUTTON_SCALE);
                            showHideClock(TMP_CLOCK_VISIBILITY);
							
							TMP_HOME_BUTTON = null;
							TMP_BACK_BUTTON = null;
							TMP_BACK_BUTTON_SCALE = null;
							
							if (DEBUG) log(param, "Restore previous navigation bar buttons");
						}
					}
				}
			});
			
//			XposedBridge.hookAllMethods(PhoneStatusBarClass, "closeRecents", new XC_MethodHook() {
//				
//				@Override
//				protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//					if (PHONE_STATUSBAR_OBJECT != null) {
//						
//						if (PreferencesHelper.dynamicRecentsbutton) {
//							
//							setRecentsButtonIcon(RECENTS_BUTTON_ORIG);
//						}
//					}
//				}
//			});
		}
	}
	
	public static boolean isLauncherInForeground() {
		
		if (activityManager == null) {
			activityManager = (ActivityManager) systemUIContext.getSystemService(Context.ACTIVITY_SERVICE);
		}
		
		List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
		return Common.PACKAGE_NAMES.contains(appProcesses.get(0).processName.replace(":search", ""));
	}
	
	public static void setHomeButtonIcon(Drawable icon) {
		HOME_BUTTON.setAlpha(0f);
		HOME_BUTTON.setImageDrawable(icon);
		
		if (PreferencesHelper.dynamicAnimateIconHomebutton) {
			HOME_BUTTON.animate().alpha(1f).setDuration(animationDuration).start();
		}
		else {
			HOME_BUTTON.setAlpha(1f);
		}
	}
    
    public static void setBackButtonIcon(Drawable icon, ScaleType scaleType) {
    	BACK_BUTTON.setAlpha(0f);
		BACK_BUTTON.setScaleType(scaleType);
		BACK_BUTTON.setImageDrawable(icon);
		
		if (PreferencesHelper.dynamicAnimateIconBackbutton) {
			BACK_BUTTON.animate().alpha(1f).setDuration(animationDuration).start();
		}
		else {
			BACK_BUTTON.setAlpha(1f);
		}
	}
    
    public static void showHideClock(boolean show) {
    	if (PHONE_STATUSBAR_OBJECT == null) return;
    	IS_CLOCK_VISIBLE = show;
		if (DEBUG) log("SystemUIReceiver: " + (show ? "Show" : "Hide") + " clock");
		callMethod(PHONE_STATUSBAR_OBJECT, "showClock", show);
	}
}