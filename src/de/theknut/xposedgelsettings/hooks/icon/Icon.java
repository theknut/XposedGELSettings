package de.theknut.xposedgelsettings.hooks.icon;

import android.graphics.drawable.Drawable;

public class Icon {
    
    private String componentName;
    private String packageName;
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

    public String getPackageName() {
        if (packageName == null) {
            if (componentName.contains("/")) {
                this.packageName = componentName.substring(0, componentName.indexOf("/"));
            } else {
                this.packageName = componentName;
            }
        }
        return this.packageName;
    }

    public boolean isLoad() {
        return getIcon() != null;
    }

    public Drawable getIcon() {
        return icon;
    }
    
    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
    
    public boolean wasPreThemed() {
        return wasPreThemed;
    }
    
    @Override
    public boolean equals(Object o) {
        return o.equals(getComponentName())
               || (wasPreThemed() && ((String) o).contains(getComponentName()));
    }
}