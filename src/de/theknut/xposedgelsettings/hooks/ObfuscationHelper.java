package de.theknut.xposedgelsettings.hooks;

import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import static de.robv.android.xposed.XposedHelpers.findClass;

public class ObfuscationHelper extends HooksBaseClass {

    public static final int GNL_3_3_11 = 300303110;
    public static final int GNL_3_4_15 = 300304150;
    public static final int GNL_3_5_14 = 300305140;
    public static final int GNL_3_6_13 = 300306130;
    public static final int GNL_3_6_16 = 300306160;
    public static final int GNL_3_9_00 = 300309000;
    public static final int GNL_4_0_26 = 300400260;
    public static final int GNL_4_1_21 = 300401210;
    public static final int GNL_4_1_29 = 300401290;
    public static final int GNL_4_2_16 = 300403094;
    public static final int GNL_4_3_10 = 300403395;
    public static final int GNL_4_4_09 = 300404573;
    public static final int GNL_4_5_12 = 300405825;
    public static final int GNL_4_5_13 = 300405835;
    public static final int GNL_4_6_10 = 300407085;
    public static final int GNL_4_7_12 = 300409539;
    public static final int GNL_4_7_13 = 300412076;
    public static final int GNL_4_8_10 = 300412106;
    public static final int GNL_4_9_22 = 300413946;
    public static final int GNL_5_1_15 = 300420096;
    public static final int GNL_5_2_33 = 300441146;
    public static final int GNL_5_3_23 = 300515416;
    public static final int GNL_5_3_26 = 300515596;
    public static final int GNL_5_4_24 = 300522076;
    public static final int GNL_5_5_26 = 300552016;
    public static final int GNL_5_5_28 = 300552106;

    public static int getVersionIndex(int version) {

        if (Common.HOOKED_PACKAGE.equals(Common.GEL_PACKAGE)) {
            if (version >= GNL_3_6_16 && version < GNL_3_9_00) {
                return 3;
            } else if (version >= GNL_5_5_26) {
                return 20;
            } else if (version >= GNL_5_4_24) {
                return 19;
            } else if (version >= GNL_5_3_23) {
                return 18;
            } else if (version >= GNL_5_2_33) {
                return 17;
            } else if (version >= GNL_5_1_15) {
                return 16;
            } else if (version >= GNL_4_9_22) {
                return 15;
            } else if (version >= GNL_4_8_10) {
                return 14;
            } else if (version >= GNL_4_7_12) {
                return 13;
            } else if (version >= GNL_4_6_10) {
                return 12;
            } else if (version >= GNL_4_5_12) {
                return 11;
            } else if (version >= GNL_4_4_09) {
                return 10;
            } else if (version >= GNL_4_3_10) {
                return 9;
            } else if (version >= GNL_4_2_16) {
                return 8;
            } else if (version >= GNL_4_1_29) {
                return 7;
            } else if (version >= GNL_4_1_21) {
                return 6;
            } else if (version >= GNL_4_0_26) {
                return 5;
            } else if (version >= GNL_3_9_00) {
                return 4;
            } else if (version >= GNL_3_6_13) {
                return 3;
            } else if (version >= GNL_3_5_14) {
                return 2;
            } else if (version >= GNL_3_4_15) {
                return 1;
            } else if (version <= GNL_3_3_11) {
                return 0;
            }
        }

        return 0;
    }

    public static void init(LoadPackageParam lpparam, int versionIdx) {
        long time = System.currentTimeMillis();

        ClassNames.initNames(versionIdx);
        Methods.initMethodNames(versionIdx);
        Fields.initFieldNames(versionIdx);
        Classes.hookAllClasses(lpparam);

        if (DEBUG) log("Initialized ObfuscationHelper in " + (System.currentTimeMillis() - time) + "ms");
    }

    public static class ClassNames {

        public static String LAUNCHER,
                WORKSPACE,
                WORKSPACE_STATE,
                DEVICE_PROFILE,
                DYNAMIC_GRID,
                CELL_LAYOUT,
                CELL_LAYOUT_CELL_INFO,
                CELL_LAYOUT_LAYOUT_PARAMS,
                PAGED_VIEW,
                PAGED_VIEW_ICON,
                PAGED_VIEW_CELL_LAYOUT,
                PAGED_VIEW_WITH_DRAGGABLE_ITEMS,
                APPS_CUSTOMIZE_CONTENT_TYPE,
                APPS_CUSTOMIZE_CELL_LAYOUT,
                APPS_CUSTOMIZE_LAYOUT,
                APPS_CUSTOMIZE_PAGED_VIEW,
                APPS_CUSTOMIZE_TAB_HOST,
                WALLPAPER_OFFSET_INTERPOLATOR,
                FOLDER,
                FOLDER_ICON,
                HOTSEAT,
                START_SETTINGS_ON_CLICK,
                DRAG_SOURCE,
                ITEM_INFO,
                APP_INFO,
                SHORTCUT_INFO,
                SEARCH_DROP_TARGET_BAR,
                ICON_CACHE,
                UTILITIES,
                CACHE_ENTRY,
                LOADER_TASK,
                FOLDER_INFO,
                LAUNCHER_MODEL,
                APP_WIDGET_RESIZE_FRAME,
                CELL_LAYOUT_ITEM_CONFIGURATION,
                LAUNCHER_APPWIDGET_INFO,
                DRAG_LAYER,
                LAUNCHER_APP_WIDGET_HOST_VIEW,
                BUBBLE_TEXT_VIEW,
                USER_HANDLE,
                LAUNCHER_ACTIVITY_INFO_COMPAT,
                GELCLASS,
                NOW_OVERLAY,
                SEARCH_OVERLAY_IMPL,
                GSA_CONFIG_FLAGS,
                RECOGNIZER_VIEW,
                SEARCH_PLATE,
                GEL_SEARCH_PLATE_CONTAINER,
                SEARCH_PLATE_TRANSITIONS_MANAGER,
                WEATHER_ENTRY_ADAPTER,
                SEARCH_SETTINGS,
                SEARCH_PLATE_BAR,
                URI_LOADER,
                WEATHER_POINT,
                LAUNCHER_APP_STATE,
                FOLDER_PAGED_VIEW,
                LAUNCHER_APPS_COMPAT,
                ALL_APPS_CONTAINER_VIEW,
                ALPHABETICAL_APPS_LIST,
                ALL_APPS_GRID_ADAPTER,
                APP_NAME_COMPARATOR,
                HINT_TEXT_VIEW,
                LAUNCHER_CALLBACKS_IMPL,
                FULL_MERGE_ALGORITHM,
                WIDGETS_MODEL,
                ALL_APPS_RECYCLER_VIEW,
                ALL_APPS_RECYCLER_VIEW_CONTAINER_VIEW;

