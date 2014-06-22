package de.theknut.xposedgelsettings.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.ExpandableListView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;

import de.theknut.xposedgelsettings.R;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.icon.IconPack;
import de.theknut.xposedgelsettings.hooks.icon.IconPreview;

public class FragmentSelectiveIcon extends FragmentActivity implements ActionBar.TabListener {

    AppSectionsPagerAdapter mAppSectionsPagerAdapter;
    ViewPager mViewPager;

    Intent intent;
    static SharedPreferences prefs;

    static Activity mActivity;
    static int tabCount;
    static List<String> tags;
    static String currentIconPack;
    static String appComponentName;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selectiveicon);

        intent = getIntent();
        appComponentName = intent.getStringExtra("app");
        mActivity = this;
        prefs = getSharedPreferences(Common.PREFERENCES_NAME, Context.MODE_WORLD_READABLE);

        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());
        final ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(CommonUI.UIColor));
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        PackageManager pkgMgr = getApplicationContext().getPackageManager();
        List<String> packages = CommonUI.getIconPacks(getApplicationContext());
        HashMap<String, String> iconPacks = new HashMap<String, String>();
        for (String pgk : packages) {
            try {
                if (hasDrawableList(pgk)) {
                    String iconPackName = (String) pkgMgr.getApplicationInfo(pgk, 0).loadLabel(pkgMgr);
                    iconPacks.put(iconPackName.toLowerCase(Locale.US), pgk);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        tags = new ArrayList<String>();
        Resources resources = getApplicationContext().getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        int outerSize = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, displayMetrics));
        int innerSize = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, displayMetrics));
        int distance = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, displayMetrics));

        for (String key : new TreeSet<String>(iconPacks.keySet())) {
            Drawable icon = null;
            try {
                icon = pkgMgr.getApplicationIcon(iconPacks.get(key));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            actionBar.addTab(
                    actionBar.newTab()
                            .setIcon(createIcon(resources, icon, outerSize, innerSize, distance))
                            .setText(key)
                            .setTag(iconPacks.get(key))
                            .setTabListener(this)
            );

            tags.add(iconPacks.get(key));
        }

        tabCount = actionBar.getTabCount();
        mAppSectionsPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (appComponentName.equals("all_apps_button_icon")) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.icon_menu, menu);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_refresh:

                SharedPreferences.Editor editor = prefs.edit();
                String key = "selectedicons";
                String appComponentName = "all_apps_button_icon";
                HashSet<String> selectedIcons = (HashSet<String>) prefs.getStringSet(key, new HashSet<String>());

                Iterator it = selectedIcons.iterator();
                while (it.hasNext()) {
                    String[] name = it.next().toString().split("\\|");
                    if (name[0].equals(appComponentName)) {
                        it.remove();
                    }
                }

                editor.remove(key);
                editor.apply();
                editor.putStringSet(key, selectedIcons);
                editor.apply();
                mActivity.finish();
                break;
            default:
                break;
        }

        return true;
    }

    public boolean hasDrawableList(String packageName) {
        try {
            Context resContext = getApplicationContext().createPackageContext(packageName, Context.CONTEXT_IGNORE_SECURITY);
            Resources res = resContext.getResources();

            int resId = res.getIdentifier("drawable", "xml", packageName);
            if (resId == 0) {
                return res.getAssets().open("drawable.xml") != null;
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Drawable createIcon(Resources resources, Drawable icon, int outerSize, int innerSize, int distance) {
        Bitmap outer = Bitmap.createBitmap(outerSize, outerSize, Bitmap.Config.ARGB_4444);
        Bitmap inner = Bitmap.createScaledBitmap(((BitmapDrawable) icon).getBitmap(), innerSize, innerSize, false);

        Bitmap bmOverlay = Bitmap.createBitmap(outer.getWidth(), outer.getHeight(), outer.getConfig());
        Canvas c = new Canvas(bmOverlay);
        c.drawBitmap(outer, new Matrix(), null);

        c.save();
        c.translate(distance, distance);
        c.drawBitmap(inner, new Matrix(), null);
        c.restore();

        return new BitmapDrawable(resources, bmOverlay);
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());
        currentIconPack = (String) tab.getTag();

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    public static class AppSectionsPagerAdapter extends FragmentStatePagerAdapter {

        public AppSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment = new IconPackPage();
            Bundle args = new Bundle();
            args.putString("app", appComponentName);
            args.putString("pkg", tags.get(i));
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return tabCount;
        }
    }

    SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (sharedPreferences.getBoolean("autokilllauncher", false)) {
                CommonUI.restartLauncher(false);
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        getSharedPreferences(Common.PREFERENCES_NAME, Context.MODE_WORLD_READABLE).registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

    @Override
    public void onPause() {
        getSharedPreferences(Common.PREFERENCES_NAME, Context.MODE_WORLD_READABLE).unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
        super.onPause();
    }

    public static class IconPackPage extends Fragment {

        public LayoutInflater inflater;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            this.inflater = inflater;
            View rootView = inflater.inflate(R.layout.expandablelistview, null);
            new IconPackLoader(getActivity(), rootView).execute(getArguments().getString("pkg"));
            return rootView;
        }

        public class ExpandableListAdapter extends BaseExpandableListAdapter {

            private IconPack iconPack;
            private List<String> categories;
            private List<List<List<IconPreview>>> children;

            private void fillListView(int colCount) {
                HashMap<String, List<IconPreview>> previews = this.iconPack.getIconPreviews();
                categories = new ArrayList<String>(previews.keySet());
                children = new ArrayList<List<List<IconPreview>>>();

                for (String category : categories) {
                    List<List<IconPreview>> iconPreviews = new ArrayList<List<IconPreview>>();
                    List<IconPreview> categoryIcons = previews.get(category);

                    int length = (categoryIcons.size() > 20) ? (20 - 20%colCount) : categoryIcons.size();
                    for (int i = 0; i < categoryIcons.size(); ) {
                        iconPreviews.add(getSubList(categoryIcons, i, length));
                        i += length;
                        length = (categoryIcons.size() - i > 20) ? (20 - 20%colCount) : categoryIcons.size() - i;
                    }

                    children.add(iconPreviews);
                }
            }

            private List<IconPreview> getSubList(List<IconPreview> categoryIcons, int start, int length) {
                List<IconPreview> subList = new ArrayList<IconPreview>();
                for (int i = start; i < start + length; i++) {
                    subList.add(categoryIcons.get(i));
                }
                return subList;
            }

            public void setIconPack(IconPack iconPack, int columnCount) {
                this.iconPack = iconPack;
                fillListView(columnCount);
            }

            @Override
            public int getGroupCount() {
                return categories.size();
            }

            @Override
            public int getChildrenCount(int i) {
                return children.get(i).size();
            }

            @Override
            public Object getGroup(int i) {
                return categories.get(i);
            }

            @Override
            public Object getChild(int i, int i1) {
                return children.get(i).get(i1);
            }

            @Override
            public long getGroupId(int i) {
                return i;
            }

            @Override
            public long getChildId(int i, int i1) {
                return i1;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }

            @Override
            public View getGroupView(int i, boolean isExpanded, View convertView, ViewGroup viewGroup) {
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.group_row, null);
                }

                CheckedTextView groupTitle = (CheckedTextView) convertView;
                groupTitle.setAllCaps(true);
                groupTitle.setText(getGroup(i).toString());
                groupTitle.setChecked(isExpanded);

                return convertView;
            }

            @Override
            public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

                MyGridView grid;
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.child_row, null);
                }

                grid = (MyGridView) convertView.findViewById(R.id.gridview);
                grid.setFocusable(true);
                grid.setClickable(true);

                ImageAdapter imageAdapter = new ImageAdapter(iconPack.getContext());
                imageAdapter.fillIcons(children.get(groupPosition).get(childPosition));
                grid.setAdapter(imageAdapter);

                return grid;
            }

            @Override
            public boolean isChildSelectable(int i, int i1) {
                return true;
            }
        }

        public class IconPackLoader extends AsyncTask<Object, Void, Void> {
            Context mContext;
            View rootView;
            ExpandableListView elv;
            ExpandableListAdapter ela;

            public IconPackLoader(Context c, View rootView) {
                mContext = c;
                this.rootView = rootView;
            }

            @Override
            protected Void doInBackground(Object... params) {
                elv = (ExpandableListView) rootView.findViewById(R.id.list);

                try {
                    IconPack iconPack = new IconPack(mContext, ((String) params[0]));
                    iconPack.loadIconCategories(mContext);
                    ela = new ExpandableListAdapter();
                    Resources resources = mContext.getResources();
                    DisplayMetrics displayMetrics = resources.getDisplayMetrics();
                    int requestedColumnWidth = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, displayMetrics));
                    int requestedHorizontalSpacing = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, displayMetrics));
                    int mNumColumns = (elv.getWidth() + requestedHorizontalSpacing) / (requestedColumnWidth + requestedHorizontalSpacing);

                    ela.setIconPack(iconPack, mNumColumns);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                elv.setAdapter(ela);
            }
        }
    }

    public static class ImageAdapter extends BaseAdapter {
        private Context mContext;
        private List<IconPreview> icons;
        public static int iconSize, paddingSize;

        public ImageAdapter(Context c) {
            mContext = c;

            DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
            iconSize = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 64, dm));
            paddingSize = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, dm));
        }

        public int getCount() {
            return icons.size();
        }

        public void fillIcons(List<IconPreview> icons) {
            this.icons = icons;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            final ImageView imageView;

            imageView = new ImageView(mContext);
            imageView.setClickable(true);
            imageView.setFocusable(true);
            imageView.setId(position);
            imageView.setImageResource(android.R.drawable.sym_def_app_icon);
            imageView.setTag(icons.get(position).getDrawableName());
            imageView.setLayoutParams(new MyGridView.LayoutParams(iconSize - paddingSize, iconSize - paddingSize));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setPadding(paddingSize, paddingSize, paddingSize, paddingSize);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences.Editor editor = prefs.edit();

                    String key = "selectedicons";
                    HashSet<String> selectedIcons = (HashSet<String>) prefs.getStringSet(key, new HashSet<String>());

                    Iterator it = selectedIcons.iterator();
                    while (it.hasNext()) {
                        String[] item = it.next().toString().split("\\|");
                        if (item[0].equals(appComponentName)) {
                            it.remove();
                        }
                    }

                    selectedIcons.add(appComponentName + "|" + currentIconPack + "|" + v.getTag());

                    editor.remove(key);
                    editor.apply();
                    editor.putStringSet(key, selectedIcons);
                    editor.apply();

                    mActivity.setResult(RESULT_OK);
                    mActivity.finish();
                }
            });

            new ImageLoader().execute(imageView, mContext, icons.get(position).getResID());
            return imageView;
        }

        public class ImageLoader extends AsyncTask<Object, Void, Void> {
            ImageView image;
            Drawable icon;

            @Override
            protected Void doInBackground(Object... params) {

                image = (ImageView) params[0];
                icon = ((Context) params[1]).getResources().getDrawable((Integer) params[2]);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                image.setImageDrawable(icon);
            }
        }
    }
}