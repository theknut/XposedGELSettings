package de.theknut.xposedgelsettings.hooks.icon;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.Utils;
import de.theknut.xposedgelsettings.ui.CommonUI;

public class IconPack {

    static final String ICONBACK = "iconback";
    static final String ICONMASK = "iconmask";
    static final String ICONUPON = "iconupon";

    static final int COMPONENTNAME = 0;
    static final int ICONPACKNAME = 1;
    static final int DRAWABLENAME = 2;

    private List<IconInfo> appFilter;
    private List<Icon> icons;
    private List<IconInfo> calendarIcon;
    private LinkedHashMap<String, List<IconPreview>> previewIcons;
    private List<String> unthemedIcons;

    private List<String> iconTheme;
    private float scaleFactor = 0.75f;
    private String packageName;
    private Resources resources;
    private Context context;
    private Random rand;
    private int launcherDPI;

    private HashMap<String, Drawable> iconUponMask;
    private List<Drawable> iconBack;
    private Bitmap iconMask, iconUpon;
    private Canvas mCanvas = new Canvas();
    private Paint mPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
    private Paint mMaskPaint = new Paint();

    private static int dayOfMonth;

    public IconPack(Context context, String packageName) throws NameNotFoundException {
        this.packageName = packageName;
        this.launcherDPI = context.getResources().getDisplayMetrics().densityDpi;
        this.unthemedIcons = new ArrayList<String>();
        this.icons = new ArrayList<Icon>();
        this.calendarIcon = new ArrayList<IconInfo>();
        setDayOfMonth(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        mMaskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));

