package de.theknut.xposedgelsettings.hooks.general;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.theknut.xposedgelsettings.R;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.HooksBaseClass;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Classes;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Fields;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Methods;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;
import de.theknut.xposedgelsettings.hooks.Utils;
import de.theknut.xposedgelsettings.ui.AllAppsList;
import de.theknut.xposedgelsettings.ui.FragmentSelectiveIcon;
import de.theknut.xposedgelsettings.ui.SaveActivity;

import static de.robv.android.xposed.XposedBridge.hookAllMethods;
import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.getAdditionalInstanceField;
import static de.robv.android.xposed.XposedHelpers.getIntField;
import static de.robv.android.xposed.XposedHelpers.getLongField;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.setAdditionalInstanceField;
import static de.robv.android.xposed.XposedHelpers.setObjectField;
import static de.theknut.xposedgelsettings.hooks.Utils.isIntersecting;

/**
 * Created by Alexander Schulz on 20.07.2014.
 */
public class ContextMenu extends HooksBaseClass{

    static Context XGELSContext;
    static final String HOLDER_TAG = "XGELS_CONTEXT_MENU_HOLDER";
    static final String CONTEXT_MENU_TAG = "XGELS_CONTEXT_MENU";
    static float contextMenuWidth, contextMenuHeight, contextMenuItemWidth, padding, downX = -1, downY = -1;
    static int animatingDuration = 150;
    static boolean isAnimating;
    static boolean isOpen;
    static int closeThreshold;

    static final int DISABLED = 0;
    static final int SHORTCUT_ONLY = 1;
    static final int WIDGET_ONLY = 2;
    static final int SHORTCUT_WIDGET = 3;
    static ClassLoader classLoader;

    public static void initAllHooks(final LoadPackageParam lpparam) {
        classLoader = lpparam.classLoader;
        if (isMode(DISABLED)) return;

        if (isMode(WIDGET_ONLY) || isMode(SHORTCUT_WIDGET)) {
            XC_MethodHook addResizeFrameHook = new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    int WIDGET = 0;
                    if ((Common.PACKAGE_OBFUSCATED && Common.GNL_PACKAGE_INFO.versionCode >= ObfuscationHelper.GNL_4_2_16)
                            || !Common.PACKAGE_OBFUSCATED) {
                        WIDGET = 1;
                    } else if (Common.PACKAGE_OBFUSCATED && Common.GNL_PACKAGE_INFO.versionCode < ObfuscationHelper.GNL_4_2_16) {
                        WIDGET = 0;
                    }

                    Object resize = getAdditionalInstanceField(param.args[WIDGET], "resize");
                    if (resize == null) {
                        param.setResult(null);
                    } else {
                        setAdditionalInstanceField(param.args[WIDGET], "resize", false);
                        if (!(Boolean) resize) {
                            param.setResult(null);
                        }
                    }
                }
            };

