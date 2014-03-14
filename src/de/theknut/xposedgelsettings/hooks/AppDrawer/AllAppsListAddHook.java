package de.theknut.xposedgelsettings.hooks.AppDrawer;

import static de.robv.android.xposed.XposedHelpers.getObjectField;

import android.content.ComponentName;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.theknut.xposedgelsettings.AllAppsListToRename;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;

public final class AllAppsListAddHook extends XC_MethodHook {
	
	// http://androidxref.com/4.4.2_r1/xref/packages/apps/Launcher3/src/com/android/launcher3/AllAppsList.java#65
	// public void add(AppInfo info)
	
	@Override
	protected void beforeHookedMethod(MethodHookParam param) throws Throwable {		
		String title = (String) getObjectField(param.args[0], "title");
		ComponentName componentName = (ComponentName) getObjectField(param.args[0], "componentName");
        
		if (PreferencesHelper.hiddenApps.contains(componentName.getPackageName() + "#" + title)) {
			// don't add it to the allAppsList if it is in our list
			param.setResult(null);
		}
        
        for (String item : PreferencesHelper.renamedApps){
            if (item.split(AllAppsListToRename.separator)[0].equals(componentName.getPackageName())){
                XposedHelpers.setObjectField(param.args[0], "title", item.split(AllAppsListToRename.separator, 2)[1]);
            }
        }
        

        if (PreferencesHelper.modifiedIconApps.contains(componentName.getPackageName())){
          Bitmap bm = drawableToBitmap(Resources.getSystem().getDrawable(android.R.drawable.sym_def_app_icon));
//          Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_launcher);
          XposedHelpers.setObjectField(param.args[0], "iconBitmap", bm);
        }
	}
	
	public static Bitmap drawableToBitmap (Drawable drawable) {
	    if (drawable instanceof BitmapDrawable) {
	        return ((BitmapDrawable)drawable).getBitmap();
	    }

	    Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Config.ARGB_8888);
	    Canvas canvas = new Canvas(bitmap); 
	    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
	    drawable.draw(canvas);

	    return bitmap;
	}
}