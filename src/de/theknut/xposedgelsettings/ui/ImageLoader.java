package de.theknut.xposedgelsettings.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashSet;

import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.icon.IconPack;

/**
 * Created by Alexander Schulz on 15.06.2014.
 */
public class ImageLoader extends AsyncTask<Void, Void, Void> {

    PackageManager pm;
    ResolveInfo item;
    ViewHolder holder;
    IconPack iconPack;
    Drawable icon;
    Drawable selectedIcon;
    ComponentName cmpName;
    boolean iconNotFound;

    public ImageLoader(PackageManager pm, ResolveInfo item, ViewHolder holder, IconPack iconPack) {
        this.pm = pm;
        this.item = item;
        this.holder = holder;
        this.iconPack = iconPack;
    }

    @Override
    protected Void doInBackground(Void... params) {

        cmpName = new ComponentName(item.activityInfo.packageName, item.activityInfo.name);
        if (iconPack == null) {
            this.icon = item.loadIcon(pm);
        } else {
            this.icon = iconPack.loadIcon(cmpName.flattenToString());

            if (this.icon == null) {
                iconNotFound = true;
                this.icon = item.loadIcon(pm);
            }

            SharedPreferences prefs = CommonUI.CONTEXT.getSharedPreferences(Common.PREFERENCES_NAME, Context.MODE_WORLD_READABLE);
            HashSet<String> selectedIcons = (HashSet<String>) prefs.getStringSet("selectedicons", new HashSet<String>());

            for (String i : selectedIcons) {
                String[] split = i.split("\\|");
                if (split[0].equals(cmpName.flattenToString())) {
                    selectedIcon = iconPack.loadSingleIconFromIconPack(split[1], split[0], split[2], false);
                }
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {

        holder.imageView.setImageDrawable(icon);
        if (holder.delete == null || holder.checkBox.getVisibility() == View.VISIBLE) return;

        if (holder.delete.getVisibility() == View.VISIBLE) {
            holder.textView.setTextColor(Color.parseColor("#ff22aa00"));
            if (selectedIcon != null) {
                holder.selectedIcon.setImageDrawable(selectedIcon);
            }
        } else if (iconPack != null && !iconPack.isDefault()
                    && FragmentIcon.iconPack.getUnthemedIcons().contains(cmpName.flattenToString())) {
            holder.textView.setTextColor(Color.RED);
        }  else {
            holder.textView.setTextColor(CommonUI.TextColor);
        }
    }

    public static class ViewHolder {
        ImageView imageView;
        TextView textView;
        CheckBox checkBox;
        ImageButton delete;
        ImageView selectedIcon;
        String cmpName;

        public void loadImageAsync(PackageManager pm, ResolveInfo item, ViewHolder holder, IconPack iconPack) {
            new ImageLoader(pm, item, holder, iconPack).execute();
        }
    }
}