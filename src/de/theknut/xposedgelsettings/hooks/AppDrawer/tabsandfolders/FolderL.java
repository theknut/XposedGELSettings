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
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Classes;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Fields;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Methods;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getIntField;
import static de.robv.android.xposed.XposedHelpers.getLongField;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.newInstance;
import static de.robv.android.xposed.XposedHelpers.setBooleanField;
import static de.robv.android.xposed.XposedHelpers.setObjectField;

/**
 * Created by Alexander Schulz on 22.09.2014.
 */
public class FolderL extends Folder implements View.OnLongClickListener, View.OnClickListener {

    public FolderL(String folderCfg) {
        this(folderCfg, true);
    }

    public FolderL(String folderCfg, boolean initData) {
        super(folderCfg, initData);
    }

    public FolderL(Intent intent, boolean initData) {
        super(intent, initData);
    }

    public View makeFolderIcon(ViewGroup appsCustomizeCellLayout) {
        if (folderIcon == null) {
            createFolderIcon(appsCustomizeCellLayout);
        } else {
            if (folderIcon.getParent() != null) {
                ((ViewGroup) folderIcon.getParent()).removeView(folderIcon);
            }
        }
        return folderIcon;
    }

    protected void createFolderIcon(ViewGroup appsCustomizeCellLayout) {
        int id = Common.LAUNCHER_CONTEXT.getResources().getIdentifier("folder_icon", "layout", Common.HOOKED_PACKAGE);
        if (id != 0) {
            Object folderInfo = newInstance(Classes.FolderInfo);
            setObjectField(folderInfo, "title", getTitle());
            setObjectField(folderInfo, Fields.iiScreenId, FOLDER_ID);
            setObjectField(folderInfo, Fields.iiContainer, -101);
            setObjectField(folderInfo, Fields.iiID, getId());

            if (Common.PACKAGE_OBFUSCATED && Common.GNL_VERSION >= ObfuscationHelper.GNL_4_2_16) {
                folderIcon = (View) callStaticMethod(Classes.FolderIcon, Methods.fiFromXml, id, Common.LAUNCHER_INSTANCE, appsCustomizeCellLayout, folderInfo, getObjectField(Common.LAUNCHER_INSTANCE, Fields.lIconCache));
            } else if (Common.PACKAGE_OBFUSCATED) {
                folderIcon = (View) callStaticMethod(Classes.FolderIcon, Methods.fiFromXml, id, Common.LAUNCHER_INSTANCE, appsCustomizeCellLayout, folderInfo);
            } else {
                folderIcon = (View) callStaticMethod(Classes.FolderIcon, Methods.fiFromXml, id, Common.LAUNCHER_INSTANCE, appsCustomizeCellLayout, folderInfo, getObjectField(Common.LAUNCHER_INSTANCE, Fields.lIconCache));
            }

            TextView folderName = (TextView) getObjectField(folderIcon, Fields.fiFolderName);
            setBooleanField(folderName, Fields.btvShadowsEnabled, false);
            folderName.getPaint().clearShadowLayer();

            EditText folderEditName = ((EditText) getObjectField(getObjectField(folderIcon, Fields.fiFolder), Fields.fFolderEditText));
            folderEditName.setKeyListener(null);
            final FolderL folder = this;
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

            addItems();
        }
    }

    protected void addItems() {
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
                            app.setOnLongClickListener(null);
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

    public void closeFolder() {
        callMethod(Common.LAUNCHER_INSTANCE, Methods.lCloseFolder);
    }
}