        public static void initNames(int idx) {

            String prefix = "com.android.launcher3.";

            RECOGNIZER_VIEW = new String[]{"com.google.android.search.shared.ui.RecognizerView", "com.google.android.search.searchplate.RecognizerView", "com.google.android.search.searchplate.RecognizerView", "com.google.android.search.searchplate.RecognizerView", "com.google.android.search.searchplate.RecognizerView", "com.google.android.search.searchplate.RecognizerView", "com.google.android.search.searchplate.RecognizerView", "com.google.android.search.searchplate.RecognizerView", "com.google.android.search.searchplate.RecognizerView", "com.google.android.search.searchplate.RecognizerView", "com.google.android.search.searchplate.RecognizerView", "com.google.android.apps.gsa.searchplate.RecognizerView", "com.google.android.apps.gsa.searchplate.RecognizerView", "com.google.android.apps.gsa.searchplate.RecognizerView", "com.google.android.apps.gsa.searchplate.RecognizerView", "com.google.android.apps.gsa.searchplate.RecognizerView", "com.google.android.apps.gsa.searchplate.RecognizerView", "com.google.android.apps.gsa.searchplate.RecognizerView", "com.google.android.apps.gsa.searchplate.RecognizerView", "com.google.android.apps.gsa.searchplate.RecognizerView", "com.google.android.apps.gsa.searchplate.RecognizerView"}[idx];
            SEARCH_PLATE = new String[]{"com.google.android.search.shared.ui.SearchPlate", "com.google.android.search.searchplate.SearchPlate", "com.google.android.search.searchplate.SearchPlate", "com.google.android.search.searchplate.SearchPlate", "com.google.android.search.searchplate.SearchPlate", "com.google.android.search.searchplate.SearchPlate", "com.google.android.search.searchplate.SearchPlate", "com.google.android.search.searchplate.SearchPlate", "com.google.android.search.searchplate.SearchPlate", "com.google.android.search.searchplate.SearchPlate", "com.google.android.search.searchplate.SearchPlate", "com.google.android.apps.gsa.searchplate.SearchPlate", "com.google.android.apps.gsa.searchplate.SearchPlate", "com.google.android.apps.gsa.searchplate.SearchPlate", "com.google.android.apps.gsa.searchplate.SearchPlate", "com.google.android.apps.gsa.searchplate.SearchPlate", "com.google.android.apps.gsa.searchplate.SearchPlate", "com.google.android.apps.gsa.searchplate.SearchPlate", "com.google.android.apps.gsa.searchplate.SearchPlate", "com.google.android.apps.gsa.searchplate.SearchPlate", "com.google.android.apps.gsa.searchplate.SearchPlate"}[idx];
            NOW_OVERLAY = new String[]{"com.google.android.sidekick.shared.client.NowOverlay", "dzk", "enc", "evx", "evx", "fma", "gen", "gen", "com.google.android.sidekick.shared.client.aj", "com.google.android.sidekick.shared.client.aj", "com.google.android.sidekick.shared.client.ak", "com.google.android.sidekick.shared.client.aj", "com.google.android.sidekick.shared.client.ak", "com.google.android.sidekick.shared.client.am", "com.google.android.sidekick.shared.client.v", "com.google.android.sidekick.shared.legacyclient.m", "com.google.android.sidekick.shared.legacyclient.m", "com.google.android.sidekick.shared.legacyclient.c", "com.google.android.sidekick.shared.legacyclient.c", "com.google.android.apps.gsa.sidekick.shared.overlay.c", "com.google.android.apps.gsa.sidekick.shared.overlay.c"}[idx]; // now_overlay:card_view_state
            SEARCH_OVERLAY_IMPL = new String[]{"com.google.android.search.gel.SearchOverlayImpl", "ccu", "cmh", "cuc", "cuc", "ebj", "erb", "erb", "com.google.android.search.shared.overlay.u", "com.google.android.search.shared.overlay.u", "com.google.android.search.shared.overlay.u", "com.google.android.search.shared.overlay.u", "com.google.android.search.shared.overlay.s", "com.google.android.search.shared.overlay.s", "com.google.android.search.shared.overlay.m", "com.google.android.apps.gsa.search.shared.overlay.m", "com.google.android.apps.gsa.search.shared.overlay.m", "com.google.android.apps.gsa.search.shared.overlay.j", "com.google.android.apps.gsa.search.shared.overlay.l", "com.google.android.apps.gsa.search.shared.overlay.n", "com.google.android.apps.gsa.search.shared.overlay.n"}[idx]; // search_overlay_impl:search_box_stats
            GSA_CONFIG_FLAGS = new String[]{"com.google.android.search.core.GsaConfigFlags", "ayc", "bgr", "bnj", "bnj", "chh", "cug", "cug", "com.google.android.search.core.av", "com.google.android.search.core.au", "com.google.android.search.core.ak", "com.google.android.search.core.as", "com.google.android.search.core.aq", "com.google.android.search.core.ao", "com.google.android.search.core.v", "com.google.android.apps.gsa.search.core.b.n", "com.google.android.apps.gsa.search.core.b.o", "com.google.android.apps.gsa.search.core.config.GsaConfigFlags", "com.google.android.apps.gsa.search.core.config.GsaConfigFlags", "com.google.android.apps.gsa.search.core.config.GsaConfigFlags", "com.google.android.apps.gsa.search.core.config.GsaConfigFlags"}[idx]; // "int array"
            HINT_TEXT_VIEW = new String[]{"com.google.android.apps.gsa.searchplate.HintTextView", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "com.google.android.apps.gsa.searchplate.HintTextView", "com.google.android.apps.gsa.searchplate.HintTextView", "com.google.android.apps.gsa.searchplate.HintTextView"}[idx]; // "say_ok_google_x_translation"
            LAUNCHER_CALLBACKS_IMPL = new String[]{"com.google.android.launcher.c", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "com.google.android.launcher.c", "com.google.android.launcher.c", "com.google.android.launcher.c"}[idx]; // "reflection_last_predictions"

            SEARCH_SETTINGS = new String[]{"", "", "", "", "", "", "", "", "com.google.android.search.core.dx", "com.google.android.search.core.dz", "com.google.android.search.core.dz", "com.google.android.search.core.eb", "com.google.android.search.core.eb", "com.google.android.search.core.eh", "com.google.android.search.core.ch", "com.google.android.apps.gsa.search.core.b.u", "com.google.android.apps.gsa.search.core.b.x", "com.google.android.apps.gsa.search.core.config.t", "com.google.android.search.core.state.h", "com.google.android.apps.gsa.search.core.state.s", "com.google.android.apps.gsa.search.core.state.u"}[idx]; // first_hotword_hint_shown_at
            SEARCH_PLATE_BAR = new String[]{"", "", "", "", "", "", "", "", "com.google.android.search.searchplate.an", "com.google.android.search.searchplate.an", "com.google.android.search.searchplate.an", "com.google.android.apps.gsa.searchplate.ap", "com.google.android.apps.gsa.searchplate.aq", "com.google.android.apps.gsa.searchplate.ap", "com.google.android.apps.gsa.searchplate.o", "com.google.android.apps.gsa.searchplate.o", "com.google.android.apps.gsa.searchplate.o", "com.google.android.apps.gsa.searchplate.n", "com.google.android.apps.gsa.searchplate.n", "com.google.android.apps.gsa.searchplate.n", "com.google.android.apps.gsa.searchplate.q"}[idx]; // search_plate_rounded_corner_radius
            SEARCH_PLATE_TRANSITIONS_MANAGER = new String[]{"com.google.android.search.shared.ui.SearchPlate$TransitionsManager", "cen", "cog", "cwb", "cwb", "dsi", "egu", "egu", "com.google.android.search.searchplate.af", "com.google.android.search.searchplate.af", "com.google.android.search.searchplate.af", "com.google.android.apps.gsa.searchplate.ah", "com.google.android.apps.gsa.searchplate.ai", "com.google.android.apps.gsa.searchplate.ah", "com.google.android.apps.gsa.searchplate.l", "com.google.android.apps.gsa.searchplate.l", "com.google.android.apps.gsa.searchplate.l", "com.google.android.apps.gsa.searchplate.k", "com.google.android.apps.gsa.searchplate.k", "com.google.android.apps.gsa.searchplate.k", "com.google.android.apps.gsa.searchplate.n"}[idx]; // in SearchPlate: "(this, this);" "com.google.android.search.searchplate"
            GELCLASS = new String[]{"com.google.android.launcher.GEL", "com.google.android.launcher.GEL", "com.google.android.launcher.GEL", "com.google.android.launcher.GEL", "com.google.android.launcher.GEL", "com.google.android.launcher.GEL", "com.google.android.launcher.GEL", "com.google.android.launcher.GEL", "com.google.android.launcher.GEL", "com.google.android.launcher.GEL", "com.google.android.launcher.GEL", "com.google.android.launcher.GEL", "com.google.android.launcher.GEL", "com.google.android.launcher.GEL", "com.google.android.launcher.GEL", "com.google.android.launcher.GEL", "com.google.android.launcher.GEL", "com.google.android.launcher.GEL", "com.google.android.launcher.GEL", "com.google.android.launcher.GEL", "com.google.android.launcher.GEL", "com.google.android.launcher.GEL"}[idx];
            GEL_SEARCH_PLATE_CONTAINER = new String[]{"com.google.android.search.gel.GelSearchPlateContainer", "com.google.android.search.gel.GelSearchPlateContainer", "com.google.android.search.gel.GelSearchPlateContainer", "com.google.android.search.gel.GelSearchPlateContainer", "com.google.android.search.gel.GelSearchPlateContainer", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""}[idx];
            URI_LOADER = new String[]{"com.google.android.shared.util.UriLoader", "cxw", "eno", "dtb", "dtb", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""}[idx];
            WEATHER_POINT = new String[]{"com.google.geo.sidekick.Sidekick.WeatherEntry.WeatherPoint", "him", "ich", "ilp", "ilp", "aps", "aps", "ara", "com.google.android.apps.sidekick.e.ca", "com.google.android.apps.sidekick.e.ca", "com.google.android.apps.sidekick.e.ca", "", "", "", "", "", "", "", "", "", ""}[idx]; // getLocation in WeatherEntryAdapter // since GS 4.0 it's not the same class anymore but it does the same
            WEATHER_ENTRY_ADAPTER = new String[]{"com.google.android.sidekick.shared.cards.WeatherEntryAdapter", "dye", "elt", "euo", "euo", "fzq", "gtm", "gtm", "com.google.android.sidekick.shared.ui.qp.id", "com.google.android.sidekick.shared.ui.qp.ie", "com.google.android.sidekick.shared.ui.qp.im", "", "", "", "", "", "", "", "", "", ""}[idx]; // empty text -> "  "

            if (Common.GNL_VERSION >= ObfuscationHelper.GNL_5_3_23 && Common.GNL_PACKAGE_INFO.applicationInfo.targetSdkVersion >= 19) {
                idx = 0;
                FOLDER_PAGED_VIEW = prefix + "FolderPagedView";
                LAUNCHER_APPS_COMPAT = prefix + "compat.LauncherAppsCompat";
                ALL_APPS_CONTAINER_VIEW = prefix + "allapps.AllAppsContainerView";
                ALPHABETICAL_APPS_LIST = prefix + "allapps.AlphabeticalAppsList";
                ALL_APPS_GRID_ADAPTER = prefix + "allapps.AllAppsGridAdapter";
                APP_NAME_COMPARATOR = prefix + "model.AppNameComparator";
                ALL_APPS_RECYCLER_VIEW = prefix + "allapps.AllAppsRecyclerView";
                ALL_APPS_RECYCLER_VIEW_CONTAINER_VIEW = prefix + "allapps.AllAppsRecyclerViewContainerView";
                FULL_MERGE_ALGORITHM = prefix + "allapps.FullMergeAlgorithm";
                WIDGETS_MODEL = prefix + "model.WidgetsModel";
            }

            LAUNCHER = new String[]{prefix + "Launcher", prefix + "Launcher", prefix + "Launcher", prefix + "Launcher", prefix + "Launcher", prefix + "Launcher", prefix + "Launcher", prefix + "Launcher", prefix + "Launcher", prefix + "Launcher", prefix + "Launcher", prefix + "Launcher", prefix + "Launcher", prefix + "Launcher", prefix + "Launcher", prefix + "Launcher", prefix + "Launcher", prefix + "Launcher", prefix + "Launcher"}[idx];
            WORKSPACE = new String[]{prefix + "Workspace", prefix + "Workspace", prefix + "Workspace", prefix + "Workspace", prefix + "Workspace", prefix + "Workspace", prefix + "Workspace", prefix + "Workspace", prefix + "Workspace", prefix + "Workspace", prefix + "Workspace", prefix + "Workspace", prefix + "Workspace", prefix + "Workspace", prefix + "Workspace", prefix + "Workspace", prefix + "Workspace", prefix + "Workspace", prefix + "Workspace"}[idx];
            CELL_LAYOUT = new String[]{prefix + "CellLayout", prefix + "CellLayout", prefix + "CellLayout", prefix + "CellLayout", prefix + "CellLayout", prefix + "CellLayout", prefix + "CellLayout", prefix + "CellLayout", prefix + "CellLayout", prefix + "CellLayout", prefix + "CellLayout", prefix + "CellLayout", prefix + "CellLayout", prefix + "CellLayout", prefix + "CellLayout", prefix + "CellLayout", prefix + "CellLayout", prefix + "CellLayout", prefix + "CellLayout"}[idx];
            CELL_LAYOUT_LAYOUT_PARAMS = new String[]{CELL_LAYOUT + "$LayoutParams", CELL_LAYOUT + "$LayoutParams", CELL_LAYOUT + "$LayoutParams", CELL_LAYOUT + "$LayoutParams", CELL_LAYOUT + "$LayoutParams", CELL_LAYOUT + "$LayoutParams", CELL_LAYOUT + "$LayoutParams", CELL_LAYOUT + "$LayoutParams", CELL_LAYOUT + "$LayoutParams", CELL_LAYOUT + "$LayoutParams", CELL_LAYOUT + "$LayoutParams", CELL_LAYOUT + "$LayoutParams", CELL_LAYOUT + "$LayoutParams", CELL_LAYOUT + "$LayoutParams", CELL_LAYOUT + "$LayoutParams", CELL_LAYOUT + "$LayoutParams", CELL_LAYOUT + "$LayoutParams", CELL_LAYOUT + "$LayoutParams", CELL_LAYOUT + "$LayoutParams"}[idx];
			APPS_CUSTOMIZE_PAGED_VIEW = new String[]{prefix + "AppsCustomizePagedView", prefix + "AppsCustomizePagedView", prefix + "AppsCustomizePagedView", prefix + "AppsCustomizePagedView", prefix + "AppsCustomizePagedView", prefix + "AppsCustomizePagedView", prefix + "AppsCustomizePagedView", prefix + "AppsCustomizePagedView", prefix + "AppsCustomizePagedView", prefix + "AppsCustomizePagedView", prefix + "AppsCustomizePagedView", prefix + "AppsCustomizePagedView", prefix + "AppsCustomizePagedView", prefix + "AppsCustomizePagedView", prefix + "AppsCustomizePagedView", prefix + "AppsCustomizePagedView", prefix + "AppsCustomizePagedView", prefix + "AppsCustomizePagedView", prefix + "AppsCustomizePagedView"}[idx];
            APPS_CUSTOMIZE_TAB_HOST = new String[]{prefix + "AppsCustomizeTabHost", prefix + "AppsCustomizeTabHost", prefix + "AppsCustomizeTabHost", prefix + "AppsCustomizeTabHost", prefix + "AppsCustomizeTabHost", prefix + "AppsCustomizeTabHost", prefix + "AppsCustomizeTabHost", prefix + "AppsCustomizeTabHost", prefix + "AppsCustomizeTabHost", prefix + "AppsCustomizeTabHost", prefix + "AppsCustomizeTabHost", prefix + "AppsCustomizeTabHost", prefix + "AppsCustomizeTabHost", prefix + "AppsCustomizeTabHost", prefix + "AppsCustomizeTabHost", prefix + "AppsCustomizeTabHost", prefix + "AppsCustomizeTabHost", prefix + "AppsCustomizeTabHost", prefix + "AppsCustomizeTabHost"}[idx];
            SEARCH_DROP_TARGET_BAR = new String[]{prefix + "SearchDropTargetBar", prefix + "SearchDropTargetBar", prefix + "SearchDropTargetBar", prefix + "SearchDropTargetBar", prefix + "SearchDropTargetBar", prefix + "SearchDropTargetBar", prefix + "SearchDropTargetBar", prefix + "SearchDropTargetBar", prefix + "SearchDropTargetBar", prefix + "SearchDropTargetBar", prefix + "SearchDropTargetBar", prefix + "SearchDropTargetBar", prefix + "SearchDropTargetBar", prefix + "SearchDropTargetBar", prefix + "SearchDropTargetBar", prefix + "SearchDropTargetBar", prefix + "SearchDropTargetBar", prefix + "SearchDropTargetBar", prefix + "SearchDropTargetBar"}[idx];
            DRAG_LAYER = new String[]{prefix + "DragLayer", prefix + "DragLayer", prefix + "DragLayer", prefix + "DragLayer", prefix + "DragLayer", prefix + "DragLayer", prefix + "DragLayer", prefix + "DragLayer", prefix + "DragLayer", prefix + "DragLayer", prefix + "DragLayer", prefix + "DragLayer", prefix + "DragLayer", prefix + "DragLayer", prefix + "DragLayer", prefix + "DragLayer", prefix + "DragLayer", prefix + "DragLayer", prefix + "DragLayer"}[idx];
            FOLDER = new String[]{prefix + "Folder", prefix + "Folder", prefix + "Folder", prefix + "Folder", prefix + "Folder", prefix + "Folder", prefix + "Folder", prefix + "Folder", prefix + "Folder", prefix + "Folder", prefix + "Folder", prefix + "Folder", prefix + "Folder", prefix + "Folder", prefix + "Folder", prefix + "Folder", prefix + "Folder", prefix + "Folder", prefix + "Folder"}[idx];
            FOLDER_ICON = new String[]{prefix + "FolderIcon", prefix + "FolderIcon", prefix + "FolderIcon", prefix + "FolderIcon", prefix + "FolderIcon", prefix + "FolderIcon", prefix + "FolderIcon", prefix + "FolderIcon", prefix + "FolderIcon", prefix + "FolderIcon", prefix + "FolderIcon", prefix + "FolderIcon", prefix + "FolderIcon", prefix + "FolderIcon", prefix + "FolderIcon", prefix + "FolderIcon", prefix + "FolderIcon", prefix + "FolderIcon", prefix + "FolderIcon"}[idx];
            HOTSEAT = new String[]{prefix + "Hotseat", prefix + "Hotseat", prefix + "Hotseat", prefix + "Hotseat", prefix + "Hotseat", prefix + "Hotseat", prefix + "Hotseat", prefix + "Hotseat", prefix + "Hotseat", prefix + "Hotseat", prefix + "Hotseat", prefix + "Hotseat", prefix + "Hotseat", prefix + "Hotseat", prefix + "Hotseat", prefix + "Hotseat", prefix + "Hotseat", prefix + "Hotseat", prefix + "Hotseat", prefix + "Hotseat", prefix + "Hotseat", prefix + "Hotseat"}[idx];
            BUBBLE_TEXT_VIEW = new String[]{prefix + "BubbleTextView", prefix + "BubbleTextView", prefix + "BubbleTextView", prefix + "BubbleTextView", prefix + "BubbleTextView", prefix + "BubbleTextView", prefix + "BubbleTextView", prefix + "BubbleTextView", prefix + "BubbleTextView", prefix + "BubbleTextView", prefix + "BubbleTextView", prefix + "BubbleTextView", prefix + "BubbleTextView", prefix + "BubbleTextView", prefix + "BubbleTextView", prefix + "BubbleTextView", prefix + "BubbleTextView", prefix + "BubbleTextView", prefix + "BubbleTextView"}[idx];
            DEVICE_PROFILE = new String[]{prefix + "DeviceProfile", "mz", "qi", "rj", "rj", "tu", "vg", "vg", prefix + "bq", prefix + "bq", prefix + "bq", prefix + "br", prefix + "br", prefix + "br", prefix + "ar", prefix + "ar", prefix + "ar", prefix + "ar"}[idx]; // All Device Profiles must have
            DYNAMIC_GRID = new String[]{prefix + "DynamicGrid", "nw", "rf", "sg", "sg", "ur", "wd", "wd", prefix + "cn", prefix + "cn", prefix + "cn", prefix + "co", prefix + "co", prefix + "co", prefix + "bf", prefix + "bf", prefix + "bf", prefix + "bf"}[idx]; // --------
            CELL_LAYOUT_CELL_INFO = new String[]{CELL_LAYOUT + "$CellInfo", "lz", "pi", "qj", "qj", "sy", "ue", "ue", prefix + "al", prefix + "al", prefix + "al", prefix + "am", prefix + "am", prefix + "am", prefix + "y", prefix + "y", prefix + "y", prefix + "y"}[idx]; // Cell[view=
            PAGED_VIEW_WITH_DRAGGABLE_ITEMS = new String[]{prefix + "PagedViewWithDraggableItems", "vl", "yw", "zy", "zy", "acr", "aed", "aed", prefix + "kx", prefix + "kx", prefix + "kx", prefix + "la", prefix + "la", prefix + "la", prefix + "en", prefix + "en", prefix + "en", prefix + "en"}[idx]; // AppsCustomizePagedView extends
            WALLPAPER_OFFSET_INTERPOLATOR = new String[]{WORKSPACE + "$WallpaperOffsetInterpolator", "zd", "acp", "adr", "ads", "agt", "aih", "aih", prefix + "pa", prefix + "pa", prefix + "pa", prefix + "pc", prefix + "pc", prefix + "pc", prefix + "gj", prefix + "gj", prefix + "gj", prefix + "gk"}[idx]; // Error updating wallpaper offset
            START_SETTINGS_ON_CLICK = new String[]{ "", "pu", "td", "ue", "ue", "wt", "xz", "xz", prefix + "ek", prefix + "ek", prefix + "ek", prefix + "el", prefix + "el", prefix + "el", "", "", "", ""}[idx]; // in onCreate: "View localView3 = findViewById("
            ITEM_INFO = new String[]{prefix + "ItemInfo", "pr", "ta", "ub", "ub", "wq", "xx", "xx", prefix + "ei", prefix + "ei", prefix + "ei", prefix + "ej", prefix + "ej", prefix + "ej", prefix + "ca", prefix + "ca", prefix + "ca", prefix + "ca"}[idx]; // Item(id=
            APP_INFO = new String[]{prefix + "AppInfo", "kr", "ob", "pc", "pc", "rr", "sx", "sx", prefix + "d", prefix + "d", prefix + "d", prefix + "d", prefix + "d", prefix + "d", prefix + "d", prefix + "d", prefix + "d", prefix + "d"}[idx]; // ApplicationInfo
            SHORTCUT_INFO = new String[]{prefix + "ShortcutInfo", "vz", "zl", "aan", "aan", "ade", "aeq", "aeq", prefix + "li", prefix + "li", prefix + "li", prefix + "ll", prefix + "ll", prefix + "ll", prefix + "ex", prefix + "ex", prefix + "ex", prefix + "ex"}[idx]; // ShortcutInfo(title=
            ICON_CACHE = new String[]{prefix + "IconCache", "pk", "ss", "tt", "tt", "wi", "xo", "xo", prefix + "dy", prefix + "dy", prefix + "dy", prefix + "dz", prefix + "dz", prefix + "dz", prefix + "bs", prefix + "bs", prefix + "bs", prefix + "bs"}[idx]; // Launcher.IconCache
            UTILITIES = new String[]{prefix + "Utilities", "wi", "zu", "aaw", "aaw", "adm", "aez", "aez", prefix + "lr", prefix + "lr", prefix + "lr", prefix + "lt", prefix + "lt", prefix + "lt", prefix + "ff", prefix + "ff", prefix + "ff", prefix + "fg"}[idx]; // Launcher.Utilities
            LAUNCHER_MODEL = new String[]{prefix + "LauncherModel", "sg", "vq", "ws", "ws", "zh", "aat", "aat", prefix + "hi", prefix + "hi", prefix + "hi", prefix + "hm", prefix + "hm", prefix + "hm", prefix + "cy", prefix + "cy", prefix + "cy", prefix + "cw"}[idx]; // Error: ItemInfo passed to checkItemInfo doesn't match original
            LOADER_TASK = new String[]{LAUNCHER_MODEL + "$LoaderTask", "tb", "wl", "xn", "xn", "aae", "abq", "abq", prefix + "ih", prefix + "ih", prefix + "ih", prefix + "in", prefix + "in", prefix + "in", prefix + "dc", prefix + "dc", prefix + "dc", prefix + "da"}[idx]; // Should not call runBindSynchronousPage
            FOLDER_INFO = new String[]{prefix + "FolderInfo", "oz", "sh", "ti", "ti", "vy", "xj", "xj", prefix + "dt", prefix + "dt", prefix + "dt", prefix + "du", prefix + "du", prefix + "du", prefix + "bn", prefix + "bn", prefix + "bn", prefix + "bn"}[idx]; // FolderInfo(id=
            LAUNCHER_APP_STATE = new String[]{prefix + "LauncherAppState", "rr", "vb", "wd", "wd", "yt", "aad", "aad", prefix + "gr", prefix + "gr", prefix + "gr", prefix + "gs", prefix + "gs", prefix + "gs", prefix + "cn", prefix + "cn", prefix + "cn", prefix + "cn"}[idx]; // "LauncherAppState inited"
            LAUNCHER_APPWIDGET_INFO = new String[]{prefix + "LauncherAppWidgetInfo", "rv", "vf", "wh", "wh", "yx", "aah", "aah", prefix + "gv", prefix + "gv", prefix + "gv", prefix + "gw", prefix + "gw", prefix + "gw", prefix + "cq", prefix + "cq", prefix + "cq", prefix + "cq"}[idx]; // AppWidget(id=
            PAGED_VIEW = new String[]{prefix + "PagedView", prefix + "PagedView", prefix + "PagedView", prefix + "PagedView", prefix + "PagedView", prefix + "PagedView", prefix + "PagedView", prefix + "PagedView", prefix + "kg", prefix + "kg", prefix + "kg", prefix + "kj", prefix + "kj", prefix + "kj", prefix + "ed", prefix + "ed", prefix + "ed", prefix + "ed"}[idx];
			WORKSPACE_STATE = new String[]{WORKSPACE + "$State", "zc", "aco", "adq", "adr", "ags", "aig", "aig", prefix + "oz", prefix + "oz", prefix + "oz", prefix + "pb", prefix + "pb", prefix + "pb", prefix + "gi", prefix + "eh", prefix + "gi", prefix + "gi", prefix + "gj"}[idx]; // CellLayout cannot have UNSPECIFIED dimensions" the one with more members
            APPS_CUSTOMIZE_CELL_LAYOUT = new String[]{prefix + "AppsCustomizeCellLayout", "kw", "yr", "zt", "zt", "rw", "tc", "tc", prefix + "i", prefix + "i", prefix + "i", prefix + "j", prefix + "j", prefix + "j", prefix + "g", prefix + "g", prefix + "g", prefix + "g"}[idx]; // "getContext().getResources().getDrawable" new <>(localContext) - the one above
            APPS_CUSTOMIZE_CONTENT_TYPE = new String[]{APPS_CUSTOMIZE_PAGED_VIEW + "$ContentType", "lf", "oo", "pp", "pp", "se", "tk", "tk", prefix + "q", prefix + "q", prefix + "q", prefix + "s", prefix + "s", prefix + "s", prefix + "h", prefix + "h", prefix + "h", prefix + "h"}[idx];
            DRAG_SOURCE = new String[]{prefix + "DragSource", "nn", "qw", "rx", "rx", "ui", "vu", "vu", prefix + "ce", prefix + "ce", prefix + "ce", prefix + "cf", prefix + "cf", prefix + "cf", prefix + "az", prefix + "az", prefix + "az", prefix + "az"}[idx]; // in SearchDropTargetBar: ", Object paramObject, int paramInt)"
            CACHE_ENTRY = new String[]{ICON_CACHE + "$CacheEntry", "pl", "st", "tu", "tu", "wj", "xp", "xp", prefix + "ea", prefix + "ea", prefix + "ea", prefix + "eb", prefix + "eb", prefix + "eb", prefix + "bt", prefix + "bt", prefix + "bt", prefix + "bt"}[idx]; // new HashMap(50)
            APP_WIDGET_RESIZE_FRAME = new String[]{prefix + "AppWidgetResizeFrame", "ks", "oc", "pd", "pd", "rs", "sy", "sy", prefix + "e", prefix + "e", prefix + "e", prefix + "e", prefix + "e", prefix + "e", prefix + "e", prefix + "e", prefix + "e", prefix + "e"}[idx]; // in AppsCustomizePagedView first line below "if (i >= 17)"member initialization from Enum
			PAGED_VIEW_CELL_LAYOUT = new String[]{prefix + "PagedViewCellLayout", "vd", "yo", "zq", "zq", "acl", "adx", "adx", prefix + "kr", prefix + "kr", prefix + "kr", prefix + "ku", prefix + "ku", prefix + "ku", prefix + "eh", prefix + "eh", prefix + "eh", prefix + "eh"}[idx]; // CellLayout cannot have UNSPECIFIED dimensions" the one with more members
            CELL_LAYOUT_ITEM_CONFIGURATION = new String[]{CELL_LAYOUT + "$ItemConfiguration", "ma", "pj", "qk", "qk", "sz", "uf", "uf", prefix + "am", prefix + "am", prefix + "am", prefix + "an", prefix + "an", prefix + "an", prefix + "z", prefix + "z", prefix + "z", prefix + "z"}[idx]; // in CellLayout Math.abs(paramArrayOfInt[0]) last interface parameter
			LAUNCHER_APP_WIDGET_HOST_VIEW = new String[]{prefix + "LauncherAppWidgetHostView", "ru", "ve", "wg", "wg", "yw", "aag", "aag", prefix + "gu", prefix + "gu", prefix + "gu", prefix + "gv", prefix + "gv", prefix + "gv", prefix + "cp", prefix + "cp", prefix + "cp", prefix + "cp"}[idx]; // in Workspace "getAppWidgetInfo"
            PAGED_VIEW_ICON = new String[]{prefix + "PagedViewIcon", prefix + "PagedViewIcon", prefix + "PagedViewIcon", prefix + "PagedViewIcon", prefix + "PagedViewIcon", prefix + "PagedViewIcon", "", "", "", "", "", "", "", "", "", "", "", "", ""}[idx];
			APPS_CUSTOMIZE_LAYOUT = new String[]{prefix + "AppsCustomizeLayout", prefix + "AppsCustomizeLayout", prefix + "AppsCustomizeLayout", prefix + "AppsCustomizeLayout", prefix + "AppsCustomizeLayout", prefix + "AppsCustomizeLayout", prefix + "AppsCustomizeLayout", prefix + "AppsCustomizeLayout", prefix + "AppsCustomizeLayout", prefix + "AppsCustomizeLayout", prefix + "AppsCustomizeLayout", prefix + "AppsCustomizeLayout", prefix + "AppsCustomizeLayout", prefix + "AppsCustomizeLayout", prefix + "AppsCustomizeLayout", prefix + "AppsCustomizeLayout", prefix + "AppsCustomizeLayout", prefix + "AppsCustomizeLayout"}[idx]; // Trebuchet only
            LAUNCHER_ACTIVITY_INFO_COMPAT = new String[]{"com.android.launcher3.compat.LauncherActivityInfoCompat", "", "adb", "aed", "aee", "ahh", "aiw", "aiw", prefix + "b.d", prefix + "b.d", prefix + "b.d", prefix + "b.d", prefix + "b.d", prefix + "b.d", prefix + "b.d", prefix + "b.d", prefix + "b.d", prefix + "b.d"}[idx];
            USER_HANDLE = new String[]{"com.android.launcher3.compat.UserHandleCompat", "", "adl", "aen", "aeo", "ahw", "ajm", "ajm", prefix + "b.u", prefix + "b.u", prefix + "b.u", prefix + "b.u", prefix + "b.u", prefix + "b.u", prefix + "b.q", prefix + "b.q", prefix + "b.q", prefix + "b.q"}[idx]; // last parameter in IconCache "cacheLocked"
        }
    }

