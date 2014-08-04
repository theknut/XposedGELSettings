package de.theknut.xposedgelsettings.hooks;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.widget.Toast;

import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.util.Calendar;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.theknut.xposedgelsettings.R;
import de.theknut.xposedgelsettings.ui.InAppPurchase;

import static de.robv.android.xposed.XposedHelpers.callMethod;

public class HooksBaseClass extends XC_MethodHook {
	
	protected static boolean DEBUG = PreferencesHelper.Debug;
	
	public static void log(String msg) {
		String timestamp = DateFormat.getTimeInstance(DateFormat.SHORT).format(Calendar.getInstance().getTime());
		XposedBridge.log(timestamp + " XGELS| " + msg);
	}
	
	public static void log(MethodHookParam param, String msg) {
		String timestamp = DateFormat.getTimeInstance(DateFormat.SHORT).format(Calendar.getInstance().getTime());
		XposedBridge.log(timestamp + " XGELS| " + param.method.getName() + ": " + msg);
	}

    public static void startActivity(Intent intent) {
        if (Common.GNL_VERSION >= ObfuscationHelper.GNL_3_5_14) {
            Resources res = Common.LAUNCHER_CONTEXT.getResources();
            int task_open_enter = res.getIdentifier("task_open_enter", "anim", Common.GEL_PACKAGE);
            int no_anim = res.getIdentifier("no_anim", "anim", Common.GEL_PACKAGE);
            callMethod(Common.LAUNCHER_INSTANCE, "startActivity", intent, ActivityOptions.makeCustomAnimation(Common.LAUNCHER_CONTEXT, task_open_enter, no_anim).toBundle());
        } else {
            callMethod(Common.LAUNCHER_INSTANCE, "startActivity", intent);
        }
    }

    public static void showPremiumOnly() {
        try {
            Context XGELSContext = Common.LAUNCHER_CONTEXT.createPackageContext(Common.PACKAGE_NAME, Context.CONTEXT_IGNORE_SECURITY);
            Toast.makeText(Common.LAUNCHER_CONTEXT, XGELSContext.getResources().getString(R.string.toast_donate_only), Toast.LENGTH_LONG).show();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static boolean checkPremium() {
        try {
            return InAppPurchase.checkPremium((Activity) Common.LAUNCHER_INSTANCE);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        }
    }
}