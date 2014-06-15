package de.theknut.xposedgelsettings.ui;

import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

/**
 * Created by Alexander Schulz on 15.06.2014.
 */
public class ImageLoader extends AsyncTask<Void, Void, Void> {

    PackageManager pm;
    ResolveInfo item;
    ImageView image;
    Drawable icon;

    public ImageLoader(PackageManager pm, ResolveInfo item, ImageView imageView) {
        this.pm = pm;
        this.item = item;
        this.image = imageView;
    }

    @Override
    protected Void doInBackground(Void... params) {

        this.icon = item.loadIcon(pm);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        this.image.setImageDrawable(icon);
    }
}