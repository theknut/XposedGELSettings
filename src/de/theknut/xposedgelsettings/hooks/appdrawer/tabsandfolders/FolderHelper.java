package de.theknut.xposedgelsettings.hooks.appdrawer.tabsandfolders;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import de.theknut.xposedgelsettings.R;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Classes;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Fields;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Methods;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;
import de.theknut.xposedgelsettings.hooks.Utils;
import de.theknut.xposedgelsettings.ui.AllAppsList;
import de.theknut.xposedgelsettings.ui.SaveActivity;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.getLongField;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

/**
 * Created by Alexander Schulz on 26.09.2014.
 */
public final class FolderHelper {

    private static final FolderHelper INSTANCE = new FolderHelper();

    private Context XGELSContext;
    private ArrayList<Folder> folders;
    private AlertDialog folderSettingsDialog;

    public static FolderHelper getInstance() {
        return INSTANCE;
    }

    public void init() {
        this.XGELSContext = Common.XGELSCONTEXT;
        this.folders = new ArrayList<Folder>();
        //initFolders();
    }

    private void initFolders() {
        for (String item : PreferencesHelper.appdrawerFolderData) {
            folders.add(Common.IS_M_GNL ? new FolderM(item) : new FolderL(item));
        }

        Collections.sort(folders, new Comparator<Folder>() {
            @Override
            public int compare(Folder lhs, Folder rhs) {
                return lhs.getIndex() - rhs.getIndex();
            }
        });

        for (Folder folder : folders) {
            folder.initData();
            folder.makeFolderIcon((ViewGroup) getObjectField(Common.APP_DRAWER_INSTANCE, "mAppsRecyclerView"));
        }
    }

    public boolean hasFolder() {
        return folders.size() != 0;
    }

    public ArrayList<Folder> getFoldersForTab(long tabId) {
        ArrayList<Folder> folders = new ArrayList<Folder>();
        for (Folder folder : this.folders) {
            if (folder.getTabId() == tabId) {
                folders.add(folder);
            }
        }
        return folders;
    }

    public Folder getFolder(long id) {
        for (Folder folder : folders) {
            if (folder.getId() == id) {
                return folder;
            }
        }
        return null;
    }

    public ArrayList getAllApps() {
        return ((ArrayList) getObjectField(getObjectField(Common.LAUNCHER_INSTANCE, Fields.lAppsCustomizePagedView), Fields.acpvAllApps));
    }

    public void addFolder(Folder folder) {
        folders.add(folder);
        TabHelper.getInstance().invalidate();
        Toast.makeText(Common.LAUNCHER_CONTEXT, XGELSContext.getString(R.string.toast_appdrawer_folderadded_title), Toast.LENGTH_LONG).show();
        Toast.makeText(Common.LAUNCHER_CONTEXT, XGELSContext.getString(R.string.toast_appdrawer_folderadded_title), Toast.LENGTH_LONG).show();
    }

    private void syncIndexes() {
        int i = 0;

        Collections.sort(folders, new Comparator<Folder>() {
            @Override
            public int compare(Folder lhs, Folder rhs) {
                return lhs.getIndex() - rhs.getIndex();
            }
        });

        Iterator<Folder> it = folders.iterator();
        while (it.hasNext()) {
            it.next().setIndex(i++);
        }
    }

    public void setupFolderSettings(final Folder folder) {
        setupFolderSettings(folder, folder.getTabId());
    }

