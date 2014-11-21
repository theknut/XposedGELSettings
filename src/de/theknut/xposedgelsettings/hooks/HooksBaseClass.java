package de.theknut.xposedgelsettings.hooks;

import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.theknut.xposedgelsettings.ui.InAppPurchase;

public class HooksBaseClass extends XC_MethodHook {

    protected static boolean DEBUG = PreferencesHelper.Debug;

    public static void log(String msg) {
        String timestamp = new SimpleDateFormat("HH:mm:ss.SSS").format(Calendar.getInstance().getTime());
        XposedBridge.log(timestamp + " XGELS| " + msg);
    }

    public static void log(MethodHookParam param, String msg) {
        String timestamp = new SimpleDateFormat("HH:mm:ss.SSS").format(Calendar.getInstance().getTime());
        XposedBridge.log(timestamp + " XGELS| " + param.method.getName() + ": " + msg);
    }

    public static boolean checkPremium() {
        try {
            return InAppPurchase.checkPremium(Common.LAUNCHER_INSTANCE);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        }
    }
}