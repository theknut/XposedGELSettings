package de.theknut.xposedgelsettings.hooks.gestures;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

import java.io.IOException;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

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
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.HooksBaseClass;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;

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
	static boolean isAnimating;
	static boolean isDockHidden = true;
	static boolean autoHideAppDock = PreferencesHelper.hideAppDock && PreferencesHelper.autoHideAppDock;
	static ViewPropertyAnimator hideAnimation;
	static ViewPropertyAnimator showAnimation;
	static View mHotseat;
	
	static WindowManager wm;
	static Display display;
	static Point size;
	static int width;
	static int height;
	static int sector;
	static boolean isLandscape;
	
	static void init() throws IOException {
		wm = (WindowManager) Common.LAUNCHER_CONTEXT.getSystemService(Context.WINDOW_SERVICE);
		display = wm.getDefaultDisplay();
		size = new Point();
		display.getSize(size);
		width = size.x;
		height = size.y;
		
		sector = (Integer) (width / 3);
		
		mHotseat = (View) getObjectField(Common.LAUNCHER_INSTANCE, "mHotseat");
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
		
		if (action.equals("NOTIFICATION_BAR")) {
			
			Intent myIntent = new Intent();
			myIntent.putExtra(Common.XGELS_ACTION, "NOTIFICATION_BAR");
			myIntent.setAction(Common.PACKAGE_NAME + ".Intent");
			Common.LAUNCHER_CONTEXT.sendBroadcast(myIntent);
			
		} else if (action.equals("QUICKSETTINGS_PANEL")) {
			
			Intent myIntent = new Intent();
			myIntent.putExtra(Common.XGELS_ACTION, "SETTINGS_BAR");
			myIntent.setAction(Common.PACKAGE_NAME + ".Intent");
			Common.LAUNCHER_CONTEXT.sendBroadcast(myIntent);
			
		} else if (action.equals("OPEN_APPDRAWER")) {
			((Activity)Common.LAUNCHER_INSTANCE).runOnUiThread(new Runnable() {
			     @Override
			     public void run() {
			    	 
			    	 callMethod(Common.LAUNCHER_INSTANCE, "showAllApps", true, Common.CONTENT_TYPE, true);
			     }
		    });
			
		} else if (action.equals("LAST_APP")) {
			
			Intent myIntent = new Intent();
			myIntent.putExtra(Common.XGELS_ACTION_EXTRA, Common.XGELS_ACTION_OTHER);
			myIntent.putExtra(Common.XGELS_ACTION, "GESTURE_LAST_APP");						
			myIntent.setAction(Common.XGELS_INTENT);
			Common.LAUNCHER_CONTEXT.sendBroadcast(myIntent);
			
		} else if (action.equals("OPEN_SETTINGS")) {
			
			Intent LaunchIntent = new Intent(android.provider.Settings.ACTION_SETTINGS);
			LaunchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			Common.LAUNCHER_CONTEXT.startActivity(LaunchIntent);
			
		} else if (action.equals("SCREEN_OFF")) {
			
			Intent myIntent = new Intent();
			myIntent.putExtra(Common.XGELS_ACTION_EXTRA, Common.XGELS_ACTION_OTHER);
			myIntent.putExtra(Common.XGELS_ACTION, "GO_TO_SLEEP");						
			myIntent.setAction(Common.XGELS_INTENT);
			Common.LAUNCHER_CONTEXT.sendBroadcast(myIntent);
			
		} else if (action.equals("TOGGLE_DOCK")) {
			log("handle toggle dock");
			if (PreferencesHelper.appdockSettingsSwitch && PreferencesHelper.hideAppDock) {
				
				if (isDockHidden) {
					showAppdock(animateDuration);
				} else {
					hideAppdock(animateDuration);
				}
			}
			
		} else if (action.contains("APP")) {
			
			String pkg = PreferencesHelper.prefs.getString(gestureKey + "_launch", "");
			
			if (!pkg.equals("")) {
				Intent LaunchIntent = Common.LAUNCHER_CONTEXT.getPackageManager().getLaunchIntentForPackage(pkg);
				LaunchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				Common.LAUNCHER_CONTEXT.startActivity(LaunchIntent);
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
		if (Common.LAUNCHER_INSTANCE == null) return;
		log("Show");
		
		final LayoutParams lp = (LayoutParams) mHotseat.getLayoutParams();
		
		if (isLandscape) {
			lp.width = Common.HOTSEAT_BAR_HEIGHT;
			lp.height = LayoutParams.MATCH_PARENT;
		}
		else {
			lp.width = LayoutParams.MATCH_PARENT;
			lp.height = Common.HOTSEAT_BAR_HEIGHT;
		}
	
		showAnimation = mHotseat.animate();
		showAnimation.setListener(new AnimatorListener() {						
			@Override
			public void onAnimationEnd(Animator animation) {
				isDockHidden = false;
				isAnimating = false;
			}

			@Override
			public void onAnimationCancel(Animator animation) {
				isAnimating = false;
			}

			@Override
			public void onAnimationRepeat(Animator animation) {}

			@Override
			public void onAnimationStart(Animator animation) {
				
				mHotseat.setLayoutParams(lp);
				mHotseat.setBackgroundColor(Color.parseColor(ColorPickerPreference.convertToARGB(PreferencesHelper.appDockBackgroundColor)));
				mHotseat.setVisibility(View.VISIBLE);
				isAnimating = true;
			}
		});
		showAnimation.alpha(1f).setDuration(duration).start();	
	}
	
	static void hideAppdock(int duration) {
		if (Common.LAUNCHER_INSTANCE == null) return;
		log("hide");
		
		if (duration != 0) {
			
			hideAnimation = mHotseat.animate();
			hideAnimation.setListener(new AnimatorListener() {						
				@Override
				public void onAnimationEnd(Animator animation) {
					hide();
				}

				@Override
				public void onAnimationCancel(Animator animation) {
					hide();
				}

				@Override
				public void onAnimationRepeat(Animator animation) {}

				@Override
				public void onAnimationStart(Animator animation) {
					isAnimating = true;
				}
				
				public void hide() {
					mHotseat.setVisibility(View.GONE);
					isDockHidden = true;
					isAnimating = false;
					
					LayoutParams lp = (LayoutParams) mHotseat.getLayoutParams();
					lp.width = 0;
					lp.height = 0;				
					
					mHotseat.setLayoutParams(lp);
				}
			});
			
			hideAnimation.alpha(0f).setDuration(duration).start();
		}
		else {
			mHotseat.setVisibility(View.GONE);
			isDockHidden = true;
		}
	}
}
