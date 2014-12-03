package de.theknut.xposedgelsettings.hooks.appdrawer.tabsandfolders;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Classes;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Fields;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Methods;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.getIntField;
import static de.robv.android.xposed.XposedHelpers.getLongField;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.newInstance;
import static de.robv.android.xposed.XposedHelpers.setBooleanField;
import static de.robv.android.xposed.XposedHelpers.setObjectField;

/**
 * Created by Alexander Schulz on 22.09.2014.
 */
public class Folder extends AppDrawerItem implements View.OnLongClickListener, View.OnClickListener {

    public static final String KEY_PREFIX= "folder";
    public static final long FOLDER_ID = 0xABCDEF;

    private View folderIcon;
    private long tabId = Tab.APPS_ID;

    public Folder(String folderCfg) {
        this(folderCfg, true);
    }

    public Folder(String folderCfg, boolean initData) {
        String[] settings = folderCfg.split("\\|");

        for (String setting : settings) {
            if (setting.startsWith("idx=")) {
                this.idx = Integer.parseInt(setting.split("=")[1]);
            } else if (setting.startsWith("id=")) {
                this.id = Long.parseLong(setting.split("=")[1]);
            } else if (setting.startsWith("title=")) {
                this.title = setting.split("=")[1];
            } else if (setting.startsWith("hide=")) {
                this.hideFromAppsPage = Boolean.parseBoolean(setting.split("=")[1]);
            } else if (setting.startsWith("tabid=")) {
                this.tabId = Long.parseLong(setting.split("=")[1]);
            }
        }

        if (initData) initData();
    }

    public Folder(Intent intent, boolean initData) {
        this.id = intent.getLongExtra("itemid", -1);
        this.idx = intent.getIntExtra("index", -1);
        this.title = intent.getStringExtra("name");
        this.tabId = intent.getLongExtra("tabid", Tab.APPS_ID);
        this.hideFromAppsPage = intent.getBooleanExtra("hide", false);

        if (initData) initData();
    }

    public View makeFolderIcon(ViewGroup appsCustomizeCellLayout) {
        if (folderIcon == null) {
            create(appsCustomizeCellLayout);
        } else {
            if (folderIcon.getParent() != null) {
                ((ViewGroup) folderIcon.getParent()).removeView(folderIcon);
            }
        }
        return folderIcon;
    }

    public View getFolderIcon() {
        return folderIcon;
    }

