package de.theknut.xposedgelsettings.hooks.icon;

import android.graphics.drawable.Drawable;

public class IconPreview {

    private int resID;
    private String drawableName;
    private Drawable icon;

    public IconPreview(int resID, String drawableName) {
        this.resID = resID;
        this.drawableName = drawableName;
    }

    public int getResID() {
        return resID;
    }

    public String getDrawableName() {
        return drawableName;
    }

    public Drawable getIcon() {
        return icon;
    }
    
    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
}