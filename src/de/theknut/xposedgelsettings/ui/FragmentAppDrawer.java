package de.theknut.xposedgelsettings.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import de.theknut.xposedgelsettings.R;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper;
import de.theknut.xposedgelsettings.ui.preferences.MyPreferenceCategory;

public class FragmentAppDrawer extends FragmentBase {

    public FragmentAppDrawer() { }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.options_fragment, container, false);
        addPreferencesFromResource(R.xml.appdrawer_fragment);

        findPreference("gridsize").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                final ViewGroup numberPickerView = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.grid_number_picker, null);

                int minValue = 3, maxValue = 15;
                final NumberPicker nphc = (NumberPicker) numberPickerView.findViewById(R.id.numberPickerHorizontalColumn);
                nphc.setMinValue(minValue);
                nphc.setMaxValue(maxValue);
                nphc.setValue(Integer.parseInt(sharedPrefs.getString("xcountallappshorizontal", "" + 6)));

                final NumberPicker nphr = (NumberPicker) numberPickerView.findViewById(R.id.numberPickerHorizontalRow);
                nphr.setMinValue(minValue);
                nphr.setMaxValue(maxValue);
                nphr.setValue(Integer.parseInt(sharedPrefs.getString("ycountallappshorizontal", "" + 4)));

                final NumberPicker npvc = (NumberPicker) numberPickerView.findViewById(R.id.numberPickerVerticalColumn);
                npvc.setMinValue(minValue);
                npvc.setMaxValue(maxValue);
                npvc.setValue(Integer.parseInt(sharedPrefs.getString("xcountallapps", "" + 4)));

                final NumberPicker npvr = (NumberPicker) numberPickerView.findViewById(R.id.numberPickerVerticalRow);
                npvr.setMinValue(minValue);
                npvr.setMaxValue(maxValue);
                npvr.setValue(Integer.parseInt(sharedPrefs.getString("ycountallapps", "" + 5)));

                new MaterialDialog.Builder(mActivity)
                        .title(R.string.pref_grid_size_summary)
                        .customView(numberPickerView)
                        .cancelable(false)
                        .theme(Theme.DARK)
                        .positiveText(android.R.string.ok)
                        .callback(new MaterialDialog.SimpleCallback() {
                            @Override
                            public void onPositive(MaterialDialog materialDialog) {
                                // due to legacy reasons we need to save them as strings... -.-
                                sharedPrefs.edit()
                                        .putString("ycountallapps", "" + npvr.getValue())
                                        .putString("xcountallapps", "" + npvc.getValue())
                                        .putString("ycountallappshorizontal", "" + nphr.getValue())
                                        .putString("xcountallappshorizontal", "" + nphc.getValue())
                                        .apply();

                                materialDialog.dismiss();
                            }
                        })
                        .build()
                        .show();
                return true;
            }
        });

        findPreference("iconsizeappdrawer").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                final String[] values = getResources().getStringArray(R.array.iconsize_entries);
                final NumberPicker numberPicker = CommonUI.getNumberPicker(mContext, sharedPrefs, values, "iconsizeappdrawer", "100");
                new MaterialDialog.Builder(mActivity)
                        .title(R.string.pref_iconsize_title)
                        .customView(numberPicker)
                        .theme(Theme.DARK)
                        .positiveText(android.R.string.ok)
                        .callback(new MaterialDialog.SimpleCallback() {
                            @Override
                            public void onPositive(MaterialDialog materialDialog) {
                                // due to legacy reasons we need to save them as strings... -.-
                                sharedPrefs.edit()
                                        .remove("iconsizeappdrawer")
                                        .putString("iconsizeappdrawer", "" + values[numberPicker.getValue()])
                                        .apply();
                                materialDialog.dismiss();
                            }
                        })
                        .build()
                        .show();
                return true;
            }
        });

        findPreference("cleartabdata").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new MaterialDialog.Builder(mActivity)
                        .theme(Theme.DARK)
                        .title(getString(R.string.alert_appdrawer_clear_tabs_title))
                        .content(getString(R.string.alert_appdrawer_clear_tabs_summary))
                        .positiveText(android.R.string.ok)
                        .negativeText(android.R.string.cancel)
                        .callback(new MaterialDialog.Callback() {
                            @Override
                            public void onPositive(MaterialDialog materialDialog) {
                                SharedPreferences settings = mContext.getSharedPreferences(Common.PREFERENCES_NAME, 0);
                                SharedPreferences.Editor editor = settings.edit();
                                editor.remove("appdrawertabdata").apply();
                                Toast.makeText(mContext, getString(R.string.alert_appdrawer_clear_tabs_success), Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onNegative(MaterialDialog materialDialog) {
                                materialDialog.dismiss();
                            }
                        }).build().show();
                return true;
            }
        });

        findPreference("addfolder").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(Common.XGELS_ACTION_MODIFY_FOLDER);
                        intent.putExtra("setup", true);
                        mContext.sendBroadcast(intent);
                    }
                }, 2000);

                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);

                return true;
            }
        });

        findPreference("appdrawerswipetabs").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if ((Boolean) newValue) {
                    SharedPreferences.Editor editor = mContext.getSharedPreferences(Common.PREFERENCES_NAME, Context.MODE_WORLD_READABLE).edit();
                    editor.remove("continuousscrollwithappdrawer").apply();
                }
                return true;
            }
        });

        try {
            int version = mContext.getPackageManager().getPackageInfo(Common.GEL_PACKAGE, 0).versionCode;
            if (version < ObfuscationHelper.GNL_4_0_26) {
                MyPreferenceCategory cat = (MyPreferenceCategory) this.findPreference("settings");
                cat.removePreference(this.findPreference("appdrawerfolderstylebackgroundcolor"));
            } else if (version >= ObfuscationHelper.GNL_4_0_26) {
                findPreference("movetabhostbottom").setEnabled(false);
                findPreference("appdrawerswipetabs").setEnabled(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!InAppPurchase.isPremium) {
            findPreference("enableappdrawertabs").setEnabled(false);
            findPreference("addfolder").setEnabled(false);
        } else {
            getPreferenceScreen().removePreference(this.findPreference("needsDonate"));
        }

        rootView = CommonUI.setBackground(rootView, R.id.prefbackground);

        return rootView;
    }
}