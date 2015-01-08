package de.theknut.xposedgelsettings.hooks.notificationbadges;

import android.content.ComponentName;
import android.content.pm.ResolveInfo;

/**
 * Created by Alexander Schulz on 06.09.2014.
 */
public class PendingNotification {
    private ComponentName componentName;
    private int count;

    public PendingNotification(ResolveInfo info, int count) {
        this.componentName = new ComponentName(info.activityInfo.packageName, info.activityInfo.name);
        this.count = count;
    }

    public PendingNotification(ComponentName componentname, int count) {
        this.componentName = componentname;
        this.count = count;
    }

    public int getCount() {
        return this.count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof PendingNotification) {
            return ((PendingNotification) o).componentName.equals(this.componentName);
        }
        return this.componentName.equals(o);
    }

    @Override
    public String toString() {
        return componentName + " Count: " + count;
    }
}