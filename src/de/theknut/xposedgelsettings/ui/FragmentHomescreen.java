package de.theknut.xposedgelsettings.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import de.theknut.xposedgelsettings.R;
import de.theknut.xposedgelsettings.ui.preferences.MyPreferenceCategory;
import de.theknut.xposedgelsettings.ui.preferences.MyPreferenceScreen;

public class FragmentHomescreen extends FragmentBase {

    public FragmentHomescreen() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.options_fragment, container, false);
        addPreferencesFromResource(R.xml.homescreen_fragment);

        findPreference("gridsize").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                final ViewGroup numberPickerView = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.grid_number_picker, null);

                int minValue = 4, maxValue = 15;
                final NumberPicker npvc = (NumberPicker) numberPickerView.findViewById(R.id.numberPickerVerticalColumn);
                npvc.setMinValue(minValue);
                npvc.setMaxValue(maxValue);
                npvc.setValue(Integer.parseInt(sharedPrefs.getString("xcounthomescreen", "" + 4)));

                final NumberPicker npvr = (NumberPicker) numberPickerView.findViewById(R.id.numberPickerVerticalRow);
                npvr.setMinValue(minValue);
                npvr.setMaxValue(maxValue);
                npvr.setValue(Integer.parseInt(sharedPrefs.getString("ycounthomescreen", "" + 4)));

                final MaterialDialog numberPickerDialog = new MaterialDialog.Builder(mActivity)
                        .title(R.string.pref_grid_size_vert_summary)
                        .customView(numberPickerView, true)
                        .cancelable(false)
                        .theme(Theme.DARK)
                        .positiveText(android.R.string.ok)
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog materialDialog) {
                                // due to legacy reasons we need to save them as strings... -.-
                                SharedPreferences.Editor editor = sharedPrefs.edit();
                                editor.putString("ycounthomescreen", "" + npvr.getValue())
                                        .putString("xcounthomescreen", "" + npvc.getValue())
                                        .apply();

                                materialDialog.dismiss();
                            }
                        })
                        .build();

                numberPickerView.findViewById(R.id.horizontallayout).setVisibility(View.GONE);
                numberPickerDialog.show();
                return true;
            }
        });

        findPreference("workspacerect").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                final String[] values = getResources().getStringArray(R.array.workspacerect);
                final NumberPicker numberPicker = CommonUI.getNumberPicker(mContext, sharedPrefs, values, "workspacerect", "1");
                new MaterialDialog.Builder(mActivity)
                        .title(R.string.pref_workspacerect_title)
                        .customView(numberPicker, true)
                        .cancelable(false)
                        .theme(Theme.DARK)
                        .positiveText(android.R.string.ok)
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog materialDialog) {
                                // due to legacy reasons we need to save them as strings... -.-
                                sharedPrefs.edit()
                                        .putString("workspacerect", "" + values[numberPicker.getValue()])
                                        .apply();
                                materialDialog.dismiss();
                            }
                        })
                        .build()
                        .show();
                return true;
            }
        });

        findPreference("defaulthomescreen").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                final NumberPicker numberPicker = new NumberPicker(mContext);
                numberPicker.setMinValue(1);
                numberPicker.setMaxValue(10);
                numberPicker.setValue(Integer.parseInt(sharedPrefs.getString("defaulthomescreen", "1")));

                new MaterialDialog.Builder(mActivity)
                        .title(R.string.pref_default_homescreen_title)
                        .customView(numberPicker, true)
                        .theme(Theme.DARK)
                        .positiveText(android.R.string.ok)
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog materialDialog) {
                                // due to legacy reasons we need to save them as strings... -.-
                                sharedPrefs.edit()
                                        .putString("defaulthomescreen", "" + numberPicker.getValue())
                                        .apply();
                                materialDialog.dismiss();
                            }
                        })
                        .build()
                        .show();
                return true;
            }
        });

        this.findPreference("hide_appdock").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                if ((Boolean) newValue) {
                    new MaterialDialog.Builder(mActivity)
                            .theme(Theme.DARK)
                            .cancelable(false)
                            .title(android.R.string.dialog_alert_title)
                            .content(R.string.alert_hidedock_summary)
                            .positiveText(android.R.string.ok)
                            .callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog materialDialog) {
                                    if (!toastShown) {
                                        Toast.makeText(mContext, R.string.toast_reboot, Toast.LENGTH_LONG).show();
                                        toastShown = true;
                                    }
                                    materialDialog.dismiss();
                                }
                            })
                            .build()
                            .show();
                }

                return true;
            }
        });

        findPreference("positionallappsbutton").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                final String[] values = getResources().getStringArray(R.array.allappsbuttonposition_entries);
                final NumberPicker numberPicker = CommonUI.getNumberPicker(mContext, sharedPrefs, values, "positionallappsbutton", "0");
                new MaterialDialog.Builder(mActivity)
                        .title(R.string.pref_switch_position_all_apps_button_title)
                        .customView(numberPicker, true)
                        .theme(Theme.DARK)
                        .positiveText(android.R.string.ok)
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog materialDialog) {
                                String value = values[numberPicker.getValue()];

                                // due to legacy reasons we need to save them as strings... -.-
                                sharedPrefs.edit()
                                        .remove("positionallappsbutton")
                                        .putString("positionallappsbutton", "" + (value.equals(values[0]) ? -1 : value))
                                        .apply();
                                materialDialog.dismiss();
                            }
                        })
                        .build()
                        .show();
                return true;
            }
        });

        findPreference("appdockiconsize").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                final String[] values = getResources().getStringArray(R.array.iconsize_entries);
                final NumberPicker numberPicker = CommonUI.getNumberPicker(mContext, sharedPrefs, values, "appdockiconsize", "100");

                new MaterialDialog.Builder(mActivity)
                        .title(R.string.pref_appdockiconsize_title)
                        .customView(numberPicker, true)
                        .theme(Theme.DARK)
                        .positiveText(android.R.string.ok)
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog materialDialog) {
                                // due to legacy reasons we need to save them as strings... -.-
                                sharedPrefs.edit()
                                        .remove("appdockiconsize")
                                        .putString("appdockiconsize", "" + values[numberPicker.getValue()])
                                        .apply();
                                materialDialog.dismiss();
                            }
                        })
                        .build()
                        .show();
                return true;
            }
        });

        findPreference("appdockcount").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                final NumberPicker numberPicker = new NumberPicker(mContext);
                numberPicker.setMinValue(1);
                numberPicker.setMaxValue(12);
                numberPicker.setValue(Integer.parseInt(sharedPrefs.getString("appdockcount", "1")));
                new MaterialDialog.Builder(mActivity)
                        .title(R.string.pref_appdock_count_title)
                        .customView(numberPicker, true)
                        .theme(Theme.DARK)
                        .positiveText(android.R.string.ok)
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog materialDialog) {
                                // due to legacy reasons we need to save them as strings... -.-
                                sharedPrefs.edit()
                                        .remove("appdockcount")
                                        .putString("appdockcount", "" + numberPicker.getValue())
                                        .apply();
                                materialDialog.dismiss();
                            }
                        })
                        .build()
                        .show();
                return true;
            }
        });

        findPreference("appdockrect").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                final String[] values = getResources().getStringArray(R.array.workspacerect);
                final NumberPicker numberPicker = CommonUI.getNumberPicker(mContext, sharedPrefs, values, "appdockrect", "1");
                new MaterialDialog.Builder(mActivity)
                        .title(R.string.pref_workspacerect_dialog)
                        .customView(numberPicker, true)
                        .theme(Theme.DARK)
                        .positiveText(android.R.string.ok)
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog materialDialog) {
                                // due to legacy reasons we need to save them as strings... -.-
                                sharedPrefs.edit()
                                        .remove("appdockrect")
                                        .putString("appdockrect", "" + values[numberPicker.getValue()])
                                        .apply();
                                materialDialog.dismiss();
                            }
                        })
                        .build()
                        .show();
                return true;
            }
        });

        findPreference("iconsize").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                final String[] values = getResources().getStringArray(R.array.iconsize_entries);
                final NumberPicker numberPicker = CommonUI.getNumberPicker(mContext, sharedPrefs, values, "iconsize", "100");
                new MaterialDialog.Builder(mActivity)
                        .title(R.string.pref_iconsize_title)
                        .customView(numberPicker, true)
                        .theme(Theme.DARK)
                        .positiveText(android.R.string.ok)
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog materialDialog) {
                                // due to legacy reasons we need to save them as strings... -.-
                                sharedPrefs.edit()
                                        .remove("iconsize")
                                        .putString("iconsize", "" + values[numberPicker.getValue()])
                                        .apply();
                                materialDialog.dismiss();
                            }
                        })
                        .build()
                        .show();
                return true;
            }
        });

        findPreference("icontextsize").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                final String[] values = getResources().getStringArray(R.array.iconsize_entries);
                final NumberPicker numberPicker = CommonUI.getNumberPicker(mContext, sharedPrefs, values, "icontextsize", "100");
                new MaterialDialog.Builder(mActivity)
                        .title(R.string.pref_icontextsize_title)
                        .customView(numberPicker, true)
                        .theme(Theme.DARK)
                        .positiveText(android.R.string.ok)
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog materialDialog) {
                                // due to legacy reasons we need to save them as strings... -.-
                                sharedPrefs.edit()
                                        .remove("icontextsize")
                                        .putString("icontextsize", "" + values[numberPicker.getValue()])
                                        .apply();
                                materialDialog.dismiss();
                            }
                        })
                        .build()
                        .show();
                return true;
            }
        });

        final MyPreferenceScreen smartFolderMode = (MyPreferenceScreen) findPreference("smartfoldermode");
        final int modeIdx = Integer.parseInt(sharedPrefs.getString("smartfoldermode", "0"));
        smartFolderMode.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new MaterialDialog.Builder(mActivity)
                        .theme(Theme.DARK)
                        .title(R.string.pref_switch_smart_folder_title)
                        .items(getResources().getStringArray(R.array.smartfoldermode_entries))
                        .itemsCallbackSingleChoice(Integer.parseInt(sharedPrefs.getString("smartfoldermode", "0")), new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                // due to legacy reasons we need to save it as string
                                sharedPrefs.edit().putString("smartfoldermode", "" + which).apply();
                                smartFolderMode.setSummary(text);
                                return true;
                            }
                        })
                        .build()
                        .show();
                return false;
            }
        });
        smartFolderMode.setSummary(getResources().getStringArray(R.array.smartfoldermode_entries)[modeIdx]);

        if (!InAppPurchase.isPremium) {
            findPreference("unlimitedfoldersize").setEnabled(false);
        } else {
            MyPreferenceCategory cat = (MyPreferenceCategory) this.findPreference("folders");
            cat.removePreference(this.findPreference("needsDonate"));
        }

        rootView = CommonUI.setBackground(rootView, R.id.prefbackground);
        return rootView;
    }
}