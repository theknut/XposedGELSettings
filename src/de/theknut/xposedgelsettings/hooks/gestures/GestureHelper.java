package de.theknut.xposedgelsettings.hooks.gestures;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.WindowManager;
import android.widget.FrameLayout.LayoutParams;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

import java.io.IOException;

import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.HooksBaseClass;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Fields;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;
import de.theknut.xposedgelsettings.hooks.Utils;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

public class GestureHelper extends HooksBaseClass {
	
	public enum Gestures {
		DOWN_LEFT,
		DOWN_MIDDLE,
		DOWN_RIGHT,
		UP_LEFT,
		UP_MIDDLE,
		UP_RIGHT,
		DOUBLE_TAP,
		NONE
	}
	
	static float gestureDistance = 50.0f;
	static int animateDuration = 300;
	static boolean isAnimating = false;
	static boolean autoHideAppDock = PreferencesHelper.hideAppDock && PreferencesHelper.autoHideAppDock;
	static ViewPropertyAnimator hideAnimation;
	static AnimatorListener hideListener;
	static ViewPropertyAnimator showAnimation;
	static AnimatorListener showListener;
	static View mHotseat;
	static int FORCEHIDE = 0xB00B5;
	
	static WindowManager wm;
	static Display display;
	static Point size;
	static int width;
	static int height;
	static int sector;
	static boolean isLandscape;
	
	static void init() throws IOException {		
		if (DEBUG) log("Init Gestures");
		
		wm = (WindowManager) Common.LAUNCHER_CONTEXT.getSystemService(Context.WINDOW_SERVICE);
		display = wm.getDefaultDisplay();
		size = new Point();
		display.getSize(size);
		width = size.x;
		height = size.y;
		
		sector = (Integer) (width / 3);
		
		if (PreferencesHelper.appdockSettingsSwitch && PreferencesHelper.hideAppDock) {
			
			isAnimating = false;
			mHotseat = (View) getObjectField(Common.LAUNCHER_INSTANCE, Fields.lHotseat);
			mHotseat.setBackgroundColor(Color.parseColor(ColorPickerPreference.convertToARGB(PreferencesHelper.appDockBackgroundColor)));
			
			showListener = new AnimatorListener() {
				
				@Override
				public void onAnimationEnd(Animator animation) {
					isAnimating = false;
					Common.APPDOCK_HIDDEN = false;
					if (DEBUG)  log("OnEnd Showanimation");
				}
	
				@Override
				public void onAnimationCancel(Animator animation) {
					isAnimating = false;
					Common.APPDOCK_HIDDEN = false;
					if (DEBUG) log("OnCancel Showanimation");
				}
	
				@Override
				public void onAnimationRepeat(Animator animation) {
					isAnimating = true;
					if (DEBUG) log("OnRepeat Showanimation");
				}
	
				@Override
				public void onAnimationStart(Animator animation) {
					//Common.APPDOCK_HIDDEN = false;
					isAnimating = true;
					mHotseat.setVisibility(View.VISIBLE);
				}
			};		
			
			hideListener = new AnimatorListener() {	
				
				@Override
				public void onAnimationEnd(Animator animation) {
					hide();
					if (DEBUG) log("OnEnd Hideanimation");
				}
	
				@Override
				public void onAnimationCancel(Animator animation) {
					hide();
					if (DEBUG) log("OnCancel Hideanimation");
				}
	
				@Override
				public void onAnimationRepeat(Animator animation) {
					isAnimating = true;
					if (DEBUG) log("OnRepeat Hideanimation");
				}
	
				@Override
				public void onAnimationStart(Animator animation) {
					//Common.APPDOCK_HIDDEN = true;
					isAnimating = true;
				}
				
				public void hide() {
					mHotseat.setVisibility(View.INVISIBLE);
					
					LayoutParams lp = (LayoutParams) mHotseat.getLayoutParams();
					lp.width = 0;
					lp.height = 0;				
					
					mHotseat.setLayoutParams(lp);
					isAnimating = false;
					Common.APPDOCK_HIDDEN = true;
				}
			};
		}
	}
	
	static String getGestureKey(Gestures gesture) {

		switch (gesture) {
			case DOWN_LEFT:
				return "gesture_one_down_left";
			case DOWN_MIDDLE:
				return "gesture_one_down_middle";
			case DOWN_RIGHT:
				return "gesture_one_down_right";
			case UP_LEFT:
				return "gesture_one_up_left";
			case UP_MIDDLE:
				return "gesture_one_up_middle";
			case UP_RIGHT:
				return "gesture_one_up_right";
			case DOUBLE_TAP:
				return "gesture_double_tap";
			default:
				return "";
		}
	}
	