    public static class Classes {

        static public Class<?> Launcher,
                Workspace,
                AppInfo,
                AppsCustomizePagedView,
                AllAppsContainerView,
                AllAppsRecyclerViewContainerView,
                AllAppsRecyclerView,
                CellLayout,
                WallpaperOffsetInterpolator,
                PagedViewIcon,
                DeviceProfile,
                AppsCustomizeLayout,
                AppsCustomizeTabHost,
                Folder,
                FolderIcon,
                PagedView,
                GELClass,
                NowOverlay,
                SearchOverlayImpl,
                SearchDropTargetBar,
                DragSource,
                CellLayoutLayoutParams,
                CellLayoutCellInfo,
                WorkspaceState,
                Hotseat,
                StartSettingsOnClick,
                AppsCustomizeContentType,
                ShortcutInfo,
                IconCache,
                Utilities,
                CacheEntry,
                ItemInfo,
                LoaderTask,
                FolderInfo,
                LauncherModel,
                AppWidgetResizeFrame,
                ItemConfiguration,
                UserHandle,
                LauncherActivityInfoCompat,
                LauncherAppWidgetInfo,
                DragLayer,
                LauncherAppWidgetHostView,
                GSAConfigFlags,
                RecognizerView,
                SearchPlate,
                SearchPlateBar,
                GelSearchPlateContainer,
                TransitionsManager,
                BubbleTextView,
                LauncherAppState,
                DynamicGrid,
                AppsCustomizeCellLayout,
                WeatherEntryAdapter,
                WeatherPoint,
                SearchSettings,
                UriLoader,
                FolderPagedView,
                LauncherAppsCompat,
                AlphabeticalAppsList,
                AllAppsGridAdapter,
                HintTextView,
                AppNameComparator,
                FullMergeAlgorithm,
                GelAppFilter,
                WidgetsModel,
                LauncherCallbacksImpl;

