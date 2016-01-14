package de.theknut.xposedgelsettings.hooks.appdrawer.tabsandfolders;

import android.content.Intent;
import android.content.res.Configuration;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Method;

import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Classes;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Fields;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Methods;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.getIntField;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.newInstance;
import static de.robv.android.xposed.XposedHelpers.setBooleanField;
import static de.robv.android.xposed.XposedHelpers.setObjectField;

/**
 * Created by Alexander Schulz on 22.09.2014.
 */
public class FolderM extends Folder implements View.OnLongClickListener, View.OnClickListener {

    public static int FOLDER_ITEM_ID = 0xF01DE5;

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
            Object folderInfo = newInstance(Classes.FolderInfo);
            setObjectField(folderInfo, "title", getTitle());
            setObjectField(folderInfo, Fields.iiScreenId, FOLDER_ID);
            setObjectField(folderInfo, Fields.iiContainer, -101);
            setObjectField(folderInfo, Fields.iiID, getId());

            String methodName = Methods.fiFromXml + "$";
            for (Method method : Classes.FolderIcon.getDeclaredMethods()) {
                if (method.getName().contains(methodName)) {
                    folderIcon = (View) callStaticMethod(Classes.FolderIcon, method.getName(), id, Common.LAUNCHER_INSTANCE, appsCustomizeCellLayout, folderInfo);
                    break;
                }
            }

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

            ImageView background = (ImageView) getObjectField(folderIcon, Fields.fiPreviewBackground);
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) background.getLayoutParams();

            Object launcherAppState = callStaticMethod(Classes.LauncherAppState, "getInstance");
            Object InvDevPro = getObjectField(launcherAppState, "mInvariantDeviceProfile");
            if (Common.LAUNCHER_CONTEXT.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                Common.DEVICE_PROFIL = getObjectField(InvDevPro, "landscapeProfile");
            } else {

                Common.DEVICE_PROFIL = getObjectField(InvDevPro, "portraitProfile");
            }

            int padding = getIntField(Common.DEVICE_PROFIL, Fields.dpFolderBackgroundOffset);
            background.setPadding(0, -padding, 0, 0);
            folderIcon.setPadding(0, -padding, 0, 0);
            layoutParams.topMargin = padding;

            int folderIconSize = Common.APP_DRAWER_ICON_SIZE + (2 * - padding);
            layoutParams.height = layoutParams.width = folderIconSize;

            TextView t = ((TextView) getObjectField(folderIcon, Fields.fiFolderName));
            t.setCompoundDrawablePadding(0);
            t.setTextSize(0, getIntField(Common.DEVICE_PROFIL, Fields.dpIconTextSize));
            ((ViewGroup.MarginLayoutParams) t.getLayoutParams()).topMargin = Common.APP_DRAWER_ICON_SIZE + (padding * -1);

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
            callMethod(folderIcon, Methods.fiAddItem, shortcutInfo);
        }
    }

    public void closeFolder() {
        callMethod(Common.LAUNCHER_INSTANCE, Methods.lCloseFolder, getObjectField(folderIcon, "mFolder"), true);
    }
}