package de.theknut.xposedgelsettings.hooks;

import android.content.Context;
import de.robv.android.xposed.XC_MethodHook;

public final class DeviceProfileHook extends XC_MethodHook {
	
	@Override
	protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
		if (!(param.args[0] instanceof Context)) {
			
			if (PreferencesHelper.changeGridSize) {
				param.args[3] = PreferencesHelper.yCountHomescreen;
				param.args[4] = PreferencesHelper.xCountHomescreen;
			}
			
			if (PreferencesHelper.changeHotseatCount) {
				// number of hotseat icons also includes the app drawer so there has to be an odd number
				param.args[7] = PreferencesHelper.hotseatCount + 1;
			}
		}
	}
}