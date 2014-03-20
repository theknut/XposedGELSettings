package de.theknut.xposedgelsettings.hooks.systemui;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getCharField;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.getStaticObjectField;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.PowerManager;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers.ClassNotFoundError;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.theknut.xposedgelsettings.R;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.HooksBaseClass;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;

public class SystemUIReceiver extends HooksBaseClass {
	
	public static View CLOCK_VIEW;
	public static ImageView HOME_BUTTON;
	public static ImageView BACK_BUTTON;
	public static Drawable ALL_APPS_BUTTON;
	public static Drawable POWER_OFF_BUTTON;
	public static Drawable HOME_BUTTON_ORIG;
	public static Drawable BACK_BUTTON_ORIG;
	public static ScaleType BACK_BUTTON_ORIG_SCALE;
	public static Object PHONE_STATUSBAR_OBJECT;
	
	public static void initAllHooks(LoadPackageParam lpparam) {
		
		log("SystemUI.initAllHooks: found SystemUI " + lpparam.packageName);
		
		final Class<?> nbv = findClass("com.android.systemui.statusbar.phone.PhoneStatusBar", lpparam.classLoader);		
		
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
				XposedBridge.log("XGELS: YOUR ANDROID VERSION IS NOT SUPPORTED!!!");
				return;
			}
		} catch (ClassNotFoundError cnfe) {
			// not using CM -> going for AOSP then
		}
		
		final Class<?> nbe = tmpnbe;
		
		XposedBridge.hookAllMethods(nbv, "addNavigationBar", new XC_MethodHook() {
			@SuppressLint("NewApi")
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				
				PHONE_STATUSBAR_OBJECT = param.thisObject;
				
				Object navBar = getObjectField(PHONE_STATUSBAR_OBJECT, "mNavigationBarView");
				
				try {
					HOME_BUTTON = (ImageView) callMethod(navBar, "getHomeButton");
					BACK_BUTTON = (ImageView) callMethod(navBar, "getBackButton");					
				} catch (NoSuchMethodError nsme) {
					// probably running CM
					View mCurrentView = (View) getObjectField(navBar, "mCurrentView");
					
					if (android.os.Build.VERSION.SDK_INT == android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
						HOME_BUTTON = (ImageView) mCurrentView.findViewWithTag(getStaticObjectField(nbe, "HOME"));
						BACK_BUTTON = (ImageView) mCurrentView.findViewWithTag(getStaticObjectField(nbe, "BACK"));
					}
					else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
						HOME_BUTTON = (ImageView) mCurrentView.findViewWithTag(getStaticObjectField(nbe, "NAVBAR_HOME"));
						BACK_BUTTON = (ImageView) mCurrentView.findViewWithTag(getStaticObjectField(nbe, "NAVBAR_BACK"));
					}
				}
				
				HOME_BUTTON_ORIG = HOME_BUTTON.getDrawable();
				BACK_BUTTON_ORIG = BACK_BUTTON.getDrawable();
				BACK_BUTTON_ORIG_SCALE = BACK_BUTTON.getScaleType();
				
				final Context systemUIContext = ((Context) getObjectField(PHONE_STATUSBAR_OBJECT, "mContext"));
				
				IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(Common.XGELS_INTENT);
                intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
                systemUIContext.registerReceiver(new BroadcastReceiver() {
                	
                	final int animationDuration = 300;
                	Context myContext = null;
                	
                	boolean init(Context context) {
                		try {
	                		myContext = context.createPackageContext(Common.PACKAGE_NAME, Context.CONTEXT_IGNORE_SECURITY);
							ALL_APPS_BUTTON = myContext.getResources().getDrawable(R.drawable.ic_home_all_apps_holo_dark);
							POWER_OFF_BUTTON = myContext.getResources().getDrawable(R.drawable.navbar_power_icon);
							
							return true;
                		} catch (Throwable e) {
                			log("Something went wrong while initializing context in makeStatusBarView\n" + e);
                			return false;
                		}
                	}
                	
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        if (intent.getAction().equals(Common.XGELS_INTENT) && intent.hasExtra(Common.XGELS_ACTION)) {
                        	
                        	if (myContext == null && !init(context)) return;
                        	
                        	if (intent.getStringExtra(Common.XGELS_ACTION).equals("ON_DEFAULT_HOMESCREEN")) {
								if (PreferencesHelper.dynamicHomebutton && PreferencesHelper.dynamicIconHomebutton
									&& !HOME_BUTTON.getDrawable().equals(ALL_APPS_BUTTON)) {
									
									setHomeButtonIcon(ALL_APPS_BUTTON);
								}
	                    		
	                    		if (PreferencesHelper.dynamicBackbutton && PreferencesHelper.dynamicIconBackbutton
	                    			&& !BACK_BUTTON.getDrawable().equals(POWER_OFF_BUTTON)) {
	                    			
	                    			setBackButtonIcon(POWER_OFF_BUTTON, ScaleType.FIT_CENTER);
	                    		}
	                    		
	                    		if (PreferencesHelper.hideClock) {
	                    			log("custom");
	                    			callMethod(PHONE_STATUSBAR_OBJECT, "showClock", false);
	                    		}
                        	}
                        	else if (intent.getStringExtra(Common.XGELS_ACTION).equals("HOME_ORIG")) {
                        		
                        		if (PreferencesHelper.dynamicHomebutton && PreferencesHelper.dynamicIconHomebutton
                        			&& !HOME_BUTTON.getDrawable().equals(HOME_BUTTON_ORIG)) {
                        			
                        			setHomeButtonIcon(HOME_BUTTON_ORIG);
                        		}
                        		
                        		if (PreferencesHelper.dynamicBackbutton && PreferencesHelper.dynamicIconBackbutton
                        			&& !BACK_BUTTON.getDrawable().equals(BACK_BUTTON_ORIG)) {
                        			
	                        		setBackButtonIcon(BACK_BUTTON_ORIG, BACK_BUTTON_ORIG_SCALE);
                        		}
                        		
                        		if (PreferencesHelper.hideClock) {
                        			log("home orig");
                        			callMethod(PHONE_STATUSBAR_OBJECT, "showClock", true);
                        		}
                        	}
                        	else if (intent.getStringExtra(Common.XGELS_ACTION).equals("BACK_ORIG")) {
                        		
                        		if (PreferencesHelper.dynamicBackbutton && PreferencesHelper.dynamicIconBackbutton
                    				&& !BACK_BUTTON.getDrawable().equals(BACK_BUTTON_ORIG)) {
	                        		
	                        		setBackButtonIcon(BACK_BUTTON_ORIG, BACK_BUTTON_ORIG_SCALE);
                        		}
                        	}
                        	else if (intent.getStringExtra(Common.XGELS_ACTION).equals("BACK_POWER_OFF")) {
                        		
                        		if (PreferencesHelper.dynamicBackbutton && PreferencesHelper.dynamicIconBackbutton
                    				&& !BACK_BUTTON.getDrawable().equals(POWER_OFF_BUTTON)) {
                        			
		                    		setBackButtonIcon(POWER_OFF_BUTTON, ScaleType.FIT_CENTER);
                        		}
                        	}
                        	else if (intent.getStringExtra(Common.XGELS_ACTION).equals("GO_TO_SLEEP")) {
                        		
	                        	PowerManager pm = (PowerManager) systemUIContext.getSystemService(Context.POWER_SERVICE);
	    						pm.goToSleep(SystemClock.uptimeMillis());
                        	}
                        }
                    }
                    
                    public void setHomeButtonIcon(Drawable icon) {
        				HOME_BUTTON.setAlpha(0f);
        	    		HOME_BUTTON.setImageDrawable(icon);
        	    		
        	    		if (PreferencesHelper.dynamicAnimateIconHomebutton) {
        	    			HOME_BUTTON.animate().alpha(1f).setDuration(animationDuration).start();
        	    		}
        	    		else {
        	    			HOME_BUTTON.setAlpha(1f);
        	    		}
        			}
                    
                    public void setBackButtonIcon(Drawable icon, ScaleType scaleType) {
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
                }, intentFilter);
			};
		});
	}
}
