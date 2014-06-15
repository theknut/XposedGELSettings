package de.theknut.xposedgelsettings.hooks.notificationbadges;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Fields;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Methods;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

public class FolderIcon extends Icon {
	
	Object folder;
	View folderObject;
	ImageView previewBackground;
	List<Shortcut> children;
	String folderName;
	int totalcnt;
	
	public int height, width;
	
	private final int ICON = 0;
	private final int BADGE = 1;
	
	public FolderIcon(Object folder) {
		
		long time = System.currentTimeMillis();
		
		this.folder = folder;
		this.previewBackground = (ImageView) getObjectField(folder, Fields.fiPreviewBackground);
		this.folderName = (String) callMethod(getObjectField(folder, Fields.fiFolderName), "getText");
		this.childID = ((View) folder).getId();
		this.children = new ArrayList<Shortcut>();
		
		Object mContent = getObjectField(getObjectField(folder, Fields.fiFolder), Fields.fContent);		
		ViewGroup mShortcutsAndWidgets = (ViewGroup) callMethod(mContent, Methods.clGetShortcutsAndWidgets);
		
		for (int i = 0; i < mShortcutsAndWidgets.getChildCount(); i++) {
			
			try {
				Shortcut shortcut = new Shortcut(mShortcutsAndWidgets.getChildAt(i));
				this.children.add(shortcut);
			} catch (Exception ex) {
				// don't add if Shortcut constructor fails
			}
		}
		
		for (Iterator<Shortcut> it = NotificationBadgesHelper.shortcutsDesktop.iterator(); it.hasNext();) {
			
			Shortcut shortcut = it.next();
			for (Shortcut folderChildren : this.children) {
								
				if (shortcut.childID == folderChildren.childID) {
					if (DEBUG) log("FolderIcon.Cstr: Found " + shortcut.pgkName + " (ID" + shortcut.childID + ") in folder " + this.folderName + " ID(" + this.childID + ") and removed from it shortcutDesktop list");
					it.remove();
				}
			}
		}
		
		if (DEBUG) log("Created: " + folderName + " (ID" + childID + ") in " + (System.currentTimeMillis() - time) + "ms");
	}
	
	public void setBadge(int cnt) {
		
		long time = System.currentTimeMillis();
		totalcnt = cnt;
		
		if (totalcnt == 0) {
			resetBadges(false);
		}
        callMethod(this.folder, "invalidate");
		if (DEBUG) log(folderName + " - setting badgenr " + totalcnt + " took " + (System.currentTimeMillis() - time) + "ms");
	}
	
	public void setCanvas(Canvas c) {
		
		if (totalcnt != 0) {
			c.save();


			Drawable d = textToDrawable(totalcnt, previewBackground.getDrawable());
			c.translate(
                    d.getIntrinsicWidth() + (d.getIntrinsicWidth() - Math.round(d.getIntrinsicWidth() * ((float) PreferencesHelper.iconSize / 100))),
                    d.getIntrinsicHeight() + (d.getIntrinsicHeight() - Math.round(d.getIntrinsicHeight() * ((float) PreferencesHelper.iconSize / 100)))
            );
			d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
			d.draw(c);
			
			c.restore();
		}
	}
	
	public void resetBadges(boolean resetChildren) {
		
		Object drawable = previewBackground.getDrawable();
		
		if (drawable instanceof LayerDrawable) {
			previewBackground.setImageDrawable(layers[ICON]);
		}
		
		if (resetChildren) {
			for (Shortcut shortcut : this.children) {
				shortcut.resetBadge();
			}
		}
	}
	
	public void setBadgeVisible(boolean visible) {
		
		Object drawable = ((ImageView) this.folder).getDrawable();
		
		if (drawable instanceof LayerDrawable) {
			if (DEBUG) log(folderName + " (ID" + childID + ") - set visible " + visible);
			
			Drawable d = ((LayerDrawable) drawable).getDrawable(BADGE);
			d.setVisible(visible, false);
			layers[BADGE] = d;
			
			previewBackground.setImageDrawable(new LayerDrawable(layers));
		}
	}
	
	public void setChildrenBadgesVisible(boolean visible) {
		
		for (Shortcut shortcut : this.children) {
			shortcut.setBadgeVisible(visible);
		}
	}
	
	public void addChild(Shortcut child) {
		
		removeChild(child);
		
		if (DEBUG) log("Folder.addChild - Remove " + child.pgkName + " (ID" + child.childID + ")");
		children.add(child);
	}
	
	public void removeChild(Shortcut child) {
		
		for (Iterator<Shortcut> it = children.iterator(); it.hasNext();) {
			
			Shortcut shortcut = it.next();
			if (shortcut.childID == child.childID) {
				if (DEBUG) log("Folder.removeChild - Remove " + shortcut.pgkName + " (ID" + shortcut.childID + ")");
				shortcut.resetBadge();
				it.remove();
			}
		}
	}
}