    private void create(ViewGroup appsCustomizeCellLayout) {
        int id = Common.LAUNCHER_CONTEXT.getResources().getIdentifier("folder_icon", "layout", Common.HOOKED_PACKAGE);
        if (id != 0) {
            Object folderInfo = newInstance(Classes.FolderInfo);
            setObjectField(folderInfo, "title", getTitle());
            setObjectField(folderInfo, Fields.iiScreenId, FOLDER_ID);
            setObjectField(folderInfo, Fields.iiContainer, -101);
            setObjectField(folderInfo, Fields.iiID, getId());

            if (Common.PACKAGE_OBFUSCATED) {
                folderIcon = (View) callStaticMethod(Classes.FolderIcon, Methods.fiFromXml, id, Common.LAUNCHER_INSTANCE, appsCustomizeCellLayout, folderInfo);
            } else {
                folderIcon = (View) callStaticMethod(Classes.FolderIcon, Methods.fiFromXml, id, Common.LAUNCHER_INSTANCE, appsCustomizeCellLayout, folderInfo, getObjectField(Common.LAUNCHER_INSTANCE, Fields.lIconCache));
            }

            TextView folderName = (TextView) getObjectField(folderIcon, Fields.fiFolderName);
            setBooleanField(folderName, Fields.btvShadowsEnabled, false);
            folderName.getPaint().clearShadowLayer();

            EditText folderEditName = ((EditText) getObjectField(getObjectField(folderIcon, Fields.fiFolder), Fields.fFolderEditText));
            folderEditName.setKeyListener(null);
            final Folder folder = this;
            folderEditName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        folder.closeFolder();
                        FolderHelper.getInstance().setupFolderSettings(folder);
                    }
                }
            });

            folderIcon.setOnLongClickListener(this);
            folderIcon.setOnClickListener(this);

            ImageView background = (ImageView) getObjectField(folderIcon, Fields.fiPreviewBackground);
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) background.getLayoutParams();

            int padding = getIntField(Common.DEVICE_PROFIL, Fields.dpIconDrawablePaddingPx);
            folderIcon.setPadding(0, padding, 0, 0);
            layoutParams.height = layoutParams.width = (int) Math.ceil(Common.APP_DRAWER_ICON_SIZE);

            callMethod(callMethod(appsCustomizeCellLayout, Methods.clGetShortcutsAndWidgets), Methods.sawMeasureChild, folderIcon);
            TextView t = ((TextView) getObjectField(folderIcon, Fields.fiFolderName));
            t.setTextSize(0, getIntField(Common.DEVICE_PROFIL, Fields.dpIconTextSize));
            ((ViewGroup.MarginLayoutParams) t.getLayoutParams()).setMargins(0, (int) Math.ceil(Common.APP_DRAWER_ICON_SIZE + padding), 0, 0);

            addItems();
        }
    }

    private void addItems() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                initData();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                for (Object app : data) {
                    addItem(app);
                }

                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        ViewGroup contents = (ViewGroup) callMethod(getObjectField(getObjectField(folderIcon, Fields.fiFolder), Fields.fContent), Methods.clGetShortcutsAndWidgets);
                        Object appsCustomizePagedView = getObjectField(Common.LAUNCHER_INSTANCE, Fields.lAppsCustomizePagedView);
                        for (int i = 0; i < contents.getChildCount(); i++) {
                            TextView app = (TextView) contents.getChildAt(i);
                            app.setOnTouchListener((View.OnTouchListener) appsCustomizePagedView);
                            app.setOnLongClickListener((View.OnLongClickListener) appsCustomizePagedView);
                            app.setOnKeyListener((View.OnKeyListener) appsCustomizePagedView);
                        }

                        return null;
                    }
                }.execute();
            }
        }.execute();
    }

    public void addItem(Object shortcutInfo) {
        if (folderIcon != null || shortcutInfo != null) {
            callMethod(folderIcon, Methods.fiAddItem, shortcutInfo);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        FolderHelper.getInstance().setupFolderSettings(this);
        return true;
    }

    private void initData() {
        parseData(KEY_PREFIX);

        sort(data);
        ArrayList tmp = new ArrayList(data.size());
        for (Object app : data) {
            tmp.add(callMethod(app, Methods.aiMakeShortcut));
        }
        data = new ArrayList(tmp);
    }

    public ArrayList<String> getRawData() {
        return getRawData(KEY_PREFIX);
    }

    public void invalidate() {
        ((ViewGroup) folderIcon.getParent()).removeView(folderIcon);
        folderIcon = null;
    }

    public Context getContext() {
        if (folderIcon != null) {
            return folderIcon.getContext();
        }
        return null;
    }

    public boolean isOpen() {
        ViewGroup dragLayer = (ViewGroup) callMethod(Common.LAUNCHER_INSTANCE, Methods.lGetDragLayer);
        for (int i = 0; i < dragLayer.getChildCount(); i++) {
            View child = dragLayer.getChildAt(i);
            if (child.getClass().equals(Classes.Folder)) {
                return getLongField(getObjectField(child, Fields.fFolderInfo), Fields.iiID) == getId();
            }
        }
        return false;
    }

    public long getTabId() {
        return tabId;
    }

    public void closeFolder() {
        callMethod(Common.LAUNCHER_INSTANCE, Methods.lCloseFolder);
    }

    @Override
    public String toString() {
        return super.toString() + "|"
                + "tabid=" + tabId;
    }

    @Override
    public void onClick(View v) {
        callMethod(Common.LAUNCHER_INSTANCE, Methods.lOpenFolder, folderIcon);
    }

    public boolean contains(String o) {
        for (String cmp : getRawData()) {
            if (cmp.contains(o)) return true;
        }
        return false;
    }
}