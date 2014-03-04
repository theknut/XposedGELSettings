package de.theknut.xposedgelsettings.hooks.Gestures;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getIntField;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.setIntField;

import java.io.IOException;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.WindowManager;
import android.widget.FrameLayout.LayoutParams;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;

public class GestureHooks {
	
	static WindowManager wm;
	static Display display;
	static Point size;
	static float downY;
	static float downX;
	static float upY;
	static int width;
	static int height;
	static int animateDuration = 300;
	static boolean isLandscape;
	static boolean isAnimating;
	static boolean unhideHotseatOnSwipeUP = PreferencesHelper.hideHotseat && PreferencesHelper.gestureSwipeUp_Hotseat;
	static ViewPropertyAnimator hideAnimation;
	static ViewPropertyAnimator showAnimation;
	
	public static void initAllHooks(LoadPackageParam lpparam) {
		final Class<?> WorkspaceClass = findClass(Common.WORKSPACE, lpparam.classLoader);
		
		if (unhideHotseatOnSwipeUP) {
			final Class<?> LauncherClass = findClass(Common.LAUNCHER, lpparam.classLoader);
			
			XposedBridge.hookAllMethods(LauncherClass, "showHotseat", new XC_MethodHook() {
				@Override
				protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
					param.setResult(null);
				}
			});
			
			XposedBridge.hookAllMethods(LauncherClass, "onTransitionPrepare", new XC_MethodHook() {
				
				@Override
				protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
					hideHotseat(0);
				}
			});		
		
			XposedBridge.hookAllMethods(WorkspaceClass, "onWindowVisibilityChanged", new XC_MethodHook() {
				@Override
				protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
					if (!Common.HOTSEAT_HIDDEN) {
						hideHotseat(200);
					}
				}
			});
			
			XposedBridge.hookAllMethods(WorkspaceClass, "onRequestFocusInDescendants", new XC_MethodHook() {
				@Override
				protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
					if (!Common.HOTSEAT_HIDDEN) {
						hideHotseat(200);
					}
				}
			});
		}
		
		if (PreferencesHelper.gestureSwipeDownLeft || PreferencesHelper.gestureSwipeDownRight || PreferencesHelper.hideHotseat || PreferencesHelper.gestureSwipeUp_AppDrawer || PreferencesHelper.gestureSwipeUp_Hotseat) {
			XposedBridge.hookAllMethods(WorkspaceClass, "onInterceptTouchEvent", new XC_MethodHook() {
				
				void init() throws IOException {
					wm = (WindowManager) Common.LAUNCHER_CONTEXT.getSystemService(Context.WINDOW_SERVICE);
					display = wm.getDefaultDisplay();
					size = new Point();
					display.getSize(size);
					width = size.x;
					height = size.y;
				}			
				
				@Override
				protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
					if (wm == null) init();
					
					MotionEvent ev = (MotionEvent) param.args[0];
					
					int rotation = Common.LAUNCHER_CONTEXT.getResources().getConfiguration().orientation;
					switch (rotation) {
						case Configuration.ORIENTATION_PORTRAIT:
							if (isLandscape) {
								if (unhideHotseatOnSwipeUP) { 
									hideHotseat(0);
								}
								init();
							}
							
							isLandscape = false;
							break;
						case Configuration.ORIENTATION_LANDSCAPE:
							if (!isLandscape) {
								if (unhideHotseatOnSwipeUP) { 
									hideHotseat(0);
								}
								init();
							}
							
							isLandscape = true;
							break;
						default: break;
					}
					
					switch (ev.getAction() & MotionEvent.ACTION_MASK) {
						case MotionEvent.ACTION_DOWN:
							downY = ev.getRawY();
							downX = ev.getRawX();
							
							if (!isHotseatHidden() && PreferencesHelper.hideHotseat) {
								hideHotseat(animateDuration);
							}
							
							break;
						case MotionEvent.ACTION_UP:
							float upY = ev.getRawY();
							
							if (!isAnimating && unhideHotseatOnSwipeUP && !Common.HOTSEAT_HIDDEN
									&& upY > downY && (upY - downY) > 100 && (upY >= (height - ((float) height / 3.0)))) {
								((Activity)Common.LAUNCHER_INSTANCE).runOnUiThread(new Runnable() {
								     @Override
								     public void run() {
								    	 hideHotseat(animateDuration);
								     }
								    });
							}
							else if (!isAnimating && unhideHotseatOnSwipeUP && Common.HOTSEAT_HIDDEN
									&& downY > upY && (downY - upY) > 100 && (downY >= (height - ((float) height / 2.0)))) {
								
								((Activity)Common.LAUNCHER_INSTANCE).runOnUiThread(new Runnable() {
								     @Override
								     public void run() {
								    	 showHotseat(animateDuration);
								     }
								    });
							}
							else if (PreferencesHelper.gestureSwipeUp_AppDrawer
									&& downY > upY && (downY - upY) > 100) {
								
								((Activity)Common.LAUNCHER_INSTANCE).runOnUiThread(new Runnable() {
								     @Override
								     public void run() {
								    	 callMethod(Common.LAUNCHER_INSTANCE, "showAllApps", true, Common.CONTENT_TYPE, true);
								     }
								    });
							}
							else if ((upY - downY) > 100.0 && Common.HOTSEAT_HIDDEN
									|| (upY - downY) > 50.0 && (downY <= (height - (height / 2.0))) && !Common.HOTSEAT_HIDDEN) {
								if (downX <= (width/2) && ev.getRawX() <= (width/2) && PreferencesHelper.gestureSwipeDownLeft) {
									Intent myIntent = new Intent();
									myIntent.putExtra("XGELSACTION", "NOTIFICATION_BAR");
									myIntent.setAction(Common.PACKAGE_NAME + ".Intent");
									Common.LAUNCHER_CONTEXT.sendBroadcast(myIntent);
								}
								if (downX > (width/2) && ev.getRawX() > (width/2)  && PreferencesHelper.gestureSwipeDownRight) {
									Intent myIntent = new Intent();
									myIntent.putExtra("XGELSACTION", "SETTINGS_BAR");
									myIntent.setAction(Common.PACKAGE_NAME + ".Intent");
									Common.LAUNCHER_CONTEXT.sendBroadcast(myIntent);
								}
							}
							break;
						case MotionEvent.ACTION_MOVE:
							break;
						default:
							break;
					}
				}			
			});
		}
		//final Class<?> AppsCustomizePagedViewClass = findClass(Common.APPS_CUSTOMIZE_PAGED_VIEW, lpparam.classLoader);
