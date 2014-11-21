package de.theknut.xposedgelsettings.ui.preferences;

import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.view.View;

import de.theknut.xposedgelsettings.ui.CommonUI;

public class MyListPreference extends ListPreference {
	public MyListPreference(Context context) {
        super(context);
    }

    public MyListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        
        CommonUI.setCustomStyle(view, true, true);
    }
}