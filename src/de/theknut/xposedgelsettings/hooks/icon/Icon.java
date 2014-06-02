package de.theknut.xposedgelsettings.hooks.icon;

import android.graphics.drawable.Drawable;

public class Icon {
    
    private String componentName;
    private Drawable icon;
    private boolean wasPreThemed;
    
    public Icon(String componentName, Drawable icon) {       
        this.componentName = componentName;
        this.icon = icon;
    }
    
    public Icon(String componentName, Drawable icon, boolean wasPreThemed) {
        this.wasPreThemed = wasPreThemed;        
        this.componentName = componentName;
        this.icon = icon;
    }
    
    public String getComponentName() {
        return componentName;
    }
    
    public Drawable getIcon() {
        return icon;
    }
    
    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
    
    public boolean isLoaded() {
        return icon != null;
    }
    
    public boolean wasPreThemed() {
        return wasPreThemed;
    }
    
    @Override
    public boolean equals(Object o) {
        return ((String) o).equals(getComponentName())
               || (wasPreThemed() && ((String) o).contains(getComponentName()));
    }
    
    public boolean isThemed(String pkg) {
        return getComponentName().contains("calendar:") && pkg.contains(getComponentName().replace("calendar:", ""));
    }
}