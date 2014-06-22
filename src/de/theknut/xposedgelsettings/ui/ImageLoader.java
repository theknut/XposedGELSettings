package de.theknut.xposedgelsettings.ui;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.View;

import de.theknut.xposedgelsettings.hooks.icon.IconPack;

/**
 * Created by Alexander Schulz on 15.06.2014.
 */
public class ImageLoader extends AsyncTask<Void, Void, Void> {

    PackageManager pm;
    ResolveInfo item;
    ChooseAppList.ViewHolder holder;
    IconPack iconPack;
    Drawable icon;
    ComponentName cmpName;
    boolean iconNotFound;

    public ImageLoader(PackageManager pm, ResolveInfo item, ChooseAppList.ViewHolder holder, IconPack iconPack) {
        this.pm = pm;
        this.item = item;
        this.holder = holder;
        this.iconPack = iconPack;
    }

    @Override
    protected Void doInBackground(Void... params) {

        if (iconPack == null) {
            this.icon = item.loadIcon(pm);
        } else {
            cmpName = new ComponentName(item.activityInfo.packageName, item.activityInfo.name);
            this.icon = iconPack.loadIcon(cmpName.flattenToString());

            if (this.icon == null) {
                iconNotFound = true;
                this.icon = item.loadIcon(pm);
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {

        this.holder.imageView.setImageDrawable(icon);
        if (holder.delete == null || holder.checkBox.isShown()) return;

        if (holder.delete.getVisibility() == View.VISIBLE) {
            holder.textView.setTextColor(Color.parseColor("#ff22aa00"));
        } else if (!iconPack.isDefault()
                    && FragmentIcon.iconPack.getUnthemedIcons().contains(cmpName.flattenToString())) {
            holder.textView.setTextColor(Color.RED);
        }  else {
            holder.textView.setTextColor(CommonUI.TextColor);
        }
    }
}