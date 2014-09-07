package de.theknut.xposedgelsettings.hooks.notificationbadges;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.util.ArrayList;

import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.HooksBaseClass;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Methods;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;

import static de.robv.android.xposed.XposedHelpers.callMethod;

public class NotificationBadgesHelper extends HooksBaseClass {

    static PackageManager pm;
    static ActivityManager activityManager;

    public static int displayWidth = -1, displayHeigth = -1;
    public static int measuredWidth = -1, measuredHeigth = -1;
    public static int leftRightPadding, topBottomPadding;
    public static int frameSize;

    static ArrayList<PendingNotification> pendingNotifications = new ArrayList<PendingNotification>();

    static BroadcastReceiver notificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Intent i = intent;
            String action = i.getAction();

            if (DEBUG) log("Received Intent: " + action);

            if (action.equals(Common.MISSEDIT_CALL_NOTIFICATION)) {
                if (i.hasExtra("COUNT")) {
                    handleMissedCalls(i.getIntExtra("COUNT", 0));
                }
            } else if (action.equals(Common.MISSEDIT_SMS_NOTIFICATION)) {
                if (i.hasExtra("COUNT")) {
                    handleUnreadSMS(i.getIntExtra("COUNT", 0));
                }
            } else if (action.equals(Common.MISSEDIT_GMAIL_NOTIFICATION)) {
                if (DEBUG) log("GMAIL NOTIFICATION " + i.getStringExtra("ACCOUNT"));
                requestCounters();
            } else if (action.equals(Common.MISSEDIT_APP_NOTIFICATION)) {
                if (DEBUG) log("APP NOTIFICATION " + i.getStringExtra("COMPONENTNAME"));

                if (i.hasExtra("COMPONENTNAME")) {
                    addPendingNotification(
                            new PendingNotification(
                                    ComponentName.unflattenFromString(i.getStringExtra("COMPONENTNAME")),
                                    i.getIntExtra("COUNT", 0)
                            )
                    );
                }
            } else if (action.equals(Common.MISSEDIT_COUNTERS_STATUS)) {
                Bundle bundles = i.getBundleExtra("MISSED_CALLS");

                if (bundles != null) {
                    handleMissedCalls(bundles.getInt("COUNT"));
                } else {
                    if (DEBUG) log("No MISSED_CALLS");
                }

                bundles = i.getBundleExtra("UNREAD_SMS");

                if (bundles != null) {
                    handleUnreadSMS(bundles.getInt("COUNT"));
                } else {
                    if (DEBUG) log("No UNREAD_SMS");
                }

                bundles = i.getBundleExtra("PENDING_VOICEMAILS");

                if (bundles != null) {
                    //handleUnreadSMS(bundles.getInt("COUNT"));
                } else {
                    if (DEBUG) log("No PENDING_VOICEMAILS ");
                }

                handleEMailBundles(i, "GMAIL_ACCOUNTS", ComponentName.unflattenFromString("com.google.android.gm/com.google.android.gm.ConversationListActivityGmail"));
                handleEMailBundles(i, "K9MAIL_ACCOUNTS", ComponentName.unflattenFromString("com.fsck.k9/com.fsck.k9.activity.Accounts"));
                handleEMailBundles(i, "AQUAMAIL_ACCOUNTS", ComponentName.unflattenFromString("org.kman.AquaMail/org.kman.AquaMail.ui.AccountListActivity"));

                bundles = i.getBundleExtra("APPLICATIONS");

                if (bundles != null) {
                    for (int j = 0; j < bundles.size(); j++) {
                        Bundle bundle = bundles.getBundle("" + j);
                        if (bundle != null) {
                            if (bundle.containsKey("COMPONENTNAME")) {
                                addPendingNotification(
                                        new PendingNotification(
                                                ComponentName.unflattenFromString(bundle.getString("COMPONENTNAME")),
                                                bundle.getInt("COUNT", 0)
                                        )
                                );
                            }
                        }
                    }
                } else {
                    if (DEBUG) log("No APPLICATIONS");
                }
            } else if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
                requestCounters();
            }

            invalidate();
        }

        private void invalidate() {
            new AsyncTask<Void, Void, Void>(){
                @Override
                protected Void doInBackground(Void... params) {
                    ArrayList cellLayouts = (ArrayList) callMethod(Common.WORKSPACE_INSTANCE, Methods.wGetWorkspaceAndHotseatCellLayouts);
                    for (Object layoutParent : cellLayouts) {
                        ViewGroup layout = (ViewGroup) callMethod(layoutParent, Methods.clGetShortcutsAndWidgets);
                        int childCount = layout.getChildCount();
                        for (int i = 0; i < childCount; ++i) {
                            layout.getChildAt(i).postInvalidate();
                        }
                    }
                    return null;
                }
            }.execute();
        }

        void handleEMailBundles(Intent intent, String bundleName, ComponentName componentName) {

            Bundle bundles = intent.getBundleExtra(bundleName);

            if (bundles != null) {
                int totalCnt = 0;
                for (int j = 0; j < bundles.size(); j++) {

                    Bundle bundle = bundles.getBundle("" + j);
                    if (bundle != null) {
                        totalCnt += bundle.getInt("COUNT", 0);
                    }
                }
                addPendingNotification(new PendingNotification(componentName, totalCnt));
            } else {
                if (DEBUG) log("No " + bundleName);
            }
        }

        void handleUnreadSMS(int cnt) {

            if (pm == null) pm = Common.LAUNCHER_CONTEXT.getPackageManager();

            try {
                String packageName = PreferencesHelper.notificationSMSApp;
                ResolveInfo mInfo;

                if (TextUtils.isEmpty(packageName)) {
                    Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                    smsIntent.setType("vnd.android-dir/mms-sms");
                    mInfo = pm.resolveActivity(smsIntent, 0);
                    packageName = mInfo.activityInfo.packageName;

                    if (packageName.equals("android")) {
                        throw new Exception();
                    }
                } else {
                    mInfo = pm.resolveActivity(pm.getLaunchIntentForPackage(packageName), 0);
                }

                addPendingNotification(new PendingNotification(mInfo, cnt));
            } catch (Exception ex) {
                makeNotification("XGELS can't determine your default SMS app. Please configure it manually in the \"Notification badges\" - \"Advanced\" section. Click here to open XGELS!");

                log("Couldn't resolve default sms app. Use advanced settings in notification badges menu.");
                log("Show this to the dev: " + ex);
            }
        }

        void handleMissedCalls(int cnt) {

            if (pm == null) pm = Common.LAUNCHER_CONTEXT.getPackageManager();

            try {
                String packageName = PreferencesHelper.notificationDialerApp;
                ResolveInfo mInfo;

                if (TextUtils.isEmpty(packageName)) {
                    mInfo = pm.resolveActivity(new Intent(Intent.ACTION_DIAL) , 0);
                    packageName = mInfo.activityInfo.packageName;

                    if (packageName.equals("android")) {
                        throw new Exception();
                    }
                } else {
                    mInfo = pm.resolveActivity(pm.getLaunchIntentForPackage(packageName), 0);
                }

                addPendingNotification(new PendingNotification(mInfo, cnt));

            } catch (Exception ex) {
                makeNotification("XGELS can't determine your default Dialer app. Please configure it manually in the \"Notification badges\" - \"Advanced\" section. Click here to open XGELS!");

                log("Couldn't resolve default caller app. Use advanced settings in notification badges menu.");
                log("Show this to the dev: " + ex);
            }
        }

        void makeNotification(String msg) {
            PackageManager pm = Common.LAUNCHER_CONTEXT.getPackageManager();
            Intent LaunchIntent = pm.getLaunchIntentForPackage(Common.PACKAGE_NAME);
            LaunchIntent.putExtra("fragment", "badges");
            PendingIntent pInstallTab = PendingIntent.getActivity(Common.LAUNCHER_CONTEXT, 0xB00B5, LaunchIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.BigTextStyle notiStyle = new NotificationCompat.BigTextStyle();
            notiStyle.setBigContentTitle("Oh snap!");
            notiStyle.bigText(msg);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(Common.LAUNCHER_CONTEXT)
                    .setContentTitle("Oh snap!")
                    .setContentText(msg)
                    .setTicker("Something went wrong :-\\")
                    .setContentIntent(pInstallTab)
                    .setAutoCancel(true)
                    .setStyle(notiStyle)
                    .setSmallIcon(android.R.drawable.ic_dialog_alert);

            ((NotificationManager) Common.LAUNCHER_CONTEXT.getSystemService(Context.NOTIFICATION_SERVICE)).notify(null, 0, builder.build());
        }

        void addPendingNotification(PendingNotification pn) {
            int idx = pendingNotifications.indexOf(pn);
            if (idx != -1) {
                if (pn.getCount() == 0) {
                    log("remove " + pn);
                    pendingNotifications.remove(idx);
                } else {
                    log("update " + pn);
                    pendingNotifications.get(idx).setCount(pn.getCount());
                }
            } else {
                log("add " + pn);
                pendingNotifications.add(pn);
            }
        }
    };

    protected static void requestCounters() {
        // start or call MissedIt service in order to receive notification intents
        Common.LAUNCHER_CONTEXT.startService(new Intent(Common.MISSEDIT_REQUESET_COUNTERS));
    }

    protected static void initMeasures() {
        long time = System.currentTimeMillis();
        //if (displayWidth == -1) {
        WindowManager wm = (WindowManager) Common.LAUNCHER_CONTEXT.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        displayWidth = size.x;
        displayHeigth = size.y;

        DisplayMetrics displayMetrics = Common.LAUNCHER_CONTEXT.getResources().getDisplayMetrics();

        measuredWidth = MeasureSpec.makeMeasureSpec(displayWidth, MeasureSpec.AT_MOST);
        measuredHeigth = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);

        leftRightPadding = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, PreferencesHelper.notificationBadgeLeftRightPadding, displayMetrics));
        topBottomPadding = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, PreferencesHelper.notificationBadgeTopBottomPadding, displayMetrics));

        frameSize = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, PreferencesHelper.notificationBadgeFrameSize, displayMetrics));

        if (DEBUG) log("InitMeasures - width: " + displayWidth +" height: " + displayHeigth + " took " + (System.currentTimeMillis() - time) + "ms");
    }
}