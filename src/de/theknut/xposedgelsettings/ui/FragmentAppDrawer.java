package de.theknut.xposedgelsettings.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import de.theknut.xposedgelsettings.R;
import de.theknut.xposedgelsettings.hooks.Common;

public class FragmentAppDrawer extends FragmentBase {

    public FragmentAppDrawer() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.options_fragment, container, false);
        addPreferencesFromResource(R.xml.appdrawer_fragment);

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

        if (!InAppPurchase.isPremium) {
            findPreference("enableappdrawertabs").setEnabled(false);
        } else {
            getPreferenceScreen().removePreference(this.findPreference("needsDonate"));
        }

        rootView = CommonUI.setBackground(rootView, R.id.prefbackground);

        return rootView;
    }
}