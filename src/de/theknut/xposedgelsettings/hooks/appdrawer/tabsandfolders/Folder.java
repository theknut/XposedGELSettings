package de.theknut.xposedgelsettings.hooks.appdrawer.tabsandfolders;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Classes;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Fields;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Methods;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.getLongField;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

/**
 * Created by Alexander Schulz on 22.09.2014.
 */
public class Folder extends AppDrawerItem implements View.OnLongClickListener, View.OnClickListener {

    public static final String KEY_PREFIX= "folder";
    public static final long FOLDER_ID = 0xABCDEF;

    protected View folderIcon;
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

    public void invalidate() {}
    protected void addItems() {}
    protected void addItem(Object shortcutInfo) {}
    public View makeFolderIcon(ViewGroup appsCustomizeCellLayout) { return null; }
    protected void createFolderIcon(ViewGroup appsCustomizeCellLayout) {}

    public View getFolderIcon() {
        return folderIcon;
    }

    @Override
    public boolean onLongClick(View v) {
        FolderHelper.getInstance().setupFolderSettings(this);
        return true;
    }

    protected void initData() {
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

    public Context getContext() {
        if (folderIcon != null) {
            return folderIcon.getContext();
        }
        return null;
    }

    public boolean isOpen() {
        for (int i = 0; i < Common.DRAG_LAYER.getChildCount(); i++) {
            View child = Common.DRAG_LAYER.getChildAt(i);
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