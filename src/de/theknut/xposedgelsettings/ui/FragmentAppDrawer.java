package de.theknut.xposedgelsettings.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.Toast;

import de.theknut.xposedgelsettings.R;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper;

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
                final ViewGroup numberPickerView = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.number_picker, null);
                int padding = Math.round(mContext.getResources().getDimension(R.dimen.tab_menu_padding));
                final AlertDialog numberPickerDialog = new AlertDialog.Builder(mActivity).create();
                numberPickerDialog.setView(numberPickerView, padding, padding, padding, padding);

                final SharedPreferences prefs = mContext.getSharedPreferences(Common.PREFERENCES_NAME, Context.MODE_WORLD_READABLE);

                int minValue = 3, maxValue = 15;
                final NumberPicker nphc = (NumberPicker) numberPickerView.findViewById(R.id.numberPickerHorizontalColumn);
                nphc.setMinValue(minValue);
                nphc.setMaxValue(maxValue);
                nphc.setValue(Integer.parseInt(prefs.getString("xcountallappshorizontal", "" + 6)));

                final NumberPicker nphr = (NumberPicker) numberPickerView.findViewById(R.id.numberPickerHorizontalRow);
                nphr.setMinValue(minValue);
                nphr.setMaxValue(maxValue);
                nphr.setValue(Integer.parseInt(prefs.getString("ycountallappshorizontal", "" + 4)));

                final NumberPicker npvc = (NumberPicker) numberPickerView.findViewById(R.id.numberPickerVerticalColumn);
                npvc.setMinValue(minValue);
                npvc.setMaxValue(maxValue);
                npvc.setValue(Integer.parseInt(prefs.getString("xcountallapps", "" + 4)));

                final NumberPicker npvr = (NumberPicker) numberPickerView.findViewById(R.id.numberPickerVerticalRow);
                npvr.setMinValue(minValue);
                npvr.setMaxValue(maxValue);
                npvr.setValue(Integer.parseInt(prefs.getString("ycountallapps", "" + 5)));

                numberPickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, mContext.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // due to legacy reasons we need to save them as strings.........
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("ycountallapps", "" + npvr.getValue())
                                .putString("xcountallapps", "" + npvc.getValue())
                                .putString("ycountallappshorizontal", "" + nphr.getValue())
                                .putString("xcountallappshorizontal", "" + nphc.getValue())
                                .commit();

                        numberPickerDialog.dismiss();
                    }
                });

                numberPickerDialog.setCancelable(false);
                numberPickerDialog.show();
                return true;
            }
        });

        findPreference("cleartabdata").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new AlertDialog.Builder(mContext)
                        .setTitle(getString(R.string.alert_appdrawer_clear_tabs_title))
                        .setMessage(getString(R.string.alert_appdrawer_clear_tabs_summary))
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                SharedPreferences settings = mContext.getSharedPreferences(Common.PREFERENCES_NAME, 0);
                                SharedPreferences.Editor editor = settings.edit();
                                editor.remove("appdrawertabdata").commit();
                                Toast.makeText(mContext, getString(R.string.alert_appdrawer_clear_tabs_success), Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
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
                    editor.remove("continuousscrollwithappdrawer").commit();
                }
                return true;
            }
        });

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