	static void handleGesture(String gestureKey, String action) {
		handleGesture(Common.LAUNCHER_CONTEXT, gestureKey, action);
	}
	
	static void handleGesture(final Context context, String gestureKey, String action) {
		
		if (action.equals("NOTIFICATION_BAR")) {
			
			Intent myIntent = new Intent();
			myIntent.putExtra(Common.XGELS_ACTION_EXTRA, Common.XGELS_ACTION_OTHER);
			myIntent.putExtra(Common.XGELS_ACTION, "SHOW_NOTIFICATION_BAR");						
			myIntent.setAction(Common.XGELS_INTENT);
			context.sendBroadcast(myIntent);
			
		} else if (action.equals("QUICKSETTINGS_PANEL")) {
			
			Intent myIntent = new Intent();
			myIntent.putExtra(Common.XGELS_ACTION_EXTRA, Common.XGELS_ACTION_OTHER);
			myIntent.putExtra(Common.XGELS_ACTION, "SHOW_SETTINGS_PANEL");						
			myIntent.setAction(Common.XGELS_INTENT);
			context.sendBroadcast(myIntent);
			
		} else if (action.equals("OPEN_APPDRAWER")) {
			
			((Activity) Common.LAUNCHER_INSTANCE).runOnUiThread(new Runnable() {
			     @Override
			     public void run() {
			    	 
			    	 callMethod(Common.LAUNCHER_INSTANCE, "onClickAllAppsButton", new View(context));
			     }
		    });
			
		} else if (action.equals("LAST_APP")) {
			
			Intent myIntent = new Intent();
			myIntent.putExtra(Common.XGELS_ACTION_EXTRA, Common.XGELS_ACTION_OTHER);
			myIntent.putExtra(Common.XGELS_ACTION, "GESTURE_LAST_APP");						
			myIntent.setAction(Common.XGELS_INTENT);
			context.sendBroadcast(myIntent);
			
		} else if (action.equals("SHOW_RECENTS")) {
			
			Intent myIntent = new Intent();
			myIntent.putExtra(Common.XGELS_ACTION_EXTRA, Common.XGELS_ACTION_OTHER);
			myIntent.putExtra(Common.XGELS_ACTION, "SHOW_RECENTS");						
			myIntent.setAction(Common.XGELS_INTENT);
			context.sendBroadcast(myIntent);
			
		} else if (action.equals("OPEN_SETTINGS")) {
			
			Intent LaunchIntent = new Intent(android.provider.Settings.ACTION_SETTINGS);
			LaunchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			//callMethod(Common.LAUNCHER_INSTANCE, "startActivity", LaunchIntent);

            Utils.startActivity(LaunchIntent);
			
		} else if (action.equals("SCREEN_OFF")) {
			
			Intent myIntent = new Intent();
			myIntent.putExtra(Common.XGELS_ACTION_EXTRA, Common.XGELS_ACTION_OTHER);
			myIntent.putExtra(Common.XGELS_ACTION, "GO_TO_SLEEP");						
			myIntent.setAction(Common.XGELS_INTENT);
			context.sendBroadcast(myIntent);
			
		} else if (action.equals("TOGGLE_DOCK")) {
			
			if (PreferencesHelper.appdockSettingsSwitch && PreferencesHelper.hideAppDock) {
				
				if (mHotseat.getAlpha() == 0.0f) {
					showAppdock(animateDuration);
				} else {
					hideAppdock(animateDuration);
				}
			}
			
		} else if (action.contains("APP")) {
			
			String pkg = PreferencesHelper.prefs.getString(gestureKey + "_launch", "");
			
			if (!pkg.equals("")) {
				Intent LaunchIntent = context.getPackageManager().getLaunchIntentForPackage(pkg);
				LaunchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                Utils.startActivity(LaunchIntent);
			}
		}
	}

    static Gestures identifyGesture(float upX, float upY, float downX, float downY) {
		
		if (isSwipeDOWN(upY, downY)) {
			
			if (isSwipeLEFT(upX, downX, true)) {
				return Gestures.DOWN_LEFT;
			} else if (isSwipeRIGHT(upX, downX, true)) {
				return Gestures.DOWN_RIGHT;
			} else if (isSwipeMIDDLE(upX, downX, true)) {
				return Gestures.DOWN_MIDDLE;
			} else {
				// wait, what??
			}
		} else if (isSwipeUP(upY, downY)) {
			if (isSwipeLEFT(upX, downX, false)) {
				return Gestures.UP_LEFT;
			} else if (isSwipeRIGHT(upX, downX, false)) {
				return Gestures.UP_RIGHT;
			} else if (isSwipeMIDDLE(upX, downX, false)) {
				return Gestures.UP_MIDDLE;
			} else {
				// wait, what??
			}
		}
		
		return Gestures.NONE;
	}
	
