package de.theknut.xposedgelsettings.hooks.googlesearchbar.weatherwidget;

import android.content.res.Configuration;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.theknut.xposedgelsettings.R;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.HooksBaseClass;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Classes;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Fields;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Methods;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;
import de.theknut.xposedgelsettings.hooks.Utils;
import de.theknut.xposedgelsettings.hooks.common.CommonHooks;
import de.theknut.xposedgelsettings.hooks.common.XGELSCallback;
import de.theknut.xposedgelsettings.hooks.googlesearchbar.GetWorkspacePaddingHook;

import static de.robv.android.xposed.XposedBridge.hookAllConstructors;
import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getFloatField;
import static de.robv.android.xposed.XposedHelpers.getIntField;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

/**
 * Created by Alexander Schulz on 17.10.2014.
 */
public class WeatherWidget extends HooksBaseClass {

    static Object weatherEntry;
    private static LinearLayout widget;
    private static LinearLayout widgetContentHolder;
    static View ospc;

    static WidgetSettings widgetSettings = new WidgetSettings();

    public static void initAllHooks(final LoadPackageParam lpparam) {

        if (false && !PreferencesHelper.hideSearchBar && Common.PACKAGE_OBFUSCATED) {

            if (PreferencesHelper.searchBarWeatherWidget) {

                hookAllConstructors(Classes.WeatherPoint, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        weatherEntry = param.thisObject;
                    }
                });

                findAndHookMethod(Classes.WeatherEntryAdapter, Methods.weaUpdateWeather, new XC_MethodHook() {

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        maybeAddWidget();

                        final Animation fadeOut = AnimationUtils.loadAnimation(Common.XGELS_CONTEXT, R.anim.weather_fade_out);
                        fadeOut.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                widget.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                widget.setVisibility(View.VISIBLE);
                                updateWeatherContent();
                                widget.startAnimation(AnimationUtils.loadAnimation(Common.XGELS_CONTEXT, R.anim.weather_fade_in));
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) { }
                        });

