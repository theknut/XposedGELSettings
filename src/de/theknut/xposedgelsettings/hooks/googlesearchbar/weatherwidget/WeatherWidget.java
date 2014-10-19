package de.theknut.xposedgelsettings.hooks.googlesearchbar.weatherwidget;

import android.content.Context;
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

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.theknut.xposedgelsettings.R;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.HooksBaseClass;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Classes;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Methods;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;
import de.theknut.xposedgelsettings.hooks.Utils;
import de.theknut.xposedgelsettings.hooks.googlesearchbar.GetWorkspacePaddingHook;

import static de.robv.android.xposed.XposedBridge.hookAllConstructors;
import static de.robv.android.xposed.XposedBridge.hookAllMethods;
import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getIntField;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

/**
 * Created by Alexander Schulz on 17.10.2014.
 */
public class WeatherWidget extends HooksBaseClass {

    static Object weatherEntry;
    private static LinearLayout widget;
    private static LinearLayout widgetContentHolder;

    static WidgetSettings widgetSettings = new WidgetSettings();

    public static void initAllHooks(final LoadPackageParam lpparam) {

        if (!PreferencesHelper.hideSearchBar && Common.PACKAGE_OBFUSCATED && Common.IS_PRE_GNL_4) {

            if (PreferencesHelper.searchBarWeatherWidget) {

                findAndHookMethod(Classes.NowOverlay, Methods.noOnShow, boolean.class, boolean.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        log("#############");
                        log("dSi getDescription " + callMethod(getObjectField(weatherEntry, "dSi"), "getDescription"));
                        log("dSi getLabel " + callMethod(getObjectField(weatherEntry, "dSi"), "getLabel"));
                        log("dSi abT" + getObjectField(getObjectField(weatherEntry, "dSi"), "abT"));
                        log("dSi dSm" + getObjectField(getObjectField(weatherEntry, "dSi"), "dSm"));
                        log("dSi dSn" + getObjectField(getObjectField(weatherEntry, "dSi"), "dSn"));
                        log("dSi dSo" + getObjectField(getObjectField(weatherEntry, "dSi"), "dSo"));
                        log("dSi dSp" + getObjectField(getObjectField(weatherEntry, "dSi"), "dSp"));
                        log("dSi dSq" + getObjectField(getObjectField(weatherEntry, "dSi"), "dSq"));
                        log("dSi dmR" + getObjectField(getObjectField(weatherEntry, "dSi"), "dmR"));
                        log("dSi dzE" + getObjectField(getObjectField(weatherEntry, "dSi"), "dzE"));
                        log("dSi dzt" + getObjectField(getObjectField(weatherEntry, "dSi"), "dzt"));
                        log("dSi dSs" + getObjectField(getObjectField(weatherEntry, "dSi"), "dSs"));
                        log("dSi dSr" + getObjectField(getObjectField(weatherEntry, "dSi"), "dSr"));
                        log("abV Addr " + callMethod(getObjectField(weatherEntry, "abV"), "getAddress"));
                        log("abV Name " + callMethod(getObjectField(weatherEntry, "abV"), "getName"));
                        log("#############");
                    }
                });

                hookAllConstructors(findClass("euo", lpparam.classLoader), new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        log("get " + getObjectField(param.thisObject, "bXY").getClass().getName());
                        weatherEntry = getObjectField(getObjectField(param.thisObject, "bXY"), "dwH");
                    }
                });

                findAndHookMethod(Classes.WeatherEntryAdapter, "" /*Methods.weaAddCurrentConditions*/, Context.class, Classes.UriLoader, Classes.WeatherPoint, View.class, new XC_MethodHook() {

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        maybeAddWidget();

                        final Animation fadeOut = AnimationUtils.loadAnimation(Common.XGELSCONTEXT, R.anim.weather_fade_out);
                        fadeOut.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) { }

                            @Override
                            public void onAnimationEnd(Animation animation) {

                                for (int i = 0; i < widgetContentHolder.getChildCount(); i++) {
                                    View item = widgetContentHolder.getChildAt(i);
                                    log("child " + item);
                                    if (isCity(item)) {
                                        log("set " + getCity());
                                        ((TextView) item).setText(getCity());
                                    } else if (isTemperature(item)) {
                                        log("set " + getTemperatur());
                                        ((TextView) item).setText(getTemperatur());
                                    } else if (isWeatherDescription(item)) {
                                        log("set " + getWeatherDescription());
                                        ((TextView) item).setText(getWeatherDescription());
                                    }
                                }

                                widget.startAnimation(AnimationUtils.loadAnimation(Common.XGELSCONTEXT, R.anim.weather_fade_in));
                            }

                            private String getWeatherDescription() {
                                return (String) callMethod(getObjectField(weatherEntry, "dSi"), "getDescription");
                            }

                            private String getTemperatur() {
                                return getIntField(getObjectField(weatherEntry, "dSi"), "dSm") + "°" + widgetSettings.unit;
                            }

                            private String getCity() {
                                return (String) callMethod(getObjectField(weatherEntry, "abV"), "getName");
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) { }
                        });

                        widget.setAnimation(fadeOut);
                    }
                });

                findAndHookMethod(Classes.DeviceProfile, Methods.dpGetWorkspacePadding, Integer.TYPE, new GetWorkspacePaddingHook());

                XC_MethodHook hook = new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        ViewGroup parent = (ViewGroup) ((ViewGroup) getObjectField(Common.LAUNCHER_INSTANCE, ObfuscationHelper.Fields.lSearchDropTargetBar)).getParent();
                        if (parent.getTag() != null) {
                            ViewGroup widget = (ViewGroup) parent.getTag();
                            widget.animate().alpha(0f).setDuration(175).start();
                        }
                    }
                };

                // show DropDeleteTarget on dragging items
                if (Common.PACKAGE_OBFUSCATED) {
                    // this is actually not DragSource but the parameter type is unknown as of now
                    findAndHookMethod(Classes.SearchDropTargetBar, Methods.sdtbOnDragStart, Classes.DragSource, Object.class, hook);
                } else {
                    hookAllMethods(Classes.SearchDropTargetBar, Methods.sdtbOnDragStart, hook);
                }

                hookAllMethods(Classes.SearchDropTargetBar, Methods.sdtbOnDragEnd, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        ViewGroup parent = (ViewGroup) ((ViewGroup) getObjectField(Common.LAUNCHER_INSTANCE, ObfuscationHelper.Fields.lSearchDropTargetBar)).getParent();
                        if (parent.getTag() != null) {
                            ViewGroup widget = (ViewGroup) parent.getTag();
                            widget.animate().alpha(1f).setDuration(200).start();
                        }
                    }
                });
            }
        }
    }

    public static void maybeAddWidget() {
        ViewGroup searchBar = ((ViewGroup) getObjectField(Common.LAUNCHER_INSTANCE, ObfuscationHelper.Fields.lSearchDropTargetBar));
        ViewGroup parent = (ViewGroup) searchBar.getParent();
        if (parent.getTag() != null) {
            widget = (LinearLayout) parent.getTag();
        } else {
            addWeatherWidget(searchBar);
        }
    }

    //public static final byte CONTENT_CITY = 0xAA

    private static void addWeatherWidget(ViewGroup searchBar) {
        ViewGroup parent = ((ViewGroup) searchBar.getParent());
        widget = (LinearLayout) LayoutInflater.from(Common.XGELSCONTEXT).inflate(R.layout.qsb_weather_widget, null, true);
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
        log(view.getTag() + " " +( ((Integer) view.getTag()) & WidgetSettings.CONTENT_CITY));
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
        TextView textView = new TextView(Common.XGELSCONTEXT);
        textView.setTag(content);
        textView.setTextColor(widgetSettings.textColor);
        log("Add " + content);
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
                            "unit=" + "C" + "|" +
                            "contents=" + CONTENT_CITY + "#" + CONTENT_WEATHER_DESCRIPTION + "#" + CONTENT_TEMPERATURE;

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


    }
}