//		XposedBridge.hookAllMethods(AppsCustomizePagedViewClass, "onInterceptTouchEvent", new XC_MethodHook() {
//			
//			void init() throws IOException {
//				wm = (WindowManager) Common.LAUNCHER_CONTEXT.getSystemService(Context.WINDOW_SERVICE);
//				display = wm.getDefaultDisplay();
//				size = new Point();
//				display.getSize(size);
//				width = size.x;
//				height = size.y;
//			}			
//			
//			@Override
//			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//				if (wm == null) init();
//				
//				MotionEvent ev = (MotionEvent) param.args[0];
//				
//				int rotation = Common.LAUNCHER_CONTEXT.getResources().getConfiguration().orientation;
//				switch (rotation) {
//					case Configuration.ORIENTATION_PORTRAIT:
//						if (isLandscape) {
//							init();
//						}
//						
//						isLandscape = false;
//						break;
//					case Configuration.ORIENTATION_LANDSCAPE:
//						if (!isLandscape) {
//							init();
//						}
//						
//						isLandscape = true;
//						break;
//					default: break;
//				}
//				
//				switch (ev.getAction() & MotionEvent.ACTION_MASK) {
//					case MotionEvent.ACTION_DOWN:
//						downY = ev.getRawY();
//						downX = ev.getRawX();
//						
//						break;
//					case MotionEvent.ACTION_UP:
//						float upY = ev.getRawY();
//						
//						if ((upY - downY) > 100.0) {
//							callMethod(getObjectField(param.thisObject, "mLauncher"), "showWorkspace", true);
//						}
//						break;
//					default:
//						break;
//				}
//			}
//		});
	}
	
	private static void showHotseat(int duration) {
		if (Common.LAUNCHER_INSTANCE == null) return;
		
		final View mHotseat = (View) getObjectField(Common.LAUNCHER_INSTANCE, "mHotseat");
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
				Common.HOTSEAT_HIDDEN = false;
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
				mHotseat.setBackgroundColor(Color.parseColor(ColorPickerPreference.convertToARGB(PreferencesHelper.hotseatBackgroundColor)));
				mHotseat.setVisibility(View.VISIBLE);
				isAnimating = true;
			}
		});
		showAnimation.alpha(1f).setDuration(duration).start();				
	}
	
	private static boolean isHotseatHidden() {
		return Common.HOTSEAT_HIDDEN;
	}
	
	private static void hideHotseat(int duration) {
		if (Common.LAUNCHER_INSTANCE == null) return;
		
		final View mHotseat = (View) getObjectField(Common.LAUNCHER_INSTANCE, "mHotseat");
		
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
					Common.HOTSEAT_HIDDEN = true;
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
			Common.HOTSEAT_HIDDEN = true;
		}
	}
}