        if (!packageName.equals(Common.ICONPACK_DEFAULT)) {
            this.iconUponMask = new HashMap<String, Drawable>();
            this.appFilter = new ArrayList<IconInfo>();
            this.iconTheme = Arrays.asList(ICONBACK, ICONMASK, ICONUPON);
            this.iconBack = new ArrayList<Drawable>();
            this.context = context.createPackageContext(this.packageName, Context.CONTEXT_IGNORE_SECURITY);
            this.resources = this.context.getResources();
            this.rand = new Random();
        } else {
            this.context = context;
            this.resources = this.context.getResources();
        }
    }

    public Context getContext() {
        return context;
    }

    public Resources getResources() {
        return resources;
    }

    public List<Icon> getIcons() {
        return icons;
    }

    public List<String> getUnthemedIcons() {
        return unthemedIcons;
    }

    public List<IconInfo> getAppFilter() {
        return appFilter;
    }

    public String getPackageName() {
        return packageName;
    }

    public float getScaleFactor() {
        return scaleFactor;
    }

    public void setScaleFactor(float scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    public Drawable getIconMask() {
        return this.iconUponMask.get(ICONMASK);
    }

    public Bitmap getIconMaskBitmap() {
        if (iconMask == null) {
            iconMask = CommonUI.drawableToBitmap(getIconMask());
        }

        return iconMask;
    }

    public boolean hasIconMask() {
        return this.iconUponMask != null && this.iconUponMask.get(ICONMASK) != null;
    }

    public boolean hasIconBack() {
        return this.iconBack != null && this.iconBack.size() != 0;
    }

    public boolean hasIconUpon() {
        return this.iconUponMask != null && this.iconUponMask.get(ICONUPON) != null;
    }

    public Drawable getIconBack() {
        return this.iconBack.get(this.rand.nextInt(this.iconBack.size()));
    }

    public Bitmap getIconBackBitmap() {
        return CommonUI.drawableToBitmap(getIconBack());
    }

    public Bitmap getIconUponBitmap() {
        if (iconUpon == null) {
            iconUpon = CommonUI.drawableToBitmap(this.iconUponMask.get(ICONUPON));
        }

        return iconUpon;
    }

    public List<IconInfo> getCalendarIcons() {
        return this.calendarIcon;
    }

    public int getDPI() {
        return launcherDPI;
    }

    public static int getDayOfMonth() {
        return IconPack.dayOfMonth;
    }

    public static void updateDayOfMonth() {
        IconPack.dayOfMonth = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }

    public void setDayOfMonth(int dayOfMonth) {
        IconPack.dayOfMonth = dayOfMonth;
    }

    public boolean shouldThemeMissingIcons() {
        return hasIconBack() || hasIconMask() || hasIconUpon();
    }

    public boolean isAppFilterLoaded() {
        return (appFilter != null && appFilter.size() != 0)
                || shouldThemeMissingIcons();
    }

    public void onDateChanged() {
        IconPack.updateDayOfMonth();
        List<ResolveInfo> calendars = IconHooks.getCalendars();

        Iterator<Icon> it = icons.iterator();
        while (it.hasNext()) {
            Icon icon = it.next();
            for (ResolveInfo calendar : calendars) {
                if (icon.getPackageName().contains(calendar.activityInfo.packageName)) {
                    it.remove();
                }
            }
        }
    }

    public Bitmap themeIcon(Bitmap tmpIcon) {
        Bitmap tmpFinalIcon = Bitmap.createBitmap(tmpIcon.getWidth(), tmpIcon.getHeight(), Config.ARGB_8888);
        drawFinalIcon(tmpFinalIcon, tmpIcon);
        return tmpFinalIcon;
    }

    private void drawFinalIcon(Bitmap finalIcon, Bitmap originalIcon) {

        Canvas c = new Canvas();
        c.setBitmap(finalIcon);

        if (hasIconBack()) {
            Bitmap iconBack = Bitmap.createScaledBitmap(getIconBackBitmap(), originalIcon.getWidth(), originalIcon.getHeight(), true);
            c.drawBitmap(iconBack, 0, 0, mPaint);
        } else {
            setScaleFactor(1.0f);
        }

        if (hasIconMask()) {
            Bitmap iconMask = Bitmap.createScaledBitmap(getIconMaskBitmap(), originalIcon.getWidth(), originalIcon.getHeight(), true);
            Bitmap tmpMask = Bitmap.createBitmap(iconMask.getWidth(), iconMask.getHeight(), Config.ARGB_8888);
            applyMaskToBitmap(tmpMask, originalIcon, iconMask, getScaleFactor());
            c.drawBitmap(tmpMask, 0, 0, mPaint);
        } else {
            applyScaleFactor(c, finalIcon, originalIcon, getScaleFactor());
        }

        if (hasIconUpon()) {
            Bitmap iconUpon = Bitmap.createScaledBitmap(getIconUponBitmap(), originalIcon.getWidth(), originalIcon.getHeight(), true);
            c.drawBitmap(iconUpon, 0, 0, mPaint);
        }

        c.setBitmap(null);
    }

    // Copyright Kevin Barry, TeslaCoil Software
    private void applyMaskToBitmap(Bitmap dst, Bitmap src, Bitmap mask, float scale) {
        if (dst != null) {
            Canvas c = mCanvas;
            c.setBitmap(dst);

            applyScaleFactor(c, dst, src, scale);

            c.drawBitmap(mask, 0, 0, mMaskPaint);
            c.setBitmap(null);
        }
    }

        // Copyright Kevin Barry, TeslaCoil Software
        private void applyScaleFactor(Canvas c, Bitmap dst, Bitmap src, float scale) {
            c.save();
            c.translate(dst.getWidth()*.5f, dst.getHeight()*.5f);

            c.scale(scale, scale);
            int scaledW = dst.getScaledWidth(c);
            int scaledH = dst.getScaledHeight(c);
            int l = - scaledW/2;
            int t = - scaledH/2;
            c.drawBitmap(src, l, t, mPaint);
            c.restore();
        }

    public Drawable loadIcon(String pkg) {

        for (Icon icon : getIcons()) {
            if (icon.equals(pkg)) {
                return icon.getIcon();
            }
        }

        if (isAppFilterLoaded()) {
            int id = getResourceIdForDrawable(pkg);
            if (id != 0) {
                try {
                    Icon icon = new Icon(pkg, resources.getDrawableForDensity(id, getDPI()));
                    getIcons().add(icon);
                    return icon.getIcon();
                } catch (Exception e) { }
            }
        }

        unthemedIcons.add(pkg);
        return null;
    }

    public void loadSelectedIcons(Set<String> selectedIcons) {

        for (String selectedIcon : selectedIcons) {
            String[] info = selectedIcon.split("\\|");
            loadSingleIconFromIconPack(info[ICONPACKNAME], info[COMPONENTNAME], info[DRAWABLENAME]);
        }
    }

    public Drawable loadSingleIconFromIconPack(String iconPackPackageName, String componentName, String drawableName) {
        return loadSingleIconFromIconPack(iconPackPackageName, componentName, drawableName, true);
    }

    public Drawable loadSingleIconFromIconPack(String iconPackPackageName, String componentName, String drawableName, boolean addToCache) {
        Resources res = getResources();
        int id = 0;

        if (isDefault(iconPackPackageName)) {
            try {
                iconPackPackageName = drawableName.substring(0, drawableName.indexOf(":"));
                Context iconPackContext = getContext().createPackageContext(iconPackPackageName, Context.CONTEXT_IGNORE_SECURITY);
                res = iconPackContext.getResources();
                id = res.getIdentifier(drawableName.split("/")[1], drawableName.split("/")[0].split(":")[1], iconPackPackageName);
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
        } else if (iconPackPackageName.equals("sdcard")) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    &&  (ActivityCompat.checkSelfPermission(Common.LAUNCHER_CONTEXT, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    ||  ActivityCompat.checkSelfPermission(Common.LAUNCHER_CONTEXT, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                Utils.requestPermission(Common.LAUNCHER_INSTANCE, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, Common.PERMISSION_REQUEST_RESTART);
                return null;
            }

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile("/mnt/sdcard/XposedGELSettings/icons/" + drawableName + ".png", options);
            if (bitmap == null) return null;
            Drawable icon = new BitmapDrawable(res, bitmap);
            if (addToCache) getIcons().add(new Icon(drawableName.replace("#", "/"), icon));
            return icon;
        } else {
            if (!iconPackPackageName.equals(getPackageName())) {
                try {
                    Context iconPackContext = getContext().createPackageContext(iconPackPackageName, Context.CONTEXT_IGNORE_SECURITY);
                    res = iconPackContext.getResources();
                } catch (NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
            id = res.getIdentifier(drawableName, "drawable", iconPackPackageName);
        }

        if (id != 0) {
            Drawable icon = res.getDrawableForDensity(id, getDPI());
            if (addToCache) getIcons().add(new Icon(componentName, icon));
            return icon;
        }

        return null;
    }

    public int getResourceIdForDrawable(String pkg) {
        List<String> history = new ArrayList<String>();
        int id = 0;

        while (true) {
            String drawableName = getDrawableName(pkg, history);

            if (drawableName != "") {
                id = resources.getIdentifier(drawableName, "drawable", getPackageName());

                if (id == 0) {
                    history.add(drawableName);
                    continue;
                }

                return id;
            } else {
                break;
            }
        }

        return 0;
    }

    public String getDrawableName(String pkg, List<String> history) {
        String name = getIconNameExact(pkg, history);
        if (name == "") {
            name = getIconNameRelative(pkg, history);
        }

        return name;
    }

    public String getIconNameExact(String pkg, List<String> history) {

        for (IconInfo icon : appFilter) {
            if (icon.getComponentName().equals(pkg) && !(history.contains(icon.getDrawableName()))) {
                return icon.getDrawableName();
            }
        }

        return "";
    }

    public String getIconNameRelative(String pkg, List<String> history) {

        for (IconInfo icon : appFilter) {

            String iconName = pkg;
            if (iconName.contains("/")) {
                iconName = iconName.substring(0, iconName.indexOf('/'));
            }

            if (icon.getComponentName().contains(iconName) && !(history.contains(icon.getDrawableName()))) {
                return icon.getDrawableName();
            }
        }

        return "";
    }

    public int getTotalIconCount() {
        List<String> names = new ArrayList<String>();
        for (IconInfo icon : appFilter) {
            if (!names.contains(icon.getDrawableName())) {
                names.add(icon.getDrawableName());
            }
        }

        return names.size();
    }

    public List<IconInfo> loadAppFilter() {
        ArrayList<IconInfo> icons = new ArrayList<IconInfo>();
        try {
            Context resContext = getContext().createPackageContext(getPackageName(), Context.CONTEXT_IGNORE_SECURITY);
            XmlPullParserFactory factory = null;
            XmlPullParser parser = null;
            Resources res = resContext.getResources();

            int resId = res.getIdentifier("appfilter", "xml", packageName);
            if (resId != 0) {
                parser = res.getXml(resId);
            } else {
                factory = XmlPullParserFactory.newInstance();
                parser = factory.newPullParser();
                parser.setInput(new InputStreamReader(res.getAssets().open("appfilter.xml"), "UTF-8"));
            }

            for (int eventType = parser.next(); eventType != XmlPullParser.END_DOCUMENT; eventType = parser.next()) {
                String name = parser.getName();
                if (name == null) continue;

                if (name.equalsIgnoreCase("item")) {
                    readIconInfo(parser);
                } else if (name.equalsIgnoreCase("calendar")) {
                    readCalendarInfo(parser);
                } else if (iconTheme.contains(name.toLowerCase(Locale.US))) {
                    readAndLoadIconBackMaskUpon(name, res, parser);
                } else if (name.equalsIgnoreCase("scale")) {
                    readScaleFactor(parser);
                } else {
                    continue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return icons;
    }

    private void readIconInfo(XmlPullParser parser) {
        try {
            parser.require(XmlPullParser.START_TAG, null, "item");

            String drawableName = parser.getAttributeValue(null, "drawable");
            if (TextUtils.isEmpty(drawableName)) {
                drawableName = parser.getAttributeValue(null, "image");
            }

            String componentName = parser.getAttributeValue(null, "component");
            if (componentName == null) {
                componentName = parser.getAttributeValue(null, "Component");
            }
            componentName = componentName.replace("ComponentInfo{", "").replace("}", "");

            if (!TextUtils.isEmpty(componentName) && !TextUtils.isEmpty(drawableName)) {
                appFilter.add(new IconInfo(componentName, drawableName));
            }

            parser.nextTag();
            parser.require(XmlPullParser.END_TAG, null, "item");
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readCalendarInfo(XmlPullParser parser) {
        try {
            parser.require(XmlPullParser.START_TAG, null, "calendar");

            String componentName = parser.getAttributeValue(null, "component")
                    .replace("ComponentInfo{", "")
                    .replace("}", "");
            String prefix = parser.getAttributeValue(null, "prefix");
            IconInfo icon = new IconInfo(componentName, null, prefix);
            appFilter.add(0, icon);
            calendarIcon.add(icon);

            parser.nextTag();
            parser.require(XmlPullParser.END_TAG, null, "calendar");
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readScaleFactor(XmlPullParser parser) {
        try {
            parser.require(XmlPullParser.START_TAG, null, "scale");
            setScaleFactor(Float.parseFloat(parser.getAttributeValue(0)));
            parser.nextTag();
            parser.require(XmlPullParser.END_TAG, null, "scale");
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readAndLoadIconBackMaskUpon(String name, Resources res, XmlPullParser parser) {

        try {
            if (name.equals(ICONBACK)) {

                for (int i = 0; i < parser.getAttributeCount(); i++) {
                    int resID = res.getIdentifier(parser.getAttributeValue(i), "drawable", getPackageName());
                    if (resID != 0) {
                        iconBack.add(res.getDrawable(resID));
                    }
                }
            } else {
                int resID = res.getIdentifier(parser.getAttributeValue(0), "drawable", getPackageName());
                if (resID != 0) {
                    iconUponMask.put(name, res.getDrawable(resID));
                }
            }

            parser.nextTag();
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadIconCategories(Context context) {

        previewIcons = new LinkedHashMap<String, List<IconPreview>>();
        ArrayList<IconPreview> icons = new ArrayList<IconPreview>();
        String currentCategory = null;

        if (isDefault()) {
            loadDefaultCategories();
            return;
        }

        try {
            Context resContext = context.createPackageContext(getPackageName(), Context.CONTEXT_IGNORE_SECURITY);
            XmlPullParserFactory factory = null;
            XmlPullParser parser = null;
            Resources res = resContext.getResources();

            int resId = res.getIdentifier("drawable", "xml", getPackageName());
            if (resId != 0) {
                parser = res.getXml(resId);
            } else {
                factory = XmlPullParserFactory.newInstance();
                parser = factory.newPullParser();
                parser.setInput(new InputStreamReader(res.getAssets().open("drawable.xml"), "UTF-8"));
            }

            for (int eventType = parser.next(); eventType != XmlPullParser.END_DOCUMENT; eventType = parser.next()) {
                String name = parser.getName();
                if (name == null) continue;

                if (name.equalsIgnoreCase("item")) {
                    readPreviewIcon(parser, icons);
                } else if (name.equalsIgnoreCase("category")) {
                    currentCategory = readCategory(parser, currentCategory, icons);
                    icons = new ArrayList<IconPreview>();
                } else {
                    continue;
                }
            }

            if (currentCategory == null) {
                currentCategory = "all icons";
            }

            previewIcons.put(currentCategory, icons);
        } catch (Exception e) {
            loadAppFilter();
            previewIcons.put("all icons", getIconPreviews(getAppFilter()));
        }
    }

    private void loadDefaultCategories() {
        ArrayList<IconPreview> icons = new ArrayList<IconPreview>();

        try {
            for (ResolveInfo info : CommonUI.getAllApps()) {
                Resources res = getContext().createPackageContext(info.activityInfo.packageName, 0).getResources();
                IconPreview icon = new IconPreview(info.getIconResource(), res.getResourceName(info.getIconResource()));
                icon.setIcon(res.getDrawable(icon.getResID()));
                icons.add(icon);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        previewIcons.put("all icons", icons);
    }

    private ArrayList<IconPreview> getIconPreviews(List<IconInfo> appFilter) {
        ArrayList<IconPreview> result = new ArrayList<IconPreview>();
        List<Integer> history = new ArrayList<Integer>();

        for (IconInfo iconInfo : appFilter) {
            int id = resources.getIdentifier(iconInfo.getDrawableName(), "drawable", getPackageName());
            if (id != 0 && !history.contains(id)) {
                history.add(id);
                result.add(new IconPreview(id, iconInfo.getDrawableName()));
            }
        }

        return result;
    }

    private String readCategory(XmlPullParser parser, String currentCategory, List<IconPreview> icons) {
        try {
            String name;
            parser.require(XmlPullParser.START_TAG, null, "category");

            if (currentCategory == null) {
                name = parser.getAttributeValue(0);
                parser.nextTag();
                return name;
            }

            if (icons.size() != 0) {
                previewIcons.put(currentCategory, icons);
            }

            name = parser.getAttributeValue(0);

            parser.nextTag();
            parser.require(XmlPullParser.END_TAG, null, "category");

            return name;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void readPreviewIcon(XmlPullParser parser, List<IconPreview> icons) {
        try {
            parser.require(XmlPullParser.START_TAG, null, "item");

            String drawableName = parser.getAttributeValue(0);
            int id = resources.getIdentifier(drawableName, "drawable", getPackageName());
            if (id != 0) {
                icons.add(new IconPreview(id, drawableName));
            }

            parser.nextTag();
            parser.require(XmlPullParser.END_TAG, null, "item");
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, List<IconPreview>> getIconPreviews() {
        return previewIcons;
    }

    public boolean isDefault() {
        return getPackageName().equals(Common.ICONPACK_DEFAULT);
    }

    public boolean isDefault(String packageName) {
        return packageName.equals(Common.ICONPACK_DEFAULT);
    }
}