                        if (((Boolean) callMethod(Common.LAUNCHER_INSTANCE, Methods.lIsAllAppsVisible) // workspace state is not normal
                                || !getObjectField(Common.WORKSPACE_INSTANCE, Fields.wState).toString().equals("NORMAL"))
                                || ((Boolean) callMethod(Common.LAUNCHER_INSTANCE, Methods.lHasCustomContentToLeft) // is on G Now page
                                    && getIntField(Common.WORKSPACE_INSTANCE, Fields.pvCurrentPage) == 0)
                                || Common.LAUNCHER_CONTEXT.getResources().getConfiguration().orientation != Configuration.ORIENTATION_PORTRAIT) { // is not in portrait mode
                            widget.setVisibility(View.GONE);
                            updateWeatherContent();
                        } else {
                            widget.startAnimation(fadeOut);
                        }
                    }
                });

                CommonHooks.GetWorkspacePaddingListeners.add(new GetWorkspacePaddingHook());

                CommonHooks.OnDragStartListeners.add(new XGELSCallback() {
                    @Override
                    public void onBeforeHookedMethod(MethodHookParam param) throws Throwable {
                        fadeOut(175);
                    }
                });
                CommonHooks.OnDragEndListeners.add(new XGELSCallback() {
                    @Override
                    public void onBeforeHookedMethod(MethodHookParam param) throws Throwable {
                        fadeIn(0);
                    }
                });

                XposedBridge.hookAllConstructors(findClass("com.google.android.search.shared.ui.ReverseDrawRestrictedLayout", lpparam.classLoader), new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        ospc = (View) param.thisObject;
                    }
                });

                findAndHookMethod(Classes.Workspace, Methods.wUpdateStateForCustomContent, Integer.TYPE, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        if (widgetContentHolder == null ||
                                Common.LAUNCHER_CONTEXT.getResources().getConfiguration().orientation != Configuration.ORIENTATION_PORTRAIT) {
                            return;
                        }

                        widget.setVisibility(View.VISIBLE);
                        widget.setAlpha(1 - getFloatField(param.thisObject, Fields.wLastCustomContentScrollProgress));
                        widget.setTranslationY(ospc.getTranslationY());
                    }
                });

                findAndHookMethod(Classes.SearchPlate, Methods.spOnModeChanged, Integer.TYPE, Integer.TYPE, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        if ((Integer) param.args[0] > 0) {
                            fadeOut(0);
                        } else {
                            if ((Integer) param.args[1] != 6 && (Integer) param.args[1] != 2) {
                                fadeIn(500);
                            }
                        }
                    }
                });

                CommonHooks.EnterOverviewModeListeners.add(new XGELSCallback() {
                    @Override
                    public void onBeforeHookedMethod(MethodHookParam param) throws Throwable {
                        fadeOut(0);
                    }

                    @Override
                    public void onAfterHookedMethod(MethodHookParam param) throws Throwable {
                        fadeOut(0);
                    }
                });

                findAndHookMethod(Classes.Launcher, Methods.lShowWorkspace, boolean.class, Runnable.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        fadeIn(1000);
                    }
                });
            }
        }
    }

    public static void fadeOut(int duration) {
        ViewGroup parent = (ViewGroup) ((ViewGroup) getObjectField(Common.LAUNCHER_INSTANCE, Fields.lSearchDropTargetBar)).getParent();
        if (parent.getTag() != null) {
            ViewGroup widget = (ViewGroup) parent.getTag();
            widget.animate().alpha(0.0f).setDuration(duration).start();
        }
    }

    public static void fadeIn(int startDelay) {
        ViewGroup parent = (ViewGroup) ((ViewGroup) getObjectField(Common.LAUNCHER_INSTANCE, Fields.lSearchDropTargetBar)).getParent();
        if (parent.getTag() != null) {
            ViewGroup widget = (ViewGroup) parent.getTag();
            widget.animate().alpha(1.0f).setDuration(200).setStartDelay(startDelay).start();
        }
    }

    private static void updateWeatherContent() {
        for (int i = 0; i < widgetContentHolder.getChildCount(); i++) {
            View item = widgetContentHolder.getChildAt(i);
            if (isCity(item)) {
                ((TextView) item).setText(getCity());
            } else if (isTemperature(item)) {
                ((TextView) item).setText(getTemperatur());
            } else if (isWeatherDescription(item)) {
                ((TextView) item).setText(getWeatherDescription());
            }
        }
    }

    private static String getWeatherDescription() {
        return (String) callMethod(weatherEntry, Methods.wpGetWeatherDescription);
    }

    private static String getTemperatur() {
        return callMethod(weatherEntry, Methods.wpGetTemperatur) + widgetSettings.unit;
    }

    private static String getCity() {
        return (String) callMethod(weatherEntry, "getLocation");
    }

    public static void maybeAddWidget() {
        ViewGroup searchDropTargetBar = ((ViewGroup) getObjectField(Common.LAUNCHER_INSTANCE, Fields.lSearchDropTargetBar));
        ViewGroup parent = (ViewGroup) searchDropTargetBar.getParent();
        if (parent.getTag() != null) {
            widget = (LinearLayout) parent.getTag();
        } else {
            addWeatherWidget(searchDropTargetBar);
        }
    }

    private static void addWeatherWidget(ViewGroup searchBar) {
        ViewGroup parent = ((ViewGroup) searchBar.getParent());
        widget = (LinearLayout) LayoutInflater.from(Common.XGELS_CONTEXT).inflate(R.layout.qsb_weather_widget, null, true);
        widget.setPadding(searchBar.getPaddingLeft(), searchBar.getHeight() - Utils.dpToPx(8), searchBar.getPaddingRight(), parent.getPaddingBottom());

        widgetContentHolder = (LinearLayout) widget.findViewById(R.id.weathercontentview);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) widgetContentHolder.getLayoutParams();
        layoutParams.gravity = widgetSettings.gravity;
        widgetContentHolder.setLayoutParams(layoutParams);

        for (int i = 0; i < widgetSettings.contents.size(); i++) {
            int content = widgetSettings.contents.get(i);
            if ((content & WidgetSettings.TEXTVIEW) == WidgetSettings.TEXTVIEW) {
                widgetContentHolder.addView(getTextView(widgetSettings, content));
            }

            if (i != widgetSettings.contents.size() - 1) {
                widgetContentHolder.addView(getDivider(widgetSettings));
            }
        }

        parent.setTag(widget);
        parent.addView(widget);
    }

    private static boolean isTemperature(View view) {
        return (((Integer) view.getTag()) & WidgetSettings.TEXTMASK) == WidgetSettings.CONTENT_TEMPERATURE;
    }

    private static boolean isCity(View view) {
        return (((Integer) view.getTag()) & WidgetSettings.TEXTMASK) == WidgetSettings.CONTENT_CITY;
    }

    private static boolean isWeatherDescription(View view) {
        return (((Integer) view.getTag()) & WidgetSettings.TEXTMASK) == WidgetSettings.CONTENT_WEATHER_DESCRIPTION;
    }

    private static View getDivider(WidgetSettings widgetSettings) {
        TextView divider = getTextView(widgetSettings, WidgetSettings.DIVIDER);
        divider.setText(" " + widgetSettings.divider + " ");
        return divider;
    }

    private static TextView getTextView(WidgetSettings widgetSettings, int content) {
        TextView textView = new TextView(Common.XGELS_CONTEXT);
        textView.setTag(content);
        textView.setTextColor(widgetSettings.textColor);
        if (widgetSettings.textSize != -1) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
        }

        if (widgetSettings.textShadow) {
            textView.setShadowLayer(2f, 1f, 1f, Color.BLACK);
        }

        if (widgetSettings.fillParent) {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            textView.setLayoutParams(lp);
        }

        return textView;
    }

    public static class WidgetSettings {

        public static final int TEXTVIEW = 0x80;
        public static final int DIVIDER = 0x100;
        public static final int TEXTMASK = 0xFF;

        public static final int CONTENT_CITY = 0x81;
        public static final int CONTENT_WEATHER_DESCRIPTION = 0x82;
        public static final int CONTENT_TEMPERATURE = 0x83;

        String divider, unit;
        boolean fillParent, textShadow;
        int gravity = Gravity.END, textSize = 10, textColor = Color.WHITE;

        ArrayList<Integer> contents = new ArrayList<Integer>();

        public WidgetSettings() {
            String settings =
                    "gravity=" + Gravity.CENTER_HORIZONTAL + "|" +
                            "fillparent=" + Boolean.toString(false) + "|" +
                            "textSize=" + "" + -1 + "|" +
                            "textShadow=" + "" + Boolean.toString(true) + "|" +
                            "textColor=" + "" + Color.WHITE + "|" +
                            "divider=" + "-" + "|" +
                            "contents=" + CONTENT_CITY + "#" + CONTENT_WEATHER_DESCRIPTION + "#" + CONTENT_TEMPERATURE;

            this.unit = getDefaultUnit();

            for (String setting : settings.split("\\|")) {
                if (setting.startsWith("gravity=")) {
                    this.gravity = Integer.parseInt(setting.split("=")[1]);
                } else if (setting.startsWith("fillparent=")) {
                    this.fillParent = Boolean.parseBoolean(setting.split("=")[1]);
                } else if (setting.startsWith("textSize=")) {
                    this.textSize = Integer.parseInt(setting.split("=")[1]);
                } else if (setting.startsWith("textShadow=")) {
                    this.textShadow = Boolean.parseBoolean(setting.split("=")[1]);
                }  else if (setting.startsWith("textColor=")) {
                    this.textColor = Integer.parseInt(setting.split("=")[1]);
                } else if (setting.startsWith("divider=")) {
                    this.divider = setting.split("=")[1];
                } else if (setting.startsWith("unit=")) {
                    this.unit = setting.split("=")[1];
                } else if (setting.startsWith("contents=")) {
                    for (String content : setting.replace("contents=", "").split("#")) {
                        contents.add(Integer.parseInt(content));
                    }
                }
            }
        }

        public String getDefaultUnit() {
            String countryCode = Locale.getDefault().getCountry();
            if ("US".equals(countryCode) // USA
                    || "LR".equals(countryCode) // liberia
                    || "MM".equals(countryCode)) {// burma
                return "F";
            }
            return "C";
        }
    }
}