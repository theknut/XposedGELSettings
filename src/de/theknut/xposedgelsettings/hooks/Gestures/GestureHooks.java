package de.theknut.xposedgelsettings.hooks.gestures;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getBooleanField;
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
import de.theknut.xposedgelsettings.hooks.HooksBaseClass;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;
import de.theknut.xposedgelsettings.hooks.googlesearchbar.GoogleSearchBarHooks;

public class GestureHooks extends HooksBaseClass {
	
	static WindowManager wm;
	static Display display;
	static Point size;
	static int width;
	static int height;
	static int animateDuration = 300;
	static boolean isLandscape;
	static boolean isAnimating;
	static boolean unhideAppdockOnSwipeUP = PreferencesHelper.hideAppDock && PreferencesHelper.gestureSwipeUp_AppDock;
	static ViewPropertyAnimator hideAnimation;
	static ViewPropertyAnimator showAnimation;
	
	public static void initAllHooks(LoadPackageParam lpparam) {
		
		final Class<?> WorkspaceClass = findClass(Common.WORKSPACE, lpparam.classLoader);
		
		if (unhideAppdockOnSwipeUP) {
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
					hideAppdock(0);
				}
			});		
		
			XposedBridge.hookAllMethods(WorkspaceClass, "onWindowVisibilityChanged", new XC_MethodHook() {
				@Override
				protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
					if (!Common.APPDOCK_HIDDEN) {
						hideAppdock(200);
					}
				}
			});
			
			XposedBridge.hookAllMethods(WorkspaceClass, "onRequestFocusInDescendants", new XC_MethodHook() {
				@Override
				protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
					if (!Common.APPDOCK_HIDDEN) {
						hideAppdock(200);
					}
				}
			});
		}
		
		if (PreferencesHelper.gestureSwipeDownLeft || PreferencesHelper.gestureSwipeDownRight || PreferencesHelper.hideAppDock || PreferencesHelper.gestureSwipeUp_AppDrawer || PreferencesHelper.gestureSwipeUp_AppDock) {
			XposedBridge.hookAllMethods(WorkspaceClass, "onInterceptTouchEvent", new XC_MethodHook() {
				
				boolean gnow = true;
				float downY, downX;
				
				void init() throws IOException {
					wm = (WindowManager) Common.LAUNCHER_CONTEXT.getSystemService(Context.WINDOW_SERVICE);
					display = wm.getDefaultDisplay();
					size = new Point();
					display.getSize(size);
					width = size.x;
					height = size.y;
					
//					if (Common.NOW_OVERLAY_INSTANCE != null && Common.GEL_INSTANCE != null) {
//						gnow = getBooleanField(Common.GEL_INSTANCE, "mNowEnabled");
//					}
					
					gnow = (Boolean) callMethod(Common.LAUNCHER_INSTANCE, "hasCustomContentToLeft");
				}			
				
				@Override
				protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
					if (wm == null) init();
					
					final int currentPage = getIntField(Common.WORKSPACE_INSTANCE, "mCurrentPage");					
					if (currentPage == 0 && gnow) return;
					
					MotionEvent ev = (MotionEvent) param.args[0];
					
					int rotation = Common.LAUNCHER_CONTEXT.getResources().getConfiguration().orientation;
					switch (rotation) {
						case Configuration.ORIENTATION_PORTRAIT:
							if (isLandscape) {
								if (unhideAppdockOnSwipeUP) { 
									hideAppdock(0);
								}
								init();
							}
							
							isLandscape = false;
							break;
						case Configuration.ORIENTATION_LANDSCAPE:
							if (!isLandscape) {
								if (unhideAppdockOnSwipeUP) { 
									hideAppdock(0);
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
							
							if (!isAppdockHidden() && PreferencesHelper.hideAppDock) {
								hideAppdock(animateDuration);
							}
							
							break;
						case MotionEvent.ACTION_UP:
							float upY = ev.getRawY();
							if (!isAnimating && unhideAppdockOnSwipeUP && !Common.APPDOCK_HIDDEN
									&& upY > downY && (upY - downY) > 100 && (upY >= (height - ((float) height / 3.0)))) {
								((Activity)Common.LAUNCHER_INSTANCE).runOnUiThread(new Runnable() {
								     @Override
								     public void run() {
								    	 hideAppdock(animateDuration);
								     }
								    });
							}
							else if (!isAnimating && unhideAppdockOnSwipeUP && Common.APPDOCK_HIDDEN
									&& downY > upY && (downY - upY) > 100 && (downY >= (height - ((float) height / 2.0)))) {
								
								((Activity)Common.LAUNCHER_INSTANCE).runOnUiThread(new Runnable() {
								     @Override
								     public void run() {
								    	 showAppdock(animateDuration);
								     }
								    });
							}
							else if ((PreferencesHelper.gestureSwipeUp_AppDrawer)
									&& downY > upY && (downY - upY) > 100) {
								
								((Activity)Common.LAUNCHER_INSTANCE).runOnUiThread(new Runnable() {
								     @Override
								     public void run() {
								    	 callMethod(Common.LAUNCHER_INSTANCE, "showAllApps", true, Common.CONTENT_TYPE, true);
								    	 
								    	 if (currentPage == (PreferencesHelper.defaultHomescreen - 1)) {
								    		 Intent myIntent = new Intent();								    	 
											 myIntent.putExtra(Common.XGELS_ACTION, "HOME_ORIG");						
											 myIntent.setAction(Common.XGELS_INTENT);
											 Common.LAUNCHER_CONTEXT.sendBroadcast(myIntent);
								    	 }
								     }
								    });
							}
							else if ((upY - downY) > 100.0 && Common.APPDOCK_HIDDEN
									|| (upY - downY) > 50.0 && (downY <= (height - (height / 2.0))) && !Common.APPDOCK_HIDDEN) {
								
								if (downX <= (width/2) && ev.getRawX() <= (width/2) && PreferencesHelper.gestureSwipeDownLeft
									|| PreferencesHelper.gestureSwipeDownLeft && !PreferencesHelper.gestureSwipeDownRight) {
									
									Intent myIntent = new Intent();
									myIntent.putExtra("XGELSACTION", "NOTIFICATION_BAR");
									myIntent.setAction(Common.PACKAGE_NAME + ".Intent");
									Common.LAUNCHER_CONTEXT.sendBroadcast(myIntent);
								}
								if (downX > (width/2) && ev.getRawX() > (width/2)  && PreferencesHelper.gestureSwipeDownRight
									|| PreferencesHelper.gestureSwipeDownRight && !PreferencesHelper.gestureSwipeDownLeft) {
									
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
		
		if (PreferencesHelper.gestureSwipeDown_CloseAppDrawer) {
			XC_MethodHook gestureHook = new XC_MethodHook() {
				
				void init() throws IOException {
					wm = (WindowManager) Common.LAUNCHER_CONTEXT.getSystemService(Context.WINDOW_SERVICE);
					display = wm.getDefaultDisplay();
					size = new Point();
					display.getSize(size);
					width = size.x;
					height = size.y;
				}
				
				float downY;
				boolean isDown = false;
				
				@Override
				protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
					
					if (wm == null) init();
					
					MotionEvent ev = (MotionEvent) param.args[0];
					
					int rotation = Common.LAUNCHER_CONTEXT.getResources().getConfiguration().orientation;
					switch (rotation) {
						case Configuration.ORIENTATION_PORTRAIT:
							if (isLandscape) {
								init();
							}
							
							isLandscape = false;
							break;
						case Configuration.ORIENTATION_LANDSCAPE:
							if (!isLandscape) {
								init();
							}
							
							isLandscape = true;
							break;
						default: break;
					}
					
					switch (ev.getAction() & MotionEvent.ACTION_MASK) {
					
						case MotionEvent.ACTION_MOVE:
							log("MOVE: " + ev.getRawY());
							
							if (!isDown) {
								downY = ev.getRawY();
							}
							
							break;
						case MotionEvent.ACTION_DOWN:
							log("DOWN: " + ev.getRawY());
							downY = ev.getRawY();
							isDown = true;
							break;
						case MotionEvent.ACTION_UP:
							log("UP: " + ev.getRawY());
							isDown = false;
							if ((ev.getRawY() - downY) > (height / 6)) {
								callMethod(getObjectField(param.thisObject, "mLauncher"), "showWorkspace", true);
							}
							
							break;
						default:
							break;
					}
				}
			};
			
			if (Common.HOOKED_PACKAGE.equals(Common.TREBUCHET_PACKAGE)) {
				final Class<?> PagedViewWithDraggableItemsClass = findClass(Common.PAGED_VIEW_WITH_DRAGGABLE_ITEMS, lpparam.classLoader);
				XposedBridge.hookAllMethods(PagedViewWithDraggableItemsClass, "onTouchEvent", gestureHook);
				XposedBridge.hookAllMethods(PagedViewWithDraggableItemsClass, "onInterceptTouchEvent", gestureHook);
			}
			else if (Common.HOOKED_PACKAGE.equals(Common.GEL_PACKAGE)) {
				final Class<?> PagedViewWithDraggableItemsClass = findClass(Common.PAGED_VIEW_WITH_DRAGGABLE_ITEMS, lpparam.classLoader);
				XposedBridge.hookAllMethods(PagedViewWithDraggableItemsClass, "onTouchEvent", gestureHook);
				XposedBridge.hookAllMethods(PagedViewWithDraggableItemsClass, "onInterceptTouchEvent", gestureHook);
			}
		}
	}
	
	private static void showAppdock(int duration) {
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
				Common.APPDOCK_HIDDEN = false;
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
	
	private static boolean isAppdockHidden() {
		return Common.APPDOCK_HIDDEN;
	}
	
	private static void hideAppdock(int duration) {
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
					Common.APPDOCK_HIDDEN = true;
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
			Common.APPDOCK_HIDDEN = true;
		}
	}
}