package de.theknut.xposedgelsettings.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import java.util.Arrays;
import java.util.List;

import de.theknut.xposedgelsettings.R;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper;
import de.theknut.xposedgelsettings.ui.preferences.MyPreferenceScreen;
import de.theknut.xposedgelsettings.ui.preferences.SwitchCompatPreference;

public class FragmentGeneral extends FragmentBase {
	
	public FragmentGeneral() { }
     
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	super.onCreateView(inflater, container, savedInstanceState);
    	
    	View rootView = inflater.inflate(R.layout.options_fragment, container, false);
        addPreferencesFromResource(R.xml.general_fragment);

        findPreference("enablellauncher").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                if (!(Boolean) newValue) return true;

                try {
                    PackageManager pkgMgr = mContext.getPackageManager();
                    PackageInfo packageInfo = pkgMgr.getPackageInfo(Common.GEL_PACKAGE, 0);
                    if ((Integer.parseInt(packageInfo.versionName.split("\\.")[0]) >= 3)
                        && (Integer.parseInt(packageInfo.versionName.split("\\.")[1]) < 5)) {
                        Toast.makeText(
                                mContext,
                                "Your Google Search version is outdated (" + packageInfo.versionName + ")\n" + "Google Search 3.5 and up is required!\nThis tweak will have no effect!",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                } catch (Exception e) {
                    // shouldn't be here but lets prevent this from crashing...
                }
                return true;
            }
        });

        findPreference("enablerotation").setOnPreferenceChangeListener(onChangeListenerFullReboot);

        final SwitchCompatPreference resizeallwidgets = (SwitchCompatPreference) findPreference("resizeallwidgets");
        final SwitchCompatPreference overlappingWidgets = (SwitchCompatPreference) findPreference("overlappingwidgets");

        resizeallwidgets.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if(!(Boolean) newValue) {
                    overlappingWidgets.setChecked(false);
                }
                return true;
            }
        });
        overlappingWidgets.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if((Boolean) newValue) {
                    resizeallwidgets.setChecked(true);
                }

                return true;
            }
        });

        final MyPreferenceScreen contextmenuMode = (MyPreferenceScreen) findPreference("contextmenumode");
        final int modeIdx = Integer.parseInt(sharedPrefs.getString("contextmenumode", "3"));
        contextmenuMode.setSummary(getResources().getStringArray(R.array.contextmenu_mode_entries)[modeIdx]);
        contextmenuMode.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new MaterialDialog.Builder(mActivity)
                        .theme(Theme.DARK)
                        .title(R.string.pref_contextmenu_mode_title)
                        .items(getResources().getStringArray(R.array.contextmenu_mode_entries))
                        .itemsCallbackSingleChoice(Integer.parseInt(sharedPrefs.getString("contextmenumode", "3")), new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                // due to legacy reasons we need to save it as string
                                sharedPrefs.edit().putString("contextmenumode", "" + which).apply();
                                contextmenuMode.setSummary(text);
                                dialog.dismiss();
                            }
                        })
                        .build()
                        .show();
                return false;
            }
        });

        final MyPreferenceScreen pageIndicatorMode = (MyPreferenceScreen) findPreference("pageindicatormode");
        final int pageIndicatorModeIdx = sharedPrefs.getInt("pageindicatormode", 0);
        pageIndicatorMode.setSummary(getResources().getStringArray(R.array.pageindicator_mode_entries)[pageIndicatorModeIdx]);
        pageIndicatorMode.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new MaterialDialog.Builder(mActivity)
                        .theme(Theme.DARK)
                        .title(R.string.pref_pageindicator_title)
                        .items(getResources().getStringArray(R.array.pageindicator_mode_entries))
                        .itemsCallbackSingleChoice(sharedPrefs.getInt("pageindicatormode", 0), new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                sharedPrefs.edit().putInt("pageindicatormode", which).apply();
                                pageIndicatorMode.setSummary(text);
                                dialog.dismiss();
                            }
                        })
                        .build()
                        .show();
                return false;
            }
        });

        final MyPreferenceScreen scrollSpeed = (MyPreferenceScreen) findPreference("scrolldevider");
        final List<String> values = Arrays.asList(getResources().getStringArray(R.array.general_scroll_devider_values));
        final int currSelection = Integer.parseInt(sharedPrefs.getString("scrolldevider", "10"));
        final int speedIdx = values.indexOf("" + currSelection);
        scrollSpeed.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new MaterialDialog.Builder(mActivity)
                        .theme(Theme.DARK)
                        .title(R.string.pref_general_scroll_devider_title)
                        .items(getResources().getStringArray(R.array.general_scroll_devider_entries))
                        .itemsCallbackSingleChoice(values.indexOf("" + Integer.parseInt(sharedPrefs.getString("scrolldevider", "10"))), new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                // due to legacy reasons we need to save it as string
                                sharedPrefs.edit().putString("scrolldevider", getResources().getStringArray(R.array.general_scroll_devider_values)[which]).apply();
                                scrollSpeed.setSummary(text);
                                dialog.dismiss();
                            }
                        })
                        .build()
                        .show();
                return false;
            }
        });
        scrollSpeed.setSummary(getResources().getStringArray(R.array.general_scroll_devider_entries)[speedIdx]);

        findPreference("continuousscrollwithappdrawer").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if ((Boolean) newValue) {
                    SharedPreferences.Editor editor = mContext.getSharedPreferences(Common.PREFERENCES_NAME, Context.MODE_WORLD_READABLE).edit();
                    editor.remove("appdrawerswipetabs").apply();
                }
                return true;
            }
        });

        if (!InAppPurchase.isPremium) {
            findPreference("overlappingwidgets").setEnabled(false);
        } else {
            getPreferenceScreen().removePreference(this.findPreference("needsDonate"));
        }

        findPreference("hidewidgets").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                getActivity().startActivity(new Intent(getActivity(), AllWidgetsList.class));
                return true;
            }
        });

        findPreference("appnames").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getActivity(), ChooseAppList.class);
                intent.putExtra("mode", ChooseAppList.MODE_APP_RENAME);
                getActivity().startActivity(intent);
                return true;
            }
        });

        try {
            int versionCode = mContext.getPackageManager().getPackageInfo(Common.GEL_PACKAGE, 0).versionCode;
            if (versionCode >= ObfuscationHelper.GNL_4_0_26) {
                getPreferenceScreen().removePreference(this.findPreference("enablellauncher"));
            } else if (versionCode >= ObfuscationHelper.GNL_4_1_21) {
                getPreferenceScreen().removePreference(this.findPreference("enablellauncher"));
                getPreferenceScreen().removePreference(this.findPreference("glowcolor"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        rootView = CommonUI.setBackground(rootView, R.id.prefbackground);
        
        return rootView;
    }
}