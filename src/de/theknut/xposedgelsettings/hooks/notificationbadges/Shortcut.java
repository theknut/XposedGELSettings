package de.theknut.xposedgelsettings.hooks.notificationbadges;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.View;
import android.widget.TextView;

import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Methods;

import static de.robv.android.xposed.XposedHelpers.callMethod;

public class Shortcut extends Icon {
	
	String pgkName;
	Object shortcut;
	Drawable origIcon;
	int currCnt;
	
	private final int ICON = 0;
	private final int BADGE = 1;
	
	public Shortcut(Object shortcut) throws Exception {
		
		long time = System.currentTimeMillis();
		
		this.shortcut = shortcut;
		this.childID = ((View) shortcut).getId();
		
		// the tag is either AppInfo (app drawer) or ShortcutInfo (desktop)
		// and contains the launch intent
		Object tag = ((View) shortcut).getTag();
		
		try {			
			Intent i = (Intent) callMethod(tag, Methods.siGetIntent);
			this.pgkName = i.getComponent().getPackageName();
		} catch (Exception ex) {
			log("Shortcut.Cstr: There was a problem getting the intent for the shortcut - " + tag);
			log("Exception: " + ex);
			
			throw new Exception("XGELS: Don't create Shortcut internally - aborting Constructor");
		}
		
		Object drawable = ((TextView) this.shortcut).getCompoundDrawables()[1];
		if (!(drawable instanceof LayerDrawable)) {
			origIcon = layers[ICON] = (Drawable) drawable;
		}
		
		if (DEBUG) log("Shortcut.Cstr: Created " + pgkName + " (ID" + childID + ") in " + (System.currentTimeMillis() - time) + "ms");
	}
	
	public void setBadge(int cnt) {
		
		long time = System.currentTimeMillis();
		currCnt = cnt;
		
		if (currCnt != 0) {
			
			Object drawable = ((TextView) this.shortcut).getCompoundDrawables()[1];
			
			if (drawable instanceof LayerDrawable) {
				layers[ICON] = ((LayerDrawable) drawable).getDrawable(ICON);
			} else {
				layers[ICON] = (Drawable) drawable;
			}
			
			layers[BADGE] = textToDrawable(currCnt, layers[ICON]);			
			LayerDrawable ld = new LayerDrawable(layers);			
			ld.setLayerInset(BADGE, 0, 0, layers[ICON].getIntrinsicWidth() - layers[BADGE].getIntrinsicWidth(), layers[ICON].getIntrinsicHeight() - layers[BADGE].getIntrinsicHeight());
			
			((TextView) shortcut).setCompoundDrawablesWithIntrinsicBounds(null, ld, null, null);
			
			callMethod(this.shortcut, "invalidate");
		} else {
			resetBadge();
		}
		
		if (DEBUG) log("Shortcut.SetBadge: " + pgkName + " (ID" + childID + ") - setting badgenr " + currCnt + " took " + (System.currentTimeMillis() - time) + "ms");
	}
	
	public void setBadgeVisible(boolean visible) {
		
		Object drawable = ((TextView) this.shortcut).getCompoundDrawables()[1];
		
		if (drawable instanceof LayerDrawable) {
			if (DEBUG) log(pgkName + " (ID" + childID + ") - set visible " + visible);
			
			Drawable d = ((LayerDrawable) drawable).getDrawable(BADGE);
			d.setVisible(visible, false);
			layers[BADGE] = d;
			
			((TextView) shortcut).setCompoundDrawablesWithIntrinsicBounds(null, new LayerDrawable(layers), null, null);
		}		
	}
	
	public void resetBadge() {
		
		Object drawable = ((TextView) this.shortcut).getCompoundDrawables()[1];
		
		if (drawable instanceof LayerDrawable) {
			((TextView) shortcut).setCompoundDrawablesWithIntrinsicBounds(null, layers[ICON], null, null);
		}		
	}
}