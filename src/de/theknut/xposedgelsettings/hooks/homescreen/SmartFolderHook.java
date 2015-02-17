package de.theknut.xposedgelsettings.hooks.homescreen;

import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.HooksBaseClass;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Fields;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Methods;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;
import de.theknut.xposedgelsettings.hooks.Utils;
import de.theknut.xposedgelsettings.hooks.gestures.GestureHelper;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.getBooleanField;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

public class SmartFolderHook extends HooksBaseClass {

    float downY, downX;

	@Override
	protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        if ((Boolean) callMethod(Common.LAUNCHER_INSTANCE, Methods.lIsAllAppsVisible)) {
            return;
        }

        MotionEvent ev = (MotionEvent) param.args[0];
        switch (ev.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                Common.FOLDER_GESTURE_ACTIVE = true;
                downY = ev.getRawY();
                downX = ev.getRawX();
                break;
            case MotionEvent.ACTION_CANCEL:
                Common.FOLDER_GESTURE_ACTIVE = false;
                break;
            case MotionEvent.ACTION_UP:
                if (getBooleanField(getObjectField(param.thisObject, Fields.fiLongPressHelper), Fields.clphHasPerformedLongPress)) {
                    return;
                }

                if (downY - ev.getRawY() <= GestureHelper.gestureDistance && downX - ev.getRawX() <= GestureHelper.gestureDistance) {
                    handleAction("SWIPE", param);
                } else {
                    handleAction("TOUCH", param);
                }

                Common.FOLDER_GESTURE_ACTIVE = false;
                break;
        }
	}

    public void handleAction(String action, MethodHookParam param) {
        // 1 = Swipe up - open folder / Touch - launch app
        // 2 = Swipe up - launch app / Touch - open folder

        if (action.equals("SWIPE")) {
            if (PreferencesHelper.smartFolderMode == 1) {
                openFolder(param);
            } else if (PreferencesHelper.smartFolderMode == 2) {
                launchFirstApp(param);
            }
        } else if (action.equals("TOUCH")) {
            if (PreferencesHelper.smartFolderMode == 1) {
                launchFirstApp(param);
            } else if (PreferencesHelper.smartFolderMode == 2) {
                openFolder(param);
            }
        }
    }

    public void openFolder(MethodHookParam param) {
        callMethod(Common.LAUNCHER_INSTANCE, Methods.lOpenFolder, param.thisObject);
    }

    public void launchFirstApp(MethodHookParam param) {
        View firstItemInFolder = (View) ((ArrayList) callMethod(
                getObjectField(param.thisObject, Fields.fiFolder),
                Methods.fGetItemsInReadingOrder))
                .get(0);

        Utils.startActivity((Intent) callMethod(firstItemInFolder.getTag(), "getIntent"));
        param.setResult(true);
    }
}