	static boolean isSwipeDOWN(float upY, float downY) {
		return (upY - downY) > gestureDistance;
	}
	
	static boolean isSwipeUP(float upY, float downY) {
		return ((upY - downY) < -gestureDistance);
	}
	
	static boolean isSwipeLEFT(float upX, float downX, boolean isUP) {
		
		if (isUP) {
			if (PreferencesHelper.gesture_one_down_middle.equals("NONE")) {
				return downX < (width / 2);
			}
		} else {
			if (PreferencesHelper.gesture_one_up_middle.equals("NONE")) {
				return downX < (width / 2);
			}
		}
		
		return downX < sector;
	}
	
	static boolean isSwipeMIDDLE(float upX, float downX, boolean isUP) {
		return downX > sector && downX < (sector * 2);
	}
	
	static boolean isSwipeRIGHT(float upX, float downX, boolean isUP) {
		
		if (isUP) {
			if (PreferencesHelper.gesture_one_down_middle.equals("NONE")) {
				return downX > (width / 2);
			}
		} else {
			if (PreferencesHelper.gesture_one_up_middle.equals("NONE")) {
				return downX > (width / 2);
			}
		}
		
		return downX > (sector * 2);
	}
	
	static void showAppdock(int duration) {
		mHotseat = (View) getObjectField(Common.LAUNCHER_INSTANCE, Fields.lHotseat);
		
		if (Common.LAUNCHER_INSTANCE == null || mHotseat == null || mHotseat.getAlpha() != 0.0f) {
			if (DEBUG) log("Don't show App Dock");
			return;
		}
		
		if (DEBUG) log("Show App Dock");
		
		final LayoutParams lp = (LayoutParams) mHotseat.getLayoutParams();
		
		if (isLandscape) {
			lp.width = Common.HOTSEAT_BAR_HEIGHT;
			lp.height = LayoutParams.MATCH_PARENT;
		}
		else {
			lp.width = LayoutParams.MATCH_PARENT;
			lp.height = Common.HOTSEAT_BAR_HEIGHT;
		}
		
		mHotseat.setLayoutParams(lp);
		
		showAnimation = mHotseat.animate();
		showAnimation.setListener(showListener);
		showAnimation
			.alpha(1f)
			.setDuration(duration)
			.start();	
	}
	
	static void hideAppdock(final int duration) {
		
		if (duration == FORCEHIDE) {
			
			mHotseat = (View) getObjectField(Common.LAUNCHER_INSTANCE, Fields.lHotseat);
			
			LayoutParams lp = (LayoutParams) mHotseat.getLayoutParams();
			lp.width = 0;
			lp.height = 0;				
			
			isAnimating = false;
			mHotseat.setAlpha(0.0f);
			mHotseat.setLayoutParams(lp);
		}
		
		if (Common.LAUNCHER_INSTANCE == null || mHotseat == null || isAnimating || mHotseat.getAlpha() == 0.0f || Utils.isFolderOpen()) {
			if (DEBUG) log("Don't hide App Dock\n" +
                    "Was Launcher null: " + (Common.LAUNCHER_INSTANCE == null)
                    + "\nWas Hotseat null: " + (mHotseat == null)
                    + "\nisAnimating: " + isAnimating
                    + "\nAlpha == 0.0f: " + (mHotseat.getAlpha() == 0.0f)
                    + "\nisFolderOpen: " + Utils.isFolderOpen());
			return;
		}
		
		if (DEBUG) log("Hide App Dock (duration " + duration + ")");
		
		if (duration != 0) {
			
			mHotseat = (View) getObjectField(Common.LAUNCHER_INSTANCE, Fields.lHotseat);
			hideAnimation = mHotseat.animate();
			hideAnimation.setListener(hideListener);
			
			((Activity) Common.LAUNCHER_INSTANCE).runOnUiThread(new Runnable() {
			     @Override
			     public void run() {
			    	 
			    	 hideAnimation
				    	 .alpha(0f)
                         .setListener(new AnimatorListener() {
                                 @Override
                                 public void onAnimationStart(Animator animation) {
                                     callMethod(Common.LAUNCHER_INSTANCE, ObfuscationHelper.Methods.lCloseFolder);
                                 }

                                 @Override
                                 public void onAnimationEnd(Animator animation) { }

                                 @Override
                                 public void onAnimationCancel(Animator animation) { }

                                 @Override
                                 public void onAnimationRepeat(Animator animation) { }
                             })
				    	 .setDuration(duration)
				    	 .start();
			     }
		    });			
		}
		else {
			
			LayoutParams lp = (LayoutParams) mHotseat.getLayoutParams();
			lp.width = 0;
			lp.height = 0;				
			
			mHotseat.setLayoutParams(lp);
		}
	}
}
