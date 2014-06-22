package de.theknut.xposedgelsettings.hooks.homescreen;

import android.app.ActivityOptions;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import de.robv.android.xposed.XC_MethodHook;

import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Fields;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Methods;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.getBooleanField;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

public class SmartFolderHook extends XC_MethodHook {

    float downY, downX;

	@Override
	protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
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
                Common.FOLDER_GESTURE_ACTIVE = false;
                if (getBooleanField(getObjectField(param.thisObject, Fields.fiLongPressHelper), Fields.clphHasPerformedLongPress)) {
                    return;
                }

                if (downY - ev.getRawY() <= 10.0 && downX - ev.getRawX() <= 10.0) {

                    View firstItemInFolder = (View) ((ArrayList) callMethod(
                            getObjectField(param.thisObject, Fields.fiFolder),
                            Methods.fGetItemsInReadingOrder))
                            .get(0);

                    ActivityOptions activityOptions = ActivityOptions.makeScaleUpAnimation(firstItemInFolder, 0, 0, firstItemInFolder.getMeasuredWidth(), firstItemInFolder.getMeasuredHeight());
                    Common.LAUNCHER_CONTEXT.startActivity((Intent) callMethod(firstItemInFolder.getTag(), "getIntent"), activityOptions.toBundle());
                    param.setResult(true);
                } else {
                    callMethod(Common.LAUNCHER_INSTANCE, Methods.lOpenFolder, param.thisObject);
                }
                break;
        }
	}
}
