package de.theknut.xposedgelsettings.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;

import de.theknut.xposedgelsettings.R;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.Utils;
import de.theknut.xposedgelsettings.hooks.icon.IconPack;
import de.theknut.xposedgelsettings.hooks.icon.IconPreview;
import de.theknut.xposedgelsettings.ui.preferences.MyGridView;
import eu.janmuller.android.simplecropimage.CropImage;

public class FragmentSelectiveIcon extends ActionBarActivity implements ActionBar.TabListener {

    AppSectionsPagerAdapter mAppSectionsPagerAdapter;
    ViewPager mViewPager;

    Intent intent;

    static Activity mActivity;
    static int tabCount;
    static List<String> tags;
    static String currentIconPack;
    static String appComponentName;
    static long itemID;
    static int mode;

    static final int MODE_PICK_GLOBAL_ICON = 1;
    public static final int MODE_PICK_SHORTCUT_ICON = 2;
    static final int MODE_PICK_APPDRAWER_ICON = 3;
    public static final int MODE_PICK_FOLDER_ICON = 4;
    private static int REQUEST_PICK_PICTURE = 10;
    private static int REQUEST_CROP_PICTURE = 11;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selectiveicon);

        intent = getIntent();
        appComponentName = intent.getStringExtra("app");
        mode = intent.getIntExtra("mode", 1);

        if (mode == MODE_PICK_SHORTCUT_ICON || mode == MODE_PICK_FOLDER_ICON) {
            itemID = intent.getLongExtra("itemtid", 0);
        }

        CommonUI.CONTEXT = CommonUI.ACTIVITY = mActivity = this;

        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        if (intent.hasExtra("name")) {
            actionBar.setTitle(intent.getStringExtra("name"));
        }

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        String currentIconPack = getSharedPreferences(Common.PREFERENCES_NAME, Context.MODE_WORLD_READABLE).getString("iconpack", "");
        PackageManager pkgMgr = getApplicationContext().getPackageManager();
        List<String> packages = CommonUI.getIconPacks(getApplicationContext());
        LinkedHashMap<String, String> iconPacks = new LinkedHashMap<String, String>();
        iconPacks.put(getString(R.string.pref_icon_noiconpack), Common.ICONPACK_DEFAULT);

        for (String pkg : packages) {
            try {
                if (shouldShow(pkg)) {
                    String iconPackName = (String) pkgMgr.getApplicationInfo(pkg, 0).loadLabel(pkgMgr);
                    iconPacks.put(iconPackName.toLowerCase(Locale.US), pkg);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        tags = new ArrayList<String>();
        Resources resources = getApplicationContext().getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        int outerSize = Utils.dpToPx(32, displayMetrics);
        int innerSize = Utils.dpToPx(24, displayMetrics);
        int distance = Utils.dpToPx(4, displayMetrics);

        int startingPosition = 0;
        for (String key : new TreeSet<String>(iconPacks.keySet())) {
            Drawable icon = null;
            try {
                icon = iconPacks.get(key).equals(Common.ICONPACK_DEFAULT)
                        ? getResources().getDrawable(android.R.drawable.sym_def_app_icon)
                        : pkgMgr.getApplicationIcon(iconPacks.get(key));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            ActionBar.Tab tab = actionBar.newTab()
                    .setIcon(createIcon(resources, icon, outerSize, innerSize, distance))
                    .setText(key)
                    .setTag(iconPacks.get(key))
                    .setTabListener(this);
            actionBar.addTab(tab);
            tags.add(iconPacks.get(key));

            if (iconPacks.get(key).equals(currentIconPack)) {
                startingPosition = actionBar.getTabCount() - 1;
            }
        }

        tabCount = actionBar.getTabCount();
        actionBar.setSelectedNavigationItem(startingPosition);
        mAppSectionsPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.icon_menu, menu);

        if (mode == MODE_PICK_APPDRAWER_ICON) {
            menu.findItem(R.id.appdrawerdefault).setVisible(true);
        } else if (mode == MODE_PICK_SHORTCUT_ICON || mode == MODE_PICK_FOLDER_ICON) {
            menu.findItem(R.id.shortcutfolderdefault).setVisible(true);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        SharedPreferences prefs = getSharedPreferences(Common.PREFERENCES_NAME, Context.MODE_WORLD_READABLE);
        SharedPreferences.Editor editor = prefs.edit();
        String key, shortcutItem;
        Iterator it;
        HashSet<String> icons;

        switch (item.getItemId()) {
            case R.id.appdrawerdefault:
            case R.id.shortcutfolderdefault:
                shortcutItem = String.valueOf(itemID);
                if (mode == MODE_PICK_SHORTCUT_ICON) {
                    key = "shortcuticons";
                } else if (mode == MODE_PICK_APPDRAWER_ICON) {
                    key = "selectedicons";
                    shortcutItem = appComponentName;
                } else {
                    key = "foldericons";
                }

                icons = (HashSet<String>) prefs.getStringSet(key, new HashSet<String>());
                it = icons.iterator();
                while (it.hasNext()) {
                    String[] name = it.next().toString().split("\\|");
                    if (name[0].equals(shortcutItem)) {
                        it.remove();
                    }
                }

                editor.remove(key).commit();
                editor.putStringSet(key, icons).commit();
                finishActivity(true);

                break;
            case R.id.iconfromgallery:
                Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, REQUEST_PICK_PICTURE);
                break;
            default:
                break;
        }

        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_PICK_PICTURE) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());

                if (bitmap.getWidth() <= 192 && bitmap.getHeight() <= 192
                        && (bitmap.getWidth() / bitmap.getHeight() == 1.0)) {
                    File dst;
                    if (mode == MODE_PICK_APPDRAWER_ICON) {
                        dst = new File("/mnt/sdcard/XposedGELSettings/icons/all_apps_button_icon.png");
                    } else {
                        dst = new File("/mnt/sdcard/XposedGELSettings/icons/" + itemID + ".png");
                    }
                    dst.getParentFile().mkdirs();
                    dst.createNewFile();

                    FileOutputStream out = null;
                    try {
                        out = new FileOutputStream(dst);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (out != null) {
                                out.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    saveExternalIcon();
                    return;
                }

                Intent intent = new Intent(getApplicationContext(), CropImage.class);
                intent.putExtra(CropImage.IMAGE_PATH, data.getData().toString());
                intent.putExtra(CropImage.PATH_AS_URI, true);
                intent.putExtra(CropImage.RETURN_DATA, true);
                intent.putExtra(CropImage.RETURN_DATA_AS_BITMAP, true);
                intent.putExtra(CropImage.SCALE, true);
                intent.putExtra(CropImage.ASPECT_X, 1);
                intent.putExtra(CropImage.ASPECT_Y, 1);
                intent.putExtra(CropImage.OUTPUT_X, 192);
                intent.putExtra(CropImage.OUTPUT_Y, 192);
                startActivityForResult(intent, REQUEST_CROP_PICTURE);
            } catch (Exception e) {
                Toast.makeText(this, "Couldn't load image. Please send a bug report from the XGELS settings menu!", Toast.LENGTH_LONG).show();
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CROP_PICTURE) {
            FileOutputStream out = null;
            File dst;
            if (mode == MODE_PICK_APPDRAWER_ICON) {
                dst = new File("/mnt/sdcard/XposedGELSettings/icons/all_apps_button_icon.png");
            } else {
                dst = new File("/mnt/sdcard/XposedGELSettings/icons/" + itemID + ".png");
            }
            dst.getParentFile().mkdirs();
            try {
                out = new FileOutputStream(dst);
                ((Bitmap) data.getParcelableExtra(CropImage.RETURN_DATA_AS_BITMAP)).compress(Bitmap.CompressFormat.PNG, 100, out);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.flush();
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            saveExternalIcon();
        }
    }

    public void saveExternalIcon() {
        SharedPreferences prefs = CommonUI.CONTEXT.getSharedPreferences(Common.PREFERENCES_NAME, Context.MODE_WORLD_READABLE);
        SharedPreferences.Editor editor = prefs.edit();

        String key = "selectedicons";
        if (mode == MODE_PICK_SHORTCUT_ICON) {
            key = "shortcuticons";
        } else if (mode == MODE_PICK_FOLDER_ICON) {
            key = "foldericons";
        }

        HashSet<String> selectedIcons = (HashSet<String>) prefs.getStringSet(key, new HashSet<String>());

        String searchString = mode == MODE_PICK_APPDRAWER_ICON ? "all_apps_button_icon" : String.valueOf(itemID);
        Iterator it = selectedIcons.iterator();
        while (it.hasNext()) {
            String[] item = it.next().toString().split("\\|");
            if (item[0].equals(searchString)) {
                it.remove();
            }
        }

        selectedIcons.add(searchString + "|sdcard|" + searchString);

        editor.remove(key).commit();
        editor.putStringSet(key, selectedIcons).commit();
        finishActivity(false);
    }

    public static void finishActivity(boolean restoreDefault) {
        if (mode == MODE_PICK_SHORTCUT_ICON || mode == MODE_PICK_FOLDER_ICON) {
            Intent intent = new Intent(Common.XGELS_ACTION_UPDATE_ICON);
            intent.putExtra("mode", mode);
            intent.putExtra("itemid", itemID);
            intent.putExtra("default", restoreDefault);
            mActivity.sendBroadcast(intent);
        }

        mActivity.finish();
    }

    public boolean shouldShow(String packageName) {
        try {
            Context resContext = getApplicationContext().createPackageContext(packageName, Context.CONTEXT_IGNORE_SECURITY);
            Resources res = resContext.getResources();

            int resId = res.getIdentifier("drawable", "xml", packageName);
            if (resId == 0) {
                return res.getAssets().open("drawable.xml") != null;
            }

            return true;
        } catch (Exception e) {

            try {
                IconPack tmpIconPack = new IconPack(getApplicationContext(), packageName);
                tmpIconPack.loadAppFilter();
                return tmpIconPack.getAppFilter().size() != 0;
            } catch (PackageManager.NameNotFoundException e1) {
                e1.printStackTrace();
            }

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
    public void onTabSelected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());
        currentIconPack = (String) tab.getTag();
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) { }

    @Override
    public void onTabReselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) { }

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
        if (mode == MODE_PICK_APPDRAWER_ICON || mode == MODE_PICK_GLOBAL_ICON) {
            getSharedPreferences(Common.PREFERENCES_NAME, Context.MODE_WORLD_READABLE).registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
        }
    }

    @Override
    public void onPause() {
        if (mode == MODE_PICK_APPDRAWER_ICON || mode == MODE_PICK_GLOBAL_ICON) {
            getSharedPreferences(Common.PREFERENCES_NAME, Context.MODE_WORLD_READABLE).unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
        }
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
                    int requestedColumnWidth = Utils.dpToPx(50, displayMetrics);
                    int requestedHorizontalSpacing = Utils.dpToPx(8, displayMetrics);
                    int gridWidth = elv.getWidth();
                    if (gridWidth == 0) {
                        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
                        Display display = wm.getDefaultDisplay();
                        Point size = new Point();
                        display.getSize(size);
                        gridWidth = size.x;
                    }

                    int mNumColumns = (gridWidth + requestedHorizontalSpacing) / (requestedColumnWidth + requestedHorizontalSpacing);

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
            iconSize = Utils.dpToPx(64, dm);
            paddingSize = Utils.dpToPx(5, dm);
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
                    SharedPreferences prefs = CommonUI.CONTEXT.getSharedPreferences(Common.PREFERENCES_NAME, Context.MODE_WORLD_READABLE);
                    SharedPreferences.Editor editor = prefs.edit();

                    String key = "selectedicons";
                    if (mode == MODE_PICK_SHORTCUT_ICON) {
                        key = "shortcuticons";
                        appComponentName = "" + itemID;
                    } else if (mode == MODE_PICK_FOLDER_ICON) {
                        key = "foldericons";
                        appComponentName = "" + itemID;
                    }

                    HashSet<String> selectedIcons = (HashSet<String>) prefs.getStringSet(key, new HashSet<String>());

                    Iterator it = selectedIcons.iterator();
                    while (it.hasNext()) {
                        String[] item = it.next().toString().split("\\|");
                        if (item[0].equals(appComponentName)) {
                            it.remove();
                        }
                    }

                    selectedIcons.add(appComponentName + "|" + currentIconPack + "|" + v.getTag());

                    editor.remove(key).commit();
                    editor.putStringSet(key, selectedIcons).commit();

                    mActivity.setResult(RESULT_OK);
                    finishActivity(false);
                }
            });

            new ImageLoader().execute(imageView, mContext, icons.get(position));
            return imageView;
        }

        public class ImageLoader extends AsyncTask<Object, Void, Void> {
            ImageView image;
            Drawable icon;

            @Override
            protected Void doInBackground(Object... params) {

                image = (ImageView) params[0];
                IconPreview iconPreview = (IconPreview) params[2];
                icon = iconPreview.getIcon() != null
                        ? iconPreview.getIcon()
                        : ((Context) params[1]).getResources().getDrawable(iconPreview.getResID());
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                image.setImageDrawable(icon);
            }
        }
    }
}