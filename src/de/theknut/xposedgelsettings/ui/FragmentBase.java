package de.theknut.xposedgelsettings.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import java.util.Arrays;
import java.util.List;

import de.theknut.xposedgelsettings.R;
import de.theknut.xposedgelsettings.hooks.Common;

@SuppressLint("WorldReadableFiles")
public class FragmentBase extends PreferenceFragment {

    public static Context mContext;
    public static Activity mActivity;
    public static boolean toastShown = false;
    public static boolean alertShown = false;
    public static boolean alertAnswerKill = false;
    public static String TAG = "XGELS";
    public static SharedPreferences sharedPrefs;

    OnPreferenceChangeListener onChangeListenerLauncherReboot = new Preference.OnPreferenceChangeListener() {

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {

            if (!toastShown) {
                Toast.makeText(mContext, R.string.toast_reboot, Toast.LENGTH_LONG).show();
                toastShown = true;
            }

            return true;
        }
    };

    OnPreferenceChangeListener onChangeListenerFullReboot = new Preference.OnPreferenceChangeListener() {

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {

            if (!toastShown) {
                Toast.makeText(mContext, R.string.toast_full_reboot, Toast.LENGTH_LONG).show();
                toastShown = true;
            }

            CommonUI.needFullReboot = true;
            return true;
        }
    };

    OnSharedPreferenceChangeListener onChangeListenerKillLauncher = new OnSharedPreferenceChangeListener() {

        List<String> keys = Arrays.asList("1", "2", "PREFS_VERSION_KEY", "dontshowkilldialog", "autokilllauncher", "dontshowgoogleplaydialog", "debug", "premiumlistpref");

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            sharedPreferences.edit().commit();
            if (keys.contains(key)) return;

            if (!sharedPreferences.getBoolean("dontshowkilldialog", false) && !alertShown) {

                LayoutInflater adbInflater = LayoutInflater.from(mContext);
                View dontShowAgainLayout = adbInflater.inflate(R.layout.dialog_with_checkbox, null);
                ((TextView) dontShowAgainLayout.findViewById(R.id.message)).setText(R.string.alert_autokill_summary);
                final CheckBox dontShowAgain = (CheckBox) dontShowAgainLayout.findViewById(R.id.skip);
                dontShowAgain.setIncludeFontPadding(false);
                dontShowAgain.setText(R.string.alert_autokill_checkbox);

                new MaterialDialog.Builder(mActivity)
                        .theme(Theme.DARK)
                        .customView(dontShowAgainLayout)
                        .cancelable(false)
                        .title(R.string.alert_autokill_title)
                        .positiveText(R.string.alert_autokill_ok)
                        .negativeText(R.string.alert_autokill_cancel)
                        .callback(new MaterialDialog.Callback() {
                            @Override
                            public void onPositive(MaterialDialog materialDialog) {
                                if (dontShowAgain.isChecked()) {
                                    SharedPreferences settings = mContext.getSharedPreferences(Common.PREFERENCES_NAME, Context.MODE_WORLD_READABLE);
                                    SharedPreferences.Editor editor = settings.edit();
                                    editor.putBoolean("dontshowkilldialog", true).commit();
                                    editor.putBoolean("autokilllauncher", true).commit();
                                }

                                alertAnswerKill = true;
                                CommonUI.restartLauncher(false);
                            }

                            @Override
                            public void onNegative(MaterialDialog materialDialog) {
                                if (dontShowAgain.isChecked()) {
                                    SharedPreferences settings = mContext.getSharedPreferences(Common.PREFERENCES_NAME, Context.MODE_WORLD_READABLE);
                                    SharedPreferences.Editor editor = settings.edit();
                                    editor.putBoolean("dontshowkilldialog", true).commit();
                                    editor.putBoolean("autokilllauncher", false).commit();
                                }

                                materialDialog.dismiss();
                            }
                        })
                        .build()
                        .show();

                alertShown = true;
            }

            if (sharedPreferences.getBoolean("autokilllauncher", false) || alertAnswerKill) {
                CommonUI.restartLauncher(false);
            }
        }
    };

    @SuppressWarnings("deprecation")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getPreferenceManager().setSharedPreferencesMode(Context.MODE_WORLD_READABLE);
        sharedPrefs = getPreferenceManager().getSharedPreferences();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = mActivity = activity;
    }

    @Override
    public void onResume() {
        super.onResume();

        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(onChangeListenerKillLauncher);
        MainActivity.closeDrawer();
    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(onChangeListenerKillLauncher);

        super.onPause();
    }
}
