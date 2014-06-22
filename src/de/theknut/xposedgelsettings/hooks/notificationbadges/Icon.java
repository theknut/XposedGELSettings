package de.theknut.xposedgelsettings.hooks.notificationbadges;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.HooksBaseClass;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;

public class Icon extends HooksBaseClass{
	
	int childID;
	public Drawable[] layers = new Drawable[2];	
	private TextView badgeCount;
	
	public Drawable textToDrawable(int currCnt, Drawable icon) {
    	
		setupTextView(currCnt);
		
		Bitmap badgeBitmap = Bitmap.createBitmap(badgeCount.getMeasuredWidth(), badgeCount.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(badgeBitmap);
		
		badgeCount.layout(0, 0, badgeCount.getMeasuredWidth(), badgeCount.getMeasuredHeight());
		badgeCount.draw(c);
		
		return new BitmapDrawable(Common.LAUNCHER_CONTEXT.getResources(), badgeBitmap);
    }
	
	private void initBadge() {
		
		NotificationBadgesHelper.initMeasures();
		
		badgeCount = new TextView(Common.LAUNCHER_CONTEXT);
		badgeCount.setTextColor(Color.parseColor(ColorPickerPreference.convertToARGB(PreferencesHelper.notificationBadgeTextColor)));
		badgeCount.setTextSize(TypedValue.COMPLEX_UNIT_DIP, PreferencesHelper.notificationBadgeTextSize);
		badgeCount.setGravity(Gravity.CENTER);
		badgeCount.setIncludeFontPadding(false);
		badgeCount.setPadding(
			NotificationBadgesHelper.leftRightPadding,
			NotificationBadgesHelper.topBottomPadding,
			NotificationBadgesHelper.leftRightPadding,
			NotificationBadgesHelper.topBottomPadding
		);		
		
		GradientDrawable gdDefault = new GradientDrawable();		
		gdDefault.setColor(Color.parseColor(ColorPickerPreference.convertToARGB(PreferencesHelper.notificationBadgeBackgroundColor)));
		gdDefault.setCornerRadius(PreferencesHelper.notificationBadgeCornerRadius);
		
		if (NotificationBadgesHelper.frameSize != 0) {
			gdDefault.setStroke(NotificationBadgesHelper.frameSize, Color.parseColor(ColorPickerPreference.convertToARGB(PreferencesHelper.notificationBadgeFrameColor)));
		}
		
		gdDefault.setShape(GradientDrawable.RECTANGLE);
		
		badgeCount.setBackground(gdDefault);
		badgeCount.setDrawingCacheEnabled(true);
	}
	
	protected TextView setupTextView(int currCnt) {
		
		initBadge();
		
		badgeCount.setText("" + currCnt);
		badgeCount.measure(NotificationBadgesHelper.measuredWidth, NotificationBadgesHelper.measuredHeigth);
		
		return badgeCount;
	}
}
