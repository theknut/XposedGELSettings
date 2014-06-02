package de.theknut.xposedgelsettings.hooks.icon;

public class IconInfo {
    
    private String componentName;
    private String drawableName;
    private String prefix;
    
    public IconInfo(String componentName, String drawableName) {
        this.componentName = componentName;
        this.drawableName = drawableName;
        this.prefix = null;
    }
    
    public IconInfo(String componentName, String drawableName, String prefix) {
        this.componentName = componentName;
        this.drawableName = drawableName;
        this.prefix = prefix;
    }
    
    public String getComponentName() {
        return componentName;
    }
    
    public String getDrawableName() {
        return hasPrefix() ? getDynamicIcon() : this.drawableName;  
    }
    
    public String getDynamicIcon() {
        return getPrefix() + IconPack.getDayOfMonth();
    }
    
    public String getPrefix() {
        return prefix;
    }
    
    public boolean hasPrefix() {
        return prefix != null;
    }
}