package de.theknut.xposedgelsettings.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.theknut.xposedgelsettings.R;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.icon.IconPack;

public class FragmentIcon extends FragmentBase {
    
    PackageManager packageManager;
    MyPreferenceScreen iconPackSupport;
    MyListPreference iconPackList;
    List<String> notSupportedIconsList;
    boolean dirty;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	super.onCreateView(inflater, container, savedInstanceState);
    	
    	View rootView = inflater.inflate(R.layout.options_fragment, container, false);
        addPreferencesFromResource(R.xml.icon_fragment);
        
        packageManager = mContext.getPackageManager();
        List<String> packages = CommonUI.getIconPacks(mContext);
        
        iconPackList = (MyListPreference) findPreference("iconpack");
        if (packages.isEmpty()) {
            CharSequence[] tmp = new CharSequence[1];
            tmp[0] = getString(R.string.pref_icon_noiconpack);
            iconPackList.setEntries(tmp);
            tmp = new CharSequence[1];
            tmp[0] = Common.ICONPACK_DEFAULT;
            iconPackList.setEntryValues(tmp);
            iconPackList.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    new AlertDialog.Builder(CommonUI.CONTEXT)
                    .setCancelable(false)
                    .setTitle(R.string.alert_icon_noiconpackfound_title)
                    .setMessage(R.string.alert_icon_noiconpackfound_summary)
                    .setPositiveButton(R.string.alert_icon_noiconpackfound_yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("market://developer?id=Vertumus"));
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                            } catch (android.content.ActivityNotFoundException anfe) {
                                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=Vertumus"));
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                            }
                            
                            getActivity().finish();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
                    
                    return false;
                }
            });
        } else {            
            HashMap<String, String> iconPacks = new HashMap<String, String>();
            for (String pgk : packages) {
                try {
                    String iconPackName = (String) packageManager.getApplicationInfo(pgk, 0).loadLabel(packageManager);
                    iconPacks.put(iconPackName, pgk);                
                } catch (NameNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            List<String> names = new ArrayList<String>(iconPacks.keySet());
            Collections.sort(names, String.CASE_INSENSITIVE_ORDER);
            iconPacks.put(getString(R.string.pref_icon_noiconpack), Common.ICONPACK_DEFAULT);
            names.add(0, getString(R.string.pref_icon_noiconpack));
            
            CharSequence[] tmp = new CharSequence[names.size()];
            try {
                for (int j = 0; j < tmp.length; j++) {
                    tmp[j] = names.get(j);
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
            iconPackList.setEntries(tmp);

            tmp = new CharSequence[names.size()];
            try {
                for (int j = 0; j < tmp.length; j++) {
                    tmp[j] = iconPacks.get(names.get(j));
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
            iconPackList.setEntryValues(tmp);

            iconPackList.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    dirty = true;
                    notSupportedIconsList = null;
                    
                    MyListPreference pref = (MyListPreference) preference;
                    String iconPackName = (String) pref.getEntries()[pref.findIndexOfValue((String) newValue)];
                    pref.setSummary(iconPackName);

                    if (((String) newValue).equals(Common.ICONPACK_DEFAULT)) {
                        getFragmentManager().beginTransaction().replace(R.id.content_frame, new FragmentIcon()).commit();
                        return true;
                    }
                    
                    new UpdateStatisticAsyncTask().execute(iconPackName);

                    return true;
                }
            });
            
            if (!packages.contains(iconPackList.getValue())) {
                iconPackList.setValueIndex(0);
            }
        }
        
        iconPackList.setSummary(iconPackList.getEntry());
        
        iconPackSupport = (MyPreferenceScreen) findPreference("support");
        iconPackSupport.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            
            @Override
            public boolean onPreferenceClick(Preference preference) {
                
                if (dirty || notSupportedIconsList == null) return false;
                
                StringBuilder sb = new StringBuilder();
                for (String string : notSupportedIconsList) {
                    sb.append(string).append('\n');
                }
                
                new AlertDialog.Builder(CommonUI.CONTEXT)
                .setCancelable(true)
                .setMessage(sb.toString())
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
                
                return true;
            }
        });
        
        if (!InAppPurchase.isPremium) {
            iconPackList.setEnabled(false);
            iconPackSupport.setEnabled(false);
            findPreference("autoupdateapplyiconpack").setEnabled(false);
            findPreference("hideiconpacks").setEnabled(false);
        } else {
            getPreferenceScreen().removePreference(this.findPreference("needsDonate"));
        }
        
        rootView = CommonUI.setBackground(rootView, R.id.prefbackground);
        
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (iconPackList.getEntryValues().length != 0) {
            new UpdateStatisticAsyncTask().execute(iconPackList.getEntry().toString());
        } else {
            iconPackSupport.setSummary(getString(R.string.pref_icon_noiconpack));
        }
    }

    private class UpdateStatisticAsyncTask extends AsyncTask<String, Void, Void> {
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            
            iconPackSupport.setSummary("Loading...\n");            
        }
        
        @Override
        protected Void doInBackground(final String... params) {

            String newSummary;
            notSupportedIconsList = new ArrayList<String>();


            Activity activity = getActivity();
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    public void run() {iconPackSupport.setTitle(params[0]);
                    }
                });
            }

            try {
                List<ResolveInfo> apps = CommonUI.getAllApps();
                if (!iconPackList.getValue().equals(Common.ICONPACK_DEFAULT)) {
                    ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
                    int dpi = activityManager.getLauncherLargeIconDensity();
                    IconPack ip = new IconPack(mContext, iconPackList.getValue(), dpi);
                    ip.loadAppFilter();                

                    int cnt = 0;
                    for (ResolveInfo resolveInfo : apps) {
                        if (ip.getResourceIdForDrawable(resolveInfo.activityInfo.packageName) != 0) {
                            cnt++;
                        } else {
                            notSupportedIconsList.add(resolveInfo.loadLabel(packageManager).toString());
                        }
                    }

                    newSummary = String.format(mContext.getString(R.string.pref_icon_support_summary), cnt, apps.size(), ip.getTotalIconCount());
                } else {
                    newSummary = (String) iconPackList.getEntry();
                }

                final String summary = newSummary;
                activity = getActivity();
                if (activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            iconPackSupport.setSummary(summary);
                        }
                    });
                }
            } catch (NameNotFoundException e) {
                e.printStackTrace();

                activity = getActivity();
                if (activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            iconPackSupport.setSummary("Error while loading statistics\n");
                        }
                    });
                }
            }
            
            return null;
        }
        
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            
            dirty = false;
        }
    }
}