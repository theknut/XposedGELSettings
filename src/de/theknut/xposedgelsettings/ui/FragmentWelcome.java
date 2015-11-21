package de.theknut.xposedgelsettings.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import de.theknut.xposedgelsettings.R;
import de.theknut.xposedgelsettings.hooks.Common;
import eu.janmuller.android.simplecropimage.CropImage;

@SuppressLint("WorldReadableFiles")
public class FragmentWelcome extends FragmentBase {

    static boolean shown = false;
    MaterialDialog IsXposedInstalledAlert, IsModuleActive, IsInstalledFromPlayStore, IsSupportedLauncherInstalled, NeedReboot, LuckyPatcher;
    List<MaterialDialog> alerts;
    int alertToShow = 0;
    boolean cancel;
    View rootView;

    private static int REQUEST_PICK_PICTURE = 10;
    private static int REQUEST_CROP_PICTURE = 11;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        rootView = inflater.inflate(R.layout.welcome, container, false);

        /*rootView.findViewById(R.id.madeingermany).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getPointerCount() == 2) {
                    if (InAppPurchase.checkFreedom()) return false;
                    try {
                        InAppPurchase.purchaseSpecialOffer();
                    } catch (Exception e) {}
                }
                return true;
            }
        });*/

        rootView.findViewById(R.id.welcometext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                //startActivityForResult(photoPickerIntent, REQUEST_PICK_PICTURE);
            }
        });

        return CommonUI.setBackground(rootView, R.id.welcomebackground);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_PICK_PICTURE) {
            InputStream imageStream;
            Bitmap yourSelectedImage = null;

            try {
                imageStream = mActivity.getContentResolver().openInputStream(data.getData());
                yourSelectedImage = BitmapFactory.decodeStream(imageStream);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            FileOutputStream out = null;
            File file = new File("/mnt/sdcard/XposedGELSettings/icons/test.png");
            file.getParentFile().mkdirs();
            try {
                out = new FileOutputStream(file);
                yourSelectedImage.compress(Bitmap.CompressFormat.PNG, 100, out);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.flush();
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // create explicit intent
            Intent intent = new Intent(mContext, CropImage.class);
            intent.putExtra(CropImage.IMAGE_PATH, file.getAbsolutePath());
            intent.putExtra(CropImage.SCALE, true);
            intent.putExtra(CropImage.ASPECT_X, 1);
            intent.putExtra(CropImage.ASPECT_Y, 1);
            intent.putExtra(CropImage.OUTPUT_X, 192);
            intent.putExtra(CropImage.OUTPUT_Y, 192);
            startActivityForResult(intent, REQUEST_CROP_PICTURE);
        } else if (resultCode == Activity.RESULT_OK && requestCode == 1 && data != null) {
            Uri uri = data.getData();

            if (uri != null) {
                Cursor c = null;
                try {
                    c = mContext.getContentResolver().query(uri, new String[]{
                                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                                    ContactsContract.CommonDataKinds.Phone.TYPE },
                            null, null, null);

                    if (c != null && c.moveToFirst()) {
                        String number = c.getString(0);
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + number));
                        mContext.startActivity(intent);
                    }
                } finally {
                    if (c != null) {
                        c.close();
                    }
                }
            }
        }
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onResume() {
        super.onResume();

        if (!shown) {
            shown = true;

            createAlertDialogs();
            alerts = new ArrayList<>();

            if (!isXposedInstalled()) {
                alerts.add(IsXposedInstalledAlert);
                new ShowAlertsAsyncTask().execute();
                return;
            }

            if (!isSupportedLauncherInstalled()) {
                alerts.add(IsSupportedLauncherInstalled);
            }

            if (!isXGELSActive()) {
                alerts.add(IsModuleActive);
            }

            if (hasLuckyPatcher()) {
                int lpcnt = mContext.getSharedPreferences(Common.PREFERENCES_NAME, Context.MODE_WORLD_READABLE).getInt("lpcnt", 3);
                if (lpcnt > 0) {
                    mContext.getSharedPreferences(Common.PREFERENCES_NAME, Context.MODE_WORLD_READABLE).edit().putInt("lpcnt", lpcnt - 1).apply();
                    String plural = lpcnt == 1 ? "" : "s";
                    LuckyPatcher = new MaterialDialog.Builder(mActivity)
                            .theme(Theme.DARK)
                            .title("Lucky Patcher detected")
                            .content("Lucky Patcher is installed on your device. This will break the Google InApp-Billing service and therefore your purchase can NOT be verified nor can you purchase XGELS Premium. Please disable \"Billing emulation\" and uninstall Lucky Patcher. If the issue persists flash your ROM and GAPPS again.\n\nThis dialog will be shown " + lpcnt + " more time" + plural + ".")
                            .positiveText(android.R.string.ok)
                            .callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog materialDialog) {
                                    materialDialog.dismiss();
                                }
                            })
                            .build();
                }
                alerts.add(LuckyPatcher);
            }

            SharedPreferences settings = mContext.getSharedPreferences(Common.PREFERENCES_NAME, Context.MODE_WORLD_READABLE);
            if (!isInstalledFromPlay()) {
                if (!settings.getBoolean("dontshowgoogleplaydialog", false)) {
                    alerts.add(IsInstalledFromPlayStore);
                }
            }

            Changelog cl = new Changelog(mContext);
            if (cl.firstRun()) {
                CommonUI.needFullReboot = true;
                alerts.add(cl.getFullLogDialog());
                getFragmentManager().beginTransaction().replace(R.id.content_frame, new FragmentReverseEngineering()).commit();
            }

            if (alerts.size() != 0) {
                new ShowAlertsAsyncTask().execute();
            }
        }
        rootView.findViewById(R.id.welcometext).startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.welcome_anim));
    }

    // this method gets replaced by the module and returns true
    public boolean isXGELSActive() {
        return false;
    }

    public boolean isSupportedLauncherInstalled() {
        PackageManager pm = mContext.getPackageManager();

        try {
            pm.getPackageInfo("com.google.android.launcher", PackageManager.GET_ACTIVITIES);
            return true;
        }
        catch (PackageManager.NameNotFoundException e) { }

        try {
            pm.getPackageInfo(Common.TREBUCHET_PACKAGE, PackageManager.GET_ACTIVITIES);
            return true;
        }
        catch (PackageManager.NameNotFoundException e) { }

        try {
            pm.getPackageInfo("com.android.launcher3", PackageManager.GET_ACTIVITIES);
            return true;
        }
        catch (PackageManager.NameNotFoundException e) { }

        return false;
    }

    private boolean isXposedInstalled() {
        try {
            mContext.getPackageManager().getPackageInfo("de.robv.android.xposed.installer", PackageManager.GET_ACTIVITIES);
            return true;
        }
        catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private boolean isInstalledFromPlay() {
        String installer = mContext.getPackageManager().getInstallerPackageName("de.theknut.xposedgelsettings");

        if (installer == null) {
            return false;
        }
        else {
            return installer.equals("com.android.vending");
        }
    }

    private boolean hasLuckyPatcher() {
        try {
            mContext.getPackageManager().getPackageInfo("com.android.protips", PackageManager.GET_ACTIVITIES);
            return true;
        }
        catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void createAlertDialogs() {
        IsXposedInstalledAlert = new MaterialDialog.Builder(mActivity)
                .theme(Theme.DARK)
                .cancelable(false)
                .title(getString(R.string.missing_framework))
                .content(getString(R.string.missing_framework_msg))
                .positiveText(getString(R.string.go_to_framework))
                .negativeText("Exit")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog materialDialog) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://forum.xda-developers.com/xposed/xposed-installer-versions-changelog-t2714053"));
                        startActivity(browserIntent);
                        shown = false;
                        getActivity().finish();
                    }

                    @Override
                    public void onNegative(MaterialDialog materialDialog) {
                        shown = false;
                        getActivity().finish();
                    }
                })
                .build();

        IsModuleActive = new MaterialDialog.Builder(mActivity)
                .theme(Theme.DARK)
                .cancelable(false)
                .title(getString(R.string.module_not_active))
                .content(getString(R.string.module_not_active_msg))
                .positiveText(getString(R.string.open_xposed_installer))
                .negativeText(getString(R.string.continue_text))
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog materialDialog) {
                        Intent LaunchIntent = null;

                        try {
                            LaunchIntent = mContext.getPackageManager().getLaunchIntentForPackage("de.robv.android.xposed.installer");
                            if (LaunchIntent == null) {
                                Toast.makeText(mContext, R.string.toast_openinstaller_failed, Toast.LENGTH_LONG).show();
                            } else {
                                Intent intent = new Intent("de.robv.android.xposed.installer.OPEN_SECTION");
                                intent.setPackage("de.robv.android.xposed.installer");
                                intent.putExtra("section", "modules");
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                mContext.startActivity(intent);
                            }
                        } catch (Exception e) {
                            if (LaunchIntent != null) {
                                LaunchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                mContext.startActivity(LaunchIntent);
                            } else {
                                e.printStackTrace();
                                Toast.makeText(mContext, getString(R.string.active_manually), Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onNegative(MaterialDialog materialDialog) {
                        materialDialog.dismiss();
                    }
                })
                .build();

        LayoutInflater adbInflater = LayoutInflater.from(mContext);
        View dontShowAgainLayout = adbInflater.inflate(R.layout.dialog_with_checkbox, null);
        ((TextView) dontShowAgainLayout.findViewById(R.id.message)).setText(R.string.module_not_from_google_play_msg);
        final CheckBox dontShowAgain = (CheckBox) dontShowAgainLayout.findViewById(R.id.skip);
        dontShowAgain.setIncludeFontPadding(false);
        dontShowAgain.setText(getString(R.string.dont_show_again));

        IsInstalledFromPlayStore = new MaterialDialog.Builder(mActivity)
                .theme(Theme.DARK)
                .customView(dontShowAgainLayout, true)
                .cancelable(false)
                .title(getString(R.string.module_not_from_google_play))
                .positiveText(getString(R.string.open_play_store))
                .negativeText(getString(R.string.continue_text))
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog materialDialog) {
                        if (dontShowAgain.isChecked()) {
                            SharedPreferences settings = mContext.getSharedPreferences(Common.PREFERENCES_NAME, 0);
                            settings.edit().putBoolean("dontshowgoogleplaydialog", true).apply();
                        }

                        final String appPackageName = Common.PACKAGE_NAME;
                        try {
                            CommonUI.ACTIVITY.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            CommonUI.ACTIVITY.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                    }

                    @Override
                    public void onNegative(MaterialDialog materialDialog) {
                        if (dontShowAgain.isChecked()) {
                            SharedPreferences settings = mContext.getSharedPreferences(Common.PREFERENCES_NAME, 0);
                            settings.edit().putBoolean("dontshowgoogleplaydialog", true).apply();
                        }

                        materialDialog.dismiss();
                    }
                }).build();

        NeedReboot = new MaterialDialog.Builder(mActivity)
                .theme(Theme.DARK)
                .cancelable(false)
                .title(R.string.alert_xgels_updated_title)
                .content(R.string.alert_xgels_updated_summary)
                .positiveText(R.string.full_reboot)
                .negativeText(R.string.alert_xgels_updated_cancel)
                .neutralText(R.string.hot_reboot)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onNeutral(MaterialDialog materialDialog) {
                        CommonUI.openRootShell(new String[]{ "su", "-c", "killall system_server"});
                        cancel = true;
                    }

                    @Override
                    public void onPositive(MaterialDialog materialDialog) {
                        CommonUI.openRootShell(new String[]{"su", "-c", "reboot now"});
                        cancel = true;
                    }

                    @Override
                    public void onNegative(MaterialDialog materialDialog) {
                        materialDialog.dismiss();
                    }
                })
                .build();

        IsSupportedLauncherInstalled = new MaterialDialog.Builder(mActivity)
                .theme(Theme.DARK)
                .cancelable(false)
                .title(R.string.alert_launcher_not_installed_title)
                .content(R.string.alert_launcher_not_installed_summary)
                .positiveText(R.string.alert_launcher_not_installed_get_gnl)
                .negativeText(android.R.string.ok)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog materialDialog) {
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + Common.GEL_PACKAGE)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + Common.GEL_PACKAGE)));
                        }
                    }

                    @Override
                    public void onNegative(MaterialDialog materialDialog) {
                        materialDialog.dismiss();
                    }
                })
                .build();
    }

    private class ShowAlertsAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            try {
                if (cancel) {
                    this.cancel(true);
                }

                alerts.get(alertToShow).show();
            } catch (Exception e) {
                e.printStackTrace();
                this.cancel(true);
            }
        }

        @Override
        protected Void doInBackground(final Void... params) {

            while(alerts.get(alertToShow).isShowing()) {
                try { Thread.sleep(300); } catch (InterruptedException e) { e.printStackTrace(); }

                if (cancel) {
                    this.cancel(true);
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            alertToShow++;

            if (alertToShow < alerts.size()) {
                new ShowAlertsAsyncTask().execute();
            }
        }
    }
}