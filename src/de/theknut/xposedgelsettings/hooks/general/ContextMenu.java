package de.theknut.xposedgelsettings.hooks.general;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.theknut.xposedgelsettings.R;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.HooksBaseClass;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Classes;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Fields;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Methods;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;
import de.theknut.xposedgelsettings.ui.FragmentSelectiveIcon;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.getAdditionalInstanceField;
import static de.robv.android.xposed.XposedHelpers.getLongField;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.setAdditionalInstanceField;

/**
 * Created by Alexander Schulz on 20.07.2014.
 */
public class ContextMenu extends HooksBaseClass{

    static Context XGELSContext;
    static final String HOLDER_TAG = "XGELS_CONTEXT_MENU_HOLDER";
    static final String CONTEXT_MENU_TAG = "XGELS_CONTEXT_MENU";
    static float contextMenuWidth, contextMenuHeight, contextMenuItemWidth, padding, downX, downY;
    static int animatingDuration = 150;
    static boolean isAnimating;
    static int closeThreshold;


    public static void initAllHooks(final LoadPackageParam lpparam) {

        XC_MethodHook addResizeFrameHook = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                Object resize = getAdditionalInstanceField(param.args[0], "resize");
                if (resize == null) {
                    param.setResult(null);
                } else if (resize != null) {
                    setAdditionalInstanceField(param.args[0], "resize", false);
                    if (!(Boolean) resize) {
                        param.setResult(null);
                    }
                }
            }
        };

        if (Common.PACKAGE_OBFUSCATED) {
            findAndHookMethod(Classes.DragLayer, Methods.dlAddResizeFrame, Classes.LauncherAppWidgetHostView, Classes.CellLayout, addResizeFrameHook);
        } else {
            findAndHookMethod(Classes.DragLayer, Methods.dlAddResizeFrame, Classes.ItemInfo, Classes.LauncherAppWidgetHostView, Classes.CellLayout, addResizeFrameHook);
        }

        XC_MethodHook longClickHook = new XC_MethodHook() {

            LayoutInflater inflater;

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                if (!param.thisObject.getClass().equals(Classes.Folder)) {
                    if (callMethod(Common.WORKSPACE_INSTANCE, Methods.lGetOpenFolder) != null) {
                        return;
                    }
                }

                final View longPressedItem = (View) param.args[0];
                if (longPressedItem.getClass().equals(Classes.CellLayout) || longPressedItem.getClass().equals(Classes.FolderIcon)) return;

                if (inflater == null) {
                    XGELSContext = Common.LAUNCHER_CONTEXT.createPackageContext(Common.PACKAGE_NAME, Context.CONTEXT_IGNORE_SECURITY);
                    inflater = (LayoutInflater) XGELSContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    contextMenuItemWidth = XGELSContext.getResources().getDimension(R.dimen.context_menu_icon_size);
                    contextMenuHeight = XGELSContext.getResources().getDimension(R.dimen.context_menu_icon_size);
                    padding =  XGELSContext.getResources().getDimension(R.dimen.context_menu_padding);
                    closeThreshold = Math.round(XGELSContext.getResources().getDimension(R.dimen.context_menu_close_threshold));

                    contextMenuHeight += 2 * padding;
                }

                final ViewGroup dragLayer = getDragLayer();
                ViewGroup contextMenuHolder = setupContextMenu(longPressedItem);
                closeAndRemove();
                dragLayer.addView(contextMenuHolder);
                animateOpen(contextMenuHolder);
            }

            private ViewGroup setupContextMenu(View longPressedItem) {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                ViewGroup contextMenuHolder = (ViewGroup) inflater.inflate(R.layout.contextmenu, null, true);
                ViewGroup contextMenu = (ViewGroup) contextMenuHolder.findViewById(R.id.contextmenu);
                setupMenuItems(contextMenuHolder, longPressedItem);

                contextMenuWidth = getContextMenuWidth(contextMenu);
                int availableWidth = getAvailableWidth();
                int statusBarHeight = getStatusBarHeight();

                int[] location = new int[2];
                longPressedItem.getLocationInWindow(location);

                float neededSpace = contextMenuWidth / 2;
                float x, y;
                x = location[0] - neededSpace + longPressedItem.getWidth() / 2;
                if (isWidget(longPressedItem)) {
                    y = location[1] + longPressedItem.getHeight() / 2 - contextMenuHeight;
                } else {
                    y = location[1] - contextMenuHeight;
                    if (Common.PACKAGE_OBFUSCATED) {
                        y = location[1] - contextMenuHeight / 2 - longPressedItem.getPivotY() - padding;
                    }

                    if (y < statusBarHeight) {
                        y = statusBarHeight + location[1];
                    }
                }

                if (x < (3 * padding)) {
                    x = 3 * padding;
                } else if ((x + contextMenuWidth + 3 * padding) > availableWidth) {
                    x = availableWidth - contextMenuWidth - 3 * padding;
                }

                params.setMargins(Math.round(x), Math.round(y), 0, 0);

                contextMenuHolder.setTag(HOLDER_TAG);
                contextMenu.setTag(CONTEXT_MENU_TAG);
                contextMenu.setLayoutParams(params);

                return contextMenuHolder;
            }
        };

        findAndHookMethod(Classes.Launcher, "onLongClick", View.class, longClickHook);
        findAndHookMethod(Classes.Folder, "onLongClick", View.class, longClickHook);

        findAndHookMethod(Classes.DragLayer, "onTouchEvent", MotionEvent.class, new XC_MethodHook() {

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                MotionEvent ev = (MotionEvent) param.args[0];
                if ((ev.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_MOVE) {
                    if (Math.abs(ev.getRawX() - downX) > closeThreshold
                        || Math.abs(ev.getRawY() - downY) > closeThreshold) {
                        closeAndRemove();
                    }
                }
            }
        });

        XC_MethodHook handleTouch = new XC_MethodHook() {

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                MotionEvent ev = (MotionEvent) param.args[0];
                if ((ev.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
                    closeAndRemove();
                    downX = ev.getRawX();
                    downY = ev.getRawY();
                }
            }
        };

        findAndHookMethod(Classes.Workspace, "onInterceptTouchEvent", MotionEvent.class, handleTouch);
        findAndHookMethod(Classes.Hotseat, "onInterceptTouchEvent", MotionEvent.class, handleTouch);
    }

    public static int getStatusBarHeight() {
        int result = 0;
        int resourceId = Common.LAUNCHER_CONTEXT.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = Common.LAUNCHER_CONTEXT.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private static int getAvailableWidth() {
        WindowManager wm = (WindowManager) Common.LAUNCHER_CONTEXT.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    private static float getContextMenuWidth(ViewGroup contextMenu) {
        float height = 0.0f;

        for (int i = 0; i < contextMenu.getChildCount(); i++) {
            View child = contextMenu.getChildAt(i);
            if (child.getVisibility() == View.VISIBLE) {
                height += contextMenuItemWidth;
            }
        }

        return height + 2 * padding;
    }

    private static void setupMenuItems(ViewGroup contextMenuHolder, final View longPressedItem) {

        boolean show, isWidget, isFolder, isSystemApp;

        final boolean isPremium = checkPremium();
        final Object tag = longPressedItem.getTag();

        isWidget = isWidget(tag);
        isFolder = isFolder(tag);
        isSystemApp = isSystemApp(tag);

        ImageView uninstall = (ImageView) contextMenuHolder.findViewById(R.id.uninstall);
        uninstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeAndRemove();

                Intent intent = new Intent(Intent.ACTION_DELETE);
                intent.setData(Uri.parse("package:" + getPackageName(tag)));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Common.LAUNCHER_CONTEXT.startActivity(intent);
            }
        });
        show = isSystemApp || isFolder;
        uninstall.setVisibility(show ? View.GONE : View.VISIBLE);

        ImageView appInfo = (ImageView) contextMenuHolder.findViewById(R.id.appinfo);
        appInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeContextMenu();

                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                try {
                    intent.setData(Uri.parse("package:" + getPackageName(tag)));
                } catch (Exception ex) {
                    intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                }

                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                Common.LAUNCHER_CONTEXT.startActivity(intent);
            }
        });
        show = isFolder;
        appInfo.setVisibility(show ? View.GONE : View.VISIBLE);

        ImageView remove = (ImageView) contextMenuHolder.findViewById(R.id.remove);
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeAndRemove();

                ((ViewGroup) longPressedItem.getParent()).removeView(longPressedItem);
                callStaticMethod(Classes.LauncherModel, Methods.lmDeleteItemFromDatabase, Common.LAUNCHER_INSTANCE, longPressedItem.getTag());
            }
        });
        // TODO: widgets need more than deleteItemFromDatabase, defer this for later
        // http://androidxref.com/4.4.2_r1/xref/packages/apps/Launcher3/src/com/android/launcher3/DeleteDropTarget.java#327
        show = isWidget;
        remove.setVisibility(show ? View.GONE : View.VISIBLE);

        ImageView addToFolder = (ImageView) contextMenuHolder.findViewById(R.id.addtofolder);
        addToFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeContextMenu();

                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                try {
                    intent.setData(Uri.parse("package:" + getPackageName(tag)));
                } catch (Exception ex) {
                    intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                }

                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                Common.LAUNCHER_CONTEXT.startActivity(intent);
            }
        });
        show = isFolder;
        addToFolder.setVisibility(show ? View.VISIBLE : View.GONE);

        ImageView iconPicker = (ImageView) contextMenuHolder.findViewById(R.id.settings);
        iconPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeContextMenu();

                if (!isPremium) {
                    showPremiumOnly();
                    return;
                }

                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                intent.setComponent(new ComponentName(Common.PACKAGE_NAME, Common.PACKAGE_NAME + ".ui.FragmentSelectiveIcon"));
                intent.putExtra("app", getComponentName(tag).flattenToString());
                intent.putExtra("mode", FragmentSelectiveIcon.MODE_PICK_SHORTCUT_ICON);
                intent.putExtra("itemtid", getLongField(tag, Fields.iiID));
                Common.LAUNCHER_CONTEXT.startActivity(intent);
            }
        });
        show = isFolder || isWidget;
        iconPicker.setVisibility(show ? View.GONE : View.VISIBLE);

        ImageView resize = (ImageView) contextMenuHolder.findViewById(R.id.resize);
        resize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeAndRemove();

                setAdditionalInstanceField(longPressedItem, "resize", true);
                callMethod(getDragLayer(), Methods.dlAddResizeFrame, longPressedItem, longPressedItem.getParent().getParent());
            }
        });
        show = isWidget;
        resize.setVisibility(show ? View.VISIBLE : View.GONE);

        ImageView layerUp = (ImageView) contextMenuHolder.findViewById(R.id.layerup);
        layerUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeAndRemove();
                longPressedItem.bringToFront();

                Bundle bundles = new Bundle();
                Bundle bundle = new Bundle();
                bundle.putLong("id", getLongField(tag, Fields.iiID));
                bundle.putBoolean("front", true);
                bundles.putBundle("" + 0, bundle);
                //savePositionInLayer(bundles);
            }
        });
        show = (PreferencesHelper.overlappingWidgets && isWidget) || isIntecting(longPressedItem);
        layerUp.setVisibility(show ? View.VISIBLE : View.GONE);

        ImageView layerDown = (ImageView) contextMenuHolder.findViewById(R.id.layerdown);
        layerDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeAndRemove();

                long id = getLongField(tag, Fields.iiID);
                boolean foundChild = false;
                ViewGroup shortcutAndWidgetsContainer = (ViewGroup) longPressedItem.getParent();
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < shortcutAndWidgetsContainer.getChildCount(); j++) {
                        View child = shortcutAndWidgetsContainer.getChildAt(j);
                        if (intersects(child) && getLongField(child.getTag(), Fields.iiID) != id) {
                            child.bringToFront();
                        }
                    }
                }
            }

            private boolean intersects(View child) {
                Rect myViewRect = new Rect();
                longPressedItem.getHitRect(myViewRect);

                Rect otherViewRect1 = new Rect();
                child.getHitRect(otherViewRect1);

                return Rect.intersects(myViewRect, otherViewRect1);
            }
        });
        show = (PreferencesHelper.overlappingWidgets && isWidget) || isIntecting(longPressedItem);
        layerDown.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private static void savePositionInLayer(Bundle bundles) {
        Intent i = new Intent(Common.XGELS_ACTION_SAVE_LAYER_POSITIONS);
        i.putExtra("layerpositions", bundles);
        Common.LAUNCHER_CONTEXT.sendBroadcast(i);
    }

    private static boolean isIntecting(View item) {
        long id = getLongField(item.getTag(), Fields.iiID);
        ViewGroup shortcutAndWidgetsContainer = (ViewGroup) item.getParent();
        for (int i = 0; i < shortcutAndWidgetsContainer.getChildCount(); i++) {
            Rect myViewRect = new Rect();
            Rect otherViewRect1 = new Rect();
            View child = shortcutAndWidgetsContainer.getChildAt(i);

            item.getHitRect(myViewRect);
            child.getHitRect(otherViewRect1);

            if (Rect.intersects(myViewRect, otherViewRect1) && getLongField(child.getTag(), Fields.iiID) != id) {
                return true;
            }
        }

        return false;
    }

    private static String getPackageName(Object tag) {

        try {
            return getComponentName(tag).getPackageName();
        } catch (Error e) {
            // Oh dear what happened?
        } catch (Exception e) {
            // Oh dear what happened?
        }

        return null;
    }

    private static ComponentName getComponentName(Object tag) {
        try {
            if (isWidget(tag)) {
                return ((ComponentName) getObjectField(tag, Fields.lawiProviderName));
            } else {
                Intent i = (Intent) callMethod(tag, Methods.siGetIntent);
                return i.getComponent();
            }
        } catch (Error e) {
            // Oh dear what happened?
        } catch (Exception e) {
            // Oh dear what happened?
        }

        return null;
    }

    private static boolean isWidget(View longPressedItem) {

        return isWidget(longPressedItem.getTag());
    }

    private static boolean isWidget(Object tag) {

        return tag.getClass().equals(Classes.LauncherAppWidgetInfo);
    }

    private static boolean isFolder(View longPressedItem) {

        return isFolder(longPressedItem.getTag());
    }

    private static boolean isFolder(Object tag) {

        return tag.getClass().equals(Classes.FolderInfo);
    }

    private static boolean isSystemApp(Object tag) {

        int flags;
        try {
            String pkg = getPackageName(tag);
            PackageManager pm = Common.LAUNCHER_CONTEXT.getPackageManager();
            flags = pm.getPackageInfo(pkg, 0).applicationInfo.flags;
        } catch (Error e) {
            // Oh dear what happened?
            return false;
        } catch (Exception e) {
            // Oh dear what happened?
            return false;
        }

        return (flags & 0x81) == 0x81;
    }

    private static ViewGroup getDragLayer() {
        return (ViewGroup) callMethod(Common.LAUNCHER_INSTANCE, Methods.lGetDragLayer);
    }

    private static void removeContextMenu() {
        ViewGroup dragLayer = getDragLayer();
        View v = dragLayer.findViewWithTag(HOLDER_TAG);
        if (v != null) {
            dragLayer.removeView(v);
        }
    }

    private static void animateOpen(final ViewGroup contextMenu) {
        final View menu = contextMenu.findViewById(R.id.contextmenu);
        ScaleAnimation scale = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, contextMenuWidth / 2, contextMenuHeight / 2);
        scale.setFillAfter(true);
        scale.setDuration(animatingDuration);
        scale.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) {
                menu.bringToFront();
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });
        menu.startAnimation(scale);
    }

    public static void closeAndRemove() {
        if (isAnimating) return;

        final ViewGroup contextMenuHolder = (ViewGroup) getDragLayer().findViewWithTag(HOLDER_TAG);
        if (contextMenuHolder != null) {

            final View menu = contextMenuHolder.findViewWithTag(CONTEXT_MENU_TAG);
            ScaleAnimation scale = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f, contextMenuWidth / 2, contextMenuHeight / 2);
            scale.setDuration(animatingDuration);
            scale.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    isAnimating = true;
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    removeContextMenu();
                    isAnimating = false;
                }

                @Override
                public void onAnimationRepeat(Animation animation) { }
            });
            menu.startAnimation(scale);

            contextMenuHolder.invalidate();
        }
    }

    public static boolean isOpen() {
        return getDragLayer().findViewWithTag(HOLDER_TAG) != null;
    }
}