package de.theknut.xposedgelsettings.hooks.appdrawer.tabsandfolders;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import de.robv.android.xposed.XposedBridge;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Classes;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Fields;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Methods;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.newInstance;
import static de.robv.android.xposed.XposedHelpers.setBooleanField;
import static de.robv.android.xposed.XposedHelpers.setObjectField;

/**
 * Created by Alexander Schulz on 22.09.2014.
 */
public class FolderM extends Folder implements View.OnLongClickListener, View.OnClickListener {

    public FolderM(String folderCfg) {
        this(folderCfg, true);
    }

    public FolderM(String folderCfg, boolean initData) {
        super(folderCfg, initData);
    }

    public FolderM(Intent intent, boolean initData) {
        super(intent, initData);
    }

    public View makeFolderIcon(ViewGroup allAppsGridAdapter) {
        if (folderIcon == null) {
            createFolderIcon(allAppsGridAdapter);
        } else {
            if (folderIcon.getParent() != null) {
                ((ViewGroup) folderIcon.getParent()).removeView(folderIcon);
            }
        }
        return folderIcon;
    }

    protected void createFolderIcon(ViewGroup appsCustomizeCellLayout) {
        initData();
        int id = Common.LAUNCHER_CONTEXT.getResources().getIdentifier("folder_icon", "layout", Common.HOOKED_PACKAGE);
        if (id != 0) {
            XposedBridge.log("Make folder icon");
            Object folderInfo = newInstance(Classes.FolderInfo);
            setObjectField(folderInfo, "title", getTitle());
            setObjectField(folderInfo, Fields.iiScreenId, FOLDER_ID);
            setObjectField(folderInfo, Fields.iiContainer, -101);
            setObjectField(folderInfo, Fields.iiID, getId());

            folderIcon = (View) callStaticMethod(Classes.FolderIcon, Methods.fiFromXml, id, Common.LAUNCHER_INSTANCE, appsCustomizeCellLayout, folderInfo, getObjectField(Common.LAUNCHER_INSTANCE, Fields.lIconCache));

            TextView folderName = (TextView) getObjectField(folderIcon, Fields.fiFolderName);
            setBooleanField(folderName, Fields.btvShadowsEnabled, false);
            folderName.getPaint().clearShadowLayer();

            EditText folderEditName = ((EditText) getObjectField(getObjectField(folderIcon, Fields.fiFolder), Fields.fFolderEditText));
            folderEditName.setKeyListener(null);
            final FolderM folder = this;
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
/*
            ImageView background = (ImageView) getObjectField(folderIcon, Fields.fiPreviewBackground);
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) background.getLayoutParams();

            int padding = getIntField(Common.DEVICE_PROFIL, Fields.dpFolderBackgroundOffset);
            background.setPadding(0, -padding, 0, 0);
            folderIcon.setPadding(0, -padding, 0, 0);
            layoutParams.topMargin = getIntField(Common.DEVICE_PROFIL, Fields.dpFolderBackgroundOffset);

            int folderIconSize = Common.APP_DRAWER_ICON_SIZE + (2 * - getIntField(Common.DEVICE_PROFIL, Fields.dpFolderBackgroundOffset));
            layoutParams.height = layoutParams.width = folderIconSize;

            callMethod(callMethod(appsCustomizeCellLayout, Methods.clGetShortcutsAndWidgets), Methods.sawMeasureChild, folderIcon);
            TextView t = ((TextView) getObjectField(folderIcon, Fields.fiFolderName));
            t.setCompoundDrawablePadding(0);
            t.setTextSize(0, getIntField(Common.DEVICE_PROFIL, Fields.dpIconTextSize));
            ((ViewGroup.MarginLayoutParams) t.getLayoutParams()).topMargin = Common.APP_DRAWER_ICON_SIZE + getIntField(Common.DEVICE_PROFIL, Fields.dpIconDrawablePaddingPx);
*/
            XposedBridge.log("add items");
            addItems();
        }
    }

    protected void addItems() {
        for (Object app : data) {
            addItem(app);
        }
    }

    public void addItem(Object shortcutInfo) {
        if (folderIcon != null && shortcutInfo != null) {
            XposedBridge.log("add "+ shortcutInfo);
            callMethod(folderIcon, Methods.fiAddItem, shortcutInfo);
        }
    }

    public void invalidate() {
        ((ViewGroup) folderIcon.getParent()).removeView(folderIcon);
        folderIcon = null;
    }

    public void closeFolder() {
        callMethod(Common.LAUNCHER_INSTANCE, Methods.lCloseFolder);
    }
}