            if (Common.IS_GNL && Common.IS_M_GNL) {

            } else if (Common.GNL_PACKAGE_INFO.versionCode >= ObfuscationHelper.GNL_4_2_16) {
                findAndHookMethod(Classes.DragLayer, Methods.dlAddResizeFrame, Classes.ItemInfo, Classes.LauncherAppWidgetHostView, Classes.CellLayout, addResizeFrameHook);
            }
            else if (Common.PACKAGE_OBFUSCATED && Common.GNL_PACKAGE_INFO.versionCode < ObfuscationHelper.GNL_4_2_16) {
                findAndHookMethod(Classes.DragLayer, Methods.dlAddResizeFrame, Classes.LauncherAppWidgetHostView, Classes.CellLayout, addResizeFrameHook);
            } else {
                findAndHookMethod(Classes.DragLayer, Methods.dlAddResizeFrame, Classes.ItemInfo, Classes.LauncherAppWidgetHostView, Classes.CellLayout, addResizeFrameHook);
            }
        }

        XC_MethodHook longClickHook = new XC_MethodHook() {

            LayoutInflater inflater;

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (PreferencesHelper.lockHomescreen) return;

                if (!param.thisObject.getClass().equals(Classes.Folder)) {
                    if (callMethod(Common.WORKSPACE_INSTANCE, Methods.wGetOpenFolder) != null) {
                        return;
                    }
                }

                final View longPressedItem = (View) param.args[0];
                if (longPressedItem.getTag() == null) return;
                if (longPressedItem.getClass().equals(Classes.CellLayout)) return;

                if (Common.IS_KK_TREBUCHET) {
                    try {
                        // alls apps button
                        if (getIntField(longPressedItem.getTag(), Fields.iiItemType) == 5) {
                            return;
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (isMode(SHORTCUT_ONLY) && !longPressedItem.getClass().equals(Classes.BubbleTextView)) return;
                if (isMode(WIDGET_ONLY) && !isWidget(longPressedItem)) return;

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
            final int INVALID = -1;
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                MotionEvent ev = (MotionEvent) param.args[0];
                if ((ev.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_MOVE) {
                    if (downX == INVALID || downY == INVALID) {
                        downX = ev.getRawX();
                        downY = ev.getRawY();
                    } else if (Math.abs(ev.getRawX() - downX) > closeThreshold
                            || Math.abs(ev.getRawY() - downY) > closeThreshold) {
                        closeAndRemove();
                    }
                } else if ((ev.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                    downX = downY = INVALID;
                }
            }
        });

        hookAllMethods(Classes.PagedView, Methods.pvPageBeginMoving, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (isOpen) {
                    closeAndRemove();
                }
            }
        });
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

        boolean show;
        final boolean isWidget, isFolder, isSystemApp;

        final boolean isPremium = checkPremium();
        final Object tag = longPressedItem.getTag();

        isWidget = isWidget(tag);
        isFolder = isFolder(tag);
        isSystemApp = isSystemApp(tag);

        ImageView uninstall = (ImageView) contextMenuHolder.findViewById(R.id.uninstall);
        Utils.setDrawableSelector(uninstall);
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
        show = isSystemApp || isFolder || getPackageName(tag) == null;
        uninstall.setVisibility(show ? View.GONE : View.VISIBLE);

        ImageView gesture = (ImageView) contextMenuHolder.findViewById(R.id.gesture);
        Utils.setDrawableSelector(gesture);
        gesture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeAndRemove();

                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                intent.setComponent(new ComponentName(Common.PACKAGE_NAME, SaveActivity.class.getName()));
                intent.putExtra("mode", SaveActivity.MODE_PICK_COLOR);
                intent.putExtra("itemid", getLongField(tag, Fields.iiID));
                Common.LAUNCHER_CONTEXT.startActivity(intent);
            }
        });
        show = !isFolder;
        gesture.setVisibility(View.GONE);

        ImageView appInfo = (ImageView) contextMenuHolder.findViewById(R.id.appinfo);
        Utils.setDrawableSelector(appInfo);
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
        show = isFolder || getPackageName(tag) == null;
        appInfo.setVisibility(show ? View.GONE : View.VISIBLE);

        final ImageView editName = (ImageView) contextMenuHolder.findViewById(R.id.appname);
        Utils.setDrawableSelector(editName);
        editName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeContextMenu();

                final String curName = String.valueOf(getObjectField(tag, "title"));
                final AlertDialog editNameDialog = new AlertDialog.Builder(Common.LAUNCHER_INSTANCE).create();
                final ViewGroup editNameView = (ViewGroup) LayoutInflater.from(XGELSContext).inflate(R.layout.edit_app_name, null);
                final EditText editText = (EditText) editNameView.findViewById(R.id.edit_app_name_edittext);
                editText.setHint(curName);

                int padding = Math.round(XGELSContext.getResources().getDimension(R.dimen.edit_app_name_padding));
                editNameDialog.setView(editNameView, padding, padding, padding, padding);

                editNameView.findViewById(R.id.edit_app_name_save).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String newName = editText.getText().toString().trim();
                        if (newName.length() == 0) {
                            editNameDialog.dismiss();
                            return;
                        }

                        if (!curName.equals(newName)) {
                            String itemId = String.valueOf(getLongField(tag, Fields.iiID));
                            setObjectField(tag, "title", newName);
                            ((TextView) longPressedItem).setText(newName);

                            Iterator<String> it = PreferencesHelper.appNames.iterator();
                            while (it.hasNext()) {
                                String next = it.next();
                                String[] split = next.split("\\|");
                                if (split[0].equals(itemId)) {
                                    it.remove();
                                    break;
                                }
                            }

                            PreferencesHelper.appNames.add(itemId + "|shortcut|" + newName);
                            Utils.saveToSettings(Common.LAUNCHER_CONTEXT, "appnames", PreferencesHelper.appNames);
                        }

                        editNameDialog.dismiss();
                    }
                });

                editNameView.findViewById(R.id.edit_app_name_restore).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PackageManager pm = Common.LAUNCHER_CONTEXT.getPackageManager();
                        Intent intent = (Intent) callMethod(tag, "getIntent");
                        String title = null;
                        String itemId = String.valueOf(getLongField(tag, Fields.iiID));

                        if (intent != null && intent.getComponent() != null) {
                            title = String.valueOf(pm.resolveActivity(intent, 0).activityInfo.loadLabel(pm));
                        }

                        if (!TextUtils.isEmpty(title)) {
                            setObjectField(tag, "title", title);
                            ((TextView) longPressedItem).setText(title);
                        }

                        Iterator<String> it = PreferencesHelper.appNames.iterator();
                        while (it.hasNext()) {
                            String next = it.next();
                            String[] split = next.split("\\|");
                            if (split[0].equals(itemId)) {
                                it.remove();
                                break;
                            }
                        }

                        Utils.saveToSettings(Common.LAUNCHER_CONTEXT, "appnames", PreferencesHelper.appNames);
                        editNameDialog.dismiss();
                    }
                });

                String title = curName;
                PackageManager pm = Common.LAUNCHER_CONTEXT.getPackageManager();
                Intent intent = (Intent) callMethod(tag, "getIntent");
                if (intent != null
                        && intent.getComponent() != null) {
                    title = String.valueOf(pm.resolveActivity(intent, 0).activityInfo.loadLabel(pm));
                }

                editNameDialog.setTitle(title);
                editNameDialog.show();
            }
        });
        show = !isFolder && !isWidget;
        editName.setVisibility(show ? View.VISIBLE : View.GONE);

        ImageView remove = (ImageView) contextMenuHolder.findViewById(R.id.remove);
        Utils.setDrawableSelector(remove);
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeAndRemove();

                try {
                    Animation animation = AnimationUtils.loadAnimation(Common.XGELSCONTEXT, R.anim.delete_item_anim);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) { }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            ((ViewGroup) longPressedItem.getParent()).post(new Runnable() {
                                @Override
                                public void run() {
                                    ((ViewGroup) longPressedItem.getParent()).removeView(longPressedItem);

                                    if (Common.IS_KK_TREBUCHET) {
                                        callStaticMethod(Classes.LauncherModel, "deleteItemFromDatabase", Common.LAUNCHER_INSTANCE, longPressedItem.getTag());
                                    } else if (Common.IS_PRE_GNL_4) {
                                        callStaticMethod(Classes.LauncherModel, Methods.lmDeleteItemsFromDatabase, Common.LAUNCHER_INSTANCE, longPressedItem.getTag());
                                    } else {
                                        ArrayList array = new ArrayList();
                                        array.add(longPressedItem.getTag());
                                        callStaticMethod(Classes.LauncherModel, Methods.lmDeleteItemsFromDatabase, Common.LAUNCHER_CONTEXT, array);
                                    }
                                }
                            });
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) { }
                    });
                    longPressedItem.startAnimation(animation);
                } catch(Exception e) { }
            }
        });

        // TODO: widgets need more than deleteItemFromDatabase, defer this for later
        // http://androidxref.com/4.4.2_r1/xref/packages/apps/Launcher3/src/com/android/launcher3/DeleteDropTarget.java#327
        show = isWidget || isFolder;
        remove.setVisibility(show ? View.GONE : View.VISIBLE);

        ImageView manageFolder = (ImageView) contextMenuHolder.findViewById(R.id.managefolder);
        Utils.setDrawableSelector(manageFolder);
        manageFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeContextMenu();

                Common.CURRENT_CONTEXT_MENU_ITEM = longPressedItem;
                ArrayList<String> items = new ArrayList<String>();
                Object mFolder = getObjectField(longPressedItem, Fields.fiFolder);
                ArrayList<View> folderItems = (ArrayList<View>) callMethod(mFolder, Methods.fGetItemsInReadingOrder);

                for (View item : folderItems) {
                    items.add(((Intent) callMethod(item.getTag(), "getIntent")).getComponent().flattenToString());
                }

                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                intent.setComponent(new ComponentName(Common.PACKAGE_NAME, AllAppsList.class.getName()));
                intent.putExtra("mode", AllAppsList.MODE_SELECT_FOLDER_APPS);
                intent.putExtra("foldername", ((TextView) getObjectField(longPressedItem, Fields.fiFolderName)).getText());
                intent.putExtra("itemid", getLongField(tag, Fields.iiID));
                intent.putExtra("homescreen", true);
                intent.putStringArrayListExtra("items", items);
                Common.LAUNCHER_CONTEXT.startActivity(intent);
            }
        });
        show = isFolder;
        manageFolder.setVisibility(show ? View.VISIBLE : View.GONE);

        ImageView sort = (ImageView) contextMenuHolder.findViewById(R.id.sortapps);
        Utils.setDrawableSelector(sort);
        sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeAndRemove();

                Object folder = getObjectField(longPressedItem, Fields.fiFolder);
                ArrayList tagList = new ArrayList();
                ArrayList<View> apps = (ArrayList<View>) callMethod(folder, Methods.fGetItemsInReadingOrder);
                for (View app : apps) {
                    tagList.add(app.getTag());
                }
                callMethod(folder, "h", tagList);
                callMethod(folder, "aU", 6);

            }
        });
        show = false;
        sort.setVisibility(show ? View.VISIBLE : View.GONE);

        ImageView iconPicker = (ImageView) contextMenuHolder.findViewById(R.id.settings);
        Utils.setDrawableSelector(iconPicker);
        iconPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeContextMenu();
                Common.CURRENT_CONTEXT_MENU_ITEM = longPressedItem;

                if (!isPremium) {
                    Utils.showPremiumOnly();
                    return;
                }

                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                intent.setComponent(new ComponentName(Common.PACKAGE_NAME, FragmentSelectiveIcon.class.getName()));
                intent.putExtra("mode", isFolder ? FragmentSelectiveIcon.MODE_PICK_FOLDER_ICON : FragmentSelectiveIcon.MODE_PICK_SHORTCUT_ICON);
                intent.putExtra("name", getObjectField(tag, "title").toString());
                intent.putExtra("itemtid", getLongField(tag, Fields.iiID));
                Common.LAUNCHER_CONTEXT.startActivity(intent);
            }
        });
        show = isWidget;
        iconPicker.setVisibility(show ? View.GONE : View.VISIBLE);

        ImageView resize = (ImageView) contextMenuHolder.findViewById(R.id.resize);
        Utils.setDrawableSelector(remove);
        resize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeAndRemove();

                setAdditionalInstanceField(longPressedItem, "resize", true);

                if (Common.PACKAGE_OBFUSCATED && Common.GNL_PACKAGE_INFO.versionCode < ObfuscationHelper.GNL_4_2_16) {
                    callMethod(getDragLayer(), Methods.dlAddResizeFrame, longPressedItem, longPressedItem.getParent().getParent());
                } else {
                    callMethod(getDragLayer(), Methods.dlAddResizeFrame, longPressedItem.getTag(), longPressedItem, longPressedItem.getParent().getParent());
                }
            }
        });
        show = isWidget;
        resize.setVisibility(show ? View.VISIBLE : View.GONE);

        ImageView layerUp = (ImageView) contextMenuHolder.findViewById(R.id.layerup);
        Utils.setDrawableSelector(layerUp);
        layerUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeAndRemove();
                longPressedItem.bringToFront();

                boolean dirty = false;
                long id = getLongField(tag, Fields.iiID);
                ArrayList<String> positions = new ArrayList<String>(PreferencesHelper.layerPositions);

                for (String pos : positions) {
                    log("Saved " + pos);
                }

                ViewGroup shortcutAndWidgetsContainer = (ViewGroup) longPressedItem.getParent();
                for (int j = 0; j < shortcutAndWidgetsContainer.getChildCount(); j++) {
                    View child = shortcutAndWidgetsContainer.getChildAt(j);
                    log("Child " + child.getTag());
                }

                for (int j = 0; j < shortcutAndWidgetsContainer.getChildCount(); j++) {
                    View child = shortcutAndWidgetsContainer.getChildAt(j);
                    long childId = getLongField(child.getTag(), Fields.iiID);
                    if (childId != id && Utils.isIntersecting(longPressedItem)) {
                        positions.remove("" + childId);
                        positions.remove("" + id);
                        positions.add("" + id);
                        dirty = true;
                        log("Layer up Removed " + childId);
                        log("Layer up Added " + id);
                    }
                }

                if (dirty) {
                    savePositionInLayer(positions);
                }
            }
        });
        show = (PreferencesHelper.overlappingWidgets && isWidget) || Utils.isIntersecting(longPressedItem);
        layerUp.setVisibility(show ? View.VISIBLE : View.GONE);

        ImageView layerDown = (ImageView) contextMenuHolder.findViewById(R.id.layerdown);
        Utils.setDrawableSelector(layerDown);
        layerDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeAndRemove();

                boolean dirty = false;
                long id = getLongField(tag, Fields.iiID);
                ArrayList<String> positions = new ArrayList<String>(PreferencesHelper.layerPositions);

                for (String pos : positions) {
                    log("Saved " + pos);
                }

                ViewGroup shortcutAndWidgetsContainer = (ViewGroup) longPressedItem.getParent();

                for (int j = 0; j < shortcutAndWidgetsContainer.getChildCount(); j++) {
                    View child = shortcutAndWidgetsContainer.getChildAt(j);
                    log("Child " + child.getTag());
                }

                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < shortcutAndWidgetsContainer.getChildCount(); j++) {
                        View child = shortcutAndWidgetsContainer.getChildAt(j);
                        long childId = getLongField(child.getTag(), Fields.iiID);
                        if (intersects(child) && childId != id) {
                            child.bringToFront();
                            positions.remove("" + id);
                            positions.remove("" + childId);
                            positions.add("" + childId);
                            dirty = true;
                            log("Layer down Removed " + id);
                            log("Layer down Added " + childId);
                        }
                    }
                }

                if (dirty) {
                    savePositionInLayer(positions);
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
        show = (PreferencesHelper.overlappingWidgets && isWidget) || isIntersecting(longPressedItem);
        layerDown.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private static void savePositionInLayer(ArrayList<String> positions) {
        Utils.saveToSettings(Common.LAUNCHER_CONTEXT, "layerpositions", positions);
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
                Intent i = (Intent) callMethod(tag, "getIntent");
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

        // SYSTEM - 0x1
        // FLAG_UPDATED_SYSTEM_APP - 0x80
        return (flags & 0x81) > 0;
    }

    private static ViewGroup getDragLayer() {
        return Common.DRAG_LAYER;
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
                contextMenu.bringToFront();
                isOpen = true;
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
                    isAnimating = isOpen = false;
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

    private static boolean isMode(int mode) {
        return PreferencesHelper.contextmenuMode == mode;
    }
}