    public void setupFolderSettings(final Folder folder, final long tabId) {
        boolean newFolder = folder == null;
        final ViewGroup folderSettingsView = (ViewGroup) LayoutInflater.from(Common.XGELSCONTEXT).inflate(R.layout.folder_settings_view, null);
        final EditText editText = (EditText) folderSettingsView.findViewById(R.id.foldername);

        if (newFolder) {
            folderSettingsView.findViewById(R.id.folder_settings_bar).setVisibility(View.GONE);
            folderSettingsView.findViewById(R.id.folder_settings_additional).setVisibility(View.GONE);
        } else {
            editText.setText(folder.getTitle());
        }

        int padding = Math.round(XGELSContext.getResources().getDimension(R.dimen.folder_menu_padding));
        folderSettingsDialog = new AlertDialog.Builder(Common.LAUNCHER_INSTANCE).create();
        folderSettingsDialog.setView(folderSettingsView, padding, padding, padding, padding);

        if (!newFolder) {
            ImageView save = (ImageView) folderSettingsView.findViewById(R.id.folder_save_settings);
            Utils.setDrawableSelector(save);
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    folderSettingsDialog.dismiss();

                    String title = editText.getText().toString().trim();
                    if (!folder.getTitle().equals(title)) {
                        for (Folder folder1 : folders) {
                            if (folder.getId() == folder1.getId()) {
                                folder1.setTitle(title);

                                ((TextView) getObjectField(folder.getFolderIcon(), Fields.fiFolderName)).setText(folder.getTitle());
                                ((EditText) getObjectField(getObjectField(folder.getFolderIcon(), Fields.fiFolder), Fields.fFolderEditText)).setText(folder.getTitle());

                                Intent intent = getBaseIntent(false, folder.getId(), folder.getTitle());
                                Common.LAUNCHER_CONTEXT.startActivity(intent);
                            }
                        }
                    }
                }
            });

            final ImageView manageApps = (ImageView) folderSettingsView.findViewById(R.id.manageapps);
            manageApps.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    folderSettingsDialog.dismiss();

                    ArrayList<String> items = new ArrayList<String>();
                    Object mFolder = getObjectField(folder.getFolderIcon(), Fields.fiFolder);
                    ArrayList<View> folderItems = (ArrayList<View>) callMethod(mFolder, Methods.fGetItemsInReadingOrder);

                    for (View item : folderItems) {
                        items.add(((Intent) callMethod(item.getTag(), "getIntent")).getComponent().flattenToString());
                    }

                    String folderName = "" + ((TextView) getObjectField(folder.getFolderIcon(), Fields.fiFolderName)).getText();
                    Intent intent = getBaseIntent(true, getLongField(folder.getFolderIcon().getTag(), Fields.iiID), folderName);
                    intent.setComponent(new ComponentName(Common.PACKAGE_NAME, AllAppsList.class.getName()));
                    intent.putExtra("mode", AllAppsList.MODE_SELECT_FOLDER_APPS);
                    intent.putExtra("save", true);
                    intent.putStringArrayListExtra("items", items);
                    Common.LAUNCHER_CONTEXT.startActivity(intent);

                    Common.CURRENT_CONTEXT_MENU_ITEM = folder.getFolderIcon();
                }
            });

            Utils.setDrawableSelector(manageApps);

            final ImageView deleteFolder = (ImageView) folderSettingsView.findViewById(R.id.deletefolder);
            deleteFolder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(Common.LAUNCHER_CONTEXT, XGELSContext.getString(R.string.toast_tab_delete), Toast.LENGTH_LONG).show();
                }
            });
            deleteFolder.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    folderSettingsDialog.dismiss();
                    long itemid = folder.getId();

                    removeFolder(folder);

                    TabHelper.getInstance().invalidate();

                    Intent intent = getBaseIntent(false, itemid, null);
                    Common.LAUNCHER_CONTEXT.startActivity(intent);
                    return true;
                }
            });
            Utils.setDrawableSelector(deleteFolder);

            final CheckBox hideApps = (CheckBox) folderSettingsView.findViewById(R.id.folder_hide_apps);
            hideApps.setChecked(folder.hideFromAppsPage());
            hideApps.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    folder.setHideFromAppsPage(isChecked);
                    Intent intent = getBaseIntent(false, folder.getId(), folder.getTitle());
                    Common.LAUNCHER_CONTEXT.startActivity(intent);

                    ArrayList allApps = (ArrayList) getObjectField(Common.APP_DRAWER_INSTANCE, Fields.acpvAllApps);
                    for (String app : folder.getRawData()) {
                        allApps.add(Utils.createAppInfo(ComponentName.unflattenFromString(app)));
                    }

                    callMethod(Common.APP_DRAWER_INSTANCE, Methods.acpvSetApps, allApps);
                    TabHelper.getInstance().invalidate();
                }
            });
        } else {
            folderSettingsView.findViewById(R.id.folder_save_settings).setVisibility(View.GONE);

            folderSettingsDialog.setButton(DialogInterface.BUTTON_POSITIVE, XGELSContext.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String newTabName = editText.getText().toString().trim();
                    if (newTabName.length() == 0) {
                        folderSettingsDialog.dismiss();
                        return;
                    }

                    Intent intent = getBaseIntent(true, getNewFolderId(), newTabName);
                    intent.putExtra("new", true);
                    intent.putExtra("index", getFoldersForTab(tabId).size());
                    intent.putExtra("tabid", tabId);

                    Common.LAUNCHER_CONTEXT.startActivity(intent);
                }
            });

            folderSettingsDialog.setButton(DialogInterface.BUTTON_NEGATIVE, XGELSContext.getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    folderSettingsDialog.dismiss();
                }
            });
        }

        folderSettingsDialog.show();
    }

    public void removeFolders(ArrayList<Folder> folders) {
        for (Folder folder : folders) {
            removeFolder(folder);
        }

        Common.LAUNCHER_INSTANCE.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = getBaseIntent(false, 0, null);
                        Common.LAUNCHER_CONTEXT.startActivity(intent);
                    }
                }, 500);
            }
        });
    }

    public void removeFolder(Folder folder) {
        if (folders.contains(folder)) {
            folders.remove(folder);

            if (folder.hideFromAppsPage()) {
                ArrayList allApps = (ArrayList) getObjectField(Common.APP_DRAWER_INSTANCE, Fields.acpvAllApps);
                for (String app : folder.getRawData()) {
                    allApps.add(Utils.createAppInfo(ComponentName.unflattenFromString(app)));
                }

                callMethod(Common.APP_DRAWER_INSTANCE, Methods.acpvSetApps, allApps);
            }
        }
    }

    public ArrayList<String> getAppsToHide() {
        ArrayList apps = new ArrayList();
        for (Folder folder : folders) {
            if (folder.hideFromAppsPage()) {
                apps.addAll(folder.getRawData());
            }
        }
        return apps;
    }

    public long getNewFolderId() {
        int i = 0;
        long[] ids = new long[folders.size()];

        Iterator<Folder> it = folders.iterator();
        while (it.hasNext()) {
            ids[i++] = it.next().getId();
        }

        Arrays.sort(ids);
        long lastId = -1;
        for (long id : ids) {
            if (id > (lastId + 1)) {
                return ++lastId;
            }
            lastId = id;
        }

        return ++lastId;
    }

    private Intent getBaseIntent(boolean openVisible, long itemid, String foldername) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        intent.setComponent(new ComponentName(Common.PACKAGE_NAME, openVisible ? AllAppsList.class.getName() : SaveActivity.class.getName()));
        intent.putExtra("mode", AllAppsList.MODE_MANAGE_FOLDER);
        intent.putExtra("itemid", itemid);
        intent.putExtra("name", foldername);
        syncIndexes();

        ArrayList<String> excludeApps = new ArrayList<String>();
        ArrayList<String> data = new ArrayList<String>(folders.size());
        for (Folder folder : folders) {
            data.add(folder.toString());
            if (folder.getId() != itemid) {
                excludeApps.addAll(folder.getRawData());
            }
        }

        if (openVisible) {
            intent.putExtra("excludeapps", excludeApps);
        }

        intent.putExtra("folderdata", data);
        return intent;
    }

    public Folder findOpenFolder() {
        for (int i = 0; i < Common.DRAG_LAYER.getChildCount(); i++) {
            View child = Common.DRAG_LAYER.getChildAt(i);
            if (child.getClass().equals(Classes.Folder)) {
                long id = getLongField(getObjectField(child, Fields.fFolderInfo), Fields.iiID);
                return FolderHelper.getInstance().getFolder(id);
            }
        }
        return null;
    }

    public void updateFolders(String pkg) {
        if (Common.IS_M_GNL) return;
        for (Folder folder : folders) {
            if (folder.contains(pkg)) {
                folder.invalidate();
            }
        }
    }
}