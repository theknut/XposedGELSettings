package de.theknut.xposedgelsettings.broadcastreceiver;

import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashSet;

import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.ui.Blur;
import de.theknut.xposedgelsettings.ui.CommonUI;

@SuppressLint("WorldReadableFiles")
public class XGELSReceiver extends BroadcastReceiver {

    @SuppressWarnings("deprecation")
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = context.getSharedPreferences(Common.PREFERENCES_NAME, Context.MODE_WORLD_READABLE);

        if (intent.getAction().equals(Intent.ACTION_WALLPAPER_CHANGED)) {
            boolean autoBlurImage = prefs.getBoolean("autoblurimage", false);

            if (autoBlurImage) {
                new BlurWallpaperAsyncTask().execute(context);
            }

            return;
        }

        if (intent.getAction().equals(Common.XGELS_ACTION_SAVE_ICONPACK)) {
            String pkg = intent.getStringExtra("PACKAGENAME");
            if (pkg == null) return;

            prefs.edit().remove("iconpack").commit();
            prefs.edit().putString("iconpack", pkg).commit();
        } else if (intent.getAction().equals(Common.XGELS_ACTION_SAVE_STRING_ARRAY)) {

            String key = intent.getStringExtra("key");
            ArrayList<String> stringArrayListExtra = intent.getStringArrayListExtra(key);
            SharedPreferences.Editor editor = prefs.edit();
            editor.remove(key).commit();
            editor.putStringSet(key, new HashSet(stringArrayListExtra)).commit();

        } else if (intent.getAction().equals(Common.XGELS_ACTION_SAVE_LAYER_POSITIONS)) {
            SharedPreferences.Editor editor = prefs.edit();

            String key = "layerpositions";
            HashSet<String> layerpositions = (HashSet<String>) prefs.getStringSet(key, new HashSet<String>());
            HashSet<String> newlayerpositions = new HashSet<String>();

            Bundle bundles  = intent.getBundleExtra(key);
            if (bundles != null) {
                for (int j = 0; j < bundles.size(); j++) {
                    Bundle bundle = bundles.getBundle("" + j);
                    if (bundle != null) {
                        newlayerpositions.add(bundle.getLong("id") + "|" + bundle.getBoolean("front"));
                    }
                }
            }
        }

        context.sendBroadcast(new Intent("de.theknut.xposedgelsettings.Intent.RELOAD_SETTINGS"));
    }

    @SuppressLint("SdCardPath")
    private class BlurWallpaperAsyncTask extends AsyncTask<Context, Void, Void> {

        @Override
        protected Void doInBackground(final Context... params) {

            String pathBackground = "/mnt/sdcard/XposedGELSettings/bluredbackground.png";
            File fileBackground = new File(pathBackground);

            final WallpaperManager wallpaperManager = WallpaperManager.getInstance(params[0]);
            Bitmap wallBitmap = Blur.drawableToBitmap(wallpaperManager.getFastDrawable());
            CommonUI.bluredBackground = Blur.tryBlur(wallBitmap, 50);

            if (CommonUI.bluredBackground == null) {
                return null;
            }

            FileOutputStream out = null;

            try {
                // save background
                out = new FileOutputStream(fileBackground);
                CommonUI.bluredBackground.compress(Bitmap.CompressFormat.PNG, 90, out);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try{
                    out.close();
                } catch(Throwable ignore) {}
            }

            return null;
        }
    }
}