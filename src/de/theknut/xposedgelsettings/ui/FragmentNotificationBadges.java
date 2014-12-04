package de.theknut.xposedgelsettings.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.theknut.xposedgelsettings.R;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.Utils;
import de.theknut.xposedgelsettings.ui.preferences.MyPreferenceScreen;
import de.theknut.xposedgelsettings.ui.preferences.SwitchCompatPreference;

@SuppressLint("WorldReadableFiles")
public class FragmentNotificationBadges extends FragmentBase {

    int displayWidth = -1, displayHeigth = -1;
    int measuredWidth = -1, measuredHeigth = -1;
    int leftRightPadding, topBottomPadding;
    int frameSize;

    public int notificationBadgeFrameSize;
    public int notificationBadgeTextSize;
    public int notificationBadgeCornerRadius;
    public int notificationBadgeBackgroundColor;
    public int notificationBadgeTextColor;
    public int notificationBadgeFrameColor;
    public int notificationBadgeLeftRightPadding;
    public int notificationBadgeTopBottomPadding;

    View rootView;
    TextView badgePreview;

    MyPreferenceScreen presetsPreference = null;
    String presetsKey = "notificationbadgepresets";

    OnSharedPreferenceChangeListener updateBadge = new OnSharedPreferenceChangeListener() {

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            sharedPreferences.edit().commit();
            List<String> keys = Arrays.asList(presetsKey, "notificationbadge_dialer_launch", "notificationbadge_dialer");
            if (keys.contains(key)) return;

            addIcon();

            int notificationbadgepresets = Integer.parseInt(sharedPreferences.getString(presetsKey, "1"));
            if (notificationbadgepresets != 0) {
                sharedPreferences.edit().remove(presetsKey).putString(presetsKey, "" + 0).apply();
                presetsPreference.setSummary(getResources().getStringArray(R.array.notificationbadge_presets_entries)[0]);
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        rootView = inflater.inflate(R.layout.notificationbadges_fragment, container, false);
        addPreferencesFromResource(R.xml.notificationbadges_fragment);

        presetsPreference = (MyPreferenceScreen) findPreference(presetsKey);
        final int modeIdx = Integer.parseInt(sharedPrefs.getString(presetsKey, "1"));
        presetsPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new MaterialDialog.Builder(mActivity)
                        .theme(Theme.DARK)
                        .title(R.string.pref_notificationbadge_presets_title)
                        .items(getResources().getStringArray(R.array.notificationbadge_presets_entries))
                        .itemsCallbackSingleChoice(Integer.parseInt(sharedPrefs.getString(presetsKey, "1")), new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                                switch (which) {
                                    case 1:
                                        applyPreset(Color.parseColor("#FFD44937"), Color.WHITE, "10", "0", Color.TRANSPARENT, "5", "5", "2");
                                        break;
                                    case 2:
                                        applyPreset(Color.parseColor("#A5D44937"), Color.WHITE, "10", "0", Color.TRANSPARENT, "5", "5", "2");
                                        break;
                                    case 3:
                                        applyPreset(Color.parseColor("#404040"), Color.WHITE, "10", "1", Color.parseColor("#8b8b8b"), "30", "5", "2");
                                        break;
                                    case 4:
                                        applyPreset(Color.parseColor("#404040"), Color.WHITE, "10", "1", Color.parseColor("#a8660c"), "30", "5", "2");
                                        break;
                                    case 5:
                                        applyPreset(Color.BLACK, Color.WHITE, "10", "2", Color.WHITE, "30", "5", "2");
                                        break;
                                    case 6:
                                        applyPreset(Color.parseColor("#FFD44937"), Color.WHITE, "10", "1", Color.WHITE, "25", "5", "2");
                                        break;
                                    case 7:
                                        applyPreset(-1738448543, Color.WHITE, "10", "1", Color.WHITE, "25", "5", "2");
                                        break;
                                    default:
                                        break;
                                }

                                presetsPreference.setSummary(getResources().getStringArray(R.array.notificationbadge_presets_entries)[which]);

                                // due to legacy reasons we need to save it as string
                                sharedPrefs.edit().remove(presetsKey).putString(presetsKey, "" + which).apply();

                                dialog.dismiss();
                            }
                        })
                        .build()
                        .show();
                return false;
            }
        });
        presetsPreference.setSummary(getResources().getStringArray(R.array.notificationbadge_presets_entries)[modeIdx]);

        final MyPreferenceScreen badgePosition = (MyPreferenceScreen) this.findPreference("notificationbadgeposition");
        List<PrefClass> listPrefs = new ArrayList<PrefClass>();
        listPrefs.add(new PrefClass(this.findPreference("notificationbadgetextsize"), "10" , R.array.notificationbadge_textsize_values));
        listPrefs.add(new PrefClass(this.findPreference("notificationbadgeframesize"), "0", R.array.notificationbadge_frame_values));
        listPrefs.add(new PrefClass(this.findPreference("notificationbadgecornerradius"), "5", R.array.notificationbadge_corner_values));
        listPrefs.add(new PrefClass(this.findPreference("notificationbadgeleftrightpadding"), "5", R.array.notificationbadge_leftright_padding_values));
        listPrefs.add(new PrefClass(this.findPreference("notificationbadgetopbottompadding"), "2", R.array.notificationbadge_topbottom_padding_values));

        List<ColorPickerPreference> colorPrefs = new ArrayList<ColorPickerPreference>();
        colorPrefs.add((ColorPickerPreference) this.findPreference("notificationbadgebackgroundcolor"));
        colorPrefs.add((ColorPickerPreference) this.findPreference("notificationbadgetextcolor"));
        colorPrefs.add((ColorPickerPreference) this.findPreference("notificationbadgeframecolor"));

        final MyPreferenceScreen dialerPref = (MyPreferenceScreen) this.findPreference("notificationbadge_dialer");
        final MyPreferenceScreen smsPref = (MyPreferenceScreen) this.findPreference("notificationbadge_sms");
        MyPreferenceScreen misseditPref = (MyPreferenceScreen) this.findPreference("missedit");
        MyPreferenceScreen advancedMessagePref = (MyPreferenceScreen) this.findPreference("advanced_message");
        SwitchCompatPreference enablePref = (SwitchCompatPreference) this.findPreference("enablenotificationbadges");

        if (!InAppPurchase.isPremium) {
            List prefs = new ArrayList();
            prefs.addAll(colorPrefs);
            prefs.addAll(listPrefs);
            prefs.addAll(Arrays.asList(badgePosition, dialerPref, smsPref, advancedMessagePref, enablePref));

            for (Object pref : prefs) {
                if (pref instanceof Preference) {
                    ((Preference) pref).setEnabled(false);
                } else {
                    ((PrefClass) pref).pref.setEnabled(false);
                }
            }

            badgePosition.setEnabled(false);
            dialerPref.setEnabled(false);
            smsPref.setEnabled(false);
            advancedMessagePref.setEnabled(false);
            enablePref.setEnabled(false);
            this.findPreference(presetsKey).setEnabled(false);
        } else {
            getPreferenceScreen().removePreference(this.findPreference("needsDonate"));

            final int idx = Integer.parseInt(sharedPrefs.getString(badgePosition.getKey(), "0"));
            final String[] positionEntries = getResources().getStringArray(R.array.notificationbadge_position_entries);
            badgePosition.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    new MaterialDialog.Builder(mActivity)
                            .title(preference.getTitle())
                            .theme(Theme.DARK)
                            .items(positionEntries)
                            .itemsCallbackSingleChoice(idx, new MaterialDialog.ListCallback() {
                                @Override
                                public void onSelection(MaterialDialog materialDialog, View view, int which, CharSequence text) {
                                    // due to legacy reasons we need to save them as strings... -.-
                                    sharedPrefs.edit()
                                            .remove(badgePosition.getKey())
                                            .putString(badgePosition.getKey(), "" + getResources().getStringArray(R.array.notificationbadge_position_values)[which])
                                            .apply();
                                    badgePosition.setSummary(text);
                                    materialDialog.dismiss();
                                }
                            })
                            .build()
                            .show();
                    return true;
                }
            });
            badgePosition.setSummary(positionEntries[idx]);

            for (final PrefClass pref : listPrefs) {
                pref.pref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        final String[] values = getResources().getStringArray(pref.arrayId);
                        final NumberPicker numberPicker = CommonUI.getNumberPicker(mContext, sharedPrefs, values, pref.key, pref.defVal);
                        new MaterialDialog.Builder(mActivity)
                                .title(pref.pref.getTitle())
                                .customView(numberPicker)
                                .theme(Theme.DARK)
                                .positiveText(android.R.string.ok)
                                .callback(new MaterialDialog.SimpleCallback() {
                                    @Override
                                    public void onPositive(MaterialDialog materialDialog) {
                                        // due to legacy reasons we need to save them as strings... -.-
                                        sharedPrefs.edit()
                                                .remove(pref.key)
                                                .putString(pref.key, "" + values[numberPicker.getValue()])
                                                .apply();
                                        pref.pref.setSummary(values[numberPicker.getValue()]);
                                        materialDialog.dismiss();
                                    }
                                })
                                .build()
                                .show();
                        return true;
                    }
                });
                pref.pref.setSummary(sharedPrefs.getString(pref.key, pref.defVal));
            }

            int currIdx = Integer.parseInt(sharedPrefs.getString(badgePosition.getKey(), "0"));
            badgePosition.setSummary(getResources().getStringArray(R.array.notificationbadge_position_entries)[currIdx]);

            OnPreferenceClickListener opcl = new OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {

                    CommonUI.restartLauncher(false);

                    Intent intent = new Intent(mContext, ChooseAppList.class);
                    intent.putExtra("prefKey", preference.getKey());
                    startActivityForResult(intent, 0);
                    return false;
                }
            };

            dialerPref.setOnPreferenceClickListener(opcl);
            smsPref.setOnPreferenceClickListener(opcl);
            dialerPref.setSummary(CommonUI.getAppName(dialerPref.getKey()));
            smsPref.setSummary(CommonUI.getAppName(smsPref.getKey()));

            advancedMessagePref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

                @SuppressWarnings("deprecation")
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    SharedPreferences prefs = mContext.getSharedPreferences(Common.PREFERENCES_NAME, Context.MODE_WORLD_READABLE);
                    prefs.edit()
                            .remove(dialerPref.getKey() + "_launch")
                            .putString(dialerPref.getKey() + "_launch", "")
                            .remove(smsPref.getKey() + "_launch")
                            .putString(smsPref.getKey() + "_launch", "").apply();
                    getFragmentManager().beginTransaction().replace(R.id.content_frame, new FragmentNotificationBadges()).commit();

                    return true;
                }
            });
        }

        final String misseditPackageName = "net.igecelabs.android.MissedIt";
        if (CommonUI.isPackageInstalled(misseditPackageName, mContext)) {

            misseditPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {
                    try {
                        Intent LaunchIntent = getActivity().getPackageManager().getLaunchIntentForPackage(misseditPackageName);
                        startActivity(LaunchIntent);
                    } catch (Exception e) {
                        Toast.makeText(mContext, "Ehm... that didn't work. Please open MissedIt manually :-|", Toast.LENGTH_LONG).show();
                    }

                    return true;
                }
            });

            misseditPref.setSummary(R.string.pref_notificationbadge_open_missedit_title);
            misseditPref.setSummary(R.string.pref_notificationbadge_open_missedit_summary);
        } else {
            misseditPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + misseditPackageName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + misseditPackageName)));
                    }
                    return true;
                }
            });

            misseditPref.setSummary(R.string.pref_notificationbadge_missedit_missing_title);
            misseditPref.setSummary(R.string.pref_notificationbadge_missedit_missing_summary);
        }

        rootView = CommonUI.setBackground(rootView, R.id.prefbackground);
        addIcon();

        return rootView;
    }

    public static class PrefClass {
        MyPreferenceScreen pref;
        String key, defVal;
        int arrayId;

        public PrefClass(Preference pref, String defVal, int arrayId) {
            this.pref = (MyPreferenceScreen) pref;
            this.key = pref.getKey();
            this.defVal = defVal;
            this.arrayId = arrayId;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(updateBadge);
    }

    @Override
    public void onPause() {

        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(updateBadge);

        super.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (data == null) return;

        String gestureKey = data.getStringExtra("prefKey");

        if (!gestureKey.equals("")) {
            MyPreferenceScreen pref = (MyPreferenceScreen) this.findPreference(gestureKey);
            pref.setSummary(CommonUI.getAppName(gestureKey));
        }
    }

    @SuppressLint("SdCardPath")
    public void addIcon() {

        initPrefs();

        badgePreview = (TextView) rootView.findViewById(R.id.badgepreviewicon);
        badgePreview.setDrawingCacheEnabled(true);
        badgePreview.setTextColor(Color.WHITE);
        badgePreview.setShadowLayer(2.0f, 0.0f, 0.0f, Color.BLACK);

        badgePreview.setText("Gmail");
        badgePreview.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_launcher_mail, 0, 0);

        badgePreview.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {

                Drawable d = badgePreview.getCompoundDrawables()[1];

                File file = new File("/mnt/sdcard/XposedGELSettings/badge.png");

                if (file.exists()) {
                    file.delete();
                }

                Bitmap bitmap = Blur.drawableToBitmap(d);
                FileOutputStream outStream = null;

                try {
                    file.getParentFile().mkdirs();
                    file.createNewFile();

                    outStream = new FileOutputStream(file.getAbsolutePath());
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, outStream);

                    outStream.flush();
                    outStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                ArrayList<Uri> uris = new ArrayList<Uri>();
                uris.add(Uri.parse("file://" + file.getAbsolutePath()));
                uris.add(Uri.parse("file://" + mContext.getApplicationInfo().dataDir + "/shared_prefs/de.theknut.xposedgelsettings_preferences.xml"));

                Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                String[] recipients = {"theknutcoding@gmail.com"};
                intent.putExtra(Intent.EXTRA_EMAIL, recipients);
                intent.putExtra(Intent.EXTRA_SUBJECT, "XGELS Badge Preset");
                intent.putExtra(Intent.EXTRA_TEXT, "Badge Preset Name: <name>");
                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                intent.setType("text/html");
                startActivity(Intent.createChooser(intent, "Send mail"));

                return true;
            }
        });

        setBadge();
    }

    public void setBadge() {

        TextView badge = null;
        int position = Integer.parseInt(mContext.getSharedPreferences(Common.PREFERENCES_NAME, Context.MODE_WORLD_READABLE).getString("notificationbadgeposition", "0"));
        int[] ids = new int[] {R.id.badgetopleft, R.id.badgetopright, R.id.badgebottomright, R.id.badgebottomleft};
        for (int i = 0; i < ids.length; i++) {
            badge = (TextView) rootView.findViewById(ids[i]);
            if (i == position) {
                badge.setVisibility(View.VISIBLE);
                initBadge(badge);
            } else {
                badge.setVisibility(View.GONE);
            }
        }
    }
    private void initBadge(TextView badge) {

        initMeasures();

        badge.setText("" + 5);
        badge.setTextColor(Color.parseColor(ColorPickerPreference.convertToARGB(notificationBadgeTextColor)));
        badge.setTextSize(TypedValue.COMPLEX_UNIT_DIP, notificationBadgeTextSize);
        badge.setGravity(Gravity.CENTER);
        badge.setIncludeFontPadding(false);
        badge.setPadding(
                leftRightPadding,
                topBottomPadding,
                leftRightPadding,
                topBottomPadding
        );

        GradientDrawable gdDefault = new GradientDrawable();
        gdDefault.setColor(Color.parseColor(ColorPickerPreference.convertToARGB(notificationBadgeBackgroundColor)));
        gdDefault.setCornerRadius(notificationBadgeCornerRadius);

        if (frameSize != 0) {
            gdDefault.setStroke(frameSize, Color.parseColor(ColorPickerPreference.convertToARGB(notificationBadgeFrameColor)));
        }

        gdDefault.setShape(GradientDrawable.RECTANGLE);

        badge.setBackground(gdDefault);
        badge.setDrawingCacheEnabled(true);
    }

    public void initMeasures() {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        frameSize = Utils.dpToPx(notificationBadgeFrameSize, displayMetrics);
        leftRightPadding = Utils.dpToPx(notificationBadgeLeftRightPadding, displayMetrics);
        topBottomPadding = Utils.dpToPx(notificationBadgeTopBottomPadding, displayMetrics);

        if (displayWidth == -1) {

            WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            displayWidth = size.x;
            displayHeigth = size.y;

            measuredWidth = MeasureSpec.makeMeasureSpec(displayWidth, MeasureSpec.AT_MOST);
            measuredHeigth = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
    }

    @SuppressWarnings("deprecation")
    public void initPrefs() {

        SharedPreferences prefs = mContext.getSharedPreferences(Common.PREFERENCES_NAME, Context.MODE_WORLD_READABLE);
        notificationBadgeFrameSize = Integer.parseInt(prefs.getString("notificationbadgeframesize", "0"));
        notificationBadgeTextSize = Integer.parseInt(prefs.getString("notificationbadgetextsize", "10"));
        notificationBadgeCornerRadius = Integer.parseInt(prefs.getString("notificationbadgecornerradius", "5"));
        notificationBadgeBackgroundColor = prefs.getInt("notificationbadgebackgroundcolor", Color.argb(0xA0, 0xD4, 0x49, 0x37));
        notificationBadgeTextColor = prefs.getInt("notificationbadgetextcolor", Color.argb(0xFF, 0xFF, 0xFF, 0xFF));
        notificationBadgeFrameColor = prefs.getInt("notificationbadgeframecolor", Color.argb(0xFF, 0xFF, 0xFF, 0xFF));
        notificationBadgeLeftRightPadding = Integer.parseInt(prefs.getString("notificationbadgeleftrightpadding", "5"));
        notificationBadgeTopBottomPadding = Integer.parseInt(prefs.getString("notificationbadgetopbottompadding", "2"));
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("CommitPrefEdits")
    public void applyPreset(int notificationbadgebackgroundcolor, int notificationbadgetextcolor, String notificationbadgetextsize, String notificationbadgeframesize, int notificationbadgeframecolor, String notificationbadgecornerradius, String notificationbadgeleftrightpadding, String notificationbadgetopbottompadding) {
        SharedPreferences prefs = mContext.getSharedPreferences(Common.PREFERENCES_NAME, Context.MODE_WORLD_READABLE);
        Editor editor = prefs.edit();

        changePref(editor, "notificationbadgebackgroundcolor", notificationbadgebackgroundcolor);
        changePref(editor, "notificationbadgetextcolor", notificationbadgetextcolor);
        changePref(editor, "notificationbadgetextsize", notificationbadgetextsize);
        changePref(editor, "notificationbadgeframesize", notificationbadgeframesize);
        changePref(editor, "notificationbadgeframecolor", notificationbadgeframecolor);
        changePref(editor, "notificationbadgecornerradius", notificationbadgecornerradius);
        changePref(editor, "notificationbadgeleftrightpadding", notificationbadgeleftrightpadding);
        changePref(editor, "notificationbadgetopbottompadding", notificationbadgetopbottompadding);

        addIcon();
    }

    public void changePref(Editor editor, String key, Object value) {
        editor.remove(key).commit();

        if (value instanceof Integer) {
            editor.putInt(key, (Integer) value).commit();
        } else if (value instanceof String) {
            editor.putString(key, (String) value).commit();
        }
    }
}