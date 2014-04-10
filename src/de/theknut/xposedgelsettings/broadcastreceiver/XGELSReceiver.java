package de.theknut.xposedgelsettings.broadcastreceiver;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;

import de.theknut.xposedgelsettings.R;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.ui.Blur;
import de.theknut.xposedgelsettings.ui.CommonUI;
import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

@SuppressLint("WorldReadableFiles")
public class XGELSReceiver extends BroadcastReceiver {
	
	public static Object sbservice = null;
	public static Class<?> statusbarManager = null;
	public static Method showNotificationPanel = null;
	public static Method showSettingsPanel = null;
	public static String NOTIFICATION_PANEL = "expandNotificationsPanel";
	public static String SETTINGS_PANEL = "expandSettingsPanel";

    @SuppressWarnings("deprecation")
	@Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Common.XGELS_INTENT)) {
        	
        	String extra = intent.getStringExtra(Common.XGELS_ACTION);
        	
        	if (extra != null) {
	        	if (extra.equals("NOTIFICATION_BAR")) {
	        		showNotificationPanel(context);
				} else if (extra.equals("SETTINGS_BAR")) {
	        		showSettingsPanel(context);
	        	}
        	}
        } else if (intent.getAction().equals(Intent.ACTION_WALLPAPER_CHANGED)) {
        	boolean autoBlurImage = context.getSharedPreferences(Common.PREFERENCES_NAME, Context.MODE_WORLD_READABLE).getBoolean("autoblurimage", false);
        	
        	if (autoBlurImage) {
        		new BlurWallpaperAsyncTask().execute(context);
        	}
        }
    }
	
	public static boolean init(Context context) {
    	
    	if (sbservice != null && statusbarManager != null && showNotificationPanel != null && showSettingsPanel != null) {
    		return true;
    	}
    	
    	try {
    		sbservice = context.getSystemService("statusbar");				
    		statusbarManager = Class.forName("android.app.StatusBarManager");
    		showNotificationPanel = statusbarManager.getMethod(NOTIFICATION_PANEL);
    		showSettingsPanel = statusbarManager.getMethod(SETTINGS_PANEL);
    		
    		if (sbservice != null && statusbarManager != null && showNotificationPanel != null && showSettingsPanel != null) {
    			Common.IS_INIT = true;
    			return true;
    		}
    		
    		return false;
    	} catch (Exception e) {
			Toast.makeText(context, R.string.toast_fail, Toast.LENGTH_LONG).show();
			e.printStackTrace();
			
			return false;
		}    	
    }
    
    public void showNotificationPanel(Context context) {    	
		try {
			if (init(context)) {
				showNotificationPanel.invoke(sbservice);
			}
		} catch (Exception e) {
			Toast.makeText(context, R.string.toast_fail, Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
    }
    
    public void showSettingsPanel(Context context) {    	
		try {
			if (init(context)) {
				showSettingsPanel.invoke(sbservice);
			}
		} catch (Exception e) {
			Toast.makeText(context, R.string.toast_fail, Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
    }
    
    private class BlurWallpaperAsyncTask extends AsyncTask<Context, Void, Void> {

		@Override
		protected Void doInBackground(final Context... params) {
			
			String pathBackground = Environment.getExternalStorageDirectory().getPath() + "/XposedGELSettings/bluredbackground.png";
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