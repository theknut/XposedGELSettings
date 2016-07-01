package de.theknut.xposedgelsettings.broadcastreceiver;

import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.icon.IconPack;
import de.theknut.xposedgelsettings.ui.Blur;
import de.theknut.xposedgelsettings.ui.CommonUI;
import de.theknut.xposedgelsettings.ui.FragmentIcon;

@SuppressLint("WorldReadableFiles")
public class XGELSReceiver extends BroadcastReceiver {

    @SuppressWarnings("deprecation")
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_DATE_CHANGED)) {
           context.sendBroadcast(new Intent(Common.XGELS_ACTION_RESTART_LAUNCHER));
           return;
        }

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
        } else if (intent.getAction().equals(Common.XGELS_ACTION_SAVE_SETTING)) {

            SharedPreferences.Editor editor = prefs.edit();
            String type = intent.getStringExtra("type");
            String key = intent.getStringExtra("key");
            editor.remove(key).commit();

            if (type.equals("boolean")) {
                editor.putBoolean(key, intent.getBooleanExtra(key, false)).commit();
            } else if (type.equals("arraylist")) {
                ArrayList<String> d = intent.getStringArrayListExtra(key);
                editor.putStringSet(key, new HashSet(intent.getStringArrayListExtra(key))).commit();
            }

            context.sendBroadcast(new Intent(Common.XGELS_ACTION_RELOAD_SETTINGS));

            if (intent.getBooleanExtra("restart", false)) {
                CommonUI.CONTEXT = context;
                CommonUI.restartLauncher(false);
            }
        } else if (intent.getAction().equals(Common.XGELS_ACTION_CONVERT_SETTING)) {
            String key = intent.getStringExtra("key");
            Set<String> hiddenWidgets = prefs.getStringSet(key, new HashSet<String>());
            Set<String> tmp = new HashSet<String>();

            Iterator<String> it = hiddenWidgets.iterator();
            while (it.hasNext()) {
                String[] item = it.next().split("#");
                if (item.length > 1) {
                    tmp.add(item[0] + "/" + item[0] + item[1]);
                }
            }

            if (tmp.size() == hiddenWidgets.size()) {
                prefs.edit().remove(key).apply();
                prefs.edit().putStringSet(key, tmp).apply();
                context.sendBroadcast(new Intent(Common.XGELS_ACTION_RELOAD_SETTINGS));
                Toast.makeText(context, "Settings successfully converted! Please restart your launcher!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "Settings couldn't be converted successfully. Please reassign the setting (" + key + ") manually!", Toast.LENGTH_LONG).show();
            }
        } else if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            try {
                FragmentIcon.iconPack = new IconPack(
                        CommonUI.CONTEXT,
                        CommonUI.CONTEXT.getSharedPreferences(
                                Common.PREFERENCES_NAME,
                                Context.MODE_WORLD_READABLE
                        ).getString("iconpack", Common.ICONPACK_DEFAULT));
                FragmentIcon.iconPack.loadAppFilter();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