        public static void hookAllClasses(LoadPackageParam lpparam) {
            Launcher = findClass(ClassNames.LAUNCHER, lpparam.classLoader);
            Workspace = findClass(ClassNames.WORKSPACE, lpparam.classLoader);

            if (Common.GNL_VERSION >= ObfuscationHelper.GNL_5_3_23) {
                FolderPagedView = findClass(ClassNames.FOLDER_PAGED_VIEW, lpparam.classLoader);
                LauncherAppsCompat = findClass(ClassNames.LAUNCHER_APPS_COMPAT, lpparam.classLoader);
                AllAppsContainerView = findClass(ClassNames.ALL_APPS_CONTAINER_VIEW, lpparam.classLoader);
                AlphabeticalAppsList = findClass(ClassNames.ALPHABETICAL_APPS_LIST, lpparam.classLoader);
                AllAppsGridAdapter = findClass(ClassNames.ALL_APPS_GRID_ADAPTER, lpparam.classLoader);
                AppNameComparator = findClass(ClassNames.APP_NAME_COMPARATOR, lpparam.classLoader);
                HintTextView = findClass(ClassNames.HINT_TEXT_VIEW, lpparam.classLoader);
                LauncherCallbacksImpl = findClass(ClassNames.LAUNCHER_CALLBACKS_IMPL, lpparam.classLoader);
                AllAppsRecyclerView = findClass(ClassNames.ALL_APPS_RECYCLER_VIEW, lpparam.classLoader);
                AllAppsRecyclerViewContainerView = findClass(ClassNames.ALL_APPS_RECYCLER_VIEW_CONTAINER_VIEW, lpparam.classLoader);
                FullMergeAlgorithm = findClass(ClassNames.FULL_MERGE_ALGORITHM, lpparam.classLoader);
                WidgetsModel = findClass(ClassNames.WIDGETS_MODEL, lpparam.classLoader);

                if (Common.GNL_VERSION >= ObfuscationHelper.GNL_5_5_26) {
                    GelAppFilter = findClass("com.google.android.apps.gsa.launcher.GelAppFilter", lpparam.classLoader);
                } else {
                    GelAppFilter = findClass("com.google.android.launcher.GelAppFilter", lpparam.classLoader);
                }
            } else {
                AppsCustomizePagedView = findClass(ClassNames.APPS_CUSTOMIZE_PAGED_VIEW, lpparam.classLoader);
                AppsCustomizeCellLayout = findClass(ClassNames.APPS_CUSTOMIZE_CELL_LAYOUT, lpparam.classLoader);
                AppsCustomizeContentType = findClass(ClassNames.APPS_CUSTOMIZE_CONTENT_TYPE, lpparam.classLoader);
            }

            CellLayout = findClass(ClassNames.CELL_LAYOUT, lpparam.classLoader);
            CellLayoutLayoutParams = findClass(ClassNames.CELL_LAYOUT_LAYOUT_PARAMS, lpparam.classLoader);
            WallpaperOffsetInterpolator = findClass(ClassNames.WALLPAPER_OFFSET_INTERPOLATOR, lpparam.classLoader);
            PagedView = findClass(ClassNames.PAGED_VIEW, lpparam.classLoader);
            DeviceProfile = findClass(ClassNames.DEVICE_PROFILE, lpparam.classLoader);

            AppInfo = findClass(ClassNames.APP_INFO, lpparam.classLoader);

            if (Common.GNL_VERSION < ObfuscationHelper.GNL_5_3_23)
                DynamicGrid = findClass(ClassNames.DYNAMIC_GRID, lpparam.classLoader);

            if (Common.IS_KK_TREBUCHET) {
                AppsCustomizeLayout = findClass(ClassNames.APPS_CUSTOMIZE_LAYOUT, lpparam.classLoader);
            } else {
                if (Common.GNL_VERSION < ObfuscationHelper.GNL_5_3_23)
                    AppsCustomizeTabHost = findClass(ClassNames.APPS_CUSTOMIZE_TAB_HOST, lpparam.classLoader);
            }

            CellLayoutCellInfo = findClass(ClassNames.CELL_LAYOUT_CELL_INFO, lpparam.classLoader);
            Folder = findClass(ClassNames.FOLDER, lpparam.classLoader);
            FolderIcon = findClass(ClassNames.FOLDER_ICON, lpparam.classLoader);
            Hotseat = findClass(ClassNames.HOTSEAT, lpparam.classLoader);
            DragSource = findClass(ClassNames.DRAG_SOURCE, lpparam.classLoader);
            ShortcutInfo = findClass(ClassNames.SHORTCUT_INFO, lpparam.classLoader);
            SearchDropTargetBar = findClass(ClassNames.SEARCH_DROP_TARGET_BAR, lpparam.classLoader);
            IconCache = findClass(ClassNames.ICON_CACHE, lpparam.classLoader);
            Utilities = findClass(ClassNames.UTILITIES, lpparam.classLoader);
            CacheEntry = findClass(ClassNames.CACHE_ENTRY, lpparam.classLoader);
            ItemInfo = findClass(ClassNames.ITEM_INFO, lpparam.classLoader);
            LoaderTask = findClass(ClassNames.LOADER_TASK, lpparam.classLoader);
            FolderInfo = findClass(ClassNames.FOLDER_INFO, lpparam.classLoader);
            LauncherModel = findClass(ClassNames.LAUNCHER_MODEL, lpparam.classLoader);
            AppWidgetResizeFrame = findClass(ClassNames.APP_WIDGET_RESIZE_FRAME, lpparam.classLoader);
            ItemConfiguration = findClass(ClassNames.CELL_LAYOUT_ITEM_CONFIGURATION, lpparam.classLoader);
            LauncherAppWidgetInfo = findClass(ClassNames.LAUNCHER_APPWIDGET_INFO, lpparam.classLoader);
            DragLayer = findClass(ClassNames.DRAG_LAYER, lpparam.classLoader);
            LauncherAppWidgetHostView = findClass(ClassNames.LAUNCHER_APP_WIDGET_HOST_VIEW, lpparam.classLoader);
            BubbleTextView = findClass(ClassNames.BUBBLE_TEXT_VIEW, lpparam.classLoader);
            LauncherAppState = findClass(ClassNames.LAUNCHER_APP_STATE, lpparam.classLoader);

            if (Common.IS_PRE_GNL_4 && !Common.IS_L_TREBUCHET) {
                // PagedViewIcon was removed in Google Search 4.0
                PagedViewIcon = findClass(ClassNames.PAGED_VIEW_ICON, lpparam.classLoader);
            }

            if (Common.IS_L_TREBUCHET) {
                WorkspaceState = findClass(ClassNames.WORKSPACE_STATE, lpparam.classLoader);
                UserHandle = findClass(ClassNames.USER_HANDLE, lpparam.classLoader);
                LauncherActivityInfoCompat = findClass(ClassNames.LAUNCHER_ACTIVITY_INFO_COMPAT, lpparam.classLoader);
            }

            if (lpparam.packageName.equals(Common.GEL_PACKAGE)) {
                GELClass = findClass(ClassNames.GELCLASS, lpparam.classLoader);
                NowOverlay = findClass(ClassNames.NOW_OVERLAY, lpparam.classLoader);
                SearchOverlayImpl = findClass(ClassNames.SEARCH_OVERLAY_IMPL, lpparam.classLoader);
                GSAConfigFlags = findClass(ClassNames.GSA_CONFIG_FLAGS, lpparam.classLoader);
                TransitionsManager = findClass(ClassNames.SEARCH_PLATE_TRANSITIONS_MANAGER, lpparam.classLoader);
                RecognizerView = findClass(ClassNames.RECOGNIZER_VIEW, lpparam.classLoader);
                SearchPlate = findClass(ClassNames.SEARCH_PLATE, lpparam.classLoader);

                if (Common.PACKAGE_OBFUSCATED) {
                    WorkspaceState = findClass(ClassNames.WORKSPACE_STATE, lpparam.classLoader);

                    if (Common.GNL_VERSION >= GNL_4_2_16) {
                        SearchSettings = findClass(ClassNames.SEARCH_SETTINGS, lpparam.classLoader);
                        SearchPlateBar = findClass(ClassNames.SEARCH_PLATE_BAR, lpparam.classLoader);
                    }

                    if (Common.GNL_VERSION >= GNL_4_0_26 && Common.GNL_VERSION < GNL_4_5_12) {
                        WeatherEntryAdapter = findClass(ClassNames.WEATHER_ENTRY_ADAPTER, lpparam.classLoader);
                        WeatherPoint = findClass(ClassNames.WEATHER_POINT, lpparam.classLoader);
                    }

                    if (Common.IS_PRE_GNL_4) {
                        // GelSearchPlateContainer was removed in Google Search 4.0
                        GelSearchPlateContainer = findClass(ClassNames.GEL_SEARCH_PLATE_CONTAINER, lpparam.classLoader);
                        if (Common.GNL_VERSION != GNL_3_6_16) WeatherEntryAdapter = findClass(ClassNames.WEATHER_ENTRY_ADAPTER, lpparam.classLoader);
                        UriLoader = findClass(ClassNames.URI_LOADER, lpparam.classLoader);
                        WeatherPoint = findClass(ClassNames.WEATHER_POINT, lpparam.classLoader);
                    }

                    if (Common.GNL_VERSION >= ObfuscationHelper.GNL_3_5_14) {
                        UserHandle = findClass(ClassNames.USER_HANDLE, lpparam.classLoader);
                        LauncherActivityInfoCompat = findClass(ClassNames.LAUNCHER_ACTIVITY_INFO_COMPAT, lpparam.classLoader);
                    }

                    if (Common.GNL_VERSION >= ObfuscationHelper.GNL_3_4_15
                            && Common.GNL_VERSION < ObfuscationHelper.GNL_4_8_10) {
                        StartSettingsOnClick = findClass(ClassNames.START_SETTINGS_ON_CLICK, lpparam.classLoader);
                    }
                }
            }
        }
    }

    public static class Methods {

        public static String pviApplyFromApplicationInfo,
                clAddViewToCellLayout,
                clSetIsHotseat,
                woiSyncWithScroll,
                wStartDrag,
                acpvOnPackagesUpdated,
                lGetQsbBar,
                pvPageBeginMoving,
                pvPageEndMoving,
                sdtbOnDragStart,
                sdtbOnDragEnd,
                lHasCustomContentToLeft,
                lHideAppsCustomizeHelper,
                lShowWorkspace,
                lAddOnResumeCallback,
                wMoveToDefaultScreen,
                pvOverScroll,
                lFinishBindingItems,
                dpGetWorkspacePadding,
                lIsAllAppsVisible,
                wGetOpenFolder,
                wIsOnOrMovingToCustomContent,
                wEnterOverviewMode,
                wMoveToCustomContentScreen,
                pvSnapToPage,
                lOpenFolder,
                wOnDragEnd,
                wGetViewForTag,
                wGetScreenWithId,
                wGetFolderForTag,
                lCloseFolder,
                lCloseFolderWParam,
                acthOnTabChanged,
                pvSetCurrentPage,
                dpUpdateFromConfiguration,
                dgGetDeviceProfile,
                acthSetInsets,
                wSnapToPage,
                soiSetSearchStarted,
                noOnShow,
                wOnLauncherTransitionEnd,
                fiAdd,
                fiAddItem,
                fiRemove,
                sawMeasureChild,
                fGetItemsInReadingOrder,
                fBind,
                clGetShortcutsAndWidgets,
                acthGetContentTypeForTabTag,
                wOnTransitionPrepare,
                icGetFullResIcon,
                uCreateIconBitmap,
                icCacheLocked,
                clMarkCellsForView,
                lmCheckItemPlacement,
                acpvBeginDragging,
                acpvUpdatePageCounts,
                lBindAppsUpdated,
                lmIsShortcutInfoUpdateable,
                clAttemptPushInDirection,
                btvSetShadowsEnabled,
                btvApplyFromApplicationInfo,
                btvApplyFromShortcutInfo,
                acpvSetApps,
                acpvUpdateApps,
                acpvRemoveApps,
                lSetWorkspaceBackground,
                lGetDragLayer,
                dlAddResizeFrame,
                btvCreateGlowingOutline,
                lmDeleteItemsFromDatabase,
                lmDeleteFolderContentsFromDatabase,
                siGetIcon,
                spSetProximityToNow,
                tmSetTransitionsEnabled,
                uIsL,
                lasIsDisableAllApps,
                acpvSyncAppsPageItems,
                acpvSetContentType,
                acpvInvalidatePageData,
                acpvSyncPages,
                pvIsLayoutRtl,
                pvGetPageAt,
                acpvEnableHwLayersOnVisiblePages,
                lCreateAppDragInfo,
                aiMakeShortcut,
                lmGetAppNameComparator,
                acthSetContentTypeImmediate,
                wGetWorkspaceAndHotseatCellLayouts,
                fiFromXml,
                acpvSetAllAppsPadding,
                weaAddCurrentConditions,
                wUpdateStateForCustomContent,
                weaUpdateWeather,
                wpGetWeatherDescription,
                wpGetTemperatur,
                lDispatchOnLauncherTransitionStart,
                lDispatchOnLauncherTransitionEnd,
                uGetCenterDeltaInScreenSpace,
                spOnModeChanged,
                clGetChildrenScale,
                acpvRemoveAllViewsOnPage,
                wGetChangeStateAnimation,
                ssHotwordUsageStats,
                ssFirstHotwordHintShownAt,
                htvShowHotword,
                htvAnimateHotword;
        public static String[] covbMethods;

