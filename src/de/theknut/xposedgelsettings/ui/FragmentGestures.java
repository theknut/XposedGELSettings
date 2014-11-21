package de.theknut.xposedgelsettings.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.theknut.xposedgelsettings.R;
import de.theknut.xposedgelsettings.ui.preferences.MyCheckboxPreference;
import de.theknut.xposedgelsettings.ui.preferences.MyPreferenceScreen;
import de.theknut.xposedgelsettings.ui.preferences.SwitchCompatPreference;

@SuppressLint("WorldReadableFiles")
public class FragmentGestures extends FragmentBase {

    List<String> gestureEntries, gestureEntriesLimited;
    List<String> gestureValues, gestureValuesLimited;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.options_fragment, container, false);
        addPreferencesFromResource(R.xml.gestures_fragment);

        gestureEntries = Arrays.asList(getResources().getStringArray(R.array.gesture_actions_entries));
        gestureEntriesLimited = Arrays.asList(getResources().getStringArray(R.array.gesture_actions_entries_limited));
        gestureValues = Arrays.asList(getResources().getStringArray(R.array.gesture_actions_values));
        gestureValuesLimited = Arrays.asList(getResources().getStringArray(R.array.gesture_actions_values_limited));

        MyCheckboxPreference gesture_double_tap_only_on_wallpaper = (MyCheckboxPreference) this.findPreference("gesture_double_tap_only_on_wallpaper");
        MyPreferenceScreen gesture_double_tap = (MyPreferenceScreen) this.findPreference("gesture_double_tap");
        MyPreferenceScreen gesture_one_down_left = (MyPreferenceScreen) this.findPreference("gesture_one_down_left");
        MyPreferenceScreen gesture_one_down_middle = (MyPreferenceScreen) this.findPreference("gesture_one_down_middle");
        MyPreferenceScreen gesture_one_down_right = (MyPreferenceScreen) this.findPreference("gesture_one_down_right");
        final MyPreferenceScreen gesture_one_up_left = (MyPreferenceScreen) this.findPreference("gesture_one_up_left");
        MyPreferenceScreen gesture_one_up_middle = (MyPreferenceScreen) this.findPreference("gesture_one_up_middle");
        final MyPreferenceScreen gesture_one_up_right = (MyPreferenceScreen) this.findPreference("gesture_one_up_right");
        SwitchCompatPreference gesture_appdrawer =  (SwitchCompatPreference) this.findPreference("gesture_appdrawer");

        gesture_appdrawer.setOnPreferenceChangeListener(onChangeListenerLauncherReboot);

        List<MyPreferenceScreen> prefs = new ArrayList<MyPreferenceScreen>();
        prefs.add(gesture_double_tap);
        prefs.add(gesture_one_down_left);
        prefs.add(gesture_one_down_middle);
        prefs.add(gesture_one_down_right);
        prefs.add(gesture_one_up_left);
        prefs.add(gesture_one_up_middle);
        prefs.add(gesture_one_up_right);

        for (final MyPreferenceScreen pref : prefs) {

            final String value = sharedPrefs.getString(pref.getKey(), "NONE");
            if (value.equals("APP")) {
                pref.setSummary(gestureEntries.get(gestureValues.indexOf(value)) + " - " + CommonUI.getAppName(pref.getKey()));
            } else {
                pref.setSummary(gestureEntries.get(gestureValues.indexOf(value)));
            }

            pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(final Preference preference) {
                    new MaterialDialog.Builder(mActivity)
                            .theme(Theme.DARK)
                            .title(pref.getTitle())
                            .items(getResources().getStringArray(R.array.gesture_actions_entries))
                            .itemsCallbackSingleChoice(gestureValues.indexOf(sharedPrefs.getString(preference.getKey(), "NONE")), new MaterialDialog.ListCallback() {
                                @Override
                                public void onSelection(MaterialDialog dialog, View view, int which, String text) {

                                    if (gestureValues.get(which).equals("TOGGLE_DOCK")
                                            && preference.getKey().equals("gesture_double_tap")) {
                                        Toast.makeText(mContext, "Double tap to toggle dock is currently not supported :(", Toast.LENGTH_LONG).show();
                                        return;
                                    }

                                    if (gestureValues.get(which).equals("APP")) {
                                        Intent intent = new Intent(mContext, ChooseAppList.class);
                                        intent.putExtra("prefKey", preference.getKey());
                                        startActivityForResult(intent, 0);
                                    }

                                    sharedPrefs.edit().putString(preference.getKey(), gestureValues.get(which)).apply();
                                    preference.setSummary(text);
                                    dialog.dismiss();
                                }
                            })
                            .build()
                            .show();
                    return true;
                }
            });
        }

        if (!InAppPurchase.isPremium) {
            gesture_double_tap.setEnabled(false);
            gesture_one_down_middle.setEnabled(false);
            gesture_one_up_left.setEnabled(false);
            gesture_one_up_right.setEnabled(false);
            gesture_double_tap_only_on_wallpaper.setEnabled(false);
            gesture_one_up_middle.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(final Preference preference) {
                    new MaterialDialog.Builder(mActivity)
                            .theme(Theme.DARK)
                            .title(preference.getTitle())
                            .items(getResources().getStringArray(R.array.gesture_actions_entries_limited))
                            .itemsCallbackSingleChoice(gestureValues.indexOf(sharedPrefs.getString(preference.getKey(), "NONE")), new MaterialDialog.ListCallback() {
                                @Override
                                public void onSelection(MaterialDialog dialog, View view, int which, String text) {

                                    sharedPrefs.edit()
                                            .remove(preference.getKey())
                                            .putString(preference.getKey(), gestureValues.get(which))
                                            .remove(gesture_one_up_left.getKey())
                                            .putString(gesture_one_up_left.getKey(), gestureValues.get(which))
                                            .remove(gesture_one_up_right.getKey())
                                            .putString(gesture_one_up_right.getKey(), gestureValues.get(which))
                                            .apply();

                                    preference.setSummary(text);
                                    gesture_one_up_left.setSummary(text);
                                    gesture_one_up_right.setSummary(text);

                                    if (!toastShown) {
                                        Toast.makeText(mContext, R.string.toast_reboot, Toast.LENGTH_LONG).show();
                                        toastShown = true;
                                    }

                                    dialog.dismiss();
                                }
                            })
                            .build()
                            .show();
                    return true;
                }
            });
        }
        else {
            getPreferenceScreen().removePreference(this.findPreference("needsDonate"));
        }

        rootView = CommonUI.setBackground(rootView, R.id.prefbackground);

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (data == null) return;
        String gestureKey = data.getStringExtra("prefKey");

        if (!gestureKey.equals("")) {
            MyPreferenceScreen pref = (MyPreferenceScreen) this.findPreference(gestureKey);
            pref.setSummary(pref.getSummary() + " - " + CommonUI.getAppName(gestureKey));

            if (requestCode == Activity.RESULT_OK) {
                if (!toastShown) {
                    Toast.makeText(mContext, R.string.toast_reboot, Toast.LENGTH_LONG).show();
                    toastShown = true;
                }
            }
        }
    }
}