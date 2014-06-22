package de.theknut.xposedgelsettings.ui;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;

public class MyPreferenceScreen extends Preference implements View.OnLongClickListener {
	
	public MyPreferenceScreen(Context context) {
        super(context);
    }

    public MyPreferenceScreen(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyPreferenceScreen(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);

        CommonUI.setCustomStyle(view, true, true);
    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }
}
