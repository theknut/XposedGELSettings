package de.theknut.xposedgelsettings.ui;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import de.theknut.xposedgelsettings.R;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.icon.Icon;
import de.theknut.xposedgelsettings.hooks.icon.IconPack;

public class FragmentIcon extends FragmentBase {
    
    PackageManager packageManager;
    MyPreferenceScreen iconPackSupport;
    MyListPreference iconPackList;
    List<String> notSupportedIconsList;
    static public IconPack iconPack;
    GridView grid;
    ProgressBar progressBar;
    boolean dirty;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	super.onCreateView(inflater, container, savedInstanceState);
    	
    	View rootView = inflater.inflate(R.layout.icon_fragment, container, false);
        addPreferencesFromResource(R.xml.icon_fragment);

        this.findPreference("selectiveicon").setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent i = new Intent(getActivity(), ChooseAppList.class);
                startActivity(i);
                return true;
            }
        });

        this.findPreference("allappsbuttonicon").setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent i = new Intent(getActivity(), FragmentSelectiveIcon.class);
                i.putExtra("app", "all_apps_button_icon");
                i.putExtra("mode", FragmentSelectiveIcon.MODE_PICK_APPDRAWER_ICON);
                startActivity(i);
                return true;
            }
        });

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
                    new AlertDialog.Builder(mContext)
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

            final HashMap<String, String> iconPacks = new HashMap<String, String>();
            for (String pgk : packages) {
                try {
                    String iconPackName = (String) packageManager.getApplicationInfo(pgk, 0).loadLabel(packageManager);
                    iconPacks.put(iconPackName, pgk);                
                } catch (NameNotFoundException e) {
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
            } catch (Exception e) { }
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
                    iconPackList.setEnabled(false);
                    
                    MyListPreference pref = (MyListPreference) preference;
                    String iconPackName = (String) pref.getEntries()[pref.findIndexOfValue((String) newValue)];
                    pref.setSummary(iconPackName);

                    if (((String) newValue).equals(Common.ICONPACK_DEFAULT)) {
                        iconPack = null;
                        getFragmentManager().beginTransaction().replace(R.id.content_frame, new FragmentIcon()).commit();
                        return true;
                    }

                    grid.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setIndeterminate(true);
                    loadIconPack(true);

                    return true;
                }
            });
            
            if (!packages.contains(iconPackList.getValue())) {
                iconPackList.setValueIndex(0);
            }

            progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
            grid = (GridView) rootView.findViewById(R.id.iconpreview);

            try {
                if (iconPack == null) {
                    iconPack = new IconPack(mContext, iconPackList.getValue());
                    iconPack.loadAppFilter();
                }

                new AsyncTask<Void, Void, Void>() {

                    ImageAdapter imageAdapter;

                    @Override
                    protected Void doInBackground(Void... params) {

                        while(CommonUI.LOADING_ICONPACK) {
                            SystemClock.sleep(100);
                        }

                        imageAdapter = new ImageAdapter(iconPack);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        grid.setAdapter(imageAdapter);
                    }
                }.execute();

            } catch (NameNotFoundException e) {
                e.printStackTrace();
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
                
                new AlertDialog.Builder(mContext)
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
        } else {
            getPreferenceScreen().removePreference(this.findPreference("needsDonate"));
        }

        iconPackSupport.setDependency(iconPackList.getKey());
        findPreference("autoupdateapplyiconpack").setDependency(iconPackList.getKey());
        findPreference("hideiconpacks").setDependency(iconPackList.getKey());
        findPreference("selectiveicon").setDependency(iconPackList.getKey());
        findPreference("allappsbuttonicon").setDependency(iconPackList.getKey());
        
        rootView = CommonUI.setBackground(rootView, R.id.prefbackground);
        
        return rootView;
    }

    private class UpdateStatisticAsyncTask extends AsyncTask<Void, Void, Void> {

        String summary;
        IconPack mIconPack;

        public UpdateStatisticAsyncTask(IconPack iconPack) {
            mIconPack = iconPack;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            iconPackList.setEnabled(false);
            iconPackSupport.setSummary("Loading...\n\n");
        }
        
        @Override
        protected Void doInBackground(Void... params) {

            notSupportedIconsList = new ArrayList<String>();

            List<ResolveInfo> apps = CommonUI.getAllApps();
            if (!iconPackList.getValue().equals(Common.ICONPACK_DEFAULT)) {

                int cnt = 0;
                for (ResolveInfo resolveInfo : apps) {
                    if (mIconPack.getResourceIdForDrawable(resolveInfo.activityInfo.packageName) != 0) {
                        cnt++;
                    } else {
                        notSupportedIconsList.add(resolveInfo.loadLabel(packageManager).toString());
                    }
                }

                summary = String.format(
                        mContext.getString(R.string.pref_icon_support_summary), // string
                        String.valueOf(mIconPack.getCalendarIcons().size() != 0), // calendar support
                        cnt, // count supported apps
                        apps.size(), // count installed apps
                        mIconPack.getTotalIconCount() // count total apps
                );
            } else {
                summary = (String) iconPackList.getEntry();
            }

            return null;
        }
        
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            iconPackSupport.setSummary(summary);
            iconPackList.setEnabled(InAppPurchase.isPremium);
            dirty = false;
        }
    }

    public class ImageAdapter extends BaseAdapter {
        private Context mContext;
        private IconPack mIconPack;
        private List<ResolveInfo> apps;
        private int iconSize, paddingSize;

        public ImageAdapter(IconPack iconPack) {

            if (iconPack == null) {
                try {
                    mIconPack = new IconPack(mContext, Common.ICONPACK_DEFAULT);
                } catch (NameNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                mIconPack = iconPack;
            }

            mContext = mIconPack.getContext();

            DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
            iconSize = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 64, dm));
            paddingSize = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, dm));

            apps = CommonUI.getAllApps();
        }

        public int getCount() {
            return apps.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {

            final ImageView imageView;

            imageView = new ImageView(mContext);
            imageView.setId(position);
            imageView.setLayoutParams(new GridView.LayoutParams(iconSize - paddingSize, iconSize - paddingSize));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setPadding(paddingSize, paddingSize, paddingSize, paddingSize);

            new ImageLoader().execute(imageView, mIconPack, apps.get(position));

            return imageView;
        }

        public class ImageLoader extends AsyncTask<Object, Void, Void> {

            ImageView image;
            Drawable icon;

            @Override
            protected Void doInBackground(Object... params) {

                ResolveInfo info = (ResolveInfo) params[2];
                ComponentName cmpName = new ComponentName(info.activityInfo.packageName, info.activityInfo.name);
                image = (ImageView) params[0];
                icon = mIconPack.loadIcon(cmpName.flattenToString());

                if (icon == null) {
                    if (!mIconPack.shouldThemeMissingIcons()) {
                        icon = info.loadIcon(mIconPack.getContext().getPackageManager());
                    } else {
                        icon = info.loadIcon(mIconPack.getContext().getPackageManager());
                        Bitmap tmpIcon = Bitmap.createBitmap(CommonUI.drawableToBitmap(icon));
                        Bitmap tmpFinalIcon = mIconPack.themeIcon(tmpIcon);
                        icon = new BitmapDrawable(mIconPack.getResources(), tmpFinalIcon);
                    }

                    Icon newIcon = new Icon(cmpName.flattenToString(), icon);
                    mIconPack.getIcons().add(newIcon);
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (progressBar.getVisibility() == View.VISIBLE
                    && image.getId() == grid.getNumColumns()) {
                    progressBar.setVisibility(View.GONE);
                    progressBar.setIndeterminate(false);

                    try {
                        if (iconPackList.getEntryValues().length != 0) {
                            iconPackSupport.setTitle(iconPackList.getEntry().toString());
                            new UpdateStatisticAsyncTask(mIconPack).execute();
                        } else {
                            iconPackSupport.setSummary(getString(R.string.pref_icon_noiconpack));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                image.setImageDrawable(icon);
            }
        }
    }

    public static void loadIconPack(final boolean reloadFragment) {

        try {
            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... params) {
                    CommonUI.LOADING_ICONPACK = true;
                    try {
                        FragmentIcon.iconPack = new IconPack(
                                CommonUI.CONTEXT,
                                CommonUI.CONTEXT.getSharedPreferences(
                                        Common.PREFERENCES_NAME,
                                        Context.MODE_WORLD_READABLE
                                ).getString("iconpack", Common.ICONPACK_DEFAULT)
                        );
                        FragmentIcon.iconPack.loadAppFilter();

                        for (ResolveInfo info : CommonUI.getAllApps()) {
                            ComponentName cmpName = new ComponentName(info.activityInfo.packageName, info.activityInfo.name);
                            Drawable icon = FragmentIcon.iconPack.loadIcon(cmpName.flattenToString());

                            if (icon == null) {
                                if (!FragmentIcon.iconPack.shouldThemeMissingIcons()) {
                                    icon = info.loadIcon(FragmentIcon.iconPack.getContext().getPackageManager());
                                } else {
                                    icon = info.loadIcon(FragmentIcon.iconPack.getContext().getPackageManager());
                                    Bitmap tmpIcon = Bitmap.createBitmap(CommonUI.drawableToBitmap(icon));
                                    Bitmap tmpFinalIcon = FragmentIcon.iconPack.themeIcon(tmpIcon);
                                    icon = new BitmapDrawable(FragmentIcon.iconPack.getResources(), tmpFinalIcon);
                                }

                                Icon newIcon = new Icon(cmpName.flattenToString(), icon);
                                FragmentIcon.iconPack.getIcons().add(newIcon);
                            }
                        }
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    CommonUI.LOADING_ICONPACK = false;

                    try {
                        if (reloadFragment) {
                            CommonUI.ACTIVITY.getFragmentManager().beginTransaction().replace(R.id.content_frame, new FragmentIcon()).commit();
                        }
                    } catch (Exception ex) {}
                }
            }.execute();
        } catch (Exception ex) {}
    }
}