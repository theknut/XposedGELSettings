package de.theknut.xposedgelsettings.hooks.appdrawer;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Handler;
import de.robv.android.xposed.XC_MethodHook;
import de.theknut.xposedgelsettings.R;
import de.theknut.xposedgelsettings.ui.FragmentAppDrawer;
import de.theknut.xposedgelsettings.ui.FragmentDonate;
import de.theknut.xposedgelsettings.ui.FragmentGeneral;
import de.theknut.xposedgelsettings.ui.FragmentGestures;
import de.theknut.xposedgelsettings.ui.FragmentHomescreen;
import de.theknut.xposedgelsettings.ui.FragmentImExport;
import de.theknut.xposedgelsettings.ui.FragmentSearchbar;
import de.theknut.xposedgelsettings.ui.FragmentSystemUI;
import de.theknut.xposedgelsettings.ui.FragmentWelcome;

public class OnClickHook  extends XC_MethodHook {
	
	// http://androidxref.com/4.4.2_r1/xref/packages/apps/Launcher3/src/com/android/launcher3/AppsCustomizePagedView.java#492
	// void onClick(View v)
	
	@Override
	protected void afterHookedMethod(final MethodHookParam param) throws Throwable
	{
		callMethod(getObjectField(param.thisObject, "mLauncher"), "showWorkspace", false);
	}
}