        public static void initMethodNames(int idx) {

            soiSetSearchStarted = new String[]{"setSearchStarted", "cs", "cI", "cI", "cI", "eG", "fg", "fh", "gt", "hc", "hG", "hN", "io", "iM", "je", "ei", "ey", "eU", "ef", "eN", "eX"}[idx]; // 1. "search_overlay_impl:search_started" 2. onResume before cancel()
            tmSetTransitionsEnabled = new String[]{"setTransitionsEnabled", "cG", "cY", "cZ", "cZ", "ea", "eE", "eF", "fM", "gw", "hc", "cR", "df", "dt", "dA", "eM", "fc", "fy", "eK", "ft", "fD"}[idx]; // (4)
            ssHotwordUsageStats = new String[]{"", "", "", "", "", "", "", "", "afv", "ask", "azJ", "aCQ", "aHh", "aLB", "aUf", "XF", "Zl", "Zl", "", "", ""}[idx]; // "hotword_usage_stats"
            ssFirstHotwordHintShownAt = new String[]{"", "", "", "", "", "", "", "", "afw", "asl", "azK", "aCR", "aHi", "aLC", "aUg", "XG", "Zm", "", "bcW", "aim", "anx"}[idx]; // "first_hotword_hint_shown_at"
            htvShowHotword = new String[]{"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "eC", "fl", "ft"}[idx]; // ".setFloatValues(new float[] { 0.0F, 1.0F });"
            htvAnimateHotword =    new String[]{"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "aqI", "aqI", "aDo", "aKB"}[idx]; // "cancel()"
            noOnShow = new String[]{"onShow", "p", "u", "v", "v", "x", "y", "y", "z", "A", "D", "C", "A", "C", "F", "I", "I", "G", "E", "z", "A"}[idx]; // 1. "now_overlay" 2. boolean paramBoolean1, boolean paramBoolean2 3. the one with isConnected
            spSetProximityToNow = new String[]{"setProximityToNow", "x", "x", "x", "x", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""}[idx]; // (float paramFloat) with RecognizerView
            weaAddCurrentConditions = new String[]{"addCurrentConditions", "a", "a", "a", "a", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""}[idx];
            weaUpdateWeather = new String[]{"", "", "", "", "", "aCZ", "aGu", "aGu", "bfe", "biH", "bpo", "", "", "", "", "", "", "", "", "", ""}[idx]; // only "void" method
            wpGetWeatherDescription = new String[]{"", "", "", "", "", "tz", "vd", "vd", "AZ", "XI", "ado", "", "", "", "", "", "", "", "", "", ""}[idx]; // in WeatherEntryAdapter - (TextUtils.isEmpty(str)))
            wpGetTemperatur = new String[]{"", "", "", "", "", "tx", "vb", "vb", "AX", "XG", "adm", "", "", "", "", "", "", "", "", "", ""}[idx]; // in WeatherEntryAdapter - ().length() > 3
            spOnModeChanged = new String[]{"onModeChanged", "", "", "", "", "av", "aA", "aA", "bf", "bE", "bJ", "aR", "aR", "aU", "ba", "bh", "bm", "bl", "aJ", "aN", ""}[idx]; // 1. com.google.android.apps.gsa.searchplate.SearchPlate 2. "if ((paramInt1 == 0) && ((paramInt2 & 0x4) != 0))"

            covbMethods = new String[][]{
                    {"", "", ""}, {"", "", ""}, {"", "", ""}, {"", "", ""}, {"", "", ""}, {"", "", ""}, {"", "", ""}, {"", "", ""}, {"", "", ""}, {"", "", ""}, {"", "", ""}, {"", "", ""}, {"", "", ""},
                    {"Uk", "Ul", "Um"},
                    {"aai", "aaj", "aak"},
                    {"aov", "aow", "aox"},
                    {"asA", "asB", "asz"},
                    {"awu", "awv", "awv"},
                    {"aqG", "aqE", "aqH"},
                    {"", "", ""},
                    {"", "", ""}
            }[idx]; // com.google.android.apps.gsa.searchplate.ClearOrVoiceButton

            if (Common.GNL_VERSION >= ObfuscationHelper.GNL_5_3_23) {
                idx = 0;
                lIsAllAppsVisible = "isAppsViewVisible";
            } else {
                lIsAllAppsVisible = new String[]{"isAllAppsVisible", "gs", "hh", "hq", "hr", "hI", "iD", "iD", "kv", "kv", "ky", "ky", "kA", "kA", "kA", "kA", "kB", "jk"}[idx]; // onBackPressed second if clause method call
            }

            lOpenFolder = new String[]{"openFolder", "i", "i", "i", "i", "i", "i", "i", "k", "k", "k", "k", "k", "k", "k", "k", "k", "c"}[idx]; // "Opening folder ("
            lHideAppsCustomizeHelper = new String[]{"hideAppsCustomizeHelper", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a"}[idx]; // config_appsCustomizeConcealTime
            lShowWorkspace = new String[]{"showWorkspace", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a"}[idx]; // boolean paramBoolean, Runnable paramRunnable
            lAddOnResumeCallback = new String[]{"addOnResumeCallback", "a", "a", "a", "a", "a", "a", "a", "d", "d", "d", "d", "d", "d", "d", "d", "d", "c"}[idx]; // (Runnable paramRunnable)
            lCloseFolderWParam = new String[]{"closeFolder", "h", "h", "h", "h", "h", "h", "h", "h", "h", "h", "h", "h", "h", "h", "h", "h", "b"}[idx]; // if ((ViewGroup)paramFolder.getParent().getParent() != null)
            lBindAppsUpdated = new String[]{"bindAppsUpdated", "l", "l", "l", "l", "l", "l", "l", "l", "l", "l", "l", "l", "l", "l", "l", "l", "k"}[idx]; // "(this, paramArrayList), false));"
            lGetQsbBar = new String[]{"getQsbBar", "gw", "hl", "hu", "hv", "hM", "", "", "", "", "", "", "", "", "", "", "", ""}[idx]; // "public View "
            lHasCustomContentToLeft = new String[]{"hasCustomContentToLeft", "fL", "gA", "gJ", "gK", "hc", "ib", "ib", "jM", "jM", "jP", "jP", "jS", "jS", "jR", "jR", "jS", "iI"}[idx]; // "()) || (!"
            lFinishBindingItems = new String[]{"finishBindingItems", "U", "Z", "Z", "Z", "ac", "aa", "aa", "au", "av", "av", "av", "av", "av", "av", "au", "au", "aq"}[idx]; // hasFocus()
            lCloseFolder = new String[]{"closeFolder", "gr", "hg", "hp", "hq", "hH", "iC", "iC", "ku", "ku", "kx", "kx", "kz", "kz", "kz", "kz", "kA", "jj"}[idx]; // if (localFolder != null)
            lSetWorkspaceBackground = new String[]{"setWorkspaceBackground", "N", "S", "S", "S", "V", "W", "W", "ao", "ap", "ap", "ap", "ap", "ap", "ap", "ap", "ap", "al"}[idx]; // localView.setBackground(localDrawable);
            lGetDragLayer = new String[]{"getDragLayer", "fV", "gK", "gT", "gU", "hn", "ik", "ik", "jZ", "jZ", "kc", "kc", "kf", "kf", "ke", "ke", "kf", "iR"}[idx]; // public DragLayer
            lCreateAppDragInfo = new String[]{"createAppDragInfo", "e", "d", "d", "d", "d", "d", "d", "e", "e", "e", "e", "e", "e", "e", "e", "e", "e"}[idx]; // (Intent paramIntent)
            lDispatchOnLauncherTransitionStart = new String[]{"dispatchOnLauncherTransitionStart", "c", "c", "c", "c", "c", "c", "c", "c", "c", "c", "c", "c", "c", "c", "c", "c", "c"}[idx]; // (paramView, 0.0F);
            lDispatchOnLauncherTransitionEnd = new String[]{"dispatchOnLauncherTransitionEnd", "d", "d", "d", "d", "d", "d", "d", "d", "d", "d", "d", "d", "d", "d", "d", "d", "d"}[idx]; // (paramView, 1.0F);
            clAttemptPushInDirection = new String[]{"attemptPushInDirection", "b", "b", "b", "b", "b", "b", "b", "c", "c", "c", "c", "c", "c", "c", "c", "c", "c", "c"}[idx]; // "if (Math.abs(paramArrayOfInt[0]) + Math.abs(paramArrayOfInt[1]) > 1)"
            clMarkCellsForView = new String[]{"markCellsForView", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a"}[idx]; // int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean[][] paramArrayOfBoolean, boolean paramBoolean
            clGetShortcutsAndWidgets = new String[]{"getShortcutsAndWidgets", "dH", "ew", "eF", "eF", "eZ", "ez", "ez", "he", "he", "hh", "hh", "hj", "hj", "hj", "hj", "hk", "gk"}[idx]; // first method call in "View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean1, boolean paramBoolean2)"
            clAddViewToCellLayout = new String[]{"addViewToCellLayout", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a"}[idx]; // View paramView, int paramInt1, int paramInt2, CellLayout.LayoutParams paramLayoutParams, boolean paramBoolean
            clSetIsHotseat = new String[]{"setIsHotseat", "D", "G", "G", "G", "I", "J", "J", "Y", "Z", "Z", "Z", "Z", "Z", "Z", "Z", "Z", "X"}[idx]; // to assignments of "(paramBoolean);"
            clGetChildrenScale = new String[]{"getChildrenScale", "dv", "ek", "et", "et", "eM", "em", "em", "gR", "gR", "gU", "gU", "gW", "gW", "gW", "gW", "gX", "fW"}[idx]; // paramView.setScaleX(<method>)
            wStartDrag = new String[]{"startDrag", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a"}[idx]; // isInTouchMode
            wMoveToDefaultScreen = new String[]{"moveToDefaultScreen", "ao", "at", "at", "at", "av", "at", "at", "aN", "aO", "aO", "aO", "aO", "aO", "aO", "aN", "aN", "aI"}[idx]; // Launcher onNewIntent method call of workspace member with (true)
            pvOverScroll = new String[]{"overScroll", "g", "g", "g", "g", "h", "h", "h", "o", "o", "o", "o", "o", "o", "o", "o", "o", "m"}[idx]; // (float paramFloat)
            wGetOpenFolder = new String[]{"getOpenFolder", "jp", "kj", "kn", "ks", "kK", "lV", "lV", "oY", "oY", "pa", "pe", "pg", "ph", "ph", "ph", "pg", "nb"}[idx]; // localDragLayer.getChildCount();
            wIsOnOrMovingToCustomContent = new String[]{"isOnOrMovingToCustomContent", "jJ", "kE", "kI", "kN", "le", "mp", "mp", "pu", "pu", "pw", "pA", "pC", "pD", "pD", "pD", "pC", "nw"}[idx]; // " == 0);"
            wEnterOverviewMode = new String[]{"enterOverviewMode", "jO", "kJ", "kN", "kS", "lj", "mu", "mu", "pB", "pB", "pD", "pH", "pJ", "pK", "pK", "pK", "pJ", "nC"}[idx]; // "(true, -1, true);"
            wMoveToCustomContentScreen = new String[]{"moveToCustomContentScreen", "ap", "au", "au", "au", "aw", "au", "au", "an", "ao", "ao", "ao", "ao", "ao", "ao", "ao", "ao", "ak"}[idx]; // "View localView = getChildAt(i);" with "-301L"
            wSnapToPage = new String[]{"snapToPage", "bc", "bs", "bt", "bv", "bA", "bz", "bz", "cM", "cL", "cL", "cN", "cN", "cN", "cN", "cN", "cN", "cx"}[idx]; // in PagedView requestChildFocus - last line of method
            wOnDragEnd = new String[]{"onDragEnd", "dt", "ei", "er", "er", "eK", "ek", "ek", "gO", "gO", "gR", "gR", "gT", "gT", "gT", "gT", "gU", "fU"}[idx]; // only method without interface parameters with InstallShortcutReceiver
            wGetWorkspaceAndHotseatCellLayouts = new String[]{"getWorkspaceAndHotseatCellLayouts", "ka", "kV", "la", "lf", "lx", "mH", "mH", "pS", "pS", "pU", "pY", "qa", "qb", "qb", "qb", "qa", "nR"}[idx]; // localArrayList.add((CellLayout)getChildAt(j));
            wGetViewForTag = new String[]{"getViewForTag", "I", "V", "V", "V", "af", "ae", "ae", "at", "at", "at", "at", "at", "at", "at", "at", "at", "af"}[idx]; // "(this, paramObject));"
            wGetFolderForTag = new String[]{"getFolderForTag", "H", "U", "U", "U", "ae", "ad", "ad", "as", "as", "as", "as", "as", "as", "as", "as", "as", "ae"}[idx]; // "public Folder"
            wGetScreenWithId = new String[]{"getScreenWithId", "j", "j", "j", "j", "j", "j", "j", "p", "p", "p", "p", "p", "p", "p", "p", "p", "m"}[idx]; // public CellLayout
            wUpdateStateForCustomContent = new String[]{"updateStateForCustomContent", "H", "av", "aL", "aL", "aU", "aU", "aU", "dg", "df", "df", "dh", "dh", "dh", "dh", "dh", "dh", "bM"}[idx]; // setBackgroundAlpha(0.8F
            wGetChangeStateAnimation = new String[]{"getChangeStateAnimation", "", "", "", "", "b", "b", "b", "c", "c", "c", "c", "c", "c", "c", "c", "c", "b"}[idx]; // (float paramFloat, boolean paramBoolean)
            pvPageBeginMoving = new String[]{"pageBeginMoving", "ii", "iY", "jb", "jc", "jn", "kw", "kw", "nh", "nh", "nj", "nn", "np", "np", "np", "np", "nq", "lt"}[idx]; // above "awakenScrollBars"
            pvPageEndMoving = new String[]{"pageEndMoving", "ij", "iZ", "jc", "jd", "jJ", "kU", "kU", "nI", "nI", "nK", "nO", "nQ", "nQ", "nQ", "nQ", "nR", "lV"}[idx]; // method above "accessibility"
            pvSnapToPage = new String[]{"snapToPage", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a"}[idx]; // int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean, TimeInterpolator paramTimeInterpolator
            pvGetPageAt = new String[]{"getPageAt", "at", "aJ", "aJ", "aJ", "aS", "aS", "aS", "bX", "bW", "bW", "bW", "bW", "bW", "bW", "bW", "bW", "bK"}[idx]; // return getChildAt(paramInt);
            pvSetCurrentPage =  new String[]{"setCurrentPage", "aV", "bl", "bm", "bo", "bt", "bs", "bs", "cE", "cD", "cD", "cF", "cF", "cF", "cF", "cF", "cF", "cp"}[idx]; // "if (getChildCount() == 0)"
            pvIsLayoutRtl = new String[]{"isLayoutRtl", "hX", "iN", "iQ", "iR", "jc", "kl", "kl", "iw", "iw", "iz", "iz", "iB", "iB", "iB", "iB", "iC", "hY"}[idx]; // "getLayoutDirection() == 1"
            sdtbOnDragStart = new String[]{"onDragStart", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a"}[idx]; // twice .start in the method
            sdtbOnDragEnd = new String[]{"onDragEnd", "dt", "ei", "er", "er", "eK", "ek", "ek", "gO", "gO", "gR", "gR", "gT", "gT", "gT", "gT", "gU", "fU"}[idx]; // twice .reverse
            btvApplyFromApplicationInfo = new String[]{"applyFromApplicationInfo", "", "", "", "", "b", "b", "b", "b", "b", "b", "b", "b", "b", "b", "b", "b", "b"}[idx];
            btvApplyFromShortcutInfo = new String[]{"applyFromShortcutInfo", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a"}[idx]; // Bitmap localBitmap = param
            acpvOnPackagesUpdated = new String[]{"onPackagesUpdated", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a"}[idx]; // "can not fit on this device"
            acpvSetApps = new String[]{"setApps", "b", "b", "b", "b", "b", "b", "b", "b", "b", "b", "b", "b", "b", "b", "b", "b", "b"}[idx]; // Collections.sort
            acpvUpdateApps = new String[]{"updateApps", "g", "g", "g", "g", "g", "g", "g", "d", "c", "c", "c", "c", "c", "c", "c", "c", "c"}[idx]; // contains method which calls "binarySearch"
            acpvRemoveApps = new String[]{"removeApps", "f", "f", "f", "f", "f", "f", "f", "f", "f", "f", "f", "f", "f", "f", "f", "f", "f"}[idx]; //
            acpvEnableHwLayersOnVisiblePages = new String[]{"enableHwLayersOnVisiblePages", "db", "dQ", "dZ", "dZ", "et", "dU", "dU", "gw", "gw", "gz", "gz", "gB", "gB", "gB", "gB", "gC", "fE"}[idx]; // "localArrayList2.add(" between break; and return;
            acpvSyncAppsPageItems = new String[]{"syncAppsPageItems", "aq", "aG", "aG", "aG", "aP", "aP", "aP", "g", "g", "g", "g", "g", "g", "g", "g", "g", "f"}[idx]; // int k = Math.min(i + j, this.
            acpvSetContentType = new String[]{"setContentType", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a"}[idx];
            acpvInvalidatePageData = new String[]{"invalidatePageData", "j", "k", "k", "k", "k", "j", "j", "o", "o", "o", "o", "o", "o", "o", "o", "o", "n"}[idx]; // method for "(i, true);"
            acpvSyncPages = new String[]{"syncPages", "da", "dP", "dY", "dY", "es", "dT", "dT", "gv", "gv", "gy", "gy", "gA", "gA", "gA", "gA", "gB", "fD"}[idx]; // removeAllViews
            acpvBeginDragging = new String[]{"beginDragging", "n", "n", "n", "n", "G", "E", "E", "R", "R", "R", "R", "S", "S", "S", "S", "S", "R"}[idx]; // postDelayed
            acpvUpdatePageCounts = new String[]{"updatePageCounts", "cO", "dD", "dM", "dM", "eg", "dG", "dG", "gk", "gk", "gn", "gn", "gp", "gp", "gp", "gp", "gq", "fs"}[idx]; // (int)Math.ceil
            acpvRemoveAllViewsOnPage = new String[]{"removeAllViewsOnPage", "cI", "dx", "dG", "dG", "eb", "dC", "dC", "gf", "gf", "gi", "gi", "gk", "gk", "gk", "gk", "gl", "fo"}[idx]; // ")localView).<methodname>"
            acthSetInsets = new String[]{"setInsets", "c", "c", "c", "c", "b", "b", "b", "e", "e", "e", "e", "e", "e", "e", "e", "e", "d"}[idx]; // (Rect
            acthGetContentTypeForTabTag = new String[]{"getContentTypeForTabTag", "j", "r", "r", "q", "q", "p", "p", "y", "y", "y", "y", "y", "y", "y", "y", "y", "x"}[idx]; // (String paramString)
            acthSetContentTypeImmediate = new String[]{"setContentTypeImmediate", "b", "b", "b", "b", "b", "b", "b", "b", "b", "b", "b", "b", "b", "b", "b", "b", "b"}[idx]; // setOnTabChangedListener(null)
            dpGetWorkspacePadding = new String[]{"getWorkspacePadding", "aC", "aS", "aS", "aS", "ba", "ba", "ba", "ch", "cg", "cg", "cg", "cg", "cg", "cg", "cg", "cg", "bU"}[idx]; // Rect localRect2 = new Rect();
            dpUpdateFromConfiguration = new String[]{"updateFromConfiguration", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a"}[idx]; // float paramFloat, int paramInt, Resources paramResources, DisplayMetrics paramDisplayMetrics
            dgGetDeviceProfile = new String[]{"getDeviceProfile", "eV", "fK", "fT", "fT", "gl", "fM", "fM", "iP", "iP", "iS", "iS", "iU", "iU", "iU", "iU", "iV", "hP"}[idx]; // public final
            fGetItemsInReadingOrder = new String[]{"getItemsInReadingOrder", "fr", "gh", "gq", "gr", "gJ", "hJ", "hJ", "jr", "jr", "ju", "ju", "jw", "jw", "jv", "jv", "jw", "in"}[idx]; // public ArrayList
			fiAddItem = new String[]{"addItem", "i", "i", "i", "i", "i", "i", "i", "i", "i", "i", "i", "i", "i", "i", "i", "i", "i"}[idx]; // FolderIcon - below "getVisibility() == 0;" with interface parameter ShortcutInfo
            fBind = new String[]{"bind", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a"}[idx]; // ", boolean paramBoolean1, boolean paramBoolean2)"
            fiAdd = new String[]{"add", "j", "j", "j", "j", "j", "j", "j", "j", "j", "j", "j", "j", "j", "j", "j", "j", "j"}[idx]; // FolderInfo - .add
            fiRemove = new String[]{"remove", "k", "k", "k", "k", "k", "k", "k", "k", "k", "k", "k", "k", "k", "k", "k", "k", "k"}[idx]; // FolderInfo - .remove
            fiFromXml = new String[]{"fromXml", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a"}[idx]; // FolderIcon - method with ".topMargin" in it
            sawMeasureChild = new String[]{"measureChild", "M", "M", "M", "M", "ad", "ad", "ad", "as", "as", "as", "as", "at", "at", "at", "at", "at", "ar"}[idx]; // in Launcher above "return localFolderIcon"
            siGetIcon = new String[]{"getIcon", "a", "a", "a", "a", "b", "b", "b", "b", "b", "b", "b", "b", "b", "b", "b", "b", "b"}[idx]; // public Bitmap
            aiMakeShortcut = new String[]{"makeShortcut", "cE", "dt", "dC", "dC", "dX", "dy", "dy", "ga", "ga", "ge", "ge", "gg", "gg", "gg", "gg", "gh", "fk"}[idx]; // (this);
            btvSetShadowsEnabled = new String[]{"setShadowsEnabled", "w", "z", "z", "z", "", "", "", "", "", "", "", "", "", "", "", "", ""}[idx]; // invalidate
            icGetFullResIcon = new String[]{"getFullResIcon", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a"}[idx]; // (Resources paramResources, int paramInt)
            icCacheLocked = new String[]{"cacheLocked", "b", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a"}[idx];
            uCreateIconBitmap = new String[]{"createIconBitmap", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a"}[idx]; // (Drawable paramDrawable, Context paramContext)
            uGetCenterDeltaInScreenSpace = new String[]{"getCenterDeltaInScreenSpace", "", "", "", "", "b", "b", "b", "b", "b", "b", "b", "b", "b", "b", "b", "b", "b"}[idx]; // public static int[]
            lmGetAppNameComparator = new String[]{"getAppNameComparator", "hw", "im", "iq", "ir", "iB", "jH", "jH", "mh", "mh", "mj", "mn", "mp", "mp", "mp", "mp", "mq", "kE"}[idx]; // public static final Comparator
			lmDeleteFolderContentsFromDatabase = new String[]{"deleteFolderContentsFromDatabase", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a"}[idx]; // (Context paramContext, FolderInfo paramsh)
			lmCheckItemPlacement = new String[]{"checkItemPlacement", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a"}[idx]; // "Error loading shortcut into "
            dlAddResizeFrame = new String[]{"addResizeFrame", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a"}[idx]; // ", CellLayout paramCellLayout)"
            lasIsDisableAllApps = new String[]{"isDisableAllApps", "ha", "hS", "hW", "hX", "im", "jd", "jd", "lC", "lC", "lE", "lF", "lH", "lH", "lH", "lH", "lI", "kc"}[idx]; // launcher_noallapps
            woiSyncWithScroll = new String[]{"syncWithScroll", "kf", "la", "lf", "lk", "lC", "mM", "mM", "pZ", "pZ", "qb", "qf", "qh", "qi", "qi", "qi", "qh", "nW"}[idx]; // computeScroll in Workspace

            wOnLauncherTransitionEnd = new String[]{"onLauncherTransitionEnd", "a", "a", "a", "a", "a", "a", "a", "c", "c", "c", "c", "c", "c", "c", "a", "a", "c"}[idx]; // (Launcher paramLauncher, boolean paramBoolean1, boolean paramBoolean2)
            wOnTransitionPrepare = new String[]{"onLauncherTransitionPrepare", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a"}[idx]; // Method with "if ((bool) && (" in it
			pviApplyFromApplicationInfo = new String[]{"applyFromApplicationInfo", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a"}[idx]; // only pre GNL 4
            lmDeleteItemsFromDatabase = new String[]{"deleteItemsFromDatabase", "b", "b", "b", "b", "c", "b", "b", "c", "c", "c", "c", "c", "c", "c", "c", "c", "c"}[idx]; // (paramContext, localArrayList); - link to "deleting a folder"
            uIsL = new String[]{"", "", "jO", "jS", "jV", "", "", "", "", "", "", "", "", "", "", "", "", ""}[idx];
			btvCreateGlowingOutline = new String[]{"createGlowingOutline", "a", "a", "a", "a", "", "", "", "", "", "", "", "", "", "", "", "", ""}[idx];
			acpvSetAllAppsPadding = new String[]{"setAllAppsPadding", "b", "b", "b", "b", "", "", "", "", "", "", "", "", "", "", "", "", ""}[idx]; // .set(paramRect);
			acthOnTabChanged = new String[]{"onTabChanged", "c", "c", "c", "c", "", "", "", "", "", "", "", "", "", "", "", "", ""}[idx]; // setBackgroundColor
			lmIsShortcutInfoUpdateable = new String[]{"isShortcutInfoUpdateable", "e", "e", "e", "e", "f", "", "", "", "", "", "", "", "", "", "", "", ""}[idx]; // "android.intent.action.MAIN"

            if (Common.GNL_VERSION == ObfuscationHelper.GNL_5_3_26) {
                ssFirstHotwordHintShownAt = "bcU";
            }
        }
    }

    public static class Fields {

        public static String dpHotseatAllAppsRank,
                dpNumHotseatIcons,
                dpNumCols,
                cllpCanReorder,
                sdtbIsSearchBarHidden,
                sdtbQsbBar,
                wCustomContentShowing,
                pvCurrentPage,
                lHotseat,
                lAppsCustomizeTabHost,
                acthInTransition,
                wState,
                wDefaultPage,
                btvShadowsEnabled,
                fiPreviewBackground,
                fiFolderName,
                fFolderEditText,
                fFolderInfo,
                fiFolderInfo,
                fiFolder,
                fiContents,
                fiOpened,
                acpvContentType,
                pvIsPageMoving,
                wIsSwitchingState,
                dpHotseatBarHeightPx,
                lState,
                wTouchState,
                pvNextPage,
                lHasFocus,
                lPaused,
                aiComponentName,
                acpvAllAppsNumCols,
                acpvAllAppsNumRows,
                pvPageIndicator,
                acthContent,
                dpPageIndicatorHeightPx,
                lAppsCustomizePagedView,
                iiID,
                iiItemType,
                iiScreenId,
                iiContainer,
                ceIcon,
                lIconCache,
                fiLongPressHelper,
                clphHasPerformedLongPress,
                lawiProviderName,
                fMaxCountY,
                fMaxCountX,
                fMaxNumItems,
                fFolderNameHeight,
                acthAppsCustomizePane,
                acpvNumAppsPages,
                acpvCellCountX,
                acpvCellCountY,
                uIconWidth,
                uIconHeight,
                acpvAllApps,
                clIsHotseat,
                clShortcutsAndWidgets,
                sawIsHotseat,
                dpIconTextSize,
                acpvContentHeight,
                dpAllAppsIconSize,
                fContent,
                dpIconDrawablePaddingPx,
                dpFolderBackgroundOffset,
                lSearchDropTargetBar,
                wLastCustomContentScrollProgress,
                pvOverscrollX,
                dpFolderIconSize,
                lmWorkspaceItems,
                lmFolders,
                acclFocusHandlerView,
                acpvLayoutInflater,
                siIcon,
                spbMic;
        public static String[] covbFields;

        public static void initFieldNames(int idx) {

            spbMic = new String[]{"", "", "", "", "", "", "", "", "cnl", "cKX", "cWT", "bhN", "brt", "bAy", "bKi", "cfv", "cnJ", "czP", "cgX", "cyk", "cMr"}[idx]; // (enable field in search) 1. search_plate_rounded_corner_radius 2. <field>.getOpacity()
            covbFields = new String[][]{
                    {"", "", "", ""}, {"", "", "", ""}, {"", "", "", ""}, {"", "", "", ""}, {"", "", "", ""}, {"", "", "", ""}, {"", "", "", ""}, {"", "", "", ""},
                    {"qW", "ckE", "ckG", "ckH"},
                    {"qW", "cIq", "cIs", "cIt"},
                    {"ra", "cUk", "cUm", "cUn"},
                    {"rf", "beZ", "bfb", "bfc"},
                    {"rl", "boB", "boD", "boE"},
                    {"rl", "bxK", "bxM", "bxN"},
                    {"rk", "bHs", "bHu", "bHv"},
                    {"rk", "ccE", "ccG", "ccH"},
                    {"rk", "ckT", "ckU", "ckR"},
                    {"sz", "cwX", "cwY", "cwZ"},
                    {"tR", "cef", "ceg", "ceh"},
                    {"", "cvG", "cvG", "cvG"},
                    {"", "cJz", "cJz", "cJz"}
            }[idx]; // com.google.android.apps.gsa.searchplate.ClearOrVoiceButton

            if (Common.GNL_VERSION >= ObfuscationHelper.GNL_4_5_13
                    && Common.GNL_VERSION < ObfuscationHelper.GNL_4_6_10) {
                covbFields = new String[]{"rf", "beY", "bfa", "bfb"};
                spbMic = "bhM";
            }

            if (Common.GNL_VERSION >= ObfuscationHelper.GNL_5_3_23 && Common.GNL_PACKAGE_INFO.applicationInfo.targetSdkVersion >= 19) {
                idx = 0;
            }

            dpHotseatAllAppsRank = new String[]{"hotseatAllAppsRank", "zp", "BQ", "Cv", "Cu", "DY", "CV", "CV", "Is", "Ir", "Iw", "IC", "IN", "IN", "IM", "IM", "IM", "JX"}[idx]; // only / 2 operation
            dpNumHotseatIcons = new String[]{"numHotseatIcons", "yz", "AY", "BD", "BC", "Di", "Cg", "Cg", "HB", "HA", "HF", "HL", "HW", "HW", "HV", "HV", "HV", "Jg"}[idx]; // toString of DynamicGrid ", hc: "
            dpHotseatBarHeightPx = new String[]{"hotseatBarHeightPx", "zo", "BP", "Cu", "Ct", "DX", "CU", "CU", "Ir", "Iq", "Iv", "IB", "IM", "IM", "IL", "IL", "IL", "JW"}[idx]; // 4 * ...
            dpPageIndicatorHeightPx = new String[]{"pageIndicatorHeightPx", "zw", "BX", "CC", "CB", "Ef", "Da", "Da", "Ix", "Iw", "IB", "IH", "IS", "IS", "IR", "IR", "IR", "Kc"}[idx]; // last parameter in last localRect2.set(
            dpIconTextSize = new String[]{"allAppsIconTextSizePx", "zd", "BE", "Cj", "Ci", "DJ", "CG", "CG", "Id", "Ic", "Ih", "In", "Iy", "Iy", "Ix", "Ix", "Ix", "JI"}[idx]; // localPaint.setTextSize
            dpAllAppsIconSize = new String[]{"allAppsIconSizePx", "zc", "BD", "Ci", "Ch", "DN", "CK", "CK", "If", "Ie", "Ij", "Ip", "IA", "IA", "Iz", "Iz", "Iz", "JK"}[idx]; //  = "(paramInt + this.<field"
            dpIconDrawablePaddingPx = new String[]{"iconDrawablePaddingPx", "zZ", "BA", "Cf", "Ce", "DK", "CH", "CH", "Ie", "Id", "Ii", "Io", "Iz", "Iz", "Iy", "Iy", "Iy", "JJ"}[idx]; // in BubbleTextView setCompoundDrawablePadding
            dpFolderBackgroundOffset = new String[]{"folderBackgroundOffset", "zZ", "BA", "Cf", "Ce", "DK", "CH", "CH", "Ik", "Ij", "Io", "Iu", "IF", "IF", "IE", "IE", "IE", "JP"}[idx]; // in FolderIcon topMargin
            dpNumCols = new String[]{"numColumns", "yy", "AX", "BC", "BB", "Dh", "Cf", "Cf", "HA", "Hz", "HE", "HK", "HV", "HV", "HU", "HU", "HU", "Jf"}[idx]; // ", c: "
            dpFolderIconSize = new String[]{"folderIconSizePx", "zi", "BJ", "Co", "Cn", "DS", "CP", "CP", "Il", "Ik", "Ip", "Iv", "IG", "IG", "IF", "IF", "IF", "JQ"}[idx]; // " + 2 * -"
            cllpCanReorder = new String[]{"canReorder", "wf", "yE", "zj", "zi", "Bu", "Ar", "Ar", "FL", "FK", "FP", "FV", "Gg", "Gg", "Gf", "Gf", "Gf", "Hq"}[idx]; // second member with "= true"
            clIsHotseat = new String[]{"mIsHotseat", "vq", "xP", "yu", "yt", "AF", "zC", "zC", "EX", "EW", "Fb", "Fh", "Fs", "Fs", "Fr", "Fr", "Fr", "GC"}[idx];
            clShortcutsAndWidgets = new String[]{"mShortcutsAndWidgets", "vp", "xO", "yt", "ys", "AE", "zB", "zB", "EW", "EV", "Fa", "Fg", "Fr", "Fr", "Fq", "Fq", "Fq", "GB"}[idx];
            sawIsHotseat = new String[]{"mIsHotseatLayout", "Ng", "PQ", "Qr", "Qu", "SP", "Wx", "Wx", "XW", "XV", "XZ", "Yo", "YB", "YB", "YA", "YA", "YB", "ZD"}[idx];
            sdtbIsSearchBarHidden = new String[]{"mIsSearchBarHidden", "MV", "PF", "Qg", "Qj", "SE", "Wn", "Wn", "XL", "XK", "XO", "Yd", "Yq", "Yq", "Yp", "Yp", "Yq", "Zs"}[idx]; // under ValueAnimator
            sdtbQsbBar = new String[]{"mQSBSearchBar", "MW", "PG", "Qh", "Qk", "SF", "Wo", "Wo", "XM", "XL", "XP", "Ye", "Yr", "Yr", "Yq", "Yq", "Yr", "Zt"}[idx]; // under sdtbIsSearchBarHidden
            wCustomContentShowing = new String[]{"mCustomContentShowing", "PV", "SH", "Ti", "Ti", "VF", "Zo", "Zo", "aaV", "aaU", "aaY", "abn", "abA", "abA", "abz", "abz", "abx", "acz"}[idx]; // "() == 0) || (!this.<fieldName>"
            wState = new String[]{"mState", "Qj", "SV", "Tw", "Tw", "VT", "ZC", "ZC", "abk", "abj", "abn", "abC", "abP", "abP", "abO", "abO", "abM", "acO"}[idx]; // WorkspaceState member
            wDefaultPage = new String[]{"mDefaultPage", "PI", "Su", "SV", "SV", "Vs", "Zb", "Zb", "aaI", "aaH", "aaL", "aba", "abn", "abn", "abm", "abm", "abk", "acm"}[idx];  // "Expected custom content screen to exist", member gets decreased by one // "(-1 + this."
            wTouchState = new String[]{"mTouchState", "KY", "NF", "Oj", "On", "Qt", "TV", "TV", "Vy", "Vx", "VB", "VQ", "Wd", "Wd", "Wc", "Wc", "Wd", "Xf"}[idx]; // onInterceptTouchEvent between continue
            wIsSwitchingState = new String[]{"mIsSwitchingState", "Qk", "SW", "Tx", "Tx", "VU", "ZD", "ZD", "abl", "abk", "abo", "abD", "abQ", "abQ", "abP", "abP", "abN", "acP"}[idx]; // "return (!this.<fieldName>"
            wLastCustomContentScrollProgress = new String[]{"mLastCustomContentScrollProgress", "PW", "SI", "Tj", "Tj", "VG", "Zp", "Zp", "aaW", "aaV", "aaZ", "abo", "abB", "abB", "abA", "abA", "aby", "acA"}[idx]; // " = -1.0F;"
            lHotseat = new String[]{"mHotseat", "EO", "Hu", "HZ", "Id", "Ka", "IP", "IP", "Om", "Ol", "Oq", "Ow", "OI", "OI", "OH", "OH", "OH", "PS"}[idx];
            lSearchDropTargetBar = new String[]{"mSearchDropTargetBar", "um", "wN", "xs", "xr", "zD", "yz", "yz", "DV", "DU", "DZ", "Ef", "Eq", "Eq", "Ep", "Ep", "Ep", "FA"}[idx];
            lAppsCustomizeTabHost = new String[]{"mAppsCustomizeTabHost", "ER", "Hx", "Ic", "Ig", "Kd", "IS", "IS", "Op", "Oo", "Ot", "Oz", "OL", "OL", "OK", "OK", "OK", "PV"}[idx];
            lAppsCustomizePagedView = new String[]{"mAppsCustomizeContent", "ES", "Hy", "Id", "Ih", "Ke", "LE", "LE", "Oq", "Op", "Ou", "OA", "OM", "OM", "OL", "OL", "OL", "PW"}[idx]; // AppsCustomizePagedView in Launcher
            lIconCache = new String[]{"mIconCache", "rF", "uf", "uK", "uK", "xn", "wf", "wf", "Br", "Bq", "Bv", "Bz", "BK", "BK", "BJ", "BJ", "BJ", "CW"}[idx]; // ".flush();"
            lState = new String[]{"mState", "Et", "GZ", "HE", "HH", "JF", "Is", "Is", "NO", "NN", "NS", "NY", "Ok", "Ok", "Oj", "Oj", "Oj", "Pu"}[idx]; // onNewIntent - "if ((i != 0) && (this."
            lHasFocus = new String[]{"mHasFocus", "Fj", "HP", "It", "Ix", "Ku", "NP", "NP", "OH", "OG", "OM", "OS", "Pd", "Pd", "Pc", "Pc", "Pc", "Qn"}[idx]; // onWindowFocusChanged
            lPaused = new String[]{"mPaused", "EZ", "HF", "Ik", "Io", "Kl", "NG", "NG", "Ox", "Ow", "OM", "OH", "OT", "OT", "OS", "OS", "OS", "Qd"}[idx]; // only boolean assignement in onPause()
            btvShadowsEnabled = new String[]{"mShadowsEnabled", "ue", "wF", "xk", "xj", "zv", "yr", "yr", "DP", "DO", "DT", "DZ", "Ek", "Ek", "Ej", "Ej", "Ej", "Fu"}[idx]; // private final boolean
            fiPreviewBackground = new String[]{"mPreviewBackground", "CE", "Fh", "FM", "FP", "HL", "GL", "GL", "Mh", "Mg", "Ml", "Mr", "MC", "MC", "MB", "MB", "MB", "NM"}[idx]; // FOLDERICON - only ImageView member
            fiFolderName = new String[]{"mFolderName", "CF", "Fi", "FN", "FQ", "HM", "GM", "GM", "Mi", "Mh", "Mm", "Ms", "MD", "MD", "MC", "MC", "MC", "NN"}[idx]; // FOLDERICON - only BubbleTextView
            fiFolder = new String[]{"mFolder", "CB", "Fe", "FJ", "FM", "HI", "GI", "GI", "Me", "Md", "Mi", "Mo", "Mz", "Mz", "My", "My", "My", "NJ"}[idx]; // FOLDERICON - only Folder member
            fiLongPressHelper = new String[]{"mLongPressHelper", "ui", "wJ", "xo", "xn", "zA", "yw", "yw", "DJ", "DI", "DN", "DT", "Ee", "Ee", "Ed", "Ed", "Ed", "Fo"}[idx]; // cancelLongPress
            fFolderInfo = new String[]{"mInfo", "BF", "Ej", "EO", "ER", "GM", "FM", "FM", "Lj", "Li", "Ln", "Lt", "LE", "LE", "LD", "LD", "LD", "MO"}[idx]; // <mInfo>.title))
            fiFolderInfo = fFolderInfo; // FolderIcon - same as fFolderInfo
            fiContents = new String[]{"contents", "Dt", "FW", "GB", "GE", "IA", "HA", "HA", "MW", "MV", "Na", "Ng", "Nr", "Nr", "Nq", "Nq", "Nq", "OB"}[idx]; // first ArrayList in FolderInfo
            fiOpened = new String[]{"opened", "Ds", "FV", "GA", "GD", "Iz", "Hz", "Hz", "MV", "MU", "MZ", "Nf", "Nq", "Nq", "Np", "Np", "Np", "OA"}[idx]; // only boolean member
            fFolderEditText = new String[]{"mFolderName", "Cf", "EJ", "Fo", "Fr", "Ho", "Go", "Go", "LL", "LK", "LP", "LV", "Mg", "Mg", "Mf", "Mf", "Mf", "Nq"}[idx]; // only FolderEditText member
            fMaxCountX = new String[]{"mMaxCountX", "BM", "Eq", "EV", "EY", "GV", "FV", "FV", "Ls", "Lr", "Lw", "LC", "LN", "LN", "LM", "LM", "LM", "MX"}[idx]; // Folder constructor, last line - maxNumItems = X * Y;
            fMaxCountY = new String[]{"mMaxCountY", "BN", "Er", "EW", "EZ", "GW", "FW", "FW", "Lt", "Ls", "Lx", "LD", "LO", "LO", "LN", "LN", "LN", "MY"}[idx]; // Folder constructor, last line - maxNumItems = X * Y;
            fMaxNumItems = new String[]{"mMaxNumItems", "BO", "Es", "EX", "Fa", "GX", "FX", "FX", "Lu", "Lt", "Ly", "LE", "LP", "LP", "LO", "LO", "LO", "MZ"}[idx]; // Folder constructor, last line - maxNumItems = X * Y;
            fFolderNameHeight = new String[]{"mFolderNameHeight", "Ca", "EE", "Fj", "Fm", "Hj", "Gj", "Gj", "LG", "LF", "LK", "LQ", "Mb", "Mb", "Ma", "Ma", "Ma", "Nl"}[idx]; // <field> = ...getMeasuredHeight | in onFinishInflate
            fContent = new String[]{"mContent", "BH", "El", "EQ", "ET", "GQ", "FQ", "FQ", "Ln", "Lm", "Lr", "Lx", "LI", "LI", "LH", "LH", "LH", "MS"}[idx]; // only CellLayout member
            pvIsPageMoving = new String[]{"mIsPageMoving", "Lv", "Oc", "OG", "OK", "QN", "Up", "Up", "VU", "VT", "VX", "Wm", "Wz", "Wz", "Wy", "Wy", "Wz", "XB"}[idx];  // beneath ".cancel();" in Workspace
            pvNextPage = new String[]{"mNextPage", "KI", "Np", "NT", "NX", "Qd", "TF", "TF", "Vi", "Vh", "Vl", "VA", "VN", "VN", "VM", "VM", "VN", "WP"}[idx]; // 1. abortAnimation(); 2. <field> = -1
            pvPageIndicator = new String[]{"mPageIndicator", "Lz", "Og", "OK", "OO", "QR", "Uu", "Uu", "VZ", "VY", "Wc", "Wr", "WE", "WE", "WD", "WD", "WE", "XG"}[idx]; // setContentDescription
            pvCurrentPage = new String[]{"mCurrentPage", "KF", "Nm", "NQ", "NU", "Qa", "TC", "TC", "Vf", "Ve", "Vi", "Vx", "VK", "VK", "VJ", "VJ", "VK", "WM"}[idx]; // "if ((localView != null) && (i != this."
            pvOverscrollX = new String[]{"mOverScrollX", "Ln", "Nu", "Oy", "OC", "QF", "Uh", "Uh", "VM", "VL", "VP", "We", "Wr", "Wr", "Wq", "Wq", "Wr", "Xt"}[idx]; // " < 0);"
            aiComponentName = new String[]{"componentName", "rJ", "uj", "uO", "uO", "xr", "wj", "wj", "Bv", "Bu", "Bz", "BD", "BO", "BO", "BN", "BN", "BN", "Da"}[idx]; // only ComponentName member
            siIcon = new String[]{"mIcon", "", "", "", "", "", "", "", "Xr", "Xq", "Xu", "XJ", "XW", "XW", "XV", "XV", "XW", "YY"}[idx]; // only Bitmap member
            iiItemType = new String[]{"itemType", "En", "GT", "Hy", "HB", "Jz", "Im", "Im", "NI", "NH", "NM", "NS", "Oe", "Oe", "Od", "Od", "Od", "Po"}[idx]; // Item(id=
            iiID = new String[]{"id", "id", "id", "id", "id", "id", "id", "id", "id", "id", "id", "id", "id", "id", "id", "id", "id", "id", "id"}[idx];
            iiScreenId = new String[]{"screenId", "vO", "yn", "yS", "yR", "Bd", "Aa", "Aa", "Ft", "Fs", "Fx", "FD", "FO", "FO", "FN", "FN", "FN", "GY"}[idx];
            iiContainer = new String[]{"container", "Eo", "GU", "Hz", "HC", "JA", "In", "In", "Fu", "Ft", "Fy", "FE", "FP", "FP", "FO", "FO", "FO", "GZ"}[idx];
            ceIcon = new String[]{"icon", "DZ", "GE", "Hj", "Hm", "Jj", "HW", "HW", "Ns", "Nr", "Nw", "NC", "NN", "NN", "NM", "NM", "NM", "OX"}[idx];
            lawiProviderName = new String[]{"providerName", "GX", "JF", "Kj", "Kn", "Mp", "PL", "PL", "Rb", "Ra", "Rf", "Rl", "Rw", "Rw", "Rv", "Rv", "Rv", "SG"}[idx]; // 1. AppWidget(id= 2. only ComponentName member
            acthInTransition = new String[]{"mInTransition", "tf", "vF", "wk", "wk", "yA", "xs", "xs", "CL", "CK", "CP", "CV", "Dg", "Dg", "Df", "Df", "Df", "Eq"}[idx]; // only boolean member
            acthContent = new String[]{"mContent", "tD", "wd", "wI", "wI", "yV", "xN", "xN", "Dg", "Df", "Dk", "Dq", "DB", "DB", "DA", "DA", "DA", "EL"}[idx]; // .getLayoutParams in setInsets
            acthAppsCustomizePane = new String[]{"mAppsCustomizePane", "tB", "wb", "wG", "wG", "", "", "", "", "", "", "", "", "", "", "", "", ""}[idx]; // setAlpha
            uIconWidth = new String[]{"sIconWidth", "NC", "Qm", "QN", "QS", "Tn", "WX", "WX", "Yy", "Yx", "YB", "YQ", "Zd", "Zd", "Zd", "Zd", "Zb", "aad"}[idx]; // first private static int
            uIconHeight = new String[]{"sIconHeight", "ND", "Qn", "QO", "QT", "To", "WY", "WY", "Yz", "Yy", "YC", "YR", "Ze", "Ze", "Zc", "Zc", "Za", "aac"}[idx]; // second private static int
            acpvAllAppsNumCols = new String[]{"allAppsNumCols", "zr", "BS", "Cx", "Cw", "DZ", "CW", "CW", "Iu", "It", "Iy", "IE", "IP", "IP", "IO", "IO", "IO", "JZ"}[idx]; // 1. (int paramInt1, int paramInt2) 2. localDeviceProfile
            acpvAllAppsNumRows = new String[]{"allAppsNumRows", "zq", "BR", "Cw", "Cv", "Ea", "CX", "CX", "It", "Is", "Ix", "ID", "IO", "IO", "IN", "IN", "IN", "JY"}[idx]; // 1. (int paramInt1, int paramInt2) 2. localDeviceProfile
            acpvAllApps = new String[]{"mApps", "sA", "va", "vF", "vF", "yi", "xa", "xa", "Cs", "Cr", "Cw", "CC", "CN", "CN", "CM", "CM", "CM", "DX"}[idx]; // sort
            acpvNumAppsPages = new String[]{"mNumAppsPages", "sN", "vn", "vS", "vS", "yp", "xh", "xh", "Cz", "Cy", "CD", "CJ", "CU", "CU", "CT", "CT", "CT", "Ee"}[idx]; // Math.ceil
            acpvCellCountX = new String[]{"mCellCountX", "Lg", "NN", "Or", "Ov", "Qx", "TZ", "TZ", "VE", "VD", "VH", "VW", "Wj", "Wj", "Wi", "Wi", "Wj", "Xl"}[idx]; // "(int)Math.ceil(this." - first
            acpvCellCountY = new String[]{"mCellCountY", "Lh", "NO", "Os", "Ow", "Qy", "Ua", "Ua", "VF", "VE", "VI", "VX", "Wk", "Wk", "Wj", "Wj", "Wk", "Xm"}[idx]; // "(int)Math.ceil(this." - second
            acpvContentType = new String[]{"mContentType", "sw", "uW", "vB", "vB", "yf", "wX", "wX", "Co", "Cn", "Cs", "Cy", "CJ", "CJ", "CI", "CI", "CI", "DT"}[idx]; // private oo uW = oo.vW;
            acpvContentHeight = new String[]{"mContentHeight", "sH", "vh", "vM", "vM", "yl", "xd", "xd", "Cv", "Cu", "Cz", "CF", "CQ", "CQ", "CP", "CP", "CP", "Ea"}[idx]; // second View.MeasureSpec.makeMeasureSpec(this.
            acpvLayoutInflater = new String[]{"mLayoutInflater", "mLayoutInflater", "mLayoutInflater", "mLayoutInflater", "mLayoutInflater", "mLayoutInflater", "mLayoutInflater", "mLayoutInflater", "Cq", "Cp", "Cu", "CA", "CL", "CL", "CK", "CK", "CK", "DV"}[idx]; // only LayoutInflater member
            acclFocusHandlerView = new String[]{"mFocusHandlerView", "", "", "", "KV", "yd", "wV", "wV", "Cm", "Cl", "Cq", "Cw", "CH", "CH", "CG", "CG", "CG", "DR"}[idx]; // localBubbleTextView.setOnFocusChangeListener

            if (Common.IS_L_TREBUCHET) {
                btvShadowsEnabled = "mCustomShadowsEnabled";
            }

			clphHasPerformedLongPress = new String[]{"mHasPerformedLongPress", "wG", "zf", "zK", "zJ", "BV", "AS", "AS", "Gm", "Gl", "Gq", "Gw", "GH", "GH", "GG", "GG", "GG", "HR"}[idx]; // only boolean member
			lmWorkspaceItems = new String[]{"sBgWorkspaceItems", "HG", "Ko", "KS", "KW", "MX", "QA", "QA", "RS", "RR", "RW", "Sl", "Sw", "Sw", "Sv", "Sv", "Sw", "Ty"}[idx]; // "adding item: " in case 1 <field>.add
            lmFolders = new String[]{"sBgFolders", "HF", "Kn", "KR", "KV", "MW", "Qz", "Qz", "RR", "RQ", "RV", "Sk", "Sv", "Sv", "Su", "Su", "Sv", "Tx"}[idx]; // 1. ", not in the list of folders" 2. <field>.get(Long.valueOf(paramLong));

            if (Common.GNL_VERSION >= ObfuscationHelper.GNL_5_3_23) {
                btvShadowsEnabled = "mCustomShadowsEnabled";
                sdtbQsbBar = "mQSB";
            }

            if (Common.GNL_VERSION == ObfuscationHelper.GNL_5_3_26) {
                covbFields = new String[]{"tR", "ceg", "ceh", "cei"};
                spbMic = "cgY";
            }
        }
    }
}