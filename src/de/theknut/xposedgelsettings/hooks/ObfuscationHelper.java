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

    public static int getVersionIndex(int version) {

        if (Common.HOOKED_PACKAGE.equals(Common.GEL_PACKAGE)) {
            if (version >= GNL_3_6_16 && version < GNL_3_9_00) {
                return 3;
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
                START_SETTINGS_ONCLICK,
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
                ITEM_CONFIGURATION,
                LAUNCHER_APPWIDGET_INFO,
                DRAG_LAYER,
                LAUNCHER_APP_WIDGET_HOST_VIEW,
                BUBBLE_TEXT_VIEW,
                USER_HANDLE,
                ADB,
                GEL,
                NOW_OVERLAY,
                SEARCH_OVERLAY_IMPL,
                GSA_CONFIG_FLAGS,
                RECOGNIZER_VIEW,
                SEARCH_PLATE,
                GEL_SEARCH_PLATE_CONTAINER,
                TRANSITIONS_MANAGER,
                WEATHER_ENTRY_ADAPTER,
                URI_LOADER,
                WEATHER_POINT,
                LAUNCHER_APP_STATE;

        public static void initNames(int idx) {

            String launcherPackage = "com.android.launcher3.";
            if (Common.HOOKED_PACKAGE.equals("com.android.launcher2")) {
                launcherPackage = "com.android.launcher2.";
            }

            LAUNCHER = new String[]{launcherPackage + "Launcher", launcherPackage + "Launcher", launcherPackage + "Launcher", launcherPackage + "Launcher", launcherPackage + "Launcher", launcherPackage + "Launcher", launcherPackage + "Launcher", launcherPackage + "Launcher"}[idx];
            WORKSPACE = new String[]{launcherPackage + "Workspace", launcherPackage + "Workspace", launcherPackage + "Workspace", launcherPackage + "Workspace", launcherPackage + "Workspace", launcherPackage + "Workspace", launcherPackage + "Workspace", launcherPackage + "Workspace"}[idx];
            CELL_LAYOUT = new String[]{launcherPackage + "CellLayout", launcherPackage + "CellLayout", launcherPackage + "CellLayout", launcherPackage + "CellLayout", launcherPackage + "CellLayout", launcherPackage + "CellLayout", launcherPackage + "CellLayout", launcherPackage + "CellLayout"}[idx];
            CELL_LAYOUT_LAYOUT_PARAMS = new String[]{CELL_LAYOUT + "$LayoutParams", CELL_LAYOUT + "$LayoutParams", CELL_LAYOUT + "$LayoutParams", CELL_LAYOUT + "$LayoutParams", CELL_LAYOUT + "$LayoutParams", CELL_LAYOUT + "$LayoutParams", CELL_LAYOUT + "$LayoutParams", CELL_LAYOUT + "$LayoutParams"}[idx];
            PAGED_VIEW = new String[]{launcherPackage + "PagedView", launcherPackage + "PagedView", launcherPackage + "PagedView", launcherPackage + "PagedView", launcherPackage + "PagedView", launcherPackage + "PagedView", launcherPackage + "PagedView", launcherPackage + "PagedView"}[idx];
            PAGED_VIEW_ICON = new String[]{launcherPackage + "PagedViewIcon", launcherPackage + "PagedViewIcon", launcherPackage + "PagedViewIcon", launcherPackage + "PagedViewIcon", launcherPackage + "PagedViewIcon", launcherPackage + "PagedViewIcon", "", "", ""}[idx];
            APPS_CUSTOMIZE_LAYOUT = new String[]{launcherPackage + "AppsCustomizeLayout", launcherPackage + "AppsCustomizeLayout", launcherPackage + "AppsCustomizeLayout", launcherPackage + "AppsCustomizeLayout", launcherPackage + "AppsCustomizeLayout", launcherPackage + "AppsCustomizeLayout", launcherPackage + "AppsCustomizeLayout", launcherPackage + "AppsCustomizeLayout"}[idx]; // Trebuchet only
            APPS_CUSTOMIZE_PAGED_VIEW = new String[]{launcherPackage + "AppsCustomizePagedView", launcherPackage + "AppsCustomizePagedView", launcherPackage + "AppsCustomizePagedView", launcherPackage + "AppsCustomizePagedView", launcherPackage + "AppsCustomizePagedView", launcherPackage + "AppsCustomizePagedView", launcherPackage + "AppsCustomizePagedView", launcherPackage + "AppsCustomizePagedView"}[idx];
            APPS_CUSTOMIZE_TAB_HOST = new String[]{launcherPackage + "AppsCustomizeTabHost", launcherPackage + "AppsCustomizeTabHost", launcherPackage + "AppsCustomizeTabHost", launcherPackage + "AppsCustomizeTabHost", launcherPackage + "AppsCustomizeTabHost", launcherPackage + "AppsCustomizeTabHost", launcherPackage + "AppsCustomizeTabHost", launcherPackage + "AppsCustomizeTabHost"}[idx];
            SEARCH_DROP_TARGET_BAR = new String[]{launcherPackage + "SearchDropTargetBar", launcherPackage + "SearchDropTargetBar", launcherPackage + "SearchDropTargetBar", launcherPackage + "SearchDropTargetBar", launcherPackage + "SearchDropTargetBar", launcherPackage + "SearchDropTargetBar", launcherPackage + "SearchDropTargetBar", launcherPackage + "SearchDropTargetBar"}[idx];
            DRAG_LAYER = new String[]{launcherPackage + "DragLayer", launcherPackage + "DragLayer", launcherPackage + "DragLayer", launcherPackage + "DragLayer", launcherPackage + "DragLayer", launcherPackage + "DragLayer", launcherPackage + "DragLayer", launcherPackage + "DragLayer"}[idx];
            FOLDER = new String[]{launcherPackage + "Folder", launcherPackage + "Folder", launcherPackage + "Folder", launcherPackage + "Folder", launcherPackage + "Folder", launcherPackage + "Folder", launcherPackage + "Folder", launcherPackage + "Folder"}[idx];
            FOLDER_ICON = new String[]{launcherPackage + "FolderIcon", launcherPackage + "FolderIcon", launcherPackage + "FolderIcon", launcherPackage + "FolderIcon", launcherPackage + "FolderIcon", launcherPackage + "FolderIcon", launcherPackage + "FolderIcon", launcherPackage + "FolderIcon"}[idx];
            HOTSEAT = new String[]{launcherPackage + "Hotseat", launcherPackage + "Hotseat", launcherPackage + "Hotseat", launcherPackage + "Hotseat", launcherPackage + "Hotseat", launcherPackage + "Hotseat", launcherPackage + "Hotseat", launcherPackage + "Hotseat"}[idx];
            BUBBLE_TEXT_VIEW = new String[]{launcherPackage + "BubbleTextView", launcherPackage + "BubbleTextView", launcherPackage + "BubbleTextView", launcherPackage + "BubbleTextView", launcherPackage + "BubbleTextView", launcherPackage + "BubbleTextView", launcherPackage + "BubbleTextView", launcherPackage + "BubbleTextView"}[idx];
            GEL = new String[]{"com.google.android.launcher.GEL", "com.google.android.launcher.GEL", "com.google.android.launcher.GEL", "com.google.android.launcher.GEL", "com.google.android.launcher.GEL", "com.google.android.launcher.GEL", "com.google.android.launcher.GEL", "com.google.android.launcher.GEL"}[idx];
            RECOGNIZER_VIEW = new String[]{"com.google.android.search.shared.ui.RecognizerView", "com.google.android.search.searchplate.RecognizerView", "com.google.android.search.searchplate.RecognizerView", "com.google.android.search.searchplate.RecognizerView", "com.google.android.search.searchplate.RecognizerView", "com.google.android.search.searchplate.RecognizerView", "com.google.android.search.searchplate.RecognizerView", "com.google.android.search.searchplate.RecognizerView"}[idx];
            SEARCH_PLATE = new String[]{"com.google.android.search.shared.ui.SearchPlate", "com.google.android.search.searchplate.SearchPlate", "com.google.android.search.searchplate.SearchPlate", "com.google.android.search.searchplate.SearchPlate", "com.google.android.search.searchplate.SearchPlate", "com.google.android.search.searchplate.SearchPlate", "com.google.android.search.searchplate.SearchPlate", "com.google.android.search.searchplate.SearchPlate"}[idx];
            GEL_SEARCH_PLATE_CONTAINER = new String[]{"com.google.android.search.gel.GelSearchPlateContainer", "com.google.android.search.gel.GelSearchPlateContainer", "com.google.android.search.gel.GelSearchPlateContainer", "com.google.android.search.gel.GelSearchPlateContainer", "com.google.android.search.gel.GelSearchPlateContainer", "", "", ""}[idx];
            WORKSPACE_STATE = new String[]{WORKSPACE + "$State", "zc", "aco", "adq", "adr", "ags", "aig", "aig"}[idx]; // member initialization from Enum
            DEVICE_PROFILE = new String[]{launcherPackage + "DeviceProfile", "mz", "qi", "rj", "rj", "tu", "vg", "vg"}[idx]; // All Device Profiles must have
            DYNAMIC_GRID = new String[]{launcherPackage + "DynamicGrid", "nw", "rf", "sg", "sg", "ur", "wd", "wd"}[idx]; // --------
            CELL_LAYOUT_CELL_INFO = new String[]{CELL_LAYOUT + "$CellInfo", "lz", "pi", "qj", "qj", "sy", "ue", "ue"}[idx]; // Cell[view=
            PAGED_VIEW_CELL_LAYOUT = new String[]{launcherPackage + "PagedViewCellLayout", "vd", "yo", "zq", "zq", "acl", "adx", "adx"}[idx]; // "CellLayout cannot have UNSPECIFIED dimensions" the one with more members
            PAGED_VIEW_WITH_DRAGGABLE_ITEMS = new String[]{launcherPackage + "PagedViewWithDraggableItems", "vl", "yw", "zy", "zy", "acr", "aed", "aed"}[idx]; // AppsCustomizePagedView extends
            APPS_CUSTOMIZE_CELL_LAYOUT = new String[]{launcherPackage + "AppsCustomizeCellLayout", "kw", "yr", "zt", "zt", "rw", "tc", "tc"}[idx]; // "getContext().getResources().getDrawable" new <>(localContext) - the one above
            APPS_CUSTOMIZE_CONTENT_TYPE = new String[]{APPS_CUSTOMIZE_PAGED_VIEW + "$ContentType", "lf", "oo", "pp", "pp", "se", "tk", "tk"}[idx];
            WALLPAPER_OFFSET_INTERPOLATOR = new String[]{WORKSPACE + "$WallpaperOffsetInterpolator", "zd", "acp", "adr", "ads", "agt", "aih", "aih"}[idx]; // Error updating wallpaper offset
            START_SETTINGS_ONCLICK = new String[]{ "", "pu", "td", "ue", "ue", "wt", "xz", "xz"}[idx]; // in onCreate: "View localView3 = findViewById("
            DRAG_SOURCE = new String[]{launcherPackage + "DragSource", "nn", "qw", "rx", "rx", "ui", "vu", "vu"}[idx]; // in SearchDropTargetBar: ", Object paramObject)"
            ITEM_INFO = new String[]{launcherPackage + "ItemInfo", "pr", "ta", "ub", "ub", "wq", "xx", "xx"}[idx]; // Item(id=
            APP_INFO = new String[]{launcherPackage + "AppInfo", "kr", "ob", "pc", "pc", "rr", "sx", "sx"}[idx]; // ApplicationInfo
            SHORTCUT_INFO = new String[]{launcherPackage + "ShortcutInfo", "vz", "zl", "aan", "aan", "ade", "aeq", "aeq"}[idx]; // ShortcutInfo(title=
            ICON_CACHE = new String[]{launcherPackage + "IconCache", "pk", "ss", "tt", "tt", "wi", "xo", "xo"}[idx]; // Launcher.IconCache
            UTILITIES = new String[]{launcherPackage + "Utilities", "wi", "zu", "aaw", "aaw", "adm", "aez", "aez"}[idx]; // Launcher.Utilities
            CACHE_ENTRY = new String[]{ICON_CACHE + "$CacheEntry", "pl", "st", "tu", "tu", "wj", "xp", "xp"}[idx]; // new HashMap(50)
            LAUNCHER_MODEL = new String[]{launcherPackage + "LauncherModel", "sg", "vq", "ws", "ws", "zh", "aat", "aat"}[idx]; // Error: ItemInfo passed to checkItemInfo doesn't match original
            LOADER_TASK = new String[]{LAUNCHER_MODEL + "$LoaderTask", "tb", "wl", "xn", "xn", "aae", "abq", "abq"}[idx]; // Should not call runBindSynchronousPage
            FOLDER_INFO = new String[]{launcherPackage + "FolderInfo", "oz", "sh", "ti", "ti", "vy", "xj", "xj"}[idx]; // FolderInfo(id=
            LAUNCHER_APP_STATE = new String[]{launcherPackage + "LauncherAppState", "rr", "vb", "wd", "wd", "yt", "aad", "aad"}[idx]; // "LauncherAppState inited"
            APP_WIDGET_RESIZE_FRAME = new String[]{launcherPackage + "AppWidgetResizeFrame", "ks", "oc", "pd", "pd", "rs", "sy", "sy"}[idx]; // in AppsCustomizePagedView first line below "if (i >= 17)"
            ITEM_CONFIGURATION = new String[]{CELL_LAYOUT + "$ItemConfiguration", "ma", "pj", "qk", "qk", "sz", "uf", "uf"}[idx]; // in CellLayout Math.abs(paramArrayOfInt[0]) last interface parameter
            LAUNCHER_APPWIDGET_INFO = new String[]{launcherPackage + "LauncherAppWidgetInfo", "rv", "vf", "wh", "wh", "yx", "aah", "aah"}[idx]; // AppWidget(id=
            LAUNCHER_APP_WIDGET_HOST_VIEW = new String[]{launcherPackage + "LauncherAppWidgetHostView", "ru", "ve", "wg", "wg", "yw", "aag", "aag"}[idx]; // in Workspace "getAppWidgetInfo"
            USER_HANDLE = new String[]{"", "", "adl", "aen", "aeo", "ahw", "ajm", "ajm"}[idx]; // last parameter in IconCache "cacheLocked"
            ADB = new String[]{"", "", "adb", "aed", "aee", "ahh", "aiw", "aiw"}[idx];
            NOW_OVERLAY = new String[]{"com.google.android.sidekick.shared.client.NowOverlay", "dzk", "enc", "evx", "evx", "fma", "gen", "gen"}[idx]; // now_overlay:views_hidden_for_search
            SEARCH_OVERLAY_IMPL = new String[]{"com.google.android.search.gel.SearchOverlayImpl", "ccu", "cmh", "cuc", "cuc", "ebj", "erb", "erb"}[idx]; // search_overlay_impl:search_box_stats
            GSA_CONFIG_FLAGS = new String[]{"com.google.android.search.core.GsaConfigFlags", "ayc", "bgr", "bnj", "bnj", "chh", "cug", "cug"}[idx]; // "int array"
            TRANSITIONS_MANAGER = new String[]{"com.google.android.search.shared.ui.SearchPlate$TransitionsManager", "cen", "cog", "cwb", "cwb", "dsi", "egu", "egu"}[idx]; // in SearchPlate: "(this, this);"
            WEATHER_ENTRY_ADAPTER = new String[]{"com.google.android.sidekick.shared.cards.WeatherEntryAdapter", "dye", "elt", "euo", "euo", "fzq", "gtm", "gtm"}[idx]; // empty text -> "  "
            URI_LOADER = new String[]{"com.google.android.shared.util.UriLoader", "cxw", "eno", "dtb", "dtb", "", "", ""}[idx];
            WEATHER_POINT = new String[]{"com.google.geo.sidekick.Sidekick.WeatherEntry.WeatherPoint", "him", "ich", "ilp", "ilp", "aps", "aps", "ara", "ara"}[idx]; // getLocation in WeatherEntryAdapter // since GS 4.0 it's not the same class anymore but it does the same
        }
    }

    public static class Classes {

        static public Class<?> Launcher,
                Workspace,
                AppInfo,
                AppsCustomizePagedView,
                CellLayout,
                WallpaperOffsetInterpolator,
                PagedViewIcon,
                DeviceProfile,
                AppsCustomizeLayout,
                AppsCustomizeTabHost,
                Folder,
                FolderIcon,
                PagedViewWithDraggableItems,
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
                Adb,
                LauncherAppWidgetInfo,
                DragLayer,
                LauncherAppWidgetHostView,
                GSAConfigFlags,
                RecognizerView,
                SearchPlate,
                GelSearchPlateContainer,
                TransitionsManager,
                BubbleTextView,
                LauncherAppState,
                DynamicGrid,
                AppsCustomizeCellLayout,
                WeatherEntryAdapter,
                WeatherPoint,
                UriLoader;

        public static void hookAllClasses(LoadPackageParam lpparam) {
            Launcher = findClass(ClassNames.LAUNCHER, lpparam.classLoader);
            Workspace = findClass(ClassNames.WORKSPACE, lpparam.classLoader);
            AppsCustomizePagedView = findClass(ClassNames.APPS_CUSTOMIZE_PAGED_VIEW, lpparam.classLoader);
            AppsCustomizeCellLayout = findClass(ClassNames.APPS_CUSTOMIZE_CELL_LAYOUT, lpparam.classLoader);
            CellLayout = findClass(ClassNames.CELL_LAYOUT, lpparam.classLoader);
            CellLayoutLayoutParams = findClass(ClassNames.CELL_LAYOUT_LAYOUT_PARAMS, lpparam.classLoader);
            WallpaperOffsetInterpolator = findClass(ClassNames.WALLPAPER_OFFSET_INTERPOLATOR, lpparam.classLoader);
            PagedView = findClass(ClassNames.PAGED_VIEW, lpparam.classLoader);
            DeviceProfile = findClass(ClassNames.DEVICE_PROFILE, lpparam.classLoader);
            DynamicGrid = findClass(ClassNames.DYNAMIC_GRID, lpparam.classLoader);
            AppInfo = findClass(ClassNames.APP_INFO, lpparam.classLoader);

            if (Common.IS_TREBUCHET) {
                AppsCustomizeLayout = findClass(ClassNames.APPS_CUSTOMIZE_LAYOUT, lpparam.classLoader);
            } else {
                AppsCustomizeTabHost = findClass(ClassNames.APPS_CUSTOMIZE_TAB_HOST, lpparam.classLoader);
            }

            CellLayoutCellInfo = findClass(ClassNames.CELL_LAYOUT_CELL_INFO, lpparam.classLoader);
            AppsCustomizeContentType = findClass(ClassNames.APPS_CUSTOMIZE_CONTENT_TYPE, lpparam.classLoader);
            Folder = findClass(ClassNames.FOLDER, lpparam.classLoader);
            PagedViewWithDraggableItems = findClass(ClassNames.PAGED_VIEW_WITH_DRAGGABLE_ITEMS, lpparam.classLoader);

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
            ItemConfiguration = findClass(ClassNames.ITEM_CONFIGURATION, lpparam.classLoader);
            LauncherAppWidgetInfo = findClass(ClassNames.LAUNCHER_APPWIDGET_INFO, lpparam.classLoader);
            DragLayer = findClass(ClassNames.DRAG_LAYER, lpparam.classLoader);
            LauncherAppWidgetHostView = findClass(ClassNames.LAUNCHER_APP_WIDGET_HOST_VIEW, lpparam.classLoader);
            BubbleTextView = findClass(ClassNames.BUBBLE_TEXT_VIEW, lpparam.classLoader);
            LauncherAppState = findClass(ClassNames.LAUNCHER_APP_STATE, lpparam.classLoader);

            if (Common.IS_PRE_GNL_4) {
                // PagedViewIcon was removed in Google Search 4.0
                PagedViewIcon = findClass(ClassNames.PAGED_VIEW_ICON, lpparam.classLoader);
            }

            if (lpparam.packageName.equals(Common.GEL_PACKAGE)) {
                GELClass = findClass(ClassNames.GEL, lpparam.classLoader);
                NowOverlay = findClass(ClassNames.NOW_OVERLAY, lpparam.classLoader);
                SearchOverlayImpl = findClass(ClassNames.SEARCH_OVERLAY_IMPL, lpparam.classLoader);
                GSAConfigFlags = findClass(ClassNames.GSA_CONFIG_FLAGS, lpparam.classLoader);
                TransitionsManager = findClass(ClassNames.TRANSITIONS_MANAGER, lpparam.classLoader);
                RecognizerView = findClass(ClassNames.RECOGNIZER_VIEW, lpparam.classLoader);
                SearchPlate = findClass(ClassNames.SEARCH_PLATE, lpparam.classLoader);

                if (Common.PACKAGE_OBFUSCATED) {
                    WorkspaceState = findClass(ClassNames.WORKSPACE_STATE, lpparam.classLoader);

                    if (Common.GNL_VERSION >= GNL_4_0_26) {
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
                        Adb = findClass(ClassNames.ADB, lpparam.classLoader);
                    }

                    if (Common.GNL_VERSION >= ObfuscationHelper.GNL_3_4_15) {
                        StartSettingsOnClick = findClass(ClassNames.START_SETTINGS_ONCLICK, lpparam.classLoader);
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
                gsaShouldAlwaysShowHotwordHint,
                btvCreateGlowingOutline,
                lmDeleteItemFromDatabase,
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
                lCreateAppInfo,
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
                acpvRemoveAllViewsOnPage;
        public static String wGetChangeStateAnimation;

        public static void initMethodNames(int idx) {
            lOpenFolder = new String[]{"openFolder", "i", "i", "i", "i", "i", "i", "i"}[idx]; // "Opening folder ("
            lHideAppsCustomizeHelper = new String[]{"hideAppsCustomizeHelper", "a", "a", "a", "a", "a", "a", "a"}[idx];
            lShowWorkspace = new String[]{"showWorkspace", "a", "a", "a", "a", "a", "a", "a"}[idx]; // boolean paramBoolean, Runnable paramRunnable
            lCloseFolderWParam = new String[]{"closeFolder", "h", "h", "h", "h", "h", "h", "h"}[idx]; // if ((ViewGroup)paramFolder.getParent().getParent() != null)
            lBindAppsUpdated = new String[]{"bindAppsUpdated", "l", "l", "l", "l", "l", "l", "l"}[idx]; // "(this, paramArrayList), false));"
            lGetQsbBar = new String[]{"getQsbBar", "gw", "hl", "hu", "hv", "hM", "", ""}[idx]; // "public View "
            lHasCustomContentToLeft = new String[]{"hasCustomContentToLeft", "fL", "gA", "gJ", "gK", "hc", "ib", "ib"}[idx]; // "()) || (!"
            lIsAllAppsVisible = new String[]{"isAllAppsVisible", "gs", "hh", "hq", "hr", "hI", "iD", "iD"}[idx]; // onBackPressed second if clause method call
            lFinishBindingItems = new String[]{"finishBindingItems", "U", "Z", "Z", "Z", "ac", "aa", "aa"}[idx]; // hasFocus()
            lCloseFolder = new String[]{"closeFolder", "gr", "hg", "hp", "hq", "hH", "iC", "iC"}[idx]; // if (localFolder != null)
            lSetWorkspaceBackground = new String[]{"setWorkspaceBackground", "N", "S", "S", "S", "V", "W", "W"}[idx]; // localView.setBackground(localDrawable);
            lGetDragLayer = new String[]{"getDragLayer", "fV", "gK", "gT", "gU", "hn", "ik", "ik"}[idx]; // public final DragLayer
            lCreateAppInfo = new String[]{"", "e", "d", "d", "d", "d", "d", "d"}[idx]; // (Intent paramIntent)
            lDispatchOnLauncherTransitionStart = new String[]{"dispatchOnLauncherTransitionStart", "c", "c", "c", "c", "c", "c", "c"}[idx]; // (paramView, 0.0F);
            lDispatchOnLauncherTransitionEnd = new String[]{"dispatchOnLauncherTransitionEnd", "d", "d", "d", "d", "d", "d", "d"}[idx]; // (paramView, 1.0F);
            clAttemptPushInDirection = new String[]{"attemptPushInDirection", "b", "b", "b", "b", "b", "b", "b"}[idx]; // "if (Math.abs(paramArrayOfInt[0]) + Math.abs(paramArrayOfInt[1]) > 1)"
            clMarkCellsForView = new String[]{"markCellsForView", "a", "a", "a", "a", "a", "a", "a"}[idx]; // int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean[][] paramArrayOfBoolean, boolean paramBoolean
            clGetShortcutsAndWidgets = new String[]{"getShortcutsAndWidgets", "dH", "ew", "eF", "eF", "eZ", "ez", "ez"}[idx]; // "public final a"
            clAddViewToCellLayout = new String[]{"addViewToCellLayout", "a", "a", "a", "a", "a", "a", "a"}[idx]; // View paramView, int paramInt1, int paramInt2, CellLayout.LayoutParams paramLayoutParams, boolean paramBoolean
            clSetIsHotseat= new String[]{"setIsHotseat", "D", "G", "G", "G", "I", "J", "J"}[idx]; // two line in a row with " = true;"
            clGetChildrenScale= new String[]{"getChildrenScale", "dv", "ek", "et", "et", "eM", "em", "em"}[idx]; // paramView.setScaleX(<method>)
            wStartDrag = new String[]{"startDrag", "a", "a", "a", "a", "a", "a", "a"}[idx]; // isInTouchMode
            wMoveToDefaultScreen = new String[]{"moveToDefaultScreen", "ao", "at", "at", "at", "av", "at", "at"}[idx]; // Launcher onNewIntent method call of workspace member with (true)
            pvOverScroll = new String[]{"overScroll", "g", "g", "g", "g", "h", "h", "h"}[idx]; // (float paramFloat)
            wGetOpenFolder = new String[]{"getOpenFolder", "jp", "kj", "kn", "ks", "kK", "lV", "lV"}[idx]; // localDragLayer.getChildCount();
            wIsOnOrMovingToCustomContent = new String[]{"isOnOrMovingToCustomContent", "jJ", "kE", "kI", "kN", "le", "mp", "mp"}[idx]; // "() != null) && (this."
            wEnterOverviewMode = new String[]{"enterOverviewMode", "jO", "kJ", "kN", "kS", "lj", "mu", "mu"}[idx]; // "(true, -1, true);"
            wMoveToCustomContentScreen = new String[]{"moveToCustomContentScreen", "ap", "au", "au", "au", "aw", "au", "au"}[idx]; // "View localView = getChildAt(i);" with "-301L"
            wSnapToPage = new String[]{"snapToPage", "bc", "bs", "bt", "bv", "bA", "bz", "bz"}[idx]; // in PagedView requestChildFocus - last line of method
            wOnDragEnd = new String[]{"onDragEnd", "dt", "ei", "er", "er", "eK", "ek", "ek"}[idx]; // only method without interface parameters with InstallShortcutReceiver
            //wOnDragStart = new String[]{"onDragStart", "a", "a", "a"}[idx]; // only method with interface parameters with InstallShortcutReceiver
            wOnLauncherTransitionEnd = new String[]{"onLauncherTransitionEnd", "a", "a", "a", "a", "a", "a", "a"}[idx]; // (Launcher paramLauncher, boolean paramBoolean1, boolean paramBoolean2)
            wOnTransitionPrepare = new String[]{"onTransitionPrepare", "jR", "kM", "kR", "kW", "ln", "mx", "mx"}[idx]; // Method with "if ((bool) && (" in it
            wGetWorkspaceAndHotseatCellLayouts = new String[]{"getWorkspaceAndHotseatCellLayouts", "ka", "kV", "la", "lf", "lx", "mH", "mH"}[idx]; // localArrayList.add((CellLayout)getChildAt(j));
            wGetViewForTag = new String[]{"getViewForTag", "I", "V", "V", "V", "af", "ae", "ae"}[idx]; // "(this, paramObject));"
            wGetScreenWithId = new String[]{"getScreenWithId", "j", "j", "j", "j", "j", "j", "j"}[idx]; // public final CellLayout
            wGetFolderForTag = new String[]{"getFolderForTag", "H", "U", "U", "U", "ae", "ad", "ad"}[idx]; // "public final Folder"
            wUpdateStateForCustomContent = new String[]{"updateStateForCustomContent", "H", "av", "aL", "aL", "aU", "aU", "aU"}[idx]; // Math.abs(Math.max(Math.min
            wGetChangeStateAnimation = new String[]{"getChangeStateAnimation", "", "", "", "", "b", "b", "b"}[idx]; // (float paramFloat, boolean paramBoolean)
            pvPageBeginMoving = new String[]{"pageBeginMoving", "ii", "iY", "jb", "jc", "jn", "kw", "kw"}[idx]; // above "awakenScrollBars"
            pvPageEndMoving = new String[]{"pageEndMoving", "ij", "iZ", "jc", "jd", "jJ", "kU", "kU"}[idx]; // method above "accessibility"
            pvSnapToPage = new String[]{"snapToPage", "a", "a", "a", "a", "a", "a", "a"}[idx]; // int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean, TimeInterpolator paramTimeInterpolator
            pvGetPageAt = new String[]{"getPageAt", "at", "aJ", "aJ", "aJ", "aS", "aS", "aS"}[idx]; // return getChildAt(paramInt);
            pviApplyFromApplicationInfo = new String[]{"applyFromApplicationInfo", "a", "a", "a", "a", "a", "a", "a"}[idx]; // only pre GNL 4
            pvSetCurrentPage = new String[]{"setCurrentPage", "aV", "bl", "bm", "bo", "bt", "bs", "bs"}[idx]; // "if (getChildCount() == 0)"
            pvIsLayoutRtl = new String[]{"isLayoutRtl", "hX", "iN", "iQ", "iR", "jc", "kl", "kl"}[idx]; // "getLayoutDirection() == 1"
            sdtbOnDragStart = new String[]{"onDragStart", "a", "a", "a", "a", "a", "a", "a"}[idx]; // twice .start in the method
            sdtbOnDragEnd = new String[]{"onDragEnd", "dt", "ei", "er", "er", "eK", "ek", "ek"}[idx]; // twice .reverse
            btvCreateGlowingOutline = new String[]{"createGlowingOutline", "a", "a", "a", "a", "", "", ""}[idx]; // setBitmap
            btvApplyFromApplicationInfo = new String[]{"applyFromApplicationInfo", "", "", "", "", "b", "b", "b"}[idx];
            btvApplyFromShortcutInfo = new String[]{"applyFromShortcutInfo", "a", "a", "a", "a", "a", "a", "a"}[idx]; // Bitmap localBitmap = param
            acpvOnPackagesUpdated = new String[]{"onPackagesUpdated", "a", "a", "a", "a", "a", "a", "a"}[idx]; // "can not fit on this device"
            //acpvOverScroll = new String[]{"overScroll", "g", "g", "g", "g"}[idx]; // (float paramFloat)
            acpvSetApps = new String[]{"setApps", "b", "b", "b", "b", "b", "b", "b"}[idx]; // Collections.sort
            acpvUpdateApps = new String[]{"updateApps", "g", "g", "g", "g", "g", "g", "g"}[idx]; // in Launcher: Last method call in method with "(this, paramArrayList), false));"
            acpvRemoveApps = new String[]{"removeApps", "f", "f", "f", "f", "f", "f", "f"}[idx]; // in Launcher: Last method call in method with "if (!paramArrayList1.isEmpty())"
            acpvEnableHwLayersOnVisiblePages = new String[]{"enableHwLayersOnVisiblePages", "db", "dQ", "dZ", "dZ", "et", "dU", "dU"}[idx]; // "localArrayList2.add(" between break; and return;
            //acpvGetTabHost = new String[]{"getTabHost", "de", "dt", "ec"}[idx];
            acpvSyncAppsPageItems = new String[]{"syncAppsPageItems", "aq", "aG", "aG", "aG", "aP", "aP", "aP"}[idx]; // int k = Math.min(i + j, this.
            acpvSetContentType = new String[]{"setContentType", "a", "a", "a", "a", "a", "a", "a"}[idx];
            acpvInvalidatePageData = new String[]{"invalidatePageData", "j", "k", "k", "k", "k", "j", "j"}[idx]; // method for "(i, true);"
            acpvSyncPages = new String[]{"syncPages", "da", "dP", "dY", "dY", "es", "dT", "dT"}[idx]; // removeAllViews
            acpvBeginDragging = new String[]{"beginDragging", "n", "n", "n", "n", "G", "E", "E"}[idx]; // postDelayed
            acpvUpdatePageCounts = new String[]{"updatePageCounts", "cO", "dD", "dM", "dM", "eg", "dG", "dG"}[idx]; // (int)Math.ceil
            acpvSetAllAppsPadding = new String[]{"setAllAppsPadding", "b", "b", "b", "b", "", "", ""}[idx]; // .set(paramRect);
            acpvRemoveAllViewsOnPage = new String[]{"removeAllViewsOnPage", "cI", "dx", "dG", "dG", "eb", "dC", "dC"}[idx]; // ")localView)."
            acthOnTabChanged = new String[]{"onTabChanged", "c", "c", "c", "c", "", "", ""}[idx]; // setBackgroundColor
            acthSetInsets = new String[]{"setInsets", "c", "c", "c", "c", "b", "b", "b"}[idx]; // (Rect
            acthGetContentTypeForTabTag = new String[]{"getContentTypeForTabTag", "j", "r", "r", "q", "q", "p", "p"}[idx]; // (String paramString)
            acthSetContentTypeImmediate = new String[]{"setContentTypeImmediate", "b", "b", "b", "b", "", "", ""}[idx]; // setOnTabChangedListener(null)
            dpGetWorkspacePadding = new String[]{"getWorkspacePadding", "aC", "aS", "aS", "aS", "ba", "ba", "ba"}[idx]; // Rect localRect2 = new Rect();
            dpUpdateFromConfiguration = new String[]{"updateFromConfiguration", "a", "a", "a", "a", "a", "a", "a"}[idx]; // float paramFloat, int paramInt, Resources paramResources, DisplayMetrics paramDisplayMetrics
            dgGetDeviceProfile = new String[]{"getDeviceProfile", "eV", "fK", "fT", "fT", "gl", "fM", "fM"}[idx]; // public final
            //fOnRemove = new String[]{"onRemove", "g", "g", "g"}[idx]; // removeView(localView)
            //fOnAdd = new String[]{"onAdd", "f", "f", "f"}[idx]; // (1 + getItemCount()); - first line  = new String[]true
            //fReplaceFolderWithFinalItem = new String[]{"replaceFolderWithFinalItem", "ge", "ge", "gn"}[idx]; // if (localView != new String[]null)
            fGetItemsInReadingOrder = new String[]{"getItemsInReadingOrder", "fr", "gh", "gq", "gr", "gJ", "hJ", "hJ"}[idx]; // public final ArrayList
            fBind = new String[]{"bind", "a", "a", "a", "a", "a", "a", "a"}[idx]; // ", boolean paramBoolean1, boolean paramBoolean2)"
            fiAdd = new String[]{"add", "j", "j", "j", "j", "j", "j", "j"}[idx]; // FolderInfo - .add
            fiAddItem = new String[]{"addItem", "i", "i", "i", "i", "i", "i", "i"}[idx]; // FolderIcon - below "getVisibility() == 0;" with interface parameter ShortcutInfo
            fiRemove = new String[]{"remove", "k", "k", "k", "k", "k", "k", "k"}[idx]; // FolderInfo - .remove
            fiFromXml = new String[]{"fromXml", "a", "a", "a", "a", "a", "a", "a"}[idx]; // FolderIcon - method with ".topMargin" in it
            sawMeasureChild = new String[]{"measureChild", "M", "M", "M", "M", "ad", "ad", "ad"}[idx]; // in Launcher above "return localFolderIcon"
            siGetIcon = new String[]{"getIcon", "a", "a", "a", "a", "b", "b", "b"}[idx]; // public final Bitmap
            aiMakeShortcut = new String[]{"makeShortcut", "cE", "dt", "dC", "dC", "dX", "dy", "dy"}[idx]; // (this);
            btvSetShadowsEnabled = new String[]{"setShadowsEnabled", "w", "z", "z", "z", "", "", ""}[idx]; // invalidate
            icGetFullResIcon = new String[]{"getFullResIcon", "a", "a", "a", "a", "a", "a", "a"}[idx]; // (Resources paramResources, int paramInt)
            icCacheLocked = new String[]{"cacheLocked", "b", "a", "a", "a", "a", "a", "a"}[idx];
            uCreateIconBitmap = new String[]{"createIconBitmap", "a", "a", "a", "a", "a", "a", "a"}[idx]; // (Drawable paramDrawable, Context paramContext)
            lmCheckItemPlacement = new String[]{"checkItemPlacement", "a", "a", "a", "a", "a", "a", "a"}[idx]; // "Error loading shortcut into "
            lmIsShortcutInfoUpdateable = new String[]{"isShortcutInfoUpdateable", "e", "e", "e", "e", "f", "", ""}[idx]; // "android.intent.action.MAIN"
            lmDeleteItemFromDatabase = new String[]{"deleteItemFromDatabase", "b", "b", "b", "b", "c", "b", "b"}[idx]; // (Context paramContext, ItemInfo paramta) - link to "deleting a folder"
            lmDeleteFolderContentsFromDatabase = new String[]{"deleteFolderContentsFromDatabase", "a", "a", "a", "a", "a", "a", "a"}[idx]; // (Context paramContext, FolderInfo paramsh)
            lmGetAppNameComparator = new String[]{"getAppNameComparator", "hw", "im", "iq", "ir", "iB", "jH", "jH"}[idx]; // public static final Comparator
            dlAddResizeFrame = new String[]{"addResizeFrame", "a", "a", "a", "a", "a", "a", "a"}[idx]; // ", CellLayout paramCellLayout)"
            gsaShouldAlwaysShowHotwordHint = new String[]{"shouldAlwaysShowHotwordHint", "uK", "xE", "yB", "yG", "HU", "Kw", "Kw"}[idx]; // always_show_hotword_hint - method below "'&', '='" or with "194"
            soiSetSearchStarted = new String[]{"setSearchStarted", "cs", "cI", "cI", "cI", "eG", "fg", "fh"}[idx]; // onResume before cancel()
            noOnShow = new String[]{"onShow", "p", "u", "v", "v", "x", "y", "y"}[idx]; // boolean paramBoolean1, boolean paramBoolean2
            woiSyncWithScroll = new String[]{"syncWithScroll", "kf", "la", "lf", "lk", "lC", "mM", "mM"}[idx]; // computeScroll in Workspace
            /////////rvCanShowHotwordAnimation = new String[]{"canShowHotwordAnimation", "NH", "Se", "UC", "UH"}[idx]; //  == 5);
            spSetProximityToNow = new String[]{"setProximityToNow", "x", "x", "x", "x", "", "", ""}[idx]; // (float paramFloat) with RecognizerView
            spOnModeChanged = new String[]{"onModeChanged", "", "", "", "", "av", "aA", "aA"}[idx]; // "if ((paramInt1 == 0) && ((paramInt2 & 0x4) != 0))"
            tmSetTransitionsEnabled = new String[]{"setTransitionsEnabled", "cG", "cY", "cZ", "cZ", "ea", "eE", "eF"}[idx]; // (4)
            weaAddCurrentConditions = new String[]{"addCurrentConditions", "a", "a", "a", "a", "", "", ""}[idx];
            weaUpdateWeather = new String[]{"", "", "", "", "", "aCZ", "aGu", "aGu"}[idx]; // only "void" method
            uIsL = new String[]{"", "", "jO", "jS", "jV", "", "", ""}[idx];
            uGetCenterDeltaInScreenSpace = new String[]{"getCenterDeltaInScreenSpace", "", "", "", "", "b", "b", "b"}[idx]; // public static int[]
            lasIsDisableAllApps = new String[]{"isDisableAllApps", "ha", "hS", "hW", "hX", "im", "jd", "jd"}[idx]; // launcher_noallapps
            wpGetWeatherDescription = new String[]{"", "", "", "", "", "tz", "vd", "vd"}[idx]; // in WeatherEntryAdapter - (TextUtils.isEmpty(str)))
            wpGetTemperatur = new String[]{"", "", "", "", "", "tx", "vb", "vb"}[idx]; // in WeatherEntryAdapter - ().length() > 3
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
                lSearchDropTargetBar,
                wLastCustomContentScrollProgress,
                pvOverscrollX,
                dpFolderIconSize,
                lmWorkspaceItems,
                lmFolders,
                acclFocusHandlerView;

        public static void initFieldNames(int idx) {
            dpHotseatAllAppsRank = new String[]{"hotseatAllAppsRank", "zp", "BQ", "Cv", "Cu", "DY", "CV", "CV"}[idx]; // only / 2 operation
            dpNumHotseatIcons = new String[]{"numHotseatIcons", "yz", "AY", "BD", "BC", "Di", "Cg", "Cg"}[idx]; // toString of DynamicGrid ", hc: "
            dpHotseatBarHeightPx = new String[]{"hotseatBarHeightPx", "zo", "BP", "Cu", "Ct", "DX", "CU", "CU"}[idx]; // 4 * ...
            dpPageIndicatorHeightPx = new String[]{"pageIndicatorHeightPx", "zw", "BX", "CC", "CB", "Ef", "Da", "Da"}[idx]; // last parameter in last localRect2.set(
            dpIconTextSize = new String[]{"allAppsIconTextSizePx", "zd", "BE", "Cj", "Ci", "DJ", "CG", "CG"}[idx]; // localPaint.setTextSize
            dpAllAppsIconSize = new String[]{"allAppsIconSizePx", "zc", "BD", "Ci", "Ch", "DN", "CK", "CK"}[idx]; // first in acpv "public final float"
            dpIconDrawablePaddingPx = new String[]{"iconDrawablePaddingPx", "zZ", "BA", "Cf", "Ce", "DK", "CH", "CH"}[idx]; // in BubbleTextView setCompoundDrawablePadding
            dpNumCols = new String[]{"numColumns", "yy", "AX", "BC", "BB", "Dh", "Cf", "Cf"}[idx]; // ", c: "
            dpFolderIconSize = new String[]{"folderIconSizePx", "zi", "BJ", "Co", "Cn", "DS", "CP", "CP"}[idx]; // " + 2 * -"
            clphHasPerformedLongPress = new String[]{"mHasPerformedLongPress", "wG", "zf", "zK", "zJ", "BV", "AS", "AS"}[idx]; // only boolean member
            cllpCanReorder = new String[]{"canReorder", "wf", "yE", "zj", "zi", "Bu", "Ar", "Ar"}[idx]; // second member with "= true"
            clIsHotseat = new String[]{"mIsHotseat", "vq", "xP", "yu", "yt", "AF", "zC", "zC"}[idx];
            clShortcutsAndWidgets = new String[]{"mShortcutsAndWidgets", "vp", "xO", "yt", "ys", "AE", "zB", "zB"}[idx];
            sawIsHotseat = new String[]{"mIsHotseatLayout", "Ng", "PQ", "Qr", "Qu", "SP", "Wx", "Wx"}[idx];
            sdtbIsSearchBarHidden = new String[]{"mIsSearchBarHidden", "MV", "PF", "Qg", "Qj", "SE", "Wn", "Wn"}[idx]; // above Qsb member
            sdtbQsbBar = new String[]{"mQSBSearchBar", "MW", "PG", "Qh", "Qk", "SF", "Wo", "Wo"}[idx];
            wCustomContentShowing = new String[]{"mCustomContentShowing", "PV", "SH", "Ti", "Ti", "VF", "Zo", "Zo"}[idx]; // "() == 0) || (!this."
            wState = new String[]{"mState", "Qj", "SV", "Tw", "Tw", "VT", "ZC", "ZC"}[idx]; // WorkspaceState member
            wDefaultPage = new String[]{"mDefaultPage", "PI", "Su", "SV", "SV", "Vs", "Zb", "Zb"}[idx];  // "Expected custom content screen to exist", member gets decreased by one // "(-1 + this."
            wTouchState = new String[]{"mTouchState", "KY", "NF", "Oj", "On", "Qt", "TV", "TV"}[idx]; // onInterceptTouchEvent while clause
            wIsSwitchingState = new String[]{"mIsSwitchingState", "Qk", "SW", "Tx", "Tx", "VU", "ZD", "ZD"}[idx]; // start from onTouch, second method call in if-clause
            wLastCustomContentScrollProgress = new String[]{"mLastCustomContentScrollProgress", "PW", "SI", "Tj", "Tj", "VG", "Zp", "Zp"}[idx]; // " = -1.0F;"
            lHotseat = new String[]{"mHotseat", "EO", "Hu", "HZ", "Id", "Ka", "IP", "IP"}[idx];
            lSearchDropTargetBar = new String[]{"mSearchDropTargetBar", "um", "wN", "xs", "xr", "zD", "yz", "yz"}[idx];
            lAppsCustomizeTabHost = new String[]{"mAppsCustomizeTabHost", "ER", "Hx", "Ic", "Ig", "Kd", "IS", "IS"}[idx];
            lIconCache = new String[]{"mIconCache", "rF", "uf", "uK", "uK", "xn", "wf", "wf"}[idx]; // IconCache member in Launcher
            lState = new String[]{"mState", "Et", "GZ", "HE", "HH", "JF", "Is", "Is"}[idx]; // onNewIntent - "if ((i != 0) && (this."
            lHasFocus = new String[]{"mHasFocus", "Fj", "HP", "It", "Ix", "Ku", "NP", "NP"}[idx]; // onWindowFocusChanged
            lPaused = new String[]{"mPaused", "EZ", "HF", "Ik", "Io", "Kl", "NG", "NG"}[idx]; // only boolean assignement in onPause()
            lAppsCustomizePagedView = new String[]{"mAppsCustomizeContent", "ES", "Hy", "Id", "Ih", "Ke", "LE", "LE"}[idx]; // AppsCustomizePagedView in Launcher
            btvShadowsEnabled = new String[]{"mShadowsEnabled", "ue", "wF", "xk", "xj", "zv", "yr", "yr"}[idx]; // only boolean member = true
            fiPreviewBackground = new String[]{"mPreviewBackground", "CE", "Fh", "FM", "FP", "HL", "GL", "GL"}[idx]; // FOLDERICON - only ImageView member
            fiFolderName = new String[]{"mFolderName", "CF", "Fi", "FN", "FQ", "HM", "GM", "GM"}[idx]; // FOLDERICON - only BubbleTextView
            fiFolder = new String[]{"mFolder", "CB", "Fe", "FJ", "FM", "HI", "GI", "GI"}[idx]; // FOLDERICON - only Folder member
            fiLongPressHelper = new String[]{"mLongPressHelper", "ui", "wJ", "xo", "xn", "zA", "yw", "yw"}[idx]; // cancelLongPress
            fFolderInfo = new String[]{"mInfo", "BF", "Ej", "EO", "ER", "GM", "FM", "FM"}[idx]; // <mInfo>.title))
            fiFolderInfo = fFolderInfo; // FolderIcon - same as fFolderInfo
            fiContents = new String[]{"contents", "Dt", "FW", "GB", "GE", "IA", "HA", "HA"}[idx]; // first ArrayList in FolderInfo
            fiOpened = new String[]{"opened", "Ds", "FV", "GA", "GD", "Iz", "Hz", "Hz"}[idx]; // only boolean member
            //fFolderIcon = new String[]{"mFolderIcon", "BL", "Ep", "EU"}[idx]; // only FolderIcon member
            fFolderEditText = new String[]{"mFolderName", "Cf", "EJ", "Fo", "Fr", "Ho", "Go", "Go"}[idx]; // only FolderEditText member
            fMaxCountX = new String[]{"mMaxCountX", "BM", "Eq", "EV", "EY", "GV", "FV", "FV"}[idx]; // Folder constructor, last line - maxNumItems = X * Y;
            fMaxCountY = new String[]{"mMaxCountY", "BN", "Er", "EW", "EZ", "GW", "FW", "FW"}[idx]; // Folder constructor, last line - maxNumItems = X * Y;
            fMaxNumItems = new String[]{"mMaxNumItems", "BO", "Es", "EX", "Fa", "GX", "FX", "FX"}[idx]; // Folder constructor, last line - maxNumItems = X * Y;
            fContent = new String[]{"mContent", "BH", "El", "EQ", "ET", "GQ", "FQ", "FQ"}[idx]; // only CellLayout member
            pvIsPageMoving = new String[]{"mIsPageMoving", "Lv", "Oc", "OG", "OK", "QN", "Up", "Up"}[idx];  // "while (!this."
            pvNextPage = new String[]{"mNextPage", "KI", "Np", "NT", "NX", "Qd", "TF", "TF"}[idx]; // abortAnimation();
            pvPageIndicator = new String[]{"mPageIndicator", "Lz", "Og", "OK", "OO", "QR", "Uu", "Uu"}[idx]; // setContentDescription
            pvCurrentPage = new String[]{"mCurrentPage", "KF", "Nm", "NQ", "NU", "Qa", "TC", "TC"}[idx]; // "if ((localView != null) && (i != this."
            pvOverscrollX = new String[]{"mOverScrollX", "Ln", "Nu", "Oy", "OC", "QF", "Uh", "Uh"}[idx]; // " >= 0);"
            aiComponentName = new String[]{"componentName", "rJ", "uj", "uO", "uO", "xr", "wj", "wj"}[idx]; // only ComponentName member
            iiItemType = new String[]{"itemType", "En", "GT", "Hy", "HB", "Jz", "Im", "Im"}[idx]; // Item(id=
            iiID = new String[]{"id", "id", "id", "id", "id", "id", "id", "id"}[idx];
            iiScreenId = new String[]{"screenId", "vO", "yn", "yS", "yR", "Bd", "Aa", "Aa"}[idx];
            iiContainer = new String[]{"container", "Eo", "GU", "Hz", "HC", "JA", "In", "In"}[idx];
            //iiTitle = new String[]{"title", "title", "title", "title"}[idx];
            ceIcon = new String[]{"icon", "DZ", "GE", "Hj", "Hm", "Jj", "HW", "HW"}[idx];
            lawiProviderName = new String[]{"providerName", "GX", "JF", "Kj", "Kn", "Mp", "PL", "PL"}[idx]; // only ComponentName member
            acthInTransition = new String[]{"mInTransition", "tf", "vF", "wk", "wk", "yA", "xs", "xs"}[idx]; // only boolean member
            acthContent = new String[]{"mContent", "tD", "wd", "wI", "wI", "yV", "xN", "xN"}[idx]; // .getLayoutParams in setInsets
            //acthTabsContainer = new String[]{"mTabsContainer", "tA", "wA", "wF"}[idx]; // setAlpha
            acthAppsCustomizePane = new String[]{"mAppsCustomizePane", "tB", "wb", "wG", "wG", "", "", ""}[idx]; // setAlpha
            uIconWidth = new String[]{"sIconWidth", "NC", "Qm", "QN", "QS", "Tn", "WX", "WX"}[idx]; // first private static int
            uIconHeight = new String[]{"sIconHeight", "ND", "Qn", "QO", "QT", "To", "WY", "WY"}[idx]; // second private static int
            acpvAllAppsNumCols = new String[]{"allAppsNumCols", "zr", "BS", "Cx", "Cw", "DZ", "CW", "CW"}[idx]; // onMeasure localDeviceProfile
            acpvAllAppsNumRows = new String[]{"allAppsNumRows", "zq", "BR", "Cw", "Cv", "Ea", "CX", "CX"}[idx]; // onMeasure localDeviceProfile
            acpvAllApps = new String[]{"mApps", "sA", "va", "vF", "vF", "yi", "xa", "xa"}[idx]; // sort
            //acpvAllWidgets = new String[]{"mWidgets", "sB", "vb", "vG"}[idx]; // 2nd "isEmpty"
            acpvNumAppsPages = new String[]{"mNumAppsPages", "sN", "vn", "vS", "vS", "yp", "xh", "xh"}[idx]; // Math.ceil
            acpvCellCountX = new String[]{"mCellCountX", "Lg", "NN", "Or", "Ov", "Qx", "TZ", "TZ"}[idx]; // "(int)Math.ceil(this." - first
            acpvCellCountY = new String[]{"mCellCountY", "Lh", "NO", "Os", "Ow", "Qy", "Ua", "Ua"}[idx]; // "(int)Math.ceil(this." - second
            acpvContentType = new String[]{"mContentType", "sw", "uW", "vB", "vB", "yf", "wX", "wX"}[idx]; // private oo uW = oo.vW;
            acpvContentHeight = new String[]{"mContentHeight", "sH", "vh", "vM", "vM", "yl", "xd", "xd"}[idx]; // second View.MeasureSpec.makeMeasureSpec(this.
            lmWorkspaceItems = new String[]{"sBgWorkspaceItems", "HG", "Ko", "KS", "KW", "MX", "QA", "QA"}[idx]; // "adding item: " over case 4
            lmFolders = new String[]{"sBgFolders", "HF", "Kn", "KR", "KV", "MW", "Qz", "Qz"}[idx]; // ", not in the list of folders"
            acclFocusHandlerView = new String[]{"mFocusHandlerView", "", "", "", "KV", "yd", "wV", "wV"}[idx]; // ", not in the list of folders"